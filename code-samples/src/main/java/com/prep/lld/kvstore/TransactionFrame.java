package com.prep.lld.kvstore;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TransactionFrame {
    private final Map<String, ValueEntry> writes = new HashMap<>();
    private final Set<String> deletes = new HashSet<>();

    public void put(String key, ValueEntry entry) {
        deletes.remove(key);
        writes.put(key, entry);
    }

    public void delete(String key) {
        writes.remove(key);
        deletes.add(key);
    }

    public boolean isDeleted(String key) {
        return deletes.contains(key);
    }

    public boolean hasWrite(String key) {
        return writes.containsKey(key);
    }

    public ValueEntry getWrite(String key) {
        return writes.get(key);
    }

    public Map<String, ValueEntry> getWrites() {
        return writes;
    }

    public Set<String> getDeletes() {
        return deletes;
    }
}
