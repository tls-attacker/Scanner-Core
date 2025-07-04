/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.execution;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Extends {@link ThreadPoolExecutor} with its own afterExecute function. A
 * ScannerThreadPoolExecutor hold a semaphore which is released each time a Thread finished
 * executing or is aborted on timeout.
 */
public class ScannerThreadPoolExecutor extends ScheduledThreadPoolExecutor {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final RejectedExecutionHandler defaultHandler = new AbortPolicy();

    private final Semaphore semaphore;

    /** The time after which tasks are automatically cancelled */
    private final long timeout;

    private final Timer timer;

    /**
     * Call super and assign the semaphore
     *
     * @param corePoolSize The corePoolSize
     * @param threadFactory The threadFactory
     * @param semaphore The semaphore
     * @param timeout The timeout after which tasks are cancelled in milliseconds.
     */
    public ScannerThreadPoolExecutor(
            int corePoolSize, ThreadFactory threadFactory, Semaphore semaphore, long timeout) {
        super(corePoolSize, threadFactory, defaultHandler);
        this.semaphore = semaphore;
        this.timeout = timeout;
        this.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
        this.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        this.timer = new Timer();
    }

    /**
     * Releases the semaphore when the Runnable r finished executing.
     *
     * @param r The runnable that finished executing.
     * @param t Should r fail, t holds the exception.
     */
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        semaphore.release();
    }

    /*
     * We override the submit method(s). Next to submitting the original task, we
     * submit a task that will kill the original task after the amount of time
     * specified in timeout.
     */
    @Override
    public Future<?> submit(Runnable task) {
        Future<?> future = super.submit(task);
        cancelFuture(future);
        return future;
    }

    /**
     * Submits a Runnable task for execution and returns a Future representing that task. The task
     * will be automatically cancelled if it exceeds the configured timeout.
     *
     * @param <T> the type of the result
     * @param task the task to submit
     * @param result the result to return when the task completes
     * @return a Future representing pending completion of the task
     */
    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        Future<T> future = super.submit(task, result);
        cancelFuture(future);
        return future;
    }

    /**
     * Submits a value-returning task for execution and returns a Future representing the pending
     * results. The task will be automatically cancelled if it exceeds the configured timeout.
     *
     * @param <T> the type of the task's result
     * @param task the task to submit
     * @return a Future representing pending completion of the task
     */
    @Override
    public <T> Future<T> submit(Callable<T> task) {
        Future<T> future = super.submit(task);
        cancelFuture(future);
        return future;
    }

    @Override
    public void shutdown() {
        super.shutdown();
        timer.cancel();
    }

    @Override
    public List<Runnable> shutdownNow() {
        timer.cancel();
        return super.shutdownNow();
    }

    @Override
    public void close() {
        super.close();
        timer.cancel();
    }

    private void cancelFuture(Future<?> future) {
        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        if (!future.isDone()) {
                            future.cancel(true);
                            if (future.isCancelled()) {
                                LOGGER.error("Killed task {}", future);
                            } else {
                                LOGGER.error("Could not kill task {}", future);
                            }
                        } else {
                            LOGGER.debug("Future already done! {}", future);
                        }
                    }
                },
                timeout);
    }
}
