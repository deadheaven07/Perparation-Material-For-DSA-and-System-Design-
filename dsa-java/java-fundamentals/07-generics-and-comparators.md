# Page 7: Generics, Comparable vs. Comparator & Custom Sorting

Welcome to Page 7 of the Java Fundamentals series. Writing custom sorting logic and understanding type-safe generics are required skills for greedy algorithms, interval scheduling, graph priority queues, and sorting custom objects in DSA.

---

## 1. Generics: Purpose & Mechanics

Before Java 5, collections stored raw `Object` instances, requiring explicit typecasting and risking runtime `ClassCastException`. Generics provide **compile-time type safety**.

```java
// Without Generics (Pre-Java 5): Runtime danger
List list = new ArrayList();
list.add("Hello");
Integer num = (Integer) list.get(0); // 💥 Throws ClassCastException at runtime!

// With Generics: Compile-time safety
List<String> safeList = new ArrayList<>();
safeList.add("Hello");
// safeList.add(100); // ❌ Won't compile! Caught at compile time.
```

### Generic Classes & Methods

```java
// Generic Class
public class Pair<K, V> {
    private final K key;
    private final V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() { return key; }
    public V getValue() { return value; }
}

// Generic Method with Bounded Type Parameter
public class ArrayUtils {
    // Only accepts types that implement Comparable
    public static <T extends Comparable<T>> T findMax(T[] arr) {
        T max = arr[0];
        for (T item : arr) {
            if (item.compareTo(max) > 0) {
                max = item;
            }
        }
        return max;
    }
}
```

### Type Erasure (How Generics Work Internally)
Java implements generics using **Type Erasure**.
- During compilation, the Java compiler verifies type safety and then **erases** all generic type parameters, replacing them with their bound (or `Object` if unbounded) and inserting appropriate casts in the bytecode.
- **Why it matters:**
  1. `List<String>` and `List<Integer>` have the **exact same runtime class**: `ArrayList.class`.
  2. You **cannot** do `new T()` or `new T[10]` directly because the concrete type `T` is unknown at runtime.

---

## 2. Wildcards & The PECS Principle

When dealing with polymorphic generic collections, direct subtyping does not hold:
> `ArrayList<Integer>` is **NOT** a subtype of `ArrayList<Number>`!

To write flexible methods, Java provides **wildcards (`?`)**:

| Wildcard Syntax | Description | Example |
| :--- | :--- | :--- |
| `<?>` | Unbounded Wildcard | `List<?> list` (any type) |
| `<? extends T>` | Upper-bounded Wildcard | `List<? extends Number>` (Number or any subclass: Integer, Double) |
| `<? super T>` | Lower-bounded Wildcard | `List<? super Integer>` (Integer or any superclass: Number, Object) |

### The PECS Rule: **P**roducer **E**xtends, **C**onsumer **S**uper
- Use `<? extends T>` if your method only **reads** (produces) data from the collection.
- Use `<? super T>` if your method only **writes** (consumes) data into the collection.

---

## 3. Sorting in Java: Dual-Pivot Quicksort vs. Timsort

Java employs two distinct high-performance sorting algorithms depending on the data type:

1. **`Arrays.sort(int[] a)` (Primitive arrays)**:
   - Uses **Dual-Pivot Quicksort**.
   - Time Complexity: $O(N \log N)$ average, $O(N^2)$ worst case.
   - Space Complexity: $O(\log N)$ stack space.
   - Not stable (relative order of duplicates may change).
2. **`Arrays.sort(T[] a)` and `Collections.sort(List<T>)` (Object arrays / Collections)**:
   - Uses **Timsort** (hybrid of Merge Sort and Insertion Sort).
   - Time Complexity: $O(N \log N)$ worst and average case, $O(N)$ best case (for partially sorted data).
   - Space Complexity: $O(N)$ auxiliary space.
   - **Stable**: Preserves original relative order of equal elements.

---

## 4. `Comparable<T>` vs. `Comparator<T>`

| Feature | `Comparable<T>` | `Comparator<T>` |
| :--- | :--- | :--- |
| **Package** | `java.lang.Comparable` | `java.util.Comparator` |
| **Method** | `int compareTo(T o)` | `int compare(T o1, T o2)` |
| **Location** | Implemented **inside** the target class itself | Implemented as a **separate class / lambda** |
| **Sorting Sequence** | Defines **natural ordering** (only 1 way to sort) | Defines **custom ordering** (multiple ways to sort) |
| **Invocation** | `Collections.sort(list)` | `Collections.sort(list, myComparator)` |

### 1. `Comparable<T>` Example:

```java
public class Interval implements Comparable<Interval> {
    int start, end;

    public Interval(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public int compareTo(Interval other) {
        // Natural order: sort by start time ascending
        return Integer.compare(this.start, other.start);
    }
}
```

### 2. `Comparator<T>` with Modern Lambdas:

```java
List<Interval> intervals = new ArrayList<>();

// Lambda syntax:
Collections.sort(intervals, (a, b) -> Integer.compare(a.start, b.start));

// Or directly using List.sort():
intervals.sort((a, b) -> Integer.compare(a.start, b.start));
```

---

## 5. The Underflow Trap: `(a, b) -> a - b`

> [!CAUTION]
> **NEVER use subtraction `(a, b) -> a - b` for integer comparisons in interviews!**

Consider what happens when comparing $a = -2,000,000,000$ and $b = 1,000,000,000$:
$$a - b = -2,000,000,000 - 1,000,000,000 = -3,000,000,000$$
Because $-3,000,000,000$ exceeds `Integer.MIN_VALUE` ($-2,147,483,648$), it **underflows and wraps around to a positive number (+1,294,967,296)**!
The sort incorrectly concludes that $a > b$, corrupting your sort output or `PriorityQueue`.

### The Safe Pattern:
```java
// ✅ ALWAYS USE Integer.compare() / Double.compare()
(a, b) -> Integer.compare(a.val, b.val)
```

---

## 6. Multi-Criteria Sorting in DSA Problems

A frequent pattern in interval problems (e.g., LeetCode 56, 435) is sorting on multiple fields:
- Primary key: `start` ascending.
- Secondary key (tie-breaker): `end` descending.

### Way 1: Clean Ternary / Conditional Logic

```java
int[][] intervals = {{1, 3}, {1, 5}, {2, 6}, {1, 2}};

Arrays.sort(intervals, (a, b) -> {
    if (a[0] != b[0]) {
        return Integer.compare(a[0], b[0]); // Primary: start ascending
    }
    return Integer.compare(b[1], a[1]);     // Tie-breaker: end descending
});
```

### Way 2: Modern `Comparator` Factory Methods

```java
class Event {
    int start, end;
    // getters
    public int getStart() { return start; }
    public int getEnd() { return end; }
}

List<Event> events = new ArrayList<>();
events.sort(
    Comparator.comparingInt(Event::getStart)
              .thenComparing(Event::getEnd, Comparator.reverseOrder())
);
```

---

## 7. Self-Check & Quick Review

1. **Q**: Why can't you instantiate a generic array like `T[] arr = new T[10];`?
   - *A*: Due to **Type Erasure**, the JVM does not know what `T` is at runtime, and Java arrays need concrete type metadata to enforce runtime type safety.
2. **Q**: Is `Collections.sort()` stable?
   - *A*: **Yes**. It uses Timsort, which guarantees stability (equal keys preserve their relative order).
3. **Q**: How do you create a Max-Heap `PriorityQueue` of integers safely?
   - *A*: `new PriorityQueue<>(Collections.reverseOrder())` or `new PriorityQueue<>((a, b) -> Integer.compare(b, a))`.

---

👉 **Next Up: [Page 8: Exception Handling & Resource Management](file:///Users/deadheaven07/Downloads/Prep_DSA_SystemDesign/dsa-java/java-fundamentals/08-exception-handling.md)**
