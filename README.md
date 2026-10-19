# Event Booking Platform

Java **Spring Boot** backend for an event ticketing product: venues, concerts, accounts, and seat booking.

This project is how I work with **Spring Boot in depth** (web, security, data, transactions) and with a **microservice architecture** (auth, events, booking, and an API gateway).

---

## Stack

- Java 17, **Spring Boot 3**
- **REST** APIs, DTOs, `ProblemDetail` errors
- **Spring Security** (JWT, roles, CORS)
- **Spring Data JPA** / Hibernate
- **Resilience4j** (circuit breaker, retry), `WebClient`
- **Spring Cloud Gateway** (MVC)
- Actuator + Micrometer
- SQL (H2 when you run a service with Maven; PostgreSQL in Docker Compose), Docker Compose, Maven, Redis for the Book gate

---

## Architecture

The domain is split the way a real ticket platform is split — not one god service:

| Service | Owns |
|---|---|
| **Auth** | Users, login, JWT, roles |
| **Event** | Venues, events, and **remaining seats** (the inventory) |
| **Booking** | Tickets (who booked what) and the outbox |
| **API gateway** | One entry for clients; routes Book to Booking |

Event is the source of truth for seats. Booking never touches the seat column: it asks Event over HTTP to reserve seats, then saves the ticket.
Today the three services still run in one Spring Boot process, but they talk through HTTP clients (`AuthClient`, `EventClient`), not through each other's repositories, so each can move to its own process.

```text
   Phone / browser
         │
         ▼
   ┌─────────────┐
   │ API Gateway │  :8081
   └──────┬──────┘
          │ POST /api/events/{id}/bookings
          ▼
   ┌─────────────┐   HTTP (WebClient)   ┌─────────────┐
   │   Booking   │ ───────────────────▶ │    Event    │  reserve seats (row lock)
   │             │ ───────────────────▶ │    Auth     │  who is the user
   └──────┬──────┘                      └─────────────┘
          │ after commit
          ▼
      Outbox row ──▶ poller ──▶ notification
```

---

## Design decisions

| What | Why |
|---|---|
| **Row lock (`SELECT … FOR UPDATE`) on reserve seats** | Two people buying the last seat: the second waits for the first, then sees 0 and gets **409**. Lock timeout 3 s so a stuck lock does not hang the pool. |
| **`@Version` on `Event`** | Title edits (PATCH) use optimistic locking: a stale edit gets **409** instead of silently overwriting. |
| **Correlation id header** | `X-Correlation-Id` is created at the edge (or reused), passed on every downstream call, and returned, so one request can be traced across services. |
| **Circuit breaker on the Event call** | If Event keeps failing, the breaker opens and Book returns **503** fast instead of piling up threads. Sold out (**409**) and not found (**404**) do not count as failures. A slow Event (3 s client timeout) returns **502**. |
| **Retry only on safe reads** | The GET to Auth may retry. Book is never retried blindly — a retry could take seats twice. |
| **Click id on Book** | The Book body must include `clickId`. Booking stores it on the ticket (`bookings.click_id`, unique). Event stores it on the hold (`seat_holds.click_id`, unique). The same id, same event, and same seat count returns the first ticket and does not take another seat. A different event or seat count for that id is **409**. Missing `clickId` is **400**. |
| **Outbox + `@TransactionalEventListener(AFTER_COMMIT)`** | The notification runs only after the ticket is committed, never inside the lock. The outbox row is written in the same transaction as the ticket; a scheduled poller retries anything still `PENDING`. |
| **Health probes** | Liveness vs readiness on Actuator, used by the Compose health check. |
| **Metrics on `book()`** | Count and timing of Book through Micrometer, readable on `/actuator/metrics`. |
| **"My tickets" without N+1** | `GET /api/bookings/me` loads bookings **and** their events in one join (`@EntityGraph` on that one finder, not `EAGER` everywhere). `bookings.user_id` is indexed because Postgres does not index foreign keys by itself. |

---

## Security

- **JWT secret comes from the environment** (`JWT_SECRET`), never from the repo. The app does not start without it.
- **Roles on URLs:** only `ORGANIZER` / `ADMIN` create or edit events and venues; only `ATTENDEE` books.
- **CORS** allows the front-end origins in `app.cors.allowed-origins` (local `http://localhost:3000` and the GitHub Pages origin). CORS protects the user's browser from other sites; the API itself is protected by the token and the role rules.
- **CSRF is off** because the API is stateless: the token travels in the `Authorization` header, not in a session cookie.

---

## Run

Set the JWT secret first (any string of at least 32 characters, not committed anywhere).

PowerShell:

```powershell
$env:JWT_SECRET = "<your-secret-at-least-32-chars>"
```

Bash:

```bash
export JWT_SECRET="<your-secret-at-least-32-chars>"
```

Booking (port **8080**):

```bash
./mvnw spring-boot:run
```

Windows: `.\mvnw.cmd spring-boot:run`

Gateway (port **8081**, Book route → Booking):

```bash
./mvnw -f gateway/pom.xml spring-boot:run
```

Windows: `.\mvnw.cmd -f gateway/pom.xml spring-boot:run`

Or the whole set with Docker Compose. Compose starts PostgreSQL (one server, a database each for Auth, Event, and Booking) and Redis. The gateway's Book gate is a token bucket in Redis: 5 tokens, one token back every 15 seconds. A Maven run without Compose still uses H2, and the gate keeps that jar in memory.

PowerShell:

```powershell
$env:JWT_SECRET = "<your-secret-at-least-32-chars>"
$env:APP_INTERNAL_TOKEN = "<any-internal-token>"
docker compose up --build
```

Phone Book: `POST http://localhost:8081/api/events/{id}/bookings` (same path as `BookingController`). The front end in `event-booking-demo` talks only to the gateway on port **8081**.

---

Personal project — Anton Dahdal.
