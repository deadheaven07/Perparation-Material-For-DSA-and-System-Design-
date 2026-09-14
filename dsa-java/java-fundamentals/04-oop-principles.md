# Page 4: Object-Oriented Programming (OOP) Principles

Welcome to Page 4 of the Java Fundamentals series. Object-Oriented Programming (OOP) is foundational not just for software architecture and Low-Level Design (LLD), but also for creating custom data structures (like LinkedLists, Trees, and Tries) in DSA interviews.

---

## 1. The 4 Pillars of OOP

```
                           The 4 Pillars of OOP
             +---------------+---------------+---------------+
             |               |               |               |
       Encapsulation    Abstraction     Inheritance    Polymorphism
      (Data Hiding)   (Hiding Details) (Code Reuse)  (Multiple Forms)
```

### 1. Encapsulation (Data Hiding & Protection)
Encapsulation bundles data (fields) and the methods operating on that data into a single unit (class), restricting direct access to prevent corruption.

```java
public class BankAccount {
    // 1. Private fields (cannot be modified directly from outside)
    private double balance;

    public BankAccount(double initialBalance) {
        if (initialBalance >= 0) this.balance = initialBalance;
    }

    // 2. Controlled access via getters & setters
    public double getBalance() {
        return this.balance;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
        }
    }
}
```

### 2. Abstraction (Hiding Complexity)
Abstraction exposes *what* an object does rather than *how* it does it. In Java, this is achieved through **Abstract Classes** and **Interfaces**.

#### Abstract Class vs. Interface

| Feature | Abstract Class (`abstract class`) | Interface (`interface`) |
| :--- | :--- | :--- |
| **Inheritance** | Single inheritance (`extends`) | Multiple inheritance (`implements`) |
| **Fields** | Can have instance, static, and final fields | Only `public static final` constants |
| **Constructors** | Can have constructors | Cannot have constructors |
| **Methods** | Abstract, concrete, final, and static | Abstract, `default` (Java 8+), `static` (Java 8+), `private` (Java 9+) |
| **Use Case** | Is-a relationship sharing state & code | Contract defining behavior/capabilities (e.g., `Comparable`, `Runnable`) |

```java
// Interface defining a contract
public interface Shape {
    double calculateArea(); // abstract method
    
    // Default method (Java 8+)
    default void printInfo() {
        System.out.println("Area: " + calculateArea());
    }
}

public class Circle implements Shape {
    private final double radius;
    public Circle(double radius) { this.radius = radius; }

    @Override
    public double calculateArea() {
        return Math.PI * radius * radius;
    }
}
```

### 3. Inheritance (Code Reuse & Hierarchy)
Inheritance allows a child class to inherit fields and methods from a parent class using the `extends` keyword.

```java
class Animal {
    void eat() { System.out.println("Eating..."); }
}

class Dog extends Animal {
    void bark() { System.out.println("Barking..."); }
}
```

> [!NOTE]
> Java does **not** support multiple inheritance with classes (to avoid the "Diamond Problem"), but supports multiple inheritance with **interfaces**.

### 4. Polymorphism (Multiple Forms)
Allows an entity to take different forms at compile time or runtime.

#### A. Compile-Time (Static) Polymorphism: Method Overloading
Same method name with different parameter signatures (type, count, or order) within the same class.

```java
class MathUtils {
    public static int add(int a, int b) { return a + b; }
    public static double add(double a, double b) { return a + b; }
    public static int add(int a, int b, int c) { return a + b + c; }
}
```

#### B. Runtime (Dynamic) Polymorphism: Method Overriding
Child class provides a specific implementation of a method already defined in its parent class. The method executed is decided at **runtime** based on the actual object in Heap, not the reference type.

```java
// Reference type: List | Object type: ArrayList
List<Integer> list = new ArrayList<>(); // Programming to an interface!

class Parent {
    void show() { System.out.println("Parent"); }
}
class Child extends Parent {
    @Override
    void show() { System.out.println("Child"); }
}

Parent obj = new Child(); // Dynamic Method Dispatch
obj.show(); // Prints "Child" at runtime!
```

---

## 2. Key Keywords: `this`, `super`, `final`

### 1. `this` vs. `super`
- `this`: Refers to the current object instance. Used to distinguish instance fields from method parameters (`this.val = val;`) or invoke overloaded constructors (`this(val, null);`).
- `super`: Refers to the immediate parent class. Used to invoke parent constructors (`super(name);`) or parent methods (`super.show();`).

### 2. The `final` Keyword
- **`final` variable**: Value cannot be reassigned (constant).
- **`final` method**: Cannot be overridden by subclasses.
- **`final` class**: Cannot be extended (e.g., `java.lang.String` and `java.lang.Integer` are `final` for security and caching immutability).

---

## 3. The `equals()` and `hashCode()` Contract

This is one of the most important concepts for DSA and Java interviews. `HashSet` and `HashMap` rely on this contract to identify unique keys:

```
                  Key Insertion into HashMap
                             |
                   Compute hashCode(key)
                             |
                  Find Bucket in Hash Table
                             |
                     Is Bucket Occupied?
                     /                 \
                  (No)                (Yes)
                   |                    |
             Insert Entry        Traverse Bucket LinkedList/Tree
                                        |
                            Does existing.key.equals(newKey)?
                            /                              \
                         (Yes)                            (No)
                           |                                |
                     Update Value                     Append New Entry
```

### The Rules of the Contract:
1. If `a.equals(b) == true`, then `a.hashCode()` **must** equal `b.hashCode()`.
2. If `a.hashCode() == b.hashCode()`, `a.equals(b)` may or may not be true (hash collision).
3. If you override `equals()`, you **must** override `hashCode()`.

### Example Custom Pair Class for DSA Grids / Graphs:

```java
import java.util.Objects;

public class Pair {
    public final int row;
    public final int col;

    public Pair(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair pair = (Pair) o;
        return row == pair.row && col == pair.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col); // Computes combined hash code
    }

    @Override
    public String toString() {
        return "(" + row + ", " + col + ")";
    }
}
```

---

## 4. Inner Classes in DSA: Why Use `static class`?

When defining custom nodes for Linked Lists or Trees, always declare helper classes as **`static class`**:

```java
public class Solution {
    // ✅ RECOMMENDED: Static nested class
    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }
}
```

> [!TIP]
> **Why `static class` instead of non-static `class`?**
> A non-static inner class holds an implicit reference to its enclosing outer class instance (`Solution.this`). This consumes additional memory ($\approx 8\text{ bytes}$ pointer overhead per node) and can prevent the outer object from being garbage-collected, creating memory leaks. Declaring it `static` removes the outer reference!

---

## 5. Self-Check & Quick Review

1. **Q**: Can we override a `static` method?
   - *A*: No. Static methods belong to the class, not instances. If a child class defines a static method with the same signature, it is **method hiding**, not overriding.
2. **Q**: What happens if two distinct objects return the same `hashCode()`?
   - *A*: This is a **hash collision**. The `HashMap` stores both objects in the same bucket (as a LinkedList or balanced Red-Black tree) and disambiguates them using `equals()`.
3. **Q**: Can an `interface` have a constructor?
   - *A*: No. Interfaces cannot be instantiated directly and have no instance state to initialize.

---

👉 **Next Up: [Page 5: Arrays, Strings, String Pool & Mutability](file:///Users/deadheaven07/Downloads/Prep_DSA_SystemDesign/dsa-java/java-fundamentals/05-arrays-and-strings.md)**
