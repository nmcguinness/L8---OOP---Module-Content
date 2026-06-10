| **Item**                | **Details**    |
| :- | :- |
| **Programme**           | BSc (Hons) in Computing in Games Development; BSc (Hons) in Computing in Software Development |
| **Stage**               | 2 |
| **Module**              | COMP C8Z03 (Object-Oriented Programming) |
| **Assessment**          | Summer Repeat CA |
| **Weight**              | 50% of module grade |
| **Submission**          | GitHub URL via Moodle. One **private** individual repository (see Section 8 for naming convention). |
| **Oral Defence**        | A live online technical interview is held at the submission deadline. Demonstrated understanding is a major factor in grading. |
| **Late Submission**     | Institute policy on late submission will apply (see [DkIT Continuous Assessment Procedures](https://www.dkit.ie/about/policies/continuous-assessment-procedures)). |
| **Academic Integrity**  | Institute policy on academic integrity will apply (see [DkIT Academic Integrity Policy](https://www.dkit.ie/about/policies/academic-integrity-policy-and-procedures)). |
| **Generative AI Tools** | Institute policy on the use of Generative AI tools will apply (see [DkIT Generative AI Guide for Students](https://www.dkit.ie/about/policies/generative-artificial-intelligence-ai-and-your-assessments-a-guide-for-students)). |
| **CA Cover Sheet**      | A signed CA cover sheet must be submitted with this assignment (see [DkIT CA Cover Sheet](https://www.dkit.ie/about/policies/continuous-assessment-procedures)). |



## 1. Overview

Build a **networked, database-backed Java application** as an individual student. The system integrates a full DAO persistence layer, a multithreaded socket server with a JSON protocol, structured exception handling, design patterns, and a comprehensive JUnit 5 test suite. All technical decisions must be justified in your project documentation and defended at a live interview.

> :warning: You **must** email your lecturer a 100–150 word domain description and your proposed entity fields by **Monday 15th June at 9:00 AM** for written approval. Choose a domain that interests you — e.g. a game leaderboard, music catalogue, film database, fitness tracker, or sports statistics system. Do not begin significant coding before receiving written approval.

> :warning: **Your domain must be different from any domain used in your original GCA1 or GCA2 submissions.** Reuse of source code from any prior CA is not permitted except for code provided directly by the lecturer (e.g. sample code, starter templates, or class examples). Submitting work derived from any prior CA codebase — in whole or in part — will be treated as a breach of academic integrity.



## 2. Learning Outcomes Assessed

| Code | Learning Outcome |
|:--:|:--|
| **MLO1** | Solve intermediate-to-advanced problems using OOP and multi-tier application design. |
| **MLO2** | Employ OOP concepts, models, patterns, tools, and techniques — including generics, functional interfaces, and design patterns — to build modularised software solutions. |
| **MLO3** | Choose appropriate Collections and data structures and implement them correctly. |
| **MLO5** | Use code-management, testing, debugging, and defensive coding techniques across an incrementally developed system. |



## 3. Domain and Entity Requirements

Select a domain and model **one primary entity** throughout the project.

| Requirement | Details |
| :- | :- |
| **Primary key** | A field named `tableName_id` (e.g. `player_id`) mapped to an `INT AUTO_INCREMENT` primary key. |
| **Minimum fields** | At least one `int`, one `double`, and two `String` fields. Private fields; validated in setters/constructors (trim, blank check, range check). |
| **Invalid data** | Detect, log, and skip bad inputs — do not crash on expected errors. |
| **Seed data** | A `mysqlSetup.sql` file that creates and populates the table with at least 10 rows and recreates the schema from scratch. |



## 4. Required Features

### A — DAO and Persistence Layer

| # | Feature | Specification |
| :-: | :-- | :-- |
| F1 | **Entity and Database Setup** | Define an encapsulated, validated DTO class for your entity. Create `mysqlSetup.sql` that recreates the schema and populates ≥10 seed rows from scratch. |
| F2 | **Generic DAO Interface** | Define a generic `Dao<T, K>` interface. Implement it in `JdbcXxxDao`. Application code must depend on the interface only. `PreparedStatement` required throughout — no SQL string concatenation. |
| F3 | **Get All / Get by ID** | `getAll()` returns `List<T>`. `getById(K id)` returns `Optional<T>` — never `null`. |
| F4 | **Insert** | `insert(T entity)` inserts and returns the populated DTO with auto-generated ID from `getGeneratedKeys()`. |
| F5 | **Update** | `update(K id, T entity)` applies field changes and returns the updated DTO. |
| F6 | **Delete** | `deleteById(K id)` removes the record and returns `boolean` indicating success. |
| F7 | **Predicate Filter** | `findByFilter(Predicate<T> filter)` returns matching entities using a lambda or method reference — not a hardcoded SQL clause per filter. |

### B — Generics

| # | Feature | Specification |
| :-: | :-- | :-- |
| F8 | **Generic DAO Interface** | `Dao<T, K>` interface as described in F2. The JDBC implementation is the only class that may reference JDBC types directly. |
| F9 | **`ServerResponse<T>` Wrapper** | All server replies use a generic `ServerResponse<T>` carrying `status` (enum or String), `message`, and `data`. Raw types are not used anywhere in the codebase. |
| F10 | **`Optional<T>`** | Every method that may not find a result returns `Optional<T>`. Applies to `getById()` and any service-layer method where absence is a valid outcome. Returning `null` from these methods is not permitted. |

### C — Exception Handling

| # | Feature | Specification |
| :-: | :-- | :-- |
| F11 | **Custom Exception** | Define at least one custom checked or unchecked exception class relevant to your domain (e.g. `EntityNotFoundException`, `InvalidDataException`). Use it meaningfully — not merely as a wrapper with no added context. |
| F12 | **DAO Exception Handling** | When a database error occurs, catch the `SQLException` inside your DAO method — do not let it bubble up to the rest of the application. Either wrap it in your custom exception before re-throwing, or return a result that signals failure. The goal is that code outside the DAO never has to deal with raw JDBC exceptions. |
| F13 | **Server-Side Error Handling** | If something goes wrong while the server is handling a client request (e.g. a record is not found, or a database error occurs), the server must catch the exception and send back a structured `ServerResponse<T>` with an error status and a clear message. The client should never receive a Java stack trace, and the server should never crash because one client sent a bad request. |

### D — Sockets and Concurrency

| # | Feature | Specification |
| :-: | :-- | :-- |
| F14 | **Multithreaded Server** | Server accepts multiple simultaneous clients. Each connected client is handled on a separate thread using `ExecutorService`. The server does not block on a single client. |
| F15 | **JSON Protocol** | All client-server communication uses JSON. Define a `ClientRequest` carrying at minimum a `requestType` field and a `payload`. Server replies with `ServerResponse<T>` JSON. Document the full protocol in the README (see Section 6). |
| F16 | **Client Operations** | Client sends requests for: Display All, Display by ID, Add, Update, Delete. For each, the server calls the appropriate DAO method and returns a `ServerResponse<T>`. The client parses and displays the result. |
| F17 | **Disconnect** | Client sends a structured `DISCONNECT` request before closing the socket. Server logs the disconnection and releases the thread cleanly. |

### E — Design Patterns

| # | Feature | Specification |
| :-: | :-- | :-- |
| F18 | **Design Pattern 1** | Apply one of the following patterns in a way that genuinely improves your design: **Singleton** — ensure only one instance of a class exists (e.g. a shared database connection); **Factory** — use a dedicated method or class to create objects rather than calling `new` directly throughout your code (e.g. creating the correct DAO implementation); **Strategy** — define a family of interchangeable behaviours behind a common interface so they can be swapped at runtime (e.g. different filtering or formatting approaches). The pattern must appear in real working code, not as a standalone demo class. |
| F19 | **Design Pattern 2** | Apply a second, different pattern from: **Observer** — allow one object to notify others automatically when its state changes (e.g. the server notifying registered listeners when a client connects or disconnects); **Command** — wrap a request as an object so it can be passed around, queued, or logged (e.g. representing each client request type as a command object); **Template Method** — define the overall steps of an operation in an abstract class and let subclasses fill in the specific details (e.g. a base request handler that subclasses override per request type); **Adapter** — wrap an existing class with a new interface so it can work with code that expects a different API. |
| — | **Justification** | Both patterns must be named, explained, and justified in the README. At the interview you must be able to identify where each pattern appears in the code, explain why that pattern was chosen over alternatives, and describe the benefit it provides. |

### F — Unit Testing and Coverage

| # | Feature | Specification |
| :-: | :-- | :-- |
| F20 | **JUnit 5 Test Suite** | Write a JUnit 5 test suite covering: all DAO methods (getAll, getById, insert, update, delete, filter); JSON conversion round-trips; at least one server request/response scenario (using a test client or mocked input); correct behaviour of your custom exception. |
| F21 | **Test Quality** | Each test must assert correct behaviour — not merely call a method. Use descriptive names (e.g. `getPlayerById_returnsEmpty_whenIdDoesNotExist`). Tests must be independent; use `@BeforeEach` with known data. Database tests must use a dedicated test schema or rollback strategy. |
| F22 | **Coverage Threshold** | ≥70% line coverage across entity, DAO, JSON conversion, and exception classes, demonstrated using the IntelliJ IDEA built-in coverage runner. Screenshot of the Coverage panel committed to `/reports/coverage.png`. Coverage must reflect the full suite — not a filtered subset of classes. |

### G — Project Documentation

| # | Feature | Specification |
| :-: | :-- | :-- |
| F23 | **Javadoc** | All classes and non-trivial methods must have Javadoc. Class-level Javadoc must include `@author`. Method Javadoc must describe purpose, parameters, return values, and any exceptions thrown. Trivial getters/setters/`toString()` may omit Javadoc. |
| F24 | **Architecture Diagram** | One-page annotated diagram (Mermaid markdown recommended) showing Client → Server → DAO → Database, the JSON protocol layer, and where each design pattern is applied. Committed to the repo before the submission deadline. |
| F25 | **README** | Complete README covering: domain overview; how to run; architecture diagram link; protocol documentation (each request type, payload, and `ServerResponse<T>` shape); design pattern justification; exception handling approach; test coverage evidence; Harvard-style references; AI usage declaration. |
| F26 | **Screencast** | Record an 8–10 minute screencast demonstrating all major functional requirements working end-to-end. Must show: a client connecting to the live server and performing each CRUD operation; two simultaneous clients connected at the same time; each design pattern identified and briefly explained in the code; the JUnit test suite running with coverage output visible. State your name and student ID at the start. Filename format: `2025-26-L8-OOP-RepeatCA-StudentID`. Submit via Moodle alongside your GitHub URL. A missing or inaccessible screencast will result in **zero for Component H**. |



## 5. Required OOP Practices

The following requirements apply across the entire codebase and **will be probed at the interview**.

| Requirement | What you must do |
| :-- | :-- |
| **`PreparedStatement`** | All SQL must use `PreparedStatement`. String concatenation in SQL is not permitted at any point. |
| **`Optional<T>`** | Methods that may not find a result return `Optional<T>` — never `null`. |
| **Lambdas** | Use lambda expressions or method references in at least two distinct places — minimum: `Predicate<T>` in F7; a `Comparator` or `Function` in a stream or sort. |
| **DRY** | Eliminate duplication across DAO methods, JSON converters, and request handlers. Extract shared logic into helpers. |



## 6. Protocol Documentation (Required in README)

Document every supported request type using this format:

```
Request type:   GET_BY_ID
Payload fields: { "id": int }
Success:        ServerResponse<Player> { status: "OK", message: "...", data: { ...player fields... } }
Failure:        ServerResponse<Void>   { status: "ERROR", message: "Player not found for id 99", data: null }
```

Provide an entry for: `GET_ALL`, `GET_BY_ID`, `ADD`, `UPDATE`, `DELETE`, `DISCONNECT`.



## 7. Version Control

Maintain one **private GitHub repository** accessible to your lecturer.

**Naming convention:** `2025-26-L8-OOP-RepeatCA-StudentID`

**Commit message quality matters.** Commit history is inspected at the interview.

```
Poor:   'added stuff'    'fix'    'update'    'wip'

Good:   'Implement getPlayerById — returns Optional<Player>, empty if not found'
        'Add ServerResponse<T> wrapper — standardises all server replies with status and data'
        'Apply Factory pattern to DAO creation — removes direct JdbcXxxDao dependency from server'
        'Add custom EntityNotFoundException — wraps SQLException at DAO boundary'
        'Fix: ExecutorService not shut down on server exit — added shutdown() in finally block'
        'Add JUnit tests for insert — verify auto-generated ID returned correctly'
```

**Branch:** maintain a `main` branch with regular, incremental commits reflecting genuine development. A repository showing one or two large commits near the deadline will be treated as evidence of poor process.



## 8. Submission Checklist

| Item | Required |
| :-- | :-- |
| GitHub repo URL submitted via Moodle | ✓ |
| Repo private; lecturer added as collaborator | ✓ |
| Java source code compiles and runs | ✓ |
| `mysqlSetup.sql` recreates schema and seed data from scratch | ✓ |
| All features F1–F25 implemented | ✓ |
| JUnit 5 test suite passing | ✓ |
| IntelliJ coverage screenshot at `/reports/coverage.png` (≥70%) | ✓ |
| Architecture diagram committed to repo | ✓ |
| README complete (all sections in Section 4G) | ✓ |
| Screencast (8–10 min) submitted via Moodle; filename `2025-26-L8-OOP-RepeatCA-StudentID` | ✓ |
| Signed CA Cover Sheet uploaded to Moodle | ✓ |
| Live online interview attended | ✓ |



## 9. Assessment Breakdown

| Component | Marks | What we are looking for |
| :-- | :--: | :-- |
| **A. DAO and Persistence Layer** (F1–F7) | 20 | All CRUD correct and safe; `PreparedStatement` throughout; generic `Dao<T, K>` interface used; `Optional<T>` for nullable returns; robust connection management; comprehensive Javadoc with `@author`; all decisions explained at interview. |
| **B. Generics** (F8–F10) | 10 | Generic DAO interface with correct type parameters; `ServerResponse<T>` used consistently for all replies; `Optional<T>` used in place of `null` returns throughout; no raw types anywhere; usage explained at interview. |
| **C. Exception Handling** (F11–F13) | 10 | Custom exception class used meaningfully; `SQLException` caught and wrapped at DAO boundary; server returns structured error responses — never stack traces; exception design and placement explained at interview. |
| **D. Sockets and Concurrency** (F14–F17) | 20 | Functional multithreaded server using `ExecutorService`; `ServerResponse<T>` consistent; clean JSON protocol; client performs all CRUD over sockets; clean disconnect; two simultaneous clients demonstrable; able to trace a request end-to-end at interview. |
| **E. Design Patterns** (F18–F19) | 14 | Two distinct patterns applied purposefully; each named, implemented, and justified in README; correct pattern identified in code at interview; benefit over alternatives explained; patterns are not superficial or forced. |
| **F. Unit Testing and Coverage** (F20–F22) | 14 | JUnit 5 suite covering all DAO methods, JSON round-trips, server scenario, and custom exception behaviour; all tests pass; ≥70% coverage evidenced with IntelliJ screenshot; each test's purpose explained at interview. |
| **G. Project Documentation** (F23–F25) | 6 | Javadoc on all classes and non-trivial methods with `@author`; architecture diagram correct and up-to-date; README covers all required sections including protocol, pattern justification, exception approach, coverage evidence, Harvard references, and AI declaration. |
| **H. Screencast** (F26) | 6 | All major functional requirements demonstrated live and end-to-end; two simultaneous clients shown; design patterns identified in code with brief explanation; JUnit suite run with coverage output visible; screencast accessible and within time limit. |
| **Total** | **100** | |



## Appendix A — Assessment Rubric

| Criterion | Excellent | Good | Satisfactory | Limited | Unacceptable |
| :-- | :-- | :-- | :-- | :-- | :-- |
| **A. DAO & Persistence (22)** | All CRUD correct and safe; `PreparedStatement` throughout; generic interface used; `Optional<T>` consistent; try-with-resources on all JDBC resources; full Javadoc; decisions fully explained at interview. | All operations correct; safe SQL; `Optional<T>` mostly used; Javadoc present; most decisions explained. | Core CRUD works; `Optional<T>` in some places; explains main structure but struggles with detail. | Basic CRUD present but fragile; returning `null`; limited Javadoc; limited explanation at interview. | Missing or broken CRUD; no Javadoc; cannot explain. |
| **B. Generics (10)** | Generic DAO interface with correct bounded types; `ServerResponse<T>` used for every reply; `Optional<T>` replaces all nullable returns; no raw types; all usage explained at interview. | Generic types correct; `ServerResponse<T>` mostly used; minor raw-type instances; mostly explained. | Generic interface present; `ServerResponse<T>` used in some places; adequate explanation. | Generic types superficial or incorrect; raw types present; limited explanation. | No meaningful generics use; raw types throughout; no explanation. |
| **C. Exception Handling (12)** | Custom exception used purposefully with meaningful context; `SQLException` fully contained at DAO boundary; all server errors return structured responses; try-with-resources on every JDBC resource; design explained clearly at interview. | Custom exception present and used; most SQL exceptions contained; most server errors structured; try-with-resources mostly used; mostly explained. | Custom exception defined; some exception handling at DAO; some server error responses; adequate explanation. | Custom exception exists but barely used; SQL exceptions leak to caller; limited error responses; explanation weak. | No custom exception; exceptions propagated as stack traces; no try-with-resources. |
| **D. Sockets & Concurrency (20)** | Fully functional multithreaded server; `ExecutorService` used correctly; `ServerResponse<T>` consistent; clean JSON protocol; all CRUD over sockets; clean disconnect; two simultaneous clients demonstrated; request traced end-to-end at interview. | Functional with minor threading or protocol issues; all main operations working; most of request traced. | Core features work; explains structure; struggles with threading detail or edge cases. | Basic socket communication; fragile threading; inconsistent protocol; explanation weak. | Non-functional; no thread management; cannot explain. |
| **E. Design Patterns (14)** | Two distinct patterns correctly named and applied purposefully; both justified in README with rationale over alternatives; each located and explained in code at interview. | Both patterns correctly applied; both justified; minor rationale gaps; mostly explained at interview. | Both patterns present; adequate justification; explanations satisfactory but lacks depth. | One pattern correct; second superficial or incorrectly applied; justification weak. | No meaningful pattern application; patterns named but not implemented; no explanation. |
| **F. Testing & Coverage (14)** | Comprehensive tests across all DAO methods, JSON, server scenario, and exception behaviour; all pass; ≥70% coverage evidenced; edge cases covered; each test's purpose explained at interview. | Good breadth; all tests pass; ≥70% or near; most explained. | Tests pass; partial coverage of all layers; ≥60% or near; adequate explanation. | Significant layers untested; below threshold; limited explanation. | No meaningful tests; no coverage evidence; suite does not pass. |
| **G. Documentation (6)** | Javadoc on all classes and non-trivial methods with `@author`; architecture diagram accurate and up-to-date; README covers every required section with clear, professional writing; Harvard references correct; AI usage declared. | Javadoc present with minor gaps; diagram present; most README sections complete. | Javadoc partially complete; diagram present; README mostly adequate. | Javadoc sparse; diagram missing or inaccurate; README incomplete. | No Javadoc; no diagram; README bare or missing. |
| **H. Screencast (6)** | All major functional requirements demonstrated live and end-to-end; two simultaneous clients shown; design patterns identified in the code with brief explanation; JUnit suite run with coverage output visible; clear, well-paced presentation within the 8–10 minute limit. | All main features shown; two-client demo present; patterns briefly identified; minor gaps or pacing issues. | Most features shown; two-client demo present; some features unclear or missing; time limit respected. | Significant features missing or not working; two-client demo absent; patterns not identified in code. | Screencast missing, inaccessible, or shows no meaningful functionality. |

> **Oral Defence Policy:** Interview evidence may adjust marks for any component. Where the defence reveals a lack of understanding of submitted code, this will be reflected in the relevant component grades in line with academic integrity and module policy.



## Appendix B — Glossary of Key Terms

| Term | Meaning |
|:--|:--|
| **DAO Pattern** | Data Access Object — separates persistence logic from domain logic via an interface. |
| **JDBC** | Java Database Connectivity — API for executing SQL against a relational database. |
| **`PreparedStatement`** | Parameterised SQL statement; prevents injection; required for all queries. |
| **`Optional<T>`** | Container for a value that may be absent; eliminates `null` returns from methods. |
| **Generics** | Type parameters (`<T>`, `<T, K>`) that allow classes and methods to operate on typed data without casting. |
| **`ServerResponse<T>`** | Generic wrapper standardising all server replies with status, message, and typed data. |
| **`ExecutorService`** | Thread pool for managing concurrent client connections. |
| **Custom Exception** | An application-specific exception class that conveys domain-relevant error context. |
| **Design Pattern** | A named, reusable solution to a recurring design problem (Factory, Singleton, Strategy, Observer, Command, Template Method, Adapter). |
| **JSON Protocol** | Text-based message format used for all client-server communication. |
| **JUnit 5** | Java unit testing framework. |
| **IntelliJ Coverage Runner** | Built-in tool for measuring line and branch test coverage. |
| **Javadoc** | Structured comments (`/** */`) used to generate API documentation from source code. |
