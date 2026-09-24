package com.prep.lld.scheduler;

import java.util.Objects;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Representation of a scheduled task implementing {@link Delayed} for high-efficiency queueing.
 */
public class ScheduledJob implements Delayed {

    private final String jobId;
    private final Runnable task;
    private volatile long scheduledEpochMs;
    private final long repeatIntervalMs;
    private final RetryPolicy retryPolicy;
    private final AtomicInteger currentAttempt = new AtomicInteger(0);
    private volatile TaskStatus status = TaskStatus.PENDING;
    private volatile Throwable lastException;

    public ScheduledJob(String jobId, Runnable task, long scheduledEpochMs) {
        this(jobId, task, scheduledEpochMs, 0L, RetryPolicy.noRetry());
    }

    public ScheduledJob(String jobId, Runnable task, long scheduledEpochMs, long repeatIntervalMs, RetryPolicy retryPolicy) {
        this.jobId = Objects.requireNonNull(jobId, "jobId cannot be null");
        this.task = Objects.requireNonNull(task, "task cannot be null");
        this.scheduledEpochMs = scheduledEpochMs;
        this.repeatIntervalMs = Math.max(0L, repeatIntervalMs);
        this.retryPolicy = retryPolicy != null ? retryPolicy : RetryPolicy.noRetry();
    }

    public String getJobId() {
        return jobId;
    }

    public Runnable getTask() {
        return task;
    }

    public long getScheduledEpochMs() {
        return scheduledEpochMs;
    }

    public void setScheduledEpochMs(long scheduledEpochMs) {
        this.scheduledEpochMs = scheduledEpochMs;
    }

    public long getRepeatIntervalMs() {
        return repeatIntervalMs;
    }

    public boolean isRecurring() {
        return repeatIntervalMs > 0L;
    }

    public RetryPolicy getRetryPolicy() {
        return retryPolicy;
    }

    public int getAttemptCount() {
        return currentAttempt.get();
    }

    public int incrementAttempt() {
        return currentAttempt.incrementAndGet();
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Throwable getLastException() {
        return lastException;
    }

    public void setLastException(Throwable lastException) {
        this.lastException = lastException;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long remainingMs = scheduledEpochMs - System.currentTimeMillis();
        return unit.convert(remainingMs, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed other) {
        if (this == other) {
            return 0;
        }
        if (other instanceof ScheduledJob otherJob) {
            return Long.compare(this.scheduledEpochMs, otherJob.scheduledEpochMs);
        }
        return Long.compare(this.getDelay(TimeUnit.MILLISECONDS), other.getDelay(TimeUnit.MILLISECONDS));
    }
}
