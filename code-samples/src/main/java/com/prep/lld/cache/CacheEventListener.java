package com.prep.lld.cache;

/**
 * Observer callback for cache lifecycle events (eviction, expiration, put).
 *
 * @param <K> Key type
 * @param <V> Value type
 */
@FunctionalInterface
public interface CacheEventListener<K, V> {

    enum EventType {
        PUT,
        HIT,
        MISS,
        EVICTED,
        EXPIRED
    }

    void onEvent(EventType type, K key, V value);
}
