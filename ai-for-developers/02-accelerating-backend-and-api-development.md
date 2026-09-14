# Accelerating Backend & REST API Development

In enterprise backend development, building a single REST endpoint typically requires creating **6 to 8 interconnected files**: a database migration, JPA entity, DTO request/response records, mapper, Spring Data repository, service interface + implementation, controller, and test suite.

Typing this boilerplate manually takes 45 to 60 minutes per feature. With structured AI acceleration, you can scaffold, review, and test an entire production-grade vertical slice in **under 5 minutes**.

---

## 1. The Vertical Slice Scaffolding Pipeline

Instead of building horizontally (writing all entities, then all repositories, then all controllers), high-velocity engineers build **vertical slices**:

```
┌─────────────────────────────────────────────────────────────┐
│             THE VERTICAL SLICE SCAFFOLDING PIPELINE         │
├─────────────────────────────────────────────────────────────┤
│  1. DB Migration     ──> V1__create_subscriptions_table.sql │
│  2. JPA Entity       ──> Subscription.java (@Table, @Id)    │
│  3. DTO Records      ──> CreateSubscriptionRequest, Response│
│  4. Spring Data Repo ──> SubscriptionRepository.java        │
│  5. Business Service ──> SubscriptionService.java           │
│  6. REST Controller  ──> SubscriptionController.java        │
│  7. API Contract     ──> OpenAPI 3.0 YAML / cURL spec       │
└─────────────────────────────────────────────────────────────┘
```

---

## 2. The Master "Vertical Slice" Prompt

Here is the exact production prompt template to scaffold a complete vertical slice in modern **Java 21** and **Spring Boot 3.x**:

```text
Act as a Principal Java Backend Architect.
Task: Scaffold a complete production-grade vertical slice for an "E-Commerce Discount Voucher" feature in Spring Boot 3.2 and Java 21.

Business Requirements:
- Voucher has: code (unique, alphanumeric, e.g. "SUMMER20"), discountPercentage (1-100), maxUsageCount (positive integer), currentUsageCount (starts at 0), expiresAt (Instant), and isActive (boolean).
- Endpoint 1: POST /api/v1/vouchers (Create voucher with validation)
- Endpoint 2: POST /api/v1/vouchers/{code}/redeem (Atomically increments usage, validates not expired and usage < maxUsageCount)

Generate all of the following in separate code blocks:
1. Flyway SQL Migration (V1__create_vouchers_table.sql) with proper indexing on 'code'.
2. JPA Entity (Voucher.java) using jakarta.persistence.* with optimistic locking (@Version).
3. Request and Response DTOs using Java 21 Records with Jakarta Bean Validation (@NotBlank, @Min, @Max, @Future).
4. Spring Data JPA Repository (VoucherRepository.java) with an atomic increment query.
5. Service class (VoucherService.java) with @Transactional(rollbackFor = Exception.class).
6. REST Controller (VoucherController.java) returning ResponseEntity with RFC 7807 ProblemDetail error handling.
```

---

## 3. Database Schema & Flyway Generation

Writing database migrations by hand frequently leads to subtle schema bugs: missing foreign key constraints, unindexed lookups, or mismatched column types.

### Prompt Pattern: Java Entity ➔ Production Flyway SQL
```text
Given this Java JPA Entity:

[PASTE JPA ENTITY HERE]

Generate the matching Flyway migration script (V1__init.sql) for PostgreSQL:
- Use BIGSERIAL for primary keys.
- Add NOT NULL constraints to mandatory fields.
- Add UNIQUE index on business identifiers.
- Add B-Tree indexes for foreign keys and status query columns.
- Use TIMESTAMPTZ (TIMESTAMP WITH TIME ZONE) for temporal fields.
```

### Prompt Pattern: Complex Schema Alteration (Zero-Downtime)
```text
We have a production PostgreSQL table 'orders' with 20,000,000 rows.
Task: Write a zero-downtime Flyway migration to add a new column 'tracking_number' (VARCHAR(100)) with a UNIQUE constraint.
Constraints:
- Must NOT lock the table for long periods.
- Use 'CREATE UNIQUE INDEX CONCURRENTLY'.
- Include both up migration and rollback instructions.
```

---

## 4. DTOs, Mappings & Validation in Seconds

Mapping entities to DTOs manually is monotonous and error-prone. AI can generate **MapStruct** interfaces or modern record-based mapping constructors instantly.

### Prompt Pattern: Entity ➔ DTO + MapStruct
```text
Given this JPA entity:
public class Product {
    private Long id;
    private String sku;
    private String title;
    private BigDecimal price;
    private ProductCategory category;
    private Set<Review> reviews;
    // getters and setters
}

Generate:
1. ProductResponseDTO: Immutable Java 21 Record containing only (id, sku, title, price, categoryName, averageRating).
2. ProductMapper: MapStruct interface with componentModel = "spring", mapping 'category.name' to 'categoryName' and computing 'averageRating' from reviews set.
```

---

## 5. Automated API Contracts: OpenAPI & Postman

Frontend engineers and mobile teams often wait days for backend developers to document their APIs. You can generate comprehensive **OpenAPI 3.0 (Swagger)** definitions from your controller code in 30 seconds.

### Prompt Pattern: Controller ➔ OpenAPI 3.0 Specification
```text
Given this Spring Boot Controller:

[PASTE REST CONTROLLER CODE HERE]

Generate a complete OpenAPI 3.0 specification in YAML format:
- Include request body schema with validation constraints (pattern, min, max).
- Document all HTTP responses (200 OK, 201 Created, 400 Bad Request, 404 Not Found, 409 Conflict).
- Include example JSON payloads for both requests and responses.
```

### Prompt Pattern: Controller ➔ cURL Test Commands
```text
Generate a bash script with curl commands to test all endpoints in this controller:
- Include Authorization header with a mock Bearer JWT token.
- Include one valid happy-path curl request with JSON payload.
- Include two boundary-test curl requests that intentionally trigger HTTP 400 validation errors.
```

---

## 6. Realistic Mock Data Generation

Frontend and QA teams need realistic datasets with foreign names, addresses, edge-case phone numbers, and varied timestamps. Instead of typing dummy JSON like `{"name": "test", "age": 123}`, prompt AI for rich mock fixtures:

```text
Generate a JSON array of 10 realistic 'Order' mock objects for a fintech app:
- Include diverse currencies (USD, EUR, GBP, JPY with 0 decimals).
- Include varying order statuses ('PENDING', 'PAID', 'FAILED', 'REFUNDED').
- Include realistic timestamps within the last 48 hours.
- Include edge cases: one order with $0.01 amount, one order with $25,000 amount, and one order with a 45-character customer name containing Unicode accents.
```

---

## 7. Self-Check & Quick Review

1. **Q**: What is the primary velocity advantage of scaffolding vertical slices over horizontal layers?
   - *A*: Vertical slices produce a testable, end-to-end working feature immediately. You can test the database, service logic, and HTTP endpoint in a single feedback loop rather than waiting until all layers are written.
2. **Q**: Why should you ask AI to use Java 21 Records for DTOs instead of standard classes?
   - *A*: Records provide built-in immutability, compact constructor syntax, and automatic `equals()`, `hashCode()`, and `toString()` implementations with zero boilerplate and zero Lombok dependencies.
3. **Q**: How does AI speed up frontend and mobile team collaboration?
   - *A*: By immediately converting controller signatures into OpenAPI 3.0 YAML specs and realistic mock JSON payloads, allowing frontend teams to mock APIs and develop in parallel without blocking on backend deployment.

---

## 🧭 Continue Learning

| ◀️ Previous Topic | 🧭 Track Hub | Next Topic ▶️ |
| :--- | :---: | ---: |
| [**Page 1: The Velocity Mental Model**](01-ai-developer-mental-model-and-velocity.md)<br><sub>*RTCC Prompting & Avoiding Hallucinations*</sub> | [**AI for Developers Index**](README.md)<br><sub>*Master Visual Roadmap*</sub> | [**Page 3: Rapid Debugging & Troubleshooting**](03-rapid-debugging-and-troubleshooting.md)<br><sub>*Stack Trace Triage & Root Cause Analysis*</sub> |
