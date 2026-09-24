package com.prep.lld.ratelimiter;

/**
 * Factory for creating configured RateLimiter instances.
 */
public final class RateLimiterFactory {

    private RateLimiterFactory() {
        // Prevent instantiation
    }

    public static RateLimiter createTokenBucket(long capacity, double refillRatePerSecond) {
        return new TokenBucketRateLimiter(capacity, refillRatePerSecond);
    }

    public static RateLimiter createSlidingWindow(int maxRequests, long windowDurationMs) {
        return new SlidingWindowCounterRateLimiter(maxRequests, windowDurationMs);
    }
}
