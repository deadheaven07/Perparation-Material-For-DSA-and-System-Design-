# Module 04: Self-Balancing Trees, Segment Trees & Fenwick Trees

A naive Binary Search Tree guarantees $O(\log N)$ operations only when balanced. If elements arrive in sorted order, the tree degrades into an $O(N)$ linear linked list. This module develops the mathematical foundations of **AVL Tree Rotations (LL, RR, LR, RL)** to guarantee logarithmic height, builds **Segment Trees with Lazy Propagation** for dynamic range operations, and demonstrates the bitwise beauty of **Fenwick Trees (Binary Indexed Trees)**.

---

## 🏛️ 1. The Balancing Invariant: AVL Tree Rotations

```text
╭─────────────────────────────────────────────────────────────────────────────────────────────╮
│                         AVL TREE ROTATION TAXONOMY                                          │
├─────────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                             │
│ Balance Factor: BF(node) = height(node.left) - height(node.right)                          │
│ Invariant: An AVL tree strictly requires BF(node) ∈ {-1, 0, 1} for all nodes.               │
│                                                                                             │
│ 1. Left-Left (LL) Case: BF(node) > 1 and BF(node.left) >= 0                                 │
│    Fix: Single Right Rotation on node.                                                      │
│                                                                                             │
│ 2. Right-Right (RR) Case: BF(node) < -1 and BF(node.right) <= 0                             │
│    Fix: Single Left Rotation on node.                                                       │
│                                                                                             │
│ 3. Left-Right (LR) Case: BF(node) > 1 and BF(node.left) < 0                                 │
│    Fix: Left Rotation on node.left, then Right Rotation on node.                            │
│                                                                                             │
│ 4. Right-Left (RL) Case: BF(node) < -1 and BF(node.right) > 0                               │
│    Fix: Right Rotation on node.right, then Left Rotation on node.                           │
│                                                                                             │
╰─────────────────────────────────────────────────────────────────────────────────────────────╯
```

---

## Problem 1: AVL Tree Rotations & Insertion

### 1. Pointer Rewiring Mechanics

Let's trace a **Right Rotation** on root $Z$ with left child $Y$ and subtrees $T_1, T_2, T_3$:

```text
╭─────────────────────────────────────────────────────────────────────────────────────────────╮
│                         RIGHT ROTATION (LL CASE)                                            │
├─────────────────────────────────────────────────────────────────────────────────────────────┤
│             (Z)                                          (Y)                                │
│            /   \                                        /   \                               │
│          (Y)    T3        ─── Right Rotation ───>     (X)   (Z)                             │
│         /   \                                               /  \                            │
│       (X)    T2                                            T2  T3                           │
│                                                                                             │
│ Pointer Rewiring Steps:                                                                     │
│ 1. Y = Z.left                                                                               │
│ 2. Z.left = Y.right  (T2 attaches to Z's left)                                              │
│ 3. Y.right = Z       (Z becomes Y's right child)                                            │
│ 4. Recalculate heights of Z, then Y in O(1)!                                                │
╰─────────────────────────────────────────────────────────────────────────────────────────────╯
```

---

### 2. Production Implementation (Java 17/21)

```java
package com.dataship.trees.balancing;

public final class AvlTree {

    public static class AVLNode {
        public int val;
        public int height;
        public AVLNode left;
        public AVLNode right;

        public AVLNode(int val) {
            this.val = val;
            this.height = 1;
        }
    }

    private AvlTree() {}

    public static int getHeight(AVLNode node) {
        return (node == null) ? 0 : node.height;
    }

    public static int getBalanceFactor(AVLNode node) {
        return (node == null) ? 0 : getHeight(node.left) - getHeight(node.right);
    }

    private static AVLNode rightRotate(AVLNode z) {
        AVLNode y = z.left;
        AVLNode t2 = y.right;

        // Perform rotation
        y.right = z;
        z.left = t2;

        // Update heights (order matters: z first, then y)
        z.height = 1 + Math.max(getHeight(z.left), getHeight(z.right));
        y.height = 1 + Math.max(getHeight(y.left), getHeight(y.right));

        return y; // New root
    }

    private static AVLNode leftRotate(AVLNode z) {
        AVLNode y = z.right;
        AVLNode t2 = y.left;

        // Perform rotation
        y.left = z;
        z.right = t2;

        // Update heights
        z.height = 1 + Math.max(getHeight(z.left), getHeight(z.right));
        y.height = 1 + Math.max(getHeight(y.left), getHeight(y.right));

        return y; // New root
    }

    public static AVLNode insert(AVLNode node, int key) {
        // Step 1: Standard BST insertion
        if (node == null) {
            return new AVLNode(key);
        }

        if (key < node.val) {
            node.left = insert(node.left, key);
        } else if (key > node.val) {
            node.right = insert(node.right, key);
        } else {
            return node; // Duplicate keys not permitted
        }

        // Step 2: Update height of ancestor node
        node.height = 1 + Math.max(getHeight(node.left), getHeight(node.right));

        // Step 3: Check balance factor
        int balance = getBalanceFactor(node);

        // Case 1: LL
        if (balance > 1 && key < node.left.val) {
            return rightRotate(node);
        }

        // Case 2: RR
        if (balance < -1 && key > node.right.val) {
            return leftRotate(node);
        }

        // Case 3: LR
        if (balance > 1 && key > node.left.val) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // Case 4: RL
        if (balance < -1 && key < node.right.val) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }
}
```

---

## Problem 2: Segment Tree with Lazy Propagation

### 1. Architectural Foundations: Dynamic Range Query Engine

A **Segment Tree** divides an array into a binary tree of intervals where the root covers $[0 \dots N-1]$, and every node bisection covers $[L \dots \text{mid}]$ and $[\text{mid}+1 \dots R]$.

- **Memory Layout**: Flat contiguous array of size $4N$.
- **Lazy Propagation**: When updating an entire interval $[qL \dots qR]$, if the current segment falls completely inside $[qL \dots qR]$, we update the segment value and store a **lazy tag**. We do **not** recurse into its children until a future query actually accesses them!

```text
╭─────────────────────────────────────────────────────────────────────────────────────────────╮
│                         SEGMENT TREE INTERVAL BISECTION                                     │
├─────────────────────────────────────────────────────────────────────────────────────────────┤
│                              [ 0 .. 3 ] (Sum = 10)                                          │
│                             /                     \                                         │
│                   [ 0 .. 1 ] (Sum = 3)         [ 2 .. 3 ] (Sum = 7)                         │
│                   /         \                  /         \                                  │
│             [0..0] (1)   [1..1] (2)      [2..2] (3)   [3..3] (4)                            │
│                                                                                             │
│ Space: Stored in a flat 1D primitive array tree[] of size 4N. Zero pointer overhead!       │
╰─────────────────────────────────────────────────────────────────────────────────────────────╯
```

---

### 2. Production Implementation (Java 17/21)

```java
package com.dataship.trees.balancing;

public final class SegmentTreeLazy {

    private final int n;
    private final long[] tree;
    private final long[] lazy;

    public SegmentTreeLazy(int[] arr) {
        this.n = arr.length;
        this.tree = new long[4 * n];
        this.lazy = new long[4 * n];
        build(arr, 1, 0, n - 1);
    }

    private void build(int[] arr, int node, int start, int end) {
        if (start == end) {
            tree[node] = arr[start];
            return;
        }
        int mid = start + (end - start) / 2;
        build(arr, 2 * node, start, mid);
        build(arr, 2 * node + 1, mid + 1, end);
        tree[node] = tree[2 * node] + tree[2 * node + 1];
    }

    private void pushDown(int node, int start, int end) {
        if (lazy[node] != 0) {
            long val = lazy[node];
            int mid = start + (end - start) / 2;

            // Apply to left child
            tree[2 * node] += val * (mid - start + 1);
            lazy[2 * node] += val;

            // Apply to right child
            tree[2 * node + 1] += val * (end - mid);
            lazy[2 * node + 1] += val;

            // Reset lazy tag
            lazy[node] = 0;
        }
    }

    public void updateRange(int ql, int qr, int val) {
        updateRange(1, 0, n - 1, ql, qr, val);
    }

    private void updateRange(int node, int start, int end, int ql, int qr, int val) {
        if (ql <= start && end <= qr) {
            tree[node] += (long) val * (end - start + 1);
            lazy[node] += val;
            return;
        }

        pushDown(node, start, end);
        int mid = start + (end - start) / 2;

        if (ql <= mid) {
            updateRange(2 * node, start, mid, ql, qr, val);
        }
        if (qr > mid) {
            updateRange(2 * node + 1, mid + 1, end, ql, qr, val);
        }

        tree[node] = tree[2 * node] + tree[2 * node + 1];
    }

    public long queryRange(int ql, int qr) {
        return queryRange(1, 0, n - 1, ql, qr);
    }

    private long queryRange(int node, int start, int end, int ql, int qr) {
        if (ql <= start && end <= qr) {
            return tree[node];
        }

        pushDown(node, start, end);
        int mid = start + (end - start) / 2;
        long sum = 0;

        if (ql <= mid) {
            sum += queryRange(2 * node, start, mid, ql, qr);
        }
        if (qr > mid) {
            sum += queryRange(2 * node + 1, mid + 1, end, ql, qr);
        }

        return sum;
    }
}
```

---

## Problem 3: Binary Indexed Tree (Fenwick Tree)

### 1. Bitwise Least Significant Bit (LSB) Mathematics

The Fenwick Tree achieves **$O(\log N)$ point updates and prefix sum queries** using a single 1D array of size $N + 1$ through the mathematical beauty of the two's complement **Least Significant Bit (LSB)**:

$$\mathbf{\text{LSB}(i) = i \mathrel{\&} (-i)}$$

- `tree[i]` stores the sum of elements in the range:
  $$(i - \text{LSB}(i), \; i]$$
- **Query Prefix Sum**: Subtract LSB repeatedly ($i \leftarrow i - (i \mathrel{\&} -i)$).
- **Update Point**: Add LSB repeatedly ($i \leftarrow i + (i \mathrel{\&} -i)$).

```text
╭─────────────────────────────────────────────────────────────────────────────────────────────╮
│                         FENWICK TREE BITWISE RESPONSIBILITY INTERVALS                       │
├─────────────────────────────────────────────────────────────────────────────────────────────┤
│ Index i (Binary)        LSB: i & (-i)         Interval Length         Indices Covered       │
│ ─────────────────────────────────────────────────────────────────────────────────────────── │
│ 1  (0001)               1                     1                       [ 1 .. 1 ]            │
│ 2  (0010)               2                     2                       [ 1 .. 2 ]            │
│ 4  (0100)               4                     4                       [ 1 .. 4 ]            │
│ 8  (1000)               8                     8                       [ 1 .. 8 ]            │
│ 6  (0110)               2                     2                       [ 5 .. 6 ]            │
╰─────────────────────────────────────────────────────────────────────────────────────────────╯
```

---

### 2. Production Implementation (Java 17/21)

```java
package com.dataship.trees.balancing;

public final class FenwickTree {

    private final int size;
    private final long[] tree;

    public FenwickTree(int size) {
        this.size = size;
        this.tree = new long[size + 1]; // 1-based indexing
    }

    /**
     * Adds delta to index i in O(log N) time.
     */
    public void add(int i, long delta) {
        while (i <= size) {
            tree[i] += delta;
            i += i & (-i); // Advance to parent responsible interval
        }
    }

    /**
     * Computes cumulative sum from index 1 to i in O(log N) time.
     */
    public long query(int i) {
        long sum = 0;
        while (i > 0) {
            sum += tree[i];
            i -= i & (-i); // Strip LSB to hop to preceding interval
        }
        return sum;
    }

    /**
     * Computes range sum [l, r] in O(log N) time.
     */
    public long queryRange(int l, int r) {
        return query(r) - query(l - 1);
    }
}
```

---

<table width="100%">
  <tr>
    <td width="33%" align="left">
      <a href="./03-binary-search-trees-and-ordered-space.md">
        <strong>← Previous Module</strong><br>
        03. BSTs & Ordered Space
      </a>
    </td>
    <td width="33%" align="center">
      <a href="../README.md">
        <strong>Home</strong><br>
        Java DSA Master Track
      </a>
    </td>
    <td width="33%" align="right">
      <a href="./05-tries-and-bitwise-prefix-trees.md">
        <strong>Next Module →</strong><br>
        05. Tries & Bitwise Prefix Trees
      </a>
    </td>
  </tr>
</table>
