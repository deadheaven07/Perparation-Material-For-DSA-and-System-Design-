# Page 3: GoF Design Patterns in Java for System Design

Welcome to Page 3 of the System Design Fundamentals series. Design patterns are battle-tested templates for solving recurring software design problems. In LLD machine coding rounds (e.g., designing a Parking Lot, Splitwise, or an Elevator System), using the right pattern is what sets candidates apart.

---

## 1. Pattern Classification Matrix

```
                          GoF Design Patterns
             +---------------------+---------------------+
             |                     |                     |
        Creational            Structural             Behavioral
      (Object Creation)     (Class/Object Layout)   (Object Interaction)
      - Singleton           - Adapter               - Strategy
      - Factory             - Decorator             - Observer
      - Builder             - Facade                - Chain of Resp.
```

---

## 2. Creational Patterns

### 1. Singleton Pattern (The Multithreading Challenge)
Ensures a class has only **one instance** and provides a global access point to it (e.g., `ConfigurationManager`, DB Connection Pool).

#### The Production-Standard: Double-Checked Locking (DCL)

```java
public class DatabaseConnectionManager {
    // 1. MUST be volatile to prevent CPU instruction reordering!
    private static volatile DatabaseConnectionManager instance;

    // 2. Private constructor prevents instantiation via 'new'
    private DatabaseConnectionManager() {
        System.out.println("Initializing DB Connection Pool...");
    }

    // 3. Double-checked locking
    public static DatabaseConnectionManager getInstance() {
        if (instance == null) { // First Check (No lock overhead after initialization!)
            synchronized (DatabaseConnectionManager.class) {
                if (instance == null) { // Second Check (Ensures only one thread initializes)
                    instance = new DatabaseConnectionManager();
                }
            }
        }
        return instance;
    }
}
```

> [!IMPORTANT]
> **Why `volatile` is mandatory in DCL:**
> The initialization `instance = new DatabaseConnectionManager()` consists of 3 bytecode steps:
> 1. Allocate memory.
> 2. Initialize the constructor.
> 3. Point `instance` to the memory address.
> Without `volatile`, the JVM/CPU may reorder step 3 before step 2. Another thread checking `instance == null` in check #1 sees a non-null reference and accesses an **uninitialized, broken object**!

#### Alternative: Bill Pugh Singleton (Lazy & Thread-Safe via ClassLoader)
```java
public class BillPughSingleton {
    private BillPughSingleton() {}

    // Static nested class is only loaded into memory when getInstance() is called!
    private static class Holder {
        private static final BillPughSingleton INSTANCE = new BillPughSingleton();
    }

    public static BillPughSingleton getInstance() {
        return Holder.INSTANCE;
    }
}
```

---

### 2. Builder Pattern
Solves the "Telescoping Constructor" anti-pattern when an object has many optional attributes.

```java
public class HttpRequest {
    private final String url;
    private final String method;
    private final int timeout;
    private final Map<String, String> headers;

    private HttpRequest(Builder builder) {
        this.url = builder.url;
        this.method = builder.method;
        this.timeout = builder.timeout;
        this.headers = builder.headers;
    }

    public static class Builder {
        private final String url; // mandatory
        private String method = "GET"; // optional default
        private int timeout = 5000;
        private Map<String, String> headers = new HashMap<>();

        public Builder(String url) { this.url = url; }

        public Builder method(String method) { this.method = method; return this; }
        public Builder timeout(int timeout) { this.timeout = timeout; return this; }
        public Builder addHeader(String k, String v) { this.headers.put(k, v); return this; }

        public HttpRequest build() { return new HttpRequest(this); }
    }
}

// Fluent Client Usage:
HttpRequest request = new HttpRequest.Builder("https://api.example.com/data")
    .method("POST")
    .timeout(3000)
    .addHeader("Authorization", "Bearer token123")
    .build();
```

---

## 3. Structural Patterns

### 1. Decorator Pattern (Dynamic Behavior Extension)
Attaches additional responsibilities to an object dynamically. Real-world Java example: Java I/O streams!
```java
InputStream in = new BufferedInputStream(new FileInputStream("data.txt"));
```

#### LLD Example: Coffee Customizer
```java
// Component Interface
public interface Coffee {
    double getCost();
    String getDescription();
}

// Concrete Component
public class SimpleCoffee implements Coffee {
    public double getCost() { return 2.0; }
    public String getDescription() { return "Simple Coffee"; }
}

// Decorator Base
public abstract class CoffeeDecorator implements Coffee {
    protected final Coffee decoratedCoffee;
    public CoffeeDecorator(Coffee coffee) { this.decoratedCoffee = coffee; }
    public double getCost() { return decoratedCoffee.getCost(); }
    public String getDescription() { return decoratedCoffee.getDescription(); }
}

// Concrete Decorator
public class MilkDecorator extends CoffeeDecorator {
    public MilkDecorator(Coffee coffee) { super(coffee); }
    @Override
    public double getCost() { return super.getCost() + 0.5; }
    @Override
    public String getDescription() { return super.getDescription() + ", Milk"; }
}

// Usage:
Coffee myCoffee = new MilkDecorator(new SimpleCoffee());
System.out.println(myCoffee.getDescription() + " -> $" + myCoffee.getCost()); // Simple Coffee, Milk -> $2.5
```

---

## 4. Behavioral Patterns

### 1. Strategy Pattern (Interchangeable Algorithms)
Enables swapping algorithms at runtime without changing the client code.

```java
// Strategy Interface
public interface RouteStrategy {
    void calculateRoute(String origin, String destination);
}

public class DrivingStrategy implements RouteStrategy {
    public void calculateRoute(String o, String d) { System.out.println("Fastest highway route"); }
}
public class WalkingStrategy implements RouteStrategy {
    public void calculateRoute(String o, String d) { System.out.println("Scenic pedestrian path"); }
}

// Context
public class Navigator {
    private RouteStrategy strategy;
    public Navigator(RouteStrategy strategy) { this.strategy = strategy; }
    public void setStrategy(RouteStrategy strategy) { this.strategy = strategy; }
    public void navigate(String o, String d) { strategy.calculateRoute(o, d); }
}
```

### 2. Observer Pattern (Event Publishing / Pub-Sub)
Defines a one-to-many dependency between objects so that when one object changes state, all its dependents are notified automatically.

```java
public interface Observer {
    void update(double stockPrice);
}

public class StockMarket {
    private final List<Observer> observers = new ArrayList<>();
    private double price;

    public void register(Observer o) { observers.add(o); }
    public void setPrice(double newPrice) {
        this.price = newPrice;
        notifyAllObservers();
    }
    private void notifyAllObservers() {
        for (Observer o : observers) o.update(price);
    }
}
```

---

## 5. Self-Check & Quick Review

1. **Q**: Which pattern should you use if you need to create complex objects with numerous optional parameters?
   - *A*: **Builder Pattern**.
2. **Q**: What is the key difference between **Adapter** and **Decorator**?
   - *A*: **Adapter** converts an existing interface to match another expected interface (changes the interface). **Decorator** enhances the object's behavior without changing its existing interface.
3. **Q**: How does the Strategy pattern help adhere to the Open/Closed Principle?
   - *A*: New algorithms/strategies can be introduced simply by implementing the strategy interface without modifying the context class.

---

| ⬅️ Previous | 🏠 Course Index | ➡️ Next |
| :--- | :---: | ---: |
| [Page 2: SOLID Principles & LLD](02-solid-principles-in-java.md) | [System Design Index](README.md) | [Page 4: Scaling & Thread Pools](04-scaling-and-thread-pools.md) |
