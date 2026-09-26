package com.prep.lld.splitwise;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Splitwise LLD Test Suite")
class SplitwiseTest {

    private ExpenseManager manager;
    private User alice;
    private User bob;
    private User charlie;
    private User diana;

    @BeforeEach
    void setUp() {
        manager = new ExpenseManager();
        alice = new User("u1", "Alice", "alice@example.com");
        bob = new User("u2", "Bob", "bob@example.com");
        charlie = new User("u3", "Charlie", "charlie@example.com");
        diana = new User("u4", "Diana", "diana@example.com");

        manager.registerUser(alice);
        manager.registerUser(bob);
        manager.registerUser(charlie);
        manager.registerUser(diana);
    }

    @Test
    @DisplayName("Should correctly divide equal splits and track positive/negative balances")
    void testEqualSplit() {
        Expense expense = new Expense(
                "exp-1",
                "Dinner",
                300.0,
                alice,
                SplitType.EQUAL,
                List.of(new EqualSplit(alice), new EqualSplit(bob), new EqualSplit(charlie))
        );

        manager.addExpense(expense);
        Map<String, Double> balances = manager.getNetBalances();

        assertEquals(200.0, balances.get("u1"));
        assertEquals(-100.0, balances.get("u2"));
        assertEquals(-100.0, balances.get("u3"));
    }

    @Test
    @DisplayName("Should validate exact split sum equals total amount")
    void testExactSplitValidation() {
        // Correct sum
        Expense validExpense = new Expense(
                "exp-2",
                "Groceries",
                100.0,
                bob,
                SplitType.EXACT,
                List.of(new ExactSplit(alice, 40.0), new ExactSplit(bob, 60.0))
        );
        assertDoesNotThrow(() -> manager.addExpense(validExpense));

        // Mismatched sum
        assertThrows(SplitwiseException.class, () -> new Expense(
                "exp-invalid",
                "Shopping",
                100.0,
                bob,
                SplitType.EXACT,
                List.of(new ExactSplit(alice, 40.0), new ExactSplit(bob, 40.0))
        ));
    }

    @Test
    @DisplayName("Should validate percentage split sum equals 100%")
    void testPercentSplitValidation() {
        // Valid 40% + 60% = 100%
        Expense valid = new Expense(
                "exp-3",
                "Cab",
                200.0,
                charlie,
                SplitType.PERCENT,
                List.of(new PercentSplit(alice, 40.0), new PercentSplit(bob, 60.0))
        );
        assertDoesNotThrow(() -> manager.addExpense(valid));
        assertEquals(80.0, valid.getSplits().get(0).getAmount());
        assertEquals(120.0, valid.getSplits().get(1).getAmount());

        // Invalid percentage sum
        assertThrows(SplitwiseException.class, () -> new Expense(
                "exp-invalid-percent",
                "Hotel",
                500.0,
                alice,
                SplitType.PERCENT,
                List.of(new PercentSplit(alice, 50.0), new PercentSplit(bob, 40.0))
        ));
    }

    @Test
    @DisplayName("Should eliminate cyclic debt completely (A->B->C->A simplifies to 0 transactions)")
    void testCycleElimination() {
        // Alice pays $60 for Bob
        manager.addExpense(new Expense("e1", "Lunch", 60.0, alice, SplitType.EXACT, List.of(new ExactSplit(bob, 60.0))));
        // Bob pays $60 for Charlie
        manager.addExpense(new Expense("e2", "Movies", 60.0, bob, SplitType.EXACT, List.of(new ExactSplit(charlie, 60.0))));
        // Charlie pays $60 for Alice
        manager.addExpense(new Expense("e3", "Drinks", 60.0, charlie, SplitType.EXACT, List.of(new ExactSplit(alice, 60.0))));

        Map<String, Double> balances = manager.getNetBalances();
        assertEquals(0.0, balances.get("u1"));
        assertEquals(0.0, balances.get("u2"));
        assertEquals(0.0, balances.get("u3"));

        List<Transaction> transactions = manager.simplifyDebts();
        assertTrue(transactions.isEmpty(), "Cycle should eliminate all debts down to 0 transactions");
    }

    @Test
    @DisplayName("Should minimize cash flow to at most N-1 transactions and verify zero-sum settlement")
    void testMinCashFlowSimplification() {
        // Alice pays 100 for Bob and Charlie (50 each)
        manager.addExpense(new Expense("e1", "Lunch", 100.0, alice, SplitType.EQUAL, List.of(new EqualSplit(bob), new EqualSplit(charlie))));
        // Diana pays 150 for Alice and Charlie (75 each)
        manager.addExpense(new Expense("e2", "Flight", 150.0, diana, SplitType.EQUAL, List.of(new EqualSplit(alice), new EqualSplit(charlie))));

        List<Transaction> transactions = manager.simplifyDebts();
        assertTrue(transactions.size() <= 3, "For 4 users, settlements must be <= 3 transactions");

        // Verify that after executing all transactions, everyone's net balance is 0
        Map<String, Double> net = new HashMap<>(manager.getNetBalances());
        for (Transaction t : transactions) {
            net.put(t.fromUserId(), net.get(t.fromUserId()) + t.amount());
            net.put(t.toUserId(), net.get(t.toUserId()) - t.amount());
        }

        for (Map.Entry<String, Double> entry : net.entrySet()) {
            assertEquals(0.0, Math.round(entry.getValue() * 100.0) / 100.0, "Net balance after settlement must be zero for " + entry.getKey());
        }
    }

    @Test
    @DisplayName("Should guarantee thread safety and maintain zero-sum invariant under concurrent expenses")
    void testConcurrentExpenseSubmissions() throws InterruptedException {
        int threadCount = 20;
        int operationsPerThread = 50;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    for (int j = 0; j < operationsPerThread; j++) {
                        User payer = (threadId % 2 == 0) ? alice : bob;
                        User receiver = (threadId % 2 == 0) ? charlie : diana;
                        manager.addExpense(new Expense(
                                "exp-" + threadId + "-" + j,
                                "Test",
                                10.0,
                                payer,
                                SplitType.EXACT,
                                List.of(new ExactSplit(receiver, 10.0))
                        ));
                    }
                } catch (Exception e) {
                    fail("Thread failed with error: " + e.getMessage());
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        boolean completed = finishLatch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        assertTrue(completed, "Concurrent test timed out");

        // Verify Zero-Sum invariant: sum of all balances across all users must equal 0.00
        Map<String, Double> balances = manager.getNetBalances();
        double sum = balances.values().stream().mapToDouble(Double::doubleValue).sum();
        assertEquals(0.0, Math.round(sum * 100.0) / 100.0, "Zero-sum invariant violated");
    }
}
