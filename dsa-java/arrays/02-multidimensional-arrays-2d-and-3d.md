# Page 02: Multidimensional Arrays (2D & 3D)

In algorithms and high-throughput systems, multi-dimensional data structures model grids, matrices, coordinate planes, game boards, image pixels, and spatial tensor fields.

Understanding how multidimensional arrays actually exist in Java's memory model—and how to mathematically flatten and unflatten multidimensional coordinates—is essential for writing clean, cache-friendly code.

---

## 1. The Java Memory Reality: "Array of Arrays"

In languages like C or C++, a 2D array `int matrix[3][4]` is allocated as a single, contiguous block of $3 \times 4 \times 4 = 48$ consecutive bytes in RAM.

**Java does NOT do this.** In Java, all multidimensional arrays are strictly **arrays of arrays**:

```java
int[][] matrix = new int[3][4];
```

```text
╭─────────────────────────────────────────────────────────────────────────────╮
│                        JVM Heap Reality: int[3][4]                          │
├─────────────────────────────────────────────────────────────────────────────┤
│ Top-Level "Spine" Array (matrix):                                           │
│ [Header: 16B] ──► [ Row 0 Pointer ] [ Row 1 Pointer ] [ Row 2 Pointer ]    │
│                            │                 │                 │            │
│                            ▼                 ▼                 ▼            │
│ Row 0 Array:        [Header: 16B]     [Header: 16B]     [Header: 16B]       │
│                     [ 0 ][ 1 ][ 2 ][ 3 ] [ 4 ][ 5 ][ 6 ][ 7 ] [ 8 ][ 9 ][10][11] │
╰─────────────────────────────────────────────────────────────────────────────╯
```

### Implications of the "Array of Arrays" Model
1. **Multiple Heap Allocations**: `new int[3][4]` does not perform one allocation. It performs **$4$ separate heap allocations**: $1$ array of references of length 3, plus $3$ distinct 1D arrays of length 4.
2. **Non-Contiguous Rows**: Each row array can be allocated at an arbitrary, non-consecutive address anywhere on the heap.
3. **Pointer Indirection**: Accessing `matrix[r][c]` requires the CPU to:
   - Read the reference stored at `matrix[r]`.
   - Dereference that pointer to locate the target row array in memory.
   - Read the primitive value at index `c` within that row array.

---

## 2. Jagged (Ragged) Arrays

Because each row is an independent heap object, Java natively supports **Jagged Arrays** where each row has a different length:

```java
// Allocating only the spine array first:
int[][] triangle = new int[4][];

triangle[0] = new int[1]; // Row 0: length 1
triangle[1] = new int[2]; // Row 1: length 2
triangle[2] = new int[3]; // Row 2: length 3
triangle[3] = new int[4]; // Row 3: length 4
```

```text
triangle[0] ──► [ 1 ]
triangle[1] ──► [ 1 ][ 1 ]
triangle[2] ──► [ 1 ][ 2 ][ 1 ]
triangle[3] ──► [ 1 ][ 3 ][ 3 ][ 1 ]  (Pascal's Triangle representation)
```

> [!TIP]
> Jagged arrays conserve substantial memory when representing triangular matrices, adjacency lists for sparse graphs, or ragged input datasets, avoiding wasted padding cells.

---

## 3. Cache Dynamics: Row-Major vs. Column-Major Iteration

Even though different rows are separated on the heap, **each individual row array is 100% contiguous**. This creates an enormous performance difference between row-major and column-major traversals:

```java
int rows = 10000;
int cols = 10000;
int[][] matrix = new int[rows][cols];

// ✅ OPTION A: Row-Major Traversal (Cache-Optimal)
long sum = 0;
for (int r = 0; r < rows; r++) {
    for (int c = 0; c < cols; c++) {
        sum += matrix[r][c]; // Sequential access within the same row array!
    }
}

// ❌ OPTION B: Column-Major Traversal (Cache-Destructive)
for (int c = 0; c < cols; c++) {
    for (int r = 0; r < rows; r++) {
        sum += matrix[r][c]; // Jumps to a completely different heap array on EVERY iteration!
    }
}
```

```text
Row-Major (matrix[r][c]):
Row 0: [ 00 ][ 01 ][ 02 ][ 03 ] ──► All 4 ints loaded into single 64B cache line!
       ◄────── Sequential ─────►

Column-Major (matrix[r][c] with outer col loop):
Step 1: matrix[0][0] (Row 0 heap object)
Step 2: matrix[1][0] (Row 1 heap object - Cache Miss!)
Step 3: matrix[2][0] (Row 2 heap object - Cache Miss!)
```

In large matrices, **Option A is frequently $5\times$ to $15\times$ faster** than Option B strictly due to hardware CPU cache utilization.

---

## 4. 3D Arrays in Java: Structure & Object Explosion

A 3D array in Java (`int[Depth][Rows][Cols]`) is an array of arrays of arrays:

```java
int[][][] tensor = new int[3][4][5];
```

```text
tensor (Spine array of length 3)
  ├── tensor[0] (Array of 4 row pointers)
  │     ├── tensor[0][0] ──► int[5]
  │     ├── tensor[0][1] ──► int[5]
  │     ├── tensor[0][2] ──► int[5]
  │     └── tensor[0][3] ──► int[5]
  ├── tensor[1] (Array of 4 row pointers) ...
  └── tensor[2] (Array of 4 row pointers) ...
```

### The Object Explosion Problem
For dimensions $D \times R \times C$:
$$\text{Total Heap Objects} = 1 + D + (D \times R)$$
For a modest 3D grid of $100 \times 100 \times 100$:
- Total primitive elements: $1,000,000$ integers ($4$ MB).
- Total heap objects created: $1 + 100 + (100 \times 100) = \mathbf{10,101\text{ distinct objects!}}$
- Header overhead: $10,101 \times 16\text{ bytes} \approx 161.6\text{ KB}$ of metadata + $10,101$ GC tracking handles.

---

## 5. Flat 1D Array Simulation & Coordinate Mathematics

In competitive programming, game engines, and high-performance engineering, multidimensional arrays are almost always simulated using a **single flat 1D array**.

### The 2D Flat Index Mapping
Given a 2D matrix of dimensions $R \times C$:

$$\text{flatIndex}(r, c) = r \times C + c$$

$$\text{Row: } r = \left\lfloor \frac{\text{flatIndex}}{C} \right\rfloor, \quad \text{Col: } c = \text{flatIndex} \pmod C$$

```text
Logical 2D Grid (3 Rows, 4 Cols):
        Col 0   Col 1   Col 2   Col 3
Row 0: [  0  ] [  1  ] [  2  ] [  3  ]
Row 1: [  4  ] [  5  ] [  6  ] [  7  ]
Row 2: [  8  ] [  9  ] [ 10  ] [ 11  ]

Physical 1D Array:
Index:   0   1   2   3   4   5   6   7   8   9  10  11
Value: [ 0 ][ 1 ][ 2 ][ 3 ][ 4 ][ 5 ][ 6 ][ 7 ][ 8 ][ 9 ][10][11]

Example: Target coordinate (Row 2, Col 1)
flatIndex = 2 * 4 + 1 = 9.
Reverse:  r = 9 / 4 = 2,  c = 9 % 4 = 1.
```

### The 3D Flat Index Mapping
Given a 3D grid with dimensions $X \times Y \times Z$ (e.g. Depth $X$, Rows $Y$, Columns $Z$):

$$\text{flatIndex}(x, y, z) = x \times (Y \times Z) + y \times Z + z$$

$$\text{Reverse Coordinates:}$$
$$x = \left\lfloor \frac{\text{flatIndex}}{Y \times Z} \right\rfloor$$
$$y = \left\lfloor \frac{\text{flatIndex} \pmod{Y \times Z}}{Z} \right\rfloor$$
$$z = \text{flatIndex} \pmod Z$$

```java
// Production Utility Class for Flat 3D Grid
public class Flat3DGrid {
    private final int xDim, yDim, zDim;
    private final int yzStride;
    private final int[] data;

    public Flat3DGrid(int x, int y, int z) {
        this.xDim = x;
        this.yDim = y;
        this.zDim = z;
        this.yzStride = y * z;
        this.data = new int[x * y * z]; // Exactly ONE heap allocation!
    }

    public int get(int x, int y, int z) {
        return data[x * yzStride + y * zDim + z];
    }

    public void set(int x, int y, int z, int val) {
        data[x * yzStride + y * zDim + z] = val;
    }
}
```

---

## 6. Canonical 2D Matrix Traversal Routines

Master these standard traversal patterns; they appear across hundreds of interview questions:

### 1. Main Diagonal and Anti-Diagonal Traversals
In any square matrix $N \times N$:

```text
╭─────────────────────────────────────────────────────────────────────────────╮
│                        Matrix Diagonal Invariants                           │
├─────────────────────────────────────────────────────────────────────────────┤
│ Main Diagonal (Top-Left to Bottom-Right):                                   │
│ • Formula: row == col                                                       │
│ • Parallel Diagonals: (row - col) is CONSTANT for every parallel diagonal!  │
│                                                                             │
│ Anti-Diagonal (Top-Right to Bottom-Left):                                   │
│ • Formula: row + col == N - 1                                               │
│ • Parallel Anti-Diagonals: (row + col) is CONSTANT for every diagonal!     │
╰─────────────────────────────────────────────────────────────────────────────╯
```

```java
// Traverse all elements on the main diagonal
for (int i = 0; i < n; i++) {
    int val = matrix[i][i];
}

// Traverse all elements on the anti-diagonal
for (int i = 0; i < n; i++) {
    int val = matrix[i][n - 1 - i];
}
```

### 2. Boundary / Perimeter Traversal
Traversing the outer shell of an $M \times N$ matrix:

```java
public void printPerimeter(int[][] matrix) {
    int rows = matrix.length;
    int cols = matrix[0].length;

    // Top edge: Left to Right
    for (int c = 0; c < cols; c++) System.out.print(matrix[0][c] + " ");
    
    // Right edge: Top+1 to Bottom
    for (int r = 1; r < rows; r++) System.out.print(matrix[r][cols - 1] + " ");
    
    // Bottom edge: Right-1 to Left (if rows > 1)
    if (rows > 1) {
        for (int c = cols - 2; c >= 0; c--) System.out.print(matrix[rows - 1][c] + " ");
    }
    
    // Left edge: Bottom-1 to Top+1 (if cols > 1)
    if (cols > 1) {
        for (int r = rows - 2; r > 0; r--) System.out.print(matrix[r][0] + " ");
    }
}
```

---

## 7. Canonical 3D Grid Spatial Exploration: Direction Vectors

When exploring 2D and 3D grids (e.g. BFS, DFS, Flood Fill, Pathfinding), never write 6 or 26 nested `if` statements. Use **Direction Vectors**:

### 2D: 4-Directional vs. 8-Directional Neighbors
```java
// 4-Orthogonal directions: Up, Down, Left, Right
int[][] DIRS_4 = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

// 8-Directions (including diagonals)
int[][] DIRS_8 = {
    {-1, 0}, {1, 0}, {0, -1}, {0, 1},
    {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
};
```

### 3D: 6-Orthogonal Neighbors
In a 3D coordinate system $(x, y, z)$:

```java
// 6 Orthogonal neighbors: ±X, ±Y, ±Z
public static final int[][] DIRS_6 = {
    { 1,  0,  0}, // +X
    {-1,  0,  0}, // -X
    { 0,  1,  0}, // +Y
    { 0, -1,  0}, // -Y
    { 0,  0,  1}, // +Z
    { 0,  0, -1}  // -Z
};

for (int[] d : DIRS_6) {
    int nx = x + d[0];
    int ny = y + d[1];
    int nz = z + d[2];
    
    if (nx >= 0 && nx < xDim && ny >= 0 && ny < yDim && nz >= 0 && nz < zDim) {
        // Valid 3D neighbor
    }
}
```

---

## 8. Self-Check & Active Recall

1. **Q**: In Java, does `int[][] a = new int[5][5]` allocate memory contiguously in RAM?
   - *A*: No. It allocates 1 array of 5 reference pointers and 5 independent row arrays of 5 integers scattered on the heap.

2. **Q**: Given a 2D matrix with $C = 6$ columns, what are the row and column coordinates corresponding to `flatIndex = 29`?
   - *A*: $r = 29 / 6 = 4$, $c = 29 \pmod 6 = 5 \rightarrow (4, 5)$.

3. **Q**: Why is iterating column-by-column through a $10,000 \times 10,000$ Java integer matrix significantly slower than row-by-row?
   - *A*: Column traversal accesses `matrix[r][c]` with an incrementing row index on each step. Because each row is a separate heap object, every read incurs a pointer dereference and a likely CPU cache miss. Row traversal sequentially reads consecutive integers inside the same contiguous row array, maximizing 64-byte cache line hits.

---

## 🧭 Continue Learning

| ◀️ Previous Topic | 🧭 Track Hub | Next Topic ▶️ |
| :--- | :---: | ---: |
| [**Page 01: 1D Arrays & Memory**](01-array-fundamentals-and-memory-architecture.md)<br><sub>*JVM Heap, Cache Lines & ArrayList*</sub> | [**Arrays Index**](README.md)<br><sub>*All 9 Modules*</sub> | [**Page 03: Two Pointers & Sliding Window**](03-two-pointers-and-sliding-window.md)<br><sub>*Opposite Ends & Expanding Windows*</sub> |
