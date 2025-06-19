/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.execution;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

public class ScannerThreadPoolExecutorTest {

    @Test
    public void testBasicConstruction() {
        Semaphore semaphore = new Semaphore(0);
        ScannerThreadPoolExecutor executor =
                new ScannerThreadPoolExecutor(2, new NamedThreadFactory("Test"), semaphore, 5000);

        assertNotNull(executor);
        assertEquals(2, executor.getCorePoolSize());
        assertFalse(executor.isShutdown());

        executor.shutdown();
    }

    @Test
    public void testSubmitRunnable() throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        ScannerThreadPoolExecutor executor =
                new ScannerThreadPoolExecutor(1, new NamedThreadFactory("Test"), semaphore, 5000);

        AtomicBoolean taskExecuted = new AtomicBoolean(false);
        Future<?> future = executor.submit(() -> taskExecuted.set(true));

        // Wait for task completion
        semaphore.acquire();

        assertTrue(taskExecuted.get());
        assertTrue(future.isDone());

        executor.shutdown();
    }

    @Test
    public void testSubmitRunnableWithResult() throws InterruptedException, ExecutionException {
        Semaphore semaphore = new Semaphore(0);
        ScannerThreadPoolExecutor executor =
                new ScannerThreadPoolExecutor(1, new NamedThreadFactory("Test"), semaphore, 5000);

        String result = "TestResult";
        AtomicBoolean taskExecuted = new AtomicBoolean(false);
        Future<String> future = executor.submit(() -> taskExecuted.set(true), result);

        // Wait for task completion
        semaphore.acquire();

        assertTrue(taskExecuted.get());
        assertEquals(result, future.get());

        executor.shutdown();
    }

    @Test
    public void testSubmitCallable() throws InterruptedException, ExecutionException {
        Semaphore semaphore = new Semaphore(0);
        ScannerThreadPoolExecutor executor =
                new ScannerThreadPoolExecutor(1, new NamedThreadFactory("Test"), semaphore, 5000);

        Callable<Integer> callable = () -> 42;
        Future<Integer> future = executor.submit(callable);

        // Wait for task completion
        semaphore.acquire();

        assertEquals(42, future.get());

        executor.shutdown();
    }

    @Test
    public void testSemaphoreReleasedAfterTaskCompletion() throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        ScannerThreadPoolExecutor executor =
                new ScannerThreadPoolExecutor(1, new NamedThreadFactory("Test"), semaphore, 5000);

        executor.submit(
                () -> {
                    // Simple task
                });

        // Should be able to acquire, proving semaphore was released
        assertTrue(semaphore.tryAcquire(1, TimeUnit.SECONDS));

        executor.shutdown();
    }

    @Test
    public void testSemaphoreReleasedOnException() throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        ScannerThreadPoolExecutor executor =
                new ScannerThreadPoolExecutor(1, new NamedThreadFactory("Test"), semaphore, 5000);

        executor.submit(
                () -> {
                    throw new RuntimeException("Test exception");
                });

        // Semaphore should still be released even when task throws exception
        assertTrue(semaphore.tryAcquire(1, TimeUnit.SECONDS));

        executor.shutdown();
    }

    @Test
    public void testTaskTimeout() throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        // Set very short timeout
        ScannerThreadPoolExecutor executor =
                new ScannerThreadPoolExecutor(1, new NamedThreadFactory("Test"), semaphore, 100);

        CountDownLatch taskStarted = new CountDownLatch(1);
        AtomicBoolean taskCompleted = new AtomicBoolean(false);

        Future<?> future =
                executor.submit(
                        () -> {
                            taskStarted.countDown();
                            try {
                                Thread.sleep(500); // Sleep longer than timeout
                                taskCompleted.set(true);
                            } catch (InterruptedException e) {
                                // Expected when task is cancelled
                            }
                        });

        // Wait for task to start
        taskStarted.await();

        // Wait for timeout to trigger
        Thread.sleep(200);

        assertTrue(future.isCancelled());
        assertFalse(taskCompleted.get());

        // Semaphore should be released after timeout
        assertTrue(semaphore.tryAcquire(1, TimeUnit.SECONDS));

        executor.shutdown();
    }

    @Test
    public void testMultipleTasks() throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        ScannerThreadPoolExecutor executor =
                new ScannerThreadPoolExecutor(3, new NamedThreadFactory("Test"), semaphore, 5000);

        AtomicInteger counter = new AtomicInteger(0);
        int taskCount = 10;

        for (int i = 0; i < taskCount; i++) {
            executor.submit(counter::incrementAndGet);
        }

        // Wait for all tasks
        for (int i = 0; i < taskCount; i++) {
            semaphore.acquire();
        }

        assertEquals(taskCount, counter.get());

        executor.shutdown();
    }

    @Test
    public void testShutdownBehavior() throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        ScannerThreadPoolExecutor executor =
                new ScannerThreadPoolExecutor(1, new NamedThreadFactory("Test"), semaphore, 5000);

        AtomicBoolean taskExecuted = new AtomicBoolean(false);
        executor.submit(() -> taskExecuted.set(true));

        semaphore.acquire();
        assertTrue(taskExecuted.get());

        executor.shutdown();
        assertTrue(executor.isShutdown());

        // Should not accept new tasks after shutdown
        assertThrows(
                RejectedExecutionException.class,
                () -> {
                    executor.submit(() -> {});
                });
    }

    @Test
    public void testContinueExistingPeriodicTasksPolicy() {
        Semaphore semaphore = new Semaphore(0);
        ScannerThreadPoolExecutor executor =
                new ScannerThreadPoolExecutor(1, new NamedThreadFactory("Test"), semaphore, 5000);

        // These are set in constructor
        assertFalse(executor.getContinueExistingPeriodicTasksAfterShutdownPolicy());
        assertFalse(executor.getExecuteExistingDelayedTasksAfterShutdownPolicy());

        executor.shutdown();
    }

    @Test
    public void testTaskAlreadyDoneBeforeTimeout() throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        ScannerThreadPoolExecutor executor =
                new ScannerThreadPoolExecutor(1, new NamedThreadFactory("Test"), semaphore, 1000);

        CountDownLatch timeoutCheckLatch = new CountDownLatch(1);

        // Submit a quick task
        Future<?> future =
                executor.submit(
                        () -> {
                            // Quick task that completes before timeout
                        });

        // Wait for task completion
        semaphore.acquire();
        assertTrue(future.isDone());

        // Wait a bit to ensure timeout task runs
        Thread.sleep(1100);

        // The timeout task should have run but found the future already done
        // No exception should be thrown

        executor.shutdown();
    }
}
