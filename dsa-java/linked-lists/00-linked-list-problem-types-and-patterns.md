# Page 00: Linked List Problem Types & Pointer Patterns

Linked lists test an engineer's ability to manipulate **memory references and pointers without losing track of allocated nodes**. Unlike arrays, where elements can be indexed directly, linked lists require strict sequential navigation and disciplined invariant management.

---

## 1. The 3 Golden Rules of Linked List Invariants

Whenever manipulating linked lists, adhering to these three rules prevents the three most common bugs: `NullPointerException`, losing the rest of the list, and creating unintended infinite loops.

```text
╭─────────────────────────────────────────────────────────────────────────────╮
│                  THE 3 GOLDEN RULES OF LINKED LIST CODING                   │
├─────────────────────────────────────────────────────────────────────────────┤
│ 1. Protect the Next Reference:                                              │
│    Before rewiring `curr.next`, ALWAYS save the remaining list:             │
│    ListNode nextTemp = curr.next; // Never overwrite without saving!        │
│                                                                             │
│ 2. The Sentinel (Dummy) Node Pattern:                                       │
│    Whenever the head of the list might change (inserting at index 0,        │
│    deleting head, merging lists), allocate a dummy sentinel:               │
│    ListNode dummy = new ListNode(0); dummy.next = head;                     │
│    ListNode tail = dummy;                                                   │
│    // Return dummy.next at the end!                                         │
│                                                                             │
│ 3. Sever the Trailing Reference:                                            │
│    When splitting a list into two (e.g. Merge Sort, Palindrome check),      │
│    ALWAYS set the tail of the first sublist to null:                        │
│    prev.next = null; // Prevents cycles and invalid list bounds             │
╰─────────────────────────────────────────────────────────────────────────────╯
```

---

## 2. The 6 Linked List Problem Archetypes

Every linked list problem asked in technical interviews falls into one of these six structural archetypes:

```text
                            Linked List Landscape
                                      │
     ┌─────────────────┬──────────────┼──────────────┬─────────────────┐
     ▼                 ▼              ▼              ▼                 ▼
[1. Positional]  [2. Cycles &]   [3. In-Place]  [4. Multi-List]   [5. Complex]
 [Queries]       [Convergence]   [Reversals]    [Coordination]    [Graphs]
                                                                       │
                                                       ┌───────────────┘
                                                       ▼
                                                [6. Composite]
                                                [Caches & Queues]
```

### Archetype 1: Positional & Traversal Queries
- **Characteristics**: "Find the middle of the linked list", "Find the $K$-th node from the end", "Remove the $N$-th node from end in a single pass".
- **Trigger Pattern**: **Two-Pointer Gap / Fast & Slow Pointers**.
  - To find $K$-th from end: advance `fast` pointer by $K$ steps, then move both `slow` and `fast` simultaneously until `fast` reaches `null`. `slow` lands directly on the target!

### Archetype 2: Cycle & Convergence Detection
- **Characteristics**: "Determine if a linked list contains a cycle", "Find the node where the cycle begins", "Find the intersection node of two singly linked lists".
- **Trigger Pattern**: **Floyd's Tortoise & Hare** and **Pointer Cycle-Switching**.
  - `slow` moves 1 step, `fast` moves 2 steps. If they meet, a cycle exists.
  - Cycle entrance is found by resetting one pointer to `head` and advancing both by 1 step.

### Archetype 3: In-Place Structural Reversals
- **Characteristics**: "Reverse linked list", "Reverse nodes from position $L$ to $R$", "Reverse nodes in $K$-group", "Reorder list into $L_0 \rightarrow L_n \rightarrow L_1 \rightarrow L_{n-1}$".
- **Trigger Pattern**: **In-Place Reference Redirection (`prev`, `curr`, `next`)**.
  - Never allocate new nodes ($O(1)$ auxiliary space constraint).
  - Reverse subsegments using localized pointer splicing.

### Archetype 4: Multi-List Coordination & Sorting
- **Characteristics**: "Merge two sorted lists", "Merge $K$ sorted lists", "Sort an unsorted linked list in $O(N \log N)$ time", "Partition list around value $X$".
- **Trigger Pattern**: **Dummy Nodes + Pointer Splicing + Merge Sort / Heaps**.
  - Linked lists can be sorted via Merge Sort in $O(N \log N)$ time and $O(\log N)$ stack space **without** $O(N)$ array copying, because merging lists requires only pointer rewiring!

### Archetype 5: Complex & Multi-Pointer Graphs
- **Characteristics**: "Copy list with random pointer", "Flatten a multilevel doubly linked list with child pointers".
- **Trigger Pattern**: **In-Place Node Interleaving ($O(1)$ space) or Stack-Based DFS**.
  - Duplicate each node $A \rightarrow A' \rightarrow B \rightarrow B'$ to map random pointers without requiring an auxiliary hash table.

### Archetype 6: Composite Data Structures (Caches & Concurrency)
- **Characteristics**: "Implement LRU Cache with $O(1)$ `get` and `put`", "Implement LFU Cache", "Design a lock-free queue".
- **Trigger Pattern**: **Doubly Linked List + HashMap** (or atomic CAS pointers).
  - A hash map provides $O(1)$ key lookup to a node, while a doubly linked list allows $O(1)$ node excision and repositioning.

---

## 3. The Master Linked List Mapping Matrix

| Problem Clue / Objective | Optimal Technique | Auxiliary Space | Key Invariant / Mechanism |
| :--- | :--- | :---: | :--- |
| **Middle of linked list** | **Fast & Slow Pointers** | $O(1)$ | `slow` 1 step, `fast` 2 steps; stops when `fast == null` or `fast.next == null` |
| **$K$-th node from end** | **Two-Pointer Fixed Gap** | $O(1)$ | Advance `fast` by $K$ steps first, then advance both together |
| **Cycle detection (boolean)** | **Floyd's Tortoise & Hare** | $O(1)$ | If `fast == slow`, cycle detected; if `fast == null`, no cycle |
| **Cycle entrance node** | **Floyd's Cycle II Algorithm** | $O(1)$ | Move one pointer to `head`; advance both by 1 step until they meet |
| **Two lists intersection** | **Two-Pointer Switch** | $O(1)$ | When pointer reaches end, redirect to other list's head ($pA = pA.next \dots$) |
| **Check if list is palindrome** | **Middle + Reverse + Compare** | $O(1)$ | Split at middle, reverse second half in-place, compare, restore |
| **Reverse entire list** | **Iterative 3-Pointer** | $O(1)$ | `next = curr.next; curr.next = prev; prev = curr; curr = next;` |
| **Reverse subsegment $[L \dots R]$** | **Localized Pointer Splicing** | $O(1)$ | Repeatedly insert `curr.next` after sublist predecessor node |
| **Reverse in $K$-groups** | **Batch Reversal + Seam Rewire** | $O(1)$ | Count $K$ nodes ahead; reverse batch; stitch tail to next sublist |
| **Merge Two Sorted Lists** | **Dummy Node Iterative Walk** | $O(1)$ | Splice smaller node to running `tail.next` |
| **Merge $K$ Sorted Lists** | **Divide & Conquer or Min-Heap** | $O(\log K)$ or $O(K)$ | Pairwise merge ($O(N \log K)$) or PriorityQueue holding $K$ list heads |
| **Sort unsorted list** | **Linked List Merge Sort** | $O(\log N)$ | Split at middle (`fast/slow`), recursively sort halves, merge |
| **Partition list around value $X$** | **Two Dummy Lists** | $O(1)$ | Group into `< X` and `\ge X` lists, connect tails, terminate with `null` |
| **Clone list with random pointer** | **In-Place Node Interleaving** | $O(1)$ | $A \rightarrow A' \rightarrow B \rightarrow B'$, link randoms, decouple clone |
| **LRU Cache ($O(1)$ operations)** | **Doubly Linked List + HashMap** | $O(\text{Capacity})$ | HashMap stores key to Node; DLL maintains access recency |

---

## 4. The Sentinel (Dummy) Node Blueprint

In over $80\%$ of linked list interview problems, bugs occur when modifying the **head** (e.g. deleting the first node, inserting before the first node, or building a new list from scratch).

### Why the Sentinel Node is Essential
A **Sentinel Node** is an allocated node that sits permanently before the actual head of the list:

```text
Without Sentinel (Bug-Prone):
Node to delete: Head!
Must write special-case code:
if (head.val == target) {
    head = head.next; // Special branch required!
}

With Sentinel Node (Uniform & Clean):
[ Dummy: 0 ] ──► [ Head: 1 ] ──► [ Node: 2 ] ──► [ Node: 3 ]
     ▲
    prev

Deleting Head (1):
prev.next = prev.next.next; // Works identically for ANY node in the list!
return dummy.next;          // Returns new true head automatically!
```

```java
// Standard Sentinel Template
public ListNode modifyList(ListNode head) {
    ListNode dummy = new ListNode(0);
    dummy.next = head;
    ListNode curr = dummy;

    while (curr.next != null) {
        if (/* condition to delete */ false) {
            curr.next = curr.next.next;
        } else {
            curr = curr.next;
        }
    }

    return dummy.next; // Returns the updated head safely!
}
```

---

## 5. Self-Check & Active Recall

1. **Q**: What happens if you execute `curr.next = prev;` before executing `ListNode next = curr.next;` in a reversal loop?
   - *A*: The pointer to the rest of the linked list is lost. The remaining nodes become unreachable, leaking memory and leaving the list truncated.

2. **Q**: Why can't we use Binary Search directly on a standard Singly Linked List in $O(\log N)$ time even if the elements are sorted?
   - *A*: Binary search requires $O(1)$ random access to the middle element (`mid`). In a singly linked list, accessing the middle element requires an $O(N)$ sequential pointer walk, making overall search $O(N)$ rather than $O(\log N)$.

3. **Q**: In Floyd's Cycle Detection algorithm, why must `fast` advance by 2 steps while `slow` advances by 1 step?
   - *A*: In each step, the relative distance between `fast` and `slow` inside the cycle decreases by **exactly 1** ($2 - 1 = 1$). A distance decreasing by 1 cannot skip over 0; therefore, `fast` and `slow` are guaranteed to meet without looping indefinitely.

---

## 🧭 Continue Learning

| ◀️ Previous Track | 🧭 Track Hub | Next Topic ▶️ |
| :--- | :---: | ---: |
| [**Arrays Track Overview**](../arrays/README.md)<br><sub>*1D, 2D & 3D Arrays*</sub> | [**Linked Lists Index**](README.md)<br><sub>*All 8 Modules*</sub> | [**Page 01: Array Limitations & Fundamentals**](01-array-limitations-and-linkedlist-fundamentals.md)<br><sub>*ArrayList vs LinkedList Memory Model*</sub> |
