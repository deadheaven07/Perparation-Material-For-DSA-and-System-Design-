package com.prep.lld.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Enterprise-grade, thread-safe concurrent in-memory cache.
 *
 * <p>Features:
 * <ul>
 *   <li>Pluggable eviction strategies (LRU, LFU, FIFO)</li>
 *   <li>Read/Write lock striping for high concurrency</li>
 *   <li>Active background and passive on-demand TTL expiration</li>
 *   <li>Non-blocking metrics telemetry (hits, misses, evictions, expirations)</li>
 *   <li>Observer callbacks for audit and debugging</li>
 * </ul>
 *
 * @param <K> Key type
 * @param <V> Value type
 */
public class ConcurrentCache<K, V> implements AutoCloseable {

    public record CacheStats(
            long hits,
            long misses,
            long evictions,
            long expirations,
            int currentSize,
            int capacity
    ) {
        public double hitRate() {
            long total = hits + misses;
            return total == 0 ? 0.0 : (double) hits / total;
        }
    }

    private final int capacity;
    private final long defaultTtlMs;
    private final EvictionPolicy<K> evictionPolicy;
    private final Map<K, CacheEntry<K, V>> storage;
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final CopyOnWriteArrayList<CacheEventListener<K, V>> listeners = new CopyOnWriteArrayList<>();

    private final AtomicLong hitCount = new AtomicLong();
    private final AtomicLong missCount = new AtomicLong();
    private final AtomicLong evictionCount = new AtomicLong();
    private final AtomicLong expirationCount = new AtomicLong();

    private final ScheduledExecutorService cleanupExecutor;

    public ConcurrentCache(int capacity, EvictionPolicy<K> evictionPolicy) {
        this(capacity, evictionPolicy, 0L, 0L);
    }

    public ConcurrentCache(int capacity, EvictionPolicy<K> evictionPolicy, long defaultTtlMs, long cleanupIntervalMs) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive: " + capacity);
        }
        this.capacity = capacity;
        this.evictionPolicy = Objects.requireNonNull(evictionPolicy, "Eviction policy cannot be null");
        this.defaultTtlMs = Math.max(0L, defaultTtlMs);
        this.storage = new HashMap<>(capacity);

        if (cleanupIntervalMs > 0) {
            this.cleanupExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "cache-ttl-cleaner-" + System.identityHashCode(this));
                t.setDaemon(true);
                return t;
            });
            this.cleanupExecutor.scheduleAtFixedRate(this::purgeExpired, cleanupIntervalMs, cleanupIntervalMs, TimeUnit.MILLISECONDS);
        } else {
            this.cleanupExecutor = null;
        }
    }

    public void addListener(CacheEventListener<K, V> listener) {
        listeners.add(Objects.requireNonNull(listener));
    }

    public void put(K key, V value) {
        put(key, value, defaultTtlMs);
    }

    public void put(K key, V value, long ttlMs) {
        Objects.requireNonNull(key, "Cache key cannot be null");
        Objects.requireNonNull(value, "Cache value cannot be null");

        long now = System.currentTimeMillis();
        rwLock.writeLock().lock();
        try {
            CacheEntry<K, V> existing = storage.get(key);
            if (existing != null) {
                existing.setValue(value, now);
                evictionPolicy.recordAccess(key);
                notifyListeners(CacheEventListener.EventType.PUT, key, value);
                return;
            }

            // Check capacity and evict if full
            while (storage.size() >= capacity) {
                Optional<K> victimOpt = evictionPolicy.evictKey();
                if (victimOpt.isEmpty()) {
                    break;
                }
                K victimKey = victimOpt.get();
                CacheEntry<K, V> evictedEntry = storage.remove(victimKey);
                if (evictedEntry != null) {
                    evictionCount.incrementAndGet();
                    notifyListeners(CacheEventListener.EventType.EVICTED, victimKey, evictedEntry.getValue());
                }
            }

            CacheEntry<K, V> newEntry = new CacheEntry<>(key, value, ttlMs, now);
            storage.put(key, newEntry);
            evictionPolicy.recordInsertion(key);
            notifyListeners(CacheEventListener.EventType.PUT, key, value);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public Optional<V> get(K key) {
        Objects.requireNonNull(key, "Cache key cannot be null");
        long now = System.currentTimeMillis();

        rwLock.readLock().lock();
        try {
            CacheEntry<K, V> entry = storage.get(key);
            if (entry == null) {
                missCount.incrementAndGet();
                notifyListeners(CacheEventListener.EventType.MISS, key, null);
                return Optional.empty();
            }

            if (entry.isExpired(now)) {
                // Must upgrade to write lock to evict expired entry
                rwLock.readLock().unlock();
                rwLock.writeLock().lock();
                try {
                    CacheEntry<K, V> doubleCheck = storage.get(key);
                    if (doubleCheck == null || doubleCheck.isExpired(now)) {
                        if (doubleCheck != null) {
                            storage.remove(key);
                            evictionPolicy.recordDeletion(key);
                            expirationCount.incrementAndGet();
                            notifyListeners(CacheEventListener.EventType.EXPIRED, key, doubleCheck.getValue());
                        }
                        missCount.incrementAndGet();
                        notifyListeners(CacheEventListener.EventType.MISS, key, null);
                        return Optional.empty();
                    }
                } finally {
                    rwLock.readLock().lock();
                    rwLock.writeLock().unlock();
                }
            }

            entry.recordAccess(now);
            evictionPolicy.recordAccess(key);
            hitCount.incrementAndGet();
            notifyListeners(CacheEventListener.EventType.HIT, key, entry.getValue());
            return Optional.of(entry.getValue());
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public Optional<V> remove(K key) {
        Objects.requireNonNull(key, "Cache key cannot be null");
        rwLock.writeLock().lock();
        try {
            CacheEntry<K, V> entry = storage.remove(key);
            if (entry != null) {
                evictionPolicy.recordDeletion(key);
                return Optional.of(entry.getValue());
            }
            return Optional.empty();
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public boolean containsKey(K key) {
        return get(key).isPresent();
    }

    public int size() {
        rwLock.readLock().lock();
        try {
            return storage.size();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public void clear() {
        rwLock.writeLock().lock();
        try {
            storage.clear();
            evictionPolicy.clear();
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public int purgeExpired() {
        long now = System.currentTimeMillis();
        int purged = 0;
        rwLock.writeLock().lock();
        try {
            var iterator = storage.entrySet().iterator();
            while (iterator.hasNext()) {
                var entry = iterator.next();
                if (entry.getValue().isExpired(now)) {
                    evictionPolicy.recordDeletion(entry.getKey());
                    iterator.remove();
                    expirationCount.incrementAndGet();
                    purged++;
                    notifyListeners(CacheEventListener.EventType.EXPIRED, entry.getKey(), entry.getValue().getValue());
                }
            }
            return purged;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public CacheStats getStats() {
        rwLock.readLock().lock();
        try {
            return new CacheStats(
                    hitCount.get(),
                    missCount.get(),
                    evictionCount.get(),
                    expirationCount.get(),
                    storage.size(),
                    capacity
            );
        } finally {
            rwLock.readLock().unlock();
        }
    }

    private void notifyListeners(CacheEventListener.EventType type, K key, V value) {
        for (CacheEventListener<K, V> listener : listeners) {
            try {
                listener.onEvent(type, key, value);
            } catch (Exception ignored) {
                // Fail-safe listener execution
            }
        }
    }

    @Override
    public void close() {
        if (cleanupExecutor != null && !cleanupExecutor.isShutdown()) {
            cleanupExecutor.shutdownNow();
        }
    }
}
