# Page 07: Advanced Techniques & Large-Scale Systems

In enterprise architectures, high-frequency trading engines, and distributed databases, linked list principles are extended to solve high-concurrency and sub-millisecond latency challenges.

This page covers **Skip Lists** (the backbone of Redis Sorted Sets and LSM MemTables), **Lock-Free Concurrent Queues**, and **Memory-Pool Node Recyclers**.

---

## 1. Skip Lists: Achieving $O(\log N)$ Search on Linked Lists

A fundamental limitation of a singly linked list is that even if elements are strictly sorted, binary search cannot be performed because looking up the middle element takes $O(N)$ sequential steps.

A **Skip List** resolves this by layering multiple "expressway" forward pointers over a sorted linked list:

```text
╭─────────────────────────────────────────────────────────────────────────────╮
│                         Skip List Multi-Level Architecture                  │
├─────────────────────────────────────────────────────────────────────────────┤
│ Level 3: [Head] ──────────────────────────────────────────► [ 17 ] ──► null │
│ Level 2: [Head] ─────────────► [ 9 ] ─────────────────────► [ 17 ] ──► null │
│ Level 1: [Head] ──► [ 3 ] ──► [ 9 ] ─────────────► [ 14 ] ──► [ 17 ] ──► null │
│ Level 0: [Head] ──► [ 3 ] ──► [ 7 ] ──► [ 9 ] ──► [ 12 ] ──► [ 17 ] ──► null │
╰─────────────────────────────────────────────────────────────────────────────╯
```

### How Search Works in $O(\log N)$
To search for target `12`:
1. Start at top level (Level 3): `Head.next` is `17`. Since $17 > 12$, **drop down** to Level 2.
2. At Level 2: `Head.next` is `9`. Since $9 < 12$, step forward to `9`.
3. From `9` at Level 2, the next node is `17` ($17 > 12$). **Drop down** to Level 1.
4. From `9` at Level 1, the next node is `14` ($14 > 12$). **Drop down** to Level 0.
5. At Level 0: Advance from `9` to `12`. **Target found!**

### Why Databases Prefer Skip Lists over Red-Black Trees
Redis (`ZSET`), Apache Cassandra, and RocksDB use Skip Lists in their memory tables because:
1. **Concurrency Simplicity**: Rebalancing a balanced AVL or Red-Black Tree requires tree rotations that lock large subtrees. In a Skip List, insertions and deletions only affect local immediate neighbors, enabling lock-free CAS implementations!
2. **Range Scans**: Once a start key is found, scanning a range of keys is simply walking Level 0 forward in $O(1)$ per element.

```java
import java.util.Random;

public class SkipList {
    private static final int MAX_LEVEL = 16;
    private static final double P = 0.5;

    private static class Node {
        int val;
        Node[] forward; // Pointers for each level

        Node(int val, int level) {
            this.val = val;
            this.forward = new Node[level + 1];
        }
    }

    private final Node head;
    private int level;
    private final Random random;

    public SkipList() {
        this.head = new Node(-1, MAX_LEVEL);
        this.level = 0;
        this.random = new Random();
    }

    // O(log N) Search
    public boolean search(int target) {
        Node curr = head;
        // Search from highest level down to level 0
        for (int i = level; i >= 0; i--) {
            while (curr.forward[i] != null && curr.forward[i].val < target) {
                curr = curr.forward[i];
            }
        }
        curr = curr.forward[0];
        return curr != null && curr.val == target;
    }

    // O(log N) Insert with Coin-Flip Random Level Assignment
    public void add(int num) {
        Node[] update = new Node[MAX_LEVEL + 1];
        Node curr = head;

        for (int i = level; i >= 0; i--) {
            while (curr.forward[i] != null && curr.forward[i].val < num) {
                curr = curr.forward[i];
            }
            update[i] = curr;
        }

        int nodeLevel = randomLevel();
        if (nodeLevel > level) {
            for (int i = level + 1; i <= nodeLevel; i++) {
                update[i] = head;
            }
            level = nodeLevel;
        }

        Node newNode = new Node(num, nodeLevel);
        for (int i = 0; i <= nodeLevel; i++) {
            newNode.forward[i] = update[i].forward[i];
            update[i].forward[i] = newNode;
        }
    }

    private int randomLevel() {
        int lvl = 0;
        while (lvl < MAX_LEVEL && random.nextDouble() < P) {
            lvl++;
        }
        return lvl;
    }
}
```

---

## 2. Lock-Free Concurrent Linked Lists: The Michael-Scott Queue

In high-concurrency environments, using `synchronized` or `ReentrantLock` on a linked list creates thread contention and context-switching bottlenecks.

The **Michael-Scott Non-Blocking Queue** (the algorithm implemented by `java.util.concurrent.ConcurrentLinkedQueue`) allows concurrent threads to enqueue and dequeue nodes safely using **Atomic CAS (Compare-And-Swap)** without locks:

```text
╭─────────────────────────────────────────────────────────────────────────────╮
│                      Michael-Scott Lock-Free Enqueue Algorithm              │
├─────────────────────────────────────────────────────────────────────────────┤
│ Initial State:                                                              │
│ head ──► [ Sentinel Node ] ◄── tail                                         │
│                                                                             │
│ Thread Enqueuing Node X:                                                    │
│ Step 1: Read tail and tail.next. Verify tail has not shifted.               │
│ Step 2: CAS tail.next from null to Node X:                                  │
│         CAS(tail.next, null, Node X)                                        │
│         • If CAS fails: Another thread enqueued first! Loop and retry.      │
│ Step 3: Advance tail pointer to Node X:                                     │
│         CAS(tail, oldTail, Node X)                                          │
╰─────────────────────────────────────────────────────────────────────────────╯
```

```java
import java.util.concurrent.atomic.AtomicReference;

public class LockFreeQueue<E> {
    private static class Node<E> {
        final E item;
        final AtomicReference<Node<E>> next;

        Node(E item) {
            this.item = item;
            this.next = new AtomicReference<>(null);
        }
    }

    private final AtomicReference<Node<E>> head;
    private final AtomicReference<Node<E>> tail;

    public LockFreeQueue() {
        Node<E> sentinel = new Node<>(null);
        this.head = new AtomicReference<>(sentinel);
        this.tail = new AtomicReference<>(sentinel);
    }

    // Lock-Free CAS Enqueue
    public void enqueue(E item) {
        Node<E> newNode = new Node<>(item);
        while (true) {
            Node<E> curTail = tail.get();
            Node<E> curNext = curTail.next.get();

            // Verify tail is still current
            if (curTail == tail.get()) {
                if (curNext == null) {
                    // Step 1: Attempt to link new node to current tail.next
                    if (curTail.next.compareAndSet(null, newNode)) {
                        // Step 2: Attempt to advance tail pointer (ok if fails, helper thread will fix)
                        tail.compareAndSet(curTail, newNode);
                        return;
                    }
                } else {
                    // Helper thread behavior: Tail is trailing; help advance it!
                    tail.compareAndSet(curTail, curNext);
                }
            }
        }
    }
}
```

---

## 3. Memory Pools & GC-Free Node Recyclers

In ultra-low-latency financial systems, allocating new `Node` objects on every insertion and discarding them on deletion floods the JVM Young Generation with garbage, triggering **Stop-The-World GC Pauses**.

### The Free-List Object Pool Pattern
Instead of calling `new Node()`, pre-allocate an array of `Node` objects at application startup and maintain an internal **Free List**:

```java
public class PooledLinkedList {
    private static class Node {
        int val;
        int next; // Store integer index in pool array instead of object reference!
    }

    private final Node[] pool;
    private int freeListHead; // Points to first available slot
    private int listHead;

    public PooledLinkedList(int capacity) {
        this.pool = new Node[capacity];
        for (int i = 0; i < capacity; i++) {
            pool[i] = new Node();
            pool[i].next = i + 1; // Chain all slots into free list
        }
        pool[capacity - 1].next = -1; // End of free list
        this.freeListHead = 0;
        this.listHead = -1;
    }

    // Allocate without JVM Heap allocation!
    public void insert(int val) {
        if (freeListHead == -1) throw new OutOfMemoryError("Pool exhausted");

        int allocatedIndex = freeListHead;
        freeListHead = pool[freeListHead].next;

        pool[allocatedIndex].val = val;
        pool[allocatedIndex].next = listHead;
        listHead = allocatedIndex;
    }

    // Free back to pool (Zero Garbage Collection!)
    public void deleteHead() {
        if (listHead == -1) return;

        int toFreeIndex = listHead;
        listHead = pool[listHead].next;

        // Return slot to free list
        pool[toFreeIndex].next = freeListHead;
        freeListHead = toFreeIndex;
    }
}
```

- **Memory Overhead**: Zero JVM GC garbage generation during runtime; 100% reusable memory slots.
- **Cache Locality**: Stored in a flat contiguous array buffer `Node[] pool`, dramatically reducing CPU cache misses.

---

## 4. Self-Check & Active Recall

1. **Q**: Why do Redis and RocksDB use Skip Lists instead of Red-Black Trees for their concurrent in-memory data structures?
   - *A*: Red-Black Trees require tree rotations during rebalancing, which lock large branches of the tree and create thread contention. Skip Lists only require modifying local neighbor pointers, which can be accomplished with lock-free atomic CAS primitives without locking the entire structure.

2. **Q**: In the Michael-Scott lock-free queue, what does it mean when `curTail.next != null`?
   - *A*: It means another thread has already successfully executed Step 1 (linked its new node to `tail.next`), but has not yet completed Step 2 (advancing the `tail` pointer). The current thread proactively helps advance `tail` (`tail.compareAndSet(curTail, curNext)`) to prevent stalls.

3. **Q**: How does the Object Pool / Free-List pattern eliminate GC pauses in linked lists?
   - *A*: By pre-allocating all `Node` objects at startup inside an array and recycling unused indices via a `freeListHead` pointer, the program never allocates new objects via `new` during runtime and never creates orphaned objects for the garbage collector to reclaim.

---

## 🧭 Continue Learning

| ◀️ Previous Topic | 🧭 Track Hub | Next Track ▶️ |
| :--- | :---: | ---: |
| [**Page 06: Composite Structures: LRU Cache**](06-composite-data-structures-lru-and-lfu-cache.md)<br><sub>*Doubly Linked List + HashMap*</sub> | [**Linked Lists Index**](README.md)<br><sub>*All 8 Modules Complete*</sub> | [**Java Track Hub**](../README.md)<br><sub>*Master Overview & Scaffolding*</sub> |
