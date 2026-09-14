# Accelerating in Large-Scale & Enterprise Codebases

In small tutorial projects, you can easily paste an entire file into an AI prompt. But in enterprise software engineering, you will work on repositories containing **500,000+ lines of code**, dozens of multi-module Maven/Gradle subprojects, and legacy monoliths built over a decade.

If you attempt to feed a 500k-line codebase into an AI tool, you will suffer from **context rot, token exhaustion, and hallucinated suggestions**. To accelerate in massive codebases, you must master **architectural reverse-engineering**, **high-signal context slicing**, and **hybrid automated migrations**.

---

## 1. The Context Slicing Paradigm for Large Repositories

```
┌─────────────────────────────────────────────────────────────────────────┐
│              CONTEXT SLICING IN LARGE ENTERPRISE CODEBASES              │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ❌ NAIVE APPROACH (Context Dumping):                                   │
│  Developer uploads 15 full classes (12,000 lines of code)               │
│  ➔ Context window overflows or degrades attention.                      │
│  ➔ AI generates hallucinated methods and forgets project constraints.   │
│                                                                         │
│  ⚡ ENTERPRISE APPROACH (The "Skeleton" Technique):                     │
│  Developer extracts ONLY high-signal anchors:                           │
│  1. Domain Interfaces & Method Signatures (no implementation bodies)    │
│  2. Core DTO Records & Entity Annotations                               │
│  3. Relevant SQL / Flyway Table DDL                                     │
│  ➔ Total tokens: ~400 lines (3% of original size).                      │
│  ➔ AI delivers 100% accurate, contract-compliant implementations.       │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Rapid Onboarding: Reverse-Engineering an Unfamiliar Monolith

When joining a new team with a massive legacy codebase, onboarding typically takes 3 to 4 weeks. AI can reduce this to **under 3 days**:

### Technique 1: Mapping Module Dependencies from `pom.xml` / `build.gradle`
```text
Act as an Enterprise Software Architect.
Below is the root pom.xml showing all sub-modules of our enterprise backend:

[PASTE ROOT POM.XML MODULES SECTION]

Task:
1. Deduce the architectural layered model (e.g. core-domain, infrastructure, api-gateway, common-dto).
2. Identify the dependency flow: which modules depend on which?
3. Generate a Mermaid flowchart diagram visualizing the module hierarchy and dependency rules.
```

### Technique 2: Extracting Domain Boundaries from Package Outlines
```text
Here is the directory file tree of our 'billing-service' module:

[PASTE DIRECTORY TREE OR FILE LIST]

Task:
1. Identify the core domain aggregate roots and bounded contexts.
2. List which entities appear to be central to billing operations.
3. Highlight any architectural violations (e.g., controllers directly referencing repository interfaces without a service layer).
```

---

## 3. Tracing Execution Paths Across Multi-Module Repositories

In distributed systems, understanding how a button click in the UI reaches the database across microservices is difficult. Use AI as a **call-graph tracer**:

```text
Act as a Distributed Systems Engineer.
Trace the complete request execution flow for 'POST /api/v1/payments/capture' across these 4 code snippets:

1. API Gateway Route Configuration:
[PASTE GATEWAY ROUTE]

2. PaymentController in payment-service:
[PASTE CONTROLLER]

3. FeignClient call to account-service:
[PASTE FEIGN CLIENT]

4. AccountRepository & DB Schema:
[PASTE REPO & SCHEMA]

Task:
1. Produce a numbered step-by-step trace of how the HTTP request travels from Gateway to DB.
2. Generate a Mermaid sequence diagram showing the request/response interactions and where network boundaries are crossed.
3. Identify where transaction boundaries begin and end across these services.
```

---

## 4. Automated Enterprise Migrations: OpenRewrite + AI

Migrating 500 files from Java 8/11 to 21 or from Spring Boot 2 to 3 manually takes months of grueling mechanical edits. Elite engineering teams combine **OpenRewrite** (for bulk automated AST refactoring) with **AI** (for the complex remaining 10%):

```
┌─────────────────────────────────────────────────────────────┐
│             THE ENTERPRISE MIGRATION PLAYBOOK               │
├─────────────────────────────────────────────────────────────┤
│  STEP 1: OpenRewrite (Automated AST Transformation)         │
│  • Runs deterministic recipes across 1,000 files.           │
│  • Renames javax.* ➔ jakarta.* in bulk.                     │
│  • Updates pom.xml dependencies and parent versions.        │
│                                                             │
│  STEP 2: Compiler Error Triage with AI (The Final 10%)      │
│  • Feeds remaining broken classes to AI with JEP/RFC context│
│  • Fixes Hibernate 5 ➔ 6 type mapping changes.              │
│  • Migrates SecurityFilterChain lambda DSL syntax.          │
└─────────────────────────────────────────────────────────────┘
```

### Prompt Pattern: Spring Boot 2 ➔ 3 Migration Fixer
```text
We are migrating our enterprise backend from Spring Boot 2.7 to Spring Boot 3.2 (Java 21).
OpenRewrite updated our imports to 'jakarta.*', but this legacy configuration class fails to compile:

[PASTE BROKEN SPRING SECURITY OR JPA CONFIG CLASS]

Fix this class to comply with Spring Boot 3.2:
1. Convert SecurityConfigurerAdapter to SecurityFilterChain @Bean with authorizeHttpRequests() lambda DSL.
2. Replace deprecated antMatchers() with requestMatchers().
3. Replace deprecated authenticationManagerBean() with AuthenticationManager provider configuration.
```

---

## 5. Safe Cross-Module Refactoring & Blast-Radius Analysis

Before modifying a shared DTO or database column in a shared library used by 12 downstream services, you must analyze its **blast radius**:

### The "Blast Radius" Evaluation Prompt:
```text
Act as a Principal Platform Architect.
We need to modify the shared 'UserAccountDTO' record in 'common-domain-lib':

Current DTO:
public record UserAccountDTO(Long id, String email, String status, BigDecimal balance) {}

Proposed Change:
Deprecate 'balance' field and split it into 'availableBalance' and 'pendingBalance'.

Below are 3 downstream consumer services that deserialize this DTO:
[PASTE CONSUMER SNIPPETS]

Task:
1. Blast Radius Analysis: Which downstream services will experience breaking runtime JSON deserialization errors?
2. Backward Compatibility Strategy: Provide a non-breaking version of UserAccountDTO using @JsonAlias or custom getters that allows gradual migration over two release cycles.
3. Phased Rollout Plan: Outline the 3-step deployment sequence across the microservices to ensure zero-downtime deployment.
```

---

## 6. Self-Check & Quick Review

1. **Q**: Why does dumping an entire multi-thousand-line class into an AI prompt reduce code quality?
   - *A*: Large inputs dilute the model's self-attention mechanism, causing it to lose track of subtle constraints, invent missing methods, or introduce regression bugs in unchanged areas.
2. **Q**: What is the "Skeleton Technique" for context slicing?
   - *A*: Providing only interfaces, method signatures, DTO records, and database DDL without implementation bodies. It provides 100% of the architectural contract while consuming less than 5% of the token window.
3. **Q**: Why is combining OpenRewrite with AI superior to using AI alone for enterprise framework migrations?
   - *A*: OpenRewrite deterministically modifies thousands of files in seconds without hallucination risk. AI is then deployed surgically to resolve the complex architectural changes (e.g. Spring Security 6 lambda DSL) that rule engines cannot resolve automatically.

---

## 🧭 Continue Learning

| ◀️ Previous Topic | 🧭 Track Hub | Next Topic ▶️ |
| :--- | :---: | ---: |
| [**Page 5: Modern AI Developer Tools & Rules**](05-mastering-ai-developer-tools-and-rules.md)<br><sub>*Cursor, Claude Cowork, Copilot & Project Rules*</sub> | [**AI for Developers Index**](README.md)<br><sub>*Master Visual Roadmap*</sub> | [**Page 7: System Design & DSA Velocity**](07-speeding-up-system-design-and-dsa-prep.md)<br><sub>*Architecture Blueprints, Capacity Math & Socratic DSA*</sub> |
