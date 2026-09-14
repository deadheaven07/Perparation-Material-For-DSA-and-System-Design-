# Page 8: Caching Strategies & Redis Integration

Welcome to Page 8 of the Java Backend Engineering series. The fastest database query is the one you never execute. Caching cuts API response times from $50\text{ ms}$ down to sub-millisecond speeds, protects databases from traffic spikes, and is a mandatory skill for senior backend interviews.

---

## 1. Caching Fundamentals: The Cache-Aside Pattern

In 90% of web backends, the **Cache-Aside (Lazy Loading)** pattern is the industry standard:

```
[ Client Request: GET /products/42 ]
                 |
                 v
       [ Check Redis Cache ]
                 |
        +--------+--------+
        |                 |
     (Hit)              (Miss)
        |                 |
  Return Cached           v
   Product JSON      [ Query Database (PostgreSQL) ]
  (< 1ms latency)         |
                          v
                    [ Store in Redis with TTL ]
                          |
                          v
                    Return Product JSON
```

---

## 2. Spring Cache Abstraction

Spring provides a declarative, annotation-based caching abstraction. You can swap cache providers (Caffeine for local in-memory, Redis for distributed production) without modifying your business code.

### Core Caching Annotations:

| Annotation | Purpose | Example |
| :--- | :--- | :--- |
| **`@Cacheable`** | Checks cache first. If found, returns it; if miss, executes method and stores result. | `@Cacheable(value = "products", key = "#id")` |
| **`@CachePut`** | **Always executes** the method and updates the cache with the new result. | `@CachePut(value = "products", key = "#product.id()")` |
| **`@CacheEvict`** | Removes an entry from the cache (e.g., when an item is deleted). | `@CacheEvict(value = "products", key = "#id")` |
| **`@Caching`** | Combines multiple cache operations on a single method. | Evicts item from both `"products"` and `"featured"` |

### Implementation in a Spring Service:

```java
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // 1. Read: Cached by product ID with SpEL key expression
    @Cacheable(value = "products", key = "#id", unless = "#result == null")
    public ProductResponse getProductById(Long id) {
        System.out.println("Cache Miss! Fetching product " + id + " from PostgreSQL...");
        return productRepository.findById(id)
                .map(p -> new ProductResponse(p.getId(), p.getName(), p.getPrice()))
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }

    // 2. Update: Always updates DB and synchronizes cache
    @CachePut(value = "products", key = "#id")
    public ProductResponse updateProduct(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id).orElseThrow();
        product.setPrice(request.price());
        Product saved = productRepository.save(product);
        return new ProductResponse(saved.getId(), saved.getName(), saved.getPrice());
    }

    // 3. Delete: Evicts key from Redis
    @CacheEvict(value = "products", key = "#id")
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
```

---

## 3. Distributed Caching with Redis & Spring Boot

For multi-instance, horizontally scaled applications, in-memory local caches (like Caffeine) cause data inconsistency between pods. A shared **Redis Cluster** is required.

### 1. Dependencies (`pom.xml`):
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

### 2. Production Redis Configuration (JSON Serialization & TTL):

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // Configure JSON serialization instead of default binary Java serialization
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(60)) // Default TTL = 1 hour
                .disableCachingNullValues()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                // Custom TTL for specific cache names:
                .withCacheConfiguration("products", config.entryTtl(Duration.ofHours(24)))
                .withCacheConfiguration("user_sessions", config.entryTtl(Duration.ofMinutes(30)))
                .build();
    }
}
```

---

## 4. Advanced Production Cache Traps & Solutions

```
Trap 1: Cache Avalanche               Trap 2: Cache Stampede (Thundering Herd)
10,000 keys expire simultaneously      A single hot key ("BlackFridayDeal") expires
              |                                        |
All requests hit DB at once            10,000 concurrent threads query DB at once
              v                                        v
         [ DB CRASH ]                             [ DB CRASH ]
```

### 1. Cache Avalanche
- **Problem**: Many keys set with the exact same expiration time (e.g., 3600 seconds) expire at the same second.
- **Solution**: Add **randomized jitter** to TTL values (`TTL = 3600 + Random(-300, 300) seconds`).

### 2. Cache Stampede (Thundering Herd)
- **Problem**: When a single ultra-popular key expires, thousands of concurrent threads experience a cache miss and execute the expensive DB query simultaneously.
- **Solution**: Use a **Distributed Lock (via Redisson)** so only one thread recomputes the cache while others wait:

```java
@Service
public class HotItemService {

    private final RedissonClient redisson;

    public HotItemService(RedissonClient redisson) {
        this.redisson = redisson;
    }

    public String getHotItemData(String itemId) {
        String cached = redisTemplate.opsForValue().get("item:" + itemId);
        if (cached != null) return cached;

        // Acquire distributed lock so only ONE pod queries the database
        RLock lock = redisson.getLock("lock:item:" + itemId);
        try {
            if (lock.tryLock(5, 10, TimeUnit.SECONDS)) {
                // Double-check cache (another thread might have populated it!)
                cached = redisTemplate.opsForValue().get("item:" + itemId);
                if (cached != null) return cached;

                String dbValue = database.fetchHeavyItem(itemId);
                redisTemplate.opsForValue().set("item:" + itemId, dbValue, 1, TimeUnit.HOURS);
                return dbValue;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (lock.isHeldByCurrentThread()) lock.unlock();
        }
        return redisTemplate.opsForValue().get("item:" + itemId);
    }
}
```

### 3. Cache Penetration
- **Problem**: Attackers query invalid IDs (`/products/-9999`) that exist in neither cache nor database, forcing a database lookup every time.
- **Solution**:
  1. Cache `null` results with a short TTL ($60\text{ seconds}$).
  2. Put a **Bloom Filter** (probabilistic data structure) in front of the cache to immediately reject non-existent IDs.

---

## 5. Self-Check & Quick Review

1. **Q**: What is the difference between `@Cacheable` and `@CachePut`?
   - *A*: `@Cacheable` checks the cache first and skips method execution on a cache hit. `@CachePut` **always executes the method** and updates the cache with the returned result.
2. **Q**: Why is default Java serialization discouraged for Redis caching?
   - *A*: Java native serialization produces fragile binary payloads that fail deserialization if classes change (`serialVersionUID` mismatch), and cannot be inspected or read by other non-Java microservices. JSON serialization is universal and human-readable.
3. **Q**: How does a Bloom Filter prevent Cache Penetration?
   - *A*: A Bloom filter quickly tells if an element **definitely does not exist** in the database in $O(1)$ time without querying Redis or PostgreSQL.

---

| ⬅️ Previous | 🏠 Course Index | ➡️ Next |
| :--- | :---: | ---: |
| [Page 7: Async & Virtual Threads](07-async-virtual-threads-and-concurrency.md) | [Java Backend Index](README.md) | [Page 9: Messaging & Apache Kafka](09-messaging-kafka-and-event-driven.md) |
