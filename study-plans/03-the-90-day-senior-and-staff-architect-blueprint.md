# 03. The 90-Day Senior & Staff Architect Blueprint

[← Back to 60-Day SDE-2 Track](./02-the-60-day-comprehensive-sde2-backend-roadmap.md) | [Track Hub](./README.md) | [Study Plans Hub →](./README.md)

---

## 🎯 Target Persona & Overview

- **Audience:** Senior Engineers (L5), Staff Engineers (L6), Tech Leads, and Software Architects preparing for Tier-1 companies (Google, Meta, Amazon, Netflix, Uber, Stripe, Apple).
- **Time Commitment:** 12 to 18 hours per week (1.5 to 2 hours on weekdays, 4 hours on Saturday/Sunday).
- **Core Philosophy:** **Architectural Judgment, Systemic Trade-Offs & Organizational Leverage**. At L5/L6+, interviewers assume you can write bug-free code. The hiring bar pivots entirely to:
  1. **Distributed Systems Depth:** Deep understanding of failure modes, network partitions, consensus protocols, and storage engine internals.
  2. **Architectural Defense:** Can you articulate *why* a particular trade-off was made (e.g. Cassandra vs Spanner, Kafka vs SQS, Eventual vs Strong consistency) with empirical rigor?
  3. **Engineering Leadership & Influence:** Leading without authority, driving cross-team RFCs, managing Sev-1 crises, and coaching teams out of technical debt paralysis.

```
╭───────────────────────────────────────────────────────────────────────────────────────────╮
│                            THE 90-DAY SENIOR / STAFF CURRICULUM                           │
├──────────────┬──────────────────┬─────────────────────────────────────────────────────────┤
│ Phase        │ Timeline         │ Primary Strategic Focus Area                            │
├──────────────┼──────────────────┼─────────────────────────────────────────────────────────┤
│ Phase 1      │ Days 1 to 21     │ Advanced Algorithmic Patterns & Complexity Rigor        │
│ Phase 2      │ Days 22 to 49    │ Distributed Systems Foundations & Storage Internals     │
│ Phase 3      │ Days 50 to 70    │ Tier-1 HLD Architectural Defense & Heavyweight Systems  │
│ Phase 4      │ Days 71 to 90    │ Staff-Level Leadership, Cross-Org RFCs & Executive Mocks│
╰──────────────┴──────────────────┴─────────────────────────────────────────────────────────╯
```

---

## 📅 Phase 1 (Days 1 – 21): Advanced Algorithmic Patterns & Complexity

### Weeks 1 – 3: High-Yield LeetCode Hard Patterns
At senior levels, you will typically face 1 or 2 coding rounds. The standard is writing production-clean code with optimal big-O complexity in 30 minutes, explaining all invariants upfront.

- **Key Focus Topics:**
  - [Two Pointers & Sliding Window](../dsa-java/arrays/03-two-pointers-and-sliding-window.md): *Trapping Rain Water*, *Sliding Window Maximum*, *Minimum Window Substring*.
  - [Topological Sorting & DAG Architectures](../dsa-java/graphs/02-topological-sorting-and-dag-architectures.md): *Alien Dictionary*, *Course Schedule II*, *Bus Routes*.
  - [Shortest Paths & DSU](../dsa-java/graphs/03-shortest-paths-dijkstra-bellman-ford-and-floyd-warshall.md): *Cheapest Flights Within K Stops*, *Swim in Rising Water*.
  - [Dynamic Programming Foundations](../dsa-java/dynamic-programming/01-dp-fundamentals-and-1d-state-formulations.md): *Word Break II*, *Burst Balloons*, *Edit Distance*.
  - [Trees & Traversal Paradigms](../dsa-java/trees/01-tree-fundamentals-and-traversal-paradigms.md): *Serialize and Deserialize Binary Tree*, *Merge k Sorted Lists*, *Word Search II*.

---

## 📅 Phase 2 (Days 22 – 49): Distributed Systems Depth & Storage Engines

### Weeks 4 – 5: Storage Internals, Sharding & Replication
- **Database Internals Deep Dive:**
  - LSM-Trees (Log-Structured Merge-Trees) vs B-Trees: Write amplification, read amplification, SSTables, Bloom filters.
  - Review [Databases & Connection Pooling](../system-design/system-design-fundamentals/05-databases-and-connection-pooling.md).
  - Single-Leader vs Multi-Leader vs Leaderless (Dynamo-style) Replication.
  - Quorum Reads & Writes ($R + W > N$), sloppy quorums, and hinted handoff.
- **Distributed Consistency:**
  - Linearizability vs Sequential Consistency vs Causal Consistency vs Eventual Consistency.
  - PACELC Theorem: Beyond CAP.

### Weeks 6 – 7: Distributed Transactions, Consensus & Event Streams
- **Distributed Transactions & Resilience:**
  - Read [System Resilience: Circuit Breakers & Rate Limiting](../system-design/system-design-fundamentals/08-resilience-circuit-breaker-rate-limiting.md).
  - Two-Phase Commit (2PC) blocking failure modes vs Compensating Sagas.
  - Read [Scaling & Thread Pool Engineering](../system-design/system-design-fundamentals/04-scaling-and-thread-pools.md).
  - Raft Consensus: Leader election, log replication, safety invariants.
  - Paxos basics and Apache ZooKeeper / etcd atomic broadcasts.
- **Messaging & Event Streaming:**
  - Read [Asynchronous Messaging & Kafka](../system-design/system-design-fundamentals/07-asynchronous-messaging-and-kafka.md).
  - Kafka architecture: Partitioning, Consumer Groups, Log compaction, Exactly-Once Semantics (EOS).

---

## 📅 Phase 3 (Days 50 – 70): Heavyweight Architecture Defense

In Senior/Staff HLD interviews, **you drive the conversation for 45 minutes**. The interviewer will probe deeply into bottlenecks, edge-case failures, and cost/scale trade-offs.

### Week 8: Real-Time & Geospatial Scale
- **Deep Study:** [Case Study 07: Real-Time Ride-Hailing System (Uber/Lyft)](../system-design/hld-case-studies/07-design-a-real-time-ride-hailing-system-uber-lyft.md).
  - Ingestion of 1M active driver GPS pings per second.
  - Spatial indexing: Uber H3 vs S2 vs Geohashes vs Quadtrees.
  - WebSocket session pinning with Redis Pub/Sub cluster backplane.

### Week 9: Distributed Storage, Chunking & Conflict Resolution
- **Deep Study:** [Case Study 08: Distributed Cloud Storage & Sync (Google Drive / Dropbox)](../system-design/hld-case-studies/08-design-a-distributed-cloud-storage-and-sync-google-drive-dropbox.md).
  - Content-Defined Chunking (Rabin Fingerprints) for delta sync.
  - Merkle Trees for rapid peer-to-peer data consistency verification.
  - Block Storage (S3) vs Metadata Service (Spanner / CockroachDB).
- **Deep Study:** [Case Study 06: Dynamo-Style Key-Value Store](../system-design/hld-case-studies/06-design-a-distributed-key-value-store-dynamo-style.md).
  - Consistent hashing with virtual nodes, Vector Clocks, anti-entropy synchronization.

### Week 10: High-Concurrency Financial & E-Commerce Transactions
- **Deep Study:** [Case Study 09: E-Commerce Flash Sale & Inventory System](../system-design/hld-case-studies/09-design-an-ecommerce-flash-sale-and-inventory-system.md).
  - Redis Lua Scripts for atomic multi-item inventory decr.
  - Distributed Locking: Redlock vs DB Optimistic concurrency control.
  - Transactional Outbox pattern with Debezium CDC and Kafka for idempotent order creation.
- **Deep Study:** [Case Study 05: Video Streaming Platform (YouTube/Netflix)](../system-design/hld-case-studies/05-design-a-video-streaming-platform-youtube-netflix.md).
  - Distributed chunked video transcoding DAG, CDN caching hierarchies, adaptive bitrate streaming (HLS/DASH).

---

## 📅 Phase 4 (Days 71 – 90): Staff-Level Leadership, Systemic RCA & Executive Mocks

### Weeks 11 – 12: The Bar-Raiser Leadership Track
Staff and Senior engineers are expected to demonstrate cross-organizational influence and cultural stewardship.

- **Study & Internalize:**
  1. [The STAR Method & Impact Storytelling](../behavioral-and-leadership/01-the-star-method-and-high-impact-storytelling.md): Frame 8 stories around high-dollar business impact ($M saved, 99.99% availability).
  2. [Amazon Leadership Principles Mastery](../behavioral-and-leadership/02-amazon-leadership-principles-engineering-mastery.md): Focus on *Have Backbone; Disagree and Commit*, *Are Right, A Lot*, and *Invent and Simplify*.
  3. [Conflict Resolution & Technical Disagreements](../behavioral-and-leadership/03-conflict-resolution-and-technical-disagreements.md): Master the Disagree & Commit framework for high-stakes architectural disputes.
  4. [Blameless Post-Mortems & Sev-1 Incidents](../behavioral-and-leadership/04-blameless-post-mortems-and-sev-1-incident-response.md): Prepare an authentic production crisis narrative featuring 5-Whys RCA and lasting systemic CAPA.
  5. [Reverse Interviewing: 25 High-Leverage Questions](../behavioral-and-leadership/05-reverse-interviewing-25-questions-to-ask-the-interviewer.md): Rehearse senior-level inquiries for Directors and Staff Architects.

### Week 13: Executive Mock Rounds & Active Recall
- **Active Recall Drill:** Complete all 200 flashcards in [200 High-Yield Interview Flashcards](../cheatsheets/04-200-high-yield-interview-flashcards.md).
- **Full 5-Round Simulation Loop:**
  - Round 1: Coding (DSA Hard)
  - Round 2: System Architecture (Multi-Region Active-Active)
  - Round 3: Machine Coding / Concurrency (Lock-free queues, Cache)
  - Round 4: Cross-Functional Leadership & Product Strategy
  - Round 5: Bar Raiser / VP of Engineering Executive Round

---

<div align="center">

| [← Back to 60-Day SDE-2 Track](./02-the-60-day-comprehensive-sde2-backend-roadmap.md) | [Track Hub: Study Plans](./README.md) | [Study Plans Hub →](./README.md) |
| :--- | :---: | ---: |

</div>
