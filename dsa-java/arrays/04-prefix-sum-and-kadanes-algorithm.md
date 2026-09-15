# Page 04: Prefix Sums & Kadane's Algorithm

When solving subarray queries or finding optimal contiguous ranges, computing sums repeatedly leads to $O(N^2)$ or $O(N^3)$ brute-force solutions.

The **Prefix Sum** and **Kadane's Algorithm** patterns pre-aggregate information so that range queries drop to $O(1)$ time and contiguous optimization problems are solved in a single $O(N)$ pass.

---

## 1. 1D Prefix Sum Mechanics

A **Prefix Sum** array pre-computes cumulative sums up to each index.

Using a **1-indexed prefix array** (`P` of size $N + 1$ where $P[0] = 0$) simplifies edge-case handling by eliminating conditional checks for index $0$:

```text
Original Array A (0-indexed):  [ 3,  5,  2,  8,  1 ]
Indices:                         0   1   2   3   4

Prefix Array P (1-indexed):    [ 0,  3,  8, 10, 18, 19 ]
Indices:                         0   1   2   3   4   5
                                 ▲
                               P[0] = 0 (Base Case)
```

### The $O(1)$ Range Sum Query Formula
To find the sum of elements from index $L$ to index $R$ (inclusive):

$$\text{RangeSum}(L, R) = \sum_{k=L}^{R} A[k] = P[R + 1] - P[L]$$

```text
Example: Sum of range [1, 3] (elements 5 + 2 + 8 = 15):
L = 1, R = 3
RangeSum(1, 3) = P[3 + 1] - P[1] = P[4] - P[1] = 18 - 3 = 15.
```

---

## 2. Prefix Sum + Hash Map: Subarray Sum Equals K

> **Problem**: Given an array of integers `nums` and an integer `k`, return the total number of subarrays whose sum equals `k`. The array can contain **negative numbers, positives, and zeroes**.

### Why Two Pointers Fails Here
If an array contains negative numbers, expanding a sliding window does not guarantee the sum will increase, and shrinking it does not guarantee it will decrease. **Monotonicity is destroyed**.

### The Algebraic Invariant
Let $\text{prefixSum}[j]$ be the sum from index $0$ to $j$, and $\text{prefixSum}[i]$ be the sum from index $0$ to $i$ ($i < j$).
The sum of subarray from $i + 1$ to $j$ is:
$$\text{sum}(i + 1, j) = \text{prefixSum}[j] - \text{prefixSum}[i]$$
We want this sum to equal $k$:
$$\text{prefixSum}[j] - \text{prefixSum}[i] = k \implies \mathbf{\text{prefixSum}[i] = \text{prefixSum}[j] - k}$$

As we iterate through the array maintaining a running prefix sum, we query a frequency map: *"How many times have we previously observed a prefix sum equal to $(\text{currentSum} - k)$?"*

```java
import java.util.HashMap;
import java.util.Map;

public class SubarraySumEqualsK {
    public int subarraySum(int[] nums, int k) {
        int count = 0;
        int currentSum = 0;
        
        // Map stores: <PrefixSum, Frequency of Occurrence>
        Map<Integer, Integer> prefixFreq = new HashMap<>();
        
        // Base case: A prefix sum of 0 has occurred once (represents empty prefix)
        prefixFreq.put(0, 1);

        for (int num : nums) {
            currentSum += num;

            // Check if there is a prefix sum that satisfies: currentSum - target = k
            int target = currentSum - k;
            if (prefixFreq.containsKey(target)) {
                count += prefixFreq.get(target);
            }

            // Record the current prefix sum frequency
            prefixFreq.put(currentSum, prefixFreq.getOrDefault(currentSum, 0) + 1);
        }

        return count;
    }
}
```

#### Step-by-Step Trace
Input: `nums = [1, -1, 1, 1, 1]`, `k = 2`

| Iteration | `num` | `currentSum` | `target = currentSum - 2` | In Map? | Added to `count` | Updated `count` | Map State after iteration |
| :---: | :---: | :---: | :---: | :---: | :---: | :---: | :--- |
| **Start** | - | 0 | - | - | - | 0 | `{0: 1}` |
| **1** | 1 | 1 | -1 | No | 0 | 0 | `{0: 1, 1: 1}` |
| **2** | -1 | 0 | -2 | No | 0 | 0 | `{0: 2, 1: 1}` |
| **3** | 1 | 1 | -1 | No | 0 | 0 | `{0: 2, 1: 2}` |
| **4** | 1 | 2 | 0 | **Yes** (`freq = 2`) | +2 | 2 | `{0: 2, 1: 2, 2: 1}` |
| **5** | 1 | 3 | 1 | **Yes** (`freq = 2`) | +2 | 4 | `{0: 2, 1: 2, 2: 1, 3: 1}` |

- **Time Complexity**: $O(N)$ (Single linear scan).
- **Auxiliary Space**: $O(N)$ (Hash map storing at most $N + 1$ unique prefix sums).

---

## 3. State Transformation: Contiguous Array (Equal 0s and 1s)

> **Problem**: Given a binary array `nums`, find the maximum length of a contiguous subarray with an equal number of $0$s and $1$s.

### Mathematical Reduction
If we replace every `0` with `-1`:
- An equal count of $0$s and $1$s means their algebraic sum is **exactly zero**!
- The problem reduces to finding the **longest subarray with sum $= 0$**.
- A subarray sum from index $i$ to $j$ equals zero if and only if $\mathbf{\text{prefixSum}[i] == \text{prefixSum}[j]}$.
- We store the **earliest seen index** for each prefix sum.

```java
import java.util.HashMap;
import java.util.Map;

public class ContiguousArray {
    public int findMaxLength(int[] nums) {
        // Map stores: <PrefixSum, EarliestIndexSeen>
        Map<Integer, Integer> earliestIndex = new HashMap<>();
        earliestIndex.put(0, -1); // Base case: prefix sum 0 occurs at virtual index -1

        int maxLength = 0;
        int runningSum = 0;

        for (int i = 0; i < nums.length; i++) {
            // Transform 0 -> -1, 1 -> +1
            runningSum += (nums[i] == 1) ? 1 : -1;

            if (earliestIndex.containsKey(runningSum)) {
                // Maximize distance between current index and earliest occurrence
                maxLength = Math.max(maxLength, i - earliestIndex.get(runningSum));
            } else {
                // Only store earliest occurrence to maximize window length
                earliestIndex.put(runningSum, i);
            }
        }

        return maxLength;
    }
}
```

- **Time Complexity**: $O(N)$.
- **Auxiliary Space**: $O(N)$.

---

## 4. Kadane's Algorithm: Maximum Subarray Sum

> **Problem**: Given an integer array `nums`, find the contiguous subarray which has the largest sum and return its sum.

### Dynamic Programming Derivation
At each index $i$, we face a binary choice:
1. **Extend** the existing contiguous subarray: `currentMax + nums[i]`.
2. **Restart** a fresh contiguous subarray beginning at index $i$: `nums[i]`.

$$\text{dp}[i] = \max(\text{nums}[i], \text{dp}[i - 1] + \text{nums}[i])$$

Because `dp[i]` depends only on `dp[i - 1]`, we optimize space to $O(1)$ variables:

```java
public class KadanesAlgorithm {
    public int maxSubArray(int[] nums) {
        int currentMax = nums[0];
        int globalMax = nums[0];

        for (int i = 1; i < nums.length; i++) {
            // If currentMax < 0, restarting at nums[i] is strictly superior
            currentMax = Math.max(nums[i], currentMax + nums[i]);
            globalMax = Math.max(globalMax, currentMax);
        }

        return globalMax;
    }
}
```

#### Step-by-Step Dry Run Trace
Input: `nums = [-2, 1, -3, 4, -1, 2, 1, -5, 4]`

| $i$ | `nums[i]` | `currentMax` calculation | `currentMax` | `globalMax` | Decision Made |
| :---: | :---: | :--- | :---: | :---: | :--- |
| **0** | -2 | (Initialization) | -2 | -2 | Initial state |
| **1** | 1 | $\max(1, -2 + 1) = \max(1, -1)$ | 1 | 1 | **Restart** fresh subarray at index 1 |
| **2** | -3 | $\max(-3, 1 - 3) = \max(-3, -2)$ | -2 | 1 | **Extend** |
| **3** | 4 | $\max(4, -2 + 4) = \max(4, 2)$ | 4 | 4 | **Restart** fresh subarray at index 3 |
| **4** | -1 | $\max(-1, 4 - 1) = \max(-1, 3)$ | 3 | 4 | **Extend** |
| **5** | 2 | $\max(2, 3 + 2) = \max(2, 5)$ | 5 | 5 | **Extend** |
| **6** | 1 | $\max(1, 5 + 1) = \max(1, 6)$ | 6 | **6** | **Extend** (Subarray `[4, -1, 2, 1]`) |
| **7** | -5 | $\max(-5, 6 - 5) = \max(-5, 1)$ | 1 | 6 | **Extend** |
| **8** | 4 | $\max(4, 1 + 4) = \max(4, 5)$ | 5 | 6 | **Extend** |

- **Time Complexity**: $O(N)$ (Single linear pass).
- **Auxiliary Space**: $O(1)$ (Constant variables).

---

## 5. Non-Trivial Variation: Maximum Product Subarray

> **Problem**: Given an integer array `nums`, find a contiguous non-empty subarray that has the largest product.

### Why Standard Kadane's Fails
Multiplication by a negative number **inverts the sign**:
- A very small negative number (e.g. $-1000$), when multiplied by another negative number (e.g. $-2$), becomes a massive positive number ($+2000$)!
- We must track **both** `maxSoFar` and `minSoFar` at every step.

```java
public class MaximumProductSubarray {
    public int maxProduct(int[] nums) {
        int maxSoFar = nums[0];
        int minSoFar = nums[0];
        int globalMax = nums[0];

        for (int i = 1; i < nums.length; i++) {
            int current = nums[i];

            // If current number is negative, multiplying flips max and min
            if (current < 0) {
                int temp = maxSoFar;
                maxSoFar = minSoFar;
                minSoFar = temp;
            }

            maxSoFar = Math.max(current, maxSoFar * current);
            minSoFar = Math.min(current, minSoFar * current);

            globalMax = Math.max(globalMax, maxSoFar);
        }

        return globalMax;
    }
}
```

- **Time Complexity**: $O(N)$.
- **Auxiliary Space**: $O(1)$.

---

## 6. 2D Prefix Sums: Inclusion-Exclusion Principle

To perform $O(1)$ rectangle range sum queries on an $M \times N$ matrix, we construct a 2D prefix sum matrix $P$ of dimensions $(M + 1) \times (N + 1)$.

### Visual Inclusion-Exclusion Derivation

```text
╭─────────────────────────────────────────────────────────────────────────────╮
│                        2D Prefix Sum Geometry                               │
├─────────────────────────────────────────────────────────────────────────────┤
│ (0,0)                                                                       │
│   ┌─────────────────────┬──────────────────┐                                │
│   │                     │                  │                                │
│   │       Region A      │     Region B     │                                │
│   │                     │                  │                                │
│   ├─────────────────────(r1, c1)───────────┤                                │
│   │                     │                  │                                │
│   │       Region C      │   Target Rect D  │                                │
│   │                     │                  │                                │
│   └─────────────────────┴────────────────(r2, c2)                           │
│                                                                             │
│ Target Area D = (A + B + C + D) - (A + B) - (A + C) + A                     │
│                                                                             │
│ Formula:                                                                    │
│ SumRegion = P[r2+1][c2+1] - P[r1][c2+1] - P[r2+1][c1] + P[r1][c1]           │
╰─────────────────────────────────────────────────────────────────────────────╯
```

```java
public class NumMatrix {
    private final int[][] prefix;

    public NumMatrix(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        prefix = new int[rows + 1][cols + 1];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                prefix[r + 1][c + 1] = matrix[r][c] 
                                      + prefix[r][c + 1] 
                                      + prefix[r + 1][c] 
                                      - prefix[r][c];
            }
        }
    }

    public int sumRegion(int r1, int c1, int r2, int c2) {
        return prefix[r2 + 1][c2 + 1] 
             - prefix[r1][c2 + 1] 
             - prefix[r2 + 1][c1] 
             + prefix[r1][c1];
    }
}
```

- **Preprocessing Time**: $O(M \cdot N)$.
- **Query Time**: $O(1)$ direct arithmetic calculation.
- **Auxiliary Space**: $O(M \cdot N)$.

---

## 7. Self-Check & Active Recall

1. **Q**: In "Subarray Sum Equals K", why do we initialize `map.put(0, 1)` before processing elements?
   - *A*: It accounts for subarrays starting at index $0$ whose sum equals $k$ directly. When `currentSum == k`, `currentSum - k = 0`. Without `{0: 1}`, these subarrays would not be counted.

2. **Q**: What happens to Kadane's algorithm if all array elements are negative (e.g. `[-5, -2, -8]`)?
   - *A*: `currentMax` will discard prior negatives and choose `nums[i]`. `globalMax` will correctly record the least negative number ($-2$).

3. **Q**: Why is `prefix[r][c]` added back in the 2D range sum query formula?
   - *A*: Both `prefix[r1][c2+1]` and `prefix[r2+1][c1]` contain `Region A`. Subtracting both subtracts `Region A` twice. It must be added back once to maintain mathematical equality.

---

## 🧭 Continue Learning

| ◀️ Previous Topic | 🧭 Track Hub | Next Topic ▶️ |
| :--- | :---: | ---: |
| [**Page 03: Two Pointers & Sliding Window**](03-two-pointers-and-sliding-window.md)<br><sub>*Opposite Ends & Expanding Windows*</sub> | [**Arrays Index**](README.md)<br><sub>*All 9 Modules*</sub> | [**Page 05: In-Place Mutations & Cyclic Sort**](05-in-place-manipulations-and-cyclic-sort.md)<br><sub>*Dutch Flag, Permutations & In-Place Swaps*</sub> |
