# Page 02: Fast & Slow Pointers & Cycle Detection

The **Fast & Slow Pointers** pattern (also known as **Floyd's Tortoise and Hare Algorithm**) is the most mathematically elegant technique for navigating linked lists. By having two pointers traverse the list at differing velocities, we can detect cycles, locate cycle entrances, find midpoints, and identify list intersections in $O(N)$ time and **$O(1)$ auxiliary space**.

---

## 1. The Core Invariant: Why Fast and Slow Pointers Always Meet

Let pointer `slow` advance **1 node per iteration**, and pointer `fast` advance **2 nodes per iteration**:

```text
Linear Traversal:
slow: [ 1 ] ──► [ 2 ] ──► [ 3 ] ──► [ 4 ] ──► [ 5 ]
        ▲         ▲
      slow      fast (Step 1)
                  ▲                   ▲
                slow                fast (Step 2)
```

### The Relative Velocity Proof
Inside a cycle of length $C$:
- In every iteration, `slow` moves forward by $1$ step.
- `fast` moves forward by $2$ steps.
- The relative distance between `fast` and `slow` decreases by **strictly 1 node per iteration** ($2 - 1 = 1$).
- Because the distance decrements as integers ($\dots, 3, 2, 1, 0$), **`fast` can never skip over `slow`**. They are mathematically guaranteed to collide within at most $C$ iterations!

---

## 2. Canonical Problem 1: Linked List Cycle I (Boolean Detection)

> **Problem**: Given `head`, the head of a linked list, determine if the linked list has a cycle in it. Return `true` if there is a cycle, otherwise return `false`. Must use $O(1)$ memory.

```java
public class LinkedListCycleI {
    public boolean hasCycle(ListNode head) {
        if (head == null || head.next == null) return false;

        ListNode slow = head;
        ListNode fast = head;

        while (fast != null && fast.next != null) {
            slow = slow.next;         // 1 step
            fast = fast.next.next;    // 2 steps

            if (slow == fast) {
                return true; // Pointers collided: Cycle exists!
            }
        }

        return false; // Fast reached null: No cycle!
    }
}
```

- **Time Complexity**: $O(N)$ — If no cycle, `fast` reaches `null` in $N/2$ steps. If cycle exists, `fast` catches `slow` within $N + C$ steps.
- **Auxiliary Space**: $O(1)$ — Two reference variables.

---

## 3. Canonical Problem 2: Linked List Cycle II (Finding Cycle Entrance)

> **Problem**: Given the head of a linked list, return the **node where the cycle begins**. If there is no cycle, return `null`. Do not modify the linked list. Must use $O(1)$ memory.

### Mathematical Proof of the Cycle Entrance Equation

```text
╭─────────────────────────────────────────────────────────────────────────────╮
│                         Floyd's Cycle Geometry Proof                        │
├─────────────────────────────────────────────────────────────────────────────┤
│ head                                       entrance                         │
│  ● ───► ● ───► ● ───────────────► ● ◄───────────────┐                       │
│  ◄────── Distance F ────────────► │                 │                       │
│                                   ▼                 │ (Cycle Length C)      │
│                                   ●                 │                       │
│                                   │                 ▲                       │
│                                   ▼                 │                       │
│                                   ● ───► ● ───► ● ──┘                       │
│                                          ▲                                  │
│                                    meeting point                            │
│                                   (Distance a from entrance)                │
╰─────────────────────────────────────────────────────────────────────────────╯
```

Let:
- $F$ = Distance from `head` to the cycle entrance.
- $a$ = Distance from cycle entrance to the collision meeting point.
- $C$ = Total length of the cycle.

When `slow` and `fast` collide:
$$\text{Distance traveled by slow} = F + a$$
$$\text{Distance traveled by fast} = F + a + nC \quad (\text{where } n \ge 1 \text{ loops completed})$$

Because `fast` moves at twice the speed of `slow`:
$$\text{Distance}(fast) = 2 \times \text{Distance}(slow)$$
$$F + a + nC = 2(F + a)$$
$$F + a + nC = 2F + 2a$$
$$\mathbf{F = nC - a = (n - 1)C + (C - a)}$$

### The Breakthrough:
- $F$ is the distance from `head` to the entrance.
- $(C - a)$ is the remaining distance from the meeting point to the entrance!
- **If we place one pointer at `head` and keep another pointer at the meeting point, and advance both at the exact same speed of 1 step per iteration, they will collide EXACTLY at the cycle entrance!**

```java
public class LinkedListCycleII {
    public ListNode detectCycle(ListNode head) {
        if (head == null || head.next == null) return null;

        ListNode slow = head;
        ListNode fast = head;
        boolean hasCycle = false;

        // Phase 1: Detect meeting point
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;

            if (slow == fast) {
                hasCycle = true;
                break;
            }
        }

        if (!hasCycle) return null;

        // Phase 2: Find cycle entrance (advance both by 1 step)
        ListNode p1 = head;
        ListNode p2 = slow;

        while (p1 != p2) {
            p1 = p1.next;
            p2 = p2.next;
        }

        return p1; // Both meet at the cycle entrance!
    }
}
```

- **Time Complexity**: $O(N)$ (Phase 1 takes at most $2N$ steps; Phase 2 takes at most $N$ steps).
- **Auxiliary Space**: $O(1)$.

---

## 4. Canonical Problem 3: Middle of the Linked List

> **Problem**: Given the `head` of a singly linked list, return the middle node of the linked list. If there are two middle nodes (even length), return the **second middle node**.

### Termination Invariants (Odd vs. Even Length)

```text
Case 1: Odd Length (3 nodes: [1, 2, 3])
Step 0: slow=1, fast=1
Step 1: slow=2, fast=3 ──► fast.next == null (Stop!)
Middle node is 2.

Case 2: Even Length (4 nodes: [1, 2, 3, 4])
Step 0: slow=1, fast=1
Step 1: slow=2, fast=3
Step 2: slow=3, fast=null ──► fast == null (Stop!)
Second middle node is 3.
```

```java
public class MiddleOfLinkedList {
    public ListNode middleNode(ListNode head) {
        ListNode slow = head;
        ListNode fast = head;

        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        return slow; // Points directly to middle (or second middle)
    }
}
```

- **Time Complexity**: $O(N)$ (Traverses each node at most once).
- **Auxiliary Space**: $O(1)$.

---

## 5. Canonical Problem 4: Palindrome Linked List ($O(1)$ Space)

> **Problem**: Given the `head` of a singly linked list, return `true` if it is a palindrome, or `false` otherwise. Solve in $O(N)$ time and $O(1)$ auxiliary space.

### The 4-Stage In-Place Protocol
1. **Find Middle**: Locate middle of list using Fast & Slow pointers.
2. **Reverse Second Half**: Invert `next` pointers of the second half in-place.
3. **Compare Halves**: Walk two pointers from `head` and `reversed_head` comparing values.
4. **Restore List**: Re-reverse the second half back to original order (production code etiquette).

```java
public class PalindromeLinkedList {
    public boolean isPalindrome(ListNode head) {
        if (head == null || head.next == null) return true;

        // 1. Find end of first half
        ListNode firstHalfEnd = getFirstHalfEnd(head);
        // 2. Reverse second half
        ListNode secondHalfStart = reverseList(firstHalfEnd.next);

        // 3. Compare values
        ListNode p1 = head;
        ListNode p2 = secondHalfStart;
        boolean isPalin = true;

        while (isPalin && p2 != null) {
            if (p1.val != p2.val) {
                isPalin = false;
            }
            p1 = p1.next;
            p2 = p2.next;
        }

        // 4. Restore original list structure
        firstHalfEnd.next = reverseList(secondHalfStart);

        return isPalin;
    }

    private ListNode getFirstHalfEnd(ListNode head) {
        ListNode slow = head;
        ListNode fast = head;
        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
    }

    private ListNode reverseList(ListNode head) {
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

- **Time Complexity**: $O(N)$ (Middle search $N/2$ + Reverse $N/2$ + Compare $N/2$ + Restore $N/2 = 2N = O(N)$).
- **Auxiliary Space**: $O(1)$ (In-place pointer rewiring).

---

## 6. Canonical Problem 5: Intersection of Two Linked Lists

> **Problem**: Given the heads of two singly linked lists `headA` and `headB`, return the node at which the two lists intersect. If they do not intersect, return `null`. Must run in $O(N + M)$ time and $O(1)$ space.

### The Two-Pointer Cycle-Switch Technique
Let List A have length $a + c$ and List B have length $b + c$, where $c$ is the shared intersection tail.
- Pointer `pA` traverses List A, then jumps to `headB` and traverses List B. Total distance: $a + c + b$.
- Pointer `pB` traverses List B, then jumps to `headA` and traverses List A. Total distance: $b + c + a$.
- Because $a + c + b = b + c + a$, **both pointers traverse the exact same total distance!** They either meet at the intersection node or both land on `null` simultaneously.

```text
List A:   a1 ──► a2 ──┐
                      ├──► c1 ──► c2 ──► c3 (Length c)
List B:   b1 ──► b2 ──┘
          ◄─ b ─►

pA path: [ a1, a2, c1, c2, c3 ] ──► [ b1, b2, c1... ]
pB path: [ b1, b2, c1, c2, c3 ] ──► [ a1, a2, c1... ]
                                        ▲
                       Both collide at c1 at the exact same step!
```

```java
public class IntersectionOfTwoLinkedLists {
    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        if (headA == null || headB == null) return null;

        ListNode pA = headA;
        ListNode pB = headB;

        // Once pA reaches null, redirect to headB.
        // Once pB reaches null, redirect to headA.
        while (pA != pB) {
            pA = (pA == null) ? headB : pA.next;
            pB = (pB == null) ? headA : pB.next;
        }

        return pA; // Either the intersection node, or null if no intersection
    }
}
```

- **Time Complexity**: $O(M + N)$ (At most $2(M + N)$ steps).
- **Auxiliary Space**: $O(1)$.

---

## 7. Self-Check & Active Recall

1. **Q**: In "Linked List Cycle II", why does resetting one pointer to `head` and advancing both by 1 step land exactly on the cycle entrance?
   - *A*: Mathematically, $F = nC - a$. The distance from `head` to the entrance ($F$) is identical to the distance from the meeting point around the cycle to the entrance ($nC - a$). Moving both at 1 step per tick guarantees they meet at the entrance.

2. **Q**: In "Middle of the Linked List", what is the difference between `while (fast != null && fast.next != null)` and `while (fast.next != null && fast.next.next != null)`?
   - *A*: The first condition stops with `slow` at the **second middle** node of an even-length list (e.g. index 2 in a 4-node list). The second condition stops with `slow` at the **first middle** node (e.g. index 1 in a 4-node list), which is crucial when splitting a list into two equal halves (such as in Palindrome checks or Merge Sort).

3. **Q**: What happens in the Intersection of Two Linked Lists algorithm if the two lists do NOT intersect?
   - *A*: Both `pA` and `pB` will complete traversing both lists and reach `null` at the exact same step ($pA == null$ and $pB == null$). The condition `pA != pB` evaluates to `false`, and the loop safely returns `null`.

---

## 🧭 Continue Learning

| ◀️ Previous Topic | 🧭 Track Hub | Next Topic ▶️ |
| :--- | :---: | ---: |
| [**Page 01: Array Limitations & Fundamentals**](01-array-limitations-and-linkedlist-fundamentals.md)<br><sub>*ArrayList vs LinkedList Memory Model*</sub> | [**Linked Lists Index**](README.md)<br><sub>*All 8 Modules*</sub> | [**Page 03: In-Place Reversal**](03-in-place-reversal-and-subsegment-manipulations.md)<br><sub>*Iterative, Recursive & K-Group Reversals*</sub> |
