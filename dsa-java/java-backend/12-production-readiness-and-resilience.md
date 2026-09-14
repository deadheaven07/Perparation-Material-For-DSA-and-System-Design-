# Page 12: Production Readiness, Observability & Resilience

Welcome to Page 12 of the Java Backend Engineering series. Anyone can write code that works on `localhost`. Senior backend engineers write systems that **gracefully survive production traffic spikes, network failures, and infrastructure degradations**.

---

## 1. The 3 Pillars of Backend Observability

```
                             OBSERVABILITY
             +---------------------+---------------------+
             |                     |                     |
          Metrics                 Logs                Traces
   "Is the system healthy?"    "What happened?"     "Where is the bottleneck?"
    (Prometheus, Grafana)      (Logback, ELK Stack)   (Micrometer, Zipkin)
```

1. **Metrics**: Aggregated numerical data over time (e.g., JVM Heap usage, HTTP request rate, $p99$ latency).
2. **Logs**: Discrete timestamped event messages output by your application.
3. **Traces**: The complete end-to-end journey of a single request traversing multiple distributed microservices.

---

## 2. Spring Boot Actuator: Production Health Checks

Spring Boot Actuator provides built-in production management endpoints:

```properties
# application.properties
management.endpoints.web.exposure.include=health,metrics,prometheus
management.endpoint.health.show-details=always
management.endpoint.health.probes.enabled=true   # Enables Liveness & Readiness for Kubernetes
```

### 1. Liveness vs. Readiness Probes:
- **Liveness Probe** (`/actuator/health/liveness`): Is the JVM alive and running? If this fails, Kubernetes kills and restarts the pod.
- **Readiness Probe** (`/actuator/health/readiness`): Is the application ready to accept traffic (DB connected, cache warmed up)? If this fails, the load balancer stops sending traffic until it recovers.

### 2. Custom Health Indicator:

```java
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class PaymentGatewayHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        boolean isGatewayReachable = checkGatewayPing();

        if (isGatewayReachable) {
            return Health.up().withDetail("PaymentGateway", "Operational").build();
        } else {
            return Health.down().withDetail("PaymentGateway", "Timeout / Unreachable").build();
        }
    }

    private boolean checkGatewayPing() {
        // Ping external gateway socket
        return true;
    }
}
```

---

## 3. Metrics with Micrometer & Prometheus

**Micrometer** is the "SLF4J for metrics". It allows you to instrument code once and export to Prometheus, Datadog, or CloudWatch.

### Recording Custom Metrics in a Service:

```java
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

@Service
public class OrderProcessingService {

    private final Counter orderCounter;
    private final Timer orderProcessingTimer;

    public OrderProcessingService(MeterRegistry registry) {
        // 1. Counter: Tracks total number of placed orders
        this.orderCounter = Counter.builder("orders.placed.total")
                .description("Total number of orders successfully placed")
                .tag("region", "us-east")
                .register(registry);

        // 2. Timer: Tracks execution time and p95/p99 latency
        this.orderProcessingTimer = Timer.builder("orders.processing.time")
                .description("Time taken to process an order")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(registry);
    }

    public void processOrder(Long orderId) {
        orderProcessingTimer.record(() -> {
            // Business logic...
            orderCounter.increment();
        });
    }
}
```

---

## 4. Distributed Tracing: Micrometer Tracing & Zipkin

In a microservices ecosystem, a single user request can trigger calls across 5 services. How do you trace where the latency occurred?

```
Client Request (Trace ID: 7f3b89a1)
       |
       +---> [API Gateway]      (Span ID: 1, Trace ID: 7f3b89a1)
                  |
                  +---> [Order Svc]    (Span ID: 2, Parent Span: 1, Trace ID: 7f3b89a1)
                             |
                             +---> [Payment Svc]  (Span ID: 3, Parent Span: 2, Trace ID: 7f3b89a1)
```

With **Micrometer Tracing**, Spring automatically generates a **Trace ID** and injects it into every log line:

```text
2026-09-15 03:55:00.123 INFO [order-service,7f3b89a1,2] 12345 --- [main] c.e.OrderService : Order created successfully
```
*Searching for `7f3b89a1` in your logging tool brings up every log across all microservices for that exact request!*

---

## 5. Resilience Engineering: Resilience4j

When external APIs fail, **Resilience4j** prevents cascading outages using Circuit Breakers, Rate Limiters, and Bulkheads.

### 1. Circuit Breaker (`@CircuitBreaker`):
If error rate exceeds $50\%$ over a sliding window, trips to `OPEN` and executes a fallback instantly without calling the downstream network:

```java
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

@Service
public class CurrencyConversionService {

    @CircuitBreaker(name = "forexService", fallbackMethod = "fallbackExchangeRate")
    public double getExchangeRate(String from, String to) {
        // Calls remote external exchange rate API
        return remoteForexClient.getRate(from, to);
    }

    // Fallback method executed when circuit is OPEN
    public double fallbackExchangeRate(String from, String to, Throwable t) {
        System.err.println("Forex API down. Returning cached fallback rate...");
        return 1.0; // Safe default
    }
}
```

### 2. Rate Limiting (`@RateLimiter`):
Restricts the number of calls to an endpoint per unit of time.

### 3. Graceful Shutdown in Spring Boot:
Allows in-flight HTTP requests to complete before terminating the JVM during server restarts:

```properties
server.shutdown=graceful
spring.lifecycle.timeout-per-shutdown-phase=30s
```

---

## 6. Self-Check & Quick Review

1. **Q**: What is the difference between a Kubernetes Liveness probe and a Readiness probe?
   - *A*: If a **Liveness** probe fails, Kubernetes restarts the pod. If a **Readiness** probe fails, Kubernetes temporarily removes the pod from the service load balancer so it receives no traffic until it recovers.
2. **Q**: What is the difference between a Trace ID and a Span ID?
   - *A*: A **Trace ID** remains identical across all microservices for an entire end-to-end user request. A **Span ID** represents an individual unit of work within a specific microservice.
3. **Q**: What does `server.shutdown=graceful` do?
   - *A*: It stops accepting new incoming HTTP connections while giving existing, in-flight requests up to 30 seconds to finish processing before shutting down the JVM.

---

🎉 **Congratulations! You have completed the complete Java Backend Engineering Course (Pages 0 to 12)!**
You now have a production-grade, enterprise mental model spanning web protocols, Spring Boot core, persistence, concurrency, caching, messaging, security, and resilience.
