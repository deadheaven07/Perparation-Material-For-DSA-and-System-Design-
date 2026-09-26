package com.prep.lld.splitwise;

import java.util.Objects;

public record Transaction(String fromUserId, String toUserId, double amount) {
    public Transaction {
        Objects.requireNonNull(fromUserId, "fromUserId cannot be null");
        Objects.requireNonNull(toUserId, "toUserId cannot be null");
        if (amount <= 0) {
            throw new IllegalArgumentException("Transaction amount must be positive");
        }
    }
}
