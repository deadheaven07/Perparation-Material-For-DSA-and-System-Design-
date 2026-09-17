# 05. Space Optimization & In-Place Techniques

[← Back to BUD Optimization](./04-the-bud-optimization-framework.md) | [Track Hub](./README.md) | [Next: Edge-Case Defense →](./06-edge-cases-and-boundary-defense-playbook.md)

---

## 1. The Space Optimization Hierarchy

In technical interviews and production systems, cutting auxiliary space from $\mathcal{O}(N)$ to $\mathcal{O}(1)$ often distinguishes a standard senior candidate from a staff-level systems architect:

```
╭───────────────────────────────────────────────────────────────────────────────────────────╮
│                              THE 5 TIERS OF SPACE OPTIMIZATION                            │
├───────────────────────────────────────────────────────────────────────────────────────────┤
│ Tier 1: 2D Matrix DP -> 1D Rolling Array (O(M * N) -> O(min(M, N)))                       │
│ Tier 2: 1D Array DP -> Scalar Rolling Variables (O(N) -> O(1))                            │
│ Tier 3: In-Place Value Encoding (Negation Marking, Modulo Dual-Packing)                   │
│ Tier 4: Bitmask Compression (boolean[] -> primitive int / long)                          │
│ Tier 5: Structural Pointer Threading (Morris Tree Traversal in O(1) Space)                │
╰───────────────────────────────────────────────────────────────────────────────────────────╯
```

---

## 2. Technique 1: Dynamic Programming Rolling Arrays & State Compression

### Compressing 2D DP to 1D: The Knapsack / LCS Pattern

In 2D Dynamic Programming (Knapsack, Edit Distance, Longest Common Subsequence), `dp[i][w]` typically only depends on the previous row `dp[i-1]`:

```
Full 2D Matrix (Wasteful O(N * W) space):
Row 0: [ . . . . . . . . . . . . . . . . ] (Never accessed again!)
Row 1: [ . . . . . . . . . . . . . . . . ] (Never accessed again!)
Row 2: [ . . . . . . . . . . . . . . . . ] <-- Only Row i-1 is needed!
Row 3: [ . . . . . . . . . . . . . . . . ] <-- Computing current Row i!
```

```java
// 1. Full 2D Space: O(N * W)
int[][] dp = new int[n + 1][W + 1];
for (int i = 1; i <= n; i++) {
    for (int w = 1; w <= W; w++) {
        if (weights[i - 1] <= w) {
            dp[i][w] = Math.max(dp[i - 1][w], dp[i - 1][w - weights[i - 1]] + values[i - 1]);
        } else {
            dp[i][w] = dp[i - 1][w];
        }
    }
}

// 2. Optimized 1D Space: O(W) (Traversing backward avoids overwriting row i-1 values!)
int[] dp = new int[W + 1];
for (int i = 0; i < n; i++) {
    for (int w = W; w >= weights[i]; w--) {
        dp[w] = Math.max(dp[w], dp[w - weights[i]] + values[i]);
    }
}
```

---

## 3. Technique 2: In-Place Array Mutation & Modulo Packing

### In-Place Negation Marking (Values as Hash Table)

When an array of length $N$ contains values in the range $[1, N]$, you can use the **sign bit** of each array cell as a boolean visited flag without allocating any auxiliary memory!

```java
// Mark presence of value X by negating the value at index Math.abs(X) - 1:
for (int i = 0; i < nums.length; i++) {
    int targetIdx = Math.abs(nums[i]) - 1;
    if (nums[targetIdx] < 0) {
        // We have seen Math.abs(nums[i]) before! (Duplicate found!)
    } else {
        nums[targetIdx] = -nums[targetIdx]; // Mark visited
    }
}
```

---

### In-Place Modulo Packing: Storing Two Integers in One Cell

To store an old value $A$ and a new value $B$ simultaneously in cell `arr[i]` using modulus $K$ (where $K > \max(A, B)$):

$$\text{Encoded Cell} = A + B \cdot K$$

- Retrieve original value: $A = \text{Cell} \pmod K$
- Retrieve new updated value: $B = \lfloor \text{Cell} / K \rfloor$

```java
int K = 10000; // Chosen strictly greater than max possible element
for (int i = 0; i < n; i++) {
    int oldVal = arr[i] % K;
    int newVal = computeNewValue(arr, i);
    arr[i] = oldVal + (newVal % K) * K;
}

// Final decode pass:
for (int i = 0; i < n; i++) {
    arr[i] = arr[i] / K;
}
```

---

## 4. Technique 3: Bitmask State Compression

Instead of allocating a `boolean[32]` array or a `Set<Integer>` of size up to 32, store the entire state in a single **32-bit primitive `int`** (or 64-bit `long` for up to 64 elements):

```java
// Replace Set<Integer> with 32-bit int mask:
int mask = 0;

// Add element i to set:
mask |= (1 << i);

// Check if element i is in set:
boolean contains = (mask & (1 << i)) != 0;

// Remove element i from set:
mask &= ~(1 << i);

// Toggle presence of element i:
mask ^= (1 << i);
```

---

## 5. Interactive Space Optimization Drills

<details>
<summary><strong>🔍 Drill 1: "House Robber: Given houses with money, maximize loot without robbing two adjacent houses"</strong></summary>

> **Space Optimization Evolution**:
> - **Level 1 (Naive DP)**: `int[] dp = new int[N]`, where `dp[i] = Math.max(dp[i-1], dp[i-2] + nums[i])`. Space: $\mathcal{O}(N)$.
> - **Level 2 (Optimized $\mathcal{O}(1)$ Space)**: Notice `dp[i]` only ever references the previous two values!
> ```java
> int prev2 = 0, prev1 = 0;
> for (int num : nums) {
>     int current = Math.max(prev1, prev2 + num);
>     prev2 = prev1;
>     prev1 = current;
> }
> return prev1; // Strictly O(1) space!
> ```
</details>

<details>
<summary><strong>🔍 Drill 2: "Game of Life: Update an M x N board according to rules simultaneously in O(1) extra space"</strong></summary>

> **In-Place Bit Encoding Trick**:
> A cell only has 2 states: Dead (`0`) or Alive (`1`).
> - We can encode transitions using the 2nd bit:
>   - State `00` ($0$): Dead $\to$ Dead
>   - State `01` ($1$): Live $\to$ Dead
>   - State `10` ($2$): Dead $\to$ Live
>   - State `11` ($3$): Live $\to$ Live
> - Check past state: `board[r][c] & 1`.
> - Apply future state: `board[r][c] >>= 1`.
> - Space Complexity: Strictly $\mathcal{O}(1)$ without allocating a second matrix!
</details>

---

<table width="100%">
  <tr>
    <td width="33%" align="left">
      <a href="./04-the-bud-optimization-framework.md">
        <strong>← Previous Module</strong><br>
        04. The BUD Optimization Framework
      </a>
    </td>
    <td width="33%" align="center">
      <a href="./README.md">
        <strong>Track Hub</strong><br>
        Strategies Navigation
      </a>
    </td>
    <td width="33%" align="right">
      <a href="./06-edge-cases-and-boundary-defense-playbook.md">
        <strong>Next Module →</strong><br>
        06. Edge-Case Defense Playbook
      </a>
    </td>
  </tr>
</table>
