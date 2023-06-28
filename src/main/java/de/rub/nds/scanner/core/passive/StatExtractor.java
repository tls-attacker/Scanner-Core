/*
 * Scanner Core - A modular framework for probe definition, execution, and result analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.passive;

public abstract class StatExtractor<StateT, ValueT> {

    private final ExtractedValueContainer<ValueT> container;
    private final TrackableValue valueType;

    public StatExtractor(TrackableValue valueType) {
        this.valueType = valueType;
        container = new ExtractedValueContainer<>(valueType);
    }

    public TrackableValue getValueType() {
        return valueType;
    }

    public void put(ValueT value) {
        container.put(value);
    }

    public ExtractedValueContainer<ValueT> getContainer() {
        return container;
    }

    public abstract void extract(StateT state);
}
