package com.prep.lld.ratelimiter;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Token Bucket Rate Limiter with sub-millisecond nanosecond refill precision.
 * Guarantees thread safety per-client using fine-grained synchronization.
 */
public class TokenBucketRateLimiter implements RateLimiter {

    private static class Bucket {
        private final long capacity;
        private final double refillRatePerSecond;
        private double tokens;
        private long lastRefillNanoTime;

        Bucket(long capacity, double refillRatePerSecond) {
            this.capacity = capacity;
            this.refillRatePerSecond = refillRatePerSecond;
            this.tokens = capacity;
            this.lastRefillNanoTime = System.nanoTime();
        }

        synchronized boolean tryConsume(int permits) {
            long now = System.nanoTime();
            long elapsedNanos = now - lastRefillNanoTime;
            if (elapsedNanos > 0) {
                double addedTokens = (elapsedNanos / 1_000_000_000.0) * refillRatePerSecond;
                this.tokens = Math.min(capacity, this.tokens + addedTokens);
                this.lastRefillNanoTime = now;
            }

            if (this.tokens >= permits) {
                this.tokens -= permits;
                return true;
            }
            return false;
        }

        synchronized void reset() {
            this.tokens = capacity;
            this.lastRefillNanoTime = System.nanoTime();
        }
    }

    private final long capacity;
    private final double refillRatePerSecond;
    private final ConcurrentHashMap<String, Bucket> clientBuckets = new ConcurrentHashMap<>();

    public TokenBucketRateLimiter(long capacity, double refillRatePerSecond) {
        if (capacity <= 0 || refillRatePerSecond <= 0) {
            throw new IllegalArgumentException("Capacity and refill rate must be positive values.");
        }
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
    }

    @Override
    public boolean tryAcquire(String clientId, int permits) {
        if (permits <= 0) {
            throw new IllegalArgumentException("Permits must be positive: " + permits);
        }
        Bucket bucket = clientBuckets.computeIfAbsent(clientId, k -> new Bucket(capacity, refillRatePerSecond));
        return bucket.tryConsume(permits);
    }

    @Override
    public void reset(String clientId) {
        Bucket bucket = clientBuckets.get(clientId);
        if (bucket != null) {
            bucket.reset();
        }
    }
}
