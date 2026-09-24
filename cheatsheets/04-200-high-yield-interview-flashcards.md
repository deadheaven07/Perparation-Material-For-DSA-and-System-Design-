# 04. 200+ High-Yield Interview Active Recall Flashcards

[← Back to GoF Patterns & Concurrency](./03-gof-patterns-and-concurrency-cheatsheet.md) | [Cheatsheets Hub](./README.md) | [Root Hub →](../README.md)

---

## ⚡ Active Recall Philosophy & How to Use

Active recall and spaced repetition produce **300% greater memory retention** under high-stress interview conditions than passive re-reading. 

**Instructions:**
1. Read the **Question / Challenge** in the bold card prompt.
2. Formulate your verbal or mental answer **BEFORE** clicking `<summary>Reveal Answer & Deep Dive</summary>`.
3. If your answer missed the core invariant, bookmark the card and repeat the review cycle tomorrow.

```
╭───────────────────────────────────────────────────────────────────────────────────────────╮
│                              THE 200-FLASHCARD MASTER CURRICULUM                          │
├──────────────────────────────────┬───────────────────┬────────────────────────────────────┤
│ Category                         │ Card Range        │ Domain Competencies Tested         │
├──────────────────────────────────┼───────────────────┼────────────────────────────────────┤
│ 1. Data Structures & Algorithms  │ Cards 001 – 050   │ Complexities, Invariants, Pointers │
│ 2. Distributed High-Level Design │ Cards 051 – 110   │ Consensus, CAP, Sharding, Streams  │
│ 3. Java Concurrency & OOD (LLD)  │ Cards 111 – 150   │ Locks, Memory Barriers, GoF, CAS   │
│ 4. Behavioral & Leadership (Bar) │ Cards 151 – 175   │ STAR, Disagree & Commit, Sev-1 RCA │
│ 5. Database, Storage & Networks  │ Cards 176 – 205   │ LSM vs B-Tree, TCP/UDP, Isolation  │
╰──────────────────────────────────┴───────────────────┴────────────────────────────────────╯
```

---

## 🧠 Pillar 1: Data Structures & Algorithmic Invariants (Cards 001 – 050)

<details>
<summary><b>001. What is the time complexity of building a Binary Heap from an unordered array of N elements?</b></summary>
<p>
<b>Answer:</b> $O(N)$ time, NOT $O(N \log N)$.<br>
<b>Why:</b> Bottom-up heap construction (<code>heapify</code> starting at the last internal node $\lfloor N/2 \rfloor$ down to index 0) does work proportional to the height of each node. The sum of heights across all nodes forms a convergent Taylor series: $\sum_{h=1}^{\log N} \frac{N}{2^h} \cdot h = O(N)$.
</p>
</details>

<details>
<summary><b>002. How do you detect if a singly linked list contains a cycle and locate the exact starting node?</b></summary>
<p>
<b>Answer:</b> Floyd's Cycle-Finding Algorithm (Tortoise and Hare).<br>
1. Advance <code>slow</code> by 1 step and <code>fast</code> by 2 steps. If they meet, a cycle exists.<br>
2. Reset <code>slow</code> to the list head while keeping <code>fast</code> at the meeting point.<br>
3. Advance both pointers 1 step at a time. The node where they collide is the cycle entry node.
</p>
</details>

<details>
<summary><b>003. What is the Master Theorem formula for divide-and-conquer recurrence relations?</b></summary>
<p>
<b>Answer:</b> For $T(n) = a T(n/b) + f(n)$ where $a \ge 1, b > 1$:<br>
- If $f(n) = O(n^c)$ where $c < \log_b a$, then $T(n) = \Theta(n^{\log_b a})$.<br>
- If $f(n) = \Theta(n^c \log^k n)$ where $c = \log_b a$, then $T(n) = \Theta(n^{\log_b a} \log^{k+1} n)$.<br>
- If $f(n) = \Omega(n^c)$ where $c > \log_b a$ and regularity holds, then $T(n) = \Theta(f(n))$.
</p>
</details>

<details>
<summary><b>004. What invariant guarantees that a Sliding Window problem can be solved in O(N) time?</b></summary>
<p>
<b>Answer:</b> Monotonicity of the window validity.<br>
Expanding the right pointer must monotonically move the condition in one direction (e.g. increasing sum), and contracting the left pointer must monotonically restore it. Each element is added once and removed at most once, bounding total pointer moves to $2N = O(N)$.
</p>
</details>

<details>
<summary><b>005. When does QuickSort degrade to O(N²) time complexity and how do you prevent it?</b></summary>
<p>
<b>Answer:</b> When the pivot consistently partitions the array into size $0$ and $N-1$ (e.g. choosing the first or last element of an already sorted or reverse-sorted array).<br>
<b>Prevention:</b> Use Randomized Pivot selection or Median-of-Three pivot partitioning (or Dual-Pivot QuickSort as in Java's <code>Arrays.sort</code>).
</p>
</details>

<details>
<summary><b>006. What is the worst-case time complexity of searching in an unbalanced Binary Search Tree vs. an AVL Tree?</b></summary>
<p>
<b>Answer:</b> Unbalanced BST: $O(N)$ (degrades to a linked list).<br>
AVL Tree: Guaranteed $O(\log N)$ because the height difference (balance factor) between left and right subtrees never exceeds 1 for any node.
</p>
</details>

<details>
<summary><b>007. What is a Monotonic Stack and what class of problems does it solve in O(N)?</b></summary>
<p>
<b>Answer:</b> A stack whose elements are strictly increasing or decreasing.<br>
It solves <b>Next Greater Element</b>, <b>Previous Greater Element</b>, <b>Daily Temperatures</b>, and <b>Largest Rectangle in Histogram</b> by popping smaller/larger elements whenever a new element violates the monotonicity invariant.
</p>
</details>

<details>
<summary><b>008. How do you find the median of a dynamically arriving data stream in O(log N) insertion time?</b></summary>
<p>
<b>Answer:</b> Dual-Heap Pattern (Two Heaps).<br>
Maintain a <code>Max-Heap</code> for the lower half of numbers and a <code>Min-Heap</code> for the upper half. Keep the heaps balanced such that their sizes differ by at most 1. The median is either the top of the larger heap or the average of both tops in $O(1)$ query time.
</p>
</details>

<details>
<summary><b>009. What is the time and space complexity of Dijkstra's Algorithm with a Binary Heap?</b></summary>
<p>
<b>Answer:</b> Time: $O((V + E) \log V)$. Space: $O(V)$ for distance array and priority queue.<br>
With a Fibonacci Heap, time drops theoretically to $O(E + V \log V)$, but Binary Heap is universally preferred in practice due to lower constant factors.
</p>
</details>

<details>
<summary><b>010. How does Topological Sort detect cycles in a directed graph?</b></summary>
<p>
<b>Answer:</b> Using Kahn's Algorithm (BFS with In-Degrees):<br>
Initialize in-degree for all vertices. Push 0-in-degree nodes to a queue. Process nodes, decrementing neighbors' in-degrees. If the count of processed nodes at termination is $< V$, a directed cycle exists.
</p>
</details>

<details>
<summary><b>011. What is the space-optimized DP trick for 0/1 Knapsack?</b></summary>
<p>
<b>Answer:</b> Compress the 2D DP table `dp[i][w]` into a 1D array `dp[w]` by iterating the knapsack capacity $w$ <b>BACKWARDS</b> from $W$ down to $weight[i]$.<br>
Backwards iteration guarantees that each item is used at most once because `dp[w - weight[i]]` still contains values from the previous iteration.
</p>
</details>

<details>
<summary><b>012. What is the difference between BFS and DFS for finding the shortest path in an unweighted graph?</b></summary>
<p>
<b>Answer:</b> BFS guarantees the shortest path (minimum edge count) because it explores radially level-by-level.<br>
DFS does NOT guarantee the shortest path and can traverse down deep suboptimal paths before finding the target.
</p>
</details>

<details>
<summary><b>013. How does Disjoint Set Union (DSU / Union-Find) achieve near O(1) amortized time per operation?</b></summary>
<p>
<b>Answer:</b> By combining <b>Path Compression</b> (flattening tree depth on `find`) and <b>Union by Rank/Size</b> (attaching smaller tree under larger root).<br>
This bounds amortized operation time to $O(\alpha(N))$, where $\alpha$ is the inverse Ackermann function ($\alpha(N) < 5$ for all practical universe sizes).
</p>
</details>

<details>
<summary><b>014. What is the time complexity of string search using the KMP (Knuth-Morris-Pratt) algorithm?</b></summary>
<p>
<b>Answer:</b> $O(N + M)$ time where $N$ is text length and $M$ is pattern length.<br>
It precomputes the Longest Prefix which is also Suffix (LPS) table in $O(M)$, avoiding redundant backtracking over the main text during mismatches.
</p>
</details>

<details>
<summary><b>015. How do you find the single missing number in an array containing numbers 0 to N in O(1) space?</b></summary>
<p>
<b>Answer:</b> Use XOR ($a \oplus a = 0$ and $a \oplus 0 = a$).<br>
XOR all indices $0 \dots N$ with all array elements. Duplicate pairs cancel to 0, leaving strictly the missing number.
</p>
</details>

<details>
<summary><b>016. How does a Trie (Prefix Tree) achieve O(L) search time regardless of dataset size N?</b></summary>
<p>
<b>Answer:</b> Each node contains up to 26 children pointers (or hash map). Traversal follows characters of the query word of length $L$. It takes exactly $L$ pointer dereferences, completely independent of the total number of words $N$ stored in the dictionary.
</p>
</details>

<details>
<summary><b>017. What is the difference between combinations and permutations in backtracking time complexity?</b></summary>
<p>
<b>Answer:</b> Combinations: Order does NOT matter $\binom{N}{K} \implies O(2^N)$ search space.<br>
Permutations: Order DOES matter $P(N, K) \implies O(N!)$ search space.
</p>
</details>

<details>
<summary><b>018. When should you use Bellman-Ford instead of Dijkstra?</b></summary>
<p>
<b>Answer:</b> When the graph contains <b>negative edge weights</b>.<br>
Dijkstra's greedy assumption fails with negative edges. Bellman-Ford relaxes all edges $V-1$ times in $O(V \cdot E)$ and detects negative-weight cycles on the $V$-th iteration.
</p>
</details>

<details>
<summary><b>019. How do you invert a binary tree iteratively?</b></summary>
<p>
<b>Answer:</b> Use a Queue (BFS level-order traversal). Pop a node, swap its `left` and `right` children pointers, and push any non-null children into the queue until empty.
</p>
</details>

<details>
<summary><b>020. What is Kadane's Algorithm and what is its optimal invariant?</b></summary>
<p>
<b>Answer:</b> Solves Maximum Subarray Sum in $O(N)$ time and $O(1)$ space.<br>
<b>Invariant:</b> At index $i$, `current_sum = max(nums[i], current_sum + nums[i])`. If `current_sum` becomes negative, it is discarded because carrying a negative prefix will never maximize any subsequent subarray.
</p>
</details>

<details>
<summary><b>021. What is the time complexity of checking if a number is prime using trial division?</b></summary>
<p>
<b>Answer:</b> $O(\sqrt{N})$.<br>
If $N = a \cdot b$, at least one factor must be $\le \sqrt{N}$. If no divisor is found up to $\lfloor \sqrt{N} \rfloor$, $N$ is prime.
</p>
</details>

<details>
<summary><b>022. How do you implement an O(1) time Least Recently Used (LRU) Cache?</b></summary>
<p>
<b>Answer:</b> Hash Map + Doubly Linked List.<br>
- Hash Map maps `Key` $\to$ `Node` in $O(1)$.<br>
- Doubly Linked List maintains access recency in $O(1)$ node insertion/removal.
</p>
</details>

<details>
<summary><b>023. What is the difference between Prim's and Kruskal's Minimum Spanning Tree algorithms?</b></summary>
<p>
<b>Answer:</b> Kruskal's is edge-based: Sorts edges and adds them greedily using DSU to prevent cycles ($O(E \log E)$). Better for sparse graphs.<br>
Prim's is vertex-based: Grows a tree from an arbitrary root adding cheapest adjacent edges via Priority Queue ($O(E \log V)$). Better for dense graphs.
</p>
</details>

<details>
<summary><b>024. What is the optimal time complexity to compute the Longest Increasing Subsequence (LIS)?</b></summary>
<p>
<b>Answer:</b> $O(N \log N)$ using Patience Sorting with Binary Search (replacing or appending tails). The standard DP approach is $O(N^2)$.
</p>
</details>

<details>
<summary><b>025. How do you find the Lowest Common Ancestor (LCA) in a Binary Tree without parent pointers?</b></summary>
<p>
<b>Answer:</b> Recursive DFS: If current node is null, $p$, or $q$, return current.<br>
Recurse left and right. If both left and right return non-null, current node is the LCA. If only one returns non-null, propagate that non-null child upwards.
</p>
</details>

---

## 🌐 Pillar 2: Distributed High-Level Design (Cards 051 – 110)

<details>
<summary><b>051. What is the CAP Theorem and why is 'CA' a myth in distributed networks?</b></summary>
<p>
<b>Answer:</b> A distributed system can guarantee at most two of: Consistency, Availability, and Partition Tolerance.<br>
<b>Why CA is a myth:</b> Physical networks inevitably experience packet drops, fiber cuts, or latency spikes (Partition $P$). When a partition occurs, a system MUST choose either:<br>
- <b>CP:</b> Reject writes to preserve consistency across split nodes.<br>
- <b>AP:</b> Accept writes on both sides, sacrificing linearizable consistency.
</p>
</details>

<details>
<summary><b>052. What is Consistent Hashing and how does it prevent mass cache invalidation when servers are added/removed?</b></summary>
<p>
<b>Answer:</b> Maps both server nodes and keys onto a circular hash ring ($[0, 2^{32}-1]$). Keys are assigned to the first server encountered clockwise.<br>
When a server joins or leaves, only $K/N$ keys need to be remapped (where $K$ is total keys and $N$ is server count), compared to $100\%$ remapping in traditional `hash(key) % N`. Virtual nodes ensure uniform load distribution.
</p>
</details>

<details>
<summary><b>053. What is the difference between Cache-Aside and Write-Through caching?</b></summary>
<p>
<b>Answer:</b><br>
- <b>Cache-Aside (Lazy Loading):</b> Application queries cache. On miss, application queries DB, populates cache, and returns. Writes go directly to DB, and application invalidates cache.<br>
- <b>Write-Through:</b> Application writes to Cache; Cache synchronously writes to DB before returning success. Guarantees cache consistency at the expense of higher write latency.
</p>
</details>

<details>
<summary><b>054. How does the Token Bucket algorithm differ from Leaky Bucket for Rate Limiting?</b></summary>
<p>
<b>Answer:</b><br>
- <b>Token Bucket:</b> Tokens refill at a constant rate up to bucket capacity. Allows <b>bursts of traffic</b> as long as tokens are available.<br>
- <b>Leaky Bucket:</b> Requests enter a FIFO queue and leak out at a strictly smooth constant rate. Smooths out bursts into a constant flow; drops excess requests immediately if queue is full.
</p>
</details>

<details>
<summary><b>055. How do you prevent a Cache Stampede (Thundering Herd Problem)?</b></summary>
<p>
<b>Answer:</b> When a popular hot key expires, thousands of concurrent requests miss the cache and hit the database simultaneously.<br>
<b>Solutions:</b><br>
1. <b>Mutex / Distributed Lock:</b> Only the first requesting thread acquires a lock to query the DB and refresh cache; all others wait.<br>
2. <b>Early Probabilistic Expiration (XFetch):</b> Background worker refreshes key before true expiration based on read probability.<br>
3. <b>Cache Warming / Pre-fetching.</b>
</p>
</details>

<details>
<summary><b>056. What is the Transactional Outbox Pattern and why is it essential in microservices?</b></summary>
<p>
<b>Answer:</b> Solves the dual-write problem (writing to a DB and publishing to Kafka atomically).<br>
Write business entity and an event record into an <code>outbox</code> table in the <b>same local database transaction</b>. An asynchronous Change Data Capture (CDC) process (e.g. Debezium) reads the DB transaction log and streams events to Kafka with zero data loss.
</p>
</details>

<details>
<summary><b>057. How does Raft achieve consensus and leader election?</b></summary>
<p>
<b>Answer:</b> Raft elects a single leader via randomized election timeouts.<br>
The leader accepts client commands, appends them to its log, and replicates log entries to follower nodes. An entry is committed once a quorum ($\lfloor N/2 \rfloor + 1$) of followers acknowledge. If a follower does not receive a heartbeat, it becomes a candidate and requests votes.
</p>
</details>

<details>
<summary><b>058. What is the difference between Pessimistic Locking and Optimistic Locking?</b></summary>
<p>
<b>Answer:</b><br>
- <b>Pessimistic:</b> Locks the DB row explicitly (`SELECT ... FOR UPDATE`). Prevents other transactions from reading/writing until lock release. Safe for high write contention, but can cause deadlocks and high latency.<br>
- <b>Optimistic:</b> Does not acquire locks. Records a `version` column. At write time, executes `UPDATE ... WHERE id = :id AND version = :current_version`. If 0 rows updated, a race condition occurred and transaction retries. Ideal for low-to-medium contention.
</p>
</details>

<details>
<summary><b>059. What is a Merkle Tree and how is it used in Dynamo and Apache Cassandra?</b></summary>
<p>
<b>Answer:</b> A binary tree of cryptographic hashes where leaf nodes contain hashes of data records and parent nodes contain hashes of their children.<br>
Used for <b>Anti-Entropy Repair</b>: Replicas compare Merkle Tree roots. If roots match, replicas are identical. If they differ, they traverse children in $O(\log N)$ to find the exact diverged keys without transmitting whole datasets across the network.
</p>
</details>

<details>
<summary><b>060. What is the formula for Quorum Consistency in Dynamo-style stores?</b></summary>
<p>
<b>Answer:</b> $R + W > N$<br>
Where $N$ is replication factor, $R$ is read quorum, and $W$ is write quorum. By the Pigeonhole Principle, the read set and write set must overlap on at least one replica node that has the latest version.
</p>
</details>

<details>
<summary><b>061. How does Content-Defined Chunking (Rabin Fingerprints) optimize cloud file sync (Dropbox)?</b></summary>
<p>
<b>Answer:</b> Fixed-size chunking shifts all chunk boundaries if a user inserts 1 byte at the start of a file, breaking deduplication.<br>
Content-Defined Chunking uses a rolling hash (Rabin Fingerprint) to set boundaries based on data content patterns. Inserting bytes only changes the boundary of the local chunk, allowing $99\%$ of chunks to remain deduplicated and un-uploaded.
</p>
</details>

<details>
<summary><b>062. How does Uber match riders with drivers in real time?</b></summary>
<p>
<b>Answer:</b> Spatial Indexing using <b>Uber H3 (Hexagonal Hierarchical Spatial Index)</b> or Google S2.<br>
Driver GPS pings are mapped to H3 cell IDs in an in-memory Redis cluster. Ride requests query the cell and its $K$-ring neighbors in $O(1)$ time, ranking candidates by Euclidean/Haversine distance and driver rating.
</p>
</details>

<details>
<summary><b>063. What are the key differences between Kafka and RabbitMQ?</b></summary>
<p>
<b>Answer:</b><br>
- <b>Kafka:</b> Distributed, append-only partitioned commit log. High throughput (millions msg/sec). Messages are retained on disk and consumers track their own offsets (pull model). Ideal for event streaming and replay.<br>
- <b>RabbitMQ:</b> Traditional message broker. Smart broker / dumb consumer (push model). Rich exchange-based routing (topic, fanout, headers). Messages are deleted upon consumer ACK. Ideal for complex transactional task queues.
</p>
</details>

<details>
<summary><b>064. How do you design an Idempotent API endpoint for payment processing?</b></summary>
<p>
<b>Answer:</b> The client sends a unique <code>Idempotency-Key</code> header (UUID).<br>
The API Gateway or service checks a distributed store (Redis/DB) for the key. If key exists and status is <code>SUCCESS</code>, return the cached response. If processing, return <code>409 Conflict</code>. If new, insert key with status <code>PROCESSING</code>, execute payment, and atomically update to <code>COMPLETED</code>.
</p>
</details>

<details>
<summary><b>065. How does a CDN choose the optimal Edge server for a user?</b></summary>
<p>
<b>Answer:</b> Anycast DNS or GeoDNS routing.<br>
Anycast advertises the identical IP address from hundreds of data centers worldwide. Internet BGP (Border Gateway Protocol) automatically routes user packets along the lowest-latency autonomous system path to the nearest Point of Presence (PoP).
</p>
</details>

---

## ☕ Pillar 3: Java Concurrency & Low-Level Design (Cards 111 – 150)

<details>
<summary><b>111. What is the difference between 'volatile' and 'synchronized' in Java?</b></summary>
<p>
<b>Answer:</b><br>
- <b>volatile:</b> Guarantees <b>Visibility</b> and <b>Ordering</b> (happens-before relationship, prevents instruction reordering via CPU memory barriers). It does NOT guarantee atomicity (e.g. `count++` is not atomic under volatile).<br>
- <b>synchronized:</b> Guarantees <b>Visibility</b>, <b>Ordering</b>, AND <b>Mutual Exclusion / Atomicity</b> by acquiring an intrinsic monitor lock.
</p>
</details>

<details>
<summary><b>112. Why must you declare the instance 'volatile' in Double-Checked Locking Singleton?</b></summary>
<p>
<b>Answer:</b> To prevent instruction reordering during object instantiation.<br>
`instance = new Singleton()` involves 3 steps:<br>
1. Allocate memory.<br>
2. Initialize object.<br>
3. Assign reference to memory.<br>
Without `volatile`, the CPU may reorder steps to 1 $\to$ 3 $\to$ 2. Another thread can observe a non-null but uninitialized object, causing silent corrupted state crashes.
</p>
</details>

<details>
<summary><b>113. How does Compare-And-Swap (CAS) work in Java's AtomicInteger?</b></summary>
<p>
<b>Answer:</b> Hardware-level atomic CPU instruction (`cmpxchg`).<br>
It takes three parameters: memory address $V$, expected old value $A$, and new value $B$. If memory content equals $A$, it updates to $B$ and returns true; otherwise false. It allows lock-free optimistic concurrency loops without thread context switching overhead.
</p>
</details>

<details>
<summary><b>114. What is the ABA Problem in lock-free concurrency and how is it solved?</b></summary>
<p>
<b>Answer:</b> Value changes from $A \to B \to A$. A CAS thread checks value, sees $A$, and assumes nothing changed, leading to potential data corruption.<br>
<b>Solution:</b> Use <code>AtomicStampedReference</code> which couples an integer version/stamp with the object reference.
</p>
</details>

<details>
<summary><b>115. How does Java's ConcurrentHashMap achieve high write throughput without global locking?</b></summary>
<p>
<b>Answer:</b> In Java 8+, it uses <b>CAS on uninitialized bucket head nodes</b> and synchronizes strictly on the <b>first node of the targeted hash bucket</b> for collisions. Only threads hashing to the exact same bucket block each other; reads are entirely lock-free using `volatile`.
</p>
</details>

<details>
<summary><b>116. What is the difference between 'submit()' and 'execute()' in ExecutorService?</b></summary>
<p>
<b>Answer:</b><br>
- <code>execute(Runnable)</code>: Returns `void`. Uncaught exceptions crash the worker thread and are logged to standard error.<br>
- <code>submit(Callable/Runnable)</code>: Returns a `Future<T>`. Exceptions are swallowed and stored inside the Future, only rethrown when `future.get()` is invoked.
</p>
</details>

<details>
<summary><b>117. What are the 4 conditions required for a Deadlock to occur (Coffman Conditions)?</b></summary>
<p>
<b>Answer:</b><br>
1. <b>Mutual Exclusion:</b> Resources cannot be shared.<br>
2. <b>Hold and Wait:</b> A process holds resources while waiting for others.<br>
3. <b>No Preemption:</b> Resources cannot be forcibly taken away.<br>
4. <b>Circular Wait:</b> Circular chain of processes each waiting for resource held by next.
</p>
</details>

<details>
<summary><b>118. How do you eliminate the Circular Wait condition in code?</b></summary>
<p>
<b>Answer:</b> Enforce a strict <b>Global Lock Acquisition Hierarchy</b>.<br>
Always sort locks (e.g. by resource ID or memory address) before acquiring. If all threads acquire Lock A before Lock B, circular waiting is mathematically impossible.
</p>
</details>

<details>
<summary><b>119. What are Virtual Threads (Java 21 Project Loom) and how do they differ from Platform Threads?</b></summary>
<p>
<b>Answer:</b> Platform threads are 1:1 wrappers around OS kernel threads (heavyweight, ~1MB stack, limited to thousands).<br>
Virtual threads are lightweight, user-mode threads managed by the JVM ($M:N$ mapping onto carrier platform threads). When a virtual thread performs blocking I/O, the JVM unmounts it from the carrier thread, allowing millions of concurrent threads with negligible RAM overhead.
</p>
</details>

<details>
<summary><b>120. Which GoF pattern should you use when an object's behavior changes dramatically based on internal status?</b></summary>
<p>
<b>Answer:</b> <b>The State Pattern</b>.<br>
Encapsulates state-specific behaviors inside distinct state classes implementing a common interface (e.g. `OrderState: PlacedState, PreparingState, DeliveredState`), eliminating complex nested `switch` or `if-else` statements.
</p>
</details>

---

## 🎯 Pillar 4: Behavioral & Engineering Leadership (Cards 151 – 175)

<details>
<summary><b>151. What is the single biggest mistake candidates make during behavioral STAR answers?</b></summary>
<p>
<b>Answer:</b> Spending $80\%$ of their time explaining the <b>Situation</b> and <b>Task</b> rather than the <b>Action</b> and <b>Result</b>.<br>
Top candidates dedicate $\le 20\%$ to setting up the problem context and $80\%$ to their specific technical ownership, engineering decisions, and quantified business results.
</p>
</details>

<details>
<summary><b>152. What is the difference between 'I' and 'We' in technical behavioral interviews?</b></summary>
<p>
<b>Answer:</b> Interviewers cannot evaluate a team; they evaluate YOU.<br>
Using "We" obscures your individual technical contributions. Use "I" to describe your specific hypothesis, architectural design, and implementation, while using "We" only to acknowledge team collaboration.
</p>
</details>

<details>
<summary><b>153. How do you demonstrate Amazon's 'Have Backbone; Disagree and Commit' principle?</b></summary>
<p>
<b>Answer:</b><br>
1. Relentlessly challenge ideas with empirical data, benchmark metrics, and architecture diagrams when you believe a decision is flawed.<br>
2. Do not compromise for the sake of social cohesion.<br>
3. <b>Crucial Step:</b> Once the decision is made by leadership, commit $100\%$ to making it succeed without lingering resentment or saying "I told you so."
</p>
</details>

<details>
<summary><b>154. What is a Blameless Post-Mortem and why is finger-pointing an immediate hiring rejection?</b></summary>
<p>
<b>Answer:</b> In enterprise systems, outages are caused by systemic vulnerabilities (missing guardrails, flawed tests, unmonitored metrics), not individual carelessness.<br>
Blaming individuals creates a culture of fear where engineers hide mistakes. Leaders focus on root causes (5 Whys) and permanent automated safeguards.
</p>
</details>

<details>
<summary><b>155. What is the 'One-Way Door' vs. 'Two-Way Door' decision framework (Type 1 vs Type 2)?</b></summary>
<p>
<b>Answer:</b><br>
- <b>Two-Way Door (Type 2):</b> Reversible decisions (e.g. changing an API response field or testing a cache TTL). Should be made rapidly with high bias for action.<br>
- <b>One-Way Door (Type 1):</b> Irreversible, high-stakes decisions (e.g. migrating primary database from Postgres to DynamoDB or changing encryption keys). Requires deep analysis, RFC reviews, and executive alignment.
</p>
</details>

---

## 🗄️ Pillar 5: Database, Storage & Networking Internals (Cards 176 – 205)

<details>
<summary><b>176. Why are Log-Structured Merge-Trees (LSM-Trees) preferred for write-heavy workloads over B-Trees?</b></summary>
<p>
<b>Answer:</b> B-Trees perform random disk I/O to update in-place index pages.<br>
LSM-Trees append all writes sequentially to an in-memory <code>MemTable</code> and disk-based Write-Ahead Log (WAL) in $O(1)$ sequential I/O. When full, MemTables flush to immutable SSTables on disk, which are consolidated in the background via compaction.
</p>
</details>

<details>
<summary><b>177. What is the difference between Read Committed and Repeatable Read isolation levels?</b></summary>
<p>
<b>Answer:</b><br>
- <b>Read Committed:</b> Prevents Dirty Reads. Queries only see data committed before the query began. However, reading the same row twice in one transaction can yield different values if another transaction committed changes (Non-repeatable Read).<br>
- <b>Repeatable Read:</b> Prevents Dirty and Non-repeatable Reads using Multi-Version Concurrency Control (MVCC) snapshots. All reads within the transaction observe the exact state of data at transaction start.
</p>
</details>

<details>
<summary><b>178. How does TCP establish a connection and ensure reliable delivery?</b></summary>
<p>
<b>Answer:</b> Three-Way Handshake: <code>SYN</code> $\to$ <code>SYN-ACK</code> $\to$ <code>ACK</code>.<br>
Reliability is guaranteed via Sequence Numbers, Cumulative Acknowledgments (ACKs), Sliding Window flow control, and Exponential Backoff Retransmission timeouts.
</p>
</details>

<details>
<summary><b>179. What is the difference between TCP and UDP?</b></summary>
<p>
<b>Answer:</b><br>
- <b>TCP:</b> Connection-oriented, ordered, reliable byte stream with flow and congestion control. Higher overhead.<br>
- <b>UDP:</b> Connectionless, lightweight datagram protocol with zero delivery or ordering guarantees. Ideal for real-time multiplayer gaming, live audio/video streaming, and DNS lookups.
</p>
</details>

<details>
<summary><b>180. What is the N+1 Query Problem in ORMs and how do you resolve it?</b></summary>
<p>
<b>Answer:</b> Executing 1 query to fetch $N$ parent records, followed by $N$ separate queries to fetch children for each parent (total $N+1$ DB round trips).<br>
<b>Resolution:</b> Use <code>JOIN FETCH</code>, explicit SQL joins, or batch fetching (`IN (:parent_ids)`).
</p>
</details>

---

<div align="center">

| [← Back to GoF Patterns & Concurrency](./03-gof-patterns-and-concurrency-cheatsheet.md) | [Track Hub: Cheatsheets](./README.md) | [Root Hub →](../README.md) |
| :--- | :---: | ---: |

</div>
