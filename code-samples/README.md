# ☕ Java 21 LLD Machine Coding Playground & Concurrency Test Suite

A production-grade, compilable modern Java (Java 21) playground containing runnable implementations of core Low-Level Design (LLD) systems and comprehensive multi-threaded JUnit 5 test suites.

---

## 🏗️ Architecture & Component Catalog

```mermaid
graph TD
    subgraph "code-samples/src/main/java/com/prep/lld/"
        C[cache/] --> CE[ConcurrentCache.java]
        C --> EP[EvictionPolicy.java]
        C --> LRU[LRUEvictionPolicy.java]
        C --> LFU[LFUEvictionPolicy.java]
        C --> FIFO[FIFOEvictionPolicy.java]

        R[ratelimiter/] --> RL[RateLimiter.java]
        R --> TB[TokenBucketRateLimiter.java]
        R --> SW[SlidingWindowCounterRateLimiter.java]
        R --> RLF[RateLimiterFactory.java]

        S[scheduler/] --> DTS[DistributedTaskScheduler.java]
        S --> SJ[ScheduledJob.java]
        S --> RP[RetryPolicy.java]
        S --> TS[TaskStatus.java]
    end

    subgraph "code-samples/src/test/java/com/prep/lld/"
        TC[cache/ConcurrentCacheTest.java]
        TR[ratelimiter/RateLimiterTest.java]
        TS2[scheduler/TaskSchedulerTest.java]
    end

    TC -.-> C
    TR -.-> R
    TS2 -.-> S
```

| System Module | Package | Key Design Patterns | Concurrency & Algorithmic Primitives | Test Suite |
| :--- | :--- | :--- | :--- | :--- |
| **Concurrent Cache** | `com.prep.lld.cache` | Strategy, Observer, Factory | `ReentrantReadWriteLock`, O(1) Doubly Linked List, Frequency Buckets, Background Scheduled Purge | `ConcurrentCacheTest` |
| **Rate Limiter Library** | `com.prep.lld.ratelimiter` | Strategy, Factory | Per-client `synchronized` monitors, Nanosecond `System.nanoTime()` CAS refill, Sliding Window Log | `RateLimiterTest` |
| **Distributed Task Scheduler** | `com.prep.lld.scheduler` | Strategy, State, Observer | `java.util.concurrent.DelayQueue`, Thread Pool Workers, Exponential Backoff Jitter, Atomic Status CAS | `TaskSchedulerTest` |

---

## ⚡ How to Build & Run Tests

### Prerequisites
- **JDK 21** or later (`java -version` >= 21)
- **Apache Maven 3.9+** (`mvn -version`)

### Execution Commands

```bash
# Navigate to the code-samples directory
cd code-samples

# Run all JUnit 5 concurrency and functional tests
mvn test

# Run a specific test suite
mvn test -Dtest=ConcurrentCacheTest
mvn test -Dtest=RateLimiterTest
mvn test -Dtest=TaskSchedulerTest

# Package compiled JAR
mvn clean package
```

### IDE Setup
- **IntelliJ IDEA**: Open the `code-samples/` folder or root workspace, right-click `pom.xml` -> **Add as Maven Project**.
- **VS Code**: Install the *Extension Pack for Java*; the project will be detected automatically.

---

## 🔗 Links & Navigation

- 📐 [LLD & Machine Coding Master Hub](../system-design/lld-machine-coding/README.md)
- 🏛️ [System Design Track Hub](../system-design/README.md)
- 🏠 [Repository Root Hub](../README.md)
