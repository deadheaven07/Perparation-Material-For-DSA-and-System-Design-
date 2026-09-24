package com.prep.lld.cache;

import java.util.Objects;

/**
 * Encapsulates a cached entry with TTL expiration metadata and usage statistics.
 *
 * @param <K> Key type
 * @param <V> Value type
 */
public class CacheEntry<K, V> {

    private final K key;
    private volatile V value;
    private final long createdAtEpochMs;
    private final long ttlMs; // <= 0 implies never expires
    private volatile long lastAccessedEpochMs;
    private volatile long accessCount;

    public CacheEntry(K key, V value, long ttlMs, long currentEpochMs) {
        this.key = Objects.requireNonNull(key, "Cache key cannot be null");
        this.value = Objects.requireNonNull(value, "Cache value cannot be null");
        this.ttlMs = ttlMs;
        this.createdAtEpochMs = currentEpochMs;
        this.lastAccessedEpochMs = currentEpochMs;
        this.accessCount = 1L;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value, long currentEpochMs) {
        this.value = Objects.requireNonNull(value, "Cache value cannot be null");
        this.lastAccessedEpochMs = currentEpochMs;
        this.accessCount++;
    }

    public long getCreatedAtEpochMs() {
        return createdAtEpochMs;
    }

    public long getTtlMs() {
        return ttlMs;
    }

    public long getLastAccessedEpochMs() {
        return lastAccessedEpochMs;
    }

    public long getAccessCount() {
        return accessCount;
    }

    public void recordAccess(long currentEpochMs) {
        this.lastAccessedEpochMs = currentEpochMs;
        this.accessCount++;
    }

    public boolean isExpired(long currentEpochMs) {
        if (ttlMs <= 0L) {
            return false;
        }
        return (currentEpochMs - createdAtEpochMs) > ttlMs;
    }

    @Override
    public String toString() {
        return "CacheEntry{" +
                "key=" + key +
                ", value=" + value +
                ", ttlMs=" + ttlMs +
                ", accessCount=" + accessCount +
                '}';
    }
}
