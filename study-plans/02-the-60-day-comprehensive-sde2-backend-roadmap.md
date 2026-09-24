# 02. The 60-Day Comprehensive SDE-2 Backend Roadmap

[← Back to 30-Day FAANG Sprint](./01-the-30-day-faang-sprint-roadmap.md) | [Track Hub](./README.md) | [Next: 90-Day Senior & Staff Architect Blueprint →](./03-the-90-day-senior-and-staff-architect-blueprint.md)

---

## 🎯 Target Persona & Overview

- **Audience:** Engineers with 1 to 4 years of experience preparing for **SDE-2 / L4 (Google, Uber, Amazon, Meta, Stripe, Swiggy, Zomato)**.
- **Time Commitment:** 15 to 20 hours per week (2 hours on weekdays, 4 hours on Saturday/Sunday).
- **Core Philosophy:** **Balanced Full-Stack Systems Competency**. SDE-2 candidates are evaluated on three distinct pillars:
  1. **Coding Rigor:** Flawless algorithmic implementation and bug-free complexity analysis.
  2. **Object-Oriented Design / Machine Coding:** Writing clean, thread-safe, extensible Java code under timed pressure.
  3. **High-Level Scalable Systems:** Understanding distributed tradeoffs, caching, sharding, and messaging.

```
╭───────────────────────────────────────────────────────────────────────────────────────────╮
│                              THE 60-DAY SDE-2 4-PHASE JOURNEY                             │
├──────────────┬──────────────────┬─────────────────────────────────────────────────────────┤
│ Phase        │ Timeline         │ Core Objective & Deliverables                           │
├──────────────┼──────────────────┼─────────────────────────────────────────────────────────┤
│ Phase 1      │ Days 1 to 15     │ Algorithmic Foundations: Patterns, Trees & Heaps        │
│ Phase 2      │ Days 16 to 30    │ Advanced DSA (Graphs & DP) + Enterprise LLD Machine Code│
│ Phase 3      │ Days 31 to 45    │ Distributed High-Level Design Foundations & Case Studies│
│ Phase 4      │ Days 46 to 60    │ Heavyweight HLDs, Behavioral Bar-Raiser & Mock Loops    │
╰──────────────┴──────────────────┴─────────────────────────────────────────────────────────╯
```

---

## 📅 Phase 1 (Days 1 – 15): Algorithmic Pattern Mastery

### Days 1 – 3: Linear Sequences & Array Manipulation
- **Deep Dives:** [Two Pointers & Sliding Window](../dsa-java/arrays/03-two-pointers-and-sliding-window.md), [Fast & Slow Pointers and Cycle Detection](../dsa-java/linked-lists/02-fast-and-slow-pointers-and-cycle-detection.md).
- **Key Problems:**
  - *Container With Most Water* (LeetCode 11)
  - *Longest Repeating Character Replacement* (LeetCode 424)
  - *Find the Duplicate Number* (LeetCode 287)
  - *Minimum Window Substring* (LeetCode 76)

### Days 4 – 6: Binary Search & Intervals
- **Deep Dives:** [Advanced Array Techniques & Binary Search](../dsa-java/arrays/08-advanced-array-techniques-and-large-scale-systems.md), [K-Way Merge & Interval Scheduling](../dsa-java/heaps-and-greedy/04-k-way-merge-and-interval-scheduling.md).
- **Key Problems:**
  - *Search in Rotated Sorted Array* (LeetCode 33)
  - *Capacity To Ship Packages Within D Days* (LeetCode 1011)
  - *Merge Intervals* (LeetCode 56)
  - *Employee Free Time* (LeetCode 759)

### Days 7 – 10: Hierarchical Trees & Binary Search Trees
- **Deep Dives:** [Tree Fundamentals & Traversal Paradigms](../dsa-java/trees/01-tree-fundamentals-and-traversal-paradigms.md), [Binary Search Trees](../dsa-java/trees/03-binary-search-trees-and-ordered-space.md), [Binary Heap Fundamentals](../dsa-java/heaps-and-greedy/01-binary-heap-fundamentals-and-priority-queues.md).
- **Key Problems:**
  - *Validate Binary Search Tree* (LeetCode 98)
  - *Binary Tree Right Side View* (LeetCode 199)
  - *Lowest Common Ancestor of a Binary Tree* (LeetCode 236)
  - *Serialize and Deserialize Binary Tree* (LeetCode 297)

### Days 11 – 15: Heaps, Stacks & Backtracking
- **Deep Dives:** [Stack Fundamentals & Monotonic Stacks](../dsa-java/stacks-and-queues/01-stack-fundamentals-and-monotonic-stacks.md), [Top K & Selection Paradigms](../dsa-java/heaps-and-greedy/02-top-k-and-selection-paradigms.md), [Subsets & Permutations](../dsa-java/backtracking-and-recursion/02-subsets-and-permutations-generating-combinatorial-spaces.md).
- **Key Problems:**
  - *Daily Temperatures* (LeetCode 739)
  - *Largest Rectangle in Histogram* (LeetCode 84)
  - *Find Median from Data Stream* (LeetCode 295)
  - *N-Queens* (LeetCode 51)

---

## 📅 Phase 2 (Days 16 – 30): Advanced DSA & Enterprise LLD Machine Coding

### Days 16 – 20: Graph Algorithms & Disjoint Sets
- **Deep Dives:** [Graph Representations & Traversal Foundations](../dsa-java/graphs/01-graph-representations-and-traversal-foundations.md), [Shortest Paths](../dsa-java/graphs/03-shortest-paths-dijkstra-bellman-ford-and-floyd-warshall.md), [Disjoint Set Union & MST](../dsa-java/graphs/04-disjoint-set-union-and-minimum-spanning-trees.md).
- **Key Problems:**
  - *Word Ladder* (LeetCode 127)
  - *Cheapest Flights Within K Stops* (LeetCode 787)
  - *Number of Connected Components in an Undirected Graph* (LeetCode 323)
  - *Redundant Connection* (LeetCode 684)

### Days 21 – 25: Dynamic Programming (1D, 2D, Knapsack)
- **Deep Dives:** [DP Fundamentals & 1D State Formulations](../dsa-java/dynamic-programming/01-dp-fundamentals-and-1d-state-formulations.md), [Grid & Multi-Dimensional DP](../dsa-java/dynamic-programming/02-grid-and-multi-dimensional-dp.md), [The Knapsack Family & Subset Sums](../dsa-java/dynamic-programming/03-the-knapsack-family-and-subset-sums.md).
- **Key Problems:**
  - *Coin Change II* (LeetCode 518)
  - *Longest Increasing Subsequence* (LeetCode 300)
  - *Target Sum* (LeetCode 494)
  - *Burst Balloons* (LeetCode 312)

### Days 26 – 30: Enterprise LLD Machine Coding Sprints
- Study [Low-Level Design Track Hub](../system-design/lld-machine-coding/README.md) and implement:
  - Day 26: [01. Parking Lot System](../system-design/lld-machine-coding/01-design-a-parking-lot-system.md) (Strategy, Spot allocation)
  - Day 27: [02. Elevator Control System](../system-design/lld-machine-coding/02-design-an-elevator-control-system.md) (SCAN scheduling)
  - Day 28: [04. Expense Sharing System (Splitwise)](../system-design/lld-machine-coding/04-design-an-expense-sharing-system-splitwise.md) (Min Cash Flow)
  - Day 29: [05. Movie Ticket Booking System (BookMyShow)](../system-design/lld-machine-coding/05-design-a-movie-ticket-booking-system-bookmyshow.md) (Seat locks)
  - Day 30: [06. Concurrent In-Memory Cache](../system-design/lld-machine-coding/06-design-a-concurrent-in-memory-cache-with-eviction-policies.md) (LRU/LFU/FIFO + ReadWriteLock)

---

## 📅 Phase 3 (Days 31 – 45): High-Level Distributed Systems Foundations

### Days 31 – 35: Distributed Architecture Foundations
- Read and master:
  - [01. Java Backend Architecture & Threading Models](../system-design/system-design-fundamentals/01-java-backend-architecture.md)
  - [02. SOLID Principles in Java](../system-design/system-design-fundamentals/02-solid-principles-in-java.md)
  - [03. GoF Design Patterns in Java](../system-design/system-design-fundamentals/03-design-patterns-in-java.md)
  - [04. Scaling & Thread Pool Engineering](../system-design/system-design-fundamentals/04-scaling-and-thread-pools.md)
  - [05. Databases & Connection Pooling (HikariCP)](../system-design/system-design-fundamentals/05-databases-and-connection-pooling.md)

### Days 36 – 40: Distributed Middleware & Event Streaming
- Read and master:
  - [06. Caching Strategies & LRU in Java](../system-design/system-design-fundamentals/06-caching-strategies-and-lru.md)
  - [07. Asynchronous Messaging & Kafka](../system-design/system-design-fundamentals/07-asynchronous-messaging-and-kafka.md)
  - [08. Resilience: Circuit Breakers & Rate Limiting](../system-design/system-design-fundamentals/08-resilience-circuit-breaker-rate-limiting.md)
  - [09. Microservices & API Gateways](../system-design/system-design-fundamentals/09-microservices-and-api-gateways.md)
  - [10. 4-Step System Design Interview Framework & Math](../system-design/system-design-fundamentals/10-interview-framework-and-math.md)

### Days 41 – 45: Foundational HLD Case Studies
- Execute 45-minute timed architectural mocks:
  - Day 41: [Case Study 01: Distributed Rate Limiter](../system-design/hld-case-studies/01-design-a-distributed-rate-limiter.md)
  - Day 42: [Case Study 02: Global URL Shortener (TinyURL)](../system-design/hld-case-studies/02-design-a-global-url-shortener-tinyurl.md)
  - Day 43: [Case Study 03: Real-Time Chat System (WhatsApp/Slack)](../system-design/hld-case-studies/03-design-a-real-time-chat-system-whatsapp-slack.md)
  - Day 44: [Case Study 04: Distributed Message Queue (Kafka Clone)](../system-design/hld-case-studies/04-design-a-distributed-message-queue-kafka-clone.md)
  - Day 45: [Case Study 06: Dynamo-Style Key-Value Store](../system-design/hld-case-studies/06-design-a-distributed-key-value-store-dynamo-style.md)

---

## 📅 Phase 4 (Days 46 – 60): Heavyweight HLDs, Bar Raiser & Full Mocks

### Days 46 – 50: Heavyweight HLD Case Studies
- Study and present:
  - Day 46: [Case Study 05: Video Streaming Platform (YouTube/Netflix)](../system-design/hld-case-studies/05-design-a-video-streaming-platform-youtube-netflix.md)
  - Day 47: [Case Study 07: Real-Time Ride-Hailing System (Uber/Lyft)](../system-design/hld-case-studies/07-design-a-real-time-ride-hailing-system-uber-lyft.md)
  - Day 48: [Case Study 08: Cloud Storage & Sync (Google Drive/Dropbox)](../system-design/hld-case-studies/08-design-a-distributed-cloud-storage-and-sync-google-drive-dropbox.md)
  - Day 49: [Case Study 09: E-Commerce Flash Sale & Inventory System](../system-design/hld-case-studies/09-design-an-ecommerce-flash-sale-and-inventory-system.md)
  - Day 50: [LLD 07: Food Delivery System (Swiggy/Zomato)](../system-design/lld-machine-coding/07-design-a-food-delivery-system-swiggy-zomato.md)

### Days 51 – 55: Behavioral & Leadership Mastery
- Complete all modules in [Behavioral & Engineering Leadership](../behavioral-and-leadership/README.md):
  - Day 51: Polish 6 STAR stories with quantifiable metrics using [The STAR Method](../behavioral-and-leadership/01-the-star-method-and-high-impact-storytelling.md).
  - Day 52: Map stories to [Amazon Leadership Principles](../behavioral-and-leadership/02-amazon-leadership-principles-engineering-mastery.md).
  - Day 53: Rehearse technical pushback scenarios using [Conflict Resolution](../behavioral-and-leadership/03-conflict-resolution-and-technical-disagreements.md).
  - Day 54: Rehearse your Sev-1 failure story using [Blameless Post-Mortems](../behavioral-and-leadership/04-blameless-post-mortems-and-sev-1-incident-response.md).
  - Day 55: Internalize [Reverse Interviewing: 25 High-Leverage Questions](../behavioral-and-leadership/05-reverse-interviewing-25-questions-to-ask-the-interviewer.md).

### Days 56 – 59: Full Interview Loop Simulations
- Conduct 4 comprehensive full-day simulation loops:
  - **Round 1 (DSA):** 2 LeetCode Medium/Hard problems under 45 minutes.
  - **Round 2 (LLD/Machine Coding):** 90-minute clean code implementation.
  - **Round 3 (HLD):** 45-minute whiteboard design from requirements to deep dives.
  - **Round 4 (Behavioral / Hiring Manager):** 45-minute leadership & STAR interview.
- Review [200 High-Yield Interview Flashcards](../cheatsheets/04-200-high-yield-interview-flashcards.md).

### Day 60: Strategic Rest & Interview Day Readiness
- Zero strenuous coding. Review high-level cheatsheets, sleep well, and execute with absolute clarity.

---

<div align="center">

| [← Back to 30-Day FAANG Sprint](./01-the-30-day-faang-sprint-roadmap.md) | [Track Hub: Study Plans](./README.md) | [Next: 90-Day Senior & Staff Architect Blueprint →](./03-the-90-day-senior-and-staff-architect-blueprint.md) |
| :--- | :---: | ---: |

</div>
