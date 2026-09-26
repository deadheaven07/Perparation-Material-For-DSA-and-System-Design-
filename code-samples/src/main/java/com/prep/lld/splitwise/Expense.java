package com.prep.lld.splitwise;

import java.util.List;
import java.util.Objects;

public class Expense {
    private final String expenseId;
    private final String description;
    private final double totalAmount;
    private final User paidBy;
    private final SplitType splitType;
    private final List<Split> splits;

    public Expense(String expenseId, String description, double totalAmount, User paidBy, SplitType splitType, List<Split> splits) {
        this.expenseId = Objects.requireNonNull(expenseId, "expenseId cannot be null");
        this.description = description == null ? "" : description;
        if (totalAmount <= 0) {
            throw new SplitwiseException("Total amount must be greater than zero");
        }
        this.totalAmount = totalAmount;
        this.paidBy = Objects.requireNonNull(paidBy, "paidBy cannot be null");
        this.splitType = Objects.requireNonNull(splitType, "splitType cannot be null");
        if (splits == null || splits.isEmpty()) {
            throw new SplitwiseException("Splits list cannot be empty");
        }
        this.splits = splits;

        validateAndNormalizeSplits();
    }

    private void validateAndNormalizeSplits() {
        switch (splitType) {
            case EQUAL -> {
                double equalShare = Math.round((totalAmount / splits.size()) * 100.0) / 100.0;
                double accumulated = 0.0;
                for (int i = 0; i < splits.size(); i++) {
                    Split s = splits.get(i);
                    if (i == splits.size() - 1) {
                        s.setAmount(Math.round((totalAmount - accumulated) * 100.0) / 100.0);
                    } else {
                        s.setAmount(equalShare);
                        accumulated += equalShare;
                    }
                }
            }
            case EXACT -> {
                double sum = 0.0;
                for (Split split : splits) {
                    if (!(split instanceof ExactSplit)) {
                        throw new SplitwiseException("Split must be an instance of ExactSplit for EXACT type");
                    }
                    sum += split.getAmount();
                }
                if (Math.abs(sum - totalAmount) > 0.01) {
                    throw new SplitwiseException("Sum of exact splits (" + sum + ") does not equal total amount (" + totalAmount + ")");
                }
            }
            case PERCENT -> {
                double percentSum = 0.0;
                for (Split split : splits) {
                    if (!(split instanceof PercentSplit ps)) {
                        throw new SplitwiseException("Split must be an instance of PercentSplit for PERCENT type");
                    }
                    percentSum += ps.getPercent();
                    double amount = Math.round((totalAmount * (ps.getPercent() / 100.0)) * 100.0) / 100.0;
                    split.setAmount(amount);
                }
                if (Math.abs(percentSum - 100.0) > 0.01) {
                    throw new SplitwiseException("Sum of percentages (" + percentSum + ") must equal 100.0");
                }
            }
        }
    }

    public String getExpenseId() {
        return expenseId;
    }

    public String getDescription() {
        return description;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public User getPaidBy() {
        return paidBy;
    }

    public SplitType getSplitType() {
        return splitType;
    }

    public List<Split> getSplits() {
        return splits;
    }
}
