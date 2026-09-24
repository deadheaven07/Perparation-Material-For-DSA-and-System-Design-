package com.prep.lld.scheduler;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * High-throughput, thread-safe in-memory task scheduler powered by {@link DelayQueue}
 * and a decoupled worker thread pool.
 */
public class DistributedTaskScheduler implements AutoCloseable {

    private final DelayQueue<ScheduledJob> delayQueue = new DelayQueue<>();
    private final ConcurrentHashMap<String, ScheduledJob> registry = new ConcurrentHashMap<>();
    private final ExecutorService workerPool;
    private final Thread dispatcherThread;
    private volatile boolean isRunning = true;

    public DistributedTaskScheduler(int workerCount) {
        if (workerCount <= 0) {
            throw new IllegalArgumentException("Worker count must be positive: " + workerCount);
        }
        this.workerPool = Executors.newFixedThreadPool(workerCount, r -> {
            Thread t = new Thread(r, "scheduler-worker-" + r.hashCode());
            t.setDaemon(true);
            return t;
        });

        this.dispatcherThread = new Thread(this::dispatchLoop, "scheduler-dispatcher");
        this.dispatcherThread.setDaemon(true);
        this.dispatcherThread.start();
    }

    public void schedule(String jobId, Runnable task, long delayMs) {
        schedule(jobId, task, delayMs, 0L, RetryPolicy.noRetry());
    }

    public void scheduleWithRetry(String jobId, Runnable task, long delayMs, RetryPolicy retryPolicy) {
        schedule(jobId, task, delayMs, 0L, retryPolicy);
    }

    public void scheduleAtFixedRate(String jobId, Runnable task, long initialDelayMs, long periodMs) {
        if (periodMs <= 0) {
            throw new IllegalArgumentException("Period must be positive: " + periodMs);
        }
        schedule(jobId, task, initialDelayMs, periodMs, RetryPolicy.noRetry());
    }

    private void schedule(String jobId, Runnable task, long delayMs, long periodMs, RetryPolicy retryPolicy) {
        long runAt = System.currentTimeMillis() + Math.max(0L, delayMs);
        ScheduledJob job = new ScheduledJob(jobId, task, runAt, periodMs, retryPolicy);
        registry.put(jobId, job);
        delayQueue.offer(job);
    }

    public boolean cancel(String jobId) {
        ScheduledJob job = registry.get(jobId);
        if (job != null) {
            job.setStatus(TaskStatus.CANCELLED);
            delayQueue.remove(job);
            return true;
        }
        return false;
    }

    public Optional<TaskStatus> getStatus(String jobId) {
        ScheduledJob job = registry.get(jobId);
        return job != null ? Optional.of(job.getStatus()) : Optional.empty();
    }

    public Optional<Throwable> getLastError(String jobId) {
        ScheduledJob job = registry.get(jobId);
        return job != null ? Optional.ofNullable(job.getLastException()) : Optional.empty();
    }

    private void dispatchLoop() {
        while (isRunning) {
            try {
                ScheduledJob job = delayQueue.poll(200, TimeUnit.MILLISECONDS);
                if (job == null) {
                    continue;
                }

                if (job.getStatus() == TaskStatus.CANCELLED) {
                    continue;
                }

                workerPool.submit(() -> executeJob(job));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void executeJob(ScheduledJob job) {
        if (job.getStatus() == TaskStatus.CANCELLED) {
            return;
        }

        job.setStatus(TaskStatus.RUNNING);
        try {
            job.getTask().run();

            if (job.isRecurring() && job.getStatus() != TaskStatus.CANCELLED) {
                job.setStatus(TaskStatus.PENDING);
                job.setScheduledEpochMs(System.currentTimeMillis() + job.getRepeatIntervalMs());
                delayQueue.offer(job);
            } else {
                job.setStatus(TaskStatus.COMPLETED);
            }
        } catch (Throwable t) {
            job.setLastException(t);
            int attempt = job.incrementAttempt();
            if (attempt <= job.getRetryPolicy().maxRetries()) {
                job.setStatus(TaskStatus.RETRYING);
                long backoffMs = job.getRetryPolicy().calculateDelayMs(attempt);
                job.setScheduledEpochMs(System.currentTimeMillis() + backoffMs);
                delayQueue.offer(job);
            } else {
                job.setStatus(TaskStatus.FAILED);
            }
        }
    }

    @Override
    public void close() {
        isRunning = false;
        dispatcherThread.interrupt();
        workerPool.shutdown();
        try {
            if (!workerPool.awaitTermination(2, TimeUnit.SECONDS)) {
                workerPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            workerPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
