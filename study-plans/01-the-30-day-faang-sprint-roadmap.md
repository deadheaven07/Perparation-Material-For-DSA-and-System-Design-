# 01. The 30-Day FAANG Sprint Roadmap

[← Back to Study Plans Hub](./README.md) | [Track Hub](./README.md) | [Next: 60-Day Comprehensive SDE-2 Track →](./02-the-60-day-comprehensive-sde2-backend-roadmap.md)

---

## 🎯 Target Persona & Overview

- **Audience:** Candidates with on-site interviews scheduled in 3 to 5 weeks.
- **Time Commitment:** 20 to 25 hours per week (2.5 to 3 hours on weekdays, 4 to 5 hours on weekends).
- **Core Philosophy:** **High-Yield Pattern Mastery over Volume**. Solving 75 carefully selected pattern-archetype problems is 10x more effective than aimlessly grinding 300 random problems.

```
╭───────────────────────────────────────────────────────────────────────────────────────────╮
│                                30-DAY SPRINT MILESTONE MAP                                │
├────────────┬────────────────────────────┬─────────────────────────────┬───────────────────┤
│ Week       │ Coding Focus               │ System Design Focus         │ Behavioral Focus  │
├────────────┼────────────────────────────┼─────────────────────────────┼───────────────────┤
│ Week 1     │ Linear & Window Patterns   │ System Design Foundations   │ STAR Architecture │
│ Week 2     │ Hierarchical (Trees/Heaps) │ Tier-1 HLD: Rate Limit & URL│ Technical Conflict│
│ Week 3     │ Relational (Graphs & DP)   │ Tier-1 HLD: KV & Sync Drive │ Ownership & RCA   │
│ Week 4     │ Advanced & Mocks           │ Tier-1 HLD: Uber & FlashSale│ Mock Loops & Qs   │
╰────────────┴────────────────────────────┴─────────────────────────────┴───────────────────╯
```

---

## 📅 Week 1 (Days 1 – 7): Linear Foundations, Core HLD & STAR Framing

### Day 1: Two Pointers & In-Place Manipulation
- **DSA:** Review [Two Pointers & Sliding Window](../dsa-java/arrays/03-two-pointers-and-sliding-window.md).
  - Solve: *Two Sum II - Input Array Is Sorted* (LeetCode 167), *3Sum* (LeetCode 15), *Container With Most Water* (LeetCode 11).
- **System Design:** Read [Java Backend Architecture & Threading Models](../system-design/system-design-fundamentals/01-java-backend-architecture.md) (Throughput, Latency, Concurrency).
- **Behavioral:** Draft 2 STAR story hooks using [The STAR Method & Storytelling](../behavioral-and-leadership/01-the-star-method-and-high-impact-storytelling.md).

### Day 2: Sliding Window (Fixed & Dynamic)
- **DSA:** Review [Two Pointers & Sliding Window](../dsa-java/arrays/03-two-pointers-and-sliding-window.md).
  - Solve: *Maximum Average Subarray I* (LeetCode 643), *Longest Substring Without Repeating Characters* (LeetCode 3), *Minimum Window Substring* (LeetCode 76).
- **System Design:** Read [Microservices, API Gateways & Protocols](../system-design/system-design-fundamentals/09-microservices-and-api-gateways.md).

### Day 3: Fast & Slow Pointers & In-Place Linked Lists
- **DSA:** Review [Fast & Slow Pointers and Cycle Detection](../dsa-java/linked-lists/02-fast-and-slow-pointers-and-cycle-detection.md).
  - Solve: *Linked List Cycle II* (LeetCode 142), *Reverse Linked List* (LeetCode 206), *Reorder List* (LeetCode 143).
- **System Design:** Read [Caching Strategies & LRU in Java](../system-design/system-design-fundamentals/06-caching-strategies-and-lru.md) (Cache-Aside, Write-Through, Eviction).

### Day 4: Modified Binary Search
- **DSA:** Review [Advanced Array Techniques & Binary Search](../dsa-java/arrays/08-advanced-array-techniques-and-large-scale-systems.md).
  - Solve: *Search in Rotated Sorted Array* (LeetCode 33), *Find Minimum in Rotated Sorted Array* (LeetCode 153), *Koko Eating Bananas* (LeetCode 875).
- **System Design:** Read [Databases & Connection Pooling](../system-design/system-design-fundamentals/05-databases-and-connection-pooling.md).

### Day 5: Interval Scheduling & Merging
- **DSA:** Review [K-Way Merge & Interval Scheduling](../dsa-java/heaps-and-greedy/04-k-way-merge-and-interval-scheduling.md).
  - Solve: *Merge Intervals* (LeetCode 56), *Insert Interval* (LeetCode 57), *Non-overlapping Intervals* (LeetCode 435).
- **Behavioral:** Flesh out your primary technical impact story: State the baseline metric, your technical intervention, and the final quantified delta.

### Days 6 & 7 (Weekend Review & Deep Dives):
- **DSA:** Review all week's problems without looking at code. Re-solve missed edge cases.
- **HLD Deep Dive:** Study [Case Study 02: Design a Global URL Shortener (TinyURL)](../system-design/hld-case-studies/02-design-a-global-url-shortener-tinyurl.md). Draw the architecture end-to-end on paper or whiteboard from memory in 35 minutes.

---

## 📅 Week 2 (Days 8 – 14): Hierarchical Structures, Heaps & Scalable HLD

### Day 8: Binary Trees & Tree Traversals (BFS / DFS)
- **DSA:** Review [Tree Fundamentals & Traversal Paradigms](../dsa-java/trees/01-tree-fundamentals-and-traversal-paradigms.md).
  - Solve: *Maximum Depth of Binary Tree* (LeetCode 104), *Invert Binary Tree* (LeetCode 226), *Lowest Common Ancestor of a BST* (LeetCode 235).
- **System Design:** Read [Asynchronous Messaging & Kafka](../system-design/system-design-fundamentals/07-asynchronous-messaging-and-kafka.md).

### Day 9: Binary Tree Construction & Path Sums
- **DSA:** Review [Tree Archetypes & Path Metrics](../dsa-java/trees/02-tree-archetypes-and-path-metrics.md).
  - Solve: *Construct Binary Tree from Preorder and Inorder Traversal* (LeetCode 105), *Binary Tree Maximum Path Sum* (LeetCode 124).
- **System Design:** Study [Case Study 01: Design a Distributed Rate Limiter](../system-design/hld-case-studies/01-design-a-distributed-rate-limiter.md). Master Token Bucket vs Redis Sliding Window.

### Day 10: Heaps & Top-K Frequent Elements
- **DSA:** Review [Binary Heap Fundamentals & Priority Queues](../dsa-java/heaps-and-greedy/01-binary-heap-fundamentals-and-priority-queues.md) and [Top K & Selection Paradigms](../dsa-java/heaps-and-greedy/02-top-k-and-selection-paradigms.md).
  - Solve: *Kth Largest Element in an Array* (LeetCode 215), *Top K Frequent Elements* (LeetCode 347), *Find Median from Data Stream* (LeetCode 295).

### Day 11: Monotonic Stack & Sliding Window Maximum
- **DSA:** Review [Stack Fundamentals & Monotonic Stacks](../dsa-java/stacks-and-queues/01-stack-fundamentals-and-monotonic-stacks.md).
  - Solve: *Daily Temperatures* (LeetCode 739), *Largest Rectangle in Histogram* (LeetCode 84), *Sliding Window Maximum* (LeetCode 239).
- **Behavioral:** Study [Amazon Leadership Principles Mastery](../behavioral-and-leadership/02-amazon-leadership-principles-engineering-mastery.md) for *Customer Obsession* and *Bias for Action*.

### Day 12: Backtracking & State Space Pruning
- **DSA:** Review [Subsets & Permutations Generating Spaces](../dsa-java/backtracking-and-recursion/02-subsets-and-permutations-generating-combinatorial-spaces.md).
  - Solve: *Subsets* (LeetCode 78), *Permutations* (LeetCode 46), *Word Search* (LeetCode 79).
- **System Design:** Study [Case Study 04: Design a Distributed Message Queue (Kafka Clone)](../system-design/hld-case-studies/04-design-a-distributed-message-queue-kafka-clone.md).

### Days 13 & 14 (Weekend Mock & LLD):
- **LLD Machine Coding:** Code [01. Design a Parking Lot System](../system-design/lld-machine-coding/01-design-a-parking-lot-system.md) from scratch in Java in 60 minutes.
- **Behavioral:** Prepare responses for technical disagreements using [Conflict Resolution & Technical Disagreements](../behavioral-and-leadership/03-conflict-resolution-and-technical-disagreements.md).

---

## 📅 Week 3 (Days 15 – 21): Graphs, Dynamic Programming & Heavyweight Systems

### Day 15: Graph Traversals (BFS / DFS) & Cycle Detection
- **DSA:** Review [Graph Representations & Traversal Foundations](../dsa-java/graphs/01-graph-representations-and-traversal-foundations.md).
  - Solve: *Number of Islands* (LeetCode 200), *Clone Graph* (LeetCode 133), *Course Schedule* (LeetCode 207 - Cycle detection).
- **System Design:** Read [System Resilience: Circuit Breakers & Rate Limiting](../system-design/system-design-fundamentals/08-resilience-circuit-breaker-rate-limiting.md).

### Day 16: Shortest Paths (Dijkstra) & Topological Sort
- **DSA:** Review [Topological Sorting & DAG Architectures](../dsa-java/graphs/02-topological-sorting-and-dag-architectures.md) and [Shortest Paths](../dsa-java/graphs/03-shortest-paths-dijkstra-bellman-ford-and-floyd-warshall.md).
  - Solve: *Network Delay Time* (LeetCode 743), *Course Schedule II* (LeetCode 210), *Alien Dictionary* (LeetCode 269).
- **System Design:** Study [Case Study 06: Dynamo-Style Key-Value Store](../system-design/hld-case-studies/06-design-a-distributed-key-value-store-dynamo-style.md).

### Day 17: 1D Dynamic Programming Foundations
- **DSA:** Review [DP Fundamentals & 1D State Formulations](../dsa-java/dynamic-programming/01-dp-fundamentals-and-1d-state-formulations.md).
  - Solve: *Climbing Stairs* (LeetCode 70), *House Robber* (LeetCode 198), *Coin Change* (LeetCode 322).
- **System Design:** Study [Case Study 08: Distributed Cloud Storage & Sync (Google Drive / Dropbox)](../system-design/hld-case-studies/08-design-a-distributed-cloud-storage-and-sync-google-drive-dropbox.md) (Chunking & Merkle Trees).

### Day 18: 2D Dynamic Programming (Grid & String Alignment)
- **DSA:** Review [Grid & Multi-Dimensional DP](../dsa-java/dynamic-programming/02-grid-and-multi-dimensional-dp.md).
  - Solve: *Unique Paths* (LeetCode 62), *Longest Common Subsequence* (LeetCode 1143), *Edit Distance* (LeetCode 72).
- **Behavioral:** Prepare your failure / outage story using [Blameless Post-Mortems & Sev-1 Incident Response](../behavioral-and-leadership/04-blameless-post-mortems-and-sev-1-incident-response.md).

### Day 19: Trie (Prefix Tree) & Advanced String Matching
- **DSA:** Review [Tries & Bitwise Prefix Trees](../dsa-java/trees/05-tries-and-bitwise-prefix-trees.md).
  - Solve: *Implement Trie (Prefix Tree)* (LeetCode 208), *Design Add and Search Words Data Structure* (LeetCode 211), *Word Search II* (LeetCode 212).
- **System Design:** Study [Case Study 07: Real-Time Ride-Hailing System (Uber/Lyft)](../system-design/hld-case-studies/07-design-a-real-time-ride-hailing-system-uber-lyft.md) (Geohash & Quadtree dispatch).

### Days 20 & 21 (Weekend Heavyweight Systems & LLD):
- **HLD Deep Dive:** Study [Case Study 09: E-Commerce Flash Sale & Inventory System](../system-design/hld-case-studies/09-design-an-ecommerce-flash-sale-and-inventory-system.md).
- **LLD Machine Coding:** Code [06. Design a Concurrent In-Memory Cache](../system-design/lld-machine-coding/06-design-a-concurrent-in-memory-cache-with-eviction-policies.md) from scratch in Java.

---

## 📅 Week 4 (Days 22 – 30): Mock Interview Loops, Edge Cases & Polish

### Days 22 – 24: Timed Coding Simulators
- Solve 2 LeetCode Medium/Hard problems under strict 40-minute timer without an IDE autocomplete:
  - Day 22: *Trapping Rain Water* (LeetCode 42) + *Word Break* (LeetCode 139).
  - Day 23: *Merge k Sorted Lists* (LeetCode 23) + *Median of Two Sorted Arrays* (LeetCode 4).
  - Day 24: *LRU Cache* (LeetCode 146) + *Serialize and Deserialize Binary Tree* (LeetCode 297).

### Days 25 – 27: Full-Loop System Design Mocks
- Execute 45-minute timed whiteboard presentations:
  - Day 25: Mock Design: Scalable Chat / WhatsApp ([Case Study 03](../system-design/hld-case-studies/03-design-a-real-time-chat-system-whatsapp-slack.md)).
  - Day 26: Mock Design: Video Streaming Platform ([Case Study 05](../system-design/hld-case-studies/05-design-a-video-streaming-platform-youtube-netflix.md)).
  - Day 27: Review [System Design Numbers & Capacity Estimations](../cheatsheets/02-system-design-numbers-and-capacity-math.md).

### Days 28 – 29: Behavioral Polish & Reverse Interview Drill
- Review all STAR stories with verbal delivery.
- Select and rehearse your top 5 questions from [Reverse Interviewing: 25 High-Leverage Questions](../behavioral-and-leadership/05-reverse-interviewing-25-questions-to-ask-the-interviewer.md).
- Review [200 High-Yield Interview Flashcards](../cheatsheets/04-200-high-yield-interview-flashcards.md).

### Day 30: Rest, Decompression & Mindset
- **Zero heavy coding.** Review high-level cheatsheets, sleep 8+ hours, verify hardware, camera, and internet connection. Walk into your interview with supreme confidence.

---

<div align="center">

| [← Back to Study Plans Hub](./README.md) | [Track Hub: Study Plans](./README.md) | [Next: 60-Day Comprehensive SDE-2 Track →](./02-the-60-day-comprehensive-sde2-backend-roadmap.md) |
| :--- | :---: | ---: |

</div>
