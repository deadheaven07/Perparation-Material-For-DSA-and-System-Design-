package com.prep.lld.splitwise;

import java.util.Objects;

public record User(String userId, String name, String email) {
    public User {
        Objects.requireNonNull(userId, "userId cannot be null");
        Objects.requireNonNull(name, "name cannot be null");
    }
}
