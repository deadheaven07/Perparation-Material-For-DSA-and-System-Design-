# Page 9: Microservices, API Gateways & Protocols (REST vs. gRPC)

Welcome to Page 9 of the System Design Fundamentals series. As applications scale beyond a single codebase, monolithic architectures decompose into microservices. Managing service discovery, protocol efficiency, and cross-cutting security requires dedicated architectural patterns.

---

## 1. The API Gateway Pattern

In a microservices architecture, clients should **never** talk to dozens of backend microservices directly. An **API Gateway** acts as the single, secure front door.

```
[ Mobile App ]      [ Web Browser ]
      \                   /
       v                 v
   +---------------------------+
   |        API Gateway        |  1. Central Authentication & JWT Validation
   |   (Spring Cloud Gateway)  |  2. SSL/TLS Termination
   |                           |  3. Rate Limiting & Throttling
   +---------------------------+  4. Dynamic Routing & Load Balancing
        /          |          \
       v           v           v
  [User Svc]  [Order Svc]  [Payment Svc]
    (JVM)        (JVM)         (JVM)
```

### Why Use an API Gateway?
1. **Centralized Authentication**: Validates JWT signatures once at the edge, injecting decoded user IDs (`X-User-Id: 42`) into internal downstream headers.
2. **Protocol Translation**: Accepts public REST/JSON from mobile clients and translates it to high-speed internal binary **gRPC** for backend services.
3. **Cross-Origin Resource Sharing (CORS)**: Solved centrally rather than in every microservice.
4. **Request Aggregation**: A single client request to `/home-feed` triggers parallel calls to User, Order, and Recommendation services, returning a unified response to reduce mobile battery drain.

---

## 2. Service Discovery & Registration

How does the API Gateway know the dynamic IP addresses of 20 dynamically scaling `OrderService` Docker containers?

```
1. Register on Startup
   [ Order Service Pod (10.0.1.45) ] ---> [ Service Registry (Consul / Eureka) ]
                                          Stores: "order-service" -> [10.0.1.45, 10.0.1.46]

2. Query & Route
   [ API Gateway ] ---> Queries Registry for "order-service" ---> Routes to 10.0.1.45
```

### Client-Side vs. Server-Side Discovery:
- **Client-Side (Spring Cloud Eureka)**: The caller queries the registry, caches the IP list, and executes client-side load balancing (via Spring Cloud LoadBalancer).
- **Server-Side (Kubernetes / AWS ALB)**: The client calls a DNS name (`http://order-service:8080`). The Kubernetes CoreDNS and `kube-proxy` handle internal routing transparently.

---

## 3. Communication Protocols: REST vs. gRPC

For internal communication between Java microservices, **gRPC** is standard at high scale:

```
REST over HTTP/1.1 (Text JSON)
+-------------------------------------------------------+
| HTTP/1.1 Header (Text)                                |
| Content-Type: application/json                        |
| Payload: { "userId": 101, "name": "Alice", ... }      |
+-------------------------------------------------------+
* Bulky text serialization, requires new TCP handshake or sequential head-of-line blocking.

gRPC over HTTP/2 (Binary Protocol Buffers)
+-------------------------------------------------------+
| HTTP/2 Binary Frame (Compressed Header)               |
| Stream ID: 1 | Binary Payload (Protobuf Encoded)      |
+-------------------------------------------------------+
* Compact binary encoding, multiplexed streams over a SINGLE long-lived TCP connection!
```

### Head-to-Head Comparison:

| Dimension | REST (JSON over HTTP/1.1) | gRPC (Protobuf over HTTP/2) |
| :--- | :--- | :--- |
| **Data Format** | Human-readable text JSON | Compact **Binary** Protocol Buffers |
| **Schema Contract** | Loose / Optional (OpenAPI/Swagger) | **Strict contract** (`.proto` file) |
| **Performance** | Slower (parsing strings into memory) | **$5\times\text{ to }10\times$ faster throughput** |
| **Multiplexing** | No (Head-of-line blocking per connection) | **Yes** (Hundreds of concurrent streams per connection) |
| **Streaming** | Limited (Server-Sent Events) | Bi-directional streaming supported natively |
| **Best Used For** | External public APIs, Browser clients | **Internal microservice-to-microservice RPC** |

### Defining a Protobuf Contract (`order.proto`):

```protobuf
syntax = "proto3";

option java_package = "com.example.grpc";
option java_multiple_files = true;

message OrderRequest {
    int64 orderId = 1;
    string customerId = 2;
}

message OrderResponse {
    int64 orderId = 1;
    string status = 2;
    double totalAmount = 3;
}

service OrderRpcService {
    rpc GetOrderDetails (OrderRequest) returns (OrderResponse);
}
```
*The `protoc` compiler automatically generates high-performance Java classes and non-blocking stubs.*

---

## 4. Distributed Tracing: Solving the Microservice Debugging Nightmare

When an API call traverses 10 different Java services over the network, standard single-server logs (`System.out.println`) become useless. 

**Distributed Tracing (OpenTelemetry / Zipkin)** solves this using **Trace IDs** and **Span IDs**:

```
Client Request (Trace ID: abc-123)
      |
      +---> [API Gateway]     (Span ID: 1, Trace ID: abc-123)
                 |
                 +---> [Order Svc]   (Span ID: 2, Parent Span: 1, Trace ID: abc-123)
                            |
                            +---> [Payment Svc] (Span ID: 3, Parent Span: 2, Trace ID: abc-123)
```

1. **Trace ID**: A globally unique identifier generated at the API Gateway and propagated in the HTTP headers (`traceparent`) across every downstream service.
2. **Span ID**: Represents an individual unit of work within a single service.
3. If an error occurs in `Payment Svc`, searching for `Trace ID: abc-123` in centralized logging (ELK / Grafana Loki) shows the exact end-to-end timeline and failure point.

---

## 5. Self-Check & Quick Review

1. **Q**: Why is HTTP/2 multiplexing in gRPC a major advantage over HTTP/1.1 in REST?
   - *A*: HTTP/1.1 requires a separate TCP connection per concurrent request or suffers from head-of-line blocking. HTTP/2 allows multiplexing hundreds of parallel bidirectional requests over a **single established TCP socket**.
2. **Q**: What is the difference between a Reverse Proxy and an API Gateway?
   - *A*: A **Reverse Proxy** (like Nginx) primarily handles low-level traffic routing, SSL termination, and caching. An **API Gateway** provides application-level logic like JWT validation, user rate-limiting, and request aggregation.
3. **Q**: What is the role of a Trace ID in distributed logging?
   - *A*: It links all log entries across multiple disparate microservices into a single, cohesive timeline for a single end-user request.

---

👉 **Next Up: [Page 10: The 4-Step System Design Interview Blueprint & Capacity Math](file:///Users/deadheaven07/Downloads/Prep_DSA_SystemDesign/system-design/system-design-fundamentals/10-interview-framework-and-math.md)**
