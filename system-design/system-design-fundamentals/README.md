# System Design Fundamentals: Java Perspective

Welcome to the comprehensive System Design preparation guide tailored specifically from a **Java & JVM ecosystem perspective**. This guide is designed for students and engineers who want to bridge the gap between Java coding and designing scalable, fault-tolerant enterprise systems.

---

## 📖 Table of Contents (Page by Page)

| Page | Title | Key Topics & Java Integration |
| :---: | :--- | :--- |
| **[Page 1](file:///Users/deadheaven07/Downloads/Prep_DSA_SystemDesign/system-design/system-design-fundamentals/01-java-backend-architecture.md)** | **Java Backend Architecture & Threading Models** | Client-Server flow, Reverse Proxies, Tomcat vs Netty, Thread-per-request vs Reactive vs Virtual Threads (Java 21) |
| **[Page 2](file:///Users/deadheaven07/Downloads/Prep_DSA_SystemDesign/system-design/system-design-fundamentals/02-solid-principles-in-java.md)** | **SOLID Principles & Clean LLD** | Single Responsibility, Open/Closed, Liskov, Interface Segregation, Dependency Inversion with Java examples |
| **[Page 3](file:///Users/deadheaven07/Downloads/Prep_DSA_SystemDesign/system-design/system-design-fundamentals/03-design-patterns-in-java.md)** | **GoF Design Patterns for System Design** | Singleton (Double-Checked Locking, Bill Pugh), Factory, Builder, Strategy, Observer, Decorator in Java |
| **[Page 4](file:///Users/deadheaven07/Downloads/Prep_DSA_SystemDesign/system-design/system-design-fundamentals/04-scaling-and-thread-pools.md)** | **Scaling & Java Thread Pool Engineering** | Vertical vs Horizontal, `ThreadPoolExecutor`, sizing formulas for CPU vs I/O bound tasks, GC pause impacts |
| **[Page 5](file:///Users/deadheaven07/Downloads/Prep_DSA_SystemDesign/system-design/system-design-fundamentals/05-databases-and-connection-pooling.md)** | **Databases & Connection Pooling (HikariCP)** | SQL vs NoSQL, HikariCP pool sizing, ACID, Isolation levels, Optimistic vs Pessimistic locking in Java/JPA |
| **[Page 6](file:///Users/deadheaven07/Downloads/Prep_DSA_SystemDesign/system-design/system-design-fundamentals/06-caching-strategies-and-lru.md)** | **Caching Strategies & LRU in Java** | Cache-Aside vs Write-Through, In-Memory LRU with `LinkedHashMap`/Caffeine, Redis integration, Thundering Herd |
| **[Page 7](file:///Users/deadheaven07/Downloads/Prep_DSA_SystemDesign/system-design/system-design-fundamentals/07-asynchronous-messaging-and-kafka.md)** | **Message Queues & Event Streaming (Kafka)** | Synchronous vs Asynchronous, RabbitMQ vs Kafka, Java Kafka Producer/Consumer groups, idempotency |
| **[Page 8](file:///Users/deadheaven07/Downloads/Prep_DSA_SystemDesign/system-design/system-design-fundamentals/08-resilience-circuit-breaker-rate-limiting.md)** | **Resilience: Circuit Breakers & Rate Limiters** | Fault tolerance, Circuit Breakers (Resilience4j), Rate Limiting (Token Bucket in Java), Exponential backoff |
| **[Page 9](file:///Users/deadheaven07/Downloads/Prep_DSA_SystemDesign/system-design/system-design-fundamentals/09-microservices-and-api-gateways.md)** | **Microservices, API Gateways & Protocols** | Service discovery, Reverse proxy vs API gateway, REST vs gRPC (HTTP/2 multiplexing), Distributed Tracing |
| **[Page 10](file:///Users/deadheaven07/Downloads/Prep_DSA_SystemDesign/system-design/system-design-fundamentals/10-interview-framework-and-math.md)** | **The 4-Step System Design Interview Blueprint** | Requirements, Back-of-the-envelope capacity math (QPS, storage, bandwidth), High-level diagram, Deep dive |

---

## 🎯 How Students Should Use This Guide
1. **Understand Both Layers**: System Design consists of **High-Level Design (HLD)** (distributed architecture, databases, caches, queues) and **Low-Level Design (LLD)** (OOP, design patterns, clean code in Java).
2. **Follow the Code Snippets**: Every theoretical concept is accompanied by clean, idiomatic Java implementations.
3. **Practice Capacity Estimations**: Master the math on Page 10 to comfortably answer scale questions in interviews.
