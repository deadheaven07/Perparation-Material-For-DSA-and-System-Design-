package com.prep.lld.splitwise;

import java.util.Objects;

public abstract class Split {
    protected final User user;
    protected double amount;

    protected Split(User user) {
        this.user = Objects.requireNonNull(user, "User cannot be null");
    }

    public User getUser() {
        return user;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
