package com.prep.lld.kvstore;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TransactionalKeyStore {
    private final Map<String, ValueEntry> globalStore = new ConcurrentHashMap<>();
    private final ThreadLocal<Deque<TransactionFrame>> transactionStack = ThreadLocal.withInitial(ArrayDeque::new);
    private final ReentrantReadWriteLock globalLock = new ReentrantReadWriteLock();

    public void set(String key, String value) {
        set(key, value, null);
    }

    public void set(String key, String value, Long ttlMs) {
        Objects.requireNonNull(key, "key cannot be null");
        Objects.requireNonNull(value, "value cannot be null");

        ValueEntry entry = (ttlMs != null) ? ValueEntry.of(value, ttlMs) : ValueEntry.of(value);
        Deque<TransactionFrame> stack = transactionStack.get();

        if (stack.isEmpty()) {
            globalLock.writeLock().lock();
            try {
                globalStore.put(key, entry);
            } finally {
                globalLock.writeLock().unlock();
            }
        } else {
            stack.peek().put(key, entry);
        }
    }

    public Optional<String> get(String key) {
        Objects.requireNonNull(key, "key cannot be null");
        Deque<TransactionFrame> stack = transactionStack.get();

        // 1. Search transaction stack from top to bottom
        for (TransactionFrame frame : stack) {
            if (frame.isDeleted(key)) {
                return Optional.empty();
            }
            if (frame.hasWrite(key)) {
                ValueEntry entry = frame.getWrite(key);
                if (entry.isExpired()) {
                    return Optional.empty();
                }
                return Optional.of(entry.value());
            }
        }

        // 2. Fall back to global store
        globalLock.readLock().lock();
        try {
            ValueEntry entry = globalStore.get(key);
            if (entry == null) {
                return Optional.empty();
            }
            if (entry.isExpired()) {
                return Optional.empty();
            }
            return Optional.of(entry.value());
        } finally {
            globalLock.readLock().unlock();
        }
    }

    public void delete(String key) {
        Objects.requireNonNull(key, "key cannot be null");
        Deque<TransactionFrame> stack = transactionStack.get();

        if (stack.isEmpty()) {
            globalLock.writeLock().lock();
            try {
                globalStore.remove(key);
            } finally {
                globalLock.writeLock().unlock();
            }
        } else {
            stack.peek().delete(key);
        }
    }

    public void begin() {
        transactionStack.get().push(new TransactionFrame());
    }

    public void commit() {
        Deque<TransactionFrame> stack = transactionStack.get();
        if (stack.isEmpty()) {
            throw new KeyStoreException("No active transaction to commit");
        }

        TransactionFrame current = stack.pop();

        if (stack.isEmpty()) {
            // Root transaction committing to global store
            globalLock.writeLock().lock();
            try {
                for (String delKey : current.getDeletes()) {
                    globalStore.remove(delKey);
                }
                globalStore.putAll(current.getWrites());
            } finally {
                globalLock.writeLock().unlock();
            }
        } else {
            // Nested transaction merging into parent frame
            TransactionFrame parent = stack.peek();
            for (String delKey : current.getDeletes()) {
                parent.delete(delKey);
            }
            for (Map.Entry<String, ValueEntry> entry : current.getWrites().entrySet()) {
                parent.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public void rollback() {
        Deque<TransactionFrame> stack = transactionStack.get();
        if (stack.isEmpty()) {
            throw new KeyStoreException("No active transaction to rollback");
        }
        stack.pop();
    }

    public int getTransactionDepth() {
        return transactionStack.get().size();
    }

    public void clearThreadTransactions() {
        transactionStack.get().clear();
    }
}
