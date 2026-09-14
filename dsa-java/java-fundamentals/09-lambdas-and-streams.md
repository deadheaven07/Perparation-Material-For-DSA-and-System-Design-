# Page 9: Functional Java - Lambdas, Streams API & Records

Welcome to Page 9 of the Java Fundamentals series. Modern Java (Java 8 through Java 17 LTS) shifted the language from purely imperative OOP to a hybrid functional paradigm. Lambdas and the Streams API enable concise, expressive data transformations.

---

## 1. Functional Interfaces

A **Functional Interface** is an interface that contains **exactly one abstract method** (SAM — Single Abstract Method). It can contain any number of `default` or `static` methods.

It is marked with the optional `@FunctionalInterface` annotation, which causes the compiler to generate an error if a second abstract method is added.

### The 4 Core Built-In Functional Interfaces (`java.util.function`):

| Interface | Method Signature | Purpose | Example |
| :--- | :--- | :--- | :--- |
| **`Predicate<T>`** | `boolean test(T t)` | Evaluates a condition | `x -> x > 0` |
| **`Function<T, R>`** | `R apply(T t)` | Transforms input of type `T` to type `R` | `s -> s.length()` |
| **`Consumer<T>`** | `void accept(T t)` | Consumes data without returning anything | `x -> System.out.println(x)` |
| **`Supplier<T>`** | `T get()` | Generates/supplies a value with no input | `() -> Math.random()` |

---

## 2. Lambda Expressions & Method References

### Lambda Syntax:
$$\text{(parameters)} \longrightarrow \text{expression / \{ statements \}}$$

```java
// Traditional Anonymous Inner Class
Runnable r1 = new Runnable() {
    @Override
    public void run() {
        System.out.println("Running...");
    }
};

// Modern Lambda Equivalent:
Runnable r2 = () -> System.out.println("Running...");
```

### Method References (`::`)
Shorthand for calling an existing method by name:

```java
// Lambda:
Consumer<String> printer = s -> System.out.println(s);
// Method Reference equivalent:
Consumer<String> printerRef = System.out::println;

// Constructor Reference:
Supplier<List<Integer>> listSupplier = ArrayList::new;
```

> [!NOTE]
> **Variable Capture**: Lambdas can access local variables from their enclosing scope only if those variables are **`final` or effectively final** (never reassigned after initialization).

---

## 3. The Streams API: Pipelines & Operations

A **Stream** is a sequence of elements supporting sequential and parallel aggregate operations. It does **not** store data (it is not a data structure); instead, it conveys elements from a source through a pipeline of computational steps.

```
+------------+      +--------------+      +--------------+      +--------------------+
|   Source   | ---> | Intermediate | ---> | Intermediate | ---> | Terminal Operation |
| Collection |      |   .filter()  |      |    .map()    |      |     .collect()     |
+------------+      +--------------+      +--------------+      +--------------------+
                           (Lazy Operations)                       (Executes Pipeline)
```

### 1. Intermediate Operations (Lazy)
These do not execute until a terminal operation is called:
- `.filter(Predicate<T>)`: Retains elements matching the condition.
- `.map(Function<T, R>)`: Transforms elements.
- `.mapToInt(ToIntFunction<T>)`: Maps to an unboxed primitive `IntStream` (avoiding wrapper overhead).
- `.sorted()` / `.sorted(Comparator<T>)`: Sorts elements.
- `.distinct()`: Removes duplicates using `.equals()`.
- `.limit(n)`: Truncates stream to max $n$ elements.

### 2. Terminal Operations (Eager)
Triggers the execution of the pipeline and produces a result or side-effect:
- `.collect(Collectors.toList())` or `.toList()` (Java 16+): Collects into a list.
- `.count()`: Returns element count.
- `.reduce(...)`: Combines elements into a single aggregate value.
- `.forEach(Consumer<T>)`: Iterates over each item.
- `.anyMatch(Predicate)` / `.allMatch(Predicate)`: Returns boolean condition check.

---

## 4. Top 3 Most Used Stream Idioms in DSA & LLD

### 1. Converting Between Primitive Array and Object List

```java
int[] nums = {1, 2, 3, 4, 5};

// int[] -> List<Integer>
List<Integer> list = Arrays.stream(nums)
                           .boxed()
                           .collect(Collectors.toList());

// List<Integer> -> int[]
int[] arr = list.stream()
                .mapToInt(Integer::intValue)
                .toArray();
```

### 2. Frequency Counting in 1 Line

```java
List<String> words = Arrays.asList("apple", "banana", "apple", "cherry", "banana");

Map<String, Long> freq = words.stream()
    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
// Result: {banana=2, apple=2, cherry=1}
```

### 3. Finding Max / Sum on Custom Objects

```java
List<int[]> intervals = Arrays.asList(new int[]{1, 5}, new int[]{2, 9}, new int[]{4, 6});

// Max end time:
int maxEnd = intervals.stream()
                      .mapToInt(interval -> interval[1])
                      .max()
                      .orElse(0); // returns 9
```

---

## 5. Performance Caution: Streams vs. Classic Loops in DSA

While Streams write elegant and clean code, be mindful in competitive programming and tight DSA loops ($N \ge 10^6$):

| Metric | Classic `for` Loop | Streams API |
| :--- | :--- | :--- |
| **Execution Overhead** | Minimal (Direct CPU registers & array indexing) | Moderate (Pipeline objects, boxing/unboxing) |
| **Speed** | **Fastest** (Ideal for inner $O(N)$ DSA loops) | $2\times\text{ to }5\times$ slower for simple primitive operations |
| **Readability** | Verbose | **High, declarative, maintainable** |
| **Best Used For** | Core algorithmic routines / Contest code | LLD, Business logic, Pipeline data processing |

---

## 6. Modern Java Feature: Records (Java 16 LTS)

When designing graph edges, coordinate points, or tree return nodes in DSA, traditional classes require boilerplate getters, `equals()`, `hashCode()`, and `toString()`.

**Records** eliminate all this boilerplate with a single line:

```java
// Defines an immutable data carrier with automatic constructor, getters, equals(), hashCode(), and toString()!
public record Point(int x, int y) {}

// Usage:
Point p1 = new Point(3, 4);
System.out.println(p1.x()); // 3 (Accessor method has same name as field)
System.out.println(p1);     // "Point[x=3, y=4]"

Point p2 = new Point(3, 4);
System.out.println(p1.equals(p2)); // true! Evaluates field equality automatically.
```

---

## 7. Self-Check & Quick Review

1. **Q**: Can a `@FunctionalInterface` have more than one abstract method?
   - *A*: No. It must have exactly one abstract method.
2. **Q**: Why are stream intermediate operations described as "lazy"?
   - *A*: They do not compute anything until a terminal operation (like `.collect()` or `.count()`) is invoked on the stream pipeline.
3. **Q**: What are the accessor methods for a `record Node(int id, String name)`?
   - *A*: `node.id()` and `node.name()` (Records do not use the `get` prefix).

---

👉 **Next Up: [Page 10: Multithreading & Concurrency Basics for Technical Interviews](file:///Users/deadheaven07/Downloads/Prep_DSA_SystemDesign/dsa-java/java-fundamentals/10-concurrency-and-threads.md)**
