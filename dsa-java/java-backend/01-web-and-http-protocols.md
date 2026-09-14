# Page 1: Web & HTTP Protocols from Ground Zero

Welcome to Page 1 of the Java Backend Engineering series. Before writing a single line of backend Java code, you must understand the language of the internet: **HTTP (Hypertext Transfer Protocol)**. Every Java controller, REST endpoint, and microservice communicates using these fundamentals.

---

## 1. The Client-Server Request-Response Lifecycle

When a client (browser or mobile app) sends an HTTP request to `https://api.example.com/users/42`, the following sequence takes place over the network:

```
[ Browser / Mobile Client ]
            |
            | 1. DNS Lookup (Finds IP address: 198.51.100.1)
            v
       [ DNS Server ]
            |
            | 2. TCP 3-Way Handshake (SYN -> SYN-ACK -> ACK)
            | 3. TLS / SSL Negotiation (Encrypts socket connection)
            v
  [ Reverse Proxy / Load Balancer (Nginx / ALB) ]
            |
            | 4. Forwards raw HTTP byte stream
            v
  [ Java Backend (Tomcat / Spring Boot on port 8080) ]
            |
            | 5. Deserializes HTTP bytes -> HttpServletRequest object
            v
  [ UserController.java ]
```

---

## 2. Anatomy of an HTTP Request and Response

### 1. The HTTP Request Packet

```http
POST /api/v1/orders HTTP/1.1
Host: api.example.com
User-Agent: Mozilla/5.0 (iPhone)
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Accept: application/json
Content-Length: 48

{
  "productId": 101,
  "quantity": 2,
  "coupon": "SAVE20"
}
```

- **Request Line**: `POST` (Method), `/api/v1/orders` (Path / URI), `HTTP/1.1` (Protocol version).
- **Headers**: Key-value metadata providing instructions about content type, authentication, and caching.
- **Body**: The payload data (usually JSON) being sent to the backend.

### 2. The HTTP Response Packet

```http
HTTP/1.1 201 Created
Date: Tue, 15 Sep 2026 03:55:00 GMT
Content-Type: application/json
Content-Length: 65
Connection: keep-alive

{
  "orderId": 98765,
  "status": "CONFIRMED",
  "totalAmount": 79.99
}
```

- **Status Line**: `HTTP/1.1` (Protocol), `201` (Status Code), `Created` (Reason Phrase).
- **Headers**: Metadata describing the response payload.
- **Body**: The data returned to the client.

---

## 3. HTTP Methods, Safety & Idempotency

Understanding **Safety** and **Idempotency** is one of the most frequently asked backend interview topics.

- **Safe**: Calling the endpoint **does not modify** any state on the server (read-only).
- **Idempotent**: Making the same request **multiple times** produces the exact same server state as making it once. ($f(f(x)) = f(x)$).

| Method | Typical Action | Safe? | Idempotent? | Request Body Allowed? |
| :--- | :--- | :---: | :---: | :---: |
| **GET** | Retrieve a resource (`/users/42`) | **YES** | **YES** | No (discouraged) |
| **POST** | Create a new resource (`/users`) | NO | NO | **YES** |
| **PUT** | Replace an existing resource completely (`/users/42`) | NO | **YES** | **YES** |
| **PATCH** | Partially update a resource (`/users/42`) | NO | NO (typically) | **YES** |
| **DELETE** | Delete a resource (`/users/42`) | NO | **YES** | Optional |
| **OPTIONS** | Inquire about supported HTTP methods (CORS preflight) | **YES** | **YES** | No |
| **HEAD** | Same as GET, but returns headers only (no body) | **YES** | **YES** | No |

> [!TIP]
> **Why is DELETE Idempotent?**
> If you call `DELETE /users/42`:
> - First call: User 42 is deleted from database. Server state = User 42 does not exist.
> - Second call: User 42 is already deleted. Server state = User 42 does not exist (unchanged!).
> Even if the status code changes from `200/204` to `404`, the **server state** is identical. Hence, `DELETE` is idempotent.

---

## 4. HTTP Status Codes: The Standard Categories

Status codes allow clients to programmatically handle responses without parsing the body text:

```
                          HTTP Status Codes
           +---------+---------+---------+---------+
           |         |         |         |         |
          2xx       3xx       4xx       5xx       1xx
       (Success) (Redirect) (Client)  (Server)  (Informational)
```

### 1. 2xx: Success
- **`200 OK`**: Standard success for GET, PUT, PATCH.
- **`201 Created`**: Resource successfully created (standard response for POST); should include a `Location` header.
- **`204 No Content`**: Request succeeded, but there is no body to return (common for DELETE operations).

### 2. 3xx: Redirection
- **`301 Moved Permanently`**: Resource relocated to a new URL; browsers cache this redirect permanently.
- **`302 Found (Temporary Redirect)`**: Request should be repeated with another URI temporarily.
- **`304 Not Modified`**: Tells client/browser to load resource from its local cache (E-Tag match).

### 3. 4xx: Client Error (The Client Made a Mistake)
- **`400 Bad Request`**: Malformed JSON or invalid parameters.
- **`401 Unauthorized`**: Authentication is missing or invalid (User is not logged in / missing token).
- **`403 Forbidden`**: Authenticated, but user lacks permissions (e.g., normal user trying to access admin dashboard).
- **`404 Not Found`**: The requested resource does not exist.
- **`409 Conflict`**: Request conflicts with current server state (e.g., duplicate email registration).
- **`422 Unprocessable Entity`**: Syntactically valid JSON, but fails validation (e.g., age is negative).
- **`429 Too Many Requests`**: Rate limit exceeded (client throttled).

### 4. 5xx: Server Error (The Backend Made a Mistake)
- **`500 Internal Server Error`**: Unhandled exception in Java code (e.g., `NullPointerException`).
- **`502 Bad Gateway`**: Nginx/Load balancer received an invalid response from upstream Java app server.
- **`503 Service Unavailable`**: Server is overloaded or down for maintenance.
- **`504 Gateway Timeout`**: Upstream service (e.g., payment microservice) took too long to reply.

---

## 5. REST Architectural Constraints (Roy Fielding)

A web service is truly **RESTful** (Representational State Transfer) if it adheres to these 6 constraints:

1. **Client-Server Separation**: The user interface concerns are separated from data storage and business logic concerns.
2. **Statelessness**: Every request from client to server must contain **all** the information necessary to understand and process the request. The server stores no client context across requests.
3. **Cacheability**: Responses must explicitly define themselves as cacheable or non-cacheable (`Cache-Control: max-age=3600`).
4. **Uniform Interface**: Resources are identified by stable URIs (`/api/v1/products/5`), using standard HTTP methods.
5. **Layered System**: The client cannot tell whether it is connected directly to the end server, or to an intermediary proxy or load balancer.
6. **Code-on-Demand (Optional)**: Servers can temporarily extend client functionality by transferring executable code (e.g., JavaScript).

---

## 6. State Management: Cookies vs. Sessions vs. Stateless JWT

How does the backend know who you are across requests?

```
Pattern 1: Server-Side Sessions (Stateful)
Client ---> [Login] ---> Server creates Session in memory (ID: s-1234)
                    <--- Sets Cookie: Set-Cookie: JSESSIONID=s-1234
Client ---> [GET /profile] (Sends Cookie: JSESSIONID=s-1234)
* Problem: If Server 1 crashes or traffic routes to Server 2, session is LOST unless Redis is used!

Pattern 2: JSON Web Tokens (Stateless - Modern Standard)
Client ---> [Login] ---> Server signs Token: Header.Payload.Signature
                    <--- Returns Token: { "token": "eyJhbGciOi..." }
Client ---> [GET /profile] (Header: Authorization: Bearer eyJhbGciOi...)
* Advantage: ANY Java backend instance can verify the signature mathematically without DB lookups!
```

| Dimension | Cookies | Server Session (`JSESSIONID`) | JSON Web Token (JWT) |
| :--- | :--- | :--- | :--- |
| **Where Stored** | Client Browser | Server RAM / Redis | Client Browser (Local/Memory) |
| **Size Limit** | $4\text{ KB}$ | Unlimited (server RAM) | Typically $1\text{–}2\text{ KB}$ |
| **Scalability** | High | Hard (requires sticky sessions or Redis) | **Infinite horizontal scalability** |
| **Best Used For** | Simple web UI tracking | Legacy Monolith apps | **Modern REST APIs & Microservices** |

---

## 7. Self-Check & Quick Review

1. **Q**: What is the difference between `401 Unauthorized` and `403 Forbidden`?
   - *A*: `401` means **unauthenticated** (the server doesn't know who you are; please log in). `403` means **unauthorized** (the server knows who you are, but you don't have permission to perform this action).
2. **Q**: Why is `PUT` idempotent while `POST` is not?
   - *A*: `PUT /users/42` updates user 42 to the exact specified payload. Calling it 10 times results in the same user record. `POST /users` creates a new record each time, resulting in 10 separate user rows in the database.
3. **Q**: Why does a preflight `OPTIONS` request happen in web browsers?
   - *A*: Cross-Origin Resource Sharing (CORS). Browsers send an `OPTIONS` request first to verify with the backend whether the calling origin is allowed to send custom headers or methods before sending the actual payload.

---

## 🧭 Continue Learning

| ◀️ Previous Topic | 🧭 Track Hub | Next Topic ▶️ |
| :--- | :---: | ---: |
| [**Page 0: Roadmap & Prerequisites**](00-java-backend-roadmap-and-prerequisites.md)<br><sub>*Architecture & 3-Tier Mental Model*</sub> | [**Java Backend Index**](README.md)<br><sub>*Curriculum & Architecture*</sub> | [**Page 2: Servlets & Spring MVC**](02-servlet-containers-and-spring-mvc.md)<br><sub>*Tomcat, DispatcherServlet & Filters*</sub> |
