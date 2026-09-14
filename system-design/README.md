# System Design Preparation

Comprehensive resources, notes, and architectural case studies covering both **High-Level Design (HLD)** and **Low-Level Design (LLD)**.

---

## 🏛️ High-Level Design (HLD)

### 1. System Design Fundamentals
- **Scalability**: Horizontal vs. Vertical Scaling
- **Reliability, Availability & Fault Tolerance**: SLAs, SLOs, SLIs, Redundancy
- **Consistency Models**: Strong vs. Eventual Consistency, CAP Theorem, PACELC Theorem
- **Databases**: SQL vs. NoSQL, Replication, Sharding, Partitioning, ACID vs. BASE

### 2. Core Architectural Building Blocks
- **Load Balancers** (Reverse Proxy, Round Robin, Consistent Hashing)
- **Caching** (Cache-Aside, Write-Through, Write-Back, Eviction policies: LRU, LFU)
- **Message Queues & Event Streaming** (Kafka, RabbitMQ, SQS)
- **Content Delivery Networks (CDNs)**
- **API Gateways & Rate Limiters** (Token Bucket, Leaky Bucket)

### 3. Classic System Design Problems
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
- **Design Patterns**:
  - *Creational*: Factory, Abstract Factory, Singleton, Builder
  - *Structural*: Adapter, Decorator, Facade, Proxy
  - *Behavioral*: Strategy, Observer, Command, State
- **Classic LLD Problems**:
  - Parking Lot System
  - Elevator Management System
  - Tic-Tac-Toe / Chess Game
  - BookMyShow / Movie Ticket Booking
