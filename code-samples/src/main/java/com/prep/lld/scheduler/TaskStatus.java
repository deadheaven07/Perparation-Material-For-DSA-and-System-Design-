package com.prep.lld.scheduler;

/**
 * Lifecycle states of an asynchronously scheduled task.
 */
public enum TaskStatus {
    PENDING,
    RUNNING,
    COMPLETED,
    FAILED,
    RETRYING,
    CANCELLED
}
