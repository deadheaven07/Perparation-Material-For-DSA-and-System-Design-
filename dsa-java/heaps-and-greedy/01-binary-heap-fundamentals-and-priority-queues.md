# 01. Binary Heap Fundamentals & Priority Queues

[← Back to Heaps Hub](./README.md) | [Track Hub](./README.md) | [Next: Top-K & Selection Paradigms →](./02-top-k-and-selection-paradigms.md)

---

## 1. Heap Mechanics: Sift-Up vs. Sift-Down

A Binary Heap enforces the heap-order property using two complementary primitive operations:

```
╭───────────────────────────────────────────────────────────────────────────────────────────╮
│                                   SIFT-UP (BUBBLE-UP)                                     │
├───────────────────────────────────────────────────────────────────────────────────────────┤
│ • Trigger: An element is inserted at the next available leaf position (index size).      │
│ • Mechanics: While current element < parent, swap with parent.                           │
│ • Maximum Depth: Path from leaf to root = O(log N) comparisons.                           │
╰───────────────────────────────────────────────────────────────────────────────────────────╯

╭───────────────────────────────────────────────────────────────────────────────────────────╮
│                                  SIFT-DOWN (BUBBLE-DOWN / SINK)                           │
├───────────────────────────────────────────────────────────────────────────────────────────┤
│ • Trigger: Root element is extracted (poll). The last leaf element is moved to index 0.  │
│ • Mechanics: While current element > minimum of left/right child, swap with smallest child.│
│ • Maximum Depth: Path from root to leaf = O(log N) comparisons.                           │
╰───────────────────────────────────────────────────────────────────────────────────────────╯
```

```
Sift-Up on inserting 2:                      Sift-Down on polling root:
       4                                            8 (Swapped from last leaf)
     /   \                                        /   \
    7     9                                      3     5
   /                                            / \
 [2] <- New element                            7   6
Step 1: 2 < 7 -> Swap with 7                 Step 1: Smallest child is 3 -> Swap with 3
Step 2: 2 < 4 -> Swap with 4 (Becomes Root!) Step 2: Smallest child is 6 -> Swap with 6
```

---

## 2. Production Java 17/21 Implementation: Custom Generic `ArrayMinHeap<T>`

Below is a complete, production-grade implementation of a binary min-heap supporting dynamic array resizing, custom comparators, and an $\mathcal{O}(N)$ bottom-up `heapify` constructor:

```java
package com.structures.heaps;

import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;

/**
 * High-performance, pointerless Binary Min-Heap backed by a contiguous array.
 * Supports O(1) peek, O(log N) offer/poll, and O(N) heapify construction.
 *
 * @param <T> Element type
 */
public final class ArrayMinHeap<T> {

    private static final int DEFAULT_CAPACITY = 16;

    @SuppressWarnings("unchecked")
    private T[] data = (T[]) new Object[DEFAULT_CAPACITY];
    private int size = 0;
    private final Comparator<? super T> comparator;

    public ArrayMinHeap() {
        this(null);
    }

    public ArrayMinHeap(Comparator<? super T> comparator) {
        this.comparator = comparator;
    }

    /**
     * O(N) Heapify Constructor: Transforms an arbitrary array into a valid heap.
     */
    @SuppressWarnings("unchecked")
    public ArrayMinHeap(T[] elements, Comparator<? super T> comparator) {
        this.comparator = comparator;
        this.size = elements.length;
        this.data = (T[]) new Object[Math.max(DEFAULT_CAPACITY, size * 2)];
        System.arraycopy(elements, 0, this.data, 0, size);
        buildHeap();
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Heap is empty");
        }
        return data[0];
    }

    /**
     * Inserts an element into the heap.
     * Time: O(log N), Space: O(1) amortized.
     */
    public void offer(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Null values are not permitted in heap");
        }
        ensureCapacity();
        data[size] = value;
        siftUp(size);
        size++;
    }

    /**
     * Extracts and returns the minimum element.
     * Time: O(log N), Space: O(1).
     */
    public T poll() {
        if (isEmpty()) {
            throw new NoSuchElementException("Heap is empty");
        }
        T root = data[0];
        data[0] = data[size - 1];
        data[size - 1] = null; // Prevent memory leak
        size--;
        if (size > 0) {
            siftDown(0);
        }
        return root;
    }

    private void buildHeap() {
        // Start from the last internal node: (size / 2) - 1 and sink down
        for (int i = (size >>> 1) - 1; i >= 0; i--) {
            siftDown(i);
        }
    }

    private void siftUp(int index) {
        T key = data[index];
        while (index > 0) {
            int parentIdx = (index - 1) >>> 1;
            T parent = data[parentIdx];
            if (compare(key, parent) >= 0) {
                break;
            }
            data[index] = parent;
            index = parentIdx;
        }
        data[index] = key;
    }

    private void siftDown(int index) {
        T key = data[index];
        int half = size >>> 1; // Nodes >= half are leaves

        while (index < half) {
            int childIdx = (index << 1) + 1; // Left child
            T child = data[childIdx];
            int rightIdx = childIdx + 1;

            if (rightIdx < size && compare(data[rightIdx], child) < 0) {
                childIdx = rightIdx;
                child = data[childIdx];
            }

            if (compare(key, child) <= 0) {
                break;
            }

            data[index] = child;
            index = childIdx;
        }
        data[index] = key;
    }

    @SuppressWarnings("unchecked")
    private int compare(T a, T b) {
        if (comparator != null) {
            return comparator.compare(a, b);
        }
        return ((Comparable<? super T>) a).compareTo(b);
    }

    private void ensureCapacity() {
        if (size == data.length) {
            data = Arrays.copyOf(data, data.length << 1);
        }
    }
}
```

---

## 3. Java Collections Framework: `java.util.PriorityQueue` Under the Hood

When working with Java's standard library `PriorityQueue`, several implementation characteristics directly impact enterprise design:

```
╭───────────────────────────────────────────────────────────────────────────────────────────╮
│                                 JDK PRIORITYQUEUE INTERNALS                               │
├───────────────────────────────────────────────────────────────────────────────────────────┤
│ 1. Initial Capacity: Defaults to 11 elements. Size grows by 100% when < 64, and by 50%    │
│    thereafter (newCapacity = oldCapacity + (oldCapacity < 64 ? oldCapacity + 2 : old >> 1))│
│ 2. Thread Safety Hazard: PriorityQueue is NOT synchronized! In concurrent multi-threaded  │
│    producers/consumers, always use PriorityBlockingQueue.                                 │
│ 3. The O(N) Remove Hazard: Calling pq.remove(element) performs a LINEAR SCAN to find the │
│    index before sinking/swapping. NEVER call remove(element) in a tight loop!             │
╰───────────────────────────────────────────────────────────────────────────────────────────╯
```

---

## 4. The Staff-Level Structure: Indexed Priority Queue (IPQ)

### 4.1 Why Standard Priority Queues Fail in Graph Algorithms
In Dijkstra's algorithm and network routing schedulers, we need to update a node's distance: **`decreaseKey(nodeId, newDist)`**.
- In standard `PriorityQueue`, finding `nodeId` takes $\mathcal{O}(N)$ scan time $\implies$ overall Dijkstra degrades from $\mathcal{O}(E \log V)$ to $\mathcal{O}(V^2)$!
- **The Solution: Indexed Priority Queue (IPQ)**. By maintaining an internal **Position Map (`pm`)**, the heap tracks where each unique key ID currently resides in the heap array, enabling $\mathcal{O}(1)$ key lookup and $\mathcal{O}(\log N)$ `decreaseKey` and `remove(id)`!

```
╭───────────────────────────────────────────────────────────────────────────────────────────╮
│                                 INDEXED PRIORITY QUEUE ARRAYS                             │
├───────────────────────────────────────────────────────────────────────────────────────────┤
│ • values[i]: Stored payload / priority for key ID i.                                      │
│ • im[heapIdx]: Inverse Map -> Maps heap index (0 to size-1) to Key ID.                    │
│ • pm[keyId]: Position Map -> Maps Key ID to its current index in im[] heap array.        │
│                                                                                           │
│ When swapping elements A and B at heap indices i and j:                                   │
│   1. Swap im[i] and im[j].                                                                │
│   2. Update position map: pm[im[i]] = i and pm[im[j]] = j!                                │
╰───────────────────────────────────────────────────────────────────────────────────────────╯
```

---

### 4.2 Production Java 17/21 Implementation: `IndexedMinPriorityQueue`

```java
package com.structures.heaps;

import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * Indexed Binary Min-Priority Queue.
 * Supports O(1) key containment checks, O(log N) updates (decreaseKey), and O(log N) deletions.
 */
public final class IndexedMinPriorityQueue<T extends Comparable<T>> {

    private final int capacity;
    private int size = 0;

    private final int[] im;     // Inverse map: heapIndex -> keyId
    private final int[] pm;     // Position map: keyId -> heapIndex
    private final Object[] val; // Associated values: keyId -> value

    public IndexedMinPriorityQueue(int capacity) {
        this.capacity = capacity;
        this.im = new int[capacity];
        this.pm = new int[capacity];
        this.val = new Object[capacity];
        Arrays.fill(pm, -1);
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean contains(int keyId) {
        validateKey(keyId);
        return pm[keyId] != -1;
    }

    public int peekKeyId() {
        if (isEmpty()) throw new NoSuchElementException("Heap is empty");
        return im[0];
    }

    @SuppressWarnings("unchecked")
    public T peekValue() {
        if (isEmpty()) throw new NoSuchElementException("Heap is empty");
        return (T) val[im[0]];
    }

    /**
     * Inserts value associated with keyId in O(log N) time.
     */
    public void insert(int keyId, T value) {
        if (contains(keyId)) {
            throw new IllegalArgumentException("Key ID already exists: " + keyId);
        }
        pm[keyId] = size;
        im[size] = keyId;
        val[keyId] = value;
        siftUp(size);
        size++;
    }

    /**
     * Polls the minimum key ID in O(log N) time.
     */
    public int pollKeyId() {
        int minKey = peekKeyId();
        delete(minKey);
        return minKey;
    }

    /**
     * Deletes arbitrary keyId from heap in O(log N) time.
     */
    public void delete(int keyId) {
        if (!contains(keyId)) {
            throw new NoSuchElementException("Key does not exist: " + keyId);
        }
        int idx = pm[keyId];
        swap(idx, size - 1);
        size--;
        siftDown(idx);
        siftUp(idx);
        val[keyId] = null;
        pm[keyId] = -1;
    }

    /**
     * Decreases the value of an existing keyId in O(log N) time.
     */
    public void decreaseKey(int keyId, T newValue) {
        if (!contains(keyId)) {
            throw new NoSuchElementException("Key does not exist: " + keyId);
        }
        if (compareValues(newValue, (T) val[keyId]) > 0) {
            throw new IllegalArgumentException("New value is greater than existing value");
        }
        val[keyId] = newValue;
        siftUp(pm[keyId]);
    }

    private void siftUp(int i) {
        while (i > 0) {
            int p = (i - 1) >>> 1;
            if (compareValues((T) val[im[i]], (T) val[im[p]]) >= 0) break;
            swap(i, p);
            i = p;
        }
    }

    private void siftDown(int i) {
        int half = size >>> 1;
        while (i < half) {
            int left = (i << 1) + 1;
            int right = left + 1;
            int smallest = left;

            if (right < size && compareValues((T) val[im[right]], (T) val[im[left]]) < 0) {
                smallest = right;
            }

            if (compareValues((T) val[im[i]], (T) val[im[smallest]]) <= 0) break;

            swap(i, smallest);
            i = smallest;
        }
    }

    private void swap(int i, int j) {
        int keyI = im[i];
        int keyJ = im[j];

        im[i] = keyJ;
        im[j] = keyI;

        pm[keyI] = j;
        pm[keyJ] = i;
    }

    @SuppressWarnings("unchecked")
    private int compareValues(T a, T b) {
        return a.compareTo(b);
    }

    private void validateKey(int keyId) {
        if (keyId < 0 || keyId >= capacity) {
            throw new IndexOutOfBoundsException("Key index out of bounds: " + keyId);
        }
    }
}
```

---

## 5. Dry-Run Trace Table & Interviewer Stress Defenses

#### Dry-Run: Sift-Down during Poll
Given Min-Heap array `[1, 3, 5, 7, 9, 6]` ($N=6$):
1. Extract root `1`. Swap last element `6` to index `0`. Size becomes `5`. Current array: `[6, 3, 5, 7, 9]`.
2. `siftDown(0)`:
   - Left child at index 1 (`3`), Right child at index 2 (`5`).
   - Smallest child is `3` (index 1).
   - `6 > 3` $\implies$ Swap index 0 and 1! Array: `[3, 6, 5, 7, 9]`.
3. Current index is 1:
   - Left child at index 3 (`7`), Right child at index 4 (`9`).
   - Smallest child is `7` (index 3).
   - `6 <= 7` $\implies$ Invariant satisfied! Terminate. Result array: `[3, 6, 5, 7, 9]`.

#### Interviewer Stress Defenses:
- **Why is `buildHeap()` linear while repeated insertions are $\mathcal{O}(N \log N)$?**
  > Most nodes in a tree reside near the bottom. When inserting one by one, leaves travel upward across $\log N$ levels. In `buildHeap()`, leaves are processed first and travel downward across $0$ levels!
- **Why not use `PriorityQueue.remove(Object)` in sliding window problems?**
  > Because standard `PriorityQueue` has no inverted index map, calling `remove(o)` requires an $\mathcal{O}(N)$ scan. Over $N$ elements, this degrades the entire algorithm to $\mathcal{O}(N^2)$! Always use **Lazy Deletion** via Hash Map or an **Indexed Priority Queue**.

---

<table width="100%">
  <tr>
    <td width="33%" align="left">
      <a href="./README.md">
        <strong>← Previous Module</strong><br>
        Heaps & Greedy Track Hub
      </a>
    </td>
    <td width="33%" align="center">
      <a href="../README.md">
        <strong>Home</strong><br>
        Java DSA Master Track
      </a>
    </td>
    <td width="33%" align="right">
      <a href="./02-top-k-and-selection-paradigms.md">
        <strong>Next Module →</strong><br>
        02. Top-K & Selection Paradigms
      </a>
    </td>
  </tr>
</table>
