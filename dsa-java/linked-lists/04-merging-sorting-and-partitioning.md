# Page 04: Merging, Sorting & Partitioning

Sorting and merging linked lists highlight one of the greatest architectural advantages of pointer-based data structures: **rearranging elements requires zero physical data copying**. 

In an array, merging two sorted lists requires allocating an auxiliary array of size $N + M$ ($O(N + M)$ space); in a linked list, merging requires **$O(1)$ auxiliary space** via reference rewiring.

---

## 1. Canonical Problem 1: Merge Two Sorted Lists

> **Problem**: You are given the heads of two sorted linked lists `list1` and `list2`. Merge the two lists into one sorted list. The list should be made by splicing together the nodes of the first two lists. Return the head of the merged linked list.

### The Sentinel Pointer Walk
1. Initialize a `dummy` node and maintain a pointer `tail = dummy`.
2. Compare `list1.val` and `list2.val`. Splice the smaller node to `tail.next`, and advance that list's pointer.
3. Once one list is exhausted, attach the remainder of the other list in a **single $O(1)$ pointer assignment**!

```java
public class MergeTwoSortedLists {
    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;

        while (list1 != null && list2 != null) {
            if (list1.val <= list2.val) {
                tail.next = list1;
                list1 = list1.next;
            } else {
                tail.next = list2;
                list2 = list2.next;
            }
            tail = tail.next;
        }

        // O(1) Splicing: Attach whichever sublist still has remaining nodes
        tail.next = (list1 != null) ? list1 : list2;

        return dummy.next;
    }
}
```

- **Time Complexity**: $O(N + M)$ (Visits each node at most once).
- **Auxiliary Space**: $O(1)$ (Zero allocation; strictly re-links existing nodes).

---

## 2. Canonical Problem 2: Merge $K$ Sorted Lists (Hard)

> **Problem**: You are given an array of `k` linked-lists `lists`, each linked-list is sorted in ascending order. Merge all the linked-lists into one sorted linked-list and return it.

We explore the two optimal production approaches:

### Approach A: Divide & Conquer (Pairwise Merge)
Group the $K$ lists into pairs and merge each pair using `mergeTwoLists`. After the first pass, $K/2$ lists remain. Repeat until only $1$ list remains:

```text
Pass 0: [ L0 ] [ L1 ] [ L2 ] [ L3 ] [ L4 ] [ L5 ] [ L6 ] [ L7 ]  (K lists)
           └──┬──┘       └──┬──┘       └──┬──┘       └──┬──┘
Pass 1:    [ M01 ]       [ M23 ]       [ M45 ]       [ M67 ]     (K/2 lists)
              └──────┬──────┘             └──────┬──────┘
Pass 2:           [ M0123 ]                   [ M4567 ]          (K/4 lists)
                     └────────────────┬────────────────┘
Pass 3:                          [ FINAL SORTED ]                (1 list)
Total Levels = log2(K)
```

```java
public class MergeKSortedListsDivideAndConquer {
    public ListNode mergeKLists(ListNode[] lists) {
        if (lists == null || lists.length == 0) return null;
        return divideAndConquer(lists, 0, lists.length - 1);
    }

    private ListNode divideAndConquer(ListNode[] lists, int left, int right) {
        if (left == right) return lists[left];
        int mid = left + (right - left) / 2;

        ListNode l1 = divideAndConquer(lists, left, mid);
        ListNode l2 = divideAndConquer(lists, mid + 1, right);

        return mergeTwo(l1, l2);
    }

    private ListNode mergeTwo(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;
        while (l1 != null && l2 != null) {
            if (l1.val <= l2.val) {
                tail.next = l1;
                l1 = l1.next;
            } else {
                tail.next = l2;
                l2 = l2.next;
            }
            tail = tail.next;
        }
        tail.next = (l1 != null) ? l1 : l2;
        return dummy.next;
    }
}
```

- **Time Complexity**: $O(N \log K)$ where $N$ is the total number of nodes across all lists.
- **Auxiliary Space**: $O(\log K)$ recursion call stack space.

---

### Approach B: Min-Heap (PriorityQueue)
Insert the head node of each of the $K$ lists into a **Min-Heap**. Repeatedly poll the minimum node, append it to the merged list, and offer its `next` node into the heap:

```java
import java.util.PriorityQueue;

public class MergeKSortedListsHeap {
    public ListNode mergeKLists(ListNode[] lists) {
        if (lists == null || lists.length == 0) return null;

        // Min-heap ordered by node value
        PriorityQueue<ListNode> minHeap = new PriorityQueue<>(lists.length, (a, b) -> Integer.compare(a.val, b.val));

        // Offer initial heads
        for (ListNode listHead : lists) {
            if (listHead != null) {
                minHeap.offer(listHead);
            }
        }

        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;

        while (!minHeap.isEmpty()) {
            ListNode smallest = minHeap.poll();
            tail.next = smallest;
            tail = tail.next;

            if (smallest.next != null) {
                minHeap.offer(smallest.next);
            }
        }

        return dummy.next;
    }
}
```

- **Time Complexity**: $O(N \log K)$ — Each of the $N$ nodes is pushed and polled from the heap of size $K$ once.
- **Auxiliary Space**: $O(K)$ — For the PriorityQueue holding at most $K$ nodes.

---

## 3. Canonical Problem 3: Sort List ($O(N \log N)$ Merge Sort)

> **Problem**: Given the `head` of a linked list, return the list after sorting it in ascending order. Must run in $O(N \log N)$ time and $O(\log N)$ stack space without converting the list to an array.

### Why Merge Sort is Superior for Linked Lists
QuickSort requires random access and in-place pivoting, which performs poorly on linked lists. **Merge Sort** is ideal:
1. Finding the midpoint takes $O(N)$ with Fast & Slow pointers.
2. Severing the list into two independent halves takes $O(1)$.
3. Merging two sorted lists takes $O(N)$ time and **$O(1)$ extra space**.

```java
public class SortListMergeSort {
    public ListNode sortList(ListNode head) {
        // Base case: 0 or 1 node is already sorted
        if (head == null || head.next == null) {
            return head;
        }

        // 1. Split list into two halves using Fast & Slow pointers
        ListNode prev = null;
        ListNode slow = head;
        ListNode fast = head;

        while (fast != null && fast.next != null) {
            prev = slow;
            slow = slow.next;
            fast = fast.next.next;
        }

        // Sever the bridge between first half and second half!
        prev.next = null;

        // 2. Recursively sort each half
        ListNode leftSorted = sortList(head);
        ListNode rightSorted = sortList(slow);

        // 3. Merge sorted halves
        return merge(leftSorted, rightSorted);
    }

    private ListNode merge(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;

        while (l1 != null && l2 != null) {
            if (l1.val <= l2.val) {
                tail.next = l1;
                l1 = l1.next;
            } else {
                tail.next = l2;
                l2 = l2.next;
            }
            tail = tail.next;
        }

        tail.next = (l1 != null) ? l1 : l2;
        return dummy.next;
    }
}
```

- **Time Complexity**: $O(N \log N)$ (Standard divide-and-conquer recurrence $T(N) = 2T(N/2) + O(N)$).
- **Auxiliary Space**: $O(\log N)$ (Recursive call stack depth).

---

## 4. Canonical Problem 4: Partition List

> **Problem**: Given the `head` of a linked list and a value `x`, partition it such that all nodes less than `x` come before nodes greater than or equal to `x`. You should **preserve the original relative order** of the nodes in each of the two partitions.

### The Dual Sentinel List Pattern
1. Create two independent dummy lists: `lessHead` (for nodes $< x$) and `greaterHead` (for nodes $\ge x$).
2. Walk the original list, detaching and appending nodes to either `lessTail` or `greaterTail`.
3. Connect `lessTail.next = greaterHead.next`.
4. **Sever the tail**: Set `greaterTail.next = null` to prevent accidental cycles!

```text
Original List: [ 1 ──► 4 ──► 3 ──► 2 ──► 5 ──► 2 ], x = 3

Less List:     dummyLess ──► [ 1 ] ──► [ 2 ] ──► [ 2 ]
Greater List:  dummyGreater ──► [ 4 ] ──► [ 3 ] ──► [ 5 ]

Stitch:
[ 1 ──► 2 ──► 2 ] ──► [ 4 ──► 3 ──► 5 ] ──► null
```

```java
public class PartitionList {
    public ListNode partition(ListNode head, int x) {
        ListNode lessDummy = new ListNode(0);
        ListNode greaterDummy = new ListNode(0);

        ListNode lessTail = lessDummy;
        ListNode greaterTail = greaterDummy;

        ListNode curr = head;
        while (curr != null) {
            if (curr.val < x) {
                lessTail.next = curr;
                lessTail = lessTail.next;
            } else {
                greaterTail.next = curr;
                greaterTail = greaterTail.next;
            }
            curr = curr.next;
        }

        // CRITICAL: Terminate greater list to prevent cycle!
        greaterTail.next = null;

        // Connect tail of less list to head of greater list
        lessTail.next = greaterDummy.next;

        return lessDummy.next;
    }
}
```

- **Time Complexity**: $O(N)$ (Single pass).
- **Auxiliary Space**: $O(1)$ (Zero new nodes allocated; rewires existing pointers).

---

## 5. Self-Check & Active Recall

1. **Q**: In "Partition List", what catastrophic bug occurs if `greaterTail.next = null;` is omitted?
   - *A*: If the last node in `greaterTail` was originally followed by a node that was moved to `lessTail`, failing to set `greaterTail.next = null` leaves an active reference pointing backward, creating a **cyclic linked list** that crashes with an infinite loop.

2. **Q**: Why is `PriorityQueue` space $O(K)$ in Merge $K$ Sorted Lists instead of $O(N)$?
   - *A*: The heap stores at most **one node per list** at any given moment (the current head of that list). As soon as a node is polled, only its single immediate successor is offered. At no point does the heap exceed $K$ elements.

3. **Q**: In Sort List (Merge Sort), why do we set `prev.next = null` after the Fast & Slow pointer split?
   - *A*: To officially terminate the left half. If `prev.next` is not set to `null`, the left sublist remains attached to the right sublist, causing infinite recursion on the left half.

---

## 🧭 Continue Learning

| ◀️ Previous Topic | 🧭 Track Hub | Next Topic ▶️ |
| :--- | :---: | ---: |
| [**Page 03: In-Place Reversals**](03-in-place-reversal-and-subsegment-manipulations.md)<br><sub>*Iterative, Recursive & K-Group Reversals*</sub> | [**Linked Lists Index**](README.md)<br><sub>*All 8 Modules*</sub> | [**Page 05: Deep Copy & Complex Rewiring**](05-deep-copy-and-complex-pointer-rewiring.md)<br><sub>*Random Pointers & Multilevel Lists*</sub> |
