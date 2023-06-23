/*
 * Scanner Core - A modular framework for probe definition, execution, and result analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.execution;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class NamedThreadFactory implements ThreadFactory {

    private int number;

    private final String prefix;

    public NamedThreadFactory(String prefix) {
        this.number = 1;
        this.prefix = prefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread newThread = Executors.defaultThreadFactory().newThread(r);
        newThread.setName(prefix + "-" + number);
        number++;
        return newThread;
    }
}