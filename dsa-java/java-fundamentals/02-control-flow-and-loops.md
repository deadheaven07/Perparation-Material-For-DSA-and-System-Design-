# Page 2: Control Flow, Modern Switch Expressions & Loops

Welcome to Page 2 of the Java Fundamentals series. This page covers the decision-making and looping structures in Java, including modern switch expressions and labeled jumps that make multi-dimensional search clean and bug-free.

---

## 1. Decision Making: Conditional Statements

### 1. `if - else if - else` Chain
Conditionals evaluate a `boolean` expression. Unlike languages like C/C++, Java **does not** accept integers (like `0` or `1`) as truthy/falsy values.

```java
int score = 85;

if (score >= 90) {
    System.out.println("Grade A");
} else if (score >= 80) {
    System.out.println("Grade B");
} else {
    System.out.println("Grade C");
}
```

### 2. Ternary Operator (`? :`)
A compact replacement for simple `if-else` assignments:
$$\text{variable} = (\text{condition}) \ ? \ \text{value\_if\_true} \ : \ \text{value\_if\_false};$$

```java
int a = 15, b = 25;
int max = (a > b) ? a : b;

// Useful in DSA for clamping or boundary checks:
int validIndex = (index >= 0 && index < n) ? index : -1;
```

---

## 2. Switch Statements: Classic vs. Modern (Java 14+)

Switch works with: `byte`, `short`, `char`, `int`, `String`, and `enum` types. (Note: `long`, `float`, and `double` are **not** permitted in switch statements).

### Classic Switch (With Fall-Through)
Requires explicit `break` statements; otherwise, execution falls through to the next case.

```java
int day = 3;
switch (day) {
    case 1:
        System.out.println("Monday");
        break;
    case 2:
        System.out.println("Tuesday");
        break;
    case 3:
        System.out.println("Wednesday"); // Prints Wednesday
        break;
    default:
        System.out.println("Weekend/Other");
}
```

### Modern Switch Expressions (Java 14+ / Standard in Java 17 LTS)
Modern Java supports arrow labels (`->`), eliminates accidental fall-through, and allows `switch` to return a value directly.

```java
// 1. Arrow syntax without return value (no break needed)
int code = 200;
switch (code) {
    case 200 -> System.out.println("OK");
    case 404 -> System.out.println("Not Found");
    case 500 -> System.out.println("Internal Server Error");
    default  -> System.out.println("Unknown Status");
}

// 2. Switch as an expression returning a value
String dayType = switch (day) {
    case 1, 2, 3, 4, 5 -> "Weekday";
    case 6, 7          -> "Weekend";
    default            -> {
        // Multi-line block uses `yield` to return the value
        System.err.println("Invalid day: " + day);
        yield "Invalid";
    }
};
```

---

## 3. Looping Constructs

### 1. The Standard `for` Loop
Best when the number of iterations is known upfront.

```java
// Syntax: for (initialization; termination_condition; update)
for (int i = 0; i < n; i++) {
    // executes n times: i = 0, 1, ..., n-1
}

// Multi-variable loops (useful for two pointers):
for (int l = 0, r = n - 1; l < r; l++, r--) {
    // swap elements at l and r
}
```

### 2. Enhanced `for-each` Loop
Used to iterate over arrays and any collection implementing `java.lang.Iterable`.

```java
int[] nums = {10, 20, 30, 40};
for (int num : nums) {
    System.out.print(num + " ");
}
```

> [!CAUTION]
> **Limitations of `for-each` in DSA:**
> 1. **Read-only index**: You cannot access or modify the current index `i`.
> 2. **Cannot modify array elements**: Assigning `num = 0` inside the loop only modifies the local copy `num`, **not** the underlying array slot `nums[i]`.
> 3. **Forward-only**: Cannot iterate backwards or step by $+2$.

### 3. `while` Loop
Pre-test loop: evaluates the condition before entering the body. Ideal when the number of iterations depends on a dynamic condition.

```java
// Common pattern: Binary Search or Two-Pointer
int left = 0, right = n - 1;
while (left <= right) {
    int mid = left + (right - left) / 2;
    if (nums[mid] == target) return mid;
    else if (nums[mid] < target) left = mid + 1;
    else right = mid - 1;
}
```

### 4. `do-while` Loop
Post-test loop: executes the body **at least once** before checking the condition.

```java
int val = 0;
do {
    // Always runs at least once
    val++;
} while (val < 5);
```

---

## 4. Branching & Jump Statements: Labeled Jumps

In Java, `break` and `continue` control loop execution:
- `break`: Exits the innermost loop immediately.
- `continue`: Skips the rest of the current iteration and jumps to the update/condition check.

### The Power of Labeled `break` and `continue`
When working with nested loops (like 2D grids, graphs, or backtracking), exiting an outer loop from deep within an inner loop usually requires messy boolean flags. Java provides **labels** to cleanly jump to any outer loop:

```java
public class MatrixSearch {
    public static boolean searchMatrix(int[][] grid, int target) {
        // Label the outer loop
        searchOuter:
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {
                if (grid[r][c] == target) {
                    System.out.println("Found target at (" + r + ", " + c + ")");
                    break searchOuter; // Jumps completely out of both loops!
                }
            }
        }
        return false;
    }
}
```

Similarly, `continue searchOuter;` skips directly to the next iteration of the `r` loop, immediately bypassing remaining columns.

---

## 5. DSA Performance Note: Cache Locality & 2D Traversal

When iterating over a 2D array (`int[][] matrix`), the traversal order significantly impacts performance due to CPU cache lines:

```
Row-Major Order (Fast, CPU Cache Friendly):
[ (0,0) -> (0,1) -> (0,2) ] -> [ (1,0) -> (1,1) -> (1,2) ]

Column-Major Order (Slow, Frequent Cache Misses):
(0,0) -> (1,0) -> (2,0) -> (0,1) -> (1,1) -> ...
```

```java
int rows = matrix.length;
int cols = matrix[0].length;

// ✅ FAST: Row-major traversal (sequential memory access)
for (int r = 0; r < rows; r++) {
    for (int c = 0; c < cols; c++) {
        sum += matrix[r][c];
    }
}

// ❌ SLOW: Column-major traversal (jumping across array references in heap)
for (int c = 0; c < cols; c++) {
    for (int r = 0; r < rows; r++) {
        sum += matrix[r][c];
    }
}
```

---

## 6. Self-Check & Quick Review

1. **Q**: Can we use `float` or `double` in a `switch` statement?
   - *A*: No. Floating-point numbers suffer from precision issues and cannot be cleanly hashed or compared for exact equality in switch bytecode tables.
2. **Q**: What is the difference between `break` and `labeled break`?
   - *A*: Plain `break` only exits the immediately enclosing loop. A labeled `break LabelName;` can exit any specified outer loop level.
3. **Q**: Why does `for (int x : arr) { x = 0; }` not reset the elements of `arr`?
   - *A*: In an enhanced for loop, `x` is a local copy of each element. Reassigning `x` only updates the local variable, not the original slot in `arr`.

---

👉 **Next Up: [Page 3: Memory Layout, Stack vs. Heap & Pass-by-Value](file:///Users/deadheaven07/Downloads/Prep_DSA_SystemDesign/dsa-java/java-fundamentals/03-memory-model-and-methods.md)**
