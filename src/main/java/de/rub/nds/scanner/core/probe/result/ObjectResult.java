/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.probe.result;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.rub.nds.scanner.core.probe.AnalyzedProperty;

@JsonIncludeProperties({"type", "value"})
@JsonPropertyOrder({"type", "value"})
public class ObjectResult<T> implements TestResult {

    private final AnalyzedProperty property;
    protected final T value;

    @SuppressWarnings("unused")
    // Default constructor for deserialization
    private ObjectResult() {
        // Default constructor for deserialization
        this.property = null;
        this.value = null;
    }

    public ObjectResult(AnalyzedProperty property, T value) {
        this.property = property;
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public AnalyzedProperty getProperty() {
        return property;
    }

    public <S> S getValue(Class<S> valueClass) {
        return valueClass.cast(value);
    }

    @Override
    public String getName() {
        return property.getName();
    }
}
