package com.prep.lld.kvstore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Transactional Key-Value Store LLD Test Suite")
class TransactionalKeyStoreTest {

    private TransactionalKeyStore store;

    @BeforeEach
    void setUp() {
        store = new TransactionalKeyStore();
    }

    @Test
    @DisplayName("Should perform direct get, set, and delete operations outside transactions")
    void testBasicOperations() {
        store.set("user:101", "Alice");
        assertEquals(Optional.of("Alice"), store.get("user:101"));

        store.delete("user:101");
        assertEquals(Optional.empty(), store.get("user:101"));
    }

    @Test
    @DisplayName("Should rollback uncommitted mutations to preserve initial state")
    void testSingleTransactionRollback() {
        store.set("balance", "100");

        store.begin();
        store.set("balance", "200");
        assertEquals(Optional.of("200"), store.get("balance"));

        store.rollback();
        assertEquals(Optional.of("100"), store.get("balance"));
    }

    @Test
    @DisplayName("Should commit mutations into global state")
    void testSingleTransactionCommit() {
        store.set("count", "1");

        store.begin();
        store.set("count", "5");
        store.set("status", "ACTIVE");
        store.commit();

        assertEquals(Optional.of("5"), store.get("count"));
        assertEquals(Optional.of("ACTIVE"), store.get("status"));
    }

    @Test
    @DisplayName("Should handle nested transactions with inner rollback and outer commit")
    void testNestedTransactionInnerRollback() {
        store.set("key", "global");

        store.begin(); // Outer
        store.set("key", "outer_val");

        store.begin(); // Inner
        store.set("key", "inner_val");
        assertEquals(Optional.of("inner_val"), store.get("key"));
        store.rollback(); // Rollback inner

        assertEquals(Optional.of("outer_val"), store.get("key"));
        store.commit(); // Commit outer

        assertEquals(Optional.of("outer_val"), store.get("key"));
    }

    @Test
    @DisplayName("Should handle nested transactions with inner commit and outer rollback")
    void testNestedTransactionOuterRollback() {
        store.set("key", "initial");

        store.begin(); // Outer
        store.set("key", "outer_val");

        store.begin(); // Inner
        store.set("key", "inner_val");
        store.commit(); // Merges into outer

        assertEquals(Optional.of("inner_val"), store.get("key"));

        store.rollback(); // Rollback outer discards everything
        assertEquals(Optional.of("initial"), store.get("key"));
    }

    @Test
    @DisplayName("Should commit all layers when nested transactions are committed all the way")
    void testNestedTransactionFullCommit() {
        store.begin(); // Depth 1
        store.set("level1", "v1");

        store.begin(); // Depth 2
        store.set("level2", "v2");

        store.begin(); // Depth 3
        store.set("level3", "v3");

        assertEquals(3, store.getTransactionDepth());

        store.commit(); // Merges 3 into 2
        store.commit(); // Merges 2 into 1
        store.commit(); // Merges 1 into global

        assertEquals(0, store.getTransactionDepth());
        assertEquals(Optional.of("v1"), store.get("level1"));
        assertEquals(Optional.of("v2"), store.get("level2"));
        assertEquals(Optional.of("v3"), store.get("level3"));
    }

    @Test
    @DisplayName("Should throw KeyStoreException when committing or rolling back without active transaction")
    void testNoActiveTransactionError() {
        assertThrows(KeyStoreException.class, () -> store.commit());
        assertThrows(KeyStoreException.class, () -> store.rollback());
    }

    @Test
    @DisplayName("Should expire keys automatically when TTL elapses")
    void testTtlExpiration() throws InterruptedException {
        store.set("session_token", "xyz-123", 50L);
        assertEquals(Optional.of("xyz-123"), store.get("session_token"));

        // Wait for TTL expiration
        Thread.sleep(70);

        assertEquals(Optional.empty(), store.get("session_token"));
    }

    @Test
    @DisplayName("Should guarantee snapshot isolation and avoid dirty reads across threads")
    void testSnapshotIsolationAcrossThreads() throws InterruptedException {
        store.set("account", "1000");

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch transactionStarted = new CountDownLatch(1);
        CountDownLatch readAttempted = new CountDownLatch(1);
        CountDownLatch transactionCommitted = new CountDownLatch(1);

        AtomicReference<Optional<String>> thread2ReadBeforeCommit = new AtomicReference<>();
        AtomicReference<Optional<String>> thread2ReadAfterCommit = new AtomicReference<>();

        // Thread 1: Mutates key inside transaction
        executor.submit(() -> {
            store.begin();
            store.set("account", "5000");
            transactionStarted.countDown();

            try {
                readAttempted.await(5, TimeUnit.SECONDS);
                store.commit();
                transactionCommitted.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Thread 2: Reads key while Thread 1's transaction is uncommitted, then reads after commit
        executor.submit(() -> {
            try {
                transactionStarted.await(5, TimeUnit.SECONDS);
                thread2ReadBeforeCommit.set(store.get("account"));
                readAttempted.countDown();

                transactionCommitted.await(5, TimeUnit.SECONDS);
                thread2ReadAfterCommit.set(store.get("account"));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));

        // Thread 2 should have seen 1000 before commit, not 5000 (no dirty read!)
        assertEquals(Optional.of("1000"), thread2ReadBeforeCommit.get());
        // Thread 2 should have seen 5000 after commit
        assertEquals(Optional.of("5000"), thread2ReadAfterCommit.get());
    }
}
