# Page 10: The 4-Step System Design Interview Blueprint & Capacity Math

Welcome to Page 10 of the System Design Fundamentals series. Knowing architecture concepts is only half the battle; knowing **how to run a 45-minute system design interview** is what earns offers at top tech companies.

---

## 1. The 45-Minute Interview Timeline

```text
╭─────────────────────────────────────────────────────────────────────────────╮
│                 The 45-Minute System Design Interview Timeline              │
├─────────────────┬───────────────────────────────────────────────────────────┤
│ [00:00 - 05:00] │ 🎯 Step 1: Clarify Requirements & Scope (Functional & NFR)│
│ [05:00 - 12:00] │ 🧮 Step 2: Capacity Estimation (QPS, Storage, Bandwidth)  │
│ [12:00 - 25:00] │ 🏛️ Step 3: High-Level Architecture & Core API Design      │
│ [25:00 - 42:00] │ 🔍 Step 4: Deep Dive Bottlenecks, Caches, DB Sharding, GC │
│ [42:00 - 45:00] │ 🏁 Wrap Up & Candidate Questions                          │
╰─────────────────┴───────────────────────────────────────────────────────────╯
```

---

## 2. Step 1: Clarify Requirements (5 Mins)

Never jump straight into drawing boxes. Ask questions to establish bounds:

### 1. Functional Requirements (What does the user do?)
- Focus strictly on **2 to 3 core features**. (e.g., For TinyURL: 1. Generate short URL from long URL, 2. Redirect short URL to long URL).
- Explicitly state out-of-scope items (e.g., custom alias editing, user analytics).

### 2. Non-Functional Requirements (System Quality Attributes)
- **High Availability vs. Consistency**: Is 99.99% uptime required, or must data be strictly consistent immediately (CAP theorem)?
- **Latency**: Sub-millisecond reads ($p99 < 50\text{ ms}$)?
- **Read-to-Write Ratio**: Is it read-heavy (Twitter: 100:1) or write-heavy (IoT sensor logs: 1:10)?

---

## 3. Step 2: Capacity Estimation & Back-of-the-Envelope Math (7 Mins)

### The Essential Cheat Sheet for Interview Math:

$$\text{Seconds in a day} = 24 \times 60 \times 60 = 86,400 \approx \mathbf{100,000\text{ seconds}}$$

| Scale | Daily Requests | Average QPS (Queries Per Second) | Peak QPS ($2\times$) |
| :--- | :--- | :--- | :--- |
| **1 Million** | $10^6$ | $10^6 / 10^5 = \mathbf{10\text{ QPS}}$ | $20\text{ QPS}$ |
| **100 Million** | $10^8$ | $10^8 / 10^5 = \mathbf{1,000\text{ QPS}}$ | $2,000\text{ QPS}$ |
| **1 Billion** | $10^9$ | $10^9 / 10^5 = \mathbf{10,000\text{ QPS}}$ | $20,000\text{ QPS}$ |

### Data Storage Units:
- $1\text{ Byte} = 8\text{ bits}$
- $1\text{ KB} = 1,024\text{ Bytes} \approx 10^3\text{ Bytes}$
- $1\text{ MB} = 10^6\text{ Bytes}$
- $1\text{ GB} = 10^9\text{ Bytes}$
- $1\text{ TB} = 10^{12}\text{ Bytes}$
- $1\text{ PB} = 10^{15}\text{ Bytes}$

---

## 4. Worked Example: TinyURL (URL Shortener) Capacity Math

### Assumptions:
- **Daily Active Users (DAU)**: $100\text{ Million}$ URL shortening requests per month $\approx 3.3\text{ Million writes/day}$.
- **Read-to-Write Ratio**: $10:1$ (Reads: $33\text{ Million reads/day}$).

### 1. Throughput (QPS) Calculations:
$$\text{Write QPS} = \frac{3,300,000}{100,000} \approx \mathbf{33\text{ writes/sec}} \quad (\text{Peak} \approx 70\text{ writes/sec})$$
$$\text{Read QPS} = \frac{33,000,000}{100,000} \approx \mathbf{330\text{ reads/sec}} \quad (\text{Peak} \approx 660\text{ reads/sec})$$

### 2. Storage Estimation (5 Years):
- Each record stores:
  - `short_key`: 7 bytes (Base62)
  - `long_url`: 500 bytes
  - `created_at`: 8 bytes
  - Total $\approx 500\text{ bytes per URL entry}$.
$$\text{Storage per year} = 3.3\text{M} \times 365 \times 500\text{ bytes} \approx 1.2\text{B} \times 500\text{ bytes} \approx 600\text{ GB/year}$$
$$\text{5-Year Storage} = 600\text{ GB} \times 5 = \mathbf{3\text{ TB}}$$
*(A single commodity database drive can easily store 3 TB!)*

### 3. Memory / Cache Estimation (80/20 Rule):
We cache $20\%$ of daily read requests in Redis:
$$\text{Daily Read Volume} = 33\text{ Million} \times 500\text{ bytes} \approx 16.5\text{ GB/day}$$
$$\text{Cache Size (20\%)} = 0.20 \times 16.5\text{ GB} \approx \mathbf{3.3\text{ GB RAM}}$$
*(A single modest Redis instance with 8 GB RAM easily holds the working set!)*

---

## 5. Step 3: High-Level Architecture Diagram (15 Mins)

Draw the end-to-end flow clearly:

```
[ Client ]
    |
    v
[ Load Balancer ]
    |
    v
[ Java API Service (Spring Boot) ]
    |
    +-----> [ Redis Cache ] (Checks for short_key -> long_url)
    |             | (Cache Miss)
    |             v
    +-----> [ PostgreSQL / NoSQL DB ] (Reads/Writes persistent record)
```

### Core APIs:
1. `POST /api/v1/urls`
   - Request: `{ "longUrl": "https://example.com/very/long/path" }`
   - Response: `{ "shortUrl": "https://tiny.url/aBc123z" }`
2. `GET /{shortKey}`
   - Response: `HTTP 302 (Found / Temporary Redirect)` with header `Location: https://example.com/very/long/path`

> [!TIP]
> **HTTP 301 vs. HTTP 302 in URL Shortening:**
> - **301 (Permanent Redirect)**: The browser caches the redirect locally; subsequent requests go directly to the destination without hitting our server (saves bandwidth, but prevents click tracking).
> - **302 (Temporary Redirect)**: Every click routes through our server first, allowing us to track analytics and click counts.

---

## 6. Step 4: Deep Dive & Bottlenecks (15 Mins)

This is where you showcase deep engineering expertise:

### How to Generate the 7-Character Short Key?
Using **Base62 encoding** (`[a-z, A-Z, 0-9]`), a 7-character string provides:
$$62^7 \approx \mathbf{3.5\text{ Trillion unique URLs}}$$

### Handling Key Generation Collisions:
1. **Hashing (MD5 / SHA-256)**: Take first 7 characters. Problem: Hash collisions require appending salt and retrying.
2. **Dedicated Token / Range Counter Service (Optimal)**:
   - A distributed counter service allocates batches of integers (e.g., JVM node 1 gets range $1\text{ to }1,000,000$; node 2 gets $1,000,001\text{ to }2,000,000$).
   - The Java node converts the unique 64-bit integer into Base62:
     ```java
     public class Base62Encoder {
         private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
         public static String encode(long num) {
             StringBuilder sb = new StringBuilder();
             while (num > 0) {
                 sb.append(ALPHABET.charAt((int) (num % 62)));
                 num /= 62;
             }
             return sb.reverse().toString();
         }
     }
     ```
   - **Zero collisions guaranteed, $O(1)$ generation speed, no database lookups needed!**

---

## 7. Self-Check & Quick Review

1. **Q**: What is the rough formula to convert daily requests to Queries Per Second (QPS)?
   - *A*: Divide total daily requests by $100,000$ (e.g., $10\text{ Million daily requests} / 100,000 \approx 100\text{ QPS}$).
2. **Q**: Why should you never say "I will use MongoDB because it's web-scale" in an interview?
   - *A*: Always ground database choices in access patterns: "We need simple key-value lookups with high read volume and horizontal partitioning, making a key-value store like DynamoDB or Redis well-suited."
3. **Q**: What does the 80/20 rule dictate for cache capacity estimation?
   - *A*: $20\%$ of the daily read traffic volume should be sized into RAM to absorb $80\%$ of all incoming read queries.

---

## 🧭 Continue Learning

| ◀️ Previous Topic | 🧭 Track Hub | 🚀 Next Track |
| :--- | :---: | ---: |
| [**Page 9: Microservices & API Gateways**](09-microservices-and-api-gateways.md)<br><sub>*API Gateway, Service Discovery & gRPC*</sub> | [**System Design Index**](README.md)<br><sub>*Architecture, LLD & Scalability*</sub> | [**Java Backend Engineering Course**](../../dsa-java/java-backend/README.md)<br><sub>*Spring Boot, Persistence & Microservices*</sub> |
