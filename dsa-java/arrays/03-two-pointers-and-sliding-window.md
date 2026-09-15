# Page 03: Two Pointers & Sliding Window Patterns

The **Two Pointers** and **Sliding Window** patterns are the most effective techniques for transforming brute-force $O(N^2)$ algorithms into optimal linear $O(N)$ solutions.

They operate by maintaining **invariants** over monotonic properties or contiguous subsegments, eliminating redundant checks.

---

## 1. Pattern 1: Opposite-Direction Two Pointers

In opposite-direction two pointers, one pointer begins at index `0` (`left`) and the other at index `N - 1` (`right`). They converge inward based on an evaluation condition:

```text
Initial State:
[ 1,  3,  4,  7,  9,  11,  15 ]
  ▲                         ▲
 left                     right

Condition Check:
• If sum < target: Increment left (brings smaller values up)
• If sum > target: Decrement right (brings larger values down)
• If sum == target: Solution found!
```

### Why Does This Work? (Mathematical Proof of Invariant)
In a sorted array, if `arr[left] + arr[right] > target`, then `arr[right]` **cannot pair with any element** between `left` and `right - 1`, because those elements are all $\ge arr[left]$. In one comparison, we eliminate an entire row of $O(N)$ candidate pairs!

---

### Canonical Problem 1: Container With Most Water

> **Problem**: Given $N$ non-negative integers $a_1, a_2, \dots, a_n$ where each represents a point at coordinate $(i, a_i)$, find two lines that together with the x-axis form a container that stores the most water.

#### Intuition & Proof of Optimality
The area formed between `left` and `right` is:
$$\text{Area} = (right - left) \times \min(height[left], height[right])$$
If we move the pointer pointing to the **taller** line inward, the width $(right - left)$ strictly decreases, and the height can never exceed the shorter line. Thus, moving the taller line can **never increase the area**. We must move the **shorter** line to have any chance of finding a taller wall.

```java
public class ContainerWithMostWater {
    public int maxArea(int[] height) {
        int left = 0;
        int right = height.length - 1;
        int maxWater = 0;

        while (left < right) {
            int currentWidth = right - left;
            int currentHeight = Math.min(height[left], height[right]);
            int currentArea = currentWidth * currentHeight;
            
            maxWater = Math.max(maxWater, currentArea);

            // Advance the shorter boundary
            if (height[left] < height[right]) {
                left++;
            } else {
                right--;
            }
        }

        return maxWater;
    }
}
```

#### Step-by-Step Dry Run Trace
Input: `height = [1, 8, 6, 2, 5, 4, 8, 3, 7]`

| Iteration | `left` | `right` | `height[left]` | `height[right]` | `width` | `minHeight` | `area` | `maxWater` | Pointer Shift |
| :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :--- |
| **1** | 0 | 8 | 1 | 7 | 8 | 1 | 8 | 8 | `left++` ($1 < 7$) |
| **2** | 1 | 8 | 8 | 7 | 7 | 7 | 49 | 49 | `right--` ($8 \ge 7$) |
| **3** | 1 | 7 | 8 | 3 | 6 | 3 | 18 | 49 | `right--` ($8 \ge 3$) |
| **4** | 1 | 6 | 8 | 8 | 5 | 8 | 40 | 49 | `right--` ($8 \ge 8$) |
| **5** | 1 | 5 | 8 | 4 | 4 | 4 | 16 | 49 | `right--` ($8 \ge 4$) |
| **6** | 1 | 4 | 8 | 5 | 3 | 5 | 15 | 49 | `right--` ($8 \ge 5$) |
| **7** | 1 | 3 | 8 | 2 | 2 | 2 | 4 | 49 | `right--` ($8 \ge 2$) |
| **8** | 1 | 2 | 8 | 6 | 1 | 6 | 6 | 49 | `right--` ($8 \ge 6$) |

- **Time Complexity**: $O(N)$ — Every iteration increments `left` or decrements `right`.
- **Auxiliary Space**: $O(1)$ — Only scalar variables.

---

### Canonical Problem 2: Trapping Rain Water

> **Problem**: Given $n$ non-negative integers representing an elevation map where the width of each bar is $1$, compute how much water it can trap after raining.

```text
Elevation Profile:
3 |        █
2 |    █···██·█
1 |  █·██·██████
0 └──┴─┴──┴─┴┴┴┴──
Water trapped at index i = max(0, min(leftMax, rightMax) - height[i])
```

#### The $O(1)$ Space Two-Pointer Breakthrough
Instead of precomputing prefix and suffix max arrays ($O(N)$ space), maintain `leftMax` and `rightMax` on the fly.
- If `height[left] < height[right]`, we are guaranteed that `leftMax < rightMax` (or the right wall is higher). Therefore, the water trapped at `left` is governed purely by `leftMax`, independent of intermediate elements!

```java
public class TrappingRainWater {
    public int trap(int[] height) {
        if (height == null || height.length == 0) return 0;

        int left = 0, right = height.length - 1;
        int leftMax = 0, rightMax = 0;
        int totalWater = 0;

        while (left < right) {
            if (height[left] < height[right]) {
                if (height[left] >= leftMax) {
                    leftMax = height[left];
                } else {
                    totalWater += leftMax - height[left];
                }
                left++;
            } else {
                if (height[right] >= rightMax) {
                    rightMax = height[right];
                } else {
                    totalWater += rightMax - height[right];
                }
                right--;
            }
        }

        return totalWater;
    }
}
```

- **Time Complexity**: $O(N)$ (single pass).
- **Auxiliary Space**: $O(1)$ (constant space).

---

## 2. Pattern 2: Sliding Window (Fixed & Dynamic)

The Sliding Window pattern applies to **contiguous subarrays**. Instead of recalculating properties of the window from scratch ($O(K)$ or $O(N)$ per step), we add the new element entering on the right and evict the old element leaving on the left in $O(1)$ time.

```text
Fixed Window (Size = 3):
Step 1: [ 2,  1,  5 ], 1,  3,  2   ──► Sum = 8
Step 2:   2, [ 1,  5,  1 ], 3,  2   ──► New Sum = Old Sum - 2 + 1 = 7
Step 3:   2,  1, [ 5,  1,  3 ], 2   ──► New Sum = Old Sum - 1 + 3 = 9
```

### Canonical Problem 3: Longest Substring Without Repeating Characters

> **Problem**: Given a string `s`, find the length of the longest substring without repeating characters.

#### High-Performance Java Implementation (ASCII Lookup Array)
Instead of a slow boxed `HashMap<Character, Integer>`, use a direct primitive `int[128]` storing the **last observed 1-based index** of each character.

```java
public class LongestSubstringWithoutRepeating {
    public int lengthOfLongestSubstring(String s) {
        // Direct ASCII array avoids HashMap overhead and object boxing
        int[] lastIndex = new int[128];
        int maxLength = 0;
        int left = 0;

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);

            // If character was seen inside the current window, jump left pointer
            if (lastIndex[c] > left) {
                left = lastIndex[c];
            }

            maxLength = Math.max(maxLength, right - left + 1);
            // Store 1-based index to distinguish from default 0 value
            lastIndex[c] = right + 1;
        }

        return maxLength;
    }
}
```

#### Step-by-Step Trace
Input: `s = "abcabcbb"`

| `right` | Character `c` | `lastIndex[c]` | `left` (after check) | Window Substring | `right - left + 1` | `maxLength` | Update `lastIndex[c]` |
| :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **0** | `'a'` | 0 | 0 | `"a"` | 1 | 1 | `lastIndex['a'] = 1` |
| **1** | `'b'` | 0 | 0 | `"ab"` | 2 | 2 | `lastIndex['b'] = 2` |
| **2** | `'c'` | 0 | 0 | `"abc"` | 3 | 3 | `lastIndex['c'] = 3` |
| **3** | `'a'` | 1 | 1 | `"bca"` | 3 | 3 | `lastIndex['a'] = 4` |
| **4** | `'b'` | 2 | 2 | `"cab"` | 3 | 3 | `lastIndex['b'] = 5` |
| **5** | `'c'` | 3 | 3 | `"abc"` | 3 | 3 | `lastIndex['c'] = 6` |
| **6** | `'b'` | 5 | 5 | `"b"` | 2 | 3 | `lastIndex['b'] = 7` |
| **7** | `'b'` | 7 | 7 | `"b"` | 1 | 3 | `lastIndex['b'] = 8` |

- **Time Complexity**: $O(N)$ (Each character is processed in $O(1)$ without backtracking).
- **Auxiliary Space**: $O(1)$ ($128$-element primitive integer array).

---

### Canonical Problem 4: Minimum Window Substring

> **Problem**: Given two strings `s` and `t`, return the minimum window substring of `s` such that every character in `t` (including duplicates) is included in the window. If no such window exists, return `""`.

#### Expanding & Contracting Window Template

```java
public class MinimumWindowSubstring {
    public String minWindow(String s, String t) {
        if (s.length() < t.length()) return "";

        int[] targetFreq = new int[128];
        for (char c : t.toCharArray()) {
            targetFreq[c]++;
        }

        int requiredMatches = 0;
        for (int count : targetFreq) {
            if (count > 0) requiredMatches++;
        }

        int[] windowFreq = new int[128];
        int formedMatches = 0;
        int left = 0;
        int minLen = Integer.MAX_VALUE;
        int startIdx = 0;

        for (int right = 0; right < s.length(); right++) {
            char rightChar = s.charAt(right);
            windowFreq[rightChar]++;

            if (targetFreq[rightChar] > 0 && windowFreq[rightChar] == targetFreq[rightChar]) {
                formedMatches++;
            }

            // Contract the window from left as long as it satisfies the condition
            while (formedMatches == requiredMatches) {
                // Update minimal window
                if (right - left + 1 < minLen) {
                    minLen = right - left + 1;
                    startIdx = left;
                }

                char leftChar = s.charAt(left);
                windowFreq[leftChar]--;

                if (targetFreq[leftChar] > 0 && windowFreq[leftChar] < targetFreq[leftChar]) {
                    formedMatches--;
                }

                left++;
            }
        }

        return minLen == Integer.MAX_VALUE ? "" : s.substring(startIdx, startIdx + minLen);
    }
}
```

- **Time Complexity**: $O(|S| + |T|)$ — `right` traverses up to $|S|$, and `left` traverses up to $|S|$ throughout the entire process.
- **Auxiliary Space**: $O(1)$ — Two fixed $128$-element primitive frequency arrays.

---

## 3. Self-Check & Active Recall

1. **Q**: Why can't we sort the array to solve "Minimum Window Substring"?
   - *A*: The problem requires finding a **contiguous substring** of `s`. Sorting permutes the original string, destroying contiguous adjacency.

2. **Q**: In "Container With Most Water", if `height[left] == height[right]`, which pointer can you move?
   - *A*: You can move **either pointer** (or both simultaneously). Since both walls have the same height, neither can form a larger area with any inner wall unless a wall taller than both is found.

3. **Q**: What guarantees that the dynamic sliding window runs in $O(N)$ time even with a nested `while (formedMatches == requiredMatches)` loop?
   - *A*: Amortized analysis: Each element is added to the window at most **once** by the outer loop (`right++`), and removed at most **once** by the inner loop (`left++`). Total pointer operations $\le 2N = O(N)$.

---

## 🧭 Continue Learning

| ◀️ Previous Topic | 🧭 Track Hub | Next Topic ▶️ |
| :--- | :---: | ---: |
| [**Page 02: 2D & 3D Arrays**](02-multidimensional-arrays-2d-and-3d.md)<br><sub>*Matrix Layout, Jagged Arrays & Flat Math*</sub> | [**Arrays Index**](README.md)<br><sub>*All 9 Modules*</sub> | [**Page 04: Prefix Sum & Kadane**](04-prefix-sum-and-kadanes-algorithm.md)<br><sub>*1D/2D Prefix Arrays & Dynamic Programming*</sub> |
