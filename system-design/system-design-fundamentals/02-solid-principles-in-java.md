# Page 2: SOLID Principles & Clean Low-Level Design (LLD) in Java

Welcome to Page 2 of the System Design Fundamentals series. In technical interviews, Low-Level Design (LLD) evaluates your ability to write modular, extensible, and maintainable object-oriented code. The **SOLID** principles are the universal rubric used by interviewers.

---

## 1. The SOLID Overview

| Acronym | Principle | One-Line Summary |
| :---: | :--- | :--- |
| **S** | **Single Responsibility Principle (SRP)** | A class should have one, and only one, reason to change. |
| **O** | **Open/Closed Principle (OCP)** | Open for extension, but closed for modification. |
| **L** | **Liskov Substitution Principle (LSP)** | Subclasses must be substitutable for their base classes without breaking behavior. |
| **I** | **Interface Segregation Principle (ISP)** | Clients should not be forced to depend on methods they do not use. |
| **D** | **Dependency Inversion Principle (DIP)** | Depend on abstractions (interfaces), not on concrete implementations. |

---

## 2. Deep Dive with Java Code

### 1. Single Responsibility Principle (SRP)
❌ **Violation (The "God Object")**:
```java
public class Invoice {
    private double amount;
    public Invoice(double amount) { this.amount = amount; }

    public void calculateTotal() { /* business logic */ }
    public void saveToDatabase() { /* SQL JDBC code */ }
    public void sendEmailNotification() { /* SMTP Mail code */ }
}
```
*Why this fails*: Changes to the database schema, email provider, or tax calculation all force changes to the same `Invoice` class.

✅ **Refactored (Adhering to SRP)**:
```java
// 1. Business Logic
public class Invoice {
    private final double amount;
    public Invoice(double amount) { this.amount = amount; }
    public double calculateTotal() { return amount * 1.18; }
}

// 2. Persistence Responsibility
public class InvoiceRepository {
    public void save(Invoice invoice) { /* saves to DB */ }
}

// 3. Notification Responsibility
public class NotificationService {
    public void sendInvoiceEmail(Invoice invoice, String recipient) { /* sends email */ }
}
```

---

### 2. Open/Closed Principle (OCP)
❌ **Violation (Rigid `if/else` or `switch`)**:
```java
public class PaymentService {
    public void processPayment(String type, double amount) {
        if (type.equals("CREDIT_CARD")) {
            // charge credit card
        } else if (type.equals("PAYPAL")) {
            // charge paypal
        } else if (type.equals("CRYPTO")) {
            // charge crypto
        }
        // Adding a new payment method requires modifying this tested method!
    }
}
```

✅ **Refactored (Polymorphic Extensibility)**:
```java
// 1. Abstraction (Closed for modification)
public interface PaymentMethod {
    void pay(double amount);
}

// 2. Concrete implementations (Open for extension)
public class CreditCardPayment implements PaymentMethod {
    @Override
    public void pay(double amount) { /* charge card */ }
}

public class UpiPayment implements PaymentMethod {
    @Override
    public void pay(double amount) { /* charge UPI */ }
}

// 3. Client depends on interface
public class PaymentService {
    public void processPayment(PaymentMethod method, double amount) {
        method.pay(amount); // Adding ApplePay creates a new class without touching PaymentService!
    }
}
```

---

### 3. Liskov Substitution Principle (LSP)
Subclasses must adhere to the contract defined by the parent class.

❌ **Violation (The Classic Square-Rectangle Problem)**:
```java
class Rectangle {
    protected int width, height;
    public void setWidth(int w) { this.width = w; }
    public void setHeight(int h) { this.height = h; }
    public int getArea() { return width * height; }
}

class Square extends Rectangle {
    @Override
    public void setWidth(int w) { this.width = this.height = w; }
    @Override
    public void setHeight(int h) { this.width = this.height = h; }
}

// Client test fails:
void testArea(Rectangle r) {
    r.setWidth(5);
    r.setHeight(4);
    assert r.getArea() == 20; // 💥 FAILS for Square because getArea() returns 16!
}
```

✅ **Refactored**: Square and Rectangle are both `Shape` instances, but neither inherits from the other:
```java
public interface Shape {
    int getArea();
}
```

---

### 4. Interface Segregation Principle (ISP)
❌ **Violation (Fat Interface)**:
```java
public interface MultiFunctionDevice {
    void print();
    void scan();
    void fax();
}

// Simple printer is forced to provide useless stubs:
public class BasicPrinter implements MultiFunctionDevice {
    public void print() { System.out.println("Printing..."); }
    public void scan() { throw new UnsupportedOperationException("No scanner!"); }
    public void fax() { throw new UnsupportedOperationException("No fax!"); }
}
```

✅ **Refactored (Cohesive, Segregated Interfaces)**:
```java
public interface Printer { void print(); }
public interface Scanner { void scan(); }
public interface FaxMachine { void fax(); }

// Basic printer implements only what it supports:
public class BasicPrinter implements Printer {
    public void print() { System.out.println("Printing..."); }
}

// Advanced all-in-one printer implements multiple interfaces:
public class SmartOfficePrinter implements Printer, Scanner, FaxMachine {
    public void print() { /* ... */ }
    public void scan() { /* ... */ }
    public void fax() { /* ... */ }
}
```

---

### 5. Dependency Inversion Principle (DIP)
High-level policy should not be coupled to low-level implementation details.

❌ **Violation (Hardcoded Concrete Dependency)**:
```java
public class NotificationManager {
    // Tightly coupled to SendGridEmailSender directly:
    private SendGridEmailSender sender = new SendGridEmailSender();

    public void notifyUser(String msg) {
        sender.send(msg); // Cannot easily switch to AWS SES or Mock for tests!
    }
}
```

✅ **Refactored (Constructor Dependency Injection)**:
```java
// 1. Abstraction
public interface MessageSender {
    void send(String message);
}

// 2. Concrete implementations
public class SendGridSender implements MessageSender {
    public void send(String msg) { /* ... */ }
}
public class AwsSesSender implements MessageSender {
    public void send(String msg) { /* ... */ }
}

// 3. High-level class receives dependency via constructor
public class NotificationManager {
    private final MessageSender sender;

    // Dependency Injection (DI)
    public NotificationManager(MessageSender sender) {
        this.sender = sender;
    }

    public void notifyUser(String msg) {
        sender.send(msg);
    }
}
```

---

## 3. Self-Check & Quick Review

1. **Q**: What is the key symptom that Open/Closed Principle is being violated?
   - *A*: Having to modify existing, tested classes using growing `if-else` or `switch` statements whenever a new feature or type is added.
2. **Q**: How does Dependency Inversion Principle help in Unit Testing?
   - *A*: By depending on interfaces, you can pass mock implementations (e.g., `MockDatabaseRepository`) into classes under test without spinning up actual external databases.
3. **Q**: What is the difference between Dependency Inversion and Dependency Injection?
   - *A*: **Dependency Inversion** is the high-level design principle (depend on abstractions). **Dependency Injection** is the design pattern/technique used to deliver the abstraction (e.g., passing the dependency via a constructor).

---

## 🧭 Continue Learning

| ◀️ Previous Topic | 🧭 Track Hub | Next Topic ▶️ |
| :--- | :---: | ---: |
| [**Page 1: Java Backend Architecture**](01-java-backend-architecture.md)<br><sub>*Request Lifecycle, Tomcat & Virtual Threads*</sub> | [**System Design Index**](README.md)<br><sub>*Architecture, LLD & Scalability*</sub> | [**Page 3: GoF Design Patterns**](03-design-patterns-in-java.md)<br><sub>*Thread-Safe Singleton, Builder & Strategy*</sub> |
