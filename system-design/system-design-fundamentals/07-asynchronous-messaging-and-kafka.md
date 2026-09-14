# Page 7: Asynchronous Messaging & Event Streaming (Kafka) in Java

Welcome to Page 7 of the System Design Fundamentals series. In distributed architectures, synchronous REST calls couple systems tightly and make them prone to cascading outages. Asynchronous messaging decouples microservices, smooths out traffic spikes, and enables high-throughput event processing.

---

## 1. Synchronous vs. Asynchronous Communication

```
Synchronous (REST / gRPC)                   Asynchronous (Message Broker / Kafka)
Client ---> [Service A] ---> [Service B]    Client ---> [Service A] ---> [Message Queue]
             (Waits...)       (Waits...)                 (Acks immediately)       |
                                                                                  v
                                                                             [Service B]
                                                                          (Processes at own pace)
```

| Dimension | Synchronous (REST / gRPC) | Asynchronous (Kafka / RabbitMQ) |
| :--- | :--- | :--- |
| **Coupling** | Tightly coupled (Service A requires Service B up) | Decoupled (Service A succeeds even if Service B is down) |
| **Latency** | Cumulative ($T_A + T_B + T_C$) | Immediate acknowledgment to client |
| **Traffic Bursts** | Risk of crashing downstream service | **Traffic Buffering / Peak Shaving** |
| **Best For** | Querying real-time state (login, checkout price) | Long-running tasks, notifications, audit logs |

---

## 2. Message Queues (RabbitMQ) vs. Event Streams (Kafka)

```
RabbitMQ (Smart Broker, Dumb Consumer)
Producer ---> [ Queue ] ---> Consumer 1 (Worker receives message)
                         \-> Consumer 2
* Messages are DELETED once acknowledged.

Apache Kafka (Dumb Broker, Smart Consumer)
Producer ---> [ Partition Log: 0 | 1 | 2 | 3 | 4 | 5 ] (Append-Only Disk Log)
                              ^                   ^
                        Consumer Group B     Consumer Group A
* Messages are IMMUTABLE & PERSISTENT. Multiple consumer groups read at different offsets!
```

| Feature | Message Queue (RabbitMQ, AWS SQS) | Event Stream (Apache Kafka) |
| :--- | :--- | :--- |
| **Message Lifecycle** | Deleted after successful consumer ACK | Retained on disk according to retention policy (e.g., 7 days) |
| **Replayability** | No (cannot re-read historical messages) | **Yes** (consumers can rewind offsets to replay data) |
| **Ordering** | FIFO within queue, but hard across workers | **Strict FIFO ordering within a single partition** |
| **Throughput** | $\approx 20,000\text{ to } 50,000\text{ msg/sec}$ | **$1,000,000+\text{ msg/sec}$** (Zero-Copy OS page cache) |

---

## 3. Apache Kafka Architecture & The Partition Rule

1. **Topic**: A logical category/stream of events (e.g., `order-events`).
2. **Partition**: The physical unit of parallelism on disk. Topics are split into multiple partitions distributed across Kafka broker nodes.
3. **Consumer Group**: A group of cooperating consumers.

> [!IMPORTANT]
> **The Golden Partition Rule of Kafka:**
> Each partition inside a topic can be consumed by **at most one consumer instance** within the same consumer group!
> - If you have $4\text{ partitions}$ and $4\text{ consumers}$, each consumer reads 1 partition.
> - If you add a $5^{\text{th}}\text{ consumer}$, it will sit **idle**!
> **To increase consumer throughput, you must increase the number of partitions.**

---

## 4. Java Kafka Producer & Consumer Implementation

### 1. High-Performance Java Producer

```java
import org.apache.kafka.clients.producer.*;
import java.util.Properties;

public class OrderProducer {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        // Safe & Reliable Producer Settings:
        props.put("acks", "all"); // Waits for all replica brokers to acknowledge
        props.put("retries", 3);
        props.put("enable.idempotence", "true"); // Guarantees exactly-once send per producer session

        Producer<String, String> producer = new KafkaProducer<>(props);

        // Key = "user-123": All events for user-123 go to the same partition (guarantees ordering!)
        ProducerRecord<String, String> record = 
            new ProducerRecord<>("order-events", "user-123", "{ 'orderId': 99, 'status': 'PAID' }");

        producer.send(record, (metadata, exception) -> {
            if (exception == null) {
                System.out.println("Message sent to partition: " + metadata.partition() + 
                                   " offset: " + metadata.offset());
            } else {
                exception.printStackTrace();
            }
        });

        producer.close();
    }
}
```

### 2. Resilient Java Consumer with Manual Commit

```java
import org.apache.kafka.clients.consumer.*;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class OrderConsumer {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "order-processing-group");
        props.put("enable.auto.commit", "false"); // Manual commit prevents data loss!
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("order-events"));

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
                // 1. Process business logic
                processOrder(record.value());
            }
            // 2. Commit offset ONLY after successful processing (At-Least-Once delivery)
            consumer.commitSync();
        }
    }

    private static void processOrder(String orderJson) {
        System.out.println("Processing: " + orderJson);
    }
}
```

---

## 5. Delivery Semantics & Idempotent Processing

In distributed systems, network partitions make duplicate messages inevitable:

```
At-Least-Once Delivery = Message is guaranteed delivered, but duplicates may occur!
```

### How to Guarantee Idempotency in Java:
Because consumers might receive the same message twice (e.g., consumer crashes after processing but before committing offset):
1. Include an `idempotencyKey` (e.g., `orderId` or UUID) in every message.
2. In the consumer database, store processed keys in a `processed_messages` table with a **UNIQUE constraint**:

```sql
INSERT INTO processed_messages (message_id, processed_at) VALUES ('msg-uuid-123', NOW());
-- If msg-uuid-123 already exists, DB throws duplicate key error; consumer safely ignores it!
```

### Dead Letter Queue (DLQ):
If a message cannot be processed after multiple retries (due to corrupt JSON or unexpected bugs — a "poison pill"), it is routed to a **Dead Letter Queue (DLQ)** topic for offline inspection, preventing the consumer group from hanging forever.

---

## 6. Self-Check & Quick Review

1. **Q**: Why does Apache Kafka maintain high throughput on spinning hard drives?
   - *A*: Kafka uses **sequential disk append operations** (as fast as sequential RAM access), relies heavily on OS **Page Cache**, and transfers data via **Zero-Copy (`sendfile`)** bypassing JVM user-space memory.
2. **Q**: How do you guarantee the strict ordering of customer transactions in Kafka?
   - *A*: By using the `customerId` as the message **key**. Kafka hashes the key so that all messages with the same key always route to the **same partition**.
3. **Q**: What happens if `enable.auto.commit` is set to `true` in a Java Kafka consumer?
   - *A*: The consumer periodically commits offsets in the background regardless of whether the business processing succeeded. If the worker crashes mid-task, messages are lost forever.

---

## 🧭 Continue Learning

| ◀️ Previous Topic | 🧭 Track Hub | Next Topic ▶️ |
| :--- | :---: | ---: |
| [**Page 6: Caching & LRU in Java**](06-caching-strategies-and-lru.md)<br><sub>*Cache-Aside, LinkedHashMap LRU & Redis*</sub> | [**System Design Index**](README.md)<br><sub>*Architecture, LLD & Scalability*</sub> | [**Page 8: Circuit Breakers & Rate Limiters**](08-resilience-circuit-breaker-rate-limiting.md)<br><sub>*Resilience4j, Token Bucket & Exponential Jitter*</sub> |
