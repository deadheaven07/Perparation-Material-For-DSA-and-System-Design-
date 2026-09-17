# 03. The Knapsack Family & Subset Sum Formulations

[← Back to Grid DP](./02-grid-and-multi-dimensional-dp.md) | [Track Hub](./README.md) | [Next: Strings, Sequences & Edit Distance →](./04-strings-sequences-and-edit-distance.md)

---

## 🏛️ 1. Theoretical Foundations: The Knapsack Taxonomy

The **Knapsack Problem** is the archetypal model for resource-constrained decision optimization. Given $N$ items, each with a weight $w_i$ and value $v_i$, and a total knapsack capacity $W$:

```
╭───────────────────────────────────────────────────────────────────────────────────────────╮
│                                0/1 VS. UNBOUNDED KNAPSACK                                 │
├─────────────────────┬─────────────────────────────────┬───────────────────────────────────┤
│ Property            │ 0/1 Knapsack                    │ Unbounded Knapsack                │
├─────────────────────┼─────────────────────────────────┼───────────────────────────────────┤
│ Item Availability   │ At most ONCE per item (0 or 1)  │ Infinite copies available (≥ 0)   │
│ 2D Recurrence       │ dp[i][w] = max(dp[i-1][w],      │ dp[i][w] = max(dp[i-1][w],        │
│                     │       dp[i-1][w - w_i] + v_i)   │       dp[i][w - w_i] + v_i)       │
│ 1D Array Loop Order │ BACKWARD: w from W down to w_i  │ FORWARD: w from w_i up to W       │
│ Why the Direction?  │ Uses values from PREVIOUS pass; │ Uses newly updated values from    │
│                     │ prevents multiple item uses!    │ CURRENT pass; allows reuse!       │
╰─────────────────────┴─────────────────────────────────┴───────────────────────────────────╯
```

---

## 🧮 2. Mathematical Proof of 1D Loop Direction Invariant

In 0/1 Knapsack, the subproblem state $dp[i][w]$ depends on $dp[i-1][w - w_i]$ (the previous item's result):

$$dp[i][w] = \max(dp[i-1][w], dp[i-1][w - w_i] + v_i)$$

When compressing this into a single 1D array `dp[w]`:
- If we iterate **Forward** ($w = w_i \to W$):
  `dp[w - w_i]` has ALREADY been updated in the current iteration! Referencing it incorporates item $i$ **multiple times** (solving Unbounded Knapsack!).
- If we iterate **Backward** ($w = W \to w_i$):
  When calculating `dp[w]`, the value `dp[w - w_i]` has NOT YET been updated in the current pass—it still holds the exact value from step $i - 1$!
  This mathematically guarantees that item $i$ is used at most **once** $\blacksquare$.

---

## 3. Problem 1: Partition Equal Subset Sum (0/1 Knapsack Reduction)

### 3.1 Problem Statement & Constraints

Given an integer array `nums`, return `true` if you can partition the array into two subsets such that the sum of the elements in both subsets is equal or `false` otherwise.

```
Example 1:
Input: nums = [1,5,11,5]
Output: true
Explanation: The array can be partitioned as [1, 5, 5] and [11].

Example 2:
Input: nums = [1,2,3,5]
Output: false
Explanation: Sum is 11 (odd). Cannot partition into equal integer halves.
```

#### Constraints:
- $1 \le \text{nums.length} \le 200$.
- $1 \le \text{nums}[i] \le 100$.

---

### 3.2 Thought Process & Mathematical Reduction

```
  Total Sum Invariant:
  Let TotalSum = sum(nums).
  If TotalSum is ODD:
  Two equal integer subsets are mathematically IMPOSSIBLE! Return false immediately!
                         ↓
  Reduction to 0/1 Knapsack:
  If TotalSum is EVEN, Target = TotalSum / 2.
  Problem reduces to: Does there exist a subset of nums with sum == Target?
                         ↓
  1D Backward State:
  dp[s] = true if a subset sum of s can be formed using a subset of elements seen so far.
  Base case: dp[0] = true.
  For each num in nums:
    For s from Target down to num:
      dp[s] = dp[s] || dp[s - num]
```

---

### 3.3 Production Java 17/21 Implementation

```java
public final class PartitionEqualSubsetSum {

    /**
     * Solves partition equal subset sum via 0/1 Knapsack with backward 1D sweep.
     *
     * Time Complexity:  O(N * Target) where Target = sum / 2.
     * Space Complexity: O(Target) using a 1D boolean array.
     */
    public boolean canPartition(int[] nums) {
        int totalSum = 0;
        for (int num : nums) {
            totalSum += num;
        }

        // Odd total sum can never be partitioned into two equal integers
        if ((totalSum & 1) != 0) {
            return false;
        }

        int target = totalSum / 2;
        boolean[] dp = new boolean[target + 1];
        dp[0] = true;

        for (int num : nums) {
            // Backward iteration prevents re-using the same number
            for (int s = target; s >= num; s--) {
                dp[s] = dp[s] || dp[s - num];
            }
            if (dp[target]) {
                return true; // Early termination optimization
            }
        }

        return dp[target];
    }
}
```

---

## 4. Problem 2: Target Sum (Algebraic Reduction to Subset Sum)

### 4.1 Problem Statement & Constraints

You are given an integer array `nums` and an integer `target`.

You want to build an expression out of `nums` by adding one of the symbols `'+'` and `'-'` before each integer in `nums` and then concatenate all the integers.

Return the number of different expressions that you can build, which evaluate to `target`.

```
Example 1:
Input: nums = [1,1,1,1,1], target = 3
Output: 5
Explanation: 5 ways to assign signs to make sum 3:
-1+1+1+1+1 = 3, +1-1+1+1+1 = 3, +1+1-1+1+1 = 3, +1+1+1-1+1 = 3, +1+1+1+1-1 = 3
```

#### Constraints:
- $1 \le \text{nums.length} \le 20$.
- $0 \le \text{nums}[i] \le 1000, \sum \text{nums}[i] \le 1000$.
- $-1000 \le \text{target} \le 1000$.

---

### 4.2 Mathematical Derivation

Partition `nums` into two sets: $P$ (numbers with `+`) and $N$ (numbers with `-`):
$$\sum P - \sum N = \text{target}$$

Add $\sum P + \sum N = \text{TotalSum}$ to both sides:
$$(\sum P - \sum N) + (\sum P + \sum N) = \text{target} + \text{TotalSum}$$
$$2 \sum P = \text{target} + \text{TotalSum}$$

$$\sum P = \frac{\text{target} + \text{TotalSum}}{2}$$

**Immediate Necessary Conditions**:
1. $\text{target} + \text{TotalSum}$ must be non-negative.
2. $\text{target} + \text{TotalSum}$ must be **even** ($(\text{target} + \text{TotalSum}) \bmod 2 == 0$).
3. $|\text{target}| \le \text{TotalSum}$.

If these hold, the problem reduces to finding the number of subsets with sum equal to $\frac{\text{target} + \text{TotalSum}}{2}$!

---

### 4.3 Production Java 17/21 Implementation

```java
public final class TargetSum {

    /**
     * Reduces target sum expression search to 0/1 Knapsack subset counting.
     *
     * Time Complexity:  O(N * P) where P = (target + totalSum) / 2.
     * Space Complexity: O(P) 1D array.
     */
    public int findTargetSumWays(int[] nums, int target) {
        int totalSum = 0;
        for (int num : nums) {
            totalSum += num;
        }

        // Feasibility checks
        if (Math.abs(target) > totalSum || ((target + totalSum) & 1) != 0) {
            return 0;
        }

        int subsetSum = (target + totalSum) / 2;
        int[] dp = new int[subsetSum + 1];
        dp[0] = 1; // 1 way to form sum 0 (empty subset)

        for (int num : nums) {
            for (int s = subsetSum; s >= num; s--) {
                dp[s] += dp[s - num];
            }
        }

        return dp[subsetSum];
    }
}
```

---

## 5. Problem 3: Coin Change II (Unbounded Knapsack Combinations)

### 5.1 Problem Statement & Constraints

You are given an integer array `coins` representing coins of different denominations and an integer `amount` representing a total amount of money.

Return the **number of combinations** that make up that amount. If that amount of money cannot be made up by any combination of the coins, return `0`.

You may assume that you have an infinite number of each kind of coin.

```
Example 1:
Input: amount = 5, coins = [1,2,5]
Output: 4
Explanation: 4 ways to make 5:
5=5, 5=2+2+1, 5=2+1+1+1, 5=1+1+1+1+1
```

#### Constraints:
- $1 \le \text{coins.length} \le 300$.
- $1 \le \text{coins}[i] \le 5000$.
- $0 \le \text{amount} \le 5000$.

---

### 5.2 Combinations vs. Permutations Invariant

```
  Critical Distinguishing Invariant:
  • Permutations: [1, 2] and [2, 1] are treated as DIFFERENT.
    Outer loop = amount, Inner loop = coins.
  • Combinations: [1, 2] and [2, 1] are the SAME combination.
    Outer loop = COINS, Inner loop = amount!
                         ↓
  By iterating over coins in the outer loop, once coin c is processed,
  it is never considered again for subsequent decisions.
  This enforces a strictly monotonic coin order (e.g. coin 1s then coin 2s then coin 5s),
  guaranteeing zero duplicate permutations!
```

---

### 5.3 Production Java 17/21 Implementation

```java
public final class CoinChangeII {

    /**
     * Counts coin combinations using Unbounded Knapsack forward 1D sweep.
     *
     * Time Complexity:  O(coins.length * amount).
     * Space Complexity: O(amount) 1D array.
     */
    public int change(int amount, int[] coins) {
        int[] dp = new int[amount + 1];
        dp[0] = 1;

        // Outer loop over coins guarantees COMBINATIONS rather than permutations
        for (int coin : coins) {
            // Forward loop allows multiple uses of the same coin
            for (int a = coin; a <= amount; a++) {
                dp[a] += dp[a - coin];
            }
        }

        return dp[amount];
    }
}
```

---

<div align="center">

| [← Back to Grid DP](./02-grid-and-multi-dimensional-dp.md) | [Track Hub: Dynamic Programming](./README.md) | [Next: Strings, Sequences & Edit Distance →](./04-strings-sequences-and-edit-distance.md) |
| :--- | :---: | ---: |

</div>
