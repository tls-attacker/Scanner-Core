/*
 * Scanner Core - A modular framework for probe definition, execution, and result analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.probe.result;

import java.io.Serializable;

public class ObjectResult<T> implements TestResult, Serializable {

    private final String name;
    protected final T value;

    public ObjectResult(T value, String name) {
        this.value = value;
        this.name = name;
    }

    public T getValue() {
        return value;
    }

    @Override
    public String getName() {
        return name;
    }
}
