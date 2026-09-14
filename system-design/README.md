# System Design Preparation

Comprehensive resources, notes, and architectural case studies covering both **High-Level Design (HLD)** and **Low-Level Design (LLD)**.

---

## 🏛️ System Design Fundamentals (Java Perspective)

Master the core architectural building blocks, Java threading models, design patterns, and interview blueprint page by page:

👉 **[System Design Fundamentals Course (Pages 1 to 10)](system-design-fundamentals/README.md)**

| Page | Title | Link |
| :---: | :--- | :--- |
| **Page 1** | Java Backend Architecture & Threading Models | [01-java-backend-architecture.md](system-design-fundamentals/01-java-backend-architecture.md) |
| **Page 2** | SOLID Principles & Clean Low-Level Design (LLD) | [02-solid-principles-in-java.md](system-design-fundamentals/02-solid-principles-in-java.md) |
| **Page 3** | GoF Design Patterns in Java for System Design | [03-design-patterns-in-java.md](system-design-fundamentals/03-design-patterns-in-java.md) |
| **Page 4** | Scaling & Java Thread Pool Engineering | [04-scaling-and-thread-pools.md](system-design-fundamentals/04-scaling-and-thread-pools.md) |
| **Page 5** | Databases & Connection Pooling (HikariCP) | [05-databases-and-connection-pooling.md](system-design-fundamentals/05-databases-and-connection-pooling.md) |
| **Page 6** | Caching Strategies & Implementing LRU in Java | [06-caching-strategies-and-lru.md](system-design-fundamentals/06-caching-strategies-and-lru.md) |
| **Page 7** | Asynchronous Messaging & Event Streaming (Kafka) | [07-asynchronous-messaging-and-kafka.md](system-design-fundamentals/07-asynchronous-messaging-and-kafka.md) |
| **Page 8** | System Resilience: Circuit Breakers & Rate Limiters | [08-resilience-circuit-breaker-rate-limiting.md](system-design-fundamentals/08-resilience-circuit-breaker-rate-limiting.md) |
| **Page 9** | Microservices, API Gateways & Protocols (REST vs gRPC) | [09-microservices-and-api-gateways.md](system-design-fundamentals/09-microservices-and-api-gateways.md) |
| **Page 10** | The 4-Step System Design Interview Blueprint | [10-interview-framework-and-math.md](system-design-fundamentals/10-interview-framework-and-math.md) |

---

## 🏛️ High-Level Design (HLD) Roadmap

### 1. Architectural Building Blocks
- **Load Balancers** (Reverse Proxy, Round Robin, Consistent Hashing)
- **Caching** (Cache-Aside, Write-Through, Write-Back, Eviction policies: LRU, LFU)
- **Message Queues & Event Streaming** (Kafka, RabbitMQ, SQS)
- **Content Delivery Networks (CDNs)**
- **API Gateways & Rate Limiters** (Token Bucket, Leaky Bucket)

### 2. Classic System Design Problems
- URL Shortener (TinyURL)
- Rate Limiter
- Key-Value Store (DynamoDB-style)
- Distributed Message Queue
- Chat System (WhatsApp / Slack)
- Video Streaming Service (YouTube / Netflix)

---

## 📐 Low-Level Design (LLD) & Object-Oriented Design

- **OOP Concepts**: Encapsulation, Abstraction, Inheritance, Polymorphism
- **SOLID Principles**: Single Responsibility, Open/Closed, Liskov Substitution, Interface Segregation, Dependency Inversion
- **Design Patterns**: Creational (Singleton, Builder, Factory), Structural (Adapter, Decorator, Facade), Behavioral (Strategy, Observer, Chain of Responsibility)
- **Classic LLD Problems**:
  - Parking Lot System
  - Elevator Management System
  - Tic-Tac-Toe / Chess Game
  - BookMyShow / Movie Ticket Booking
