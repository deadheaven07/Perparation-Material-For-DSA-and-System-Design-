# Page 05: Deep Copy & Complex Pointer Rewiring

Standard linked lists contain only linear `next` (and optionally `prev`) references. In advanced systems and complex data models, nodes often contain arbitrary references—such as **random jump pointers** or **hierarchical child sublists**.

Cloning and linearizing these structures requires sophisticated pointer interleaving and splicing techniques that run in **$O(1)$ auxiliary space**.

---

## 1. Copy List with Random Pointer

> **Problem**: A linked list of length $n$ is given such that each node contains an additional random pointer, which could point to any node in the list, or `null`. Construct a **deep copy** of the list. The deep copy should consist of exactly $n$ brand new nodes, where each new node has its value set to the value of its corresponding original node. None of the pointers in the new list should point to nodes in the original list.

```java
// Node definition with Random Pointer
class Node {
    int val;
    Node next;
    Node random;

    public Node(int val) {
        this.val = val;
        this.next = null;
        this.random = null;
    }
}
```

---

### Approach A: The HashMap Strategy ($O(N)$ Space)
1. First pass: Iterate through the original list, creating an identical cloned node for each original node. Store the mapping in a `HashMap<Node, Node>`: `map.put(original, clone)`.
2. Second pass: Iterate again, wiring each clone's `next` and `random` pointers using the map:
   `map.get(curr).next = map.get(curr.next);`
   `map.get(curr).random = map.get(curr.random);`

```java
import java.util.HashMap;
import java.util.Map;

public class CopyListWithRandomPointerHashMap {
    public Node copyRandomList(Node head) {
        if (head == null) return null;

        Map<Node, Node> map = new HashMap<>();

        // Pass 1: Clone each node independently
        Node curr = head;
        while (curr != null) {
            map.put(curr, new Node(curr.val));
            curr = curr.next;
        }

        // Pass 2: Connect cloned next and random pointers
        curr = head;
        while (curr != null) {
            map.get(curr).next = map.get(curr.next);
            map.get(curr).random = map.get(curr.random);
            curr = curr.next;
        }

        return map.get(head);
    }
}
```
- **Time Complexity**: $O(N)$.
- **Auxiliary Space**: $O(N)$ (Hash table storing $N$ node references).

---

### Approach B: In-Place Node Interleaving ($O(1)$ Auxiliary Space)

We can eliminate the $O(N)$ hash table by **temporarily weaving each cloned node directly after its original counterpart**:

```text
Step 1: Interleave Cloned Nodes
Original:     [ A ] ─────────────► [ B ] ─────────────► [ C ]
Interleaved:  [ A ] ──► [ A' ] ──► [ B ] ──► [ B' ] ──► [ C ] ──► [ C' ]

Step 2: Copy Random Pointers
If A.random == C, then A'.random is simply A.random.next (which is C')!
Formula: curr.next.random = curr.random.next;

Step 3: Decouple the Intertwined Lists
Restore original list: [ A ] ──► [ B ] ──► [ C ]
Extract cloned list:   [ A' ] ──► [ B' ] ──► [ C' ]
```

```java
public class CopyListWithRandomPointerInterleaving {
    public Node copyRandomList(Node head) {
        if (head == null) return null;

        // Phase 1: Create interleaved duplicate nodes: A -> A' -> B -> B'
        Node curr = head;
        while (curr != null) {
            Node clone = new Node(curr.val);
            clone.next = curr.next;
            curr.next = clone;
            curr = clone.next;
        }

        // Phase 2: Wire random pointers for clones
        curr = head;
        while (curr != null) {
            if (curr.random != null) {
                curr.next.random = curr.random.next;
            }
            curr = curr.next.next;
        }

        // Phase 3: Decouple original list and cloned list
        Node dummyHead = new Node(0);
        Node cloneTail = dummyHead;
        curr = head;

        while (curr != null) {
            Node clone = curr.next;
            Node nextOriginal = clone.next;

            // Append clone to cloned list
            cloneTail.next = clone;
            cloneTail = clone;

            // Restore original list bridge
            curr.next = nextOriginal;

            curr = nextOriginal;
        }

        return dummyHead.next;
    }
}
```

#### Step-by-Step Dry Run Trace

| Phase | Original List Action | Interleaved Pointer State |
| :---: | :--- | :--- |
| **Start** | `A.random = C`, `B.random = A` | `A -> B -> C -> null` |
| **Phase 1** | Interleave clones | `A -> A' -> B -> B' -> C -> C' -> null` |
| **Phase 2** | Wire clone randoms | `A'.random = A.random.next = C'`<br>`B'.random = B.random.next = A'` |
| **Phase 3** | Decouple lists | Original: `A -> B -> C -> null`<br>Cloned: `A' -> B' -> C' -> null` |

- **Time Complexity**: $O(N)$ (Three consecutive linear passes).
- **Auxiliary Space**: $O(1)$ (Zero auxiliary hash map; strictly constant pointer variables).

---

## 2. Flatten a Multilevel Doubly Linked List

> **Problem**: You are given a doubly linked list, which contains nodes that have a `next` pointer, a `previous` pointer, and an additional `child` pointer. This child pointer may or may not point to a separate doubly linked list, also containing these special nodes. Flatten the list so that all the nodes appear in a single-level, doubly linked list in **depth-first order**.

```text
Multilevel Structure:
1 ─── 2 ─── 3 ─── 4 ─── 5 ─── 6 ─── null
            │
            7 ─── 8 ─── 9 ─── 10 ─── null
                  │
                  11 ── 12 ── null

Flattened Depth-First Order:
1 ── 2 ── 3 ── 7 ── 8 ── 11 ── 12 ── 9 ── 10 ── 4 ── 5 ── 6
```

### The In-Place Splicing Technique ($O(1)$ Space)
Instead of recursion or auxiliary stacks:
1. Iterate linearly through the list with pointer `curr`.
2. Whenever `curr.child != null` is encountered:
   - Find the tail of the child sublist.
   - Splice the child list between `curr` and `curr.next`.
   - Set `curr.child = null`.
3. Advance `curr = curr.next`. If child sublists contain further nested children, they will be naturally processed as `curr` advances forward!

```java
// Definition for a Multilevel Node
class MultiNode {
    public int val;
    public MultiNode prev;
    public MultiNode next;
    public MultiNode child;
}

public class FlattenMultilevelList {
    public MultiNode flatten(MultiNode head) {
        if (head == null) return null;

        MultiNode curr = head;

        while (curr != null) {
            // If current node has a child sublist, splice it in!
            if (curr.child != null) {
                MultiNode nextNode = curr.next;

                // 1. Locate the tail of the child list
                MultiNode childTail = curr.child;
                while (childTail.next != null) {
                    childTail = childTail.next;
                }

                // 2. Connect curr -> child
                curr.next = curr.child;
                curr.child.prev = curr;

                // 3. Connect childTail -> nextNode (if nextNode exists)
                if (nextNode != null) {
                    childTail.next = nextNode;
                    nextNode.prev = childTail;
                }

                // 4. Sever the child pointer
                curr.child = null;
            }

            // Advance to next node (which may now be the first child node)
            curr = curr.next;
        }

        return head;
    }
}
```

- **Time Complexity**: $O(N)$ — In the worst case, each node is visited at most twice.
- **Auxiliary Space**: $O(1)$ — Complete in-place splicing without recursion or stack frames.

---

## 3. Self-Check & Active Recall

1. **Q**: In "Copy List with Random Pointer", why is the interleaving formula `curr.next.random = curr.random.next;` correct?
   - *A*: `curr.next` is the newly created clone of `curr`. `curr.random` points to an original node in the list. Because every original node is immediately followed by its clone, `curr.random.next` points directly to the clone of `curr.random`.

2. **Q**: In "Flatten a Multilevel Doubly Linked List", what happens if a node on the child list also has a child of its own?
   - *A*: The iterative algorithm handles nested levels automatically. Splicing places the child list immediately after `curr`. As `curr = curr.next` advances, it will encounter the nested child and splice it in turn, maintaining correct depth-first ordering.

3. **Q**: What must you always do to `curr.child` after splicing in the multilevel list?
   - *A*: You must explicitly set `curr.child = null;`. Failing to nullify `child` leaves dangling references and violates the problem requirement that the flattened list be a standard single-level doubly linked list.

---

## 🧭 Continue Learning

| ◀️ Previous Topic | 🧭 Track Hub | Next Topic ▶️ |
| :--- | :---: | ---: |
| [**Page 04: Merging & Sorting**](04-merging-sorting-and-partitioning.md)<br><sub>*Merge K Lists & Merge Sort*</sub> | [**Linked Lists Index**](README.md)<br><sub>*All 8 Modules*</sub> | [**Page 06: Composite Structures: LRU Cache**](06-composite-data-structures-lru-and-lfu-cache.md)<br><sub>*Doubly Linked List + HashMap*</sub> |
