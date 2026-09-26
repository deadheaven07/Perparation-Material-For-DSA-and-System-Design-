package com.prep.lld.splitwise;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ExpenseManager {
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final List<Expense> expenses = new CopyOnWriteArrayList<>();
    // netBalances: userId -> net amount (+ means owes user money, - means user owes money)
    private final Map<String, Double> netBalances = new ConcurrentHashMap<>();
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    public void registerUser(User user) {
        Objects.requireNonNull(user, "User cannot be null");
        users.put(user.userId(), user);
        netBalances.putIfAbsent(user.userId(), 0.0);
    }

    public User getUser(String userId) {
        return users.get(userId);
    }

    public void addExpense(Expense expense) {
        Objects.requireNonNull(expense, "Expense cannot be null");
        rwLock.writeLock().lock();
        try {
            expenses.add(expense);
            User paidBy = expense.getPaidBy();

            // Payer gets credited the totalAmount minus their own share (if any)
            for (Split split : expense.getSplits()) {
                String paidToUser = split.getUser().userId();
                double amount = split.getAmount();

                if (paidBy.userId().equals(paidToUser)) {
                    continue;
                }

                // Debtor owes money (negative)
                netBalances.compute(paidToUser, (k, v) -> (v == null ? 0.0 : v) - amount);
                // Creditor is owed money (positive)
                netBalances.compute(paidBy.userId(), (k, v) -> (v == null ? 0.0 : v) + amount);
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public Map<String, Double> getNetBalances() {
        rwLock.readLock().lock();
        try {
            Map<String, Double> copy = new HashMap<>();
            for (Map.Entry<String, Double> entry : netBalances.entrySet()) {
                copy.put(entry.getKey(), Math.round(entry.getValue() * 100.0) / 100.0);
            }
            return Collections.unmodifiableMap(copy);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public List<Transaction> simplifyDebts() {
        rwLock.readLock().lock();
        try {
            return DebtSimplifier.simplifyDebts(netBalances);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public List<Expense> getExpenses() {
        return Collections.unmodifiableList(new ArrayList<>(expenses));
    }
}
