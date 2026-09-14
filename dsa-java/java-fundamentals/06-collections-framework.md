# Page 6: Java Collections Framework (JCF)

Welcome to Page 6 of the Java Fundamentals series. The Java Collections Framework (JCF) is the backbone of DSA problem solving in Java. Knowing which collection to pick and its exact Big-O complexities will determine whether your code runs in milliseconds or times out.

---

## 1. The Collections Framework Hierarchy

```
                           Iterable<E>
                                |
                          Collection<E>
             +------------------+------------------+
             |                  |                  |
          List<E>            Queue<E>            Set<E>
        (Ordered,            (FIFO /          (Unique values)
        Duplicates)         Priority)              |
             |                  |            +-----+-----+
      +------+------+        +--+--+         |           |
      |             |        |     |      HashSet    SortedSet
  ArrayList    LinkedList    |  PriorityQueue        (TreeSet)
                             |
                           Deque<E> (Double-ended)
                             |
                         ArrayDeque

  [Separate Hierarchy]:
        Map<K, V> (Key-Value pairs, Unique Keys)
             |
      +------+------+
      |             |
   HashMap      SortedMap (TreeMap)
```

---

## 2. Lists: `ArrayList` vs. `LinkedList`

### `ArrayList<E>` (The Default Dynamic Array)
- Backed by an internal resizable array.
- **Capacity Expansion**: When full, it allocates a new array $1.5\times$ the old size and copies elements over.
- **Random Access**: $O(1)$ via index.

```java
List<Integer> list = new ArrayList<>();
list.add(10);                // O(1) amortized append
list.add(0, 5);              // O(N) insertion at index
int val = list.get(1);       // O(1) indexed lookup
list.set(1, 20);             // O(1) replacement
list.remove(list.size() - 1);// O(1) remove last element
list.remove(0);              // O(N) remove first (requires left-shift)
```

### `LinkedList<E>` (Doubly Linked List)
- Each node stores pointers to `prev` and `next`.
- Insertion/deletion at ends is $O(1)$, but indexed lookup `get(i)` is $O(N)$.

> [!TIP]
> **Why `ArrayList` Almost Always Beats `LinkedList` in Interviews:**
> Even when doing insertions, `ArrayList` frequently outperforms `LinkedList` due to **CPU cache locality**. Elements in `ArrayList` are contiguous in memory, whereas `LinkedList` nodes are scattered across the Heap, causing constant CPU cache misses and higher memory overhead ($\approx 24\text{ bytes}$ per node pointer).

---

## 3. Stacks & Queues: Why NEVER use `java.util.Stack`

> [!WARNING]
> **Do NOT use `java.util.Stack` in modern Java!**
> 1. `java.util.Stack` inherits from `Vector`, meaning every method (`push`, `pop`, `peek`) is `synchronized`, incurring unnecessary locking overhead.
> 2. It violates the stack abstraction by exposing vector index methods like `stack.get(0)` or `stack.add(2, val)`.
> **Always use `ArrayDeque<E>` for Stacks and Queues.**

### 1. `ArrayDeque<E>` as a Stack (LIFO)

```java
Deque<Integer> stack = new ArrayDeque<>();
stack.push(10);              // O(1) push to top
int top = stack.peek();      // O(1) view top without popping
int popped = stack.pop();    // O(1) pop top
boolean empty = stack.isEmpty();
```

### 2. `ArrayDeque<E>` as a Standard Queue (FIFO)

```java
Queue<Integer> queue = new ArrayDeque<>();
queue.offer(10);             // O(1) enqueue at back
int front = queue.peek();    // O(1) view front
int removed = queue.poll();  // O(1) dequeue from front
```

### 3. `PriorityQueue<E>` (Binary Heap)
Used for Min-Heaps and Max-Heaps.
- Insertion (`offer`): $O(\log N)$
- Removal (`poll`): $O(\log N)$
- Peek min/max (`peek`): $O(1)$

```java
// 1. Min-Heap by default (smallest element at top)
PriorityQueue<Integer> minHeap = new PriorityQueue<>();
minHeap.offer(5);
minHeap.offer(1);
minHeap.offer(10);
System.out.println(minHeap.poll()); // Prints 1

// 2. Max-Heap (largest element at top)
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
// Or using lambda comparator:
PriorityQueue<Integer> maxHeap2 = new PriorityQueue<>((a, b) -> Integer.compare(b, a));
```

---

## 4. Sets: `HashSet` vs. `TreeSet`

| Operation | `HashSet<E>` | `TreeSet<E>` |
| :--- | :--- | :--- |
| **Internal Structure** | Hash Table (`HashMap`) | Red-Black Self-Balancing BST (`TreeMap`) |
| **Ordering** | Unordered (random) | **Sorted Natural Order** or Custom `Comparator` |
| **`add()` / `contains()` / `remove()`** | **$O(1)$ average** | **$O(\log N)$ guaranteed** |
| **Allows `null`?** | Yes (1 `null` allowed) | No (`NullPointerException`) |

### Key `TreeSet` Range Queries for DSA:

```java
TreeSet<Integer> set = new TreeSet<>(Arrays.asList(10, 20, 30, 40, 50));

System.out.println(set.ceiling(25)); // 30 (least element >= 25)
System.out.println(set.floor(25));   // 20 (greatest element <= 25)
System.out.println(set.higher(30));  // 40 (strictly > 30)
System.out.println(set.lower(30));   // 20 (strictly < 30)
```

---

## 5. Maps: `HashMap` vs. `TreeMap`

### `HashMap<K, V>` (Hash Table)
- **Time Complexity**: $O(1)$ average for `put`, `get`, `containsKey`, `remove`.
- **Internal Structure**: Array of buckets (default capacity 16, load factor 0.75). When collisions exceed 8 nodes in a bucket and table capacity $\ge 64$, the bucket converts from a Linked List to a Red-Black Tree ($O(\log N)$ worst case).

### Supercharged Java 8 Map Idioms for DSA:

```java
Map<String, Integer> map = new HashMap<>();

// 1. getOrDefault: Clean frequency counting!
map.put("apple", map.getOrDefault("apple", 0) + 1);

// 2. computeIfAbsent: Building Graph Adjacency Lists in 1 line!
Map<Integer, List<Integer>> adj = new HashMap<>();
// If vertex 'u' is not in map, initializes new ArrayList, then adds 'v':
adj.computeIfAbsent(u, k -> new ArrayList<>()).add(v);

// 3. Iterating over Map entries:
for (Map.Entry<String, Integer> entry : map.entrySet()) {
    System.out.println(entry.getKey() + " -> " + entry.getValue());
}
```

### `TreeMap<K, V>` (Sorted Map)
- Keys kept sorted in Red-Black Tree ($O(\log N)$ operations).
- Provides `firstKey()`, `lastKey()`, `ceilingKey(k)`, `floorKey(k)`.

---

## 6. Collections Quick Time Complexity Reference

| Collection | Data Structure | `get` / `contains` | `add` / `offer` | `remove` / `poll` |
| :--- | :--- | :--- | :--- | :--- |
| `ArrayList` | Resizable Array | $O(1)$ by index / $O(N)$ by val | $O(1)$ amortized | $O(N)$ |
| `LinkedList` | Doubly Linked List | $O(N)$ by index | $O(1)$ at ends | $O(1)$ at ends |
| `ArrayDeque` | Circular Array | $O(1)$ at ends | $O(1)$ at ends | $O(1)$ at ends |
| `PriorityQueue` | Binary Heap | $O(1)$ peek | $O(\log N)$ | $O(\log N)$ |
| `HashSet` | Hash Table | $O(1)$ | $O(1)$ | $O(1)$ |
| `TreeSet` | Red-Black Tree | $O(\log N)$ | $O(\log N)$ | $O(\log N)$ |
| `HashMap` | Hash Table | $O(1)$ | $O(1)$ | $O(1)$ |
| `TreeMap` | Red-Black Tree | $O(\log N)$ | $O(\log N)$ | $O(\log N)$ |

---

## 7. Self-Check & Quick Review

1. **Q**: What collection should you instantiate for implementing BFS traversal?
   - *A*: `Queue<TreeNode> queue = new ArrayDeque<>();`
2. **Q**: What happens in `(a, b) -> a - b` when `a = Integer.MIN_VALUE` and `b = 1`?
   - *A*: **Integer underflow occurs**, producing a positive result and corrupting heap ordering! Always use `Integer.compare(a, b)` instead of subtraction.
3. **Q**: How do you sort a `List<Integer> list` in descending order?
   - *A*: `list.sort(Collections.reverseOrder());` or `Collections.sort(list, Collections.reverseOrder());`

---

| ⬅️ Previous | 🏠 Course Index | ➡️ Next |
| :--- | :---: | ---: |
| [Page 5: Arrays, Strings & String Pool](05-arrays-and-strings.md) | [Java Fundamentals Index](README.md) | [Page 7: Generics & Custom Comparators](07-generics-and-comparators.md) |
