# Page 03: In-Place Reversals & Subsegment Manipulations

Reversing a linked list in-place is the ultimate test of pointer control. Because singly linked nodes maintain only a forward pointer (`next`), redirecting a node's reference backward destroys the path to the remaining list unless tracked with meticulous precision.

---

## 1. Reversing a Full List: Iterative vs. Recursive

```text
Original List:    [ 1 ] ──► [ 2 ] ──► [ 3 ] ──► null

Target State:     null ◄── [ 1 ] ◄── [ 2 ] ◄── [ 3 ] (New Head)
```

### 1. The 3-Pointer Iterative Blueprint
We maintain three pointers: `prev` (initially `null`), `curr` (initially `head`), and `nextTemp`:

```text
Step Loop:
1. Save next node:      nextTemp = curr.next;
2. Invert pointer:      curr.next = prev;
3. Advance prev:        prev = curr;
4. Advance curr:        curr = nextTemp;
```

```java
public class ReverseLinkedList {
    // Approach 1: Iterative O(N) Time, O(1) Space
    public ListNode reverseList(ListNode head) {
        ListNode prev = null;
        ListNode curr = head;

        while (curr != null) {
            ListNode nextTemp = curr.next; // 1. Save bridge to remaining nodes
            curr.next = prev;              // 2. Invert pointer
            prev = curr;                   // 3. Advance prev
            curr = nextTemp;               // 4. Advance curr
        }

        return prev; // prev now points to the new head
    }

    // Approach 2: Recursive O(N) Time, O(N) Call Stack Space
    public ListNode reverseListRecursive(ListNode head) {
        // Base case: empty list or single node
        if (head == null || head.next == null) {
            return head;
        }

        // Recursively reverse remaining sublist
        ListNode newHead = reverseListRecursive(head.next);

        // Make the next node point back to current node
        head.next.next = head;
        head.next = null; // Sever forward link to prevent cycle

        return newHead;
    }
}
```

#### Step-by-Step Dry Run Trace
Input: `head = [1, 2, 3]`

| Iteration | `curr` | `prev` | `nextTemp` | Pointer Mutation | Resulting State |
| :---: | :---: | :---: | :---: | :---: | :--- |
| **Start** | 1 | `null` | - | - | `1 -> 2 -> 3 -> null` |
| **1** | 1 | `null` | 2 | `1.next = null` | `null <- 1`, `2 -> 3 -> null` |
| **2** | 2 | 1 | 3 | `2.next = 1` | `null <- 1 <- 2`, `3 -> null` |
| **3** | 3 | 2 | `null` | `3.next = 2` | `null <- 1 <- 2 <- 3` |
| **End** | `null` | **3** | - | Loop terminates | Returns `prev = 3` |

---

## 2. Reverse Linked List II: Subsegment $[L \dots R]$

> **Problem**: Given the `head` of a singly linked list and two integers `left` and `right` where `left <= right`, reverse the nodes of the list from position `left` to position `right`, and return the reversed list in a **single pass**.

### The Head-Insertion Splicing Technique
Instead of detaching the sublist and reversing it separately, we iteratively remove the node immediately following `curr` (`curr.next`) and insert it directly behind `leftPre`:

```text
Initial Subsegment:
leftPre ──► [ 1 ] ──► [ 2 ] ──► [ 3 ] ──► [ 4 ] ──► [ 5 ]
             ▲         ▲         ▲
          leftPre    curr     forward

Step 1: Move [3] between leftPre and [2]:
leftPre ──► [ 1 ] ──► [ 3 ] ──► [ 2 ] ──► [ 4 ] ──► [ 5 ]
                       ▲         ▲
                    inserted    curr

Step 2: Move [4] between leftPre and [3]:
leftPre ──► [ 1 ] ──► [ 4 ] ──► [ 3 ] ──► [ 2 ] ──► [ 5 ]
```

```java
public class ReverseLinkedListBetween {
    public ListNode reverseBetween(ListNode head, int left, int right) {
        if (head == null || left == right) return head;

        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode leftPre = dummy;

        // 1. Advance leftPre to the node immediately preceding index 'left'
        for (int i = 1; i < left; i++) {
            leftPre = leftPre.next;
        }

        ListNode curr = leftPre.next;

        // 2. Perform (right - left) head-insertions
        for (int i = 0; i < right - left; i++) {
            ListNode forward = curr.next;
            curr.next = forward.next;
            forward.next = leftPre.next;
            leftPre.next = forward;
        }

        return dummy.next;
    }
}
```

- **Time Complexity**: $O(N)$ (Single pass, at most $N$ pointer updates).
- **Auxiliary Space**: $O(1)$ (In-place pointer rewiring).

---

## 3. Reverse Nodes in $K$-Group (Hard)

> **Problem**: Given the `head` of a linked list, reverse the nodes of the list $k$ at a time, and return the modified list. If the number of nodes is not a multiple of $k$ then left-out nodes, in the end, should remain as it is.

### Architectural Invariant
1. Look ahead by $k$ nodes. If fewer than $k$ nodes remain, **terminate** (leave remaining nodes unchanged).
2. Reverse the $k$-node batch in-place.
3. Stitch the previous group's tail to the new group's head, and advance pointer handles.

```text
Group 1: [ 1, 2 ] ──► Reversed to: [ 2, 1 ]
Group 2: [ 3, 4 ] ──► Reversed to: [ 4, 3 ]
Remaining: [ 5 ]  ──► Leaves [ 5 ] intact!

Stitching seams:
dummy ──► [ 2 ──► 1 ] ──► [ 4 ──► 3 ] ──► [ 5 ] ──► null
```

```java
public class ReverseNodesInKGroup {
    public ListNode reverseKGroup(ListNode head, int k) {
        if (head == null || k <= 1) return head;

        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode groupPrev = dummy;

        while (true) {
            // 1. Verify if at least k nodes remain
            ListNode kth = getKthNode(groupPrev, k);
            if (kth == null) {
                break; // Fewer than k nodes remain: Stop!
            }

            ListNode groupNext = kth.next;

            // 2. Reverse current k-group
            ListNode prev = groupNext;
            ListNode curr = groupPrev.next;

            while (curr != groupNext) {
                ListNode temp = curr.next;
                curr.next = prev;
                prev = curr;
                curr = temp;
            }

            // 3. Rewire seams
            ListNode tempTail = groupPrev.next;
            groupPrev.next = kth;
            groupPrev = tempTail; // Advance to tail of reversed group
        }

        return dummy.next;
    }

    private ListNode getKthNode(ListNode curr, int k) {
        while (curr != null && k > 0) {
            curr = curr.next;
            k--;
        }
        return curr;
    }
}
```

- **Time Complexity**: $O(N)$ — Every node is visited twice (once during $k$-lookahead, once during reversal).
- **Auxiliary Space**: $O(1)$ — Strictly constant pointer variables.

---

## 4. Reorder List ($L_0 \rightarrow L_n \rightarrow L_1 \rightarrow L_{n-1} \dots$)

> **Problem**: You are given the head of a singly linked-list: $L_0 \rightarrow L_1 \rightarrow \dots \rightarrow L_{n-1} \rightarrow L_n$. Reorder the list to be on the form: $L_0 \rightarrow L_n \rightarrow L_1 \rightarrow L_{n-1} \rightarrow L_2 \rightarrow L_{n-2} \dots$. You must not modify values in the nodes, only pointers.

### The 3-Stage Pattern Composition
This problem demonstrates how multiple fundamental patterns compose into a clean solution:
1. **Find Middle**: Fast & Slow pointers locate the split midpoint.
2. **Sever & Reverse**: Cut the list in half (`prev.next = null`), then reverse the second half.
3. **Interleave (Merge Alternate)**: Splice alternating nodes from both halves.

```text
Original:    [ 1 ──► 2 ──► 3 ──► 4 ──► 5 ]
Mid-Split:   List 1: [ 1 ──► 2 ──► 3 ]
             List 2: [ 4 ──► 5 ]

Reverse L2:  List 2 Reversed: [ 5 ──► 4 ]

Interleave:  1 ──► 5 ──► 2 ──► 4 ──► 3 ──► null
```

```java
public class ReorderList {
    public void reorderList(ListNode head) {
        if (head == null || head.next == null) return;

        // Stage 1: Find middle node using Fast & Slow pointers
        ListNode slow = head;
        ListNode fast = head;
        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        // Stage 2: Sever bridge and reverse second half
        ListNode secondHalf = reverse(slow.next);
        slow.next = null; // Sever bridge to prevent cycle!

        // Stage 3: Interleave both halves
        ListNode firstHalf = head;
        while (secondHalf != null) {
            ListNode temp1 = firstHalf.next;
            ListNode temp2 = secondHalf.next;

            firstHalf.next = secondHalf;
            secondHalf.next = temp1;

            firstHalf = temp1;
            secondHalf = temp2;
        }
    }

    private ListNode reverse(ListNode head) {
        ListNode prev = null;
        ListNode curr = head;
        while (curr != null) {
            ListNode nextTemp = curr.next;
            curr.next = prev;
            prev = curr;
            curr = nextTemp;
        }
        return prev;
    }
}
```

- **Time Complexity**: $O(N)$ (Find middle $N/2$ + Reverse $N/2$ + Interleave $N/2 = 1.5N = O(N)$).
- **Auxiliary Space**: $O(1)$ (Zero heap allocation).

---

## 5. Self-Check & Active Recall

1. **Q**: In "Reverse Linked List II", why is using a dummy node mandatory?
   - *A*: If `left = 1`, the subsegment to reverse starts at the very first node (`head`). The node preceding `left` (`leftPre`) does not exist unless provided by `dummy`. Returning `dummy.next` ensures the newly promoted head is returned correctly.

2. **Q**: In the recursive implementation of `reverseList`, why is `head.next = null;` required after `head.next.next = head;`?
   - *A*: If you do not set `head.next = null`, the original head node and its successor will point to each other in a cyclic loop (`A <-> B`), causing infinite traversal loops.

3. **Q**: In "Reorder List", why must `slow.next = null;` be executed before interleaving?
   - *A*: To officially terminate the first sublist. If the bridge is not severed, the final node of the first half will still point to the old second half, forming a cycle when interleaved.

---

## 🧭 Continue Learning

| ◀️ Previous Topic | 🧭 Track Hub | Next Topic ▶️ |
| :--- | :---: | ---: |
| [**Page 02: Fast & Slow Pointers**](02-fast-and-slow-pointers-and-cycle-detection.md)<br><sub>*Floyd's Algorithm, Middle & Palindrome*</sub> | [**Linked Lists Index**](README.md)<br><sub>*All 8 Modules*</sub> | [**Page 04: Merging, Sorting & Partitioning**](04-merging-sorting-and-partitioning.md)<br><sub>*Merge K Lists, Merge Sort & Partition*</sub> |
