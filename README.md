# Preparation Material for DSA, Java Backend & System Design

```text
  ██████╗  ███████╗  █████╗      ███████╗██╗   ██╗███████╗████████╗███████╗███╗   ███╗
  ██╔══██╗ ██╔════╝ ██╔══██╗     ██╔════╝╚██╗ ██╔╝██╔════╝╚══██╔══╝██╔════╝████╗ ████║
  ██║  ██║ ███████╗ ███████║     ███████╗ ╚████╔╝ ███████╗   ██║   █████╗  ██╔████╔██║
  ██║  ██║ ╚════██║ ██╔══██║     ╚════██║  ╚██╔╝  ╚════██║   ██║   ██╔══╝  ██║╚██╔╝██║
  ██████╔╝ ███████║ ██║  ██║     ███████║   ██║   ███████║   ██║   ███████╗██║ ╚═╝ ██║
  ╚═════╝  ╚══════╝ ╚═╝  ╚═╝     ╚══════╝   ╚═╝   ╚══════╝   ╚═╝   ╚══════╝╚═╝     ╚═╝
```

<div align="center">

![Java 21](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![JUnit 5](https://img.shields.io/badge/JUnit-5.10.2-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![CI Status](https://img.shields.io/badge/CI-Passing-brightgreen?style=for-the-badge&logo=githubactions&logoColor=white)
![Interactive CLI](https://img.shields.io/badge/CLI-Quiz_%7C_Tracker_%7C_Search-blueviolet?style=for-the-badge&logo=gnubash&logoColor=white)
![Modules](https://img.shields.io/badge/Curriculum-188+_Modules-orange?style=for-the-badge)
![Zero Broken Links](https://img.shields.io/badge/Integrity-100%25_Verified-success?style=for-the-badge)

**The production-grade, interactive engineering platform for technical coding, Java backend concurrency, Low-Level Machine Coding, and distributed High-Level Design.**

[⚡ Interactive CLI Suite](#-interactive-terminal-developer-suite) • [☕ Java 21 Playground](#-runnable-java-21-lld-playground) • [🏆 Why This Platform?](#-why-this-platform-differentiation-matrix) • [📁 Curriculum](#-repository-structure)

</div>

---

## 🏆 Why This Platform? (Differentiation Matrix)

| Dimension | Standard Cheat Sheets / Blind 75 | Generic System Design Repos | 🚀 This Master Platform |
| :--- | :--- | :--- | :--- |
| **Language Depth** | Language-agnostic or generic Python | Abstract box diagrams only | **Modern Java 21** with JVM memory layouts (64B cache line, compressed OOPs, memory visibility) |
| **HLD Coverage** | None (DSA only) | 4–6 shallow overviews | **17 Heavyweight Case Studies** with exact capacity math, storage partitioning, CDC, and failure triage |
| **LLD & Machine Coding** | None | Simple class diagrams | **7 Production-Grade Systems** with thread-safe concurrency (`ReentrantReadWriteLock`, atomic CAS, DelayQueue, LOOK/SCAN) |
| **Runnable Code** | Theoretical snippets | Snippets with missing imports | **Production Maven Playground** ([`code-samples/`](code-samples/README.md)) with 36 multi-threaded JUnit 5 tests |
| **Interactive Terminal Tools**| None | None | **Built-in Developer Suite**: SuperMemo SM-2 flashcard quiz (`quiz`), progress tracker (`track`), streak heatmap (`heatmap`), fuzzy search (`search`), and 45-minute mock interview simulator (`mock`) |
| **Cloud Environment** | Manual local configuration | None | **1-Click Dev Container** (`.devcontainer/`) for instant GitHub Codespaces / VS Code setup |
| **Engineering Rigor** | Frequently broken links & typos | Inconsistent formatting | **Automated CI Integrity Engine**: 100% relative link audits, Mermaid syntax validation, and test suites |

---

## ⚡ Interactive Terminal Developer Suite

This repository features built-in developer CLI tools directly runnable from your terminal via `npm`:

```bash
# 1. Active Recall Flashcard Quiz with SuperMemo SM-2 Spaced Repetition (205 cards)
npm run quiz                     # Interactive full quiz
npm run quiz -- --category hld   # Filter by track (dsa | java | hld | lld | leadership | storage)
npm run quiz -- --spaced         # SM-2 Spaced Repetition mode (prioritizes due cards)
npm run quiz -- --stats          # View mastery levels and review intervals

# 2. Interactive Study Roadmap Progress Tracker & Heatmap (supports 30, 60, and 90-day sprints)
npm run track                    # View active progress and mark milestones complete
npm run track -- --plan 2        # Switch to 60-Day Comprehensive SDE2 Roadmap
npm run heatmap                  # Render GitHub-style ASCII study streak activity heatmap

# 3. 45-Minute Timed Mock Interview Simulator with Dynamic Curveballs & Rubric Scoring
npm run mock                     # Launch interactive mock interview session
npm run mock -- --track 2        # Select track directly (1: DSA, 2: System Design, 3: Behavioral)

# 4. Sub-Second Markdown Knowledge Base Fuzzy Search
npm run search "Gorilla"         # Search TSDB Gorilla compression
npm run search "SkipList"        # Find skip list data structures and implementations
```

---

## ☕ Runnable Java 21 LLD Playground

A fully compilable Maven project is located in [`code-samples/`](code-samples/README.md) featuring 7 production-grade Low-Level Design implementations and 36 multi-threaded JUnit 5 concurrency tests:

```bash
cd code-samples
mvn test                         # Run all 36 concurrent JUnit 5 test suites
```

Key implementations:
- **Concurrent In-Memory Cache**: LRU, LFU, and FIFO pluggable eviction strategies with read/write lock striping and TTL purge.
- **In-Memory Rate Limiter Library**: Token Bucket with nanosecond refill precision and Sliding Window Counter.
- **Distributed Task Scheduler**: In-memory `DelayQueue` coordinator with a decoupled worker pool and exponential backoff retries.
- **Multi-Floor Parking Lot**: Atomic spot reservation via per-spot `ReentrantLock`, nearest-spot allocation, and dynamic hourly pricing.
- **Splitwise & Debt Simplifier**: Equal/Exact/Percent splits, cyclic debt elimination, and Min-Cash-Flow greedy graph settlement using dual Max-Heaps.
- **Transactional Key-Value Store**: Nested ACID transactions (`BEGIN`/`COMMIT`/`ROLLBACK`), active/passive TTL expiration, and snapshot isolation.
- **Elevator Controller System**: Multi-car bank coordination using LOOK / SCAN disk-scheduling sweep algorithm with bidirectional `TreeSet` stops.


---

## 📁 Repository Structure

### 🧠 [1. DSA Problem-Solving Strategies & Optimization Engine (`dsa-strategies/`)](dsa-strategies/README.md)
- [**`dsa-strategies/`**](dsa-strategies/README.md) — 8-page interactive master curriculum (Pages 01 to 07) covering the meta-algorithmic operating system for technical interviews:
  - [**`01-problem-deconstruction-and-constraint-triage.md`**](dsa-strategies/01-problem-deconstruction-and-constraint-triage.md): The $10^8$ Operations Rule, Input Constraint Rosetta Stone ($O(1)$ to $O(N!)$), and the 6 Universal Invariants (Monotonicity, Conservation, Parity, Symmetry, Pigeonhole, Topological).
  - [**`02-master-data-structures-selection-and-trade-offs.md`**](dsa-strategies/02-master-data-structures-selection-and-trade-offs.md): Exhaustive taxonomy of **ALL Data Structures** (Arrays, Lists, Stacks, Queues, Heaps, Hash Tables, Trees, BSTs, Balancing, Tries, Segment/Fenwick Trees, DSU, Graphs, Strings, Bloom Filters, LRU/LFU).
  - [**`03-master-algorithms-and-paradigms-catalog.md`**](dsa-strategies/03-master-algorithms-and-paradigms-catalog.md): Complete catalog of **ALL Algorithmic Paradigms** (Two Pointers, Sliding Window, Binary Search, Sorting/Selection, Graph BFS/DFS, Topo Sort, Shortest Path, MST, Advanced Graphs, 8 DP Varieties, Greedy, Backtracking, Divide & Conquer, Bitwise, String Matching, Math).
  - [**`04-the-bud-optimization-framework.md`**](dsa-strategies/04-the-bud-optimization-framework.md): The Systematic Optimization Engine (Bottlenecks, Unnecessary Work, Duplicated Work), evolving solutions from $O(N^3) \to O(N^2) \to O(N \log N) \to O(N) \to O(1)$ with interactive "Spot-the-BUD" challenges.
  - [**`05-space-optimization-and-in-place-techniques.md`**](dsa-strategies/05-space-optimization-and-in-place-techniques.md): Downsizing memory footprints across all DSA: state compression, rolling DP arrays, negation marking, cyclic swapping, bitmasks, Morris.
  - [**`06-edge-cases-and-boundary-defense-playbook.md`**](dsa-strategies/06-edge-cases-and-boundary-defense-playbook.md): Universal Zero-Defect Edge-Case Checklist (overflow, nulls, cycles, duplicates, parity, disconnected components, skewed trees) with interactive stress drills.
  - [**`07-live-interview-execution-and-communication.md`**](dsa-strategies/07-live-interview-execution-and-communication.md): The 45-Minute Interview Architecture, Thinking Out Loud, The 5-Step "I Am Stuck" Rescue Protocol, and Interactive Curveball Simulator.

### ☕ [2. Java Track (`dsa-java/`)](dsa-java/README.md)
- [**`00-linear-vs-non-linear-dsa-and-algorithms-guide.md`**](dsa-java/00-linear-vs-non-linear-dsa-and-algorithms-guide.md) — Foundational master guide on Linear vs. Non-Linear topologies: physical memory layouts (64B cache line prefetching vs pointer indirection), complete structural taxonomy, and an **exhaustive 15+ Computing Scenario Mapping Matrix** with exact Best/Avg/Worst TC and SC bounds.
- [**`java-fundamentals/`**](dsa-java/java-fundamentals/README.md) — 10-page master guide covering JVM memory models, Stack vs. Heap, OOP pillars, Collections Framework, Generics, Lambdas, and Multithreading basics.
- [**`java-backend/`**](dsa-java/java-backend/README.md) — 14-page enterprise backend engineering course (Pages 00 to 13) covering HTTP/REST protocols, Servlets, Spring Boot 3.x, Spring Data JPA/Hibernate (N+1 query fixes), Concurrency & Virtual Threads (Java 21), Redis Caching, Apache Kafka, Spring Cloud Microservices, Spring Security with Stateless JWT, Production Observability, and Automated Testing & Packaging (Maven/Gradle, MockMvc, Slices, Testcontainers).
- [**`arrays/`**](dsa-java/arrays/README.md) — 9-page master curriculum (Pages 00 to 08) on Arrays (1D, 2D, 3D): JVM memory layouts, CPU cache lines (64B), array-of-pointers dynamics, flat 1D coordinate math, Two Pointers, Sliding Window, Prefix Sums, Kadane's algorithm, Dutch National Flag, Next Permutation, Cyclic Sort, In-Place Matrix Transformations (Spiral, 90° Rotate, Set Zeroes, Saddleback Search), 3D State BFS, Dual-Agent 3D DP, Monotonic Deques, Answer-Space Binary Search, and Large-Scale Out-of-RAM Array architectures.
- [**`linked-lists/`**](dsa-java/linked-lists/README.md) — 8-page master curriculum (Pages 00 to 07) on Linked Lists & ArrayList: array fragmentation limitations, what linked lists solve, `ArrayList` vs `LinkedList` memory footprints and cache locality, Sentinel (Dummy) nodes, Fast & Slow pointers (Floyd's Cycle I & II, Palindromes, Intersections), In-Place Reversals ($K$-Group, Subsegments, Reorder List), Merge $K$ Sorted Lists, Linked List Merge Sort, Deep Copy with Random Pointers (In-place Interleaving), composite architectures (**LRU Cache** and **LFU Cache**), and concurrent lock-free Skip Lists and Queues.
- [**`stacks-and-queues/`**](dsa-java/stacks-and-queues/README.md) — 6-page master curriculum (Pages 01 to 05) on Stacks, Queues & Deques: LIFO/FIFO restricted-access linear mechanics, Call Stack memory layout, Min Stack in $O(1)$ space math, Monotonic Stacks (Daily Temperatures), Circular Ring Buffers, Monotonic Deques (Sliding Window Maximum), Prefix Sums with Deques (Shortest Subarray Sum $\ge K$), Expression Parsers (Shunting-Yard & Basic Calculator), and Lock-Free SPSC/MPSC Queues with cache-line padding.
- [**`trees/`**](dsa-java/trees/README.md) — 9-page master curriculum (Pages 00 to 07) on Trees, BSTs, Balancing, Tries & Storage Engines: 32-byte JVM node layouts (Compressed OOPs), The 7 Universal Tree Problem Archetypes, Morris In-Order Traversal ($O(1)$ space), Binary Tree Maximum Path Sum, BST dynamic boundary invariants and node splicing, AVL Tree rotations (LL, RR, LR, RL), Segment Trees with Lazy Propagation ($O(\log N)$ RMQ), Binary Indexed Trees (LSB bitwise Fenwick), Bitwise 32-bit Tries (Maximum XOR pair in $O(N)$), Database Storage Engines (B+ Tree page fanout $M=1000$ vs LSM-Tree Write-Ahead Log and SSTables), Binary Tree Cameras (Greedy 3-state machine), Count Complete Nodes in $O((\log N)^2)$, Radial Distance K BFS, and Euler Tour Subtree Flattening.
- [**`heaps-and-greedy/`**](dsa-java/heaps-and-greedy/README.md) — 7-page master curriculum (Pages 01 to 06) on Heaps, Priority Queues & Greedy Algorithms: pointerless array-backed complete binary trees, $\mathcal{O}(N)$ build-heap power series proof ($\sum \frac{h}{2^h} = 2$), custom `ArrayMinHeap<T>` with dynamic resizing, custom Indexed Priority Queue (IPQ) with $O(\log N)$ decrease-key and $O(1)$ lookup, Top-K Quickselect (Dutch National Flag 3-way partition in $O(N)$ average) vs Bucket Sort, Two-Heap architectures with Lazy Deletion via hash frequency maps for $O(N \log K)$ streaming window medians, IPO greedy sequencing, K-Way merges and running bounding max ranges, the Formal Greedy Exchange Argument proof template, Task Scheduler slot math, single-pass Candy slope optimization in $O(1)$ space, Course Schedule III deadline swapping, Minimum Cost to Hire K Workers ratio scanning, and Huffman Coding optimal prefix trees.
- [**`graphs/`**](dsa-java/graphs/README.md) — 7-page master curriculum (Pages 01 to 06) on Graphs, DSU & Shortest Paths: physical memory layouts (Adjacency Matrix, List, and cache-friendly Compressed Sparse Row / CSR), Handshaking Lemma degree invariants, Bidirectional BFS (Word Ladder with frontier collision), grid sinking DFS (Number of Islands), cycle-protected graph cloning, Kahn's in-degree BFS vs 3-color DFS topological sorting (Course Schedule I & II, Alien Dictionary prefix edge cases), shortest path algorithms (Dijkstra non-negative relaxation, 0-1 BFS with `ArrayDeque` in linear $O(V + E)$ time, Bellman-Ford snapshot pass arrays, Floyd-Warshall $O(V^3)$ APSP), Disjoint Set Union (DSU) with Path Compression & Union by Rank achieving Inverse Ackermann $\mathcal{O}(\alpha(N))$ time, Cut/Cycle properties, Kruskal's vs dense Prim's $O(V^2)$ MST, Bipartite odd-cycle characterization and 2-coloring, and Tarjan's single-pass DFS bridge-finding (`tin`/`low`) and Hierholzer's Eulerian paths.
- [**`dynamic-programming/`**](dsa-java/dynamic-programming/README.md) — 8-page master curriculum (Pages 01 to 07) on Dynamic Programming (DP): the 5-step DP framework (State Definition $\to$ Base Cases $\to$ Bellman Recurrence $\to$ Topological Order $\to$ Space Reduction), Memoization vs Tabulation, 1D rolling state formulations (House Robber in $O(1)$ space, Coin Change, and Longest Increasing Subsequence with $\mathcal{O}(N \log N)$ patience sort binary search), 2D Grid DP & Pathfinding (Unique Paths obstacles, Min Path Sum, Dungeon Game reverse bottom-up health invariant), the Knapsack Family (0/1 backward vs Unbounded forward capacity sweeps, Partition Equal Subset, Target Sum algebraic reductions, Coin Change II combination loops), Strings & Edit Distance (LCS with rolling rows, Levenshtein distance, Regular Expression Matching with wildcard star lookaheads), Interval DP (Burst Balloons inverted last-choice invariant, Minimax game theory), Tree DP (House Robber III 2-state vectors, Binary Tree Max Path Sum apex curvature, $O(N)$ two-pass subtree rerooting), and Bitmask DP (state-space graph BFS for Traveling Salesperson / Shortest Path All Nodes, Partition to K Equal Sum Subsets).
- [**`advanced-problems-arrays-and-linked-lists/`**](dsa-java/advanced-problems-arrays-and-linked-lists/README.md) — 7-page hard-tier algorithmic track (Pages 01 to 06) on Advanced Problems: Dual Partition Binary Search ($O(\log(\min(M, N)))$ Median), Merge Sort Inversion Tracking ($O(N \log N)$), Monotonic Stacks & 2D Histogram Reductions ($O(N)$), Non-Monotonic to Monotonic Algebraic Window Reductions ($\text{Exact}(K) = \text{AtMost}(K) - \text{AtMost}(K-1)$), Bidirectional Subsequence Contraction, Array-as-Graph Floyd's Tortoise & Hare, Synchronized In-Order BST synthesis, All $O(1)$ Doubly Linked Frequency Buckets, Dual-Deque Cursor Text Editors, Concurrent TTL LRU Caches, and LMAX-style Bounded Ring Buffers with bitwise power-of-two masking.
- [**`advanced-problems-trees-graphs-and-dp/`**](dsa-java/advanced-problems-trees-graphs-and-dp/README.md) — 7-page hard-tier algorithmic track (Pages 01 to 06) on Advanced Non-Linear & DP Problems: 3D boundary shrinking Min-Heap (Trapping Rain Water II in $\mathcal{O}(MN \log(MN))$), Bidirectional BFS & Predecessor DAG Backtracking (Word Ladder II), Dual-Key Sorting & Patience Sorting LIS (Russian Doll Envelopes in $\mathcal{O}(N \log N)$), Tarjan's Discovery & Low-Link Bridges (Critical Connections in $\mathcal{O}(V + E)$), Dual-Heap with Lazy Hash Deletion (Sliding Window Median in $\mathcal{O}(N \log K)$), and Arbitrary Cyclic Graph Wire Serialization & Deserialization with Object Identity.
- [**`backtracking-and-recursion/`**](dsa-java/backtracking-and-recursion/README.md) — 7-page master curriculum (Pages 01 to 06) on Backtracking & Recursion: JVM thread call stack frames, stack overflow limits, Fast Power ($O(\log N)$), Power Set $2^N$, duplicate pruning invariants, Permutations ($N!$), Next Permutation ($O(N)$), Combination Sum I/II/III, Partition to K Equal Sum Subsets with empty bucket symmetry breaking, in-place 2D grid character masking (Word Search I & II with Trie), bitmask constraint satisfaction (N-Queens & 9x9 Sudoku Solver), and zero-sum Game Theory (Minimax with Alpha-Beta pruning).
- [**`bit-manipulation-and-math/`**](dsa-java/bit-manipulation-and-math/README.md) — 7-page master curriculum (Pages 01 to 06) on Bit Manipulation & Mathematics: Two's complement register mechanics, sign extension (`>>` vs `>>>`), the 6 universal silicon bit hacks (`n & (n-1)`, `n & (-n)`), Brian Kernighan vs SWAR popcount, XOR self-inversion parity, 3-state finite state machines (Single Number II mod 3 counter), LSB partitioning (Single Number III), $O(3^N)$ submask enumeration, Shortest Path Visiting All Nodes BFS, Sieve of Eratosthenes vs Euler's Linear Sieve ($O(N)$ SPF), Euclidean GCD/LCM, Fast Exponentiation ($O(\log N)$), Fermat's Little Theorem modular inverse, Combinatorics $n\text{C}r \pmod M$, Reservoir Sampling, Fisher-Yates uniform shuffle, and Fast Matrix Exponentiation for linear recurrences.
- [**`string-algorithms/`**](dsa-java/string-algorithms/README.md) — 7-page master curriculum (Pages 01 to 06) on String Algorithms & Pattern Matching: Modern Java 21 Compact Strings (Latin-1 vs UTF-16 byte storage), String Constant Pool, Two-Pointer Palindromes, Knuth-Morris-Pratt (KMP) $\pi$ LPS prefix function, string periodicity arithmetic, Polynomial Rolling Hashes with Double Hashing collision mitigation, Longest Duplicate Substring ($O(N \log N)$), $Z$-algorithm with $[L, R]$ Z-box window invariant, Manacher's strictly $\mathcal{O}(N)$ mirrored palindrome radii, and the Aho-Corasick multi-pattern dictionary matching automaton.

### 🏛️ [3. System Design Track (`system-design/`)](system-design/README.md)
- [**`system-design-fundamentals/`**](system-design/system-design-fundamentals/README.md) — 10-page comprehensive guide covering High-Level Design (HLD), Low-Level Design (LLD), GoF design patterns in Java, thread pool engineering, connection pooling (HikariCP), caching strategies, Kafka event streaming, circuit breakers, and the 4-step interview blueprint.
- [**`lld-machine-coding/`**](system-design/lld-machine-coding/README.md) — 10-page enterprise track on Object-Oriented Low-Level Design & Machine Coding in Java (Java 17/21) with runnable Maven playground ([`code-samples/`](code-samples/README.md)): 90-minute interview framework, Parking Lot System (Strategy, Factory, concurrent spot locking), Elevator Control System (State, LOOK/SCAN disk scheduling, multi-car bank), In-Memory Key-Value Store with TTL & Transactions (ReadWriteLock, passive/active cleanup, ACID rollback/commit), Expense Sharing System / Splitwise (Equal/Exact/Percent strategies, greedy Min-Cash-Flow debt simplification), Movie Ticket Booking System / BookMyShow (atomic seat locks with TTL, deadlock-free sorted lock hierarchy), Concurrent In-Memory Cache with Pluggable Eviction Policies (generics, ReadWriteLock, LRU/LFU/FIFO, active+passive TTL), Food Delivery System / Swiggy-Zomato (nearest-partner matching, atomic CAS assignment, surge pricing), Distributed Task & Job Scheduler (DelayQueue, worker pool, exponential backoff), and In-Memory Rate Limiter Library (Token Bucket, Sliding Window Log, nanoTime CAS).
- [**`hld-case-studies/`**](system-design/hld-case-studies/README.md) — 18-page end-to-end High-Level Design (HLD) deep-dive case studies covering capacity estimation, data partitioning, and failure mode analysis:
  - [**`01-design-a-distributed-rate-limiter.md`**](system-design/hld-case-studies/01-design-a-distributed-rate-limiter.md): Sliding window counter, Redis cluster + Lua scripts, local token lease caching, clock drift.
  - [**`02-design-a-global-url-shortener-tinyurl.md`**](system-design/hld-case-studies/02-design-a-global-url-shortener-tinyurl.md): 100:1 read ratio, Base62 Key Generation Service (KGS), 301 vs 302 redirects, DB sharding.
  - [**`03-design-a-real-time-chat-system-whatsapp-slack.md`**](system-design/hld-case-studies/03-design-a-real-time-chat-system-whatsapp-slack.md): WebSockets, Cassandra LSM-Tree, per-chat monotonic sequencing, heartbeat presence.
  - [**`04-design-a-distributed-message-queue-kafka-clone.md`**](system-design/hld-case-studies/04-design-a-distributed-message-queue-kafka-clone.md): Sequential disk I/O, PageCache, Linux zero-copy `sendfile`, KRaft consensus, ISR.
  - [**`05-design-a-video-streaming-platform-youtube-netflix.md`**](system-design/hld-case-studies/05-design-a-video-streaming-platform-youtube-netflix.md): Transcoding DAG, Adaptive Bitrate Streaming (HLS/DASH), CDN edge caching.
  - [**`06-design-a-distributed-key-value-store-dynamo-style.md`**](system-design/hld-case-studies/06-design-a-distributed-key-value-store-dynamo-style.md): Consistent hashing, vnodes, vector clocks, tunable quorum ($R+W>N$), hinted handoff.
  - [**`07-design-a-real-time-ride-hailing-system-uber-lyft.md`**](system-design/hld-case-studies/07-design-a-real-time-ride-hailing-system-uber-lyft.md): Uber H3 Hexagonal spatial indexing, 1M/s driver pings, Quadtrees, Redis pub/sub backplane.
  - [**`08-design-a-distributed-cloud-storage-and-sync-google-drive-dropbox.md`**](system-design/hld-case-studies/08-design-a-distributed-cloud-storage-and-sync-google-drive-dropbox.md): Content-Defined Chunking (Rabin fingerprints), Merkle trees, metadata service vs block store.
  - [**`09-design-an-ecommerce-flash-sale-and-inventory-system.md`**](system-design/hld-case-studies/09-design-an-ecommerce-flash-sale-and-inventory-system.md): Redis Lua script atomic reservation, Transactional Outbox + Debezium CDC, idempotency.
  - [**`10-design-a-distributed-web-crawler-google-search.md`**](system-design/hld-case-studies/10-design-a-distributed-web-crawler-google-search.md): URL frontier politeness, MurmurHash3 de-duplication, distributed worker clusters.
  - [**`11-design-a-distributed-metrics-and-telemetry-system-datadog.md`**](system-design/hld-case-studies/11-design-a-distributed-metrics-and-telemetry-system-datadog.md): Gorilla floating-point XOR compression, M3DB/TimescaleDB chunking, Kafka ingestion.
  - [**`12-design-a-collaborative-real-time-document-editor-google-docs.md`**](system-design/hld-case-studies/12-design-a-collaborative-real-time-document-editor-google-docs.md): Operational Transformation (OT) / CRDT (Yjs/Loro), WebSocket fan-out, undo/redo vectors.
  - [**`13-design-a-proximity-and-nearby-search-service-yelp-maps.md`**](system-design/hld-case-studies/13-design-a-proximity-and-nearby-search-service-yelp-maps.md): Geohash prefix tree, Google S2 / Uber H3 cell spatial hierarchies, two-tier cache.
  - [**`14-design-a-distributed-vector-database-pinecone-milvus.md`**](system-design/hld-case-studies/14-design-a-distributed-vector-database-pinecone-milvus.md): HNSW multi-layer graph, IVF-PQ product quantization, single-stage bitset filtering, S3 immutable segments.
  - [**`15-design-a-high-throughput-payment-gateway-and-ledger-stripe.md`**](system-design/hld-case-studies/15-design-a-high-throughput-payment-gateway-and-ledger-stripe.md): Immutable double-entry ledger, Redis atomic idempotency keys, zero-sum verification, daily bank reconciliation.
  - [**`16-design-a-real-time-ad-click-event-aggregator-google-meta.md`**](system-design/hld-case-studies/16-design-a-real-time-ad-click-event-aggregator-google-meta.md): Apache Flink event-time watermarking, 1M clicks/sec ingestion, HyperLogLog unique reach, ClickHouse OLAP.
  - [**`17-design-a-global-live-video-streaming-platform-twitch-youtube-live.md`**](system-design/hld-case-studies/17-design-a-global-live-video-streaming-platform-twitch-youtube-live.md): Low-Latency HLS (LL-HLS) chunked transfer, GPU transcoder fleet, CDN origin shielding, massive chat sampling.

### 🤖 [4. AI for Developers Track (`ai-for-developers/`)](ai-for-developers/README.md)
- [**`ai-for-developers/`**](ai-for-developers/README.md) — 10-page comprehensive guide on using AI as a 10x engineering velocity multiplier and building AI applications: moving from typist to architect, 0-to-1 REST API scaffolding, instant stack trace triage, automated testing with Testcontainers, tool mastery (**Cursor**, **Claude Cowork & Projects**, **GitHub Copilot**, `.cursorrules`), accelerating in **500k+ LOC enterprise codebases**, System Design capacity math, **Autonomous Coding Agents & Model Context Protocol (MCP)**, building enterprise AI applications with **Spring AI & RAG (`pgvector`)**, and the **30+ Copy-Paste Developer Prompt Cheatsheet**.

### ⚡ [5. 24-Hour Quick-Revision Cheatsheets (`cheatsheets/`)](cheatsheets/README.md)
- [**`01-dsa-formulas-and-invariants-cheatsheet.md`**](cheatsheets/01-dsa-formulas-and-invariants-cheatsheet.md) — Master complexity bounds table, Master Theorem quick reference, Bitwise arithmetic formulas, Tree & Graph formulas, and the 6 Universal Algorithmic Invariants.
- [**`02-system-design-numbers-and-capacity-math.md`**](cheatsheets/02-system-design-numbers-and-capacity-math.md) — Latency numbers every systems engineer should know, powers of 2 vs 10 shorthand, QPS mental math rules, and High Availability SLA downtime tables.
- [**`03-gof-patterns-and-concurrency-cheatsheet.md`**](cheatsheets/03-gof-patterns-and-concurrency-cheatsheet.md) — 23 GoF Design Patterns quick-trigger matrix, Java concurrency lock selection matrix, `volatile` memory visibility rules, and modern concurrency primitives.
- [**`04-200-high-yield-interview-flashcards.md`**](cheatsheets/04-200-high-yield-interview-flashcards.md) — 200+ interactive active recall flashcards covering DSA complexity invariants, distributed HLD, Java concurrency, behavioral leadership, and database storage engines.

### 🎯 [6. Behavioral & Engineering Leadership Track ("The Bar Raiser") (`behavioral-and-leadership/`)](behavioral-and-leadership/README.md)
- [**`behavioral-and-leadership/`**](behavioral-and-leadership/README.md) — 6-page master track for navigating Tier-1 technical leadership and bar-raiser rounds:
  - [**`01-the-star-method-and-high-impact-storytelling.md`**](behavioral-and-leadership/01-the-star-method-and-high-impact-storytelling.md): The Technical STAR method, "I" vs "We" ownership, and quantifying impact metrics.
  - [**`02-amazon-leadership-principles-engineering-mastery.md`**](behavioral-and-leadership/02-amazon-leadership-principles-engineering-mastery.md): All 16 Amazon Leadership Principles translated directly into software engineering decisions.
  - [**`03-conflict-resolution-and-technical-disagreements.md`**](behavioral-and-leadership/03-conflict-resolution-and-technical-disagreements.md): Data-backed architectural disputes, PM scope creep, and Disagree & Commit protocols.
  - [**`04-blameless-post-mortems-and-sev-1-incident-response.md`**](behavioral-and-leadership/04-blameless-post-mortems-and-sev-1-incident-response.md): Incident Commander protocol, 5-Whys Root Cause Analysis, and systemic prevention (CAPA).
  - [**`05-reverse-interviewing-25-questions-to-ask-the-interviewer.md`**](behavioral-and-leadership/05-reverse-interviewing-25-questions-to-ask-the-interviewer.md): 25 high-leverage reverse interviewing questions for EMs, Staff Architects, Peers, and Executives.

### 📅 [7. Guided Study Roadmaps (30 / 60 / 90 Days) (`study-plans/`)](study-plans/README.md)
- [**`study-plans/`**](study-plans/README.md) — 4-page battle-tested study schedules tailored to interview timelines and seniority targets:
  - [**`01-the-30-day-faang-sprint-roadmap.md`**](study-plans/01-the-30-day-faang-sprint-roadmap.md): High-yield 4-week sprint focusing on top 14 coding patterns, core HLD staples, and STAR stories.
  - [**`02-the-60-day-comprehensive-sde2-backend-roadmap.md`**](study-plans/02-the-60-day-comprehensive-sde2-backend-roadmap.md): 8-week structured roadmap covering full DSA, LLD machine coding, 7 HLD case studies, and Amazon LPs.
  - [**`03-the-90-day-senior-and-staff-architect-blueprint.md`**](study-plans/03-the-90-day-senior-and-staff-architect-blueprint.md): 12-week comprehensive curriculum for Senior (L5), Staff (L6), and Principal candidates focusing on distributed consensus, multi-region architectures, and engineering influence.

---

<div align="center">

### ☕ *Sip your coffee & enjoy the journey — more master engineering notes are brewing!!* 🚀✨

*Curated with precision for software engineers, backend developers, and system architects.* 💡🎯

</div>

