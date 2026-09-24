package com.prep.lld.cache;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Optional;

/**
 * First-In First-Out (FIFO) eviction policy maintaining strict insertion order.
 *
 * @param <K> Key type
 */
public class FIFOEvictionPolicy<K> implements EvictionPolicy<K> {

    private final LinkedHashSet<K> insertionOrder = new LinkedHashSet<>();

    @Override
    public synchronized void recordAccess(K key) {
        // FIFO order is invariant to read accesses
    }

    @Override
    public synchronized void recordInsertion(K key) {
        if (!insertionOrder.contains(key)) {
            insertionOrder.add(key);
        }
    }

    @Override
    public synchronized void recordDeletion(K key) {
        insertionOrder.remove(key);
    }

    @Override
    public synchronized Optional<K> evictKey() {
        if (insertionOrder.isEmpty()) {
            return Optional.empty();
        }
        Iterator<K> iterator = insertionOrder.iterator();
        K victim = iterator.next();
        iterator.remove();
        return Optional.of(victim);
    }

    @Override
    public synchronized void clear() {
        insertionOrder.clear();
    }
}
