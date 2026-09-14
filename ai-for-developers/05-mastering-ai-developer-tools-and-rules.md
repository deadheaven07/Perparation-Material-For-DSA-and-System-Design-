# Mastering AI Developer Tools: Cursor, Claude Cowork, Copilot & Rules

Modern software development has evolved past generic chat windows. Today, elite engineering velocity is achieved by integrating AI directly into your editor, terminal, and collaborative workflows.

To maximize productivity, you must master the **three distinct tiers of AI developer tooling** and learn how to steer them using **custom project instruction files**.

---

## 1. The Modern AI Developer Tooling Spectrum

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    THE AI TOOLING SPECTRUM & VELOCITY FIT               │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  TIER 1: INLINE AUTOCOMPLETE (GitHub Copilot)                           │
│  • Strengths: Low latency (~100ms), tab-to-complete, muscle memory.     │
│  • Best For: Boilerplate methods, loop syntax, repetitious tests.       │
│                                                                         │
│  TIER 2: AGENTIC WORKSPACE IDEs (Cursor & Antigravity)                  │
│  • Strengths: Multi-file edits, @workspace semantic search, diff view.  │
│  • Best For: Refactoring across classes, feature vertical slices.       │
│                                                                         │
│  TIER 3: COLLABORATIVE CO-WORKING (Claude Cowork, Projects & Code CLI)  │
│  • Strengths: Deep architectural reasoning, persistent repository docs, │
│               interactive design brainstorming, autonomous CLI agent.   │
│  • Best For: Large system refactoring, PR reviews, design documents.   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

| Tool | Interaction Model | Primary Superpower | Best Used When |
| :--- | :--- | :--- | :--- |
| **GitHub Copilot** | Ghost text inline autocompletion | Millisecond-level completion as you type | Writing straightforward repetitive logic, getters/setters, test data fixtures |
| **Cursor / Antigravity** | Agentic IDE with `@workspace` indexing | Slicing and editing multiple files simultaneously | Scaffolding new features, multi-file renames, fixing compile errors |
| **Claude Cowork / Projects** | Collaborative persistent pairing partner | Deep architectural reasoning, long-term memory | Architecture design documents, reviewing complex PRs, debugging intricate domain logic |
| **Claude Code CLI** | Autonomous terminal agent | Executing terminal commands, running tests, fixing bugs | Automated TDD loops, running test suites, auto-fixing git merge conflicts |

---

## 2. Collaborative Pairing with Claude Cowork & Claude Projects

**Claude Projects** allow you to create a dedicated, persistent co-working environment for your specific repository or team.

### How to Set Up a 10x Claude Project:
1. **Upload Project Knowledge (Persistent Context)**:
   - Your database schema (`schema.sql` or Flyway migration scripts).
   - Core API contracts (OpenAPI YAML or key DTO records).
   - Architectural Decision Records (ADRs) or domain boundary descriptions.
   - Project coding standard guidelines.
2. **Set Custom Project Instructions**:
   - Instruct Claude to always adopt your architecture (e.g., Spring Boot 3.2, Java 21, Records, PostgreSQL, no Lombok).
3. **Collaborative Coworking Workflows**:
   - **Architecture Brainstorming**: Before writing code, ask: *"Review this planned schema change. What downstream queries or indexes will be affected?"*
   - **Interactive PR Simulation**: Paste your git diff and co-work on edge-case discovery and doc generation.
   - **Pairing with Claude Code CLI**: Use the `claude` CLI in your terminal to autonomously run `mvn test`, read compiler errors, and apply fixes directly to files.

---

## 3. Context Window Engineering: Avoiding "Context Rot"

One of the biggest mistakes developers make with AI IDEs is typing `@workspace` on every question. 

> [!WARNING]
> **The Context Rot Penalty**:
> When you dump 100 files into an AI's context window, the model suffers from **attention dilution**. It loses track of your specific instructions and generates hallucinated, generic code.

### The High-Signal Context Slicing Rule:
Instead of including entire packages, select **only the high-signal skeleton**:
- ✅ The database table schema or JPA entity.
- ✅ The interface or method signatures to implement.
- ✅ The request/response DTO records.
- ❌ Do NOT include 500 lines of unrelated business logic from other services!

---

## 4. Crafting High-Performance Project Rules

Modern AI tools read project configuration files automatically from your repository root:
- Cursor reads **`.cursorrules`**
- Claude Code reads **`CLAUDE.md`**
- GitHub Copilot reads **`.github/copilot-instructions.md`**

By creating these files, you ensure the AI **never** generates outdated Java or anti-patterns.

### Production Template: Java 21 & Spring Boot 3 (`.cursorrules` / `CLAUDE.md`)

```markdown
# Project Engineering Standards & Coding Guidelines

## Technology Stack
- Java Version: Java 21 (LTS).
- Framework: Spring Boot 3.2+ (Jakarta EE, never use javax.*).
- Persistence: Spring Data JPA + Hibernate 6 + PostgreSQL.
- Database Migrations: Flyway (scripts in src/main/resources/db/migration/).
- Testing: JUnit 5, Mockito, AssertJ, Testcontainers.

## Architecture & Code Conventions
1. Immutability:
   - Always use Java 21 Records for DTOs, API requests, and API responses.
   - Use @ConfigurationProperties with immutable records for application settings.
   - Do NOT use Lombok (@Data, @Getter, @Setter). Use explicit Java records and clean constructors.

2. Dependency Injection:
   - Always use constructor injection. NEVER use field injection (@Autowired on fields).
   - Mark injected dependencies as 'private final'.

3. Database & Transactions:
   - Always specify @Transactional(rollbackFor = Exception.class).
   - Use @EntityGraph or JOIN FETCH to prevent N+1 query traps.
   - Enforce pagination with Pageable and @PageableDefault on all list endpoints.

4. API Standards & Error Handling:
   - Return RFC 7807 ProblemDetail for all HTTP errors.
   - Use Jakarta Validation (@NotBlank, @NotNull, @Size, @Min) on all request records.

5. Testing Guidelines:
   - Follow Given-When-Then (Arrange-Act-Assert) pattern.
   - Use AssertJ (assertThat) for fluent assertions.
   - Do NOT use in-memory H2 for JPA tests; use Testcontainers with PostgreSQL.
```

---

## 5. Terminal & DevOps Velocity with AI

Developers spend significant time searching for complex `grep`, `awk`, `docker`, and `git` commands. Use AI CLI shortcuts:

### Git & Terminal Productivity Prompts:
```text
# 1. Complex Git Rebase / Reset:
"Give me the exact git command to interactively rebase the last 4 commits, squashing the top 2 into the 2nd commit, while preserving commit messages."

# 2. Docker Compose Scaffolding:
"Generate a docker-compose.yml for local development containing:
- PostgreSQL 16 on port 5432 with health check.
- Redis 7 on port 6379 with persistent volume.
- Apache Kafka 3.6 (KRaft mode, no Zookeeper) on port 9092.
Include network isolation and named volumes."

# 3. Linux Log Parsing One-Liner:
"Give me a bash command using awk and sort to count the top 10 IP addresses causing HTTP 500 errors from /var/log/nginx/access.log."
```

---

## 6. Self-Check & Quick Review

1. **Q**: What is the key functional difference between GitHub Copilot and Cursor/Antigravity?
   - *A*: Copilot specializes in millisecond-level inline autocompletion as you type in a single file. Agentic IDEs like Cursor/Antigravity can read, modify, and coordinate edits across multiple files simultaneously using semantic repository search.
2. **Q**: How does setting up a Claude Project accelerate ongoing development?
   - *A*: Claude Projects maintain persistent memory of your repository's schemas, architecture docs, and guidelines across sessions, eliminating the need to re-upload context for every new question.
3. **Q**: Why should you maintain a `.cursorrules` or `CLAUDE.md` file in your repository?
   - *A*: It automatically enforces your team's exact technology standards (e.g. Java 21, Records, no Lombok, Jakarta imports) on every AI generation without needing to type instructions manually.

---

## 🧭 Continue Learning

| ◀️ Previous Topic | 🧭 Track Hub | Next Topic ▶️ |
| :--- | :---: | ---: |
| [**Page 4: Accelerating Testing & Code Quality**](04-accelerating-testing-and-code-quality.md)<br><sub>*JUnit 5, Testcontainers & Pre-PR Reviews*</sub> | [**AI for Developers Index**](README.md)<br><sub>*Master Visual Roadmap*</sub> | [**Page 6: Large-Scale & Enterprise Codebases**](06-ai-for-large-scale-and-enterprise-codebases.md)<br><sub>*500k+ LOC, Legacy Code & Multi-Module Tracing*</sub> |
