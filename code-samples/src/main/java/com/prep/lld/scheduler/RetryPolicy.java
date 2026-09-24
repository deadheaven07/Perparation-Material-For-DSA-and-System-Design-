package com.prep.lld.scheduler;

/**
 * Encapsulates retry behavior with configurable exponential backoff and maximum attempts.
 */
public record RetryPolicy(int maxRetries, long initialDelayMs, double backoffMultiplier) {

    public static RetryPolicy noRetry() {
        return new RetryPolicy(0, 0L, 1.0);
    }

    public static RetryPolicy defaultExponentialBackoff() {
        return new RetryPolicy(3, 500L, 2.0);
    }

    public long calculateDelayMs(int currentAttempt) {
        if (currentAttempt <= 0 || maxRetries <= 0) {
            return 0L;
        }
        return (long) (initialDelayMs * Math.pow(backoffMultiplier, currentAttempt - 1));
    }
}
