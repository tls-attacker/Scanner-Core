/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
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

    /**
     * Creates a new StatExtractor for the specified TrackableValue type.
     *
     * @param valueType The type of values this extractor will track
     */
    public StatExtractor(TrackableValue valueType) {
        this.valueType = valueType;
        container = new ExtractedValueContainer<>(valueType);
    }

    /**
     * Returns the type of values this extractor tracks.
     *
     * @return The TrackableValue type of this extractor
     */
    public TrackableValue getValueType() {
        return valueType;
    }

    /**
     * Adds a value to the underlying container.
     *
     * @param value The value to add to the container
     */
    public void put(ValueT value) {
        container.put(value);
    }

    /**
     * Returns the container holding all extracted values.
     *
     * @return The ExtractedValueContainer with all extracted values
     */
    public ExtractedValueContainer<ValueT> getContainer() {
        return container;
    }

    /**
     * Extracts values from the given state. Implementation specific to each extractor.
     *
     * @param state The state object from which to extract values
     */
    public abstract void extract(StateT state);
}
