# Event Booking Platform

Java **Spring Boot** backend for an event ticketing product: venues, concerts, accounts, and seat booking.

This project is how I work with **Spring Boot in depth** (web, security, data, transactions) and with a **microservice architecture** (auth, catalog, booking, then an API gateway).

---

## Stack

- Java 17, **Spring Boot 3**
- **REST** APIs, DTOs, exception handling
- **Spring Security** (JWT, roles)
- **Spring Data JPA** / Hibernate
- SQL (H2 for local; PostgreSQL next)
- Maven, layered Spring modules

---

## Architecture (microservices)

The domain is split the way a real ticket platform is split — not one god service:

| Service | Responsibility |
|---|---|
| **Auth** | Register, login, JWT, roles |
| **Catalog** | Venues and events |
| **Booking** | Tickets and remaining seats |
| **API gateway** | One entry for clients; routing, timeouts, retries |

Booking owns inventory. Catalog owns the show. Auth owns identity. That is the microservice boundary this codebase is built toward.

```text
                     ┌─────────────┐
                     │ API Gateway │
                     └──────┬──────┘
            ┌───────────────┼───────────────┐
            ▼               ▼               ▼
       Auth service   Catalog service  Booking service
            │               │               │
            └───────────────┴───────────────┘
                         SQL
```

---

## Run

```bash
./mvnw spring-boot:run
```

Windows: `.\mvnw.cmd spring-boot:run`

---

Personal project — Anton Dahdal.
