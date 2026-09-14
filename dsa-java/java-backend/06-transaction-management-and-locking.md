# Page 6: Transaction Management & Database Locking

Welcome to Page 6 of the Java Backend Engineering series. Data integrity under high concurrent load is the hallmark of a reliable backend system. This page breaks down how Spring's **`@Transactional`** works under the hood, transaction propagation rules, and how to prevent race conditions with **Optimistic vs. Pessimistic Locking**.

---

## 1. How Spring `@Transactional` Works: The AOP Proxy

When you annotate a class or method with `@Transactional`, Spring does **not** modify your source code. Instead, it generates a **Dynamic AOP Proxy** that wraps your service bean.

```
Caller (e.g., UserController)
        |
        v Invokes userService.transferMoney()
+-----------------------------------------------------------------------------+
|                         Spring AOP Dynamic Proxy                            |
|                                                                             |
|  1. Intercepts method call                                                  |
|  2. Fetches a connection from HikariCP pool                                 |
|  3. Starts transaction: connection.setAutoCommit(false);                    |
|  4. Invokes target method on actual UserService instance...                 |
|                                                                             |
|     +-------------------------------------------------------------+         |
|     | Target: UserServiceImpl.transferMoney()                     |         |
|     |   - debitAccount(acc1, $100);                               |         |
|     |   - creditAccount(acc2, $100);                              |         |
|     +-------------------------------------------------------------+         |
|                                                                             |
|  5. If method completes successfully:                                       |
|        connection.commit();                                                 |
|     If unhandled exception thrown:                                          |
|        connection.rollback();                                               |
|  6. Returns connection to pool                                              |
+-----------------------------------------------------------------------------+
```

> [!CAUTION]
> **The Default Rollback Trap**:
> By default, Spring `@Transactional` **only rolls back on unchecked exceptions** (`RuntimeException` and `Error`). If a checked exception (e.g., `IOException` or `SQLException`) is thrown, Spring **commits** the transaction anyway!
> **Production Standard Rule**: Always write:
> ```java
> @Transactional(rollbackFor = Exception.class)
> ```

---

## 2. The #1 Spring Trap: Self-Invocation

What happens when a method calls another `@Transactional` method inside the **same class**?

```java
@Service
public class OrderService {

    public void processOrder(Order order) {
        // ... some validation ...
        this.saveOrderWithInvoice(order); // 💥 SELF-INVOCATION!
    }

    @Transactional
    public void saveOrderWithInvoice(Order order) {
        orderRepository.save(order);
        invoiceRepository.save(new Invoice(order));
    }
}
```

### Why This Fails Silently:
When `processOrder()` calls `this.saveOrderWithInvoice()`, the call is executed directly on the internal Java `this` reference, **completely bypassing the Spring AOP Proxy**!
- As a result, **no transaction is ever opened**, and no rollback will occur if an error happens!

### The Two Solutions:
1. **Move to a Separate Service (Recommended)**: Move `saveOrderWithInvoice()` to an `OrderPersistenceService` bean so it gets called through its Spring proxy.
2. **Self-Injection**: Inject the service proxy into itself:
   ```java
   @Service
   public class OrderService {
       @Lazy @Autowired
       private OrderService self; // Injects proxy

       public void processOrder(Order order) {
           self.saveOrderWithInvoice(order); // ✅ Routes through proxy!
       }
   }
   ```

---

## 3. Transaction Propagation Strategies

Propagation defines what happens if a transactional method is called by another method that already has an active transaction:

| Propagation Type | Behavior If Existing Transaction Exists | Behavior If NO Existing Transaction | Best Used For |
| :--- | :--- | :--- | :--- |
| **`REQUIRED`** (Default) | **Joins** the existing transaction | Creates a **new** transaction | 95% of business logic |
| **`REQUIRES_NEW`** | **Suspends** existing; creates an independent **new** transaction | Creates a **new** transaction | Audit logs, security logging, payment attempts that must persist even if outer flow rolls back |
| **`SUPPORTS`** | Joins existing transaction | Runs non-transactionally | Read-only methods |
| **`NOT_SUPPORTED`** | **Suspends** existing transaction | Runs non-transactionally | Heavy calculations / long I/O |
| **`MANDATORY`** | Joins existing transaction | **Throws Exception** (`TransactionRequiredException`) | Helper methods that require a transaction |
| **`NEVER`** | **Throws Exception** | Runs non-transactionally | Operations that must never touch a transaction |

### Example: Audit Logging with `REQUIRES_NEW`

```java
@Service
public class AuditService {
    // Independent transaction: commits even if the parent order placement fails!
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAttempt(String action, String user) {
        auditRepository.save(new AuditLog(action, user, Instant.now()));
    }
}
```

---

## 4. Concurrency Control: Optimistic vs. Pessimistic Locking

When thousands of users try to purchase the last 2 seats on a flight, how do you prevent double-booking?

```
                     Optimistic Locking                            Pessimistic Locking
              (@Version: Fast, No DB lock held)               (SELECT FOR UPDATE: DB row locked)
                            |                                               |
                Read Ticket (Version = 1)                       SELECT * FROM tickets WHERE id = 10
                            |                                           FOR UPDATE; (Acquires Lock)
                  Process in Java Application                               |
                            |                                       Thread 1 modifies row;
               UPDATE tickets SET status = 'SOLD',                  Thread 2 BLOCKS waiting for lock!
               version = 2 WHERE id = 10 AND version = 1;                   |
                            |                                       Thread 1 commits & releases lock.
              Did update affect 1 row?
                    /          \
                 (Yes)         (No - Conflict!)
                  |              |
               Success     Throw OptimisticLockException
                           (Retry or fail gracefully)
```

### 1. Optimistic Locking in Spring Data JPA:
Best when read volume is high and concurrent write collisions are relatively rare.

```java
@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    private Long id;
    private String status;

    @Version // JPA automatically checks and increments this column on UPDATE!
    private Long version;
}
```

### 2. Pessimistic Locking in Spring Data JPA:
Best under heavy contention (flash sales, banking ledger balances, ticket booking):

```java
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // Executes: SELECT * FROM tickets WHERE id = ? FOR UPDATE
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Ticket t WHERE t.id = :id")
    Optional<Ticket> findByIdWithLock(@Param("id") Long id);
}
```

---

## 5. Self-Check & Quick Review

1. **Q**: Why does calling a `@Transactional` private method not work in Spring?
   - *A*: Spring AOP proxies inherit from or wrap the target class. Private methods cannot be overridden or intercepted by the proxy, so no transactional advice can be executed.
2. **Q**: What happens when an `OptimisticLockException` occurs?
   - *A*: Another transaction updated the row first (incrementing its version). The current transaction fails, and the application can either retry the operation or notify the user that the resource was modified.
3. **Q**: When should you choose `REQUIRES_NEW` over `REQUIRED`?
   - *A*: When an inner operation must persist independently of whether the outer transaction succeeds or fails (e.g., logging a failed login attempt or recording payment gateway interaction history).

---

## 🧭 Continue Learning

| ◀️ Previous Topic | 🧭 Track Hub | Next Topic ▶️ |
| :--- | :---: | ---: |
| [**Page 5: JPA & Hibernate Persistence**](05-database-persistence-jpa-hibernate.md)<br><sub>*Entity Relationships & N+1 Query Fixes*</sub> | [**Java Backend Index**](README.md)<br><sub>*Curriculum & Architecture*</sub> | [**Page 7: Async & Virtual Threads**](07-async-virtual-threads-and-concurrency.md)<br><sub>*CompletableFuture & Java 21 Project Loom*</sub> |
