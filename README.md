# Event Booking Platform

Spring Boot backend for **concerts, venues, and ticket booking**.

Built as a production-style Java service: REST APIs, JPA, JWT security, and safe booking when two people try to take the last seat.

**GitHub:** [antondahdal/SpringBoot-bookingApp](https://github.com/antondahdal/SpringBoot-bookingApp)

---

## What it does today (running code)

| Area | In this repo |
|---|---|
| API | REST: register/login, venues, events, book seats |
| Security | Spring Security, JWT, roles (organizer / attendee) |
| Data | Spring Data JPA, Hibernate, H2 (dev) — PostgreSQL on the path |
| Booking | Transactional `book()` — row lock **and** `@Version` so two requests cannot oversell seats |
| HTTP | DTOs, validation, Problem Details (`404` / `401` / `409`) |
| Tests | Slice tests for booking HTTP (`201` / `401` / `409`) |

Layered layout: **controller → service → repository → database**.

```text
Client  →  Spring Boot (monolith)  →  H2 / PostgreSQL
              Auth | Events | Venues | Bookings
```

---

## Where it is going (roadmap)

The app is a **modular monolith on purpose**, then it splits. Same product, more moving parts — typical mid-level Spring path.

```text
Now                         Next                         Later
─────────────               ─────────────                ─────────────
One Spring Boot app         3 services + HTTP            Gateway in front
JWT, JPA, booking lock      Auth | Catalog | Booking     Resilience (timeout/retry)
H2 / PostgreSQL             WebClient between them       Docker Compose
                            Shared DB or per-service     Async notifications, metrics
```

| Phase | What a recruiter should read |
|---|---|
| **1 — now** | Spring Boot 3 monolith. REST, JPA, JWT, concurrent booking. |
| **2 — next** | Split into **microservices** (auth, events/catalog, booking). Services talk over HTTP. |
| **3 — after that** | API **gateway**, resilience (timeouts, retries), Docker Compose. |
| **4 — polish** | Async (notifications), Actuator / metrics, demo-ready. |

So: I **have** Spring Boot, security, JPA, and booking under load **in this repo**. Microservices, gateway, and Docker are the **next slices of this same project**, not a different toy.

---

## Why the split (short)

- **Auth** issues tokens. **Catalog** owns events and venues. **Booking** owns tickets and the seat counter.  
- Booking keeps the **lock on the event row** (inventory). Other services do not take that lock.  
- A gateway later is one door for the client; services stay behind it.

---

## Run locally

Java 17+, Maven wrapper:

```bash
./mvnw spring-boot:run
```

Windows: `.\mvnw.cmd spring-boot:run`

H2 console (dev): `/h2-console`  
Profile: `dev` (`application-dev.properties`). JWT secret is **local only** — use an env var in any real deploy.

---

## License

Personal project — Anton Dahdal.
