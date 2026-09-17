# 06. Edge-Case Defense Playbook: Zero-Defect Boundary Engineering

[← Back to Space Optimization](./05-space-optimization-and-in-place-techniques.md) | [Track Hub](./README.md) | [Next: Live Interview Execution →](./07-live-interview-execution-and-communication.md)

---

## 1. The Anatomy of Silent Algorithmic Bugs

In technical interviews and mission-critical systems, an algorithm that is theoretically optimal in Big-O will still receive an immediate rejection if it fails on edge cases. 

Over 90% of candidate failures stem from **six predictable boundary hazards**:

```
╭───────────────────────────────────────────────────────────────────────────────────────────╮
│                                THE 6 CRITICAL BOUNDARY HAZARDS                            │
├───────────────────────────────────────────────────────────────────────────────────────────┤
│ 1. Integer Overflow: Adding or multiplying large values beyond 32-bit limits.             │
│ 2. Off-by-One Traps: Misaligned inclusive vs. exclusive boundaries in loops and windows. │
│ 3. Empty & Degenerate Inputs: null, N=0, N=1, single-node trees, disconnected graphs.     │
│ 4. Duplicate Elements: Collapsing strict inequalities, infinite loops in Two Pointers.    │
│ 5. Negative Values: Modulo arithmetic errors, destroyed prefix sum monotonicity.          │
│ 6. Skewed Topologies: Tree degenerating into linked list -> StackOverflowError!           │
╰───────────────────────────────────────────────────────────────────────────────────────────╯
```

---

## 2. The Universal Edge-Case Checklist

Before declaring your code ready, mentally execute this defensive checklist:

| Category | Boundary Condition to Test | Catastrophic Failure Mode | Defensive Pattern / Code Fix |
| :--- | :--- | :--- | :--- |
| **Numeric** | `low + high` in Binary Search | Integer Overflow ($> 2 \times 10^9$) | `int mid = low + (high - low) / 2;` |
| **Numeric** | Product of two `int`s | Overflow before assignment | `long prod = (long) a * b;` |
| **Numeric** | `Math.abs(Integer.MIN_VALUE)` | Returns negative `-2147483648`! | Cast to `(long)` before taking absolute value |
| **Numeric** | Negative Modulo `(-1 % 5)` | Returns `-1` in Java, not `4`! | `int mod = (x % M + M) % M;` |
| **Collections** | Empty or single element | `NullPointerException`, `IndexOutOfBounds` | Explicit guard clause: `if (arr == null || arr.length == 0)` |
| **Duplicates** | Two Pointers with duplicates | Infinite loop or duplicate results | `while (l < r && arr[l] == arr[l+1]) l++;` |
| **Graphs** | Disconnected Components | Visiting only component 0 | Outer loop over all vertices: `for (int i = 0; i < V; i++)` |
| **Trees** | Skewed Tree ($N = 10^5$) | JVM Call Stack Overflow | Iterative BFS or Morris Traversal |

---

## 3. The 4 Defensive Coding Commandments

### Commandment 1: Guard Against Midpoint Overflow

```java
// DANGEROUS (Crashes when low + high > 2,147,483,647):
int mid = (low + high) / 2;

// BULLETPROOF (Never overflows signed 32-bit int):
int mid = low + (high - low) / 2;

// OR BITWISE UNSIGNED RIGHT SHIFT:
int mid = (low + high) >>> 1;
```

---

### Commandment 2: The Canonical Negative Modulo Formula

In Java and C++, the `%` operator is remainder, not true mathematical modulo. It preserves the sign of the dividend:
$$-1 \pmod 5 \implies -1 \text{ (Incorrect for cyclic hash index!)}$$

```java
// BULLETPROOF CYCLIC HASH INDEX:
int validIndex = ((val % M) + M) % M;
```

---

### Commandment 3: Sentinel (Dummy) Nodes for Linked Lists

Modifying linked list heads or tails creates ugly conditional branching for `head == null` or deleting the head node.

```java
// BULLETPROOF: Sentinel node eliminates all null-head branching
ListNode dummy = new ListNode(0);
dummy.next = head;
ListNode current = dummy;

// Perform deletions or insertions smoothly...

return dummy.next; // Head is safely preserved
```

---

## 4. Interactive "What Breaks This Code?" Stress Drills

Identify the edge-case bug in each snippet before expanding the solution:

<details>
<summary><strong>🔍 Drill 1: What breaks this Binary Search?</strong></summary>

```java
public int binarySearch(int[] nums, int target) {
    int low = 0, high = nums.length;
    while (low <= high) {
        int mid = (low + high) / 2;
        if (nums[mid] == target) return mid;
        if (nums[mid] < target) low = mid + 1;
        else high = mid - 1;
    }
    return -1;
}
```

> **The Bugs**:
> 1. `high = nums.length` with `while (low <= high)` causes `nums[mid]` to throw `ArrayIndexOutOfBoundsException` when `target > nums[last]`.
> 2. `(low + high) / 2` can overflow integer limits.
> **Defensive Fix**:
> Set `int high = nums.length - 1;` and `int mid = low + (high - low) / 2;`.
</details>

<details>
<summary><strong>🔍 Drill 2: What breaks this Graph Traversal?</strong></summary>

```java
public int countComponents(int n, int[][] edges) {
    List<List<Integer>> adj = buildAdj(n, edges);
    boolean[] visited = new boolean[n];
    dfs(0, adj, visited); // Traverse from node 0
    return 1;
}
```

> **The Bug**:
> - Assumes the graph is **connected** and rooted at node 0! If the graph has 3 disconnected islands, this code visits only island 0 and completely ignores the others!
> **Defensive Fix**:
> ```java
> int components = 0;
> for (int i = 0; i < n; i++) {
>     if (!visited[i]) {
>         components++;
>         dfs(i, adj, visited);
>     }
> }
> return components;
> ```
</details>

<details>
<summary><strong>🔍 Drill 3: What breaks this Subarray Product check?</strong></summary>

```java
// Check if product of any two numbers in array equals target:
public boolean hasProduct(int[] nums, int target) {
    Set<Integer> seen = new HashSet<>();
    for (int num : nums) {
        if (target % num == 0 && seen.contains(target / num)) {
            return true;
        }
        seen.add(num);
    }
    return false;
}
```

> **The Bug**:
> - If `num == 0`, `target % num` triggers an immediate **`ArithmeticException: / by zero`**!
> **Defensive Fix**:
> Explicitly handle `num == 0` before performing modulo or division:
> ```java
> if (num == 0) {
>     if (target == 0 && seen.contains(0)) return true;
>     seen.add(num);
>     continue;
> }
> ```
</details>

---

<table width="100%">
  <tr>
    <td width="33%" align="left">
      <a href="./05-space-optimization-and-in-place-techniques.md">
        <strong>← Previous Module</strong><br>
        05. Space Optimization & In-Place Techniques
      </a>
    </td>
    <td width="33%" align="center">
      <a href="./README.md">
        <strong>Track Hub</strong><br>
        Strategies Navigation
      </a>
    </td>
    <td width="33%" align="right">
      <a href="./07-live-interview-execution-and-communication.md">
        <strong>Next Module →</strong><br>
        07. Live Technical Interview Playbook
      </a>
    </td>
  </tr>
</table>
