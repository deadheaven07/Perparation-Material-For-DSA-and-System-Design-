# 03. Master Algorithmic Paradigms & Pattern Playbook

[← Back to Data Structures](./02-master-data-structures-selection-and-trade-offs.md) | [Track Hub](./README.md) | [Next: BUD Optimization →](./04-the-bud-optimization-framework.md)

---

## 1. The Global Algorithmic Taxonomy

Every algorithmic problem in technical computer science maps to one of **13 fundamental paradigms**. Recognizing the paradigm is 80% of solving the problem:

```
                    ╭───────────────────────────────────────────────╮
                    │       THE 13 ALGORITHMIC PARADIGMS            │
                    ├───────────────────────┬───────────────────────┤
                    │ 1. Two Pointers       │ 8. Dynamic Programming│
                    │ 2. Sliding Window     │ 9. Greedy Algorithms  │
                    │ 3. Binary Search      │ 10. Backtracking      │
                    │ 4. Sorting & Selection│ 11. Divide & Conquer  │
                    │ 5. Graph Traversals   │ 12. Bit Manipulation  │
                    │ 6. Shortest Paths/MST │ 13. Strings & Math    │
                    │ 7. Advanced Graphs    │                       │
                    ╰───────────────────────┴───────────────────────╯
```

---

## 2. Exhaustive Paradigm Playbook: Triggers, Invariants & Scaffolds

### 2.1 Paradigm 1: Two Pointers (Converging & Fast/Slow)
- **Primary Triggers**: Sorted array, searching pairs with target sum, palindrome verification, cycle detection in linked lists.
- **Invariant**: In sorted space, moving left pointer inward strictly increases value; moving right pointer inward strictly decreases value.
- **Fast/Slow Pointer (Floyd's)**: Fast advances 2 steps, Slow advances 1 step. If a cycle exists, Fast must overlap Slow within $\mathcal{O}(\text{cycle length})$ steps.

```java
// Scaffold: Converging Two Pointers
int left = 0, right = arr.length - 1;
while (left < right) {
    long sum = (long) arr[left] + arr[right];
    if (sum == target) {
        return new int[]{left, right};
    } else if (sum < target) {
        left++;
    } else {
        right--;
    }
}
```

---

### 2.2 Paradigm 2: Sliding Window (Fixed, Dynamic, Non-Monotonic)
- **Primary Triggers**: "Contiguous subarray/substring", "longest/shortest window satisfying condition $K$".
- **Dynamic Window Invariant**: Expand `right` to include elements. When the window becomes invalid, shrink `left` until validity is restored.

```java
// Scaffold: Dynamic Sliding Window
int left = 0, maxLength = 0;
for (int right = 0; right < s.length(); right++) {
    // 1. Add s.charAt(right) to window state
    while (/* window condition violated */) {
        // 2. Remove s.charAt(left) from window state
        left++;
    }
    maxLength = Math.max(maxLength, right - left + 1);
}
```

---

### 2.3 Paradigm 3: Binary Search (Index & Answer Space Bisection)
- **Primary Triggers**: Searching sorted elements, finding the "minimum $X$ such that condition holds", "maximum capacity / allocation / time".
- **The "Answer Space" Invariant**: If feasibility function `isValid(mid)` is monotonic ($F, F, F, T, T, T$), bisect the continuous value range $[ \text{low}, \text{high} ]$ in $\mathcal{O}(\log(\text{range}))$.

```java
// Scaffold: Binary Search on Answer Space (Find First True)
long low = minPossible, high = maxPossible, ans = -1;
while (low <= high) {
    long mid = low + (high - low) / 2;
    if (isValid(mid)) {
        ans = mid;
        high = mid - 1; // Try to find an even smaller valid answer
    } else {
        low = mid + 1;  // Increase search boundary
    }
}
```

---

### 2.4 Paradigm 4: Sorting & Selection (Quickselect, Inversions, Cyclic Sort)
- **Primary Triggers**: "Find $K$-th largest/smallest" ($\mathcal{O}(N)$ Quickselect), counting inversions ($\mathcal{O}(N \log N)$ MergeSort), values in range $1 \dots N$ with duplicates/missing ($\mathcal{O}(N)$ Cyclic Sort).
- **Cyclic Sort Invariant**: Every number $X \in [1, N]$ belongs at index $X - 1$. Swap until `arr[i] == arr[arr[i] - 1]`.

```java
// Scaffold: Cyclic Sort in O(1) Space
int i = 0;
while (i < nums.length) {
    int correctIdx = nums[i] - 1;
    if (nums[i] > 0 && nums[i] <= nums.length && nums[i] != nums[correctIdx]) {
        int temp = nums[i];
        nums[i] = nums[correctIdx];
        nums[correctIdx] = temp;
    } else {
        i++;
    }
}
```

---

### 2.5 Paradigm 5: Graph Traversals & Connectivity (BFS, 0-1 BFS, Topo Sort)
- **BFS Trigger**: Shortest path on unweighted graphs, level-by-level exploration.
- **0-1 BFS Trigger**: Edges have weights of only $0$ or $1$ (use a `Deque`: push weight 0 to front, weight 1 to back for $\mathcal{O}(V + E)$ Dijkstra replacement).
- **Topological Sort Trigger**: Directed task dependencies, circular dependency detection (Kahn's in-degree queue).

```java
// Scaffold: Kahn's Algorithm for Topological Sort
Queue<Integer> queue = new ArrayDeque<>();
for (int i = 0; i < n; i++) {
    if (inDegree[i] == 0) queue.offer(i);
}
int processed = 0;
while (!queue.isEmpty()) {
    int u = queue.poll();
    processed++;
    for (int v : adj.get(u)) {
        if (--inDegree[v] == 0) queue.offer(v);
    }
}
boolean hasCycle = (processed != n);
```

---

### 2.6 Paradigm 6: Shortest Paths & Minimum Spanning Trees (MST)
- **Dijkstra**: Non-negative edge weights; greedy priority queue relaxation in $\mathcal{O}(E \log V)$.
- **Bellman-Ford**: Negative edge weights; detects negative cycles in $\mathcal{O}(V \cdot E)$.
- **Floyd-Warshall**: All-pairs shortest path in $\mathcal{O}(V^3)$ (for $V \le 400$).
- **Kruskal's MST**: Sort edges by weight, add edge if vertices belong to different DSU components in $\mathcal{O}(E \log E)$.

---

### 2.7 Paradigm 7: Advanced Graph Algorithms (Tarjan's SCC & Bridges)
- **Bridges & Articulation Points**: DFS tracking `discoveryTime[u]` and `lowLink[u]`. An edge $(u, v)$ is a critical bridge if and only if:
  $$\text{lowLink}[v] > \text{discoveryTime}[u]$$
- **Strongly Connected Components (SCC)**: Compresses cyclic directed graphs into a Directed Acyclic Graph (DAG) using Tarjan's stack in $\mathcal{O}(V + E)$.

---

### 2.8 Paradigm 8: Dynamic Programming (The Full Spectrum)

```
╭───────────────────────────────────────────────────────────────────────────────────────────╮
│                                 THE 8 CANONICAL DP PATTERNS                               │
├───────────────────────────────────────────────────────────────────────────────────────────┤
│ 1. 1D Linear DP: dp[i] depends on dp[i-1], dp[i-2] (Fibonacci, House Robber, Jump Game).  │
│ 2. 0/1 & Unbounded Knapsack: dp[w] = max value selecting subsets under weight limit.      │
│ 3. 2D Sequence Alignment: dp[i][j] matching s1[i] and s2[j] (LCS, Edit Distance).         │
│ 4. Longest Increasing Subsequence: dp[i] or Patience Sort via Binary Search in O(N log N).│
│ 5. Interval DP: dp[i][j] solving subproblems across range [i, j] (Burst Balloons).        │
│ 6. Tree DP: dp[u] bubbling subtree states to parent (Diameter, Vertex Cover, Cameras).     │
│ 7. Bitmask DP: dp[mask][u] where mask represents visited subset of items (TSP, N <= 20).  │
│ 8. State-Machine DP: dp[i][state] modeling transitions (Stock with cooldown / fees).      │
╰───────────────────────────────────────────────────────────────────────────────────────────╯
```

---

### 2.9 Paradigm 9: Greedy Algorithms & Exchange Arguments
- **When Greedy Works**: The problem exhibits the **Greedy Choice Property** (a globally optimal solution can be arrived at by making locally optimal choices) and **Optimal Substructure**.
- **The Exchange Argument Proof**: Assume an optimal solution $OPT$ differs from greedy solution $G$. Show that exchanging $OPT$'s first difference with $G$'s choice preserves or improves optimality without violating constraints!

---

### 2.10 Paradigm 10: Backtracking & Dynamic Pruning
- **Primary Triggers**: "Generate all valid permutations / combinations / subsets", constraint satisfaction (Sudoku, N-Queens).
- **Pruning Rule**: Check validity *before* recursing, not after. If a path violates constraints, backtrack immediately!

---

### 2.11 Paradigm 11: Divide & Conquer
- **Primary Triggers**: Problem can be split into non-overlapping independent subproblems whose solutions combine efficiently.
- **Canonical Examples**: MergeSort, Fast Exponentiation ($a^b = (a^{b/2})^2$ in $\mathcal{O}(\log b)$), Closest Pair of 2D Points in $\mathcal{O}(N \log N)$.

---

### 2.12 Paradigm 12: Bit Manipulation & Bitmasks
- **Essential Bitwise Identities**:
  - `x & (x - 1)`: Erases the lowest set bit (Power of 2 check: `(x & (x - 1)) == 0`).
  - `x & (-x)`: Isolates the lowest set bit (Fenwick Tree indexing).
  - `x ^ x = 0` and `x ^ 0 = x`: XOR cancellation (finding single unique number).
  - Iterating all submasks of a mask $M$: `for (int sub = M; sub > 0; sub = (sub - 1) & M)`.

---

### 2.13 Paradigm 13: String Matching & Mathematics
- **KMP Algorithm**: Computes longest proper prefix-suffix array (`LPS` $\pi$) in $\mathcal{O}(N)$ to avoid backtracking on pattern mismatches.
- **Rabin-Karp**: Rolling polynomial hash: $H = (H \cdot B + c) \pmod M$ in $\mathcal{O}(1)$ sliding window updates.
- **Number Theory**: Euclidean GCD (`gcd(a, b) = gcd(b, a % b)`), Sieve of Eratosthenes ($O(N \log \log N)$), Fermat's Little Theorem for modular inverse ($a^{-1} \equiv a^{P-2} \pmod P$).

---

## 3. Interactive Pattern-Matching Drills

<details>
<summary><strong>🔍 Drill 1: "Given a 2D grid where 0 is water and 1 is land, find the minimum days until island 1 connects to island 2"</strong></summary>

> **Optimal Paradigm**: **Multi-Source Breadth-First Search (BFS)**
> - **Step 1**: Identify island 1 using DFS and enqueue all of its land cells into a BFS queue with distance 0.
> - **Step 2**: Perform radial BFS expansion outward across water cells (level by level).
> - **Step 3**: The very first time the BFS reaches a cell belonging to island 2, the current level depth is the strictly minimum days/bridge length!
> - **Complexity**: $\mathcal{O}(R \cdot C)$ time and space.
</details>

<details>
<summary><strong>🔍 Drill 2: "Find the longest substring containing at most K distinct characters"</strong></summary>

> **Optimal Paradigm**: **Dynamic Sliding Window with Frequency Map**
> - **Trigger Clues**: "Longest substring", "at most K distinct".
> - **Mechanism**: Expand right pointer, adding `char` to `Map<Character, Integer>`. When `map.size() > K`, shrink left pointer, decrementing counts and removing keys reaching zero.
> - **Complexity**: $\mathcal{O}(N)$ time, $\mathcal{O}(K)$ space.
</details>

<details>
<summary><strong>🔍 Drill 3: "Find the length of the longest increasing subsequence in an array of N = 100,000 integers"</strong></summary>

> **Optimal Paradigm**: **Patience Sorting via Binary Search (LIS)**
> - **Why Naive DP Fails**: $O(N^2)$ DP takes $(10^5)^2 = 10^{10} \implies$ TLE.
> - **The "Aha!" Insight**:
>   - Maintain an array `tails` where `tails[i]` stores the smallest tail of all increasing subsequences of length $i+1$.
>   - `tails` is guaranteed to be strictly sorted!
>   - For each number $X$, binary search its insertion point in `tails` in $\mathcal{O}(\log N)$.
>   - Total Runtime: $\mathcal{O}(N \log N)$.
</details>

---

<table width="100%">
  <tr>
    <td width="33%" align="left">
      <a href="./02-master-data-structures-selection-and-trade-offs.md">
        <strong>← Previous Module</strong><br>
        02. Data Structures Selection & Trade-Offs
      </a>
    </td>
    <td width="33%" align="center">
      <a href="./README.md">
        <strong>Track Hub</strong><br>
        Strategies Navigation
      </a>
    </td>
    <td width="33%" align="right">
      <a href="./04-the-bud-optimization-framework.md">
        <strong>Next Module →</strong><br>
        04. The BUD Optimization Framework
      </a>
    </td>
  </tr>
</table>
