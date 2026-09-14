# Page 5: Arrays, Strings, String Pool & Mutability

Welcome to Page 5 of the Java Fundamentals series. Arrays and Strings account for over 50% of entry-level and mid-level coding interview questions. Understanding their memory representation, immutability, and performance traps is vital.

---

## 1. Arrays in Java: Memory Representation

In Java, arrays are **first-class objects** stored in the Heap. The variable name in the Stack holds a reference to the array object.

```
Stack Memory                              Heap Memory
+-----------------------+                 +--------------------------------------+
| int[] nums ---------> | --------------> | Array Object:                        |
|                       |                 | [Header: length=4]                   |
|                       |                 | Index:  [ 0 |  1 |  2 |  3 ]         |
|                       |                 | Value:  [10 | 20 | 30 | 40 ]         |
+-----------------------+                 +--------------------------------------+
```

### 1D Array Initialization
```java
// Option 1: Declaration with size (initialized to default values: 0, false, null)
int[] arr = new int[5]; // [0, 0, 0, 0, 0]

// Option 2: Declaration with literal values
int[] primes = {2, 3, 5, 7, 11};

// Note: length is a final property, NOT a method
int size = arr.length; // ✅ arr.length (no parentheses!)
```

### 2D Arrays: "Arrays of Arrays"
Java does not have true contiguous multi-dimensional arrays. A 2D array is an **array of references**, where each element points to another 1D array in the Heap.

```java
// Standard rectangular matrix: 3 rows, 4 columns
int[][] matrix = new int[3][4];

// Jagged Array (rows with different lengths):
int[][] jagged = new int[3][];
jagged[0] = new int[2]; // row 0 has length 2
jagged[1] = new int[4]; // row 1 has length 4
jagged[2] = new int[1]; // row 2 has length 1
```

### Essential `java.util.Arrays` Utility Methods

```java
import java.util.Arrays;

int[] nums = {5, 2, 8, 1, 9};

// 1. Sorting: O(N log N)
Arrays.sort(nums); // [1, 2, 5, 8, 9] (Dual-Pivot Quicksort for primitives)

// 2. Binary Search: Array MUST be sorted first!
int idx = Arrays.binarySearch(nums, 5); // returns index 2

// 3. Fill / Reset:
Arrays.fill(nums, -1); // fills all elements with -1

// 4. Copying:
int[] copy = Arrays.copyOf(nums, nums.length);
int[] subArray = Arrays.copyOfRange(nums, 1, 4); // [fromIndex, toIndex)

// 5. Fast System Arraycopy (Native C-level speed):
// System.arraycopy(src, srcPos, dest, destPos, length);
System.arraycopy(nums, 0, copy, 0, nums.length);

// 6. Pretty Printing:
System.out.println(Arrays.toString(nums));        // for 1D array
System.out.println(Arrays.deepToString(matrix));   // for 2D array
```

---

## 2. Strings in Java & The String Constant Pool (SCP)

Strings in Java are objects of class `java.lang.String`.

> [!IMPORTANT]
> **Strings are Immutable.**
> Once a `String` object is created in memory, its content **can never be changed**. Any modification method (`concat()`, `replace()`, `toLowerCase()`) returns a **brand-new** String object.

### Why Are Strings Immutable?
1. **Security**: Strings store sensitive data (passwords, URLs, DB connection strings). Immutability prevents tampering.
2. **Thread Safety**: Multiple threads can share immutable strings without synchronization.
3. **String Constant Pool (SCP) Caching**: Allows JVM to reuse identical string literals, saving significant heap memory.
4. **HashCode Caching**: The hashcode of a String is computed once on demand and cached. This makes Strings optimal keys for `HashMap`.

### `==` vs. `.equals()`: The String Pool Trap

```
Stack                                 Heap Memory
+---------------+                     +---------------------------------------+
| s1 ---------> | ------------------> | String Constant Pool (SCP)            |
|               |                     |    "hello" <---+                      |
| s2 ---------> | --------------------+                |                      |
|               |                                      |                      |
| s3 ---------> | -------> [ New Object "hello" ] -----(intern reference)     |
+---------------+          (Outside Pool)                                     |
                                      +---------------------------------------+
```

```java
String s1 = "hello";              // Placed in String Constant Pool
String s2 = "hello";              // Reuses existing "hello" in Pool
String s3 = new String("hello");  // Forces creation of a NEW object in normal Heap

System.out.println(s1 == s2);      // true  (Same memory reference in Pool)
System.out.println(s1 == s3);      // false (Different memory addresses!)
System.out.println(s1.equals(s3)); // true  (Compares character values!)
```

> [!WARNING]
> In DSA, **NEVER** compare strings with `==`. Always use `s1.equals(s2)` or `s1.compareTo(s2)`.

---

## 3. Essential `String` Methods for DSA

| Method | Description | Time Complexity |
| :--- | :--- | :--- |
| `s.length()` | Returns number of characters | $O(1)$ |
| `s.charAt(i)` | Returns character at index `i` ($0$-indexed) | $O(1)$ |
| `s.substring(start, end)` | Returns substring from `start` to `end - 1` | $O(\text{end} - \text{start})$ |
| `s.toCharArray()` | Converts string to mutable `char[]` | $O(N)$ |
| `s.indexOf(ch)` / `s.indexOf(str)` | Returns first occurrence index or `-1` | $O(N \times M)$ |
| `s.contains(seq)` | Checks if substring exists | $O(N \times M)$ |
| `s.startsWith(prefix)` / `s.endsWith(suffix)` | Checks prefix / suffix match | $O(K)$ |
| `s.trim()` / `s.strip()` | Removes leading and trailing whitespace | $O(N)$ |
| `s.split(regex)` | Splits string by regular expression | $O(N)$ |
| `s1.compareTo(s2)` | Lexicographical comparison ($<0, 0, >0$) | $O(\min(N, M))$ |

---

## 4. Performance Trap: `String` Concatenation vs. `StringBuilder`

### The $O(N^2)$ Blunder:

```java
// ❌ HORRIBLE PERFORMANCE: O(N^2)
String s = "";
for (int i = 0; i < n; i++) {
    s += i; // Each concatenation creates a new String, copying all previous chars!
}
```

If $N = 100,000$, the above code allocates gigabytes of intermediate memory and takes several seconds, causing **Time Limit Exceeded (TLE)**.

### The Fix: `StringBuilder`

`StringBuilder` represents a **mutable sequence of characters** backed by an auto-resizing array. Appending characters is amortized **$O(1)$**.

```java
// ✅ OPTIMAL: O(N)
StringBuilder sb = new StringBuilder();
for (int i = 0; i < n; i++) {
    sb.append(i);
}
String result = sb.toString();
```

### `StringBuilder` vs. `StringBuffer`

| Feature | `StringBuilder` | `StringBuffer` |
| :--- | :--- | :--- |
| **Thread Safety** | No (Not Synchronized) | Yes (All methods are `synchronized`) |
| **Speed** | **Faster** (No locking overhead) | Slower |
| **Recommended Usage** | **DSA / Single-threaded code** | Multi-threaded legacy systems |

### Common `StringBuilder` Operations:

```java
StringBuilder sb = new StringBuilder("leetcode");

sb.append("123");       // "leetcode123"
sb.reverse();           // Reverses characters in-place: "321edocteel"
sb.deleteCharAt(0);     // Deletes char at index 0 (O(N) shift)
sb.insert(2, 'X');      // Inserts char at index 2 (O(N) shift)
sb.setLength(0);        // Clears the builder efficiently for reuse!
```

---

## 5. Self-Check & Quick Review

1. **Q**: What is the output of `System.out.println("abc" == new String("abc").intern());`?
   - *A*: `true`. Calling `.intern()` retrieves the canonical representation from the String Constant Pool, which matches `"abc"`.
2. **Q**: How do you reverse a string in Java?
   - *A*: `new StringBuilder(s).reverse().toString();`
3. **Q**: What is the default value of elements in `boolean[] flags = new boolean[10];`?
   - *A*: `false`.

---

| ⬅️ Previous | 🏠 Course Index | ➡️ Next |
| :--- | :---: | ---: |
| [Page 4: OOP Principles](04-oop-principles.md) | [Java Fundamentals Index](README.md) | [Page 6: Collections Framework](06-collections-framework.md) |
