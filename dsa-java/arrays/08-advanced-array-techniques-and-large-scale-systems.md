# Page 08: Advanced Array Techniques & Large-Scale Systems

When array sizes scale into tens of millions of elements—or when array operations must execute with sub-microsecond latency—standard patterns give way to **amortized monotonic structures, answer-space binary searches, and out-of-core streaming architectures**.

---

## 1. Sliding Window Maximum: Monotonic Deque Pattern

> **Problem**: You are given an array of integers `nums`, there is a sliding window of size `k` which is moving from the very left of the array to the very right. You can only see the `k` numbers in the window. Each time the sliding window moves right by one position. Return the max sliding window.

### Why PriorityQueue (Heap) is Suboptimal
A `PriorityQueue<Integer>` can maintain the maximum in $O(\log K)$ time, but removing elements falling out of the window takes $O(K)$ linear search time, yielding an unacceptable $O(N \cdot K)$ overall complexity.

### The Monotonic Decreasing Deque Invariant
We use an `ArrayDeque<Integer>` storing **indices**, maintaining elements in **strictly descending order of value**:

```text
╭─────────────────────────────────────────────────────────────────────────────╮
│                        Monotonic Deque Invariant                            │
├─────────────────────────────────────────────────────────────────────────────┤
│ Indices stored: [ i1,  i2,  i3 ]                                            │
│ Values:         nums[i1] > nums[i2] > nums[i3]                              │
│                                                                             │
│ • Front of Deque (peekFirst): Always holds the index of the MAXIMUM value   │
│   in the current window!                                                    │
│ • Adding element nums[i]: Pop all elements from back (pollLast) that are    │
│   <= nums[i]. (If a newer element is larger, the older smaller elements     │
│   can NEVER become the maximum in any future window!)                       │
╰─────────────────────────────────────────────────────────────────────────────╯
```

```java
import java.util.ArrayDeque;
import java.util.Deque;

public class SlidingWindowMaximum {
    public int[] maxSlidingWindow(int[] nums, int k) {
        int n = nums.length;
        int[] result = new int[n - k + 1];
        int resIdx = 0;

        // Deque stores array indices
        Deque<Integer> deque = new ArrayDeque<>();

        for (int i = 0; i < n; i++) {
            // 1. Evict indices that have fallen out of the window on the left
            while (!deque.isEmpty() && deque.peekFirst() < i - k + 1) {
                deque.pollFirst();
            }

            // 2. Remove indices from the back whose values are <= nums[i]
            while (!deque.isEmpty() && nums[deque.peekLast()] <= nums[i]) {
                deque.pollLast();
            }

            // 3. Add current index to back of deque
            deque.offerLast(i);

            // 4. Record the maximum (at the front) once first window is formed
            if (i >= k - 1) {
                result[resIdx++] = nums[deque.peekFirst()];
            }
        }

        return result;
    }
}
```

#### Step-by-Step Dry Run Trace
Input: `nums = [1, 3, -1, -3, 5, 3, 6, 7]`, `k = 3`

| $i$ | `nums[i]` | Evict Out of Window? | Remove Smaller from Back? | Deque (Indices) | Deque (Values) | `result` added |
| :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **0** | 1 | No | None | `[0]` | `[1]` | - |
| **1** | 3 | No | Pop index 0 ($1 \le 3$) | `[1]` | `[3]` | - |
| **2** | -1 | No | None ($-1 < 3$) | `[1, 2]` | `[3, -1]` | `result[0] = 3` |
| **3** | -3 | No | None ($-3 < -1$) | `[1, 2, 3]` | `[3, -1, -3]` | `result[1] = 3` |
| **4** | 5 | Evict index 1 ($1 < 2$) | Pop index 3 & 2 | `[4]` | `[5]` | `result[2] = 5` |
| **5** | 3 | No | None ($3 < 5$) | `[4, 5]` | `[5, 3]` | `result[3] = 5` |
| **6** | 6 | No | Pop index 5 & 4 | `[6]` | `[6]` | `result[4] = 6` |
| **7** | 7 | No | Pop index 6 ($6 \le 7$) | `[7]` | `[7]` | `result[5] = 7` |

- **Time Complexity**: $O(N)$ — Every element is pushed into the deque once and popped at most once.
- **Auxiliary Space**: $O(K)$ — Deque stores at most $K$ indices.

---

## 2. Binary Search on Answer Space: Split Array Largest Sum

> **Problem**: Given an integer array `nums` and an integer `k`, split `nums` into `k` non-empty subarrays such that the largest sum of any subarray is **minimized**. Return the minimized largest sum.

### The Monotonic Feasibility Predicate
Instead of binary searching for an index inside the array, we binary search over the **range of possible answers**:
- **Minimum possible answer** (`low`): $\max(nums)$ (Each element must fit in at least one subarray).
- **Maximum possible answer** (`high`): $\sum(nums)$ (All elements packed into a single subarray).

For any candidate sum `mid`, checking whether `nums` can be split into $\le k$ subarrays with max sum $\le mid$ takes a simple greedy $O(N)$ linear pass!

```java
public class SplitArrayLargestSum {
    public int splitArray(int[] nums, int k) {
        long low = 0;
        long high = 0;

        for (int num : nums) {
            low = Math.max(low, num);
            high += num;
        }

        long optimalMaxSum = high;

        while (low <= high) {
            long mid = low + (high - low) / 2;

            if (canSplit(nums, k, mid)) {
                optimalMaxSum = mid;
                high = mid - 1; // Try to minimize further
            } else {
                low = mid + 1;  // Candidate mid is too small; increase boundary
            }
        }

        return (int) optimalMaxSum;
    }

    private boolean canSplit(int[] nums, int k, long maxAllowedSum) {
        int subarraysUsed = 1;
        long currentRunningSum = 0;

        for (int num : nums) {
            if (currentRunningSum + num > maxAllowedSum) {
                subarraysUsed++;
                currentRunningSum = num;
                if (subarraysUsed > k) return false;
            } else {
                currentRunningSum += num;
            }
        }

        return true;
    }
}
```

- **Time Complexity**: $O(N \cdot \log(\sum nums - \max(nums)))$.
- **Auxiliary Space**: $O(1)$.

---

## 3. 2D Grid with 3D Elevation: Trapping Rain Water II

> **Problem**: Given an $m \times n$ integer matrix `heightMap` representing the height of each unit cell in a 2D elevation map, return the volume of water it can trap after raining.

### Why 2 Pointers Fails on 2D Grids
On a 1D line, water spills only left or right. On a 2D grid, water spills in **4 directions**. Water will leak out through the **lowest perimeter wall**.

### The Min-Heap Boundary BFS Algorithm
1. Push all outer boundary cells into a **Min-Heap** (`PriorityQueue`).
2. Pop the cell with the lowest height (the current bottleneck).
3. Inspect its 4 neighbors:
   - If a neighbor is lower than the current boundary height, it traps water:
     $$\text{Water} += \max(0, \text{currentBoundaryHeight} - \text{neighborHeight})$$
   - Push the neighbor into the heap with its effective height $\max(\text{neighborHeight}, \text{currentBoundaryHeight})$.
   - Repeat until the heap is empty.

```java
import java.util.PriorityQueue;

public class TrappingRainWaterII {
    public int trapRainWater(int[][] heightMap) {
        if (heightMap == null || heightMap.length <= 2 || heightMap[0].length <= 2) {
            return 0;
        }

        int m = heightMap.length;
        int n = heightMap[0].length;
        boolean[][] visited = new boolean[m][n];

        // Min-heap ordered by height: cell = {row, col, height}
        PriorityQueue<int[]> minHeap = new PriorityQueue<>((a, b) -> Integer.compare(a[2], b[2]));

        // Push perimeter boundary cells into heap
        for (int r = 0; r < m; r++) {
            minHeap.offer(new int[]{r, 0, heightMap[r][0]});
            minHeap.offer(new int[]{r, n - 1, heightMap[r][n - 1]});
            visited[r][0] = true;
            visited[r][n - 1] = true;
        }
        for (int c = 1; c < n - 1; c++) {
            minHeap.offer(new int[]{0, c, heightMap[0][c]});
            minHeap.offer(new int[]{m - 1, c, heightMap[m - 1][c]});
            visited[0][c] = true;
            visited[m - 1][c] = true;
        }

        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        int totalWater = 0;

        while (!minHeap.isEmpty()) {
            int[] curr = minHeap.poll();
            int r = curr[0], c = curr[1], h = curr[2];

            for (int[] d : dirs) {
                int nr = r + d[0];
                int nc = c + d[1];

                if (nr >= 0 && nr < m && nc >= 0 && nc < n && !visited[nr][nc]) {
                    visited[nr][nc] = true;
                    // If neighbor is lower than current boundary, it traps water!
                    totalWater += Math.max(0, h - heightMap[nr][nc]);
                    // Push neighbor with updated boundary elevation
                    minHeap.offer(new int[]{nr, nc, Math.max(h, heightMap[nr][nc])});
                }
            }
        }

        return totalWater;
    }
}
```

- **Time Complexity**: $O(M \cdot N \log(M \cdot N))$.
- **Auxiliary Space**: $O(M \cdot N)$.

---

## 4. Large-Scale Systems: Arrays Exceeding Available RAM

When dealing with datasets containing $10^{10}$ elements ($40$ GB) on a server with only $8$ GB of RAM, arrays cannot exist purely in memory.

### Architecture 1: External K-Way Merge Sort

```text
╭─────────────────────────────────────────────────────────────────────────────╮
│                     External K-Way Merge Sort Pipeline                      │
├─────────────────────────────────────────────────────────────────────────────┤
│ 40 GB Unsorted File on SSD                                                  │
│      │                                                                      │
│      ▼                                                                      │
│ Step 1: Read 1 GB chunks into RAM ──► Sort in-memory ──► Write sorted runs   │
│         (Generates 40 sorted chunk files on disk: run_0.bin ... run_39.bin)  │
│                                                                             │
│ Step 2: Multi-Way Merge Phase                                               │
│         Open 40 buffered file input streams simultaneously                  │
│         Maintain a Min-Heap of size 40 (one element per run file)           │
│         Pop minimum element from heap ──► Append to final output buffer      │
│         Read next element from the file stream that produced the minimum    │
╰─────────────────────────────────────────────────────────────────────────────╯
```

### Architecture 2: Memory-Mapped Files (`FileChannel.map`)
Java's `java.nio` package allows mapping physical disk files directly into the virtual address space of the OS:

```java
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MemoryMappedArray {
    public static void readMassiveArray(String filePath) throws Exception {
        try (RandomAccessFile file = new RandomAccessFile(filePath, "r");
             FileChannel channel = file.getChannel()) {
            
            // Map 1 GB of file directly into virtual memory (zero JVM heap allocation)
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, 1024 * 1024 * 1024);
            
            while (buffer.hasRemaining()) {
                int val = buffer.getInt(); // Paged directly by OS kernel from disk cache
            }
        }
    }
}
```

---

## 5. Streaming Arrays: Reservoir Sampling

> **Problem**: Select $k$ elements uniformly at random from an infinite or unknown stream of array elements, where the total stream length $N$ is unknown in advance and elements cannot all be stored in RAM.

### Algorithm (Algorithm R)
1. Store the first $k$ elements directly in the reservoir array.
2. For each subsequent element at index $i$ ($i \ge k$, 0-indexed):
   - Generate a random integer $r \in [0 \dots i]$.
   - If $r < k$, replace `reservoir[r]` with the new element `stream[i]`.

```java
import java.util.Random;

public class ReservoirSampling {
    public int[] sample(int[] stream, int k) {
        int[] reservoir = new int[k];
        Random random = new Random();

        // 1. Fill reservoir with first k elements
        for (int i = 0; i < k; i++) {
            reservoir[i] = stream[i];
        }

        // 2. Process stream from index k onward
        for (int i = k; i < stream.length; i++) {
            int j = random.nextInt(i + 1); // Random index in [0..i]
            if (j < k) {
                reservoir[j] = stream[i];
            }
        }

        return reservoir;
    }
}
```

### Mathematical Proof of Uniform Probability
Every element in the stream has an **exact probability of $k / N$** of being included in the final reservoir:
$$\Pr(\text{element } i \text{ is in reservoir at step } N) = \frac{k}{i} \times \left(1 - \frac{1}{i+1}\right) \times \left(1 - \frac{1}{i+2}\right) \dots \left(1 - \frac{1}{N}\right) = \frac{k}{N}$$

---

## 6. Numeric Overflow Defenses in Large Arrays

When processing arrays with values near `Integer.MAX_VALUE` ($2.14 \times 10^9$):

1. **Midpoint Calculation**:
   ```java
   // ❌ BAD: (low + high) can exceed Integer.MAX_VALUE and wrap to negative!
   int mid = (low + high) / 2;

   // ✅ SAFE: Subtract before adding
   int mid = low + (high - low) / 2;
   // OR using unsigned bitshift:
   int mid = (low + high) >>> 1;
   ```

2. **Prefix Sum Accumulators**:
   Always accumulate sums using 64-bit `long` to prevent overflow:
   ```java
   long runningSum = 0;
   for (int num : nums) {
       runningSum += num;
   }
   ```

---

## 7. Self-Check & Active Recall

1. **Q**: Why does Monotonic Deque achieve $O(N)$ overall time for Sliding Window Maximum even though the inner while loops pop elements?
   - *A*: Each element is inserted into the deque exactly once and removed from the deque at most once. Over the course of the algorithm, the total number of deque operations across all loop iterations is bounded by $2N = O(N)$.

2. **Q**: In Reservoir Sampling, what is the probability that the 100th element in the stream is selected into a reservoir of size 10 at step 100?
   - *A*: $\frac{k}{i} = \frac{10}{100} = 10\%$.

3. **Q**: Why can't standard Binary Search be used directly on the answer in "Split Array Largest Sum"?
   - *A*: The answer space (range of possible maximum subarray sums) is sorted and monotonic, allowing binary search. If a max sum of $M$ is feasible, any max sum $> M$ is also feasible. If $M$ is infeasible, any sum $< M$ is strictly infeasible.

---

## 🧭 Continue Learning

| ◀️ Previous Topic | 🧭 Track Hub | Next Track ▶️ |
| :--- | :---: | ---: |
| [**Page 07: 3D Arrays & Problems**](07-3d-arrays-and-advanced-multidimensional-problems.md)<br><sub>*3D BFS, Dual-Agent DP & Voxel Grids*</sub> | [**Arrays Index**](README.md)<br><sub>*All 9 Modules Complete*</sub> | [**Java Track Hub**](../README.md)<br><sub>*Fundamentals & Backend Engineering*</sub> |
