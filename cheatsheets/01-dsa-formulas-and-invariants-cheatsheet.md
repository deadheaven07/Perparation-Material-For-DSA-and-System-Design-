# 01. DSA Formulas, Complexity Bounds & Invariants Cheatsheet

[← Cheatsheets Hub](./README.md) | [Cheatsheets Hub](./README.md) | [Next: System Design Numbers & Math →](./02-system-design-numbers-and-capacity-math.md)

---

## 1. Master Data Structures Complexity Matrix

| Data Structure | Access (Avg / Worst) | Search (Avg / Worst) | Insertion (Avg / Worst) | Deletion (Avg / Worst) | Space |
| :--- | :---: | :---: | :---: | :---: | :---: |
| **Array (Contiguous)** | $\mathcal{O}(1) / \mathcal{O}(1)$ | $\mathcal{O}(N) / \mathcal{O}(N)$ | $\mathcal{O}(N) / \mathcal{O}(N)$ | $\mathcal{O}(N) / \mathcal{O}(N)$ | $\mathcal{O}(N)$ |
| **Singly Linked List** | $\mathcal{O}(N) / \mathcal{O}(N)$ | $\mathcal{O}(N) / \mathcal{O}(N)$ | $\mathcal{O}(1) / \mathcal{O}(1)^*$ | $\mathcal{O}(1) / \mathcal{O}(1)^*$ | $\mathcal{O}(N)$ |
| **Stack / Queue (`ArrayDeque`)**| $\mathcal{O}(1) / \mathcal{O}(1)$ | $\mathcal{O}(N) / \mathcal{O}(N)$ | $\mathcal{O}(1) / \mathcal{O}(1)$ | $\mathcal{O}(1) / \mathcal{O}(1)$ | $\mathcal{O}(N)$ |
| **Binary Search Tree (Unbalanced)**| $\mathcal{O}(\log N) / \mathcal{O}(N)$| $\mathcal{O}(\log N) / \mathcal{O}(N)$| $\mathcal{O}(\log N) / \mathcal{O}(N)$| $\mathcal{O}(\log N) / \mathcal{O}(N)$| $\mathcal{O}(N)$ |
| **AVL / Red-Black Tree** | $\mathcal{O}(\log N) / \mathcal{O}(\log N)$| $\mathcal{O}(\log N) / \mathcal{O}(\log N)$| $\mathcal{O}(\log N) / \mathcal{O}(\log N)$| $\mathcal{O}(\log N) / \mathcal{O}(\log N)$| $\mathcal{O}(N)$ |
| **Binary Heap (`PriorityQueue`)**| $\mathcal{O}(1) \text{ peek}$ | $\mathcal{O}(N)$ | $\mathcal{O}(\log N)$ | $\mathcal{O}(\log N) \text{ poll}$ | $\mathcal{O}(N)$ |
| **Hash Table (`HashMap`)** | $\text{N/A}$ | $\mathcal{O}(1) / \mathcal{O}(N)$ | $\mathcal{O}(1) / \mathcal{O}(N)$ | $\mathcal{O}(1) / \mathcal{O}(N)$ | $\mathcal{O}(N)$ |
| **Trie (Prefix Tree)** | $\mathcal{O}(L)$ | $\mathcal{O}(L)$ | $\mathcal{O}(L)$ | $\mathcal{O}(L)$ | $\mathcal{O}(\Sigma \cdot L \cdot N)$ |
| **Disjoint Set Union (DSU)** | $\text{N/A}$ | $\mathcal{O}(\alpha(N)) \text{ find}$ | $\mathcal{O}(\alpha(N)) \text{ union}$| $\text{N/A}$ | $\mathcal{O}(N)$ |
| **Segment Tree** | $\mathcal{O}(\log N) \text{ query}$| $\mathcal{O}(\log N)$ | $\mathcal{O}(\log N) \text{ update}$| $\mathcal{O}(\log N)$ | $\mathcal{O}(4N)$ |

*\* Insertion/Deletion in Linked List is $\mathcal{O}(1)$ given direct pointer/iterator to node.*

---

## 2. Master Theorem & Recurrence Quick Reference

For recurrences of the form: $T(N) = a T\left(\frac{N}{b}\right) + \mathcal{O}(N^d)$

| Case Condition | Asymptotic Complexity | Classic Canonical Problem |
| :--- | :---: | :--- |
| **$\log_b a > d$** (Tree leaves dominate) | $\mathcal{O}(N^{\log_b a})$ | Karatsuba Multiplication: $T(N) = 3T(N/2) + \mathcal{O}(N) \implies \mathcal{O}(N^{1.585})$ |
| **$\log_b a = d$** (Balanced across tree levels) | $\mathcal{O}(N^d \log N)$ | Merge Sort: $T(N) = 2T(N/2) + \mathcal{O}(N) \implies \mathcal{O}(N \log N)$ |
| **$\log_b a < d$** (Root level dominates) | $\mathcal{O}(N^d)$ | Binary Search: $T(N) = T(N/2) + \mathcal{O}(1) \implies \mathcal{O}(\log N)$ |

---

## 3. Bitwise Manipulation Formula Sheet

| Bitwise Operation | Syntax / Expression | Explanation & Use Case |
| :--- | :--- | :--- |
| **Isolate Lowest Set Bit** | `x & (-x)` | Extracts the lowest set 1-bit (Fenwick Tree LSB step). |
| **Clear Lowest Set Bit** | `x & (x - 1)` | Clears the rightmost 1-bit. Brian Kernighan's bit-count. |
| **Check Power of Two** | `(x > 0) && ((x & (x - 1)) == 0)` | True if integer $x$ is a non-zero power of 2. |
| **Swap Without Temporary Variable** | `a ^= b; b ^= a; a ^= b;` | Swaps two numbers in-place via XOR self-cancellation. |
| **Multiply / Divide by $2^k$** | `x << k` / `x >> k` | Fast arithmetic shifts (`x >>> k` for unsigned). |
| **Iterate All Submasks of Mask** | `for (int s = m; s > 0; s = (s - 1) & m)` | Iterates all $2^k$ submasks in strict descending order. |
| **Check $k$-th Bit** | `((x >> k) & 1) == 1` | Checks if $k$-th bit is active. |
| **Toggle $k$-th Bit** | `x ^ (1 << k)` | Inverts $k$-th bit. |

---

## 4. Tree & Graph Fundamental Formulas

1. **Handshaking Lemma:**
   $$\sum_{v \in V} \deg(v) = 2|E|$$
   *Consequence:* The number of vertices with odd degree in any undirected graph is always **even**.
2. **Euler's Formula (Planar Graphs):**
   $$V - E + F = 2$$
3. **Full Binary Tree Property:**
   A full binary tree with $I$ internal nodes has exactly:
   $$L = I + 1 \text{ leaves and } N = 2I + 1 \text{ total nodes}$$
4. **Binary Heap Height Bounds:**
   A binary heap of size $N$ has height $h = \lfloor \log_2 N \rfloor$.
   $\mathcal{O}(N)$ Heap Construction Proof: $\sum_{h=0}^\infty \frac{h}{2^h} = 2 \implies \text{BuildHeap takes } \mathcal{O}(N) \text{ time, NOT } \mathcal{O}(N \log N)$!

---

## 5. The 6 Universal Algorithmic Invariants

1. **Monotonicity Invariant:** If $P(x)$ is true $\implies P(x+1)$ is true, use **Binary Search on Answer Space**.
2. **Conservation Invariant:** If $\sum \text{elements}$ is constant, track running net sums or prefix balances.
3. **Pigeonhole Invariant:** If $N+1$ items occupy $N$ slots, at least one slot contains $\ge 2$ items (Floyd's Cycle Detection on duplicate numbers).
4. **Topological Order Invariant:** A Directed Acyclic Graph (DAG) always has at least one source (in-degree 0) and one sink (out-degree 0).
5. **Parity Invariant:** Bipartite graphs contain zero odd-length cycles.
6. **Exchange Argument Invariant:** In greedy proofs, swapping an adjacent inversion cannot improve the optimal schedule.

---

<div align="center">

| [← Cheatsheets Hub](./README.md) | [Track Hub: Cheatsheets](./README.md) | [Next: System Design Numbers & Math →](./02-system-design-numbers-and-capacity-math.md) |
| :--- | :---: | ---: |

</div>
