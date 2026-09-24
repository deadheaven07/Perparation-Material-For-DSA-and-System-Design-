package com.prep.lld.scheduler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Distributed Task Scheduler Test Suite")
class TaskSchedulerTest {

    @Test
    @DisplayName("One-shot delayed task executes successfully")
    void testDelayedExecution() throws InterruptedException {
        try (DistributedTaskScheduler scheduler = new DistributedTaskScheduler(2)) {
            CountDownLatch latch = new CountDownLatch(1);
            AtomicBoolean executed = new AtomicBoolean(false);

            scheduler.schedule("job-1", () -> {
                executed.set(true);
                latch.countDown();
            }, 50L);

            assertTrue(latch.await(2, TimeUnit.SECONDS), "Task must execute within timeout");
            assertTrue(executed.get(), "Task flag must be true");

            // Allow worker thread to finalize status
            Thread.sleep(50L);
            assertEquals(TaskStatus.COMPLETED, scheduler.getStatus("job-1").orElse(null));
        }
    }

    @Test
    @DisplayName("Recurring task executes repeatedly across scheduled periods")
    void testRecurringExecution() throws InterruptedException {
        try (DistributedTaskScheduler scheduler = new DistributedTaskScheduler(2)) {
            int runsRequired = 3;
            CountDownLatch latch = new CountDownLatch(runsRequired);
            AtomicInteger executionCount = new AtomicInteger();

            scheduler.scheduleAtFixedRate("recurring-job", () -> {
                executionCount.incrementAndGet();
                latch.countDown();
            }, 20L, 50L);

            assertTrue(latch.await(2, TimeUnit.SECONDS), "Recurring task must execute at least 3 times");
            assertTrue(executionCount.get() >= runsRequired);
        }
    }

    @Test
    @DisplayName("Failing task follows retry policy with backoff and marks FAILED after max attempts")
    void testRetryPolicyOnFailure() throws InterruptedException {
        try (DistributedTaskScheduler scheduler = new DistributedTaskScheduler(2)) {
            AtomicInteger attempts = new AtomicInteger();
            CountDownLatch failedLatch = new CountDownLatch(1);

            RetryPolicy retryPolicy = new RetryPolicy(2, 20L, 1.5);

            scheduler.scheduleWithRetry("failing-job", () -> {
                int current = attempts.incrementAndGet();
                if (current <= 3) {
                    throw new RuntimeException("Simulated worker exception on attempt " + current);
                }
            }, 10L, retryPolicy);

            // Wait for attempts to complete (Initial attempt + 2 retries = 3 attempts total)
            boolean completed = false;
            for (int i = 0; i < 20; i++) {
                Thread.sleep(50L);
                if (scheduler.getStatus("failing-job").orElse(null) == TaskStatus.FAILED) {
                    completed = true;
                    break;
                }
            }

            assertTrue(completed, "Job must transition to FAILED status");
            assertEquals(3, attempts.get(), "Initial run + 2 retries = 3 attempts");
            assertTrue(scheduler.getLastError("failing-job").isPresent());
        }
    }

    @Test
    @DisplayName("Cancelled task is removed from execution queue")
    void testJobCancellation() throws InterruptedException {
        try (DistributedTaskScheduler scheduler = new DistributedTaskScheduler(2)) {
            AtomicBoolean executed = new AtomicBoolean(false);

            scheduler.schedule("cancel-me", () -> executed.set(true), 150L);

            boolean cancelled = scheduler.cancel("cancel-me");
            assertTrue(cancelled, "Cancel call should return true");
            assertEquals(TaskStatus.CANCELLED, scheduler.getStatus("cancel-me").orElse(null));

            Thread.sleep(200L);
            assertFalse(executed.get(), "Cancelled task must never execute");
        }
    }
}
