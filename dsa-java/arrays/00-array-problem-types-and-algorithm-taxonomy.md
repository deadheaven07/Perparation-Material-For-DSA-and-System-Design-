# Page 00: Array Problem Types & Algorithm Taxonomy

Before writing code or memorizing algorithms, you must develop the ability to classify any array problem into its underlying **mathematical archetype**. Once an archetype is identified, the corresponding algorithm, time complexity target, and space constraints follow naturally.

---

## 1. The Critical Distinction: Subarray vs. Subsequence vs. Subset

A common source of confusion in interview problem statements is the exact definition of array element groupings:

```text
Given Array: A = [1, 2, 3, 4]

╭─────────────────────────────────────────────────────────────────────────────╮
│ 1. Subarray (Contiguous & Order-Preserved)                                  │
│    • A contiguous slice of elements: [2, 3], [1, 2, 3], [4]                │
│    • Non-example: [1, 3] is NOT a subarray (not contiguous)                 │
│    • Total possible non-empty subarrays for size N: N * (N + 1) / 2 = O(N²) │
├─────────────────────────────────────────────────────────────────────────────┤
│ 2. Subsequence (Non-Contiguous & Order-Preserved)                           │
│    • Derived by deleting zero or more elements without changing relative    │
│      order: [1, 3], [1, 2, 4], [2, 4]                                       │
│    • Non-example: [3, 1] is NOT a subsequence (relative order is inverted)  │
│    • Total possible non-empty subsequences for size N: 2^N - 1 = O(2^N)      │
├─────────────────────────────────────────────────────────────────────────────┤
│ 3. Subset (Non-Contiguous & Order-Independent)                              │
│    • Any mathematical selection of elements regardless of order: {3, 1},    │
│      {4, 2, 1}. Sorting the original array does NOT invalidate subsets!    │
│    • Total possible subsets for size N: 2^N                                 │
╰─────────────────────────────────────────────────────────────────────────────╯
```

> [!IMPORTANT]
> - If a problem asks about **subarrays**, sorting the array is strictly **forbidden** unless you record original indices, because sorting destroys element contiguity.
> - If a problem asks about **subsets** or **pairs/triplets satisfying an algebraic relation**, sorting is almost always the optimal first step ($O(N \log N)$) because order does not matter!

---

## 2. The 8 Fundamental Array Problem Archetypes

Every array question asked in technical interviews falls into one of these eight archetypes:

```text
                                Array Problem Landscape
                                           │
       ┌───────────────┬───────────────────┼───────────────────┬──────────────┐
       ▼               ▼                   ▼                   ▼              ▼
[1. Contiguous] [2. Subsequences]   [3. In-Place]       [4. Search &]   [5. Multi-Array]
 [Subarrays]     [& Combinations]    [Mutations]         [Lookup]        [Coordination]
       │                                                                      │
       └───────────────────────────┬──────────────────────────────────────────┘
                                   │
                     ┌─────────────┴─────────────┐
                     ▼                           ▼
              [6. 2D Matrix &]            [7. 3D & State]       [8. Streaming &]
              [Grid Traversals]           [Space Exploration]   [Massive Scale]
```

### Archetype 1: Contiguous Subarrays
- **Characteristics**: "Find the longest subarray with sum $\le K$", "Find the minimum length subarray with sum $\ge S$", "Count subarrays with exact sum $K$", "Find the maximum subarray sum".
- **Trigger Questions**:
  - Are all numbers strictly positive? $\rightarrow$ **Two Pointers / Sliding Window** ($O(N)$ time, $O(1)$ space).
  - Can numbers be negative or zero? $\rightarrow$ Monotonicity breaks. Sliding window fails! $\rightarrow$ **Prefix Sum + HashMap** ($O(N)$ time, $O(N)$ space).
  - Is it asking for maximum sum among all subarrays? $\rightarrow$ **Kadane's Algorithm** ($O(N)$ time, $O(1)$ space).

### Archetype 2: Non-Contiguous Subsequences & Subsets
- **Characteristics**: "Find the length of the longest increasing subsequence", "Count subsets that sum to $K$", "Partition array into two equal sum subsets".
- **Solving Paradigms**:
  - If $N \le 20$: **Backtracking / Bitmask DP** ($O(2^N)$).
  - If $N \le 2500$: **Dynamic Programming** (1D/2D DP, Knapsack) ($O(N^2)$ or $O(N \cdot W)$).
  - If $N \le 10^5$: **Patience Sorting with Binary Search** ($O(N \log N)$ for Longest Increasing Subsequence).

### Archetype 3: In-Place Mutations & Rearrangements
- **Characteristics**: Given $O(1)$ auxiliary space constraint. "Sort array containing 0s, 1s, and 2s", "Find next lexicographical permutation", "Rotate array by $K$ steps", "Find the first missing positive number".
- **Solving Paradigms**:
  - 3-way partitioning: **Dutch National Flag Algorithm** (low, mid, high pointers).
  - Lexicographical sequences: **Pivot finding $\rightarrow$ Successor swap $\rightarrow$ Suffix reversal**.
  - Bounded values $[1 \dots N]$: **Cyclic Sort** (place each element $X$ at index $X-1$).
  - In-place tracking without extra memory: **State encoding via sign negation or modulo math**.

### Archetype 4: Search & Element Lookup
- **Characteristics**: "Find pair with given sum", "Find peak element", "Search in rotated sorted array", "Find $K$-th largest element".
- **Solving Paradigms**:
  - Sorted array: **Two Pointers** (opposite ends) or **Binary Search** ($O(\log N)$).
  - Rotated sorted array: **Modified Binary Search** (identify which half is sorted).
  - Unsorted $K$-th element: **Quickselect** ($O(N)$ average) or **Min-Heap** ($O(N \log K)$).

### Archetype 5: Multi-Array Coordination
- **Characteristics**: "Merge two sorted arrays", "Intersection of two arrays", "Find median of two sorted arrays", "Merge overlapping intervals".
- **Solving Paradigms**:
  - Merging from backwards to avoid overwrites ($O(M + N)$).
  - Interval processing: **Sort by start time $\rightarrow$ Linear sweep with running end boundary**.
  - Median of two sorted arrays: **Binary Search on the partition of the smaller array** ($O(\log(\min(M, N)))$).

### Archetype 6: 2D Matrix & Grid Problems
- **Characteristics**: "Rotate matrix $90^\circ$ clockwise", "Spiral traversal", "Set matrix zeroes in-place", "Search in a row-wise and column-wise sorted matrix", "2D range sum query".
- **Solving Paradigms**:
  - In-place rotation: **Transpose along main diagonal $\rightarrow$ Reverse each row**.
  - Row & column sorted matrix: **Saddleback search** starting at top-right corner $(0, \text{cols}-1)$ in $O(M + N)$.
  - 2D Range Queries: **Inclusion-Exclusion Principle 2D Prefix Sum**.

### Archetype 7: 3D & Multi-Dimensional State Traversal
- **Characteristics**: "Shortest path in a grid with $K$ obstacle eliminations", "Two robots collecting cherries moving simultaneously", "3D voxel spatial connectivity".
- **Solving Paradigms**:
  - State space search: **3D BFS** with state tuple `(row, col, remaining_obstacles)`.
  - Multi-agent grid transitions: **3D Dynamic Programming** `dp[row][col1][col2]`.

### Archetype 8: Streaming & Massive Scale Arrays
- **Characteristics**: "Find sliding window maximum of size $K$", "Select $K$ random elements from an infinite stream", "Sort a 500 GB array on a machine with 8 GB RAM".
- **Solving Paradigms**:
  - Sliding window extrema: **Monotonic Deque** ($O(N)$ amortized time).
  - Infinite streaming uniform sampling: **Reservoir Sampling** ($O(N)$ time, $O(K)$ space).
  - Memory-constrained sorting: **External K-Way Merge Sort** with chunked disk buffers.

---

## 3. The Master Problem-to-Algorithm Mapping Matrix

Use this matrix to instantly narrow down candidate approaches based on problem clues:

| Problem Clue / Signal Phrase | Optimal Pattern / Algorithm | Fallback / Alternative | Time Complexity | Auxiliary Space |
| :--- | :--- | :--- | :---: | :---: |
| **Sorted array, find pair / triplet target** | **Two Pointers (Opposite Ends)** | Hash Set / Map | $O(N)$ / $O(N^2)$ | $O(1)$ |
| **Contiguous subarray, all positive, target sum** | **Sliding Window (Dynamic)** | Prefix Sum + Binary Search | $O(N)$ | $O(1)$ |
| **Contiguous subarray, includes negatives, sum $K$** | **Prefix Sum + HashMap** | *Two pointers fails!* | $O(N)$ | $O(N)$ |
| **Maximum contiguous subarray sum** | **Kadane's Algorithm** | Divide & Conquer | $O(N)$ | $O(1)$ |
| **Maximum contiguous subarray product** | **State Machine DP (Min & Max)** | Prefix/Suffix Scan | $O(N)$ | $O(1)$ |
| **Static Range Sum Queries (1D / 2D)** | **Prefix Sum Array** | Segment Tree (overkill) | $O(1)$ per query | $O(N)$ / $O(MN)$ |
| **Dynamic Range Sum Queries (with updates)** | **Fenwick Tree (BIT)** | Segment Tree | $O(\log N)$ per query | $O(N)$ |
| **Values in $[1 \dots N]$, find missing/duplicate** | **Cyclic Sort** | Negative-sign marking | $O(N)$ | $O(1)$ |
| **Nearest greater / smaller element** | **Monotonic Stack** | Two-pass scan | $O(N)$ | $O(N)$ |
| **Maximum / minimum in sliding window $K$** | **Monotonic Deque (`ArrayDeque`)** | PriorityQueue ($O(N \log K)$)| $O(N)$ | $O(K)$ |
| **Minimize the maximum / Maximize the minimum** | **Binary Search on Answer Space** | Greedy feasibility check | $O(N \log(\text{range}))$ | $O(1)$ |
| **Row-wise & Column-wise sorted matrix** | **Saddleback Search (Top-Right)** | Binary Search per row | $O(M + N)$ | $O(1)$ |
| **Grid path with $K$ obstacle eliminations** | **3D BFS (Queue with `(r, c, k)`)** | Dijkstra ($O(MNK \log(MNK))$) | $O(M \cdot N \cdot K)$ | $O(M \cdot N \cdot K)$ |
| **Two entities moving in grid simultaneously** | **3D DP (`dp[r][c1][c2]`)** | Memoized recursion | $O(R \cdot C^2)$ | $O(C^2)$ |
| **Uniform random sample from stream of unknown size**| **Reservoir Sampling** | Store entire stream (OOM) | $O(N)$ | $O(K)$ |

---

## 4. Input Size Constraints: The Mathematical Clue

During technical interviews, the problem constraints on input size $N$ mathematically dictate which algorithms are feasible without encountering a **Time Limit Exceeded (TLE)** error. Standard interview evaluation environments execute approximately **$10^7$ to $10^8$ operations per second**:

```text
╭─────────────────────────────────────────────────────────────────────────────╮
│                   INPUT SIZE CONSTRAINT TO ALGORITHM MAP                    │
├─────────────────┬───────────────────────────────┬───────────────────────────┤
│ Input Size (N)  │ Target Time Complexity        │ Typical Feasible Approach │
├─────────────────┼───────────────────────────────┼───────────────────────────┤
│ N ≤ 10 – 12     │ O(N!) or O(N² · 2^N)          │ Full Permutations / TSP   │
│ N ≤ 20 – 25     │ O(2^N) or O(N · 2^N)          │ Backtracking / Bitmask DP │
│ N ≤ 100         │ O(N⁴) or O(N³)                │ Floyd-Warshall / 3D DP    │
│ N ≤ 500 – 1,000 │ O(N²)                         │ 2D Matrix DP / 2-Loops    │
│ N ≤ 10,000      │ O(N²), O(N√N)                 │ Square Root Decomposition │
│ N ≤ 100,000     │ O(N log N) or O(N)            │ Sorting / Heap / Trees    │
│ N ≤ 1,000,000   │ O(N)                          │ Two Pointers / Hash / BIT │
│ N > 10^7        │ O(log N) or O(1)              │ Binary Search / Math      │
╰─────────────────┴───────────────────────────────┴───────────────────────────╯
```

> [!TIP]
> If $N = 10^5$, an $O(N^2)$ algorithm requires $(10^5)^2 = 10^{10}$ operations, which will instantly TLE (taking $\approx 100$ seconds). You **must** design an $O(N \log N)$ or $O(N)$ solution!

---

## 5. Self-Check & Active Recall

Test your pattern recognition reflexes:

1. **Q**: You are asked to find the maximum length contiguous subarray with an equal number of $0$s and $1$s. What archetype and algorithm is this?
   - *A*: **Archetype 1 (Contiguous Subarray)**. Transform $0 \rightarrow -1$. The problem becomes finding the longest subarray with sum $= 0$. Since the array now contains negative numbers, use **Prefix Sum + HashMap** storing the earliest index of each running sum ($O(N)$ time, $O(N)$ space).

2. **Q**: Why can't we use a two-pointer sliding window to solve "Subarray Sum Equals $K$" when the array contains negative numbers?
   - *A*: Sliding window relies on **monotonicity** (expanding the right pointer strictly increases or preserves the sum, contracting the left pointer strictly decreases it). With negative numbers, expanding the window can decrease the sum and contracting it can increase it, breaking the window invariant.

3. **Q**: An array of size $N$ contains numbers from $1$ to $N$. Exactly one number is duplicated and one is missing. Space limit is $O(1)$. Which pattern applies?
   - *A*: **Archetype 3 (In-Place Mutation / Cyclic Sort)**. Swap each number `A[i]` to its correct target index `A[i] - 1`. The index that fails to hold `index + 1` reveals both the duplicate and missing values in $O(N)$ time and $O(1)$ auxiliary space.

---

## 🧭 Continue Learning

| ◀️ Previous Topic | 🧭 Track Hub | Next Topic ▶️ |
| :--- | :---: | ---: |
| [**Track Overview**](README.md)<br><sub>*Arrays Roadmap & Decision Tree*</sub> | [**Arrays Index**](README.md)<br><sub>*All 9 Modules*</sub> | [**Page 01: 1D Arrays & Memory**](01-array-fundamentals-and-memory-architecture.md)<br><sub>*JVM Heap, Cache Lines & ArrayList*</sub> |
