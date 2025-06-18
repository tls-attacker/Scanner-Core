/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.execution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

public class NamedThreadFactoryTest {

    @Test
    public void testNewThreadWithPrefix() {
        String prefix = "TestThread";
        NamedThreadFactory factory = new NamedThreadFactory(prefix);

        Runnable runnable = () -> {};
        Thread thread = factory.newThread(runnable);

        assertNotNull(thread);
        assertEquals(prefix + "-1", thread.getName());
    }

    @Test
    public void testMultipleThreadsWithIncrementingNumbers() {
        String prefix = "Worker";
        NamedThreadFactory factory = new NamedThreadFactory(prefix);

        Thread thread1 = factory.newThread(() -> {});
        Thread thread2 = factory.newThread(() -> {});
        Thread thread3 = factory.newThread(() -> {});

        assertEquals(prefix + "-1", thread1.getName());
        assertEquals(prefix + "-2", thread2.getName());
        assertEquals(prefix + "-3", thread3.getName());
    }

    @Test
    public void testThreadsAreNotDaemon() {
        NamedThreadFactory factory = new NamedThreadFactory("Test");
        Thread thread = factory.newThread(() -> {});

        // Default thread factory creates non-daemon threads
        assertTrue(!thread.isDaemon());
    }

    @Test
    public void testThreadsWithDifferentPrefixes() {
        NamedThreadFactory factory1 = new NamedThreadFactory("Factory1");
        NamedThreadFactory factory2 = new NamedThreadFactory("Factory2");

        Thread thread1 = factory1.newThread(() -> {});
        Thread thread2 = factory2.newThread(() -> {});

        assertEquals("Factory1-1", thread1.getName());
        assertEquals("Factory2-1", thread2.getName());
    }

    @Test
    public void testThreadExecutesRunnable() throws InterruptedException {
        NamedThreadFactory factory = new NamedThreadFactory("Executor");
        AtomicInteger counter = new AtomicInteger(0);

        Runnable runnable =
                () -> {
                    counter.incrementAndGet();
                };

        Thread thread = factory.newThread(runnable);
        thread.start();
        thread.join();

        assertEquals(1, counter.get());
    }

    @Test
    public void testEmptyPrefix() {
        NamedThreadFactory factory = new NamedThreadFactory("");
        Thread thread = factory.newThread(() -> {});

        assertEquals("-1", thread.getName());
    }

    @Test
    public void testSpecialCharactersInPrefix() {
        String prefix = "Test-Thread_123@#$";
        NamedThreadFactory factory = new NamedThreadFactory(prefix);
        Thread thread = factory.newThread(() -> {});

        assertEquals(prefix + "-1", thread.getName());
    }
}
