package com.prep.lld.cache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;

/**
 * Least Frequently Used (LFU) eviction policy with O(1) time complexity.
 * Resolves frequency ties using FIFO order via LinkedHashSet buckets.
 *
 * @param <K> Key type
 */
public class LFUEvictionPolicy<K> implements EvictionPolicy<K> {

    private final Map<K, Integer> keyFrequencyMap = new HashMap<>();
    private final Map<Integer, LinkedHashSet<K>> frequencyBuckets = new HashMap<>();
    private int minFrequency = 0;

    @Override
    public synchronized void recordAccess(K key) {
        Integer currentFreq = keyFrequencyMap.get(key);
        if (currentFreq == null) {
            return;
        }

        LinkedHashSet<K> currentBucket = frequencyBuckets.get(currentFreq);
        if (currentBucket != null) {
            currentBucket.remove(key);
            if (currentBucket.isEmpty()) {
                frequencyBuckets.remove(currentFreq);
                if (minFrequency == currentFreq) {
                    minFrequency++;
                }
            }
        }

        int newFreq = currentFreq + 1;
        keyFrequencyMap.put(key, newFreq);
        frequencyBuckets.computeIfAbsent(newFreq, k -> new LinkedHashSet<>()).add(key);
    }

    @Override
    public synchronized void recordInsertion(K key) {
        if (keyFrequencyMap.containsKey(key)) {
            recordAccess(key);
            return;
        }

        keyFrequencyMap.put(key, 1);
        frequencyBuckets.computeIfAbsent(1, k -> new LinkedHashSet<>()).add(key);
        minFrequency = 1;
    }

    @Override
    public synchronized void recordDeletion(K key) {
        Integer freq = keyFrequencyMap.remove(key);
        if (freq != null) {
            LinkedHashSet<K> bucket = frequencyBuckets.get(freq);
            if (bucket != null) {
                bucket.remove(key);
                if (bucket.isEmpty()) {
                    frequencyBuckets.remove(freq);
                    if (minFrequency == freq) {
                        minFrequency = frequencyBuckets.keySet().stream()
                                .mapToInt(Integer::intValue)
                                .min()
                                .orElse(0);
                    }
                }
            }
        }
    }

    @Override
    public synchronized Optional<K> evictKey() {
        if (keyFrequencyMap.isEmpty()) {
            return Optional.empty();
        }

        LinkedHashSet<K> minBucket = frequencyBuckets.get(minFrequency);
        if (minBucket == null || minBucket.isEmpty()) {
            return Optional.empty();
        }

        Iterator<K> iterator = minBucket.iterator();
        K victim = iterator.next();
        iterator.remove();

        if (minBucket.isEmpty()) {
            frequencyBuckets.remove(minFrequency);
            minFrequency = frequencyBuckets.keySet().stream()
                    .mapToInt(Integer::intValue)
                    .min()
                    .orElse(0);
        }

        keyFrequencyMap.remove(victim);
        return Optional.of(victim);
    }

    @Override
    public synchronized void clear() {
        keyFrequencyMap.clear();
        frequencyBuckets.clear();
        minFrequency = 0;
    }
}
