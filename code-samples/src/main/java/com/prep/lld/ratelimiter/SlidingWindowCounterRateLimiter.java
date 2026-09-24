package com.prep.lld.ratelimiter;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Sliding Window Log Rate Limiter guaranteeing 100% boundary precision.
 * Eliminates 2x burst anomalies at fixed window borders.
 */
public class SlidingWindowCounterRateLimiter implements RateLimiter {

    private static class ClientWindow {
        private final Deque<Long> requestTimestamps = new ArrayDeque<>();

        synchronized boolean tryConsume(int permits, long windowDurationMs, int maxRequests) {
            long now = System.currentTimeMillis();
            long cutoff = now - windowDurationMs;

            // Evict timestamps outside active sliding window
            while (!requestTimestamps.isEmpty() && requestTimestamps.peekFirst() <= cutoff) {
                requestTimestamps.pollFirst();
            }

            if (requestTimestamps.size() + permits <= maxRequests) {
                for (int i = 0; i < permits; i++) {
                    requestTimestamps.addLast(now);
                }
                return true;
            }
            return false;
        }

        synchronized void reset() {
            requestTimestamps.clear();
        }
    }

    private final long windowDurationMs;
    private final int maxRequests;
    private final ConcurrentHashMap<String, ClientWindow> clientWindows = new ConcurrentHashMap<>();

    public SlidingWindowCounterRateLimiter(int maxRequests, long windowDurationMs) {
        if (maxRequests <= 0 || windowDurationMs <= 0) {
            throw new IllegalArgumentException("maxRequests and windowDurationMs must be positive.");
        }
        this.maxRequests = maxRequests;
        this.windowDurationMs = windowDurationMs;
    }

    @Override
    public boolean tryAcquire(String clientId, int permits) {
        if (permits <= 0) {
            throw new IllegalArgumentException("Permits must be positive: " + permits);
        }
        ClientWindow window = clientWindows.computeIfAbsent(clientId, k -> new ClientWindow());
        return window.tryConsume(permits, windowDurationMs, maxRequests);
    }

    @Override
    public void reset(String clientId) {
        ClientWindow window = clientWindows.get(clientId);
        if (window != null) {
            window.reset();
        }
    }
}
