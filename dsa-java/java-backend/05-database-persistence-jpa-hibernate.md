# Page 5: Database Persistence with Spring Data JPA & Hibernate

Welcome to Page 5 of the Java Backend Engineering series. In enterprise software, database interactions represent the single biggest bottleneck and source of production bugs. This page covers **Object-Relational Mapping (ORM)**, relationship mapping, and how to conquer the infamous **N+1 query problem**.

---

## 1. The Persistence Hierarchy: JDBC vs. JPA vs. Hibernate

Understanding the separation between specifications and implementations is essential:

```
[ Your Java Code (Spring Service) ]
                |
                v Uses high-level repositories (findByName, save)
  [ Spring Data JPA ]  (Spring abstraction library)
                |
                v Adheres to standard interfaces (EntityManager)
  [ JPA (Jakarta Persistence API) ]  (The official Java specification)
                |
                v Implements the JPA spec & generates SQL
  [ Hibernate ORM ]  (The engine: Session, Dirty Checking, Cache)
                |
                v Manages TCP sockets & SQL execution
  [ JDBC Driver ]  (PostgreSQL / MySQL driver)
                |
                v
  [ Database (PostgreSQL / MySQL) ]
```

- **JPA**: The specification (interfaces, annotations like `@Entity`, `@Id`, `@ManyToOne`).
- **Hibernate**: The engine that implements JPA and converts Java object graphs into SQL statements.
- **Spring Data JPA**: A library that generates repository implementations (`JpaRepository<User, Long>`) dynamically at runtime without you writing boilerplate DAO classes!

---

## 2. Entity Modeling Best Practices

```java
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Uses DB auto-increment
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING) // ✅ ALWAYS use STRING (never ORDINAL which breaks on enum reorder!)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    // Protected or public no-arg constructor required by Hibernate:
    protected User() {}

    public User(String email, String passwordHash, UserRole role) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    // Getters and business methods...
}
```

---

## 3. Entity Relationships: Owning vs. Inverse Side

In relational databases, relationships are formed by **Foreign Keys (FK)**. In Java, relationships are formed by **Object References**.

```
Database Schema (Foreign Key is on the 'books' table):
[ authors ]                    [ books ]
  id (PK) <--------------------- author_id (FK), id (PK), title
(Inverse Side)                 (Owning Side - holds the FK!)
```

### 1. The Golden Rule: `@ManyToOne` is Always the Owning Side
```java
@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    // ✅ ALWAYS use LAZY fetch! (EAGER causes accidental multi-table joins)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false) // Defines Foreign Key column
    private Author author;

    // constructor, getters, setters
}
```

### 2. The Inverse Side: `@OneToMany(mappedBy = "...")`
```java
@Entity
@Table(name = "authors")
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // mappedBy points to the field name in the Book class that owns the foreign key!
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Book> books = new ArrayList<>();

    // Helper synchronization method for bidirectional consistency:
    public void addBook(Book book) {
        books.add(book);
        book.setAuthor(this);
    }

    public void removeBook(Book book) {
        books.remove(book);
        book.setAuthor(null);
    }
}
```

> [!WARNING]
> **Avoid `@ManyToMany` with simple `@JoinTable` in Production**:
> A raw `@ManyToMany` makes it impossible to store additional attributes on the relationship (e.g., `enrolled_date` in Student-Course). Always model many-to-many as two separate `@ManyToOne` relationships pointing to a dedicated join entity (e.g., `Enrollment`).

---

## 4. The Dreaded N+1 Query Problem & How to Conquer It

The **N+1 Problem** is the #1 performance bug in Spring/Hibernate backends.

### How It Happens:
Suppose you want to display a list of 100 books and their respective authors:

```java
// ❌ Generates N + 1 SQL Queries!
List<Book> books = bookRepository.findAll(); // Query 1: SELECT * FROM books;

for (Book book : books) {
    // Because author is LAZY, accessing getAuthor() fires a separate SQL query for EACH book!
    System.out.println(book.getTitle() + " by " + book.getAuthor().getName()); 
}
```
If there are 100 books, this executes **1 query for books + 100 queries for authors = 101 queries!** Under high traffic, this overwhelms and crashes the database.

```
Query 1:   SELECT * FROM books;
Query 2:   SELECT * FROM authors WHERE id = 1;
Query 3:   SELECT * FROM authors WHERE id = 2;
...
Query 101: SELECT * FROM authors WHERE id = 100;  (101 network round trips!)
```

### Solution 1: `JOIN FETCH` in JPQL (The Standard Fix)
`JOIN FETCH` forces Hibernate to fetch the parent and child entities in a **single SQL JOIN query**:

```java
public interface BookRepository extends JpaRepository<Book, Long> {

    // ✅ Executes 1 SQL query: SELECT b.*, a.* FROM books b INNER JOIN authors a ON b.author_id = a.id
    @Query("SELECT b FROM Book b JOIN FETCH b.author")
    List<Book> findAllWithAuthors();
}
```

### Solution 2: `@EntityGraph` (Declarative Fix)
Overrides the default `LAZY` fetching strategy on demand for a specific query without writing custom JPQL:

```java
public interface BookRepository extends JpaRepository<Book, Long> {

    @EntityGraph(attributePaths = {"author"})
    @Override
    List<Book> findAll(); // Automatically executes a single SQL JOIN query!
}
```

---

## 5. Hibernate Caching: First-Level vs. Second-Level

```
[ Client Request ]
       |
       v
[ Spring @Transactional Service Method ]
       |
       v
+-------------------------------------------------------------+
|               1st-Level Cache (Hibernate Session)           |
| - Scope: Bound to the CURRENT transaction/thread.           |
| - Behavior: If you call findById(42) three times within one |
|   transaction, Hibernate runs SQL ONCE and caches in RAM!   |
+-------------------------------------------------------------+
       | (Cache Miss)
       v
+-------------------------------------------------------------+
|               2nd-Level Cache (Redis / Ehcache)             |
| - Scope: Shared across ALL transactions and ALL users.      |
| - Configured explicitly for read-heavy, rarely changed data |
|   (e.g., Country codes, system permissions).                |
+-------------------------------------------------------------+
       | (Cache Miss)
       v
[ Database (PostgreSQL / MySQL) ]
```

---

## 6. Self-Check & Quick Review

1. **Q**: Why should you never use `EnumType.ORDINAL` with `@Enumerated`?
   - *A*: `ORDINAL` saves the enum's position index (`0, 1, 2`) into the database. If a developer later inserts a new enum value at the top, all existing database rows will permanently map to the wrong enum values! Always use `EnumType.STRING`.
2. **Q**: What does `orphanRemoval = true` do in a `@OneToMany` relationship?
   - *A*: If a child entity is removed from the parent's collection (`author.getBooks().remove(book)`), Hibernate will automatically issue an SQL `DELETE FROM books WHERE id = ?` to remove the orphaned record from the database.
3. **Q**: What is the difference between `JOIN` and `JOIN FETCH` in JPQL?
   - *A*: A plain `JOIN` filters rows in SQL but only hydrates the root entity into memory (leaving the related entity uninitialized). A `JOIN FETCH` instructs Hibernate to initialize and populate the related child entities in the same query, eliminating the N+1 problem.

---

👉 **Next Up: [Page 6: Transaction Management & Database Locking](file:///Users/deadheaven07/Downloads/Prep_DSA_SystemDesign/dsa-java/java-backend/06-transaction-management-and-locking.md)**
