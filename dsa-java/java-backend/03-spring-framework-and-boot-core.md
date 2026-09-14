# Page 3: Spring Framework & Spring Boot Core Demystified

Welcome to Page 3 of the Java Backend Engineering series. Spring is the world's most dominant enterprise Java framework. Understanding its core engine—**Inversion of Control (IoC)**, **Dependency Injection (DI)**, and **Spring Boot Auto-configuration**—is essential for every Java backend role.

---

## 1. Inversion of Control (IoC) & Dependency Injection (DI)

In traditional programming, a class instantiates its own dependencies directly using `new`:

```java
// ❌ Tightly Coupled (Anti-Pattern):
public class OrderService {
    private PaymentGateway gateway = new StripePaymentGateway(); // Hardcoded dependency!
}
```
*If you want to switch from Stripe to PayPal, or mock `PaymentGateway` for unit testing, you must rewrite `OrderService`!*

### The Inversion:
With **Inversion of Control**, you give control of object creation to the **Spring ApplicationContext (IoC Container)**. Your class simply declares what it needs, and Spring **injects** the dependency.

```
Without IoC (Tight Coupling):
OrderService ---------------------> creates new StripePaymentGateway()

With Spring IoC Container:
[ Spring IoC Container ]
       |
       | 1. Instantiates StripePaymentGateway
       | 2. Instantiates OrderService
       | 3. Injects Stripe into OrderService constructor!
       v
OrderService (Receives PaymentGateway interface via Constructor)
```

---

## 2. Dependency Injection: Constructor vs. Field Injection

### 1. Constructor Injection (The Industry Standard)
```java
@Service
public class OrderService {
    private final PaymentGateway gateway; // Immutable!

    // Spring 4.3+: @Autowired is NOT even required if there is only one constructor
    public OrderService(PaymentGateway gateway) {
        this.gateway = gateway;
    }
}
```
**Why Constructor Injection is Preferred**:
1. **Immutability**: Dependencies can be marked `final`.
2. **Easy Unit Testing**: You can instantiate `OrderService` in a plain JUnit test with `new OrderService(new MockPaymentGateway())` without Spring or reflection!
3. **Fails Fast**: If a dependency is missing, compilation or startup fails immediately rather than at runtime with a `NullPointerException`.

### 2. Field Injection (The Anti-Pattern to Avoid)
```java
@Service
public class BadOrderService {
    @Autowired // ❌ AVOID IN PRODUCTION
    private PaymentGateway gateway; 
}
```
**Why Field Injection is Bad**:
- Cannot be marked `final`.
- Hides dependencies: A class can have 15 injected fields without the developer realizing it violates the Single Responsibility Principle.
- Requires reflection to test; cannot instantiate in plain JUnit without Mockito annotations.

---

## 3. Spring Bean Lifecycle

A **Spring Bean** is simply a Java object whose creation, configuration, and destruction are managed by the Spring IoC Container.

```
[ 1. Bean Instantiated ] 
          ↓
[ 2. Dependencies Injected (Properties Populated) ] 
          ↓
[ 3. Aware Interfaces Called (BeanNameAware, ApplicationContextAware) ] 
          ↓
[ 4. @PostConstruct / InitializingBean afterPropertiesSet() ] 
          ↓
====== BEAN READY FOR USE ======
          ↓
[ 5. Application Shuts Down ] 
          ↓
[ 6. @PreDestroy / DisposableBean destroy() ]
```

### Lifecycle Callbacks in Practice:

```java
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class CacheWarmupService {

    @PostConstruct
    public void onStartup() {
        System.out.println("Step 4: Bean ready! Warming up Redis cache from DB...");
    }

    @PreDestroy
    public void onShutdown() {
        System.out.println("Step 6: Server stopping! Flushing in-memory buffers...");
    }
}
```

---

## 4. Bean Scopes: The Prototype-in-Singleton Pitfall

Spring supports 5 scopes:
1. **`singleton`** (Default): Exactly **one instance** per Spring container. Shared across all threads.
2. **`prototype`**: A **new instance** is created every time the bean is requested.
3. **`request`** (Web): One instance per HTTP request lifecycle.
4. **`session`** (Web): One instance per HTTP session.
5. **`application`** (Web): One instance per `ServletContext`.

### The Prototype-in-Singleton Pitfall:
What happens if you inject a `prototype` bean into a `singleton` bean?

```java
@Component
@Scope("prototype")
public class TokenGenerator { /* generates unique token */ }

@Service // Singleton by default!
public class AuthService {
    @Autowired
    private TokenGenerator tokenGenerator; 
}
```

> [!WARNING]
> **The Trap**: Because `AuthService` is a singleton, it is only instantiated **once** on application startup. Therefore, its dependencies are injected **once**! The `TokenGenerator` will **never** be re-created per call, completely defeating the prototype scope!
> **The Solution**: Use `ObjectProvider<TokenGenerator>` or `@Lookup` to fetch a fresh prototype instance on demand.

---

## 5. Stereotype Annotations Breakdown

| Annotation | Layer | Special Behavior |
| :--- | :--- | :--- |
| **`@Component`** | General | Basic Spring-managed bean. Base annotation for all stereotypes. |
| **`@Service`** | Business Logic | Semantic indicator for business logic; ideal target for AOP transaction boundaries. |
| **`@Repository`** | Data Access | Automatically intercepts database-specific SQL exceptions and translates them into Spring's unified **`DataAccessException`** hierarchy. |
| **`@Controller`** | Presentation | Returns web views (HTML templates). |
| **`@RestController`** | REST API | Shorthand for `@Controller` + `@ResponseBody`. Automatically serializes returned Java objects into JSON using Jackson. |

---

## 6. Spring Boot Auto-Configuration Demystified

Before Spring Boot, configuring a Spring application required hundreds of lines of XML or verbose `@Configuration` classes just to set up a database, JSON serializer, and web server.

### What does `@SpringBootApplication` actually do?

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

`@SpringBootApplication` is a composite annotation consisting of three core annotations:
1. **`@Configuration`**: Flags the class as a source of bean definitions.
2. **`@ComponentScan`**: Recursively scans the current package and sub-packages for `@Component`, `@Service`, `@Repository`, and `@RestController`.
3. **`@EnableAutoConfiguration`**: The real magic of Spring Boot.

### How Auto-Configuration Works Under the Hood:
When your application starts, Spring Boot inspects:
1. **The Classpath**: Looks for JARs. If `spring-boot-starter-data-jpa` and `postgresql` are on the classpath, Spring Boot automatically registers a `DataSource`, an `EntityManagerFactory`, and a `TransactionManager`.
2. **Conditional Annotations**: Auto-configuration classes use:
   - `@ConditionalOnClass(DataSource.class)`: Run only if `DataSource` class exists.
   - `@ConditionalOnMissingBean(DataSource.class)`: Run **only if the developer has NOT defined their own custom DataSource bean**!

```
Developer provides custom DataSource Bean?
          /                      \
       (YES)                     (NO)
        |                         |
Spring Boot steps aside   Spring Boot automatically configures
and uses developer bean   HikariCP DataSource with default settings!
```

---

## 7. Self-Check & Quick Review

1. **Q**: Why should field injection (`@Autowired private MyService myService;`) be avoided?
   - *A*: It prevents fields from being `final` (breaking immutability), makes unit testing impossible without reflection, and masks violation of the Single Responsibility Principle.
2. **Q**: What is the default scope of a Spring Bean?
   - *A*: **Singleton** (one shared instance per Spring `ApplicationContext`).
3. **Q**: How does `@ConditionalOnMissingBean` help Spring Boot developers?
   - *A*: It provides sensible defaults out-of-the-box while allowing developers to seamlessly override any bean simply by declaring their own `@Bean` method.

---

## 🧭 Continue Learning

| ◀️ Previous Topic | 🧭 Track Hub | Next Topic ▶️ |
| :--- | :---: | ---: |
| [**Page 2: Servlets & Spring MVC**](02-servlet-containers-and-spring-mvc.md)<br><sub>*Tomcat, DispatcherServlet & Filters*</sub> | [**Java Backend Index**](README.md)<br><sub>*Curriculum & Architecture*</sub> | [**Page 4: REST APIs & Validation**](04-restful-apis-dto-and-validation.md)<br><sub>*DTO Pattern, Jakarta Validation & Error Handling*</sub> |
