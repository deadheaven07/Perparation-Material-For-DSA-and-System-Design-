# Rapid Debugging, Root-Cause Analysis & Troubleshooting

Debugging is the single largest sink of engineering time. Developers regularly spend 3 to 5 hours chasing subtle concurrency bugs, Hibernate session detachments, or Spring dependency injection cycles.

AI is an extraordinary diagnostic accelerator: it can analyze a **100-line stack trace**, trace the causal chain of exceptions through framework internals, and deliver the exact surgical fix in under **15 seconds**.

---

## 1. The AI-Driven Bug Triaging Pipeline

```
┌─────────────────────────────────────────────────────────────┐
│               AI-DRIVEN BUG TRIAGING PIPELINE               │
├─────────────────────────────────────────────────────────────┤
│  1. CAPTURE  ──> Copy full stack trace (including Caused by) │
│  2. CONTEXT  ──> Attach failing method & relevant entity/DTO │
│  3. PROMPT   ──> Execute the "Fix & Explain" Prompt         │
│  4. TRIAGE   ──> Review root cause: Framework vs Logic      │
│  5. VERIFY   ──> Apply surgical diff & write regression test│
└─────────────────────────────────────────────────────────────┘
```

---

## 2. The "Fix & Explain" Prompt Pattern

Never just paste an error and ask *"Why is this broken?"* That invites lengthy, generic essays. Instead, use the **Fix & Explain** pattern:

```text
Act as a Principal JVM and Spring Boot Performance Engineer.

Here is the exact runtime exception and stack trace:
[PASTE FULL STACK TRACE INCLUDING 'CAUSED BY' BLOCKS]

Here is the relevant Java code:
[PASTE FAILING CLASS / METHOD]

Please respond with strictly two sections:
1. ROOT CAUSE: In 2 to 3 sentences, identify the exact line and explain the architectural mechanism that failed (e.g., Hibernate session lifecycle, transaction boundary, proxy bypass).
2. SURGICAL FIX: Provide a minimal code diff showing the exact before/after change needed to resolve this error without introducing side effects.
```

---

## 3. Triaging the Top 5 Nightmare Spring Exceptions

### Nightmare 1: `LazyInitializationException: could not initialize proxy - no Session`
**The Scenario**: You return an entity from a `@Transactional` service, but when Jackson attempts to serialize a `@OneToMany` collection in the controller, the app crashes.

#### Prompt Template:
```text
I am encountering 'org.hibernate.LazyInitializationException: could not initialize proxy [Author#1] - no Session' in my Spring Boot controller.

Here is the Entity:
[PASTE ENTITY]

Here is the Repository and Service method:
[PASTE REPO & SERVICE]

Task: Provide the two standard production solutions:
1. Best Practice: Converting to an explicit DTO projection using a JOIN FETCH or @EntityGraph query.
2. Anti-Pattern Warning: Explain why enabling 'spring.jpa.open-in-view=true' is considered dangerous in production.
```

---

### Nightmare 2: `BeanCurrentlyInCreationException: Requested bean is currently in creation`
**The Scenario**: Service A injects Service B, and Service B injects Service A (Circular Dependency). Spring Boot 2 allowed this with lazy proxies; Spring Boot 3 refuses to start by default.

#### Prompt Template:
```text
Spring Boot 3 fails startup with a circular reference:
ActionService -> NotificationService -> ActionService

Here are the two service constructors:
[PASTE CONSTRUCTORS]

Task:
1. Identify the domain boundary smell causing this cycle.
2. Refactor these services by extracting the shared logic into an intermediate service or by publishing a Spring ApplicationEvent.
3. Show the refactored code without using @Lazy.
```

---

### Nightmare 3: `UnexpectedRollbackException: Transaction silently rolled back because it has been marked as rollback-only`
**The Scenario**: Method A (`REQUIRED`) calls Method B (`REQUIRED`). Method B catches an exception and suppresses it with `try/catch`. When Method A finishes, Spring throws an `UnexpectedRollbackException`.

#### Prompt Template:
```text
I am seeing:
'org.springframework.transaction.UnexpectedRollbackException: Transaction silently rolled back because it has been marked as rollback-only'

Here is Method A:
[PASTE METHOD A]

Here is Method B:
[PASTE METHOD B]

Task:
1. Explain how Spring's TransactionInterceptor marked the physical transaction rollback-only inside Method B.
2. Provide the corrected code using Propagation.REQUIRES_NEW or custom error handling so that Method A can commit even if Method B fails.
```

---

### Nightmare 4: `OptimisticLockingFailureException: Object of class [...] with identifier [...]: optimistic locking failed`
**The Scenario**: Two concurrent requests attempt to update the same record at the same time, conflicting on the `@Version` column.

#### Prompt Template:
```text
In high concurrency, our inventory service throws 'ObjectOptimisticLockingFailureException'.

Here is the service method:
[PASTE METHOD]

Task:
1. Add Spring Retry (@Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 100))) to automatically retry the transaction.
2. Show how to write a concurrent unit test using CompletableFuture and CountDownLatch to reproduce the race condition and verify the retry mechanism.
```

---

### Nightmare 5: Deadlocks & Thread Dumps
**The Scenario**: In production, your application freezes. CPU drops to 0%, but all incoming HTTP requests hang and timeout. You extract a Java thread dump (`jstack <pid>`).

#### Prompt Template:
```text
Act as a Java Concurrency Expert.
Analyze this raw thread dump extract:

[PASTE THREAD DUMP SNIPPET]

Task:
1. Identify if a Deadlock exists between threads (look for 'Found one Java-level deadlock').
2. Identify which threads hold which locks and which locks they are waiting to acquire.
3. Explain the resource acquisition order flaw in the Java code causing this deadlock and provide the fix (e.g. strict lock ordering).
```

---

## 4. Log Pattern & Error Triage

When reviewing production incidents, log files often contain thousands of lines of noisy output. You can use AI to extract the signal from the noise:

```text
Act as a Site Reliability Engineer (SRE).
Below is an excerpt of 200 lines of server logs during a high-traffic incident:

[PASTE SERVER LOGS]

Task:
1. Extract and list the distinct error codes and exceptions that occurred.
2. Group the errors by root cause.
3. Correlate timestamps: which error occurred first and likely triggered the cascading failures?
```

---

## 5. Self-Check & Quick Review

1. **Q**: Why is including the `Caused by:` section essential when prompting AI with stack traces?
   - *A*: The top of a Java stack trace is usually a generic framework wrapper (e.g., `UndeclaredThrowableException` or `NestedServletException`). The actual root cause and failing line number are almost always located in the deepest nested `Caused by:` block.
2. **Q**: Why does Spring throw `UnexpectedRollbackException` when a child method catches an exception?
   - *A*: With default `REQUIRED` propagation, the child method participates in the parent's transaction. When the exception occurs, Spring's transaction aspect marks the underlying transaction as `rollback-only`. Even if the caller catches the exception, Spring refuses to commit a compromised transaction.
3. **Q**: How does the "Fix & Explain" prompt prevent AI from giving low-signal answers?
   - *A*: It constrains the AI to two actionable deliverables: a concise root-cause explanation grounded in architectural mechanics, and a minimal before/after code diff.

---

## 🧭 Continue Learning

| ◀️ Previous Topic | 🧭 Track Hub | Next Topic ▶️ |
| :--- | :---: | ---: |
| [**Page 2: Accelerating Backend & APIs**](02-accelerating-backend-and-api-development.md)<br><sub>*0-to-1 REST APIs, Flyway & DTO Scaffolding*</sub> | [**AI for Developers Index**](README.md)<br><sub>*Master Visual Roadmap*</sub> | [**Page 4: Accelerating Testing & Code Quality**](04-accelerating-testing-and-code-quality.md)<br><sub>*JUnit 5, Testcontainers & Pre-PR Reviews*</sub> |
