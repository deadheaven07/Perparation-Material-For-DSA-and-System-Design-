# Page 05: In-Place Manipulations & Cyclic Sort

In high-concurrency systems and memory-constrained environments, allocating auxiliary arrays ($O(N)$ space) triggers garbage collection pressure and cache evictions.

**In-Place Array Manipulations** achieve optimal $O(1)$ auxiliary space by reusing the input array itself as both the workspace and the storage medium.

---

## 1. The Dutch National Flag Algorithm (3-Way Partitioning)

> **Problem**: Given an array `nums` with $n$ objects colored red, white, or blue (represented by integers $0$, $1$, and $2$), sort them **in-place** so that objects of the same color are adjacent, with the colors in the order red ($0$), white ($1$), and blue ($2$). You cannot use the library sort function.

### The 4-Zone Invariant
We maintain three pointers (`low`, `mid`, `high`) dividing the array into 4 distinct regions:

```text
╭─────────────────────────────────────────────────────────────────────────────╮
│                     Dutch National Flag 4-Zone Invariant                    │
├───────────────┬───────────────┬─────────────────────────────┬───────────────┤
│ [0 ... low-1] │ [low ... mid-1]│ [mid ... high]              │[high+1 ... n-1]│
├───────────────┼───────────────┼─────────────────────────────┼───────────────┤
│ Strictly 0s   │ Strictly 1s   │ Unclassified (To Process)   │ Strictly 2s   │
╰───────────────┴───────────────┴─────────────────────────────┴───────────────╯
                  ▲               ▲             ▲
                 low             mid           high
```

### The Transition Rules:
1. If `nums[mid] == 0`: Swap `nums[low]` and `nums[mid]`. Increment **both** `low++` and `mid++`. (We know the element brought from `low` is a $1$, so `mid` can advance).
2. If `nums[mid] == 1`: Already in correct zone. Increment `mid++`.
3. If `nums[mid] == 2`: Swap `nums[mid]` and `nums[high]`. Decrement `high--`. **Do NOT increment `mid`!** (The element swapped from `high` was previously unclassified; it must be inspected in the next iteration).

```java
public class SortColors {
    public void sortColors(int[] nums) {
        int low = 0;
        int mid = 0;
        int high = nums.length - 1;

        while (mid <= high) {
            if (nums[mid] == 0) {
                swap(nums, low, mid);
                low++;
                mid++;
            } else if (nums[mid] == 1) {
                mid++;
            } else { // nums[mid] == 2
                swap(nums, mid, high);
                high--; // Notice: mid does NOT increment here!
            }
        }
    }

    private void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }
}
```

#### Step-by-Step Dry Run Trace
Input: `nums = [2, 0, 2, 1, 1, 0]`

| Step | `low` | `mid` | `high` | Array State | Action Taken |
| :---: | :---: | :---: | :---: | :---: | :--- |
| **0** | 0 | 0 | 5 | `[ 2, 0, 2, 1, 1, 0 ]` | Initial state (`nums[mid] = 2`) |
| **1** | 0 | 0 | 4 | `[ 0, 0, 2, 1, 1, 2 ]` | Swap `mid(0)` & `high(5)`, `high--` |
| **2** | 1 | 1 | 4 | `[ 0, 0, 2, 1, 1, 2 ]` | Swap `low(0)` & `mid(0)`, `low++`, `mid++` |
| **3** | 2 | 2 | 4 | `[ 0, 0, 2, 1, 1, 2 ]` | Swap `low(1)` & `mid(1)`, `low++`, `mid++` |
| **4** | 2 | 2 | 3 | `[ 0, 0, 1, 1, 2, 2 ]` | Swap `mid(2)` & `high(4)`, `high--` |
| **5** | 2 | 3 | 3 | `[ 0, 0, 1, 1, 2, 2 ]` | `nums[mid] == 1`, `mid++` |
| **6** | 2 | 4 | 3 | `[ 0, 0, 1, 1, 2, 2 ]` | `nums[mid] == 1`, `mid++`. Loop terminates (`mid > high`). |

- **Time Complexity**: $O(N)$ — Single linear pass.
- **Auxiliary Space**: $O(1)$ — In-place pointer swaps.

---

## 2. Next Permutation: Lexicographical Sequencing

> **Problem**: Implement next permutation, which rearranges numbers into the lexicographically next greater permutation of numbers. If no such permutation is possible, rearrange it as the lowest possible order (i.e., sorted in ascending order). The replacement must be **in-place** and use only constant extra memory.

### The 4-Step Geometric Blueprint

```text
Sequence: [ 1, 3, 5, 4, 2 ]

Step 1: Scan right-to-left to find first decreasing pivot (nums[i] < nums[i+1]):
        [ 1,  3,  5,  4,  2 ]
              ▲   └─── Decreasing suffix ───┘
            pivot (index 1: value 3)

Step 2: Scan right-to-left to find smallest element greater than pivot:
        [ 1,  3,  5,  4,  2 ]
                      ▲
                  successor (index 3: value 4)

Step 3: Swap pivot and successor:
        [ 1,  4,  5,  3,  2 ]

Step 4: Reverse the decreasing suffix to make it as small as possible:
        [ 1,  4,  2,  3,  5 ] ──► Result!
```

```java
public class NextPermutation {
    public void nextPermutation(int[] nums) {
        int n = nums.length;
        int i = n - 2;

        // 1. Find the first decreasing pivot from the right
        while (i >= 0 && nums[i] >= nums[i + 1]) {
            i--;
        }

        // 2. If a valid pivot was found, find successor to swap
        if (i >= 0) {
            int j = n - 1;
            while (nums[j] <= nums[i]) {
                j--;
            }
            swap(nums, i, j);
        }

        // 3. Reverse the suffix from i + 1 to the end
        reverse(nums, i + 1, n - 1);
    }

    private void reverse(int[] nums, int start, int end) {
        while (start < end) {
            swap(nums, start, end);
            start++;
            end--;
        }
    }

    private void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }
}
```

- **Time Complexity**: $O(N)$ (at most two scans and one reversal).
- **Auxiliary Space**: $O(1)$ (constant extra memory).

---

## 3. In-Place Array Rotation: The 3-Step Reversal Algorithm

> **Problem**: Given an integer array `nums`, rotate the array to the right by `k` steps, where `k` is non-negative. Must be in-place ($O(1)$ extra space).

```text
Array: [ 1, 2, 3, 4, 5, 6, 7 ], k = 3

Step 1: Reverse the entire array:
        [ 7, 6, 5, 4, 3, 2, 1 ]

Step 2: Reverse first k elements [0 ... k-1]:
        [ 5, 6, 7, 4, 3, 2, 1 ]

Step 3: Reverse remaining n - k elements [k ... n-1]:
        [ 5, 6, 7, 1, 2, 3, 4 ] ──► Result!
```

```java
public class RotateArray {
    public void rotate(int[] nums, int k) {
        int n = nums.length;
        k %= n; // Guard against k >= n

        // 1. Reverse entire array
        reverse(nums, 0, n - 1);
        // 2. Reverse first k elements
        reverse(nums, 0, k - 1);
        // 3. Reverse remaining elements
        reverse(nums, k, n - 1);
    }

    private void reverse(int[] nums, int l, int r) {
        while (l < r) {
            int temp = nums[l];
            nums[l] = nums[r];
            nums[r] = temp;
            l++;
            r--;
        }
    }
}
```

- **Time Complexity**: $O(N)$.
- **Auxiliary Space**: $O(1)$.

---

## 4. The Cyclic Sort Pattern

The **Cyclic Sort** pattern applies whenever numbers in an array of size $N$ are bounded in the range $[1 \dots N]$ (or $[0 \dots N]$).

### The Invariant
Every number $X \in [1 \dots N]$ belongs at index $X - 1$:
- Value `1` belongs at index `0`.
- Value `2` belongs at index `1`.
- Value `v` belongs at index `v - 1`.

---

### Canonical Problem: First Missing Positive

> **Problem**: Given an unsorted integer array `nums`, return the smallest positive integer that is not present in `nums`. You must implement an algorithm that runs in $O(N)$ time and uses $O(1)$ auxiliary space.

#### Why This Works in $O(N)$ Time with While Loop
We iterate through the array. For element `nums[i]`:
- If `nums[i]` is in range $[1 \dots N]$ and is not already at its correct home (`nums[i] != nums[nums[i] - 1]`), we **swap it into its correct home**.
- Each swap places at least **one number** into its permanent final position. Since there are $N$ slots, at most $N$ swaps can occur across the entire traversal!

```java
public class FirstMissingPositive {
    public int firstMissingPositive(int[] nums) {
        int n = nums.length;

        // Phase 1: Cyclic Sort into homes
        for (int i = 0; i < n; i++) {
            while (nums[i] > 0 && nums[i] <= n && nums[i] != nums[nums[i] - 1]) {
                swap(nums, i, nums[i] - 1);
            }
        }

        // Phase 2: Identify the first mismatched index
        for (int i = 0; i < n; i++) {
            if (nums[i] != i + 1) {
                return i + 1;
            }
        }

        // If all 1..n are present, the answer is n + 1
        return n + 1;
    }

    private void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }
}
```

#### Step-by-Step Dry Run Trace
Input: `nums = [3, 4, -1, 1]` ($N = 4$)

| $i$ | `nums[i]` | Condition: in $[1 \dots 4]$ & `nums[i] != nums[nums[i]-1]`? | Swap Target | Array After Swap |
| :---: | :---: | :---: | :---: | :---: |
| **0** | 3 | $3 \in [1..4]$ and $3 \ne \text{nums}[2] (-1)$ $\rightarrow$ **True** | Swap index $0$ and $2$ | `[ -1, 4, 3, 1 ]` |
| **0** | -1 | $-1 > 0$ $\rightarrow$ **False** (Break inner loop) | None | `[ -1, 4, 3, 1 ]` |
| **1** | 4 | $4 \in [1..4]$ and $4 \ne \text{nums}[3] (1)$ $\rightarrow$ **True** | Swap index $1$ and $3$ | `[ -1, 1, 3, 4 ]` |
| **1** | 1 | $1 \in [1..4]$ and $1 \ne \text{nums}[0] (-1)$ $\rightarrow$ **True** | Swap index $1$ and $0$ | `[ 1, -1, 3, 4 ]` |
| **1** | -1 | $-1 > 0$ $\rightarrow$ **False** (Break inner loop) | None | `[ 1, -1, 3, 4 ]` |
| **2** | 3 | $3 == \text{nums}[2]$ $\rightarrow$ **False** (Already at home) | None | `[ 1, -1, 3, 4 ]` |
| **3** | 4 | $4 == \text{nums}[3]$ $\rightarrow$ **False** (Already at home) | None | `[ 1, -1, 3, 4 ]` |

**Phase 2 Check**:
- Index 0: `nums[0] == 1` (Correct)
- Index 1: `nums[1] == -1 != 2` $\rightarrow$ **First Missing Positive is 2!**

- **Time Complexity**: $O(N)$ — Phase 1 performs at most $N$ swaps; Phase 2 is a single linear scan.
- **Auxiliary Space**: $O(1)$ — Direct in-place mutation.

---

## 5. In-Place State Encoding: Product of Array Except Self

> **Problem**: Given an integer array `nums`, return an array `answer` such that `answer[i]` is equal to the product of all elements of `nums` except `nums[i]`. The algorithm must run in $O(N)$ time and without using the division operation, using $O(1)$ auxiliary space (output array does not count toward extra space).

### The Invariant
$$\text{Product Except Self}(i) = \text{Prefix Product}(0 \dots i-1) \times \text{Suffix Product}(i+1 \dots n-1)$$

We store the prefix products directly inside the return array, and maintain a running scalar variable for the suffix product:

```java
public class ProductExceptSelf {
    public int[] productExceptSelf(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];

        // Pass 1: result[i] contains product of all elements to the left of i
        result[0] = 1;
        for (int i = 1; i < n; i++) {
            result[i] = result[i - 1] * nums[i - 1];
        }

        // Pass 2: Multiply by running product of all elements to the right of i
        int rightRunningProduct = 1;
        for (int i = n - 1; i >= 0; i--) {
            result[i] = result[i] * rightRunningProduct;
            rightRunningProduct *= nums[i];
        }

        return result;
    }
}
```

- **Time Complexity**: $O(N)$ (Two linear passes).
- **Auxiliary Space**: $O(1)$ (Output array is required by problem contract; only a single scalar `rightRunningProduct` is allocated).

---

## 6. Self-Check & Active Recall

1. **Q**: In the Dutch National Flag algorithm, why do we not increment `mid` when `nums[mid] == 2` after swapping with `high`?
   - *A*: The element previously at `high` was unclassified (it could be a $0$, $1$, or $2$). If we incremented `mid`, we would skip inspecting this element, violating the invariant.

2. **Q**: What guarantees that the `while` loop in "First Missing Positive" runs in $O(N)$ time instead of $O(N^2)$?
   - *A*: Every swap places at least one previously misplaced positive number into its correct destination index $v - 1$. Once an element is at its destination, it is never swapped again. At most $N$ swaps can occur across all iterations.

3. **Q**: Why can't we use simple division ($\text{Total Product} / \text{nums}[i]$) for "Product of Array Except Self"?
   - *A*: Two reasons: 1. Division is explicitly forbidden by problem constraints; 2. Division fails catastrophically when the array contains one or more zeroes (causing `ArithmeticException: / by zero`).

---

## 🧭 Continue Learning

| ◀️ Previous Topic | 🧭 Track Hub | Next Topic ▶️ |
| :--- | :---: | ---: |
| [**Page 04: Prefix Sum & Kadane**](04-prefix-sum-and-kadanes-algorithm.md)<br><sub>*1D/2D Prefix Arrays & Dynamic Programming*</sub> | [**Arrays Index**](README.md)<br><sub>*All 9 Modules*</sub> | [**Page 06: Matrix Manipulations & 2D**](06-matrix-manipulations-and-2d-algorithms.md)<br><sub>*Spiral, In-Place Rotation & Saddleback*</sub> |
