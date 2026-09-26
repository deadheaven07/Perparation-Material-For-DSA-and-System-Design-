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
<details>
<summary><b>026. What is Binary Search on Answer Space and what invariant must the search space satisfy?</b></summary>
<p>
<b>Answer:</b> Searching for the optimal value within a numerical range $[low, high]$ rather than searching indices in an array.<br>
<b>Invariant:</b> The feasibility predicate $P(x)$ must be <b>monotonic</b>: there exists a threshold $T$ such that $P(x)$ is false for all $x < T$ and true for all $x \ge T$ (or vice-versa).
</p>
</details>

<details>
<summary><b>027. How does Kadane's Algorithm achieve O(N) time and O(1) space for Maximum Subarray?</b></summary>
<p>
<b>Answer:</b> Dynamic Programming with state compression.<br>
<b>Recurrence:</b> At each index $i$, the maximum subarray ending at $i$ is $currentMax = \max(nums[i], currentMax + nums[i])$. If the running sum drops below the current element itself, Kadane's resets the subarray start to index $i$.
</p>
</details>

<details>
<summary><b>028. How does the Dutch National Flag algorithm sort an array of 0s, 1s, and 2s in a single pass?</b></summary>
<p>
<b>Answer:</b> Three-pointer partitioning (<code>low</code>, <code>mid</code>, <code>high</code>).<br>
- If <code>nums[mid] == 0</code>: swap with <code>nums[low]</code>, increment both <code>low</code> and <code>mid</code>.<br>
- If <code>nums[mid] == 1</code>: increment <code>mid</code>.<br>
- If <code>nums[mid] == 2</code>: swap with <code>nums[high]</code>, decrement <code>high</code> (do NOT advance <code>mid</code> because swapped element is uninspected).
</p>
</details>

<details>
<summary><b>029. How does Morris In-Order Traversal achieve O(N) time and O(1) auxiliary space without recursion or a stack?</b></summary>
<p>
<b>Answer:</b> Threaded Binary Tree technique.<br>
For the current node, find its in-order predecessor (rightmost node in its left subtree). If the predecessor's right child is null, create a temporary bridge pointing to current node and move left. If the bridge already exists, cut it, visit the current node, and move right.
</p>
</details>

<details>
<summary><b>030. How does a Segment Tree with Lazy Propagation execute both range updates and range queries in O(log N)?</b></summary>
<p>
<b>Answer:</b> Deferred updates via a secondary <code>lazy[]</code> tree array.<br>
When a range update fully covers a segment tree node, update that node's value and store the pending delta in its lazy array without traversing deeper. When future queries or updates descend through that node, "push down" the pending lazy delta to children in $O(1)$ time.
</p>
</details>

<details>
<summary><b>031. How does a Fenwick Tree (Binary Indexed Tree / BIT) isolate the least significant set bit (LSB)?</b></summary>
<p>
<b>Answer:</b> Bitwise two's complement negation: <code>i & (-i)</code>.<br>
In two's complement, $-i = \sim i + 1$. AND-ing $i$ with $-i$ clears all higher bits and preserves strictly the lowest set bit. Adding <code>i & (-i)</code> advances to the parent responsible for covering index $i$; subtracting <code>i & (-i)</code> traverses to prefix sum ranges.
</p>
</details>

<details>
<summary><b>032. What is the time complexity of Quickselect for finding the k-th smallest element?</b></summary>
<p>
<b>Answer:</b> Average: $O(N)$ time. Worst-case: $O(N^2)$ (mitigated to $O(N)$ with randomized pivot).<br>
Unlike QuickSort which recurses into both left and right subarrays ($2 \times N/2$), Quickselect discards the half that cannot contain index $k$, leading to the geometric sum $N + N/2 + N/4 + \dots = 2N = O(N)$.
</p>
</details>

<details>
<summary><b>033. How does Bellman-Ford detect negative weight cycles in a directed graph?</b></summary>
<p>
<b>Answer:</b> Relax all $E$ edges $V-1$ times.<br>
In a graph with $V$ vertices, any simple shortest path has at most $V-1$ edges. If a subsequent $V$-th relaxation pass successfully decreases the distance to any vertex, a negative weight cycle is guaranteed to exist.
</p>
</details>

<details>
<summary><b>034. What is the state recurrence and time complexity of the Floyd-Warshall All-Pairs Shortest Path algorithm?</b></summary>
<p>
<b>Answer:</b> Time: $O(V^3)$. Space: $O(V^2)$.<br>
<b>Recurrence:</b> $dp[k][i][j] = \min(dp[k-1][i][j], dp[k-1][i][k] + dp[k-1][k][j])$, where $k$ is the intermediate vertex allowed on the path from $i$ to $j$. The $k$ loop must strictly be the outermost loop.
</p>
</details>

<details>
<summary><b>035. How does Tarjan's Algorithm find Strongly Connected Components (SCCs) in a single DFS pass?</b></summary>
<p>
<b>Answer:</b> By tracking discovery time <code>tin[u]</code> and lowest reachable ancestor <code>low[u]</code>.<br>
Nodes are pushed onto a DFS stack. When traversing back-edges to ancestors on the stack, update <code>low[u] = min(low[u], tin[v])</code>. If <code>low[u] == tin[u]</code> upon backtracking, $u$ is the root of an SCC; pop all nodes from the stack until $u$ is popped.
</p>
</details>

<details>
<summary><b>036. When should you use Prim's Algorithm vs. Kruskal's Algorithm for Minimum Spanning Trees (MST)?</b></summary>
<p>
<b>Answer:</b><br>
- <b>Prim's Algorithm ($O(E + V \log V)$):</b> Best for <b>dense graphs</b> ($E \approx V^2$) using an adjacency matrix or indexed priority queue.<br>
- <b>Kruskal's Algorithm ($O(E \log E)$):</b> Best for <b>sparse graphs</b> ($E \ll V^2$) because sorting edges and applying Disjoint Set Union (DSU) has minimal overhead.
</p>
</details>

<details>
<summary><b>037. What is 0-1 BFS and why is it faster than Dijkstra's algorithm for binary-weighted graphs?</b></summary>
<p>
<b>Answer:</b> A BFS variant using a double-ended queue (<code>ArrayDeque</code>) running in strictly linear $O(V + E)$ time.<br>
For edges of weight 0, push the neighbor to the <b>front</b> of the deque; for edges of weight 1, push to the <b>back</b>. This maintains monotonicity of distances in the queue without the $O(\log V)$ priority queue heap overhead.
</p>
</details>

<details>
<summary><b>038. How does a Monotonic Deque find the maximum element in all sliding windows of size K in O(N) total time?</b></summary>
<p>
<b>Answer:</b> Maintain a deque storing indices in decreasing order of their corresponding values.<br>
1. Before adding index $i$, pop elements from the back whose values are $\le nums[i]$.<br>
2. Pop elements from the front whose indices fall outside the window ($< i - k + 1$).<br>
3. The front of the deque is always the maximum element for the current window.
</p>
</details>

<details>
<summary><b>039. How do you iterate over all submasks of a bitmask in O(3^N) total time instead of O(4^N)?</b></summary>
<p>
<b>Answer:</b> Use the bitwise submask decrementation trick: <code>sub = (sub - 1) & mask</code>.<br>
Looping <code>for (int sub = mask; sub > 0; sub = (sub - 1) & mask)</code> visits only valid submasks. Across all $N$ elements, the number of mask-submask pairs is $\sum_{k=0}^N \binom{N}{k} 2^k = (1 + 2)^N = 3^N$.
</p>
</details>

<details>
<summary><b>040. How does Manacher's Algorithm find the longest palindromic substring in strictly O(N) time?</b></summary>
<p>
<b>Answer:</b> Mirrored palindrome radii optimization.<br>
By inserting boundary sentinel characters (e.g. <code>#a#b#a#</code>), all palindromes have odd length. Manacher maintains the current rightmost palindrome boundary $[L, R]$ and center $C$. For a new position $i$, it initializes the radius to $\min(R - i, radius[2C - i])$ using symmetry before expanding.
</p>
</details>

<details>
<summary><b>041. What is the Aho-Corasick Automaton and what is its query time complexity?</b></summary>
<p>
<b>Answer:</b> A Trie-based finite state machine with failure links (similar to KMP) for multi-pattern dictionary matching.<br>
It matches an arbitrary dictionary of words against a text of length $N$ in $O(N + M + Z)$ time, where $M$ is total pattern length and $Z$ is the total count of matches.
</p>
</details>

<details>
<summary><b>042. What invariant does the Z-Algorithm maintain to compute pattern matches in O(N)?</b></summary>
<p>
<b>Answer:</b> The Z-box $[L, R]$ representing the rightmost segment matching a prefix of the string.<br>
For index $i$, if $i \le R$, the algorithm initializes $Z[i] \ge \min(R - i + 1, Z[i - L])$, expanding character by character only beyond the current boundary $R$.
</p>
</details>

<details>
<summary><b>043. What is the Median-of-Medians algorithm and why is it theoretically significant?</b></summary>
<p>
<b>Answer:</b> A deterministic pivot selection algorithm for selection/QuickSort that guarantees worst-case $O(N)$ time.<br>
It divides $N$ elements into groups of 5, finds their medians, and recursively finds the median of those medians. This guarantees the pivot is at least greater than $30\%$ and less than $30\%$ of elements.
</p>
</details>

<details>
<summary><b>044. How does 3-Color DFS detect cycles in a directed graph?</b></summary>
<p>
<b>Answer:</b> Assign states: <b>White (0)</b> = Unvisited, <b>Gray (1)</b> = In current recursion stack, <b>Black (2)</b> = Fully processed.<br>
If DFS encounters an edge pointing to a <b>Gray</b> vertex, a back-edge exists, proving the graph contains a directed cycle.
</p>
</details>

<details>
<summary><b>045. What problem does Heavy-Light Decomposition (HLD) solve on trees?</b></summary>
<p>
<b>Answer:</b> Enables path queries and updates (e.g. max edge weight or sum between any two nodes $u, v$) in $O(\log^2 N)$ time.<br>
It partitions tree edges into "Heavy" (child with largest subtree) and "Light" edges. Any path from root to leaf crosses at most $\log N$ light edges, flattening tree paths into contiguous ranges in a Segment Tree.
</p>
</details>

<details>
<summary><b>046. How does Binary Exponentiation compute (x^n) % M in O(log n) time?</b></summary>
<p>
<b>Answer:</b> Repeated squaring via bitwise inspection of the exponent $n$.<br>
If $n$ is odd, multiply result by current $x$ ($res = (res \times x) \% M$). Then square $x$ ($x = (x \times x) \% M$) and shift $n$ right by 1 bit ($n \gg= 1$).
</p>
</details>

<details>
<summary><b>047. How do you compute the modular multiplicative inverse of A modulo prime P in O(log P)?</b></summary>
<p>
<b>Answer:</b> Fermat's Little Theorem: $A^{-1} \equiv A^{P-2} \pmod P$ (when $P$ is prime and $\gcd(A, P) = 1$).<br>
Compute $A^{P-2} \pmod P$ using binary exponentiation in $O(\log P)$ time. If $P$ is composite, use the Extended Euclidean Algorithm.
</p>
</details>

<details>
<summary><b>048. How does Reservoir Sampling uniformly select K items from an infinite stream of unknown size N?</b></summary>
<p>
<b>Answer:</b> Store the first $K$ items in the reservoir.<br>
For each subsequent $i$-th item ($i > K$), pick a random integer $r \in [0, i-1]$. If $r < K$, replace <code>reservoir[r]</code> with the new item. The probability of any element being retained at step $N$ is strictly $K/N$.
</p>
</details>

<details>
<summary><b>049. How does the Fisher-Yates (Knuth) Shuffle produce an unbiased uniform permutation in O(N)?</b></summary>
<p>
<b>Answer:</b> Iterate from index $i = N-1$ down to 1.<br>
At each step, select a random index $j \in [0, i]$ and swap <code>array[i]</code> with <code>array[j]</code>. Each of the $N!$ permutations is produced with exact uniform probability $1/N!$.
</p>
</details>

<details>
<summary><b>050. What is the formula for the n-th Catalan Number and what interview structures does it count?</b></summary>
<p>
<b>Answer:</b> Formula: $C_n = \frac{1}{n+1} \binom{2n}{n} = \frac{(2n)!}{(n+1)! n!}$.<br>
Counts: Number of structurally unique BSTs with $n$ keys, valid parenthesizations of $n$ pairs, non-intersecting chords of $2n$ points on a circle, and Dyck paths.
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
<details>
<summary><b>066. How does the Raft consensus algorithm handle leader election and avoid split votes?</b></summary>
<p>
<b>Answer:</b> Randomized Election Timeouts (e.g. 150ms – 300ms).<br>
When a follower's election timer expires without receiving heartbeats, it transitions to Candidate, increments term, votes for itself, and broadcasts RequestVote. Randomization ensures one candidate usually times out first and collects a majority quorum before competitors time out.
</p>
</details>

<details>
<summary><b>067. What is the fundamental difference between Paxos and Raft?</b></summary>
<p>
<b>Answer:</b> Understandability and decomposition.<br>
Paxos decomposes consensus into independent single-decree synods. Raft structures consensus into explicit, sequential subproblems: strong Leader Election, Log Replication (leader-append only), and Log Matching Safety, making it far easier to implement correctly in production.
</p>
</details>

<details>
<summary><b>068. How do Virtual Nodes (vnodes) prevent hotspotting in Consistent Hashing rings?</b></summary>
<p>
<b>Answer:</b> Map each physical server to $K$ virtual tokens (e.g. $K=256$) scattered uniformly across the hash circle.<br>
This ensures uniform distribution of keys regardless of small cluster sizes, and distributes the load of a failed server proportionally across all remaining servers rather than overloading a single successor.
</p>
</details>

<details>
<summary><b>069. What is a Bloom Filter and what are its performance characteristics?</b></summary>
<p>
<b>Answer:</b> A space-efficient probabilistic data structure that tests set membership in $O(k)$ time using $k$ hash functions and a bit array of size $m$.<br>
- <b>False Negatives:</b> 0% (If it returns "not in set", the item is definitively not in set).<br>
- <b>False Positives:</b> Possible (bounded by $p \approx (1 - e^{-kn/m})^k$). Used to skip expensive disk/database lookups.
</p>
</details>

<details>
<summary><b>070. What problem does a Count-Min Sketch solve in stream processing?</b></summary>
<p>
<b>Answer:</b> Sublinear space frequency estimation for high-velocity streaming data.<br>
Uses a 2D array of counters $d \times w$ with $d$ hash functions. When an event arrives, increment the $d$ hashed buckets. To query frequency, return the minimum value among the $d$ buckets, guaranteeing an upper bound with bounded overestimation error.
</p>
</details>

<details>
<summary><b>071. How does HyperLogLog (HLL) estimate cardinality of billions of items in 12 KB memory?</b></summary>
<p>
<b>Answer:</b> By tracking the maximum number of leading zeros in the binary hashes of elements across $m$ registers.<br>
The probability of observing $k$ consecutive leading zeros is $2^{-(k+1)}$. HLL uses harmonic mean aggregation across registers to eliminate variance, achieving a standard error of $1.04 / \sqrt{m}$.
</p>
</details>

<details>
<summary><b>072. How does the Gossip Protocol maintain cluster membership and anti-entropy?</b></summary>
<p>
<b>Answer:</b> Epidemic, decentralized information dissemination.<br>
Every $T$ milliseconds, each node randomly picks $k$ peers and exchanges heartbeat/generation state. Convergence across $N$ nodes is achieved in $O(\log N)$ rounds with zero single points of failure.
</p>
</details>

<details>
<summary><b>073. Why are Vector Clocks used over Lamport Timestamps in distributed stores like Dynamo?</b></summary>
<p>
<b>Answer:</b> To detect concurrent, causally independent write conflicts.<br>
A Lamport timestamp provides a total order but cannot distinguish whether event $A$ happened before event $B$ or if they occurred concurrently. A Vector Clock tracks per-node logical clocks $[V_1, V_2, \dots, V_N]$; if neither clock dominates the other, a branch conflict exists requiring application reconciliation.
</p>
</details>

<details>
<summary><b>074. What condition must Quorum Consensus satisfy to guarantee Strong Consistency?</b></summary>
<p>
<b>Answer:</b> $R + W > N$, where $N$ is replication factor, $R$ is read quorum, and $W$ is write quorum.<br>
By the Pigeonhole Principle, the read set and write set must overlap by at least one node, ensuring the reader always observes the most up-to-date version.
</p>
</details>

<details>
<summary><b>075. What is a Fencing Token and how does it prevent the Split-Brain Zombie Leader problem?</b></summary>
<p>
<b>Answer:</b> A monotonically increasing transaction epoch/token issued by a consensus coordinator (e.g. ZooKeeper/Raft).<br>
When a partitioned leader recovers and attempts to write to shared storage, storage rejects writes bearing an older fencing token than the currently registered epoch.
</p>
</details>

<details>
<summary><b>076. What is the PACELC theorem and how does it extend the CAP theorem?</b></summary>
<p>
<b>Answer:</b> PACELC states: If there is a <b>Partition (P)</b>, trade off <b>Availability (A)</b> vs <b>Consistency (C)</b>; <b>Else (E)</b>, trade off <b>Latency (L)</b> vs <b>Consistency (C)</b>.<br>
Explains normal operating behavior (e.g. MongoDB is PC/EC, DynamoDB is PA/EL).
</p>
</details>

<details>
<summary><b>077. What is Write-Ahead Logging (WAL) and why is it fundamental to database durability?</b></summary>
<p>
<b>Answer:</b> The rule that modification logs must be flushed to persistent disk storage (<code>fsync</code>) before dirty data pages are written to the database files.<br>
Ensures crash recovery via redo/undo logs following the ARIES protocol.
</p>
</details>

<details>
<summary><b>078. How does Change Data Capture (CDC) with Debezium eliminate the Dual-Write hazard?</b></summary>
<p>
<b>Answer:</b> Instead of the application writing to both Database and Kafka (risking partial failure), the app writes exclusively to the database.<br>
Debezium continuously tails the database transaction commit log (MySQL binlog / Postgres WAL) and streams verified commits directly into Kafka topics.
</p>
</details>

<details>
<summary><b>079. What is the Transactional Outbox Pattern?</b></summary>
<p>
<b>Answer:</b> Atomically writing business entities and outgoing event records into the same relational database in a single local transaction.<br>
A separate CDC engine or polling publisher reads from the <code>outbox</code> table and pushes events to a message queue, guaranteeing at-least-once message publication without distributed 2PC.
</p>
</details>

<details>
<summary><b>080. What are the trade-offs between Cache-Aside and Write-Through caching?</b></summary>
<p>
<b>Answer:</b><br>
- <b>Cache-Aside (Lazy Loading):</b> App queries cache; on miss, queries DB and populates cache. Only requested data is cached, but cache misses pay extra latency.<br>
- <b>Write-Through:</b> App writes to cache; cache writes synchronously to DB. Guarantees consistency and zero read penalty, but incurs write latency penalty.
</p>
</details>

<details>
<summary><b>081. What is the Thundering Herd (Cache Stampede) problem and how do you resolve it?</b></summary>
<p>
<b>Answer:</b> When a popular hot key expires and thousands of concurrent requests miss simultaneously, hammering the database.<br>
<b>Resolutions:</b> Distributed mutex lock (single thread regenerates cache), probabilistic early expiration (XFetch algorithm), or pre-emptive background warming.
</p>
</details>

<details>
<summary><b>082. What are the key database sharding strategies and their trade-offs?</b></summary>
<p>
<b>Answer:</b><br>
- <b>Hash-based:</b> Uniform data distribution via hash(key) % N, but range queries require scatter-gather across all shards.<br>
- <b>Range-based:</b> Groups adjacent keys on same shard (fast range queries), but creates hotspots on recent monotonic timestamps.<br>
- <b>Directory-based:</b> Lookup service maps keys to shards; highly flexible but adds a lookup hop and SPOF.
</p>
</details>

<details>
<summary><b>083. What is Read-Your-Own-Writes (RYOW) consistency and how is it implemented?</b></summary>
<p>
<b>Answer:</b> Guarantees that a user always observes their own updates immediately after submitting them, even if other users experience eventual consistency.<br>
<b>Implementation:</b> Route reads for updated entities to the Primary DB for a short window (e.g. 5 seconds) after a write, or track client version vectors in session cookies.
</p>
</details>

<details>
<summary><b>084. Why is the Saga Pattern preferred over Two-Phase Commit (2PC) in microservices?</b></summary>
<p>
<b>Answer:</b> 2PC is a synchronous, blocking protocol where coordinator failure locks database resources indefinitely.<br>
Sagas execute local transactions across services sequentially, using asynchronous message choreography or orchestration. If a step fails, compensating transactions are executed to roll back state without distributed locking.
</p>
</details>

<details>
<summary><b>085. What are the three states of a Circuit Breaker?</b></summary>
<p>
<b>Answer:</b><br>
- <b>CLOSED:</b> Requests flow normally; failures are counted.<br>
- <b>OPEN:</b> Failure threshold exceeded; requests fail immediately without calling downstream service.<br>
- <b>HALF-OPEN:</b> After a cooldown sleep window, allows limited canary requests. If they succeed, resets to CLOSED; if they fail, trips back to OPEN.
</p>
</details>

<details>
<summary><b>086. What is the Bulkhead Pattern in distributed architecture?</b></summary>
<p>
<b>Answer:</b> Partitioning system resources (e.g. thread pools, database connection pools, memory buffers) into isolated compartments.<br>
If one downstream dependency becomes slow or fails, its dedicated pool is exhausted without starving unrelated services sharing the process.
</p>
</details>

<details>
<summary><b>087. How does Token Bucket differ from Leaky Bucket for rate limiting?</b></summary>
<p>
<b>Answer:</b><br>
- <b>Token Bucket:</b> Tokens are added at a constant rate up to bucket capacity. Allows bursts of traffic up to the available token count.<br>
- <b>Leaky Bucket:</b> Requests enter a FIFO queue and leak out at a strictly smooth, constant rate, eliminating all burstiness.
</p>
</details>

<details>
<summary><b>088. What is CDN Origin Shielding and what problem does it solve?</b></summary>
<p>
<b>Answer:</b> An intermediate centralized caching layer placed between global CDN Edge PoPs and the origin server.<br>
Edge PoPs route cache misses through the Origin Shield rather than hitting the origin directly, collapsing redundant cache-fill spikes across hundreds of global PoPs into a single request.
</p>
</details>

<details>
<summary><b>089. What is the difference between a Layer 4 (L4) and Layer 7 (L7) Load Balancer?</b></summary>
<p>
<b>Answer:</b><br>
- <b>L4 (Transport):</b> Routes traffic based on IP and TCP/UDP ports without inspecting packet payload (extremely high throughput, low CPU overhead).<br>
- <b>L7 (Application):</b> Terminates TLS, parses HTTP headers, cookies, and URI paths to enable smart routing, sticky sessions, and rate limiting.
</p>
</details>

<details>
<summary><b>090. What is the key migration cost when adding a node in Consistent Hashing vs modulo hashing?</b></summary>
<p>
<b>Answer:</b><br>
- <b>Modulo hashing ($K \% N$):</b> Changing $N$ to $N+1$ remaps almost $100\%$ of keys, causing total cache invalidation.<br>
- <b>Consistent Hashing:</b> Adding a node migrates only $K/N$ keys on average, leaving the remaining $(N-1)/N$ keys unaffected.
</p>
</details>

<details>
<summary><b>091. How does Gorilla floating-point compression achieve 1.37 bytes per metric in Datadog/Prometheus?</b></summary>
<p>
<b>Answer:</b> XOR floating-point value compression.<br>
Most time series metrics change by small amounts. XORing consecutive IEEE 754 floats yields many leading and trailing zero bits. Gorilla stores only the count of leading/trailing zeros and the variable-length meaningful bits between them.
</p>
</details>

<details>
<summary><b>092. How does Delta-of-Deltas compression compress timestamps in Time Series Databases?</b></summary>
<p>
<b>Answer:</b> If metrics arrive at regular intervals (e.g. every 60s), the delta between timestamps is $60$, and the delta of deltas is $D = (t_i - t_{i-1}) - (t_{i-1} - t_{i-2}) = 0$.<br>
Gorilla encodes $D=0$ as a single bit <code>'0'</code>, compressing thousands of timestamps into near-zero bits.
</p>
</details>

<details>
<summary><b>093. What is the difference between State-based (CvRDT) and Operation-based (CmRDT) CRDTs?</b></summary>
<p>
<b>Answer:</b><br>
- <b>State-based (CvRDT):</b> Nodes send their full local state; replicas merge states using a join-semilattice merge function (must be Commutative, Associative, and Idempotent). Tolerates message drops.<br>
- <b>Operation-based (CmRDT):</b> Nodes broadcast mutation operations. Requires exactly-once/causal delivery, but sends much smaller network payloads.
</p>
</details>

<details>
<summary><b>094. Why does Uber prefer H3 Hexagonal spatial indexing over square grids or Geohash?</b></summary>
<p>
<b>Answer:</b> Invariant neighbor distance.<br>
In a square grid, edge neighbors are distance $1$ while diagonal neighbors are distance $\sqrt{2}$. In a regular hexagon, all 6 neighbors share identical center-to-center distances, drastically simplifying spatial radius queries, surge pricing calculations, and ride dispatching.
</p>
</details>

<details>
<summary><b>095. How does Geohash implement proximity search and what is its edge boundary gotcha?</b></summary>
<p>
<b>Answer:</b> Interleaves latitude and longitude bits into a base32 string.<br>
Points sharing long common prefixes are close together. <b>Gotcha:</b> Points immediately across a coordinate boundary line share no common prefix. Queries must search the target Geohash plus all 8 adjacent neighbor bounding boxes.
</p>
</details>

<details>
<summary><b>096. How does Content-Defined Chunking (CDC) via Rabin Fingerprints solve the byte-shift problem in file sync?</b></summary>
<p>
<b>Answer:</b> Fixed-size chunking (e.g. 4KB) invalidates all subsequent chunk hashes if a single byte is inserted at file offset 0.<br>
CDC slides a polynomial rolling hash window and declares chunk boundaries whenever the lowest $N$ bits match a pattern (e.g. <code>hash & 0x1FFF == 0</code>), ensuring shifts alter at most one chunk.
</p>
</details>

<details>
<summary><b>097. How do Merkle Trees accelerate anti-entropy synchronization in distributed replicas?</b></summary>
<p>
<b>Answer:</b> A hash tree where leaf nodes represent data hashes and parent nodes represent hashes of children.<br>
Two replicas compare root hashes in $O(1)$. If roots differ, they compare child hashes level-by-level, pinpointing the exact modified keys in $O(\log N)$ network exchanges without transferring the full dataset.
</p>
</details>

<details>
<summary><b>098. What are Kafka's three segment files per partition on disk?</b></summary>
<p>
<b>Answer:</b><br>
1. <b><code>.log</code>:</b> The actual message log storing sequential message records.<br>
2. <b><code>.index</code>:</b> Sparse index mapping message offsets to physical byte offsets in the <code>.log</code> file.<br>
3. <b><code>.timeindex</code>:</b> Sparse index mapping message timestamps to offsets for time-based lookups and retention pruning.
</p>
</details>

<details>
<summary><b>099. How does Linux zero-copy sendfile() achieve extreme throughput in Kafka?</b></summary>
<p>
<b>Answer:</b> Eliminates context switches and buffer copies between kernel and user space.<br>
Instead of copying disk data: <code>Disk -> OS Buffer -> User Space Buffer -> Socket Buffer -> NIC Buffer</code>, <code>sendfile()</code> transfers data directly: <code>Disk -> OS PageCache -> NIC Buffer</code> with DMA.
</p>
</details>

<details>
<summary><b>100. What is Incremental Cooperative Rebalancing in Apache Kafka consumers?</b></summary>
<p>
<b>Answer:</b> Traditional Eager rebalancing revokes all partition assignments across all consumers during group membership changes (causing "stop-the-world" processing pauses).<br>
Cooperative rebalancing reassigns only the specific partitions that must move, allowing unaffected consumers to continue processing without interruption.
</p>
</details>

<details>
<summary><b>101. What is the role of Tombstones in Cassandra and what hazard occurs if GC Grace Seconds is too short?</b></summary>
<p>
<b>Answer:</b> Because SSTables are immutable, deletes cannot modify disk in-place; they append a <code>Tombstone</code> record.<br>
Compaction removes tombstones only after <code>gc_grace_seconds</code> (default 10 days). If a node stays offline longer than GC grace seconds, it misses tombstone deletion and can resurrect deleted records during repair.
</p>
</details>

<details>
<summary><b>102. What causes the Cassandra Tombstone Overwhelmed exception?</b></summary>
<p>
<b>Answer:</b> Scanning more than <code>tombstone_failure_threshold</code> (default 100,000) tombstones during a query before finding live data.<br>
Occurs from queue-like patterns in Cassandra (frequent inserts and rapid deletes), causing query failure and high JVM GC pressure.
</p>
</details>

<details>
<summary><b>103. How many Hash Slots are in a Redis Cluster and how is a key routed?</b></summary>
<p>
<b>Answer:</b> Exactly <b>16,384 slots</b> ($2^{14}$).<br>
Routing formula: <code>slot = CRC16(key) % 16384</code>. Using curly braces in keys (e.g. <code>{user_123}:orders</code>) forces Redis to hash only the text within braces, co-locating related keys on the same node for multi-key operations.
</p>
</details>

<details>
<summary><b>104. What are Redis's two persistence mechanisms and their trade-offs?</b></summary>
<p>
<b>Answer:</b><br>
- <b>RDB (Snapshot):</b> Point-in-time binary snapshot of memory via <code>fork()</code> copy-on-write. Fast startup and compact, but risks data loss between snapshot intervals.<br>
- <b>AOF (Append-Only File):</b> Logs every write command with configurable <code>fsync</code> (everysec, always, no). Better durability, but larger file size and slower restart.
</p>
</details>

<details>
<summary><b>105. What is the Martin Kleppmann critique of the Redis Redlock algorithm?</b></summary>
<p>
<b>Answer:</b> Redlock relies on physical clock synchronization across independent Redis masters.<br>
A long JVM Garbage Collection pause or process freeze between acquiring the lock and performing work can cause the lock lease to expire unnoticed, leading to concurrent execution by two clients unless fencing tokens are verified by storage.
</p>
</details>

<details>
<summary><b>106. When should you use Server-Sent Events (SSE) instead of WebSockets?</b></summary>
<p>
<b>Answer:</b> When communication is strictly <b>unidirectional server-to-client</b> (e.g. live financial tickers, notifications, LLM token streaming).<br>
SSE runs over standard HTTP/1.1 or HTTP/2, supports automatic reconnection and event IDs natively, and bypasses enterprise firewall restrictions without custom protocols.
</p>
</details>

<details>
<summary><b>107. How does Low-Latency HLS (LL-HLS) achieve sub-2-second latency in video streaming?</b></summary>
<p>
<b>Answer:</b> By breaking standard 6-second video segments into tiny 200–330ms <b>partial segments</b> (chunks) transferred via HTTP/2 push / chunked transfer encoding before the full parent segment is complete.
</p>
</details>

<details>
<summary><b>108. How do you implement robust Idempotency for financial payment endpoints?</b></summary>
<p>
<b>Answer:</b> Require client-generated <code>Idempotency-Key</code> header.<br>
1. Attempt to insert key into an ACID database table with a UNIQUE constraint.<br>
2. If insert succeeds, proceed with payment and cache the resulting HTTP response.<br>
3. If unique constraint fails, retrieve and return the previously cached response immediately without re-executing payment.
</p>
</details>

<details>
<summary><b>109. What is the difference between Wait-Die and Wound-Wait deadlock prevention schemes?</b></summary>
<p>
<b>Answer:</b> Both use transaction timestamps $T_{old} < T_{young}$.<br>
- <b>Wait-Die (Non-preemptive):</b> Older waits for younger; younger dies (aborts) if requesting older's resource.<br>
- <b>Wound-Wait (Preemptive):</b> Older preempts/wounds younger immediately; younger waits if requesting older's resource. Tends to minimize aborts compared to Wait-Die.
</p>
</details>

<details>
<summary><b>110. How do Multi-Region Active-Active databases resolve write conflicts?</b></summary>
<p>
<b>Answer:</b> Three common paradigms:<br>
1. <b>Last-Write-Wins (LWW):</b> Resolves using wall-clock timestamps (risks data loss due to clock drift).<br>
2. <b>CRDTs:</b> Mathematically commutative merges without data loss (ideal for counters and sets).<br>
3. <b>Partition Pinned (Home Region):</b> Shards data by user locality, routing all writes for a user to their primary region.
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
<details>
<summary><b>121. What is the Java Memory Model (JMM) happens-before guarantee for volatile variables?</b></summary>
<p>
<b>Answer:</b> A write to a <code>volatile</code> field happens-before every subsequent read of that same field.<br>
The compiler and CPU insert memory barrier instructions (StoreStore, StoreLoad, LoadLoad, LoadStore) preventing instruction reordering, ensuring all preceding writes become immediately visible to reader threads across CPU cores.
</p>
</details>

<details>
<summary><b>122. What advantages does VarHandle offer over AtomicReference in Java 9+?</b></summary>
<p>
<b>Answer:</b> Finer-grained memory access modes and reduced memory footprint.<br>
<code>VarHandle</code> allows variable-level access without wrapping objects in separate heap-allocated <code>AtomicReference</code> instances. It supports multiple access modes: Plain, Opaque, Release/Acquire, and Volatile, optimizing performance on non-x86 architectures.
</p>
</details>

<details>
<summary><b>123. How does StampedLock achieve superior read throughput over ReentrantReadWriteLock?</b></summary>
<p>
<b>Answer:</b> Optimistic Read mode via <code>tryOptimisticRead()</code>.<br>
It returns a stamp without acquiring an actual lock or modifying cache-line state. The reader copies state, then validates the stamp with <code>validate(stamp)</code>. If no writer intervened, the read succeeds with zero cache-line ping-pong; if invalidated, it falls back to a pessimistic read lock.
</p>
</details>

<details>
<summary><b>124. What causes Virtual Thread Pinning in Java 21 and how do you avoid it?</b></summary>
<p>
<b>Answer:</b> When a virtual thread executes blocking I/O while inside a <code>synchronized</code> block/method or native call (JNI).<br>
The virtual thread cannot unmount and "pins" the underlying OS carrier thread, starving the carrier pool. <b>Remedy:</b> Replace <code>synchronized</code> with <code>ReentrantLock</code>.
</p>
</details>

<details>
<summary><b>125. What is False Sharing and how does Java mitigate it?</b></summary>
<p>
<b>Answer:</b> When two independent variables accessed by different CPU cores reside on the same 64-byte L1/L2 CPU cache line.<br>
Writes by Core 1 invalidate Core 2's cache line, causing severe cache thrashing. <b>Mitigation:</b> Cache-line padding or the <code>@jdk.internal.vm.annotation.Contended</code> annotation.
</p>
</details>

<details>
<summary><b>126. What is the difference between CompletableFuture.thenCompose() and thenCombine()?</b></summary>
<p>
<b>Answer:</b><br>
- <b><code>thenCompose()</code>:</b> Monadic <code>flatMap</code>. Chains dependent asynchronous stages where Stage B requires the output of Stage A.<br>
- <b><code>thenCombine()</code>:</b> Concurrently executes two independent futures and applies a BiFunction to both completed results.
</p>
</details>

<details>
<summary><b>127. Why do ThreadLocal variables cause severe memory leaks in ThreadPoolExecutor?</b></summary>
<p>
<b>Answer:</b> Worker threads in a pool are long-lived and reused.<br>
A thread holds a strong reference to its <code>ThreadLocalMap</code>. If the application does not explicitly invoke <code>threadLocal.remove()</code>, cached objects remain in the heap for the entire lifetime of the thread pool.
</p>
</details>

<details>
<summary><b>128. Why is the volatile modifier mandatory in the Double-Checked Locking Singleton pattern?</b></summary>
<p>
<b>Answer:</b> To prevent instruction reordering during object construction.<br>
The expression <code>instance = new Singleton()</code> executes in 3 steps: 1. Allocate memory, 2. Initialize fields, 3. Assign reference. Without <code>volatile</code>, steps 2 and 3 can reorder, allowing a second thread to observe a non-null reference to an uninitialized object.
</p>
</details>

<details>
<summary><b>129. How does ConcurrentHashMap achieve high write concurrency in Java 8+?</b></summary>
<p>
<b>Answer:</b> Fine-grained node-level synchronization.<br>
It uses CAS for inserting into empty hash buckets. When a bucket contains a collision chain, it synchronizes exclusively on the first <code>Node</code> of that specific bin. Other threads writing to different bins proceed with zero lock contention.
</p>
</details>

<details>
<summary><b>130. Why does CopyOnWriteArrayList offer O(1) lock-free reads but expensive O(N) writes?</b></summary>
<p>
<b>Answer:</b> Reads access an immutable reference to the backing array without locking or synchronization.<br>
Every mutation (add/set/remove) allocates a completely new copy of the underlying array under an internal lock, making it ideal only for read-heavy, rarely-mutated collections (e.g. event listeners).
</p>
</details>

<details>
<summary><b>131. When should you use ReentrantLock over the native synchronized keyword?</b></summary>
<p>
<b>Answer:</b> When advanced locking capabilities are required:<br>
1. Timed lock acquisition (<code>tryLock(timeout)</code> to prevent deadlocks).<br>
2. Interruptible lock acquisition (<code>lockInterruptibly()</code>).<br>
3. Fair scheduling queue policy (FIFO).<br>
4. Multiple independent condition variables (<code>newCondition()</code>).
</p>
</details>

<details>
<summary><b>132. How does the ForkJoinPool Work-Stealing algorithm maximize CPU core utilization?</b></summary>
<p>
<b>Answer:</b> Each worker thread maintains a double-ended queue (deque).<br>
A worker pushes new tasks and pops its own subtasks from the <b>head</b> of its deque (LIFO order, maximizing CPU cache locality). When a thread exhausts its own queue, it "steals" tasks from the <b>tail</b> of a peer's deque (FIFO order, minimizing contention).
</p>
</details>

<details>
<summary><b>133. What internal data structure powers Java's java.util.concurrent.DelayQueue?</b></summary>
<p>
<b>Answer:</b> A <code>PriorityQueue</code> guarded by a <code>ReentrantLock</code> implementing the <b>Leader-Follower pattern</b>.<br>
The leader thread waits for the earliest expiration delay using <code>Condition.awaitNanos()</code>, while other follower threads sleep indefinitely until signaled by the leader.
</p>
</details>

<details>
<summary><b>134. How does CountDownLatch differ from CyclicBarrier?</b></summary>
<p>
<b>Answer:</b><br>
- <b>CountDownLatch:</b> One-shot terminal latch; cannot be reset. Any thread can decrement the count without waiting.<br>
- <b>CyclicBarrier:</b> Reusable synchronization barrier where $N$ threads must all call <code>await()</code> before any thread is allowed to pass to the next phase.
</p>
</details>

<details>
<summary><b>135. What is the difference between a Binary Semaphore and a ReentrantLock?</b></summary>
<p>
<b>Answer:</b> Ownership semantics.<br>
A <code>ReentrantLock</code> has strict thread ownership: only the thread that acquired the lock can release it. A <code>Semaphore</code> tracks permits without ownership; one thread can acquire a permit and a different thread can release it (ideal for signaling).
</p>
</details>

<details>
<summary><b>136. What are the four default RejectedExecutionHandler policies in ThreadPoolExecutor?</b></summary>
<p>
<b>Answer:</b><br>
1. <b>AbortPolicy (Default):</b> Throws <code>RejectedExecutionException</code>.<br>
2. <b>CallerRunsPolicy:</b> Executes the task in the caller's invoking thread, naturally applying backpressure.<br>
3. <b>DiscardPolicy:</b> Silently drops the rejected task without exception.<br>
4. <b>DiscardOldestPolicy:</b> Drops the oldest unhandled task in the queue and retries execution.
</p>
</details>

<details>
<summary><b>137. What is the formula for optimal Thread Pool sizing?</b></summary>
<p>
<b>Answer:</b><br>
- <b>CPU-bound tasks:</b> $N_{threads} = N_{cpu} + 1$ (extra 1 handles rare page faults).<br>
- <b>I/O-bound tasks:</b> $N_{threads} = N_{cpu} \times \left(1 + \frac{\text{Wait Time}}{\text{Compute Time}}\right) = \frac{N_{cpu}}{\text{Target CPU Utilization}}$.
</p>
</details>

<details>
<summary><b>138. What are the four necessary Coffman conditions for Deadlock and how do you prevent them?</b></summary>
<p>
<b>Answer:</b> Mutual Exclusion, Hold and Wait, No Preemption, Circular Wait.<br>
<b>Prevention:</b> Eliminate Circular Wait by enforcing a global, deterministic acquisition order for all locks (e.g. always lock resource with smaller ID first).
</p>
</details>

<details>
<summary><b>139. How do you implement the Open/Closed Principle (OCP) in Low-Level Design?</b></summary>
<p>
<b>Answer:</b> Design software entities open for extension but closed for modification.<br>
Replace <code>if-else</code> or <code>switch</code> dispatch blocks with Polymorphism, Interfaces, and the Strategy / Factory design patterns so new business rules can be added via new classes without modifying existing code.
</p>
</details>

<details>
<summary><b>140. How does the Strategy Pattern differ from the State Pattern?</b></summary>
<p>
<b>Answer:</b><br>
- <b>Strategy:</b> The client consciously configures a pluggable interchangeable algorithm (e.g. <code>CreditCardPayment</code> vs <code>UPIPayment</code>). Strategies are usually unaware of each other.<br>
- <b>State:</b> The context's behavior changes dynamically based on its internal state machine (e.g. <code>OrderCreated</code> $\to$ <code>OrderShipped</code>). Concrete states frequently know about and trigger transitions to other states.
</p>
</details>

<details>
<summary><b>141. What is the difference between Factory Method and Abstract Factory patterns?</b></summary>
<p>
<b>Answer:</b><br>
- <b>Factory Method:</b> Uses inheritance; delegates instantiation of a single product to a subclass method.<br>
- <b>Abstract Factory:</b> Uses composition; defines an interface for creating families of related or dependent objects without specifying concrete classes (e.g. DarkThemeButton + DarkThemeScrollbar).
</p>
</details>

<details>
<summary><b>142. What is the Observer Pattern and how do you prevent memory leaks in subscriber lists?</b></summary>
<p>
<b>Answer:</b> A one-to-many dependency where a subject notifies observers of state changes.<br>
<b>Leak Prevention:</b> Observers registered with long-lived subjects must either be explicitly unregistered (<code>unsubscribe</code>) or stored using <code>WeakReference</code> / <code>WeakHashMap</code> so garbage collection is not blocked.
</p>
</details>

<details>
<summary><b>143. How does the Decorator Pattern differ from the Adapter Pattern?</b></summary>
<p>
<b>Answer:</b><br>
- <b>Decorator:</b> Enhances or adds responsibilities to an object dynamically while preserving the exact same interface.<br>
- <b>Adapter:</b> Converts an existing incompatible interface into the expected target interface without changing underlying behavior.
</p>
</details>

<details>
<summary><b>144. How does the Command Pattern implement multi-level Undo and Redo?</b></summary>
<p>
<b>Answer:</b> By encapsulating operations as command objects with <code>execute()</code> and <code>undo()</code> methods.<br>
Executed commands are pushed to an <code>undoStack</code>; invoking undo pops from <code>undoStack</code>, calls <code>undo()</code>, and pushes to a <code>redoStack</code>.
</p>
</details>

<details>
<summary><b>145. What is the Bill Pugh Singleton implementation and why is it thread-safe without synchronization?</b></summary>
<p>
<b>Answer:</b> Static Inner Helper Class pattern.<br>
The inner class <code>SingletonHolder</code> is not loaded into memory until the static method <code>getInstance()</code> is invoked. The JVM classloader phase guarantees atomic, thread-safe class initialization without synchronization overhead.
</p>
</details>

<details>
<summary><b>146. What is the Hollywood Principle in the Template Method Pattern?</b></summary>
<p>
<b>Answer:</b> "Don't call us, we'll call you."<br>
The abstract base class defines the invariant skeleton algorithm and calls primitive subclass hook methods at designated points, reversing standard control flow.
</p>
</details>

<details>
<summary><b>147. How does the Chain of Responsibility pattern handle sequential validation?</b></summary>
<p>
<b>Answer:</b> Handlers maintain a reference to the next handler in a pipeline.<br>
A request passes sequentially along the chain. Each handler inspects the request, executes its check (e.g. Auth $\to$ RateLimit $\to$ Sanitization), and decides whether to forward to <code>next.handle(request)</code> or terminate the chain.
</p>
</details>

<details>
<summary><b>148. What are the four core layers of Clean Architecture in Java LLD?</b></summary>
<p>
<b>Answer:</b><br>
1. <b>Entities:</b> Pure enterprise business models (zero framework dependencies).<br>
2. <b>Use Cases / Services:</b> Application business rules orchestrating workflows.<br>
3. <b>Interface Adapters:</b> Controllers, Gateways, Presenters converting formats.<br>
4. <b>Frameworks & Drivers:</b> Spring, JDBC, Redis, HTTP endpoints. Dependencies strictly point inwards.
</p>
</details>

<details>
<summary><b>149. When is Object Pooling an anti-pattern vs. a necessary optimization in Java?</b></summary>
<p>
<b>Answer:</b><br>
- <b>Anti-pattern:</b> Pooling lightweight, short-lived domain objects increases GC pressure and synchronization overhead (the modern JVM young-gen allocator is faster than manual pools).<br>
- <b>Necessary:</b> Pooling heavyweight system resources with expensive initialization costs (e.g. JDBC DB connections, TCP sockets, large off-heap ByteBuffers).
</p>
</details>

<details>
<summary><b>150. What requirements guarantee an immutable object in modern Java?</b></summary>
<p>
<b>Answer:</b><br>
1. Declare class <code>final</code> (prevent subclass overrides).<br>
2. Declare all fields <code>private final</code>.<br>
3. Do not provide mutator (setter) methods.<br>
4. Perform defensive copies of mutable arguments in constructors and getters.<br>
Java <code>record</code> types enforce these immutability principles natively.
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
<details>
<summary><b>156. How do you structure a high-impact technical story using the STAR framework?</b></summary>
<p>
<b>Answer:</b><br>
- <b>Situation (15%):</b> Context, scale, and why the problem was mission-critical.<br>
- <b>Task (10%):</b> Your explicit responsibility and constraints.<br>
- <b>Action (60%):</b> Technical decisions, trade-offs evaluated, and leadership taken.<br>
- <b>Result (15%):</b> Concrete, quantified metrics (e.g. "$p99$ latency reduced from 800ms to 45ms, saving $120K/yr in AWS compute").
</p>
</details>

<details>
<summary><b>157. What is the difference between owning 'I' vs 'We' in Senior/Staff engineering interviews?</b></summary>
<p>
<b>Answer:</b> Overusing "We" makes interviewers unable to evaluate your personal contribution; overusing "I" sounds isolated or non-collaborative.<br>
<b>Best practice:</b> Use "We" for team objectives and collaborative ideation, but pivot explicitly to "I" when detailing your personal technical contributions, architectural designs, and implementation leadership.
</p>
</details>

<details>
<summary><b>158. How do you demonstrate Amazon's 'Customer Obsession' as a backend engineer?</b></summary>
<p>
<b>Answer:</b> By prioritizing user-facing reliability, data integrity, and latency over internal developer convenience.<br>
Working backwards from customer pain points (e.g. designing graceful offline degradations or zero-downtime database migrations so users never experience service interruptions).
</p>
</details>

<details>
<summary><b>159. How do you demonstrate 'Bias for Action' without introducing reckless technical debt?</b></summary>
<p>
<b>Answer:</b> Distinguish between One-Way Doors (irreversible, high-stakes decisions requiring deep research) and Two-Way Doors (reversible, fast-iteration decisions).<br>
Take calculated risks on Two-Way Doors with automated feature flags, telemetry canaries, and quick rollback strategies.
</p>
</details>

<details>
<summary><b>160. How do you answer: 'Tell me about a time you made a major architectural mistake'?</b></summary>
<p>
<b>Answer:</b> Choose a genuine technical mistake (not a disguised humblebrag).<br>
1. Own the mistake immediately without shifting blame to teammates or tools.<br>
2. Explain the rapid mitigation steps taken to restore service.<br>
3. Highlight the systemic preventive measures (CAPA) implemented (automated integration tests, lint rules, runbooks) so the failure can never recur.
</p>
</details>

<details>
<summary><b>161. How do you demonstrate 'Dive Deep' during an engineering post-mortem?</b></summary>
<p>
<b>Answer:</b> By refusing to accept superficial explanations (e.g. "server ran out of memory").<br>
Inspect heap dumps, analyze GC telemetry, review thread dumps, and audit git commit diffs to pinpoint the exact root cause (e.g. unbounded thread-local buffer allocation).
</p>
</details>

<details>
<summary><b>162. How do you demonstrate 'Deliver Results' when facing tight deadlines and blockers?</b></summary>
<p>
<b>Answer:</b> Deconstruct the scope into a phased delivery plan (MVP first), negotiate non-critical feature deferrals with Product Managers, eliminate roadblocks for peers, and maintain automated test coverage rather than taking shortcuts that cause launch day fires.
</p>
</details>

<details>
<summary><b>163. What does the Amazon LP 'Are Right, A Lot' mean for Senior Engineers?</b></summary>
<p>
<b>Answer:</b> Having strong business and technical judgment, seeking disconfirming evidence to test your own beliefs, listening to diverse perspectives, and demonstrating a track record of architectural decisions that proved successful in production over 2–5 year horizons.
</p>
</details>

<details>
<summary><b>164. How do you navigate a technical disagreement with a peer or Tech Lead?</b></summary>
<p>
<b>Answer:</b> Remove emotion and ground the dispute in objective data: benchmarks, RFC trade-off matrices, and proof-of-concept prototypes.<br>
If consensus cannot be reached, escalate transparently or apply <b>Disagree & Commit</b>: fully support the decided path without passive-aggressive resistance.
</p>
</details>

<details>
<summary><b>165. How do you handle Scope Creep from Product Managers mid-sprint?</b></summary>
<p>
<b>Answer:</b> Use the Project Management Triangle (Scope, Time, Quality).<br>
Never sacrifice quality (testing/code review). Present the trade-off transparently: "We can incorporate Feature X, but it requires pushing Feature Y to the next milestone to preserve the committed launch date."
</p>
</details>

<details>
<summary><b>166. How do you communicate complex technical debt to non-technical executive stakeholders?</b></summary>
<p>
<b>Answer:</b> Translate technical debt into business language: financial risk, customer churn, developer velocity, and compliance SLA exposure.<br>
Explain that paying down technical debt is an investment that increases future feature delivery velocity by $30\%$ and prevents costly Sev-1 outages.
</p>
</details>

<details>
<summary><b>167. How do you mentor and upskill junior engineers on your team?</b></summary>
<p>
<b>Answer:</b> Use the Socratic method rather than providing answers directly.<br>
Conduct collaborative design walkthroughs, pair-program on complex debugging sessions, delegate ownership of complete features, and provide timely, actionable, and encouraging feedback in code reviews.
</p>
</details>

<details>
<summary><b>168. What are the core responsibilities of an Incident Commander (IC) during a Sev-1 outage?</b></summary>
<p>
<b>Answer:</b><br>
1. Establish a clear war room and communication channel.<br>
2. Maintain single-threaded command: designate who investigates what, preventing duplicate efforts.<br>
3. Prioritize service mitigation (rollback/traffic rerouting) over deep root-cause analysis.<br>
4. Provide periodic executive status broadcasts (e.g. every 15 minutes).
</p>
</details>

<details>
<summary><b>169. How does the 5-Whys methodology identify systemic root causes?</b></summary>
<p>
<b>Answer:</b> By iteratively asking "Why?" five times to drill past proximate human symptoms to systemic architectural defects.<br>
Example: Outage $\to$ DB out of connections $\to$ slow queries $\to$ missing index $\to$ no automated query plan analyzer in CI $\to$ <b>Action:</b> add automated query performance linters.
</p>
</details>

<details>
<summary><b>170. Why is a Blameless Post-Mortem culture essential for high-reliability engineering?</b></summary>
<p>
<b>Answer:</b> Scapegoating individuals causes engineers to conceal mistakes, delay incident reporting, and fear innovation.<br>
Blameless culture assumes well-intentioned engineers made reasonable choices based on available context, focusing energy on repairing systemic process and software safeguards.
</p>
</details>

<details>
<summary><b>171. What makes a Corrective and Preventive Action (CAPA) item effective?</b></summary>
<p>
<b>Answer:</b> It must be preventative, automated, assigned to a specific owner with a hard deadline, and tracked as a P0 engineering ticket rather than optional documentation.
</p>
</details>

<details>
<summary><b>172. How do you deliver constructive feedback in code reviews without demoralizing peers?</b></summary>
<p>
<b>Answer:</b> Focus on the code, not the person. Use inquisitive language ("What do you think about using a Map here?") rather than prescriptive demands. Explain the underlying "why" (concurrency, performance) and balance critical feedback with praise for well-designed modules.
</p>
</details>

<details>
<summary><b>173. How do you resolve conflicting roadmaps between Platform and Product engineering teams?</b></summary>
<p>
<b>Answer:</b> Align on shared business OKRs and customer outcomes. Establish formal SLAs for platform migrations, dedicate a fixed capacity percentage (e.g. 20%) to platform hardening, and demonstrate platform ROI through developer velocity metrics.
</p>
</details>

<details>
<summary><b>174. How do you execute a migration from a legacy monolithic database to microservices safely?</b></summary>
<p>
<b>Answer:</b> Apply the <b>Strangler Fig Pattern</b>.<br>
1. Dual-write to legacy and new databases.<br>
2. Validate parity asynchronously via automated data comparison scripts.<br>
3. Switch read traffic incrementally via feature flags.<br>
4. Stop writes to legacy and decommission.
</p>
</details>

<details>
<summary><b>175. What are high-leverage questions to ask your interviewers in reverse interviewing?</b></summary>
<p>
<b>Answer:</b><br>
- <i>To Tech Lead:</i> "What was the most contentious architectural disagreement on the team in the last 6 months, and how was it resolved?"<br>
- <i>To Engineering Manager:</i> "How do you protect your team's engineering velocity against non-stop emergency requests from business stakeholders?"
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
<details>
<summary><b>181. What is the fan-out and height of a B+ Tree with 4KB pages and 8-byte keys?</b></summary>
<p>
<b>Answer:</b> Fan-out $M \approx 4096 / (8 + 8) \approx 256$ to $1000$ pointers per page.<br>
With fan-out $M=1000$, a B+ Tree of height 3 indexes up to $10^9$ (1 Billion) records, requiring at most 3 disk I/O operations (the top 2 levels are usually pinned in RAM).
</p>
</details>

<details>
<summary><b>182. What is the difference between a Clustered Index and a Secondary Index?</b></summary>
<p>
<b>Answer:</b><br>
- <b>Clustered Index:</b> Dictates physical sorting order of rows on disk; leaf pages store the actual table row data (only 1 per table).<br>
- <b>Secondary Index:</b> Leaf pages store secondary key values and pointer / primary key references to the clustered index, requiring a secondary lookup (bookmark lookup) to fetch full row data.
</p>
</details>

<details>
<summary><b>183. What is a Covering Index (Index-Only Scan) in SQL databases?</b></summary>
<p>
<b>Answer:</b> An index containing all columns requested by a query (e.g. composite index on <code>(user_id, status, created_at)</code>).<br>
The database engine satisfies the query entirely from the B-Tree index pages without visiting the underlying clustered table pages, eliminating expensive random disk I/O.
</p>
</details>

<details>
<summary><b>184. What is the RUM Conjecture in database storage engines?</b></summary>
<p>
<b>Answer:</b> Storage engines must trade off among <b>Read Amplification (R)</b>, <b>Update/Write Amplification (U)</b>, and <b>Memory/Space Amplification (M)</b>.<br>
Optimizing for any two inherently compromises the third (e.g. B-Trees minimize Read Amplification at the cost of Write Amplification; LSM-Trees minimize Write Amplification at the cost of Read Amplification).
</p>
</details>

<details>
<summary><b>185. How does PostgreSQL implement Multi-Version Concurrency Control (MVCC) internally?</b></summary>
<p>
<b>Answer:</b> Row versioning using hidden system columns <code>xmin</code> (creation transaction ID) and <code>xmax</code> (deletion/supersession transaction ID).<br>
Updates insert a new tuple and mark the old tuple's <code>xmax</code>. Stale dead tuples are reclaimed asynchronously by the <code>VACUUM</code> background worker.
</p>
</details>

<details>
<summary><b>186. What is the difference between MySQL InnoDB's Redo Log and Undo Log?</b></summary>
<p>
<b>Answer:</b><br>
- <b>Redo Log:</b> Physical log ensuring Durability (ACID 'D'). Replays committed transactions during crash recovery.<br>
- <b>Undo Log:</b> Logical log ensuring Atomicity (ACID 'A') and MVCC. Rolls back uncommitted changes and reconstructs historical row snapshots for consistent reads.
</p>
</details>

<details>
<summary><b>187. What is Strict Two-Phase Locking (SS2PL) and why is it used?</b></summary>
<p>
<b>Answer:</b> Transactions acquire locks during the Growing Phase, but release <b>all exclusive locks simultaneously at transaction commit/abort</b>.<br>
This guarantees Serializability and eliminates cascading rollbacks (cascading aborts) where one transaction reads uncommitted changes from another that later fails.
</p>
</details>

<details>
<summary><b>188. What is a Phantom Read and how does Serializable Isolation prevent it?</b></summary>
<p>
<b>Answer:</b> When Transaction 1 executes a range query (e.g. <code>WHERE age > 30</code>), and Transaction 2 inserts a new row matching that predicate and commits, causing Transaction 1 to observe different row counts upon re-querying.<br>
<b>Prevention:</b> Serializable Isolation uses <b>Predicate Locking</b> or <b>Index-Range Locks (Next-Key Locking)</b> in InnoDB.
</p>
</details>

<details>
<summary><b>189. Why is HikariCP the fastest JDBC connection pool in the Java ecosystem?</b></summary>
<p>
<b>Answer:</b> Micro-optimizations:<br>
1. <code>FastList</code> custom collection eliminating range checks.<br>
2. <code>ConcurrentBag</code> thread-local lock-free borrowing using handoff queues.<br>
3. Bytecode-level optimization via Javassist eliminating reflection overhead.
</p>
</details>

<details>
<summary><b>190. When should you denormalize a database schema?</b></summary>
<p>
<b>Answer:</b> In high-throughput read-heavy systems where multi-table relational joins introduce intolerable latency bottlenecks.<br>
Denormalization duplicates data to allow single-table lookups, trading off increased storage and complex update synchronization for sub-millisecond read latency.
</p>
</details>

<details>
<summary><b>191. How does Optimistic Locking differ from Pessimistic Locking in database access?</b></summary>
<p>
<b>Answer:</b><br>
- <b>Optimistic Locking:</b> Assumes conflicts are rare. Adds a <code>version</code> column. Updates execute: <code>UPDATE tbl SET ..., version = version + 1 WHERE id = ? AND version = ?</code>. If row count is 0, conflict occurred.<br>
- <b>Pessimistic Locking:</b> Assumes conflicts are frequent. Acquires row locks up-front: <code>SELECT ... FOR UPDATE</code>.
</p>
</details>

<details>
<summary><b>192. What are the two phases of Two-Phase Commit (2PC) and why is it a blocking protocol?</b></summary>
<p>
<b>Answer:</b><br>
1. <b>Prepare Phase:</b> Coordinator asks participants if they can commit. Participants acquire locks, flush undo/redo logs, and vote YES or NO.<br>
2. <b>Commit Phase:</b> If all vote YES, coordinator broadcasts COMMIT. If any votes NO, broadcasts ROLLBACK.<br>
<b>Blocking:</b> If the coordinator crashes mid-commit, participants remain locked indefinitely waiting for resolution.
</p>
</details>

<details>
<summary><b>193. How does TCP AIMD (Additive Increase / Multiplicative Decrease) prevent network collapse?</b></summary>
<p>
<b>Answer:</b> Congestion avoidance algorithm.<br>
In the absence of packet loss, the congestion window ($cwnd$) increases linearly by 1 Maximum Segment Size (MSS) per Round-Trip Time (RTT). When packet loss is detected, $cwnd$ is halved multiplicatively, rapidly backing off.
</p>
</details>

<details>
<summary><b>194. Why does the TCP TIME_WAIT state persist for 2 MSL (Maximum Segment Lifetime)?</b></summary>
<p>
<b>Answer:</b> Typically 1–2 minutes.<br>
1. Ensures the final <code>ACK</code> sent by the active closer reaches the peer (resends ACK if peer retransmits <code>FIN</code>).<br>
2. Allows lingering duplicate packets from the connection to expire in the network before a new socket reuses the same IP/Port tuple.
</p>
</details>

<details>
<summary><b>195. How does HTTP/3 over QUIC eliminate TCP Head-of-Line (HoL) Blocking?</b></summary>
<p>
<b>Answer:</b> In HTTP/2 over TCP, packet loss on a single stream halts the entire TCP connection until the missing packet is retransmitted.<br>
HTTP/3 runs over <b>QUIC (UDP)</b>, implementing stream multiplexing at the transport layer: a dropped packet delays only its individual stream, while all other concurrent streams proceed unblocked.
</p>
</details>

<details>
<summary><b>196. What is the latency advantage of TLS 1.3 over TLS 1.2?</b></summary>
<p>
<b>Answer:</b> 1-RTT connection handshake (vs 2-RTT in TLS 1.2).<br>
The client sends cryptographic key shares directly in the <code>ClientHello</code> message, allowing symmetric encryption keys to be negotiated in a single round trip. Subsequent connections can resume with <b>0-RTT</b> data.
</p>
</details>

<details>
<summary><b>197. How does HTTP/2 Binary Framing differ from HTTP/1.1 text protocol?</b></summary>
<p>
<b>Answer:</b> HTTP/1.1 parses text headers line-by-line separated by CRLF. HTTP/2 divides communication into binary-encoded frames (HEADERS, DATA, SETTINGS) interleaved over a single TCP stream with stream IDs, eliminating text parsing overhead and enabling bidirectional multiplexing.
</p>
</details>

<details>
<summary><b>198. How does DNS Anycast routing work?</b></summary>
<p>
<b>Answer:</b> Multiple geographically distributed DNS servers advertise the exact same IP address via BGP (Border Gateway Protocol).<br>
Internet routers automatically route a client's DNS query along the shortest BGP AS path to the nearest geographical datacenter, providing automatic failover and DDoS absorption.
</p>
</details>

<details>
<summary><b>199. What is Rendezvous Hashing (Highest Random Weight / HRW)?</b></summary>
<p>
<b>Answer:</b> An alternative to consistent hashing rings.<br>
To route a key, compute $weight = hash(key + node_i)$ for all nodes $node_i$, routing the key to the node yielding the highest weight. Achieves optimal $O(1/N)$ load balance when nodes join or leave without building a ring data structure.
</p>
</details>

<details>
<summary><b>200. Why is Linux epoll O(1) while select/poll is O(N)?</b></summary>
<p>
<b>Answer:</b> <code>select()</code> and <code>poll()</code> require copying the full file descriptor list from user space to kernel space on every call and scanning all $N$ sockets.<br>
<code>epoll</code> registers sockets once in a kernel Red-Black tree and uses OS interrupt callbacks to enqueue active sockets to a ready list, returning strictly the ready file descriptors in $O(1)$ time.
</p>
</details>

<details>
<summary><b>201. How do STUN and TURN protocols enable WebRTC NAT Traversal?</b></summary>
<p>
<b>Answer:</b><br>
- <b>STUN (Session Traversal Utilities for NAT):</b> Discovers a client's public IP and port when behind non-symmetric NAT, allowing direct peer-to-peer UDP communication.<br>
- <b>TURN (Traversal Using Relays around NAT):</b> Fallback relay server that proxies media streams when symmetric NAT blocks direct peer-to-peer hole-punching.
</p>
</details>

<details>
<summary><b>202. What is the difference between fsync() and fdatasync() in Linux?</b></summary>
<p>
<b>Answer:</b><br>
- <b><code>fsync()</code>:</b> Flushes both dirty data pages and metadata (file size, modification timestamp, directory entries) to disk.<br>
- <b><code>fdatasync()</code>:</b> Flushes dirty data pages and only essential metadata required to read the data, skipping non-essential attributes (e.g. access time) to halve disk I/O operations.
</p>
</details>

<details>
<summary><b>203. Why are Columnar Storage formats (Parquet, ORC) 10x faster for OLAP analytical queries?</b></summary>
<p>
<b>Answer:</b> Column Projection and Compression.<br>
1. <b>Projection:</b> Queries reading 3 columns out of 100 read only 3% of data from disk.<br>
2. <b>Compression:</b> Homogeneous typed data stored contiguously achieves massive compression ratios using Dictionary Encoding and Run-Length Encoding (RLE).
</p>
</details>

<details>
<summary><b>204. What is the Write Skew anomaly in Snapshot Isolation?</b></summary>
<p>
<b>Answer:</b> Occurs when two concurrent transactions read overlapping data, verify an integrity invariant, make disjoint writes, and both commit, violating the global invariant.<br>
<i>Example:</i> Hospital on-call rule requires $\ge 1$ doctor on duty. Dr. A and Dr. B simultaneously query (see 2 on duty), and both submit resignations. Both commit, leaving 0 doctors.
</p>
</details>

<details>
<summary><b>205. How does Leveled Compaction differ from Size-Tiered Compaction in RocksDB and Cassandra?</b></summary>
<p>
<b>Answer:</b><br>
- <b>Size-Tiered Compaction:</b> Groups SSTables of similar sizes and merges them. High write throughput, but high temporary space amplification (requires up to 50% free disk space).<br>
- <b>Leveled Compaction:</b> Organizes SSTables into hierarchical levels ($L_0, L_1, \dots$) with fixed 10x size multipliers. Guarantees non-overlapping key ranges within each level $> 0$, minimizing read amplification and space amplification ($< 10\%$ disk overhead).
</p>
</details>


---

<div align="center">

| [← Back to GoF Patterns & Concurrency](./03-gof-patterns-and-concurrency-cheatsheet.md) | [Track Hub: Cheatsheets](./README.md) | [Root Hub →](../README.md) |
| :--- | :---: | ---: |

</div>
