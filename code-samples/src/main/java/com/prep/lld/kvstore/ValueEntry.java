package com.prep.lld.kvstore;

import java.time.Instant;
import java.util.Objects;

public record ValueEntry(String value, Long expireAtEpochMs) {
    public ValueEntry {
        Objects.requireNonNull(value, "value cannot be null");
    }

    public static ValueEntry of(String value) {
        return new ValueEntry(value, null);
    }

    public static ValueEntry of(String value, long ttlMs) {
        if (ttlMs <= 0) {
            throw new IllegalArgumentException("TTL must be positive");
        }
        return new ValueEntry(value, Instant.now().toEpochMilli() + ttlMs);
    }

    public boolean isExpired() {
        return expireAtEpochMs != null && Instant.now().toEpochMilli() >= expireAtEpochMs;
    }
}
