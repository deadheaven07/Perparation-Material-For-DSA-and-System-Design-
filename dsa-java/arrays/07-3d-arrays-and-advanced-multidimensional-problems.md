# Page 07: 3D Arrays & Advanced Multidimensional Problems

When solving complex algorithmic problems, multi-dimensional arrays often represent **state spaces** rather than purely physical geometries.

A problem with a 2D spatial grid $(r, c)$ and an additional discrete resource (such as remaining obstacles $k$, fuel, keys, or time) expands into a **3D State Graph** $(r, c, k)$. Similarly, when multiple agents traverse a grid simultaneously, their joint coordinates form a 3D dynamic programming state.

---

## 1. 3D State Space Search: Shortest Path with Obstacle Elimination

> **Problem**: You are given an $m \times n$ grid where each cell is either $0$ (empty) or $1$ (obstacle). You can move up, down, left, or right. You are allowed to eliminate at most $k$ obstacles. Return the minimum number of steps to walk from $(0, 0)$ to $(m - 1, n - 1)$. If it is impossible, return $-1$.

### Why Standard 2D BFS Fails
In a standard 2D grid, a cell $(r, c)$ is marked visited once. However, in this problem:
- Path A might reach $(r, c)$ in $4$ steps having consumed $3$ obstacle eliminations.
- Path B might reach $(r, c)$ in $6$ steps having consumed $0$ obstacle eliminations.
Path B takes more steps initially, but retains more obstacle-clearing power, which may be required to break through walls near the destination!

### The 3D State Transformation
Expand the state into a 3-tuple: **$(row, col, remaining\_k)$**.
The visited set becomes a 3D boolean space: `boolean[m][n][k + 1]`.
Alternatively, track the maximum remaining $k$ observed at each 2D cell: `int[][] maxRemainingK = new int[m][n]`.

```text
╭─────────────────────────────────────────────────────────────────────────────╮
│                         3D BFS State Space Expansion                        │
├─────────────────────────────────────────────────────────────────────────────┤
│ (r, c) on 2D Board ──► Expanded into 3D State Layer based on remaining k:   │
│                                                                             │
│ Layer k = 2:  [ (0,0,2) ] ──► [ (0,1,2) ]                                   │
│                     │                                                       │
│                     ▼ (Hits obstacle: drops to lower layer)                 │
│ Layer k = 1:  [ (1,0,1) ] ──► [ (1,1,1) ]                                   │
│                     │                                                       │
│                     ▼ (Hits obstacle: drops to lower layer)                 │
│ Layer k = 0:  [ (2,0,0) ] ──► No more obstacles can be eliminated!          │
╰─────────────────────────────────────────────────────────────────────────────╯
```

```java
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

public class ShortestPathObstaclesElimination {
    private static final int[][] DIRS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    public int shortestPath(int[][] grid, int k) {
        int m = grid.length;
        int n = grid[0].length;

        // Base case: Already at destination
        if (m == 1 && n == 1) return 0;

        // Mathematical Pruning: Manhattan distance is (m - 1) + (n - 1).
        // If k is large enough to blast straight to target, return Manhattan distance directly!
        if (k >= m + n - 2) {
            return m + n - 2;
        }

        // 3D visited tracking: maxRemainingK[r][c] stores highest k seen at (r, c)
        int[][] maxRemainingK = new int[m][n];
        for (int[] row : maxRemainingK) {
            Arrays.fill(row, -1);
        }

        // Queue elements: {row, col, remaining_k, steps}
        Queue<int[]> queue = new ArrayDeque<>();
        queue.offer(new int[]{0, 0, k, 0});
        maxRemainingK[0][0] = k;

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int r = curr[0];
            int c = curr[1];
            int curK = curr[2];
            int steps = curr[3];

            for (int[] dir : DIRS) {
                int nr = r + dir[0];
                int nc = c + dir[1];

                if (nr < 0 || nr >= m || nc < 0 || nc >= n) continue;

                int nextK = curK - grid[nr][nc];

                // If obstacle cannot be eliminated, or we have already visited (nr, nc)
                // with an equal or greater remaining k, prune this branch!
                if (nextK < 0 || nextK <= maxRemainingK[nr][nc]) continue;

                // Reached destination
                if (nr == m - 1 && nc == n - 1) {
                    return steps + 1;
                }

                maxRemainingK[nr][nc] = nextK;
                queue.offer(new int[]{nr, nc, nextK, steps + 1});
            }
        }

        return -1;
    }
}
```

- **Time Complexity**: $O(M \cdot N \cdot K)$ — In the worst case, each cell is visited at most $K + 1$ times.
- **Auxiliary Space**: $O(M \cdot N \cdot K)$ — For the BFS queue and state tracking.

---

## 2. 3D Dynamic Programming: Dual-Agent Simultaneous Traversal

> **Problem (Cherry Pickup II)**: You are given an $r \times c$ grid representing a field of cherries. Two robots start at row $0$: Robot 1 at $(0, 0)$ and Robot 2 at $(0, c - 1)$.
> - In each step, both robots move from row $i$ to row $i + 1$ simultaneously.
> - Robot 1 can move to column $c_1 - 1, c_1,$ or $c_1 + 1$.
> - Robot 2 can move to column $c_2 - 1, c_2,$ or $c_2 + 1$.
> - If both robots land on the same cell, the cherries are collected only **once**.
> Return the maximum cherries collected when both reach row $r - 1$.

### Collapsing 4 Dimensions to 3 Dimensions
A naive state representation would track 4 coordinates: $(r_1, c_1, r_2, c_2)$.
However, because both robots move down one row at each time step, **their row coordinates are always identical**:
$$r_1 = r_2 = r$$
Thus, the 4D state $(r_1, c_1, r_2, c_2)$ collapses into a **3D State: $dp[r][c_1][c_2]$**.

```text
At Row r:
Robot 1 is at (r, c1) ──► Can transition to c1 - 1, c1, c1 + 1 at row r + 1
Robot 2 is at (r, c2) ──► Can transition to c2 - 1, c2, c2 + 1 at row r + 1
Total transition choices per state = 3 × 3 = 9 branch combinations.
```

### Bottom-Up Tabulation with Space Optimization
Because row $r$ depends only on the results of row $r + 1$, we optimize memory from $O(R \cdot C^2)$ down to **$O(C^2)$**:

```java
public class CherryPickupII {
    public int cherryPickup(int[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;

        // dp[c1][c2] stores maximum cherries collected from current row to bottom
        int[][] dp = new int[cols][cols];

        // Base case: Last row (r = rows - 1)
        for (int c1 = 0; c1 < cols; c1++) {
            for (int c2 = 0; c2 < cols; c2++) {
                int cherries = grid[rows - 1][c1];
                if (c1 != c2) {
                    cherries += grid[rows - 1][c2];
                }
                dp[c1][c2] = cherries;
            }
        }

        // Bottom-up DP: Transition from row (rows - 2) up to row 0
        for (int r = rows - 2; r >= 0; r--) {
            int[][] nextDp = new int[cols][cols];

            for (int c1 = 0; c1 < cols; c1++) {
                for (int c2 = 0; c2 < cols; c2++) {
                    int maxFutureCherries = 0;

                    // Explore all 9 possible movement transitions
                    for (int d1 = -1; d1 <= 1; d1++) {
                        for (int d2 = -1; d2 <= 1; d2++) {
                            int nc1 = c1 + d1;
                            int nc2 = c2 + d2;

                            if (nc1 >= 0 && nc1 < cols && nc2 >= 0 && nc2 < cols) {
                                maxFutureCherries = Math.max(maxFutureCherries, dp[nc1][nc2]);
                            }
                        }
                    }

                    int currentCherries = grid[r][c1];
                    if (c1 != c2) {
                        currentCherries += grid[r][c2];
                    }

                    nextDp[c1][c2] = currentCherries + maxFutureCherries;
                }
            }

            dp = nextDp; // Advance rolling 2D DP layer
        }

        // Initial state: Robot 1 at col 0, Robot 2 at col (cols - 1)
        return dp[0][cols - 1];
    }
}
```

- **Time Complexity**: $O(R \cdot C^2 \cdot 9) = O(R \cdot C^2)$.
- **Auxiliary Space**: $O(C^2)$ — Two $C \times C$ rolling 2D arrays.

---

## 3. 3D Spatial Grid Traversals: Voxel Flood Fill

In 3D game engines, computer vision, and medical imaging, 3D grids represent **Voxel Meshes** (Volumetric Pixels).

### Orthogonal Connectivity (6 Neighbors) vs Full Spatial (26 Neighbors)

```text
Orthogonal Neighbors (Face-sharing):
       (x, y+1, z)
            ▲
            │
(x-1, y, z) ┼──► (x+1, y, z)     Plus (x, y, z+1) [Front] and (x, y, z-1) [Back]
            │
            ▼
       (x, y-1, z)
```

```java
import java.util.ArrayDeque;
import java.util.Queue;

public class VoxelConnectedComponents {
    public static final int[][] DIRS_6 = {
        {1, 0, 0}, {-1, 0, 0},
        {0, 1, 0}, {0, -1, 0},
        {0, 0, 1}, {0, 0, -1}
    };

    public int count3DIslands(int[][][] space) {
        int xDim = space.length;
        int yDim = space[0].length;
        int zDim = space[0][0].length;

        boolean[][][] visited = new boolean[xDim][yDim][zDim];
        int islandCount = 0;

        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                for (int z = 0; z < zDim; z++) {
                    if (space[x][y][z] == 1 && !visited[x][y][z]) {
                        islandCount++;
                        bfs3D(space, visited, x, y, z, xDim, yDim, zDim);
                    }
                }
            }
        }

        return islandCount;
    }

    private void bfs3D(int[][][] space, boolean[][][] visited, 
                       int startX, int startY, int startZ, 
                       int xDim, int yDim, int zDim) {
        Queue<int[]> queue = new ArrayDeque<>();
        queue.offer(new int[]{startX, startY, startZ});
        visited[startX][startY][startZ] = true;

        while (!queue.isEmpty()) {
            int[] p = queue.poll();

            for (int[] d : DIRS_6) {
                int nx = p[0] + d[0];
                int ny = p[1] + d[1];
                int nz = p[2] + d[2];

                if (nx >= 0 && nx < xDim && ny >= 0 && ny < yDim && nz >= 0 && nz < zDim
                        && space[nx][ny][nz] == 1 && !visited[nx][ny][nz]) {
                    visited[nx][ny][nz] = true;
                    queue.offer(new int[]{nx, ny, nz});
                }
            }
        }
    }
}
```

- **Time Complexity**: $O(X \cdot Y \cdot Z)$ — Each voxel is enqueued once.
- **Auxiliary Space**: $O(X \cdot Y \cdot Z)$ — For visited array and BFS queue.

---

## 4. Self-Check & Active Recall

1. **Q**: In "Shortest Path with Obstacles Elimination", why is the pruning condition `if (k >= m + n - 2) return m + n - 2;` valid?
   - *A*: The minimum possible steps between $(0, 0)$ and $(m - 1, n - 1)$ on any grid is the Manhattan distance $(m - 1) + (n - 1) = m + n - 2$. Even if every intermediate cell were an obstacle, there are at most $m + n - 2$ cells to step through. If $k$ is $\ge m + n - 2$, we can blast every single obstacle in a straight Manhattan line without detours.

2. **Q**: In "Cherry Pickup II", why can we eliminate the fourth coordinate $r_2$ from the state $(r_1, c_1, r_2, c_2)$?
   - *A*: Both robots move down one row at the exact same step. Therefore, at step $r$, Robot 1 is at row $r$ and Robot 2 is guaranteed to also be at row $r$. $r_1 = r_2 = r$, reducing the state to $(r, c_1, c_2)$.

3. **Q**: What is the memory footprint of a 3D boolean visited array `boolean[200][200][200]` in Java?
   - *A*: It creates $200 \times 200 = 40,000$ 1D boolean arrays of length 200, representing $8,000,000$ booleans ($8$ MB of data) plus $40,201$ object headers ($\approx 643$ KB), fitting comfortably in standard JVM heap memory.

---

## 🧭 Continue Learning

| ◀️ Previous Topic | 🧭 Track Hub | Next Topic ▶️ |
| :--- | :---: | ---: |
| [**Page 06: Matrix Manipulations**](06-matrix-manipulations-and-2d-algorithms.md)<br><sub>*Spiral, In-Place Rotation & Saddleback*</sub> | [**Arrays Index**](README.md)<br><sub>*All 9 Modules*</sub> | [**Page 08: Advanced & Large-Scale Systems**](08-advanced-array-techniques-and-large-scale-systems.md)<br><sub>*Monotonic Deque, Answer-Space & Big Data*</sub> |
