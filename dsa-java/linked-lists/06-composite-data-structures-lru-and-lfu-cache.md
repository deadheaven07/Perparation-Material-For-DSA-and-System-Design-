# Page 06: Composite Structures: LRU & LFU Cache

In system design and enterprise backends, pure data structures are rarely used in isolation. To meet strict Service Level Agreements (SLAs), engineers combine complementary data structures to cancel out each other's algorithmic weaknesses.

The **LRU (Least Recently Used) Cache** and **LFU (Least Frequently Used) Cache** are the quintessential composite data structures: combining the **$O(1)$ search capability of a Hash Map** with the **$O(1)$ insertion and self-excision of a Doubly Linked List**.

---

## 1. Why a Doubly Linked List is Essential for $O(1)$ Eviction

```text
╭─────────────────────────────────────────────────────────────────────────────╮
│                        Why Single Data Structures Fail                      │
├──────────────────────────┬──────────────────────────────────────────────────┤
│ Candidate Data Structure │ Algorithmic Bottleneck for LRU Cache             │
├──────────────────────────┼──────────────────────────────────────────────────┤
│ HashMap Only             │ O(1) key lookup, but NO order of access recency! │
│ Array / ArrayList Only   │ Maintains recency order, but evicting or moving  │
│                          │ an item to front costs O(N) memory shifts!       │
│ Singly Linked List Only  │ Cannot delete an arbitrary node in O(1) because   │
│                          │ finding the predecessor node requires O(N) walk! │
├──────────────────────────┴──────────────────────────────────────────────────┤
│ THE SOLUTION: HashMap + Doubly Linked List                                  │
│ • HashMap provides O(1) instant pointer access to any Node.                │
│ • Doubly Linked List allows instant O(1) self-excision via `node.prev` and  │
│   `node.next` without traversing the list!                                  │
╰─────────────────────────────────────────────────────────────────────────────╯
```

---

## 2. LRU Cache Architecture

A capacity-bounded cache that evicts the least recently accessed item when full:

```text
╭─────────────────────────────────────────────────────────────────────────────╮
│                         LRU Cache High-Level Architecture                   │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   HashMap<Key, Node> ────────────────┐                                      │
│   Key 1 ──► Node(1)                  │ (O(1) Direct Pointer)                │
│   Key 2 ──► Node(2) ─────────┐       │                                      │
│                              │       ▼                                      │
│   Doubly Linked List:        │    [Most Recent]              [Least Recent] │
│   ┌──────┐      ┌────────┐   │    ┌────────┐      ┌────────┐      ┌──────┐  │
│   │ head │ ◄──► │ Node 1 │ ◄─┴──► │ Node 2 │ ◄──► │ Node 3 │ ◄──► │ tail │  │
│   └──────┘      └────────┘        └────────┘      └────────┘      └──────┘  │
│    (Dummy)       (MRU: K1)         (K2)            (LRU: K3)       (Dummy)  │
│                                                                             │
│   • On get(key): Lookup node in Map, detach node from DLL, move to Head.    │
│   • On put(key, val): If exists, update & move to Head. If new, insert at   │
│     Head. If size > capacity, remove tail.prev from DLL & delete from Map!   │
╰─────────────────────────────────────────────────────────────────────────────╯
```

---

## 3. Production Implementation of LRU Cache

```java
import java.util.HashMap;
import java.util.Map;

public class LRUCache {
    // Internal Doubly Linked List Node
    private static class Node {
        int key;
        int value;
        Node prev;
        Node next;

        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<Integer, Node> map;
    private final Node head; // Sentinel Dummy Head (Most Recently Used)
    private final Node tail; // Sentinel Dummy Tail (Least Recently Used)

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();

        // Initialize sentinel boundary nodes
        this.head = new Node(0, 0);
        this.tail = new Node(0, 0);
        head.next = tail;
        tail.prev = head;
    }

    // O(1) Get Operation
    public int get(int key) {
        if (!map.containsKey(key)) {
            return -1;
        }

        Node node = map.get(key);
        // Move accessed node to head (marked Most Recently Used)
        moveToHead(node);
        return node.value;
    }

    // O(1) Put Operation
    public void put(int key, int value) {
        if (map.containsKey(key)) {
            Node node = map.get(key);
            node.value = value;
            moveToHead(node);
        } else {
            Node newNode = new Node(key, value);
            map.put(key, newNode);
            addToHead(newNode);

            // Evict LRU element if capacity exceeded
            if (map.size() > capacity) {
                Node lruNode = removeTail();
                map.remove(lruNode.key); // Critical: Node must store key to remove from Map!
            }
        }
    }

    // ── Helper Doubly Linked List Primitives (All O(1)) ─────────────────────

    private void addToHead(Node node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void moveToHead(Node node) {
        removeNode(node);
        addToHead(node);
    }

    private Node removeTail() {
        Node lru = tail.prev;
        removeNode(lru);
        return lru;
    }
}
```

### Step-by-Step Dry Run Trace
Input: `capacity = 2`
`put(1, 1), put(2, 2), get(1), put(3, 3), get(2), put(4, 4), get(1), get(3), get(4)`

| Step | Operation | Action on Map | Action on DLL (`head <-> ... <-> tail`) | Return Value | Notes |
| :---: | :---: | :---: | :---: | :---: | :--- |
| **1** | `put(1, 1)` | Add `{1: Node(1)}` | `head <-> [1] <-> tail` | - | New element |
| **2** | `put(2, 2)` | Add `{2: Node(2)}` | `head <-> [2] <-> [1] <-> tail` | - | [2] is MRU, [1] is LRU |
| **3** | `get(1)` | Exists in Map | `head <-> [1] <-> [2] <-> tail` | **1** | [1] moved to MRU; [2] becomes LRU! |
| **4** | `put(3, 3)` | Evict [2], Add `{3: Node(3)}` | `head <-> [3] <-> [1] <-> tail` | - | **Capacity exceeded!** [2] evicted! |
| **5** | `get(2)` | Key 2 not in map | Unchanged | **-1** | [2] was evicted |
| **6** | `put(4, 4)` | Evict [1], Add `{4: Node(4)}` | `head <-> [4] <-> [3] <-> tail` | - | **Capacity exceeded!** [1] evicted! |
| **7** | `get(1)` | Key 1 not in map | Unchanged | **-1** | [1] was evicted |
| **8** | `get(3)` | Exists in Map | `head <-> [3] <-> [4] <-> tail` | **3** | [3] moved to MRU |
| **9** | `get(4)` | Exists in Map | `head <-> [4] <-> [3] <-> tail` | **4** | [4] moved to MRU |

- **Time Complexity**: $O(1)$ strictly for both `get` and `put`.
- **Auxiliary Space**: $O(\text{Capacity})$ for Hash Map and DLL nodes.

---

## 4. LFU Cache (Least Frequently Used)

While an LRU cache evicts the oldest accessed item, an **LFU Cache** evicts the item with the **lowest total access frequency**. If multiple items share the lowest frequency, the **least recently used** among them is evicted.

### The Dual-Map Architecture
1. `keyToVal`: `Map<Integer, Integer>` (stores key $\rightarrow$ value).
2. `keyToFreq`: `Map<Integer, Integer>` (stores key $\rightarrow$ frequency).
3. `freqToKeys`: `Map<Integer, LinkedHashSet<Integer>>` (stores frequency $\rightarrow$ ordered keys for LRU tie-breaking).
4. `minFreq`: Scalar `int` tracking the current absolute minimum frequency across all keys.

```java
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class LFUCache {
    private final int capacity;
    private int minFreq;
    private final Map<Integer, Integer> keyToVal;
    private final Map<Integer, Integer> keyToFreq;
    private final Map<Integer, LinkedHashSet<Integer>> freqToKeys;

    public LFUCache(int capacity) {
        this.capacity = capacity;
        this.minFreq = 0;
        this.keyToVal = new HashMap<>();
        this.keyToFreq = new HashMap<>();
        this.freqToKeys = new HashMap<>();
    }

    public int get(int key) {
        if (!keyToVal.containsKey(key)) return -1;
        updateFrequency(key);
        return keyToVal.get(key);
    }

    public void put(int key, int value) {
        if (capacity <= 0) return;

        if (keyToVal.containsKey(key)) {
            keyToVal.put(key, value);
            updateFrequency(key);
            return;
        }

        // Evict if full
        if (keyToVal.size() >= capacity) {
            // Evict LRU key from the lowest frequency set
            LinkedHashSet<Integer> minFreqKeys = freqToKeys.get(minFreq);
            int evictKey = minFreqKeys.iterator().next(); // First element is LRU
            minFreqKeys.remove(evictKey);

            keyToVal.remove(evictKey);
            keyToFreq.remove(evictKey);
        }

        // Insert new key with frequency = 1
        keyToVal.put(key, value);
        keyToFreq.put(key, 1);
        freqToKeys.computeIfAbsent(1, k -> new LinkedHashSet<>()).add(key);
        minFreq = 1; // Reset minFreq to 1
    }

    private void updateFrequency(int key) {
        int oldFreq = keyToFreq.get(key);
        int newFreq = oldFreq + 1;
        keyToFreq.put(key, newFreq);

        // Remove from old frequency set
        LinkedHashSet<Integer> keys = freqToKeys.get(oldFreq);
        keys.remove(key);

        // If oldFreq was minFreq and no keys remain at that frequency, increment minFreq
        if (oldFreq == minFreq && keys.isEmpty()) {
            minFreq++;
        }

        // Add to new frequency set
        freqToKeys.computeIfAbsent(newFreq, k -> new LinkedHashSet<>()).add(key);
    }
}
```

- **Time Complexity**: $O(1)$ amortized for both `get` and `put` via `LinkedHashSet`.
- **Auxiliary Space**: $O(\text{Capacity})$.

---

## 5. Self-Check & Active Recall

1. **Q**: Why must the `Node` class in an LRU Cache store both `key` and `value`, rather than just `value`?
   - *A*: When the cache is full and the LRU node is evicted from the tail of the Doubly Linked List (`tail.prev`), the cache must also remove that key from `map`. Without storing `key` inside `Node`, the cache would have no way to know which map entry to delete in $O(1)$ time.

2. **Q**: Why are Sentinel `head` and `tail` nodes essential in the LRU Cache implementation?
   - *A*: Sentinel nodes eliminate null checks when the list is empty or contains only 1 element. `head.next` always points to MRU and `tail.prev` always points to LRU without checking `if (head == null)`.

3. **Q**: What is the primary operational difference between LRU and LFU cache eviction policies?
   - *A*: LRU evicts based solely on **time recency** (the key that was read/written furthest in the past). LFU evicts based on **access popularity** (the key with the fewest total access counts over its lifetime), using LRU only as a tie-breaker among keys with identical frequencies.

---

## 🧭 Continue Learning

| ◀️ Previous Topic | 🧭 Track Hub | Next Topic ▶️ |
| :--- | :---: | ---: |
| [**Page 05: Deep Copy & Rewiring**](05-deep-copy-and-complex-pointer-rewiring.md)<br><sub>*Random Pointers & Multilevel Lists*</sub> | [**Linked Lists Index**](README.md)<br><sub>*All 8 Modules*</sub> | [**Page 07: Advanced Systems & Concurrency**](07-advanced-techniques-and-system-scale.md)<br><sub>*Skip Lists & Lock-Free Concurrent Queue*</sub> |
