# Page 3: Memory Layout, Stack vs. Heap & Pass-by-Value

Welcome to Page 3 of the Java Fundamentals series. Understanding how Java manages memory under the hood is critical for writing memory-efficient algorithms and avoiding the #1 most common interview mistake: **misunderstanding Pass-by-Value**.

---

## 1. JVM Memory Areas: Stack vs. Heap vs. Metaspace

When a Java program runs, the JVM divides the operating system memory into distinct logical regions:

```text
╭─────────────────────────────────────────────────────────────────────────────╮
│                             JVM Runtime Memory                              │
├──────────────────────────────┬──────────────────────────────────────────────┤
│ 🧵 Stack Memory              │ 🌐 Heap Memory                               │
│    (Thread-Isolated)         │    (Shared Across All Threads)               │
├──────────────────────────────┼──────────────────────────────────────────────┤
│ [Frame: main()]              │                                              │
│  - primitive: int a = 10     │ ╭──────────────────────────────────────────╮ │
│  - ref: Node head ───────────┼─▶│ Node Object (Heap address: 0x4A)        │ │
│                              │  │  - val: 5, next: null                   │ │
│ [Frame: dfs(node)]           │ ╰──────────────────────────────────────────╯ │
│  - primitive: int depth = 1  │ ╭──────────────────────────────────────────╮ │
│  - ref: curr ────────────────┼─▶│ int[] array [1, 2] (Address: 0x8C)      │ │
│                              │ ╰──────────────────────────────────────────╯ │
├──────────────────────────────┴──────────────────────────────────────────────┤
│ 📚 Metaspace: Class Bytecode definitions, Method tables, static variables   │
╰─────────────────────────────────────────────────────────────────────────────╯
```

### 1. Stack Memory
- **Nature**: Fast, organized in Last-In-First-Out (LIFO) stack frames.
- **Contents**: Stores method call frames, local primitive variables, and references to objects residing in the Heap.
- **Scope**: Thread-private (each thread has its own call stack). When a method returns, its stack frame is instantly deallocated.
- **Error**: If recursion goes too deep without hitting a base case, the JVM throws **`java.lang.StackOverflowError`**.

### 2. Heap Memory
- **Nature**: Dynamic memory pool shared by all threads.
- **Contents**: All instances of classes (`new ClassName()`), strings, and arrays live here.
- **Lifecycle**: Managed automatically by the **Garbage Collector (GC)**. When no live stack reference points to a heap object, it becomes eligible for GC.
- **Error**: If the heap runs out of memory for new allocations, the JVM throws **`java.lang.OutOfMemoryError: Java heap space`**.

### 3. Metaspace
- Stores class bytecode, loaded class metadata, and static fields.
- Resides in native memory (outside the standard JVM heap) since Java 8 (replaced PermGen).

---

## 2. Pass-by-Value Mechanics (The #1 Interview Trap)

> [!IMPORTANT]
> **Java is strictly 100% Pass-by-Value.**
> There is NO pass-by-reference in Java (unlike C++ with `int &x`).
> - When you pass a **primitive**, a copy of the primitive value is passed.
> - When you pass an **object/reference**, a **copy of the reference address** is passed.

Let us trace two common scenarios to see this in action:

### Scenario A: Modifying Object Internals vs. Reassigning Reference

```java
public class ReferenceDemo {
    static class Node {
        int val;
        Node(int val) { this.val = val; }
    }

    public static void modifyNode(Node node) {
        // Mutating the object pointed to by the reference:
        node.val = 99; 
    }

    public static void reassignNode(Node node) {
        // Reassigning the local copy of the reference:
        node = new Node(500); 
    }

    public static void main(String[] args) {
        Node myNode = new Node(10);

        modifyNode(myNode);
        System.out.println(myNode.val); // Prints 99 (Heap object was modified!)

        reassignNode(myNode);
        System.out.println(myNode.val); // Still prints 99! (Reassignment only affected local variable inside method)
    }
}
```

### Scenario B: The Classic Swap Mistake

```java
// ❌ THIS DOES NOT SWAP!
public static void swap(int a, int b) {
    int temp = a;
    a = b;
    b = temp;
}

public static void main(String[] args) {
    int x = 5, y = 10;
    swap(x, y);
    System.out.println(x + ", " + y); // Prints 5, 10 (UNCHANGED!)
}
```

### How to Correctly Swap in Java:
Because Java cannot swap primitives across methods, in DSA you should:
1. Swap elements **inside an array**:
   ```java
   public static void swap(int[] nums, int i, int j) {
       int temp = nums[i];
       nums[i] = nums[j];
       nums[j] = temp;
   }
   ```
2. Or perform the swap inline using a temporary variable.

---

## 3. Static vs. Instance Context

```java
public class Counter {
    static int globalCount = 0; // Shared across ALL instances (Class variable)
    int instanceId;             // Separate copy per instance (Object variable)

    public Counter(int id) {
        this.instanceId = id;
        globalCount++;
    }

    // Static method: Operates at class level
    public static void printGlobal() {
        System.out.println("Total: " + globalCount);
        // System.out.println(instanceId); // ❌ COMPILE ERROR! Cannot access instance variable from static context
    }
}
```

### Static in DSA Problem Solving:
- On platforms like LeetCode, methods inside `class Solution` are usually instance methods (`public int maxProfit(int[] prices)`).
- If you declare `static` helper variables (e.g., `static int maxGlobal = 0;`), **be cautious**: static state persists across multiple test cases run by the online judge, leading to "Wrong Answer" on test case 2 even if test case 1 passes.
- **Rule of Thumb**: Avoid static mutable state across test cases. Pass state via method parameters or instantiate clean helper classes.

---

## 4. Recursion & The Call Stack in DSA

Every recursive call allocates a new stack frame containing local parameters and variables:

```java
public static int factorial(int n) {
    if (n <= 1) return 1; // Base case: prevents infinite stack growth
    return n * factorial(n - 1);
}
```

```text
╭─────────────────────────────────────────────────────────────────────────────╮
│                 Call Stack Progression for factorial(3)                     │
├─────────────────────────────────────────────────────────────────────────────┤
│ 4. [factorial(1)] ➔ returns 1 (Base Case reached) ➔ Frame Popped            │
│ 3. [factorial(2)] ➔ waits for factorial(1), returns 2 ➔ Frame Popped        │
│ 2. [factorial(3)] ➔ waits for factorial(2), returns 6 ➔ Frame Popped        │
│ 1. [main()]       ➔ receives 6, continues execution                         │
╰─────────────────────────────────────────────────────────────────────────────╯
```

### Stack Memory & Space Complexity Analysis
- If a recursive tree has a maximum depth of $D$, the algorithm incurs **$O(D)$ Auxiliary Space Complexity** due to the call stack frames.
- In balanced binary trees ($N$ nodes): Depth $D = \log_2 N \implies O(\log N)$ stack space.
- In skewed binary trees or linear recursions: Depth $D = N \implies O(N)$ stack space.
- If $N \ge 10^5$, recursion will usually exceed the default JVM stack size ($\sim 1\text{ MB}$) and throw `StackOverflowError`. Convert to an iterative approach using an explicit `java.util.ArrayDeque` as a stack when dealing with extremely deep recursions.

---

## 5. Self-Check & Quick Review

1. **Q**: Does Java have pointers?
   - *A*: Java has references, which are safe pointers managed by the JVM. You cannot perform pointer arithmetic (like `ptr++` in C/C++), and you cannot access the physical hardware memory address directly.
2. **Q**: What causes `StackOverflowError` vs `OutOfMemoryError`?
   - *A*: `StackOverflowError` occurs when the thread call stack exceeds its memory limit (e.g., infinite recursion). `OutOfMemoryError` occurs when the heap runs out of space to allocate new objects (e.g., creating an array of size $10^9$).
3. **Q**: If method `A` passes an `ArrayList<Integer>` to method `B`, and `B` calls `list.add(42)`, will `list` in method `A` reflect the addition?
   - *A*: **Yes**. The reference address was copied, but both references point to the exact same list object in Heap memory.

---

## 🧭 Continue Learning

| ◀️ Previous Topic | 🧭 Track Hub | Next Topic ▶️ |
| :--- | :---: | ---: |
| [**Page 2: Control Flow & Loops**](02-control-flow-and-loops.md)<br><sub>*Modern Switch, Labeled Jumps & Cache Locality*</sub> | [**Java Fundamentals Index**](README.md)<br><sub>*Core Language & Runtime Mechanics*</sub> | [**Page 4: OOP Principles**](04-oop-principles.md)<br><sub>*4 Pillars, equals/hashCode & Custom DSA Nodes*</sub> |
