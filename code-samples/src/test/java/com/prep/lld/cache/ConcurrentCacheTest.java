package com.prep.lld.cache;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Concurrent In-Memory Cache Test Suite")
class ConcurrentCacheTest {

    @Test
    @DisplayName("LRU Eviction Policy correctly evicts least recently accessed entries")
    void testLruEviction() {
        try (ConcurrentCache<String, String> cache = new ConcurrentCache<>(2, new LRUEvictionPolicy<>())) {
            cache.put("A", "Alpha");
            cache.put("B", "Beta");

            // Access A to make B the LRU victim
            assertEquals(Optional.of("Alpha"), cache.get("A"));

            // Insert C; should trigger eviction of B
            cache.put("C", "Gamma");

            assertEquals(Optional.of("Alpha"), cache.get("A"));
            assertEquals(Optional.empty(), cache.get("B"), "B should have been evicted by LRU");
            assertEquals(Optional.of("Gamma"), cache.get("C"));
            assertEquals(2, cache.size());
        }
    }

    @Test
    @DisplayName("LFU Eviction Policy evicts least frequently accessed entries")
    void testLfuEviction() {
        try (ConcurrentCache<String, String> cache = new ConcurrentCache<>(3, new LFUEvictionPolicy<>())) {
            cache.put("X", "1");
            cache.put("Y", "2");
            cache.put("Z", "3");

            // Access X twice, Y once, Z zero times after put
            cache.get("X");
            cache.get("X");
            cache.get("Y");

            // Inserting W should evict Z (lowest frequency)
            cache.put("W", "4");

            assertEquals(Optional.of("1"), cache.get("X"));
            assertEquals(Optional.of("2"), cache.get("Y"));
            assertEquals(Optional.empty(), cache.get("Z"), "Z should have been evicted by LFU");
            assertEquals(Optional.of("4"), cache.get("W"));
        }
    }

    @Test
    @DisplayName("FIFO Eviction Policy evicts oldest inserted entries regardless of access")
    void testFifoEviction() {
        try (ConcurrentCache<String, String> cache = new ConcurrentCache<>(2, new FIFOEvictionPolicy<>())) {
            cache.put("K1", "V1");
            cache.put("K2", "V2");

            // Frequently read K1
            cache.get("K1");
            cache.get("K1");

            // Insert K3; K1 was inserted first, so FIFO must evict K1
            cache.put("K3", "V3");

            assertEquals(Optional.empty(), cache.get("K1"), "K1 must be evicted first in FIFO");
            assertEquals(Optional.of("V2"), cache.get("K2"));
            assertEquals(Optional.of("V3"), cache.get("K3"));
        }
    }

    @Test
    @DisplayName("TTL expiration invalidates stale entries")
    void testTtlExpiration() throws InterruptedException {
        try (ConcurrentCache<String, String> cache = new ConcurrentCache<>(5, new LRUEvictionPolicy<>(), 100L, 0L)) {
            cache.put("quickKey", "quickVal", 50L); // 50ms TTL

            assertEquals(Optional.of("quickVal"), cache.get("quickKey"));

            // Wait for entry to expire
            Thread.sleep(70L);

            assertEquals(Optional.empty(), cache.get("quickKey"), "Expired key must return empty on get");
            assertEquals(1, cache.getStats().expirations());
        }
    }

    @Test
    @DisplayName("High-concurrency read/write operations execute safely without race conditions")
    void testConcurrentReadWriteOperations() throws InterruptedException {
        int threads = 8;
        int operationsPerThread = 500;
        int cacheCapacity = 50;

        try (ConcurrentCache<Integer, String> cache = new ConcurrentCache<>(cacheCapacity, new LRUEvictionPolicy<>())) {
            ExecutorService pool = Executors.newFixedThreadPool(threads);
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch doneLatch = new CountDownLatch(threads);
            AtomicInteger putSuccessCount = new AtomicInteger();

            for (int i = 0; i < threads; i++) {
                final int threadId = i;
                pool.submit(() -> {
                    try {
                        startLatch.await();
                        for (int j = 0; j < operationsPerThread; j++) {
                            int key = (threadId * 100) + (j % 50);
                            cache.put(key, "Val-" + key);
                            putSuccessCount.incrementAndGet();
                            cache.get(key);
                        }
                    } catch (Exception e) {
                        fail("Concurrency error in thread " + threadId + ": " + e.getMessage());
                    } finally {
                        doneLatch.countDown();
                    }
                });
            }

            startLatch.countDown();
            assertTrue(doneLatch.await(15, TimeUnit.SECONDS), "Concurrent test must complete within 15 seconds");
            pool.shutdown();

            assertTrue(cache.size() <= cacheCapacity, "Cache size must not exceed capacity under concurrency");
            assertEquals(threads * operationsPerThread, putSuccessCount.get(), "All put operations must succeed");
        }
    }
}
