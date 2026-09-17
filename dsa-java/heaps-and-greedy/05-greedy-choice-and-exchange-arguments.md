# 05. Greedy Choice & Exchange Arguments: Proving Local Decisions

[← Back to K-Way Merge](./04-k-way-merge-and-interval-scheduling.md) | [Track Hub](./README.md) | [Next: Advanced Hard Problems →](./06-advanced-hard-heap-and-greedy-problems.md)

---

## 1. The Theory of Greedy Algorithms & The Exchange Argument

In algorithm design, **Greedy** is fundamentally different from **Dynamic Programming**:
- **Dynamic Programming** explores all candidate choices at each step, evaluates their subproblem values, and memoizes the global best.
- **Greedy** irrevocably commits to the **locally optimal choice** at each step without ever looking back or re-evaluating earlier choices.

```
╭───────────────────────────────────────────────────────────────────────────────────────────╮
│                             THE GREEDY PREREQUISITE CONDITIONS                            │
├───────────────────────────────────────────────────────────────────────────────────────────┤
│ 1. Greedy Choice Property: A globally optimal solution can be reached by making a         │
│    locally optimal (greedy) choice at each stage.                                         │
│ 2. Optimal Substructure: An optimal solution to the problem contains within it optimal    │
│    solutions to its subproblems.                                                          │
╰───────────────────────────────────────────────────────────────────────────────────────────╯
```

### The Formal Exchange Argument Proof Template

To prove in a technical interview that a greedy choice is mathematically sound, use the **Exchange Argument**:

```
Step 1: Define G as the greedy solution and O as an arbitrary optimal solution.
Step 2: If G == O, the greedy solution is already optimal.
Step 3: If G != O, locate the FIRST choice where G and O differ (say, G chose g_1 while O chose o_1).
Step 4: Prove that swapping o_1 with g_1 in O creates a new valid solution O' such that:
        Cost(O') <= Cost(O)  (for minimization) or
        Value(O') >= Value(O) (for maximization).
Step 5: Conclude by mathematical induction that O can be transformed into G without ever
        degrading the objective function. Thus, G is globally optimal! Q.E.D.
```

---

## 2. Problem 1: Task Scheduler (Slot-Filling Mathematics)

### 2.1 Problem Statement & Constraints

You are given an array of CPU tasks, each represented by a letter from `'A'` to `'Z'`, and a cooling interval `n`. Each cycle or interval allows the completion of one task. Tasks can be completed in any order, but there's a constraint: identical tasks must be separated by at least `n` intervals with other tasks or idle slots.

*Return the minimum number of CPU intervals required to complete all tasks.*

```
Input: tasks = ["A","A","A","B","B","B"], n = 2
Output: 8
Explanation: A -> B -> idle -> A -> B -> idle -> A -> B
Total intervals: 8.
```

#### Constraints:
- $1 \le \text{tasks.length} \le 10^4$.
- `tasks[i]` is an uppercase English letter.
- $0 \le n \le 100$.

---

### 2.2 Thought Process: The Mathematical Slot-Filling Invariant

```
  Naive Simulation:
  Maintain a Max-Heap of task counts + wait queue of cooled tasks. Simulate tick by tick.
  Time: O(Total Intervals * log 26) -> Complex and slow!
                         ↓
  The "Aha!" Mathematical Insight:
  The task with the MAXIMUM FREQUENCY (maxFreq) dictates the overall structure!
  
  Suppose 'A' has maxFreq = 3, cooling n = 2:
  Frame:
  [ A ]  [ _ ]  [ _ ]
  [ A ]  [ _ ]  [ _ ]
  [ A ]
  
  • Number of chunks with empty slots = maxFreq - 1 = 2 chunks.
  • Size of each chunk = n + 1 = 3 slots.
  • Total base slots for maxFreq chunks = (maxFreq - 1) * (n + 1).
  • The final row contains 1 slot for 'A', plus 1 slot for every OTHER task
    that shares the exact same maximum frequency!
  
  Formula:
  Ans = (maxFreq - 1) * (n + 1) + countOfTasksWithMaxFreq
  
  What if n is small and we have lots of unique tasks?
  The total intervals can NEVER be less than tasks.length!
  
  Final Invariant:
  Min Intervals = Math.max(tasks.length, (maxFreq - 1) * (n + 1) + maxFreqCount)!
  Time: Strictly O(N) linear time, Space: O(1) (26-element array)!
```

---

### 2.3 Visual State Transition

```
tasks = ["A","A","A","B","B","B","C"], n = 2
maxFreq = 3 ('A' and 'B'). maxFreqCount = 2 ('A', 'B').

Slots Allocation Grid:
Chunk 0: [ A ] [ B ] [ C ]
Chunk 1: [ A ] [ B ] [ idle ]
Last:    [ A ] [ B ]

Total Slots: (3 - 1) * (2 + 1) + 2 = 2 * 3 + 2 = 8.
Math.max(7, 8) = 8 intervals!
```

---

### 2.4 Production Java 17/21 Implementation

```java
package com.structures.heaps;

/**
 * Solves Task Scheduler in O(N) time and O(1) space using slot-filling math.
 */
public final class TaskScheduler {

    public int leastInterval(char[] tasks, int n) {
        if (tasks == null || tasks.length == 0) {
            return 0;
        }

        // 1. Compute frequency of each task: O(N)
        int[] counts = new int[26];
        int maxFreq = 0;
        for (char task : tasks) {
            counts[task - 'A']++;
            maxFreq = Math.max(maxFreq, counts[task - 'A']);
        }

        // 2. Count how many tasks share this maximum frequency: O(26) = O(1)
        int maxFreqCount = 0;
        for (int count : counts) {
            if (count == maxFreq) {
                maxFreqCount++;
            }
        }

        // 3. Apply the slot-filling formula
        int emptySlotsNeeded = (maxFreq - 1) * (n + 1) + maxFreqCount;

        // The answer can never be fewer than the total number of tasks
        return Math.max(tasks.length, emptySlotsNeeded);
    }
}
```

---

## 3. Problem 2: Gas Station (Circular Tour Invariant)

### 3.1 Problem Statement & Constraints

There are `n` gas stations along a circular route, where the amount of gas at the $i^{\text{th}}$ station is $\text{gas}[i]$.

You have a car with an unlimited gas tank and it costs $\text{cost}[i]$ of gas to travel from the $i^{\text{th}}$ station to its next $(i + 1)^{\text{th}}$ station. You begin the journey with an empty tank at one of the gas stations.

Given two integer arrays `gas` and `cost`, return *the starting gas station's index if you can travel around the circuit once in the clockwise direction, otherwise return* `-1`. If there exists a solution, it is **guaranteed to be unique**.

```
Input: gas = [1,2,3,4,5], cost = [3,4,5,1,2]
Output: 3
Explanation:
Start at station 3 (index 3) and fill up with 4 unit of gas. Your tank = 0 + 4 = 4
Travel to station 4. Your tank = 4 - 1 + 5 = 8
Travel to station 0. Your tank = 8 - 2 + 1 = 7
Travel to station 1. Your tank = 7 - 3 + 2 = 6
Travel to station 2. Your tank = 6 - 4 + 3 = 5
Travel to station 3. The cost is 5. Your gas is just enough to travel back to station 3.
Therefore, return 3 as the starting index.
```

#### Constraints:
- $n == \text{gas.length} == \text{cost.length}$.
- $1 \le n \le 10^5$.
- $0 \le \text{gas}[i], \text{cost}[i] \le 10^4$.

---

### 3.2 The Mathematical Invariant: Total Gas vs. Total Cost

```
╭───────────────────────────────────────────────────────────────────────────────────────────╮
│                              THE CIRCULAR TOUR THEOREM                                    │
├───────────────────────────────────────────────────────────────────────────────────────────┤
│ Theorem: A complete circular tour is possible IF AND ONLY IF:                             │
│          Sum(gas[i]) >= Sum(cost[i]) across all 0 <= i < N.                               │
│                                                                                           │
│ Proof by Contradiction: If TotalGas < TotalCost, the vehicle consumes strictly more fuel  │
│ than the universe provides. Hence, NO starting point can complete a circuit.              │
│                                                                                           │
│ Greedy Pruning Invariant:                                                                 │
│ If we start at station A and run out of gas at station B (sum < 0),                       │
│ then NO station between A and B can reach B either!                                       │
│ Why? Because from A to any intermediate station k (A < k <= B), the vehicle arrived at k  │
│ with tank >= 0! Starting fresh at k with tank = 0 would perform strictly WORSE!           │
│ Therefore: We can skip all candidate starting stations from A to B in one leap!          │
│ Candidate start becomes B + 1!                                                            │
╰───────────────────────────────────────────────────────────────────────────────────────────╯
```

---

### 3.3 Production Java 17/21 Implementation

```java
package com.structures.heaps;

/**
 * Solves Gas Station in strictly O(N) time and O(1) space in a single pass.
 */
public final class GasStation {

    public int canCompleteCircuit(int[] gas, int[] cost) {
        if (gas == null || cost == null || gas.length != cost.length) {
            return -1;
        }

        int totalSurplus = 0;
        int currentTank = 0;
        int startingStation = 0;

        for (int i = 0; i < gas.length; i++) {
            int net = gas[i] - cost[i];
            totalSurplus += net;
            currentTank += net;

            // If car runs out of gas, no station between startingStation and i can be the answer
            if (currentTank < 0) {
                startingStation = i + 1;
                currentTank = 0; // Reset tank for new candidate starting station
            }
        }

        // If total gas available across all stations is less than total cost, circuit is impossible
        return totalSurplus >= 0 ? startingStation : -1;
    }
}
```

---

## 4. Problem 3: Reorganize String (Max-Heap Character Pairing)

### 4.1 Problem Statement & Constraints

Given a string `s`, rearrange the characters of `s` so that any two adjacent characters are not the same.

Return *any possible rearrangement of* `s` *or return* `""` *if not possible.*

```
Input: s = "aab"
Output: "aba"

Input: s = "aaab"
Output: ""
```

#### Constraints:
- $1 \le \text{s.length} \le 500$.
- `s` consists of lowercase English letters.

---

### 4.2 Thought Process: Greedy Character Pairing

```
  Feasibility Pigeonhole Invariant:
  If any character has frequency > (s.length() + 1) / 2:
  It is mathematically IMPOSSIBLE to place them without collision! Return "" immediately!
                         ↓
  Greedy Max-Heap Pairing:
  To avoid adjacent collisions, greedily pick the TWO MOST FREQUENT remaining characters!
  1. Poll firstMostFrequent and secondMostFrequent from Max-Heap.
  2. Append first, then second to StringBuilder.
  3. Decrement counts; if counts > 0, re-insert into Max-Heap!
  4. Repeat until heap has <= 1 element.
  Time: O(N log 26) = O(N), Space: O(1) (26 characters).
```

---

### 4.3 Production Java 17/21 Implementation

```java
package com.structures.heaps;

import java.util.PriorityQueue;

/**
 * Reorganizes a string such that no two adjacent characters match using a Max-Heap.
 */
public final class ReorganizeString {

    private record CharFreq(char ch, int freq) {}

    public String reorganizeString(String s) {
        if (s == null || s.length() <= 1) {
            return s == null ? "" : s;
        }

        // 1. Compute character frequencies: O(N)
        int[] counts = new int[26];
        int maxAllowed = (s.length() + 1) / 2;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            counts[c - 'a']++;
            if (counts[c - 'a'] > maxAllowed) {
                return ""; // Impossible to separate
            }
        }

        // 2. Max-Heap ordered by remaining frequency: O(26 log 26) = O(1)
        PriorityQueue<CharFreq> maxHeap = new PriorityQueue<>(
            26,
            (a, b) -> Integer.compare(b.freq, a.freq)
        );

        for (int i = 0; i < 26; i++) {
            if (counts[i] > 0) {
                maxHeap.offer(new CharFreq((char) ('a' + i), counts[i]));
            }
        }

        StringBuilder result = new StringBuilder(s.length());

        // 3. Greedily extract top two characters
        while (maxHeap.size() >= 2) {
            CharFreq first = maxHeap.poll();
            CharFreq second = maxHeap.poll();

            result.append(first.ch);
            result.append(second.ch);

            if (first.freq - 1 > 0) {
                maxHeap.offer(new CharFreq(first.ch, first.freq - 1));
            }
            if (second.freq - 1 > 0) {
                maxHeap.offer(new CharFreq(second.ch, second.freq - 1));
            }
        }

        // If one character remains, it must have frequency 1
        if (!maxHeap.isEmpty()) {
            result.append(maxHeap.poll().ch);
        }

        return result.toString();
    }
}
```

---

<table width="100%">
  <tr>
    <td width="33%" align="left">
      <a href="./04-k-way-merge-and-interval-scheduling.md">
        <strong>← Previous Module</strong><br>
        04. K-Way Merge & Interval Scheduling
      </a>
    </td>
    <td width="33%" align="center">
      <a href="./README.md">
        <strong>Track Hub</strong><br>
        Heaps & Greedy Track Hub
      </a>
    </td>
    <td width="33%" align="right">
      <a href="./06-advanced-hard-heap-and-greedy-problems.md">
        <strong>Next Module →</strong><br>
        06. Advanced Hard Problems
      </a>
    </td>
  </tr>
</table>
