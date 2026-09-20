# 🌋 Advanced Problems: Trees, Heaps, Graphs & Dynamic Programming

[Java Track Home](../README.md) | [Linear Advanced Track](../advanced-problems-arrays-and-linked-lists/README.md) | [Next: Trapping Rain Water II →](./01-trapping-rain-water-ii-3d-boundary-contraction.md)

---

## 🧭 Executive Overview: The Non-Linear Hard-Tier Landscape

In tier-1 technical interviews and high-scale distributed engineering, foundational single-paradigm problems rarely appear in isolation. The most challenging coding rounds evaluate **multi-concept synthesis**: problems where trees, priority queues, shortest-path graph traversals, and dynamic programming intersect.

This track represents the non-linear companion to [`advanced-problems-arrays-and-linked-lists/`](../advanced-problems-arrays-and-linked-lists/README.md). Every problem in this curriculum is classified as **Hard**, requiring multi-stage invariant proofs, optimal boundary contractions, state-space reduction, or dual-phase search architectures.

```
                      ╭────────────────────────────────────────╮
                      │      NON-LINEAR ADVANCED TAXONOMY      │
                      ╰───────────────────┬────────────────────╯
                                          │
                        What is the primary constraint challenge?
                                          │
            ┌───────────────────┬─────────┴─────────┬───────────────────┐
            ▼                   ▼                   ▼                   ▼
    BOUNDARY SHAPE        ALL OPTIMAL PATHS   MULTI-DIMENSIONAL   STREAMING MEDIANS
      INCURSION              EXPLOSION          MONOTONICITY       & CYCLIC GRAPHS
   2D Min-Heap + BFS   Bidirectional BFS +     Dual-Key Sort +    Lazy Hash Deletion
  (Trapping Rain II)     DAG Backtracking     Patience Sort LIS     & Graph Memo Maps
                         (Word Ladder II)    (Russian Envelopes)   (Median / SerDe)
```

---

## 🧮 1. Mathematical Invariants of Advanced Non-Linear Synthesis

### 1.1 The Watershed Boundary Invariant (3D Elevation Contraction)
For any 2D grid of elevations $H[r][c]$, the maximum water level $W[r][c]$ that can be retained at coordinate $(r, c)$ without spilling out is strictly bounded by the **minimum elevation along the entire surrounding boundary perimeter** that encloses $(r, c)$:

$$W[r][c] = \max(H[r][c], \min_{(u, v) \in \text{Boundary}} W[u][v])$$

By processing boundary cells inward using a **Min-PriorityQueue**, we guarantee that water levels propagate strictly monotonically from the lowest spilling breach inward.

### 1.2 The Dual-Key Sorting Invariant (Dimension Collapse)
When evaluating 2D intervals or pairs $[w_i, h_i]$ where both dimensions must strictly increase ($w_i < w_j$ and $h_i < h_j$), sorting by width ascending ($w_i \le w_j$) leaves equal widths ambiguous.
By sorting matching widths by height **descending** ($h_i \ge h_j$ when $w_i == w_j$), we make it impossible for two elements with the same width to form an increasing subsequence in height. This collapses a 2D constraint into a 1D Longest Increasing Subsequence (LIS) solvable in $\mathcal{O}(N \log N)$ via Patience Sorting!

---

## ⚡ 2. The Advanced Problem Catalog

| Module | Core Concepts & Highlights | Invariant Applied |
| :--- | :--- | :--- |
| **[01. Trapping Rain Water II](./01-trapping-rain-water-ii-3d-boundary-contraction.md)** | 3D surface water retention on an $M \times N$ matrix | Perimeter Min-Heap boundary shrink in $\mathcal{O}(M N \log(M N))$ |
| **[02. Word Ladder II](./02-word-ladder-ii-bidirectional-bfs-and-dag-backtracking.md)** | Extracting all shortest transformation sequences | Bidirectional BFS Predecessor DAG + Backtracking DFS |
| **[03. Russian Doll Envelopes](./03-russian-doll-envelopes-2d-sorting-and-patience-sort-lis.md)** | 2D nesting optimization | Dual-Key sorting ($w \uparrow, h \downarrow$) + Patience Sort $\mathcal{O}(N \log N)$ |
| **[04. Critical Connections & Biconnectivity](./04-critical-connections-and-network-biconnectivity.md)** | Network single points of failure & bridge extraction | Tarjan DFS Spanning Tree discovery ($low[v] > tin[u]$) |
| **[05. Sliding Window Median](./05-sliding-window-median-lazy-heap-vs-indexed-priority-queue.md)** | Real-time continuous distribution tracking across sliding window | Two-Heap Lazy Deletion via hash frequency maps in $\mathcal{O}(N \log K)$ |
| **[06. Serialize & Deserialize Cyclic Graphs](./06-serialize-and-deserialize-arbitrary-and-cyclic-graphs.md)** | Encoding arbitrary graphs with cycles and self-loops | Structural tokenization + visited object memoization maps |

---

## 🛠️ The 7-Step Problem Solving Framework

Every problem in this track adheres strictly to the universal 7-step engineering blueprint:
1. **Problem Statement & Constraints**: Exact input/output boundaries and scale budgets.
2. **Thought Process & Intuition**: Bottleneck identification, naive limitations, and the "Aha!" insight.
3. **Mathematical Invariant Proof**: Rigorous mathematical or structural correctness proof.
4. **Procedural Blueprint**: Mermaid flowchart outlining the exact procedural control flow.
5. **Visual State Transitions**: Curved Unicode box diagrams tracing pointer and heap transitions.
6. **Production Java 17/21 Implementation**: Complete, allocation-conscious, type-safe code.
7. **Step-by-Step Dry-Run & Stress Defenses**: Detailed trace tables and interviewer curveball defenses.

---

<div align="center">

| [← Back to Java Track Home](../README.md) | [Linear Advanced Track](../advanced-problems-arrays-and-linked-lists/README.md) | [Next: Trapping Rain Water II →](./01-trapping-rain-water-ii-3d-boundary-contraction.md) |
| :--- | :---: | ---: |

</div>
