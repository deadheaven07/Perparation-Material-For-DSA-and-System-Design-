# Page 1: Java Architecture, Syntax, Data Types & Fast I/O

Welcome to Page 1 of the Java Fundamentals series. This page establishes the core foundation of Java execution, program structure, data representation, and high-performance input/output techniques essential for solving DSA problems efficiently.

---

## 1. How Java Executes: The JVM Architecture

Java is known for **WORA (Write Once, Run Anywhere)**. This platform independence is achieved by compiling source code into an intermediate format called **Bytecode**, rather than native machine code.

```
       [Source Code]              [Bytecode]              [Machine Code]
        MyCode.java  ---javac---> MyCode.class ---JVM---> 0101010101...
                        (Compiler)            (Interpreter + JIT)
```

### The Java Ecosystem Triangle: JDK vs. JRE vs. JVM

```
+-----------------------------------------------------------+
| JDK (Java Development Kit)                                |
|   Tools: javac, jdb, jar, javadoc                         |
|  +-----------------------------------------------------+  |
|  | JRE (Java Runtime Environment)                      |  |
|  |   Core Libraries (java.lang, java.util, etc.)       |  |
|  |  +-----------------------------------------------+  |  |
|  |  | JVM (Java Virtual Machine)                    |  |  |
|  |  |   - ClassLoader Subsystem                     |  |  |
|  |  |   - Execution Engine (JIT + Interpreter + GC) |  |  |
|  |  |   - Runtime Data Areas (Heap, Stack, etc.)    |  |  |
|  |  +-----------------------------------------------+  |  |
|  +-----------------------------------------------------+  |
+-----------------------------------------------------------+
```

1. **JVM (Java Virtual Machine)**: The abstract computing machine that executes bytecode. It contains:
   - **ClassLoader**: Loads `.class` files into memory.
   - **Execution Engine**:
     - **Interpreter**: Reads bytecode instruction-by-instruction (fast startup).
     - **JIT (Just-In-Time) Compiler**: Compiles frequently executed code ("hot spots") into native machine code for maximum runtime performance.
     - **Garbage Collector (GC)**: Automatically reclaims unused heap memory.
2. **JRE (Java Runtime Environment)**: JVM + Core Standard Libraries (`rt.jar` / modules). Needed only to *run* compiled Java programs.
3. **JDK (Java Development Kit)**: JRE + Development Tools (`javac` compiler, debuggers, profiling tools). Needed to *write* and *compile* Java applications.

---

## 2. Anatomy of a Java Program

Here is the minimal working Java program:

```java
public class Solution {
    public static void main(String[] args) {
        System.out.println("Hello, DSA!");
    }
}
```

### Dissecting `public static void main(String[] args)`:

| Keyword / Identifier | Purpose | Why It Exists |
| :--- | :--- | :--- |
| `public` | Access modifier | Allows the JVM to invoke the entry point method from outside the class package. |
| `static` | Class-level method | The JVM can invoke `main()` without instantiating an object of `Solution`. This avoids memory allocation before execution begins. |
| `void` | Return type | The entry point returns nothing to the caller. When `main()` ends, the program terminates. |
| `main` | Identifier | Predefined entry point name searched by the JVM launcher. |
| `String[] args` | Parameter | Array of command-line string arguments passed to the program upon launch. |

> [!NOTE]
> In Java, the name of the `public class` must match the file name exactly (e.g., `public class Solution` requires `Solution.java`). A file can contain only **one** top-level `public` class.

---

## 3. Data Types: Primitives vs. Reference Types

Java divides data types into two distinct categories:

```
                          Data Types
                         /          \
            Primitive Types        Reference Types
            (8 built-in types)     (Objects, Arrays, Strings, Classes)
            - Stored directly in   - Stored in Heap
              Stack memory         - Reference/pointer stored in Stack
```

### The 8 Primitive Types

| Type | Category | Size | Range | Default Value | Literal Example |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `byte` | Integer | 1 byte (8 bits) | $-128$ to $127$ ($-2^7$ to $2^7-1$) | `0` | `(byte) 42` |
| `short` | Integer | 2 bytes (16 bits) | $-32,768$ to $32,767$ ($-2^{15}$ to $2^{15}-1$) | `0` | `(short) 1000` |
| `int` | Integer | 4 bytes (32 bits) | $-2.14 \times 10^9$ to $2.14 \times 10^9$ ($-2^{31}$ to $2^{31}-1$) | `0` | `100_000` |
| `long` | Integer | 8 bytes (64 bits) | $\approx -9.22 \times 10^{18}$ to $9.22 \times 10^{18}$ ($-2^{63}$ to $2^{63}-1$) | `0L` | `10000000000L` |
| `float` | Floating Point | 4 bytes (32 bits) | Single-precision IEEE 754 ($\approx 6\text{–}7$ decimal digits) | `0.0f` | `3.1415f` |
| `double`| Floating Point | 8 bytes (64 bits) | Double-precision IEEE 754 ($\approx 15\text{–}16$ decimal digits) | `0.0d` | `3.141592653589793` |
| `char` | Unicode Character | 2 bytes (16 bits) | `0` to `65,535` (`\u0000` to `\uffff`) | `'\u0000'` | `'A'`, `'\n'`, `97` |
| `boolean`| Logical | JVM dependent ($\approx 1$ byte) | `true` or `false` | `false` | `true`, `false` |

> [!WARNING]
> **DSA Interview Gotcha: Integer Overflow**
> In Binary Search, writing `int mid = (left + right) / 2;` can cause an integer overflow when `left + right > Integer.MAX_VALUE` ($2 \times 10^9$), turning `mid` into a negative number and triggering `ArrayIndexOutOfBoundsException`.
> **Always write:**
> ```java
> int mid = left + (right - left) / 2;
> // or using unsigned bit shift:
> int mid = (left + right) >>> 1;
> ```

---

## 4. Type Casting & Conversions

### 1. Widening (Implicit / Automatic)
Converting a smaller data type to a larger data type. No data loss occurs.
$$\text{byte} \longrightarrow \text{short} \longrightarrow \text{int} \longrightarrow \text{long} \longrightarrow \text{float} \longrightarrow \text{double}$$

```java
int a = 100;
long b = a;       // Automatic widening: int to long
double c = b;     // Automatic widening: long to double
```

### 2. Narrowing (Explicit / Manual)
Converting a larger data type to a smaller data type. Requires explicit parentheses casting and can lead to truncation or precision loss.

```java
double pi = 3.14159;
int truncatedPi = (int) pi; // Result: 3 (decimal truncated)

long bigNum = 130L;
byte smallByte = (byte) bigNum; // Result: -126 (overflow wraps around!)
```

### 3. Essential Character Arithmetic for DSA
Characters in Java map to ASCII/Unicode numbers. You will use this daily for frequency counters and hash maps:

```java
char ch = 'c';
int index = ch - 'a'; // 'c' (99) - 'a' (97) = 2

// Frequency array for lowercase English letters:
int[] freq = new int[26];
freq[ch - 'a']++; // increments count for 'c'
```

---

## 5. Operators & Evaluation

### Arithmetic & Relational Operators
- Standard: `+`, `-`, `*`, `/`, `%`
- Modulo with negative numbers in Java:
  ```java
  System.out.println(-7 % 3);  // Outputs: -1 (retains sign of dividend)
  // To get mathematical positive modulo:
  int positiveMod = ((n % k) + k) % k;
  ```

### Short-Circuit Evaluation (`&&`, `||`)
- In `A && B`, if `A` evaluates to `false`, `B` is **never executed**.
- In `A || B`, if `A` evaluates to `true`, `B` is **never executed**.

```java
// Safe null & boundary check pattern:
if (head != null && head.val == target) {
    // Will not throw NullPointerException because head.val is never evaluated if head == null
}
```

### Bitwise Operators (Crucial for Bitmasking & O(1) Math)
- `&` (Bitwise AND)
- `|` (Bitwise OR)
- `^` (Bitwise XOR: $x \text{ \^{} } x = 0$, $x \text{ \^{} } 0 = x$)
- `~` (Bitwise NOT / Bit inversion)
- `<<` (Signed left shift: $x \ll k = x \times 2^k$)
- `>>` (Signed right shift: preserves sign bit)
- `>>>` (Unsigned right shift: fills left with `0`)

---

## 6. High-Performance Input/Output (Fast I/O)

In competitive programming and platforms with strict execution time limits ($1.0\text{ s}$), `Scanner` can easily cause **Time Limit Exceeded (TLE)** when reading inputs $\ge 10^5$.

### Scanner vs. BufferedReader Comparison

| Metric | `java.util.Scanner` | `java.io.BufferedReader` |
| :--- | :--- | :--- |
| **Buffer Size** | $1 \text{ KB}$ | $8 \text{ KB}$ (configurable) |
| **Parsing Overhead** | High (uses RegEx internally) | Minimal (reads raw characters/lines) |
| **Speed** | Slow for large input | **5x to 10x faster** |
| **Thread Safety** | Not synchronized | Synchronized |

### Reusable Fast I/O Template for DSA

Save this boilerplate template for problems requiring high-speed input reading:

```java
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.StringTokenizer;

public class FastIOExample {
    // Fast Scanner class using BufferedReader and StringTokenizer
    static class FastReader {
        BufferedReader br;
        StringTokenizer st;

        public FastReader() {
            br = new BufferedReader(new InputStreamReader(System.in));
        }

        String next() {
            while (st == null || !st.hasMoreTokens()) {
                try {
                    String line = br.readLine();
                    if (line == null) return null;
                    st = new StringTokenizer(line);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return st.nextToken();
        }

        int nextInt() {
            return Integer.parseInt(next());
        }

        long nextLong() {
            return Long.parseLong(next());
        }

        double nextDouble() {
            return Double.parseDouble(next());
        }

        String nextLine() {
            String str = "";
            try {
                str = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return str;
        }
    }

    public static void main(String[] args) {
        FastReader in = new FastReader();
        PrintWriter out = new PrintWriter(System.out);

        // Example: Read N integers and print their sum
        // String token = in.next();
        // int n = in.nextInt();
        // out.println("Answer: " + result);

        out.flush(); // IMPORTANT: Always flush PrintWriter at the end!
    }
}
```

---

## 7. Self-Check & Quick Review

Test your understanding before proceeding to Page 2:
1. **Q**: What happens if `int a = Integer.MAX_VALUE; a++;` executes?
   - *A*: It wraps around to `Integer.MIN_VALUE` ($-2,147,483,648$).
2. **Q**: Why is `String[] args` in `main()` an array of strings?
   - *A*: Any command-line input is originally read as raw text; string parsing allows conversion to any needed type (`Integer.parseInt()`, etc.).
3. **Q**: Why do we use `PrintWriter` along with `BufferedReader`?
   - *A*: `System.out.println()` does autoflushing on each call, which incurs disk/terminal I/O overhead. `PrintWriter` buffers output in memory and flushes in batches.

---

| 🏠 Course Index | ➡️ Next |
| :---: | ---: |
| [Java Fundamentals Index](README.md) | [Page 2: Control Flow & Loops](02-control-flow-and-loops.md) |
