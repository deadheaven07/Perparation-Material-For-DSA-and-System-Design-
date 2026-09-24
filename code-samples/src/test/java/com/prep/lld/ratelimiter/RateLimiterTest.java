package com.prep.lld.ratelimiter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Rate Limiter Algorithm Test Suite")
class RateLimiterTest {

    @Test
    @DisplayName("Token Bucket permits burst up to capacity and rejects excess requests")
    void testTokenBucketBurstAndRejection() {
        // Capacity: 5 tokens, refill: 1 token/sec
        RateLimiter limiter = RateLimiterFactory.createTokenBucket(5, 1.0);
        String client = "client-alpha";

        // First 5 requests must pass
        for (int i = 0; i < 5; i++) {
            assertTrue(limiter.tryAcquire(client), "Request " + (i + 1) + " within capacity must succeed");
        }

        // 6th request immediately after must be rejected
        assertFalse(limiter.tryAcquire(client), "6th request without refill delay must be rejected");
    }

    @Test
    @DisplayName("Sliding Window Counter strictly enforces limits across sliding window duration")
    void testSlidingWindowRateLimiting() throws InterruptedException {
        // Limit: 3 requests per 200 milliseconds
        RateLimiter limiter = RateLimiterFactory.createSlidingWindow(3, 200L);
        String client = "client-beta";

        assertTrue(limiter.tryAcquire(client));
        assertTrue(limiter.tryAcquire(client));
        assertTrue(limiter.tryAcquire(client));

        // 4th request in the same window must fail
        assertFalse(limiter.tryAcquire(client), "Exceeding window limit must reject");

        // Wait for window to slide past
        Thread.sleep(220L);

        // Should now be permitted again
        assertTrue(limiter.tryAcquire(client), "Request after window slide must succeed");
    }

    @Test
    @DisplayName("Concurrent requests strictly adhere to capacity under high thread contention")
    void testConcurrentRateLimiterContention() throws InterruptedException {
        int capacity = 20;
        RateLimiter limiter = RateLimiterFactory.createTokenBucket(capacity, 0.001); // Negligible refill during test
        String client = "shared-service-key";

        int totalThreads = 50;
        ExecutorService pool = Executors.newFixedThreadPool(totalThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(totalThreads);
        AtomicInteger successCounter = new AtomicInteger();
        AtomicInteger rejectedCounter = new AtomicInteger();

        for (int i = 0; i < totalThreads; i++) {
            pool.submit(() -> {
                try {
                    startLatch.await();
                    if (limiter.tryAcquire(client)) {
                        successCounter.incrementAndGet();
                    } else {
                        rejectedCounter.incrementAndGet();
                    }
                } catch (Exception e) {
                    fail("Unexpected concurrency exception: " + e.getMessage());
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        assertTrue(doneLatch.await(3, TimeUnit.SECONDS));
        pool.shutdown();

        assertEquals(capacity, successCounter.get(), "Exactly 'capacity' requests must succeed");
        assertEquals(totalThreads - capacity, rejectedCounter.get(), "Excess requests must be rejected");
    }
}
