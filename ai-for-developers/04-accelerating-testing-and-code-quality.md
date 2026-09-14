# Accelerating Testing & Code Quality

Writing tests is widely recognized as the most valuable yet most neglected part of software development. Manual test authoring is slow because developers must write tedious mock setups, construct complex domain objects, and think of dozens of edge cases.

With AI, you can generate comprehensive **JUnit 5 test suites**, simulate **Testcontainers integration environments**, brainstorm **adversarial edge cases**, and run **pre-PR code reviews** in minutes.

---

## 1. The Automated Testing Pyramid Pipeline

```
┌─────────────────────────────────────────────────────────────┐
│                 TESTING ACCELERATION WORKFLOW               │
├─────────────────────────────────────────────────────────────┤
│  [Source Class] ────────────────────────┐                   │
│                                         ▼                   │
│  1. Unit Tests         ──> JUnit 5 + Mockito (All branches) │
│  2. Edge Cases         ──> Boundary values, nulls, limits   │
│  3. Slice Tests        ──> @WebMvcTest + MockMvc assertions │
│  4. Integration Tests  ──> @DataJpaTest + Testcontainers    │
│  5. Pre-PR Review      ──> Security, N+1, Race conditions   │
└─────────────────────────────────────────────────────────────┘
```

---

## 2. Generating Comprehensive Unit Tests (JUnit 5 + Mockito)

High-velocity developers do not write repetitive mock scaffolding by hand. They provide the production class to AI and demand 100% branch coverage:

### The Master Unit Test Prompt
```text
Act as a Staff Quality Engineer specializing in JUnit 5, Mockito, and AssertJ.

Here is the production class to test:
[PASTE JAVA SERVICE CLASS]

Generate a complete, production-grade unit test suite:
1. Frameworks: Use @ExtendWith(MockitoExtension.class), @Mock, @InjectMocks.
2. Structure: Follow the 'Given-When-Then' (Arrange-Act-Assert) pattern for every test.
3. Test Coverage:
   - Happy Path: Verify success, return values, and verify() method calls.
   - Negative Paths: Verify all custom exceptions thrown under invalid conditions.
   - Edge Cases: Null inputs, empty collections, zero/negative currency amounts.
4. Clean Assertions: Use AssertJ fluent assertions (assertThat(...)).
5. Descriptive Names: Use @DisplayName with human-readable sentences.
```

---

## 3. Web Slice Testing: `@WebMvcTest` with `MockMvc`

Writing `MockMvc` tests involves tedious JSON string serialization, header setup, and JSONPath syntax. AI generates these fluently:

### Prompt Pattern: Controller ➔ Complete `MockMvc` Suite
```text
Given this Spring Boot Controller:
[PASTE CONTROLLER CLASS]

Generate a @WebMvcTest test class:
1. Use @Autowired MockMvc and ObjectMapper.
2. Mock the service layer using @MockBean.
3. Write test methods for each endpoint:
   - Success (200 OK or 201 Created): Verify HTTP status, Content-Type, and use jsonPath("$...") to verify each JSON field.
   - Validation Failure (400 Bad Request): Pass an invalid payload (missing @NotBlank fields) and assert on ProblemDetail error response.
   - Resource Not Found (404 Not Found): Mock service to throw ResourceNotFoundException and verify error response.
```

---

## 4. Adversarial Edge-Case Brainstorming

Human developers suffer from "happy-path bias"—they write tests for how they *hope* the user will use the feature. AI has no such bias and excels at finding creative ways to break code.

### Prompt Pattern: The "Break My Code" Prompt
```text
Act as an Adversarial QA Engineer and Security Penetration Tester.

Here is my business logic method:
[PASTE METHOD CODE]

Task:
List 10 sneaky, edge-case scenarios that could cause this code to:
- Throw an unexpected NullPointerException or IndexOutOfBoundsException.
- Suffer integer overflow or precision loss with BigDecimal.
- Cause a concurrent race condition or duplicate write.
- Fail on timezone differences, daylight savings, or leap years.
- Vulnerability to injection or malformed Unicode inputs.

For the top 3 most severe edge cases, provide the exact JUnit 5 test to prove the bug.
```

---

## 5. Pre-PR Automated Code Review (The "Strict Principal Engineer")

Before submitting a Pull Request for your human team to review, run your code through this automated pre-review to catch embarrassing mistakes, security vulnerabilities, and database anti-patterns:

```text
Act as a strict, zero-tolerance Principal Backend Architect conducting a pull request review.

Here is the code diff / implementation:
[PASTE CODE]

Review this code against enterprise production standards:
1. SECURITY: Check for SQL Injection, broken authentication, IDOR vulnerabilities, or sensitive data logged in plaintext.
2. DATABASE PERFORMANCE: Look for N+1 Hibernate query traps, missing database indexes on filter columns, or unbounded query limits.
3. CONCURRENCY & TRANSACTIONS: Check for missing @Transactional(rollbackFor = Exception.class), dirty reads, or race conditions.
4. CODE CLEANLINESS: Check for memory leaks, improper resource closing, or violations of Single Responsibility.

Rate each category: [PASS / WARN / FAIL] and provide the exact line numbers and fixes for any findings.
```

---

## 6. Legacy Code Modernization to Java 21

Migrating verbose legacy code to modern Java patterns saves hundreds of lines of maintenance overhead.

### Prompt Pattern: Legacy Java 8 ➔ Modern Java 21
```text
Refactor this legacy Java 8 code to modern Java 21:

[PASTE LEGACY CODE]

Requirements:
1. Replace mutable DTO classes with immutable Java Records.
2. Replace verbose if-else/instanceof chains with Pattern Matching for switch and sealed interfaces.
3. Replace imperative collection loops with modern Streams or Sequenced Collections (getFirst(), getLast()).
4. Replace String concatenations or builders with multi-line Text Blocks where appropriate.
5. Ensure zero behavior changes or API contract regressions.
```

---

## 7. Self-Check & Quick Review

1. **Q**: Why is AssertJ preferred over standard JUnit `assertEquals()` in AI prompts?
   - *A*: AssertJ provides fluent, readable assertions (`assertThat(response.id()).isEqualTo(1L)`) with superior failure messages and type-safe chainable assertions for collections, maps, and optionals.
2. **Q**: How does the "Break My Code" prompt improve test coverage?
   - *A*: It forces the AI to assume an adversarial role, discovering edge cases (overflow, concurrent timing, null handling) that developers naturally overlook due to confirmation bias.
3. **Q**: Why should you run a pre-PR code review prompt before human review?
   - *A*: It catches mechanical flaws (missing indexes, N+1 queries, unhandled rollback exceptions) automatically, allowing human reviewers to focus on high-level architecture and business domain correctness.

---

## 🧭 Continue Learning

| ◀️ Previous Topic | 🧭 Track Hub | Next Topic ▶️ |
| :--- | :---: | ---: |
| [**Page 3: Rapid Debugging & Troubleshooting**](03-rapid-debugging-and-troubleshooting.md)<br><sub>*Stack Trace Triage & Root Cause Analysis*</sub> | [**AI for Developers Index**](README.md)<br><sub>*Master Visual Roadmap*</sub> | [**Page 5: Modern AI Developer Tools & Rules**](05-mastering-ai-developer-tools-and-rules.md)<br><sub>*Cursor, Claude Cowork, Copilot & Project Rules*</sub> |
