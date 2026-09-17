# 05. Interval DP & Game Theory Formulations

[← Back to Strings & Sequences](./04-strings-sequences-and-edit-distance.md) | [Track Hub](./README.md) | [Next: Tree DP & Subtree Rerooting →](./06-tree-dp-and-subtree-rerooting.md)

---

## 🏛️ 1. Theoretical Foundations: The Interval Length Paradigm

In **Interval Dynamic Programming**, the subproblem state $dp[i][j]$ represents an optimal solution for the contiguous sub-array or sub-string spanning from index $i$ to index $j$.

### The Loop Ordering Invariant
A standard linear sweep ($i = 0 \to N, j = 0 \to N$) **fails** in interval DP because computing $dp[i][j]$ requires solutions to smaller sub-intervals (e.g., $dp[i][k]$ and $dp[k+1][j]$).

To satisfy topological subproblem dependencies, we must iterate by **interval length**:

```
Outer Loop:   length = 1, 2, 3, ..., N
Inner Loop:   start index i from 0 to N - length
Derived:      end index j = i + length - 1
Split Point:  k from i to j - 1

Transition:
dp[i][j] = min/max ( dp[i][k] + dp[k + 1][j] + cost(i, k, j) )
           for all k in [i, j - 1]
```

---

## 2. Problem 1: Burst Balloons (Hard) — The Inverted Last-Balloon Invariant

### 2.1 Problem Statement & Constraints

You are given `n` balloons, indexed from `0` to `n - 1`. Each balloon is painted with a number on it represented by an array `nums`. You are asked to burst all the balloons.

If you burst the $i^{\text{th}}$ balloon, you will get $\text{nums}[i - 1] \times \text{nums}[i] \times \text{nums}[i + 1]$ coins. If $i - 1$ or $i + 1$ goes out of bounds of the array, then treat it as if there is a balloon with a `1` painted on it.

Return the **maximum coins** you can collect by bursting the balloons wisely.

```
Example 1:
Input: nums = [3,1,5,8]
Output: 167
Explanation:
nums = [3,1,5,8] --> [3,5,8] --> [3,8] --> [8] --> []
coins =  3*1*5    +   3*5*8   +  1*3*8  + 1*8*1 = 15 + 120 + 24 + 8 = 167.
```

#### Constraints:
- $n == \text{nums.length}$.
- $1 \le n \le 300$.
- $0 \le \text{nums}[i] \le 100$.

---

### 2.2 Thought Process: Why "Burst First" Destroys Subproblems

```
  The "Burst First" Pitfall:
  Suppose we choose balloon k to burst FIRST.
  Now balloons k - 1 and k + 1 become ADJACENT!
  The left subproblem [0 .. k - 1] and right subproblem [k + 1 .. n - 1] are NO LONGER INDEPENDENT!
  Future bursts in the left subproblem depend on balloons remaining in the right subproblem!
                         ↓
  "Aha!" Insight: Invert the Choice — Burst Balloon k LAST!
  Instead of asking which balloon to burst first, ask:
  "Which balloon k in interval [i, j] should be burst LAST?"
                         ↓
  The Independence Invariant:
  If balloon k is burst LAST in range [i, j]:
  • All other balloons in [i, k - 1] and [k + 1, j] have ALREADY been burst!
  • Therefore, when balloon k is burst, its ONLY remaining neighbors are
    the outer boundary balloons: nums[i - 1] and nums[j + 1]!
  • The left subproblem [i, k - 1] and right subproblem [k + 1, j] are
    100% INDEPENDENT and can be solved separately!
```

---

### 2.3 Mathematical Recurrence

Pad `nums` with boundary `1`s at both ends: `padded[0] = 1`, `padded[n + 1] = 1`.
Define $dp[i][j]$ as the maximum coins obtained by bursting all balloons strictly between index $i$ and $j$ (exclusive):

$$dp[i][j] = \max_{k=i+1}^{j-1} \Big( dp[i][k] + dp[k][j] + \text{padded}[i] \times \text{padded}[k] \times \text{padded}[j] \Big)$$

---

### 2.4 Production Java 17/21 Implementation

```java
public final class BurstBalloons {

    /**
     * Solves Burst Balloons using interval DP with the inverted last-balloon invariant.
     *
     * Time Complexity:  O(N^3) where N = nums.length.
     * Space Complexity: O(N^2) for the 2D DP matrix.
     */
    public int maxCoins(int[] nums) {
        int n = nums.length;
        int[] padded = new int[n + 2];
        padded[0] = 1;
        padded[n + 1] = 1;
        System.arraycopy(nums, 0, padded, 1, n);

        // dp[i][j] = max coins bursting all balloons strictly between i and j
        int[][] dp = new int[n + 2][n + 2];

        // Iterate by interval length (from smallest 3 to full range n + 2)
        for (int len = 2; len < n + 2; len++) {
            for (int i = 0; i + len < n + 2; i++) {
                int j = i + len;

                // Pick which balloon k in (i, j) to burst LAST
                for (int k = i + 1; k < j; k++) {
                    int coins = padded[i] * padded[k] * padded[j] + dp[i][k] + dp[k][j];
                    if (coins > dp[i][j]) {
                        dp[i][j] = coins;
                    }
                }
            }
        }

        return dp[0][n + 1];
    }
}
```

---

## 3. Problem 2: Predict the Winner (Minimax Game Theory DP)

### 3.1 Problem Statement & Constraints

You are given an integer array `nums`. Two players are playing a game with this array: player 1 and player 2.

Player 1 and player 2 take turns, with player 1 starting first. Both players start the game with a score of `0`. At each turn, the player takes one of the numbers from either the **beginning** or the **end** of the array, which will reduce the size of the array by `1`. The player adds the chosen number to their score. The game ends when there are no more elements in the array.

Return `true` if Player 1 can win the game. If the scores of both players are equal, player 1 is still the winner, and you should also return `true`. You may assume that both players are playing optimally.

```
Example 1:
Input: nums = [1,5,2]
Output: false
Explanation: Player 1 takes 1 or 2. Player 2 will then pick 5 and win with 5 > 3.

Example 2:
Input: nums = [1,5,233,7]
Output: true
```

#### Constraints:
- $1 \le \text{nums.length} \le 20$.
- $0 \le \text{nums}[i] \le 10^7$.

---

### 3.2 Minimax Relative Score Invariant

```
  Relative Score Formulation:
  Define dp[i][j] = The MAXIMUM net score advantage (current player score - opponent score)
                    the current player can achieve from sub-array nums[i .. j].
                         ↓
  Choices at turn:
  • Pick nums[i]: Net advantage is nums[i] - dp[i + 1][j]
  • Pick nums[j]: Net advantage is nums[j] - dp[i][j - 1]
                         ↓
  Recurrence:
  dp[i][j] = max(nums[i] - dp[i + 1][j], nums[j] - dp[i][j - 1])
  
  Player 1 wins if and only if dp[0][n - 1] >= 0!
```

---

### 3.3 Production Java 17/21 Implementation

```java
public final class PredictTheWinner {

    /**
     * Solves 2-player optimal game using Minimax Interval DP in O(N) space.
     *
     * Time Complexity:  O(N^2)
     * Space Complexity: O(N) compressed into 1D rolling array.
     */
    public boolean predictTheWinner(int[] nums) {
        int n = nums.length;
        int[] dp = nums.clone(); // Base case: length 1 interval, net advantage is nums[i]

        for (int len = 2; len <= n; len++) {
            for (int i = 0; i <= n - len; i++) {
                int j = i + len - 1;
                // dp[i] represents dp[i][j-1], dp[i+1] represents dp[i+1][j]
                dp[i] = Math.max(nums[i] - dp[i + 1], nums[j] - dp[i]);
            }
        }

        return dp[0] >= 0;
    }
}
```

---

<div align="center">

| [← Back to Strings & Sequences](./04-strings-sequences-and-edit-distance.md) | [Track Hub: Dynamic Programming](./README.md) | [Next: Tree DP & Subtree Rerooting →](./06-tree-dp-and-subtree-rerooting.md) |
| :--- | :---: | ---: |

</div>
