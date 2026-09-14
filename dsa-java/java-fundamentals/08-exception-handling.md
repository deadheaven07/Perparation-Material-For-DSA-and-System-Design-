# Page 8: Exception Handling & Defensive Coding

Welcome to Page 8 of the Java Fundamentals series. Exception handling is essential for production-grade software and LLD interviews. Understanding standard runtime exceptions also helps you debug algorithm edge-case crashes instantly.

---

## 1. The `Throwable` Hierarchy

All errors and exceptions in Java descend from the `java.lang.Throwable` class:

```
                            Throwable
                           /         \
                      Error           Exception
               (Unrecoverable)            |
             - StackOverflowError         +-----------------------+
             - OutOfMemoryError           |                       |
                                   Checked Exceptions     Unchecked Exceptions
                                   (Compile-Time Check)   (RuntimeException)
                                   - IOException          - NullPointerException
                                   - SQLException         - ArrayIndexOutOfBounds
                                   - ClassNotFound        - IllegalArgumentException
                                                          - ConcurrentModification
```

### 1. `Error` (System Failures)
Serious runtime environment conditions that an application should not try to catch. Examples: `StackOverflowError`, `OutOfMemoryError`.

### 2. Checked Exceptions
Subclasses of `Exception` that do **not** inherit from `RuntimeException`.
- The compiler forces you to handle them via a `try-catch` block or declare them using the `throws` keyword.
- Example: `IOException` when reading files with `BufferedReader`.

### 3. Unchecked Exceptions (`RuntimeException`)
Exceptions occurring due to programmer logic errors or invalid state. The compiler does **not** force handling.
- Examples: accessing `arr[10]` in an array of size 5 (`ArrayIndexOutOfBoundsException`), or dereferencing a `null` node (`NullPointerException`).

---

## 2. Handling Exceptions: `try`, `catch`, `finally`

```java
try {
    int result = 10 / 0; // Throws ArithmeticException
} catch (ArithmeticException e) {
    System.err.println("Division by zero error: " + e.getMessage());
} catch (Exception e) {
    System.err.println("General exception: " + e.getMessage());
} finally {
    // ALWAYS executes regardless of whether an exception occurred or was caught
    System.out.println("Cleanup executed.");
}
```

> [!NOTE]
> Specific catches **must** precede more generic catches; placing `catch (Exception e)` before `catch (ArithmeticException e)` results in a compile error (`unreachable code`).

---

## 3. Automatic Resource Management: Try-with-Resources

Introduced in Java 7, **Try-with-resources** automatically closes resources (files, sockets, streams) that implement the `java.lang.AutoCloseable` interface, eliminating messy `finally { resource.close(); }` blocks.

```java
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ResourceDemo {
    public static void readFile(String path) {
        // BufferedReader is automatically closed at the end of the block
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println("Failed to read file: " + e.getMessage());
        }
    }
}
```

---

## 4. Top 3 DSA Runtime Exceptions & How to Prevent Them

### 1. `NullPointerException` (NPE)
Happens when dereferencing an uninitialized reference.

```java
// Common DSA trap: Map lookup with autoboxing
Map<Integer, Integer> map = new HashMap<>();
// int count = map.get(5); // 💥 NPE! map.get(5) returns null, unboxing null to int throws NPE!

// ✅ Fix: Use getOrDefault()
int count = map.getOrDefault(5, 0);

// Boundary check in Linked List:
while (curr != null && curr.next != null) {
    curr = curr.next.next;
}
```

### 2. `ArrayIndexOutOfBoundsException`
Triggered by accessing an invalid index ($< 0$ or $\ge \text{length}$).

```java
// Prevention: Double check loop bounds
for (int i = 0; i < arr.length; i++) { // strictly < arr.length
    // ...
}
```

### 3. `ConcurrentModificationException`
Occurs when modifying a collection while iterating over it with an enhanced `for-each` loop.

```java
List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));

// ❌ CRASHES:
for (int val : list) {
    if (val % 2 == 0) list.remove(Integer.valueOf(val)); // 💥 Throws ConcurrentModificationException!
}

// ✅ Fix 1: Use Iterator.remove()
Iterator<Integer> it = list.iterator();
while (it.hasNext()) {
    if (it.next() % 2 == 0) {
        it.remove(); // safe removal
    }
}

// ✅ Fix 2 (Java 8+ Idiom):
list.removeIf(val -> val % 2 == 0);
```

---

## 5. Custom Exceptions (Essential for LLD)

In Low-Level Design (LLD), creating domain-specific exceptions makes code expressive and decoupled:

```java
// Custom Unchecked Business Exception
public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}

// Usage in LLD:
public class BankAccount {
    private double balance;

    public void withdraw(double amount) {
        if (amount > balance) {
            throw new InsufficientFundsException("Cannot withdraw " + amount + ", current balance: " + balance);
        }
        balance -= amount;
    }
}
```

---

## 6. Self-Check & Quick Review

1. **Q**: Does the `finally` block execute if the `try` block contains a `return` statement?
   - *A*: **Yes**. The `finally` block executes *after* the return expression is evaluated but *before* control returns to the caller.
2. **Q**: What happens if `System.exit(0)` is invoked inside a `try` block?
   - *A*: The JVM terminates immediately; the `finally` block will **not** execute.
3. **Q**: How do you safely remove elements matching a predicate from an `ArrayList` in modern Java?
   - *A*: `list.removeIf(predicate);`

---

| ⬅️ Previous | 🏠 Course Index | ➡️ Next |
| :--- | :---: | ---: |
| [Page 7: Generics & Custom Comparators](07-generics-and-comparators.md) | [Java Fundamentals Index](README.md) | [Page 9: Functional Java & Streams](09-lambdas-and-streams.md) |
