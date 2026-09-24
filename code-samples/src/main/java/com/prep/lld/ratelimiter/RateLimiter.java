package com.prep.lld.ratelimiter;

/**
 * High-performance, thread-safe in-memory rate limiter contract.
 */
public interface RateLimiter {

    /**
     * Attempts to acquire 1 permit for the given client identifier.
     *
     * @param clientId Unique client / IP / API key identifier
     * @return true if permitted, false if rate limit exceeded
     */
    default boolean tryAcquire(String clientId) {
        return tryAcquire(clientId, 1);
    }

    /**
     * Attempts to acquire the specified number of permits for the client identifier.
     *
     * @param clientId Unique client identifier
     * @param permits  Number of tokens / units required
     * @return true if permitted, false if rejected
     */
    boolean tryAcquire(String clientId, int permits);

    /**
     * Resets rate-limiting state for the given client.
     *
     * @param clientId Unique client identifier
     */
    void reset(String clientId);
}
