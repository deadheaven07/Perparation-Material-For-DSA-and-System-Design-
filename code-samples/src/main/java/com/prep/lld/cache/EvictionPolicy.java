package com.prep.lld.cache;

import java.util.Optional;

/**
 * Pluggable eviction policy contract for in-memory cache implementations.
 *
 * @param <K> Key type
 */
public interface EvictionPolicy<K> {

    /**
     * Records a read or update access on an existing key.
     *
     * @param key Accessed key
     */
    void recordAccess(K key);

    /**
     * Records the initial insertion of a key.
     *
     * @param key Inserted key
     */
    void recordInsertion(K key);

    /**
     * Records explicit deletion or eviction of a key.
     *
     * @param key Deleted key
     */
    void recordDeletion(K key);

    /**
     * Selects and evicts the candidate key according to policy rules.
     *
     * @return Evicted key, or empty if policy tracks no keys
     */
    Optional<K> evictKey();

    /**
     * Resets internal policy tracking state.
     */
    void clear();
}
