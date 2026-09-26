package com.prep.lld.splitwise;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class DebtSimplifier {

    private record BalanceEntry(String userId, double amount) {}

    public static List<Transaction> simplifyDebts(Map<String, Double> netBalances) {
        List<Transaction> settlements = new ArrayList<>();
        if (netBalances == null || netBalances.isEmpty()) {
            return settlements;
        }

        // Creditors: net positive balance (descending)
        PriorityQueue<BalanceEntry> creditors = new PriorityQueue<>(
                Comparator.comparingDouble(BalanceEntry::amount).reversed()
        );

        // Debtors: net negative balance (absolute amount descending)
        PriorityQueue<BalanceEntry> debtors = new PriorityQueue<>(
                Comparator.comparingDouble(BalanceEntry::amount).reversed()
        );

        for (Map.Entry<String, Double> entry : netBalances.entrySet()) {
            double balance = Math.round(entry.getValue() * 100.0) / 100.0;
            if (balance > 0.01) {
                creditors.offer(new BalanceEntry(entry.getKey(), balance));
            } else if (balance < -0.01) {
                debtors.offer(new BalanceEntry(entry.getKey(), Math.abs(balance)));
            }
        }

        while (!creditors.isEmpty() && !debtors.isEmpty()) {
            BalanceEntry creditor = creditors.poll();
            BalanceEntry debtor = debtors.poll();

            double settleAmount = Math.min(creditor.amount(), debtor.amount());
            settleAmount = Math.round(settleAmount * 100.0) / 100.0;

            if (settleAmount > 0.0) {
                settlements.add(new Transaction(debtor.userId(), creditor.userId(), settleAmount));
            }

            double remainingCredit = Math.round((creditor.amount() - settleAmount) * 100.0) / 100.0;
            double remainingDebit = Math.round((debtor.amount() - settleAmount) * 100.0) / 100.0;

            if (remainingCredit > 0.01) {
                creditors.offer(new BalanceEntry(creditor.userId(), remainingCredit));
            }
            if (remainingDebit > 0.01) {
                debtors.offer(new BalanceEntry(debtor.userId(), remainingDebit));
            }
        }

        return settlements;
    }
}
