# Page 4: Scaling & Java Thread Pool Engineering

Welcome to Page 4 of the System Design Fundamentals series. When scaling a distributed Java system, high throughput depends on two pillars: **system-level horizontal scaling** and **process-level thread pool engineering**.

---

## 1. System Scaling: Vertical vs. Horizontal

```
          Vertical Scaling (Scale-Up)               Horizontal Scaling (Scale-Out)
                +-------------+                   +-------+   +-------+   +-------+
                | 64-Core CPU |                   | 4-Core|   | 4-Core|   | 4-Core|
                | 256 GB RAM  |                   | 16 GB |   | 16 GB |   | 16 GB |
                +-------------+                   +-------+   +-------+   +-------+
                                                         \        |        /
                                                      [ Load Balancer (Nginx) ]
```

| Dimension | Vertical Scaling (Scale-Up) | Horizontal Scaling (Scale-Out) |
| :--- | :--- | :--- |
| **Method** | Add more CPU cores & RAM to a single machine | Add more commodity JVM server nodes |
| **Limit** | Hard hardware ceiling and exponential cost | Virtually linear, cost-effective scaling |
| **Downtime** | Requires downtime to upgrade hardware | Zero-downtime rolling deployments |
| **Fault Tolerance** | Single Point of Failure (SPOF) | High availability (if 1 node crashes, others take traffic) |

### Stateless Services in Java
To scale horizontally behind a load balancer, Java web servers **must be stateless**:
- Never store HTTP session state inside local JVM memory (`HttpSession`).
- Offload user session tokens (JWT) or store session data in an external distributed store like **Redis**.

---

## 2. Java `ThreadPoolExecutor` Deep-Dive

In Java, raw thread creation is expensive ($\approx 1\text{ MB}$ memory + OS kernel context switching). Production systems use **`ThreadPoolExecutor`**:

```java
ThreadPoolExecutor executor = new ThreadPoolExecutor(
    corePoolSize,          // Base number of threads kept alive
    maximumPoolSize,       // Max threads allowed under peak load
    60L, TimeUnit.SECONDS, // Idle timeout for threads beyond core
    new ArrayBlockingQueue<>(1000), // Bounded task queue
    new CustomThreadFactory("worker-"),
    new ThreadPoolExecutor.CallerRunsPolicy() // Rejection strategy
);
```

### The Exact Task Submission Lifecycle:

```
                  New Task Arrives
                         |
           Are active threads < corePoolSize?
                 /               \
              (Yes)              (No)
               |                   |
        Spawn New Worker    Is workQueue Full?
        Thread to Execute         /          \
                                (No)        (Yes)
                                 |            |
                           Enqueue Task  Are active threads < maxPoolSize?
                                               /                  \
                                            (Yes)                 (No)
                                             |                     |
                                      Spawn Worker        Trigger Rejection Policy
                                      Thread to Execute   (e.g., CallerRunsPolicy)
```

### Rejection Policies Under Heavy Load:
1. **`AbortPolicy`** (Default): Throws `RejectedExecutionException`.
2. **`CallerRunsPolicy`** (Backpressure standard): The thread submitting the task executes it directly! This slows down the producer, providing natural flow control.
3. **`DiscardPolicy`**: Silently drops the task (Dangerous).
4. **`DiscardOldestPolicy`**: Drops the oldest unhandled task in the queue and retries.

---

## 3. The Golden Formulas for Thread Pool Sizing

*How many threads should your Java application allocate?*

### Brian Goetz's Formula (*Java Concurrency in Practice*):
$$N_{\text{threads}} = N_{\text{CPU}} \times U_{\text{CPU}} \times \left(1 + \frac{W}{C}\right)$$
- $N_{\text{CPU}}$: Number of available CPU cores (`Runtime.getRuntime().availableProcessors()`).
- $U_{\text{CPU}}$: Target CPU utilization ($0 \le U \le 1$, usually $0.8$).
- $W$: Wait time (waiting for DB queries, network calls, disk I/O).
- $C$: Compute time (active CPU number crunching, parsing JSON).

### 1. For CPU-Bound Tasks (Crypto, Compression, Video Encoding)
Tasks spend nearly $100\%$ time on the CPU ($W/C \approx 0$).
$$N_{\text{threads}} = N_{\text{CPU}} + 1$$
Adding more threads than cores only introduces context-switching overhead.

### 2. For I/O-Bound Tasks (REST APIs, Microservices, DB Queries)
Tasks spend $90\%$ of their time waiting for the network ($W/C = 9/1 = 9$).
$$N_{\text{threads}} = N_{\text{CPU}} \times (1 + 9) = 10 \times N_{\text{CPU}}$$
On an 8-core machine: $8 \times 10 = 80\text{ threads}$.

---

## 4. Garbage Collection (GC) Impact on Tail Latency

A common cause of sudden latency spikes ($p99$ spikes) in Java systems is **Garbage Collection Stop-The-World (STW) Pauses**:

```
Application Thread: ---[Active]---[Active]-----[STW PAUSE]-----[Active]--->
GC Thread:          ---------------------------[Collecting]---------------->
```

### Choosing the Right Garbage Collector:
1. **G1GC (Garbage-First)**: Default since Java 9. Balances high throughput with predictable pause targets (e.g., `-XX:MaxGCPauseMillis=200`).
2. **ZGC (Z Garbage Collector)**: Available in Java 17/21. Performs all expensive phase tracing concurrently with application execution. Guarantees **sub-millisecond pause times** regardless of heap size (even on 16TB heaps!).

---

## 5. Self-Check & Quick Review

1. **Q**: What happens if you use an unbounded `LinkedBlockingQueue` for your Java thread pool?
   - *A*: The queue will never fill up, so the thread pool will **never expand beyond `corePoolSize`**, and memory will grow indefinitely until `OutOfMemoryError` occurs. Always use a bounded queue!
2. **Q**: Why is `CallerRunsPolicy` recommended for systems that must handle traffic spikes?
   - *A*: It forces the caller thread (e.g., the HTTP request thread) to execute the task, creating automatic **backpressure** that slows down incoming request consumption.
3. **Q**: What is the primary difference in thread sizing between CPU-bound and I/O-bound workloads?
   - *A*: CPU-bound workloads are limited by physical cores ($N_{\text{CPU}} + 1$), while I/O-bound workloads need many threads ($N_{\text{CPU}} \times \text{Wait/Compute ratio}$) to keep cores utilized while threads wait on network/disk.

---

| ⬅️ Previous | 🏠 Course Index | ➡️ Next |
| :--- | :---: | ---: |
| [Page 3: GoF Design Patterns](03-design-patterns-in-java.md) | [System Design Index](README.md) | [Page 5: Databases & Connection Pooling](05-databases-and-connection-pooling.md) |
