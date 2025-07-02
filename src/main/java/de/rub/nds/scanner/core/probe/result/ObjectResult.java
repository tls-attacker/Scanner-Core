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
    private ObjectResult() {
        // Default constructor for deserialization
        this.property = null;
        this.value = null;
    }

    /**
     * Constructs an ObjectResult with the specified property and value.
     *
     * @param property the analyzed property associated with this result
     * @param value the value of this result
     */
    public ObjectResult(AnalyzedProperty property, T value) {
        this.property = property;
        this.value = value;
    }

    /**
     * Returns the value of this result.
     *
     * @return the value
     */
    public T getValue() {
        return value;
    }

    /**
     * Returns the analyzed property associated with this result.
     *
     * @return the analyzed property
     */
    public AnalyzedProperty getProperty() {
        return property;
    }

    /**
     * Returns the value of this result cast to the specified type.
     *
     * @param <S> the type to cast the value to
     * @param valueClass the class to cast the value to
     * @return the value cast to the specified type
     * @throws ClassCastException if the value cannot be cast to the specified type
     */
    public <S> S getValue(Class<S> valueClass) {
        return valueClass.cast(value);
    }

    @Override
    public String getName() {
        return property.getName();
    }
}
