# Page 06: Matrix Manipulations & 2D Algorithms

Matrix operations combine index arithmetic with spatial navigation. Because allocating auxiliary matrices consumes $O(M \cdot N)$ memory, high-performance systems require **in-place boundary simulations, mathematical reflections, and saddleback search spaces**.

---

## 1. Rotate Image $90^\circ$ Clockwise In-Place

> **Problem**: You are given an $n \times n$ 2D matrix representing an image. Rotate the image by 90 degrees (clockwise) **in-place**. You must rotate the image directly without allocating an auxiliary 2D matrix.

### The Two-Step Mathematical Transformation
A $90^\circ$ clockwise rotation is mathematically equivalent to:
1. **Matrix Transpose**: Reflect elements along the main diagonal (`swap(matrix[i][j], matrix[j][i])`).
2. **Horizontal Reflection**: Reverse each row from left to right.

```text
Original Matrix:          Step 1: Transpose (swap i, j)     Step 2: Reverse Each Row
[ 1,  2,  3 ]             [ 1,  4,  7 ]                     [ 7,  4,  1 ]
[ 4,  5,  6 ]    ──►      [ 2,  5,  8 ]            ──►      [ 8,  5,  2 ]
[ 7,  8,  9 ]             [ 3,  6,  9 ]                     [ 9,  6,  3 ]
```

```java
public class RotateImage {
    public void rotate(int[][] matrix) {
        int n = matrix.length;

        // Step 1: Transpose across main diagonal (swap only where col > row)
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int temp = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = temp;
            }
        }

        // Step 2: Reverse each row horizontally
        for (int i = 0; i < n; i++) {
            int left = 0;
            int right = n - 1;
            while (left < right) {
                int temp = matrix[i][left];
                matrix[i][left] = matrix[i][right];
                matrix[i][right] = temp;
                left++;
                right--;
            }
        }
    }
}
```

> [!WARNING]
> During transposition, the inner loop must begin at `j = i + 1`. If `j` begins at `0`, elements will be swapped twice, reverting the matrix back to its original configuration!

- **Time Complexity**: $O(N^2)$ — Visiting each cell in the $N \times N$ matrix twice.
- **Auxiliary Space**: $O(1)$ — Complete in-place transformation.

---

## 2. Set Matrix Zeroes ($O(1)$ Auxiliary Space)

> **Problem**: Given an $m \times n$ integer matrix `matrix`, if an element is $0$, set its entire row and column to $0$s. You must do it **in-place** in $O(1)$ extra memory.

### The In-Place Marker Strategy
Instead of allocating $O(M + N)$ boolean arrays to remember which rows and columns contain zeroes, **reuse Row 0 and Column 0 as the marker storage**:

```text
Matrix:
[ 1,  1,  1 ]      Row 0 and Col 0 act as flags:
[ 1,  0,  1 ] ──►  matrix[1][0] = 0 (Flag: Row 1 must be zeroed)
[ 1,  1,  1 ]      matrix[0][1] = 0 (Flag: Col 1 must be zeroed)

Conflict at (0, 0):
matrix[0][0] represents both Row 0 and Col 0!
Solution: Use a separate scalar boolean `col0HasZero` for Col 0, 
          and let matrix[0][0] store the state for Row 0.
```

```java
public class SetMatrixZeroes {
    public void setZeroes(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        boolean col0HasZero = false;

        // Phase 1: Record zeroes into row 0 and col 0 markers
        for (int r = 0; r < rows; r++) {
            if (matrix[r][0] == 0) col0HasZero = true;
            for (int c = 1; c < cols; c++) {
                if (matrix[r][c] == 0) {
                    matrix[r][0] = 0; // Mark row
                    matrix[0][c] = 0; // Mark col
                }
            }
        }

        // Phase 2: Update inner cells using markers (traverse backwards)
        for (int r = rows - 1; r >= 0; r--) {
            for (int c = cols - 1; c >= 1; c--) {
                if (matrix[r][0] == 0 || matrix[0][c] == 0) {
                    matrix[r][c] = 0;
                }
            }
            if (col0HasZero) {
                matrix[r][0] = 0;
            }
        }
    }
}
```

- **Time Complexity**: $O(M \cdot N)$ (Two matrix scans).
- **Auxiliary Space**: $O(1)$ (Directly uses first row/column as bitmasks).

---

## 3. Spiral Matrix Traversal

> **Problem**: Given an $m \times n$ matrix, return all elements of the matrix in spiral order.

### The 4-Boundary Invariant Loop
Maintain four bounding pointers: `top`, `bottom`, `left`, `right`. Traverse clockwise in layers:

```text
top   ──► ┌───────────────────────────┐
          │  1 ──► 2 ──► 3 ──► 4      │
          │                    │      │
          │ 10 ──►11 ──►12     5      │
          │  ▲           │     │      │
          │  9 ◄── 8 ◄── 7 ◄── 6      │
bottom ──► └───────────────────────────┘
             ▲                       ▲
            left                   right
```

```java
import java.util.ArrayList;
import java.util.List;

public class SpiralMatrix {
    public List<Integer> spiralOrder(int[][] matrix) {
        List<Integer> result = new ArrayList<>();
        if (matrix == null || matrix.length == 0) return result;

        int top = 0;
        int bottom = matrix.length - 1;
        int left = 0;
        int right = matrix[0].length - 1;

        while (top <= bottom && left <= right) {
            // 1. Traverse Right along top boundary
            for (int c = left; c <= right; c++) {
                result.add(matrix[top][c]);
            }
            top++;

            // 2. Traverse Down along right boundary
            for (int r = top; r <= bottom; r++) {
                result.add(matrix[r][right]);
            }
            right--;

            // 3. Traverse Left along bottom boundary (verify top <= bottom still holds!)
            if (top <= bottom) {
                for (int c = right; c >= left; c--) {
                    result.add(matrix[bottom][c]);
                }
                bottom--;
            }

            // 4. Traverse Up along left boundary (verify left <= right still holds!)
            if (left <= right) {
                for (int r = bottom; r >= top; r--) {
                    result.add(matrix[r][left]);
                }
                left++;
            }
        }

        return result;
    }
}
```

> [!IMPORTANT]
> The conditional checks `if (top <= bottom)` and `if (left <= right)` before steps 3 and 4 prevent duplicate traversals on matrices with odd row or column counts (e.g. $1 \times N$ or $M \times 1$).

- **Time Complexity**: $O(M \cdot N)$ — Every cell is visited exactly once.
- **Auxiliary Space**: $O(1)$ — Excluding the returned list.

---

## 4. Search a 2D Matrix (Two Paradigms)

Matrix search problems fall into two distinct structural categories:

### Case A: Strictly Sorted Matrix (Search a 2D Matrix I)
Matrix properties:
- Integers in each row are sorted from left to right.
- The first integer of each row is greater than the last integer of the previous row.

**Insight**: The matrix is functionally an unpartitioned 1D sorted array of length $M \times N$. We perform standard **Binary Search**:

```java
public class Search2DMatrixI {
    public boolean searchMatrix(int[][] matrix, int target) {
        int m = matrix.length;
        int n = matrix[0].length;
        int low = 0;
        int high = m * n - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            // Flat coordinate math:
            int r = mid / n;
            int c = mid % n;
            int val = matrix[r][c];

            if (val == target) return true;
            if (val < target) low = mid + 1;
            else high = mid - 1;
        }

        return false;
    }
}
```
- **Time Complexity**: $O(\log(M \cdot N))$.
- **Auxiliary Space**: $O(1)$.

---

### Case B: Row-Wise and Column-Wise Sorted (Search a 2D Matrix II)
Matrix properties:
- Integers in each row are sorted in ascending from left to right.
- Integers in each column are sorted in ascending from top to bottom.
- Consecutive rows are **not** strictly sorted relative to each other.

#### The Saddleback Search Technique
Start at the **Top-Right Corner** $(0, N - 1)$:
- All elements in the same column below are strictly **greater**.
- All elements in the same row to the left are strictly **smaller**.

```text
Start at Top-Right (0, cols-1):
[ 1,   4,   7,  11, [15] ] ──► Target = 5
[ 2,   5,   8,  12,  19  ]
[ 3,   6,   9,  16,  22  ]

Rule:
• If current > target: Decrement col-- (15 > 5: Entire column 4 is eliminated!)
• If current < target: Increment row++ (1 < 5: Entire row 0 is eliminated!)
```

```java
public class Search2DMatrixII {
    public boolean searchMatrix(int[][] matrix, int target) {
        int r = 0;
        int c = matrix[0].length - 1; // Start at Top-Right corner

        while (r < matrix.length && c >= 0) {
            int val = matrix[r][c];
            if (val == target) {
                return true;
            } else if (val > target) {
                c--; // Move left: eliminate entire column
            } else {
                r++; // Move down: eliminate entire row
            }
        }

        return false;
    }
}
```

- **Time Complexity**: $O(M + N)$ — Each step either decrements `c` or increments `r`. Total steps cannot exceed $M + N$.
- **Auxiliary Space**: $O(1)$.

---

## 5. Self-Check & Active Recall

1. **Q**: Can we start Saddleback Search at the Top-Left corner $(0, 0)$?
   - *A*: **No**. At $(0, 0)$, moving right increases the value and moving down also increases the value. You cannot make a deterministic binary choice. You must start at a corner with opposing monotonicity: Top-Right (left decreases, down increases) or Bottom-Left (right increases, up decreases).

2. **Q**: How can you rotate an $N \times N$ matrix $90^\circ$ counter-clockwise in-place?
   - *A*: Either: 1. Transpose across main diagonal, then reverse each column vertically; or 2. Reverse each row horizontally, then transpose.

3. **Q**: In "Set Matrix Zeroes", why do we update the inner matrix cells backwards (from bottom-right to top-left) in Phase 2?
   - *A*: Because Row 0 and Column 0 are acting as the marker storage flags. If we overwrite them forward, we would destroy the flags before other rows and columns have had a chance to read them.

---

## 🧭 Continue Learning

| ◀️ Previous Topic | 🧭 Track Hub | Next Topic ▶️ |
| :--- | :---: | ---: |
| [**Page 05: In-Place Manipulations**](05-in-place-manipulations-and-cyclic-sort.md)<br><sub>*Dutch Flag, Permutations & In-Place Swaps*</sub> | [**Arrays Index**](README.md)<br><sub>*All 9 Modules*</sub> | [**Page 07: 3D Arrays & Multi-Dimensional**](07-3d-arrays-and-advanced-multidimensional-problems.md)<br><sub>*3D BFS, Dual-Agent DP & Voxel Grids*</sub> |
