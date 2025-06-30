/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.execution;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A thread factory that creates threads with custom names. This factory is useful for identifying
 * threads in thread dumps and monitoring tools.
 */
public class NamedThreadFactory implements ThreadFactory {

    private AtomicInteger number = new AtomicInteger(1);

    private final String prefix;

    /**
     * Creates a new NamedThreadFactory with the specified name prefix.
     *
     * @param prefix the prefix to use for thread names. Threads will be named as "prefix-number"
     *     //$NON-NLS-1$
     */
    public NamedThreadFactory(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Creates a new thread with a custom name. The thread name will be in the format
     * "prefix-number" where number increments for each new thread.
     *
     * @param r the runnable to be executed by the new thread
     * @return a newly created thread with a custom name
     */
    @Override
    public Thread newThread(Runnable r) {
        Thread newThread = Executors.defaultThreadFactory().newThread(r);
        newThread.setName(prefix + "-" + number.getAndIncrement());
        return newThread;
    }
}
