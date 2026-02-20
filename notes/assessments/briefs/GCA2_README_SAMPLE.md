---
title: "GCA2 — N-tier System"
subtitle: "README"
description: "Project overview, setup, protocol, architecture, testing evidence, and contribution matrix for GCA2."
module: "COMP C8Z03 Object-Oriented Programming"
stage: "2 (Group Project)"
generated_at: "2026-02-20 09:00 Europe/Dublin"
---

# 2025-26 - OOP - L8 - GCA2 — N-tier System

## 1. Project Overview

### Domain summary (150–200 words)
> **Replace this text** with your approved domain description.  
> Include: what the system does, who it is for, and what the core “things” are (entities).  
> Mention what binary file storage represents in your domain (e.g., profile images, evidence photos, receipts, audio clips, etc.).

### Team
- **Group ID:** `2025-26-L8-OOP-GCA2-GroupXX`
- **Members:**
  - Student A — `C00XXXX`
  - Student B — `C00XXXX`
  - Student C — `C00XXXX`

### Key features
- JDBC DAO layer with full CRUD (Stage 1 foundation)
- Client–server (sockets) JSON protocol + `ServerResponse<T>` wrapper
- Multithreaded server using `ExecutorService`
- Binary file upload + retrieval stored as DB BLOB with metadata
- JUnit 5 test suite with ≥70% line coverage evidence at final stage

---

## 2. How to Run

### Prerequisites
- Java: `17+` (or the version used in labs)
- IntelliJ IDEA (recommended)
- MySQL Server (local)
- Maven/Gradle (as per your project setup)

### 2.1 Database setup
1. Create a database (example): `gca2_db`
2. Run the script:
   - `sql/mysqlSetup.sql`
3. Verify seed data:
   - Each table has at least 10 rows.

### 2.2 Configure credentials
Create a local config file (do **not** commit credentials):
- `config/db.properties` (example keys)
  - `db.url=jdbc:mysql://localhost:3306/gca2_db`
  - `db.user=...`
  - `db.password=...`

### 2.3 Run the server
- Main class: `server.ServerMain`
- Default port: `5000` (or your chosen port)
- Expected output:
  - “Server listening on …”
  - Logs for client connect/disconnect

### 2.4 Run the client(s)
- Main class: `client.ClientMain`
- Run **two clients simultaneously** for Stage 2+ demonstration.

---

## 3. Architecture Summary

### 3.1 N-tier overview
- Client (UI / console)
- Server (socket listener + request handlers + threading)
- DAO layer (interfaces + JDBC implementations)
- Database (MySQL)

### 3.2 Architecture diagram
- Path: `docs/architecture.md`
- Diagram format: Mermaid (preferred)

---

## 4. JSON Protocol Documentation

> **Keep this section up to date** as you add request types. Stage 2 requires the protocol to be documented in the README.

### 4.1 Envelope format (example)
- **Request**
  - `type`: string (e.g., `GET_ALL_PLAYERS`)
  - `payload`: JSON object (optional)
- **Response**
  - `status`: `SUCCESS` | `FAILURE`
  - `message`: string
  - `data`: object/array/null

### 4.2 Supported request types
| Request Type | Payload fields | Success response data | Failure examples |
| :- | :- | :- | :- |
| `GET_ALL_<ENTITY>` | — | List of entity DTOs | DB connection error |
| `GET_<ENTITY>_BY_ID` | `id:int` | Entity DTO or empty | invalid id |
| `INSERT_<ENTITY>` | DTO fields | Inserted DTO with generated id | validation fail |
| `UPDATE_<ENTITY>` | `id:int` + DTO fields | Updated DTO | not found |
| `DELETE_<ENTITY>` | `id:int` | boolean or message | not found |
| `FILTER_<ENTITY>` | filter params (your design) | List of matching | invalid filter |

> Note: Stage 1 filtering is via `Predicate<T>` internally; don’t implement “SQL string filters” per request.

---

## 5. Binary File Handling (Stage 3+)

### 5.1 What binary data represents in our domain
- Example: Player profile image / Evidence photo / Receipt scan / Audio clip

### 5.2 Storage approach
- DB table includes:
  - `blob_data` (BLOB)
  - `file_name` (VARCHAR)
  - `content_type` (VARCHAR)
  - `file_size` (INT)

### 5.3 Supported binary operations
| Operation | Request type | Notes |
| :- | :- | :- |
| Upload file | `UPLOAD_<ENTITY>_FILE` | Base64 encode bytes + include metadata |
| Retrieve file | `GET_<ENTITY>_FILE` | Base64 returned, client reconstructs file |
| Query metadata only | `GET_<ENTITY>_FILE_METADATA` | Must not fetch the BLOB payload |

---

## 6. Testing & Coverage

### 6.1 Running tests
- Command:
  - `mvn test` (or your equivalent)
- Location:
  - `src/test/java/...`

### 6.2 Coverage evidence (Stage 4)
- Coverage screenshot committed to:
  - `/reports/coverage.png`
- Target:
  - **≥ 70% line coverage** across DAO + JSON + binary handling classes

---

## 7. Design Patterns, Generics, Lambdas

### 7.1 Patterns used (minimum 2)
- Pattern 1: `<name>` — why it fits
- Pattern 2: `<name>` — why it fits

### 7.2 Generics usage
- `ServerResponse<T>`
- Any additional generic abstractions

### 7.3 Functional interfaces / lambdas
- `Predicate<T>` filtering
- Any other meaningful lambdas

---

## 8. Screencast (Stage 4)

- URL: [YouTube link](www.youtube.com)

---

## 9. Contribution Matrix (Required)

> One row per **major task**. “Primary” means who implemented first version. “Contributor/Reviewer” means meaningful review, refactor, debugging, extension, or pair work.

### 9.1 Matrix (example for a 3-person team)

| Major task | Primary author | Contributor / reviewer | Notes |
| :- | :- | :- | :- |
| Domain proposal email (150–200 words) + entity list for approval | Student A | Student B | Drafted + refined before sending |
| Repo setup (private repo, collaborators, branch plan stage1–stage4) | Student B | Student C | Created branches + README skeleton |
| `mysqlSetup.sql` schema + seed data (10+ rows per table) | Student C | Student A | Re-runnable from scratch |
| DTO/entity modelling + validation rules (trim/blank/range checks) | Student A | Student C | Included int/double/string fields |
| DAO interfaces (XxxDao) for all entities | Student B | Student A | Service depends on interfaces only |
| JDBC DAO implementation: `getAll` + `getById` using `Optional<T>` | Student B | Student C | PreparedStatements throughout |
| JDBC DAO implementation: `insert` returning generated keys | Student C | Student B | Verified `getGeneratedKeys()` |
| JDBC DAO implementation: `update` + `deleteById` | Student B | Student A | Consistent return semantics |
| Predicate filtering API (`findByFilter(Predicate<T>)`) | Student A | Student B | Lambda-based filtering |
| JSON conversion (toJson/fromJson/listToJson) per entity | Student A | Student C | Round-trip verified |
| Architecture diagram (Mermaid) + annotated tier explanation | Student C | Student B | Updated as architecture evolved |
| Multithreaded server (`ExecutorService`, client handler per connection) | Student B | Student C | Clean shutdown + logging |
| `ServerResponse<T>` wrapper + consistent response mapping | Student B | Student A | No raw types |
| Protocol documentation in README (all request types + payloads) | Student A | Student B | Kept current per stage |
| Client features: display all + display by id | Student C | Student A | Implemented for owned entity |
| Client features: insert/update/delete over sockets | Student C | Student B | Handles failures gracefully |
| Error handling: structured failures (no stack traces to client) | Student B | Student A | Includes validation + DB errors |
| Binary schema extension (BLOB + metadata columns) | Student A | Student C | Updated `mysqlSetup.sql` |
| Binary upload (Base64 encode/decode + DB storage) | Student A | Student B | Stored bytes + metadata |
| Binary retrieval (reconstruct file on client) | Student A | Student C | Verified bytes match |
| Metadata-only query (no BLOB fetch) | Student B | Student A | Separate DAO method |
| Disconnect protocol (`DISCONNECT`) + cleanup | Student C | Student B | Releases thread cleanly |
| Stage 3 core tests (DAO read, insert+id, JSON round-trip) | Student C | Student A | 3+ tests each |
| Stage 4 extended tests (server scenario + binary scenario + full DAO) | Student B | Student C | Added 3+ more each |
| Coverage evidence screenshot `/reports/coverage.png` | Student A | Student B | IntelliJ coverage runner |
| Screencast (8–10 min): demo + design iterations | Student C | Student A | Script + recording + export |
| Harvard references + AI usage declaration | Student A | Student B | All sources cited |
| Final README polish (run steps, protocol, testing, evidence links) | Student B | Student C | Consistent formatting |
---

## 10. References (Harvard)

- [1] …
- [2] …

---

## 11. AI Tool Use Declaration

- Tools used:
  - …
- What was generated:
  - …
- What was modified by the team:
  - …
