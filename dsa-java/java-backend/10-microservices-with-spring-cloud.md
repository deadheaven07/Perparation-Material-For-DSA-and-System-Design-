# Page 10: Microservices Architecture with Spring Cloud

Welcome to Page 10 of the Java Backend Engineering series. As software organizations grow, a single monolithic JAR becomes a bottleneck for continuous delivery. **Spring Cloud** provides the battle-tested framework for building, discovering, routing, and configuring distributed microservices.

---

## 1. The Microservices Ecosystem Architecture

```
[ Mobile / Web Client ]
          |
          v (HTTPS Request)
+-------------------------------------------------------------+
|                  API Gateway (Spring Cloud Gateway)         |
|  - Route: /api/v1/users/**    -> USER-SERVICE               |
|  - Route: /api/v1/orders/**   -> ORDER-SERVICE              |
|  - Centralized JWT verification, Rate limiting & CORS       |
+-------------------------------------------------------------+
          |
          +-----------------------+-----------------------+
          |                       |                       |
          v                       v                       v
  [ USER-SERVICE ]        [ ORDER-SERVICE ]     [ PAYMENT-SERVICE ]
       (JVM 1)                 (JVM 2)               (JVM 3)
          |                       |                       |
          +-----------------------+-----------------------+
                                  |
                                  v Registers dynamic IP / Heartbeat
                 +---------------------------------+
                 | Service Registry (Eureka/Consul)|
                 +---------------------------------+
```

---

## 2. API Gateway: Spring Cloud Gateway

Clients should never communicate with individual microservices directly. **Spring Cloud Gateway** (built on non-blocking Netty) acts as the single reverse proxy.

### Configuration via `application.yml`:

```yaml
server:
  port: 8080

spring:
  cloud:
    gateway:
      routes:
        # Route 1: Forward user requests to USER-SERVICE
        - id: user_service_route
          uri: lb://USER-SERVICE   # 'lb://' enables client-side load balancing!
          predicates:
            - Path=/api/v1/users/**
          filters:
            - AddRequestHeader=X-Gateway-Source, PublicAPI
            - StripPrefix=0

        # Route 2: Forward order requests with Rate Limiting
        - id: order_service_route
          uri: lb://ORDER-SERVICE
          predicates:
            - Path=/api/v1/orders/**
          filters:
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 10
                redis-rate-limiter.burstCapacity: 20
```

---

## 3. Service Discovery: Netflix Eureka

In modern cloud environments, microservice instances scale up and down dynamically. Hardcoding IP addresses (e.g., `http://192.168.1.50:8081`) will crash your system when containers restart.

### How Eureka Works:
1. **Registration**: When `ORDER-SERVICE` starts up, it connects to Eureka and registers: `"ORDER-SERVICE" -> IP: 10.0.1.25, Port: 8082`.
2. **Heartbeats**: Every 30 seconds, instances send a heartbeat to Eureka. If heartbeats stop, Eureka evicts the dead instance.
3. **Lookup**: When the Gateway needs to call `ORDER-SERVICE`, it asks Eureka for available healthy IPs and load-balances across them.

### Enabling Service Discovery on a Microservice:
```java
@SpringBootApplication
@EnableDiscoveryClient // Automatically registers with Eureka on startup
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
```

---

## 4. Declarative REST Clients: Spring Cloud OpenFeign

Instead of writing cumbersome `RestTemplate` or `WebClient` boilerplate to call another microservice, **Spring Cloud OpenFeign** lets you declare remote endpoints as a simple Java interface:

### 1. Define the Feign Client Interface:

```java
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// 'name = "INVENTORY-SERVICE"' automatically looks up IPs from Eureka & load-balances!
@FeignClient(name = "INVENTORY-SERVICE", fallback = InventoryClientFallback.class)
public interface InventoryClient {

    @GetMapping("/api/v1/inventory/{productId}")
    InventoryResponse checkStock(@PathVariable("productId") Long productId);
}
```

### 2. Provide a Fallback for Fault Tolerance:

```java
import org.springframework.stereotype.Component;

@Component
public class InventoryClientFallback implements InventoryClient {
    @Override
    public InventoryResponse checkStock(Long productId) {
        // Fallback returned when Inventory service is down or timing out!
        return new InventoryResponse(productId, 0, "STATUS_UNKNOWN");
    }
}
```

### 3. Use it inside your Business Service:

```java
@Service
public class OrderService {

    private final InventoryClient inventoryClient;

    public OrderService(InventoryClient inventoryClient) {
        this.inventoryClient = inventoryClient;
    }

    public void createOrder(Long productId) {
        // Looks like a local Java method call, but executes an HTTP call under the hood!
        InventoryResponse stock = inventoryClient.checkStock(productId);
        if (stock.quantity() <= 0) {
            throw new OutOfStockException("Item is currently out of stock!");
        }
        // proceed with order creation...
    }
}
```

---

## 5. Centralized Configuration: Spring Cloud Config

In a system with 30 microservices, changing a database password or logging level shouldn't require rebuilding and redeploying 30 JAR files.

```
[ Git Repository / Vault ] (Holds central application-prod.yml)
             ^
             | Fetches configs on startup
[ Spring Cloud Config Server ]
             ^
             | Polls config on startup (or via /actuator/refresh)
   +---------+---------+
   |                   |
[ User Service ]  [ Order Service ]
```

Annotating beans with **`@RefreshScope`** allows reloading properties at runtime without restarting the JVM:

```java
@RestController
@RefreshScope // Reloads 'taxRate' dynamically when /actuator/refresh is called!
public class CheckoutController {

    @Value("${pricing.tax-rate:0.18}")
    private double taxRate;

    @GetMapping("/tax")
    public double getTax() {
        return taxRate;
    }
}
```

---

## 6. Self-Check & Quick Review

1. **Q**: What does the `lb://` prefix signify in a Spring Cloud Gateway route URI (e.g., `lb://USER-SERVICE`)?
   - *A*: It instructs the gateway to resolve `USER-SERVICE` via the Service Discovery registry (Eureka) and apply client-side load balancing across available healthy instances.
2. **Q**: Why is OpenFeign preferred over raw `RestTemplate`?
   - *A*: It is purely declarative (interface + annotations), eliminates manual HTTP connection and serialization code, and integrates out of the box with Eureka discovery and fallback circuit breakers.
3. **Q**: What happens if an instance of a service crashes abruptly in a Eureka cluster?
   - *A*: The instance stops sending its periodic heartbeats. After a configured threshold (default 90 seconds), Eureka evicts the dead instance from its registry, preventing callers from routing traffic to it.

---

## 🧭 Continue Learning

| ◀️ Previous Topic | 🧭 Track Hub | Next Topic ▶️ |
| :--- | :---: | ---: |
| [**Page 9: Messaging & Apache Kafka**](09-messaging-kafka-and-event-driven.md)<br><sub>*Event-Driven Backends, Offsets & DLQs*</sub> | [**Java Backend Index**](README.md)<br><sub>*Curriculum & Architecture*</sub> | [**Page 11: Security & JWT**](11-backend-security-spring-security-jwt.md)<br><sub>*Security Filter Chain, Stateless Auth & RBAC*</sub> |
