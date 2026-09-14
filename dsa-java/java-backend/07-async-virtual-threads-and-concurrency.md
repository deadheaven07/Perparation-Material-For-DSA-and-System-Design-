# Page 7: Asynchronous Processing & Virtual Threads (Java 21)

Welcome to Page 7 of the Java Backend Engineering series. In high-scale web applications, performing slow I/O operations (calling third-party APIs, sending emails, generating PDFs) on the main HTTP thread quickly exhausts Tomcat's worker threads. This page covers asynchronous pipelines with **`CompletableFuture`**, Spring's **`@Async`**, and the game-changing **Virtual Threads (Java 21 / Project Loom)**.

---

## 1. Why Asynchronous Processing?

In a standard synchronous backend:

```
Synchronous Request Flow (Blocks Worker Thread for 500ms):
Client ---> [ Tomcat Worker-1 Thread ] ---> [ Calls Weather API (300ms) ]
                                      |---> [ Queries DB (150ms) ]
                                      |---> [ Sends Email (50ms) ]
Total Response Time: 500ms (Thread 1 is locked and idle during network wait!)
```

If 200 users hit this endpoint concurrently, all 200 Tomcat worker threads become blocked waiting for I/O. The $201^{\text{st}}$ user is stalled or rejected with a timeout!

---

## 2. Spring `@Async` & Custom Thread Pool Configuration

Annotating a method with `@Async` causes Spring to execute it on a background thread.

> [!CAUTION]
> **The Default TaskExecutor Trap**:
> By default, if you do not define a custom `TaskExecutor`, Spring uses `SimpleAsyncTaskExecutor`. This executor **does not reuse threads**—it creates a brand-new OS thread for every single task, leading to catastrophic **`OutOfMemoryError: unable to create native thread`** under high load!

### Production-Ready Thread Pool Configuration:

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "backendTaskExecutor")
    public Executor backendTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);          // Base threads kept alive
        executor.setMaxPoolSize(50);           // Max threads under peak load
        executor.setQueueCapacity(500);        // Bounded queue
        executor.setThreadNamePrefix("async-worker-");
        
        // Backpressure policy: Caller thread executes task if queue and pool are full
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        
        executor.initialize();
        return executor;
    }
}
```

### Using `@Async` in a Service:
```java
@Service
public class NotificationService {

    @Async("backendTaskExecutor")
    public void sendEmailReceipt(String recipientEmail, String orderId) {
        // Runs on a background thread; HTTP response returns to user immediately!
        emailClient.send(recipientEmail, "Your Order " + orderId + " has been placed.");
    }
}
```

---

## 3. Non-Blocking Pipelines with `CompletableFuture`

When an API must query multiple independent microservices in parallel and combine their results, **`CompletableFuture`** provides high-throughput concurrent execution:

```
                +---> Fetch User Details (30ms) --------+
                |                                       |
Incoming ---> Parallel                               Combines in 40ms total
Request         |                                    (instead of 90ms!)
                +---> Fetch Credit Score (40ms) --------+       |
                |                                       v
                +---> Fetch Recent Transactions (20ms) -+---> Return Response DTO
```

### Parallel Aggregator Implementation:

```java
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Service
public class DashboardService {

    private final Executor backendTaskExecutor;

    public DashboardService(Executor backendTaskExecutor) {
        this.backendTaskExecutor = backendTaskExecutor;
    }

    public record DashboardResponse(String userProfile, String creditScore, String transactions) {}

    public DashboardResponse buildUserDashboard(Long userId) {
        // 1. Fire all 3 async network calls simultaneously
        CompletableFuture<String> userFuture = CompletableFuture.supplyAsync(
                () -> remoteUserClient.getUser(userId), backendTaskExecutor)
                .orTimeout(2, TimeUnit.SECONDS); // Guard against hanging calls

        CompletableFuture<String> creditFuture = CompletableFuture.supplyAsync(
                () -> remoteCreditClient.getCredit(userId), backendTaskExecutor)
                .exceptionally(ex -> "Credit score temporarily unavailable"); // Fallback

        CompletableFuture<String> txFuture = CompletableFuture.supplyAsync(
                () -> remoteTxClient.getTransactions(userId), backendTaskExecutor);

        // 2. Wait for ALL futures to complete in parallel
        CompletableFuture.allOf(userFuture, creditFuture, txFuture).join();

        // 3. Combine results without blocking
        return new DashboardResponse(
                userFuture.join(),
                creditFuture.join(),
                txFuture.join()
        );
    }
}
```

---

## 4. The Game Changer: Virtual Threads (Java 21 & Spring Boot 3.2+)

Until Java 21, every Java thread mapped directly to an operating system (OS) platform thread, costing $\approx 1\text{ MB}$ of memory and heavy context switching.

**Virtual Threads (Project Loom)** are lightweight user-mode threads managed by the JVM. They consume only **a few kilobytes** of memory.

```
Model: 100,000s of Virtual Threads (Cheap, KB memory)
                 \ | /
           [ JVM Scheduler ]
                 / | \
Small Pool of OS Carrier Threads (Equal to number of CPU Cores)
```

### How Carrier Thread Unmounting Works:
1. Virtual Thread $V_1$ runs on OS Carrier Thread $C_1$.
2. $V_1$ executes a blocking database query (`SELECT * FROM users`).
3. The JVM intercepts the blocking system call, **unmounts $V_1$** from $C_1$, and stores its state in the Heap.
4. $C_1$ immediately executes Virtual Thread $V_2$!
5. When the database query finishes, the OS notifies the JVM, which remounts $V_1$ onto any available carrier thread to continue.

### Enabling Virtual Threads in Spring Boot 3.2+:
Add a single property to `application.properties`:

```properties
spring.threads.virtual.enabled=true
```
*Spring Boot automatically switches Tomcat's request processing and `@Async` executors to use Virtual Threads!*

> [!WARNING]
> **Virtual Thread Pinning Warning**:
> If a virtual thread executes a blocking operation inside a `synchronized` block or native method, it becomes **pinned** to its carrier OS thread, preventing the carrier thread from being unmounted.
> **Fix**: Use `java.util.concurrent.locks.ReentrantLock` instead of `synchronized` blocks in code that performs blocking I/O!

---

## 5. Self-Check & Quick Review

1. **Q**: Should you create a thread pool for Virtual Threads using `ThreadPoolExecutor`?
   - *A*: **No! Never pool virtual threads.** Virtual threads are cheap and lightweight ($\approx 1\text{ KB}$). They are designed to be created on-demand for every task and discarded after completion.
2. **Q**: What does `CompletableFuture.allOf()` do?
   - *A*: It returns a new `CompletableFuture` that completes when all the given futures complete, enabling parallel aggregation of multiple asynchronous network calls.
3. **Q**: What is the problem with using `ThreadLocal` in traditional thread pools?
   - *A*: Because threads are reused across multiple requests, data stored in a `ThreadLocal` can "leak" into subsequent requests executed on the same thread, causing data corruption or memory leaks if not explicitly cleaned up via `threadLocal.remove()`.

---

👉 **Next Up: [Page 8: Caching Strategies & Redis Integration](file:///Users/deadheaven07/Downloads/Prep_DSA_SystemDesign/dsa-java/java-backend/08-caching-and-redis-integration.md)**
