/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.passive;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ExtractedValueContainer<ValueT> {

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.CLASS,
            include = JsonTypeInfo.As.PROPERTY,
            property = "@class")
    private List<Object> extractedValueList;

    private final TrackableValue type;

    @SuppressWarnings("unused")
    // Default constructor for Jackson deserialization
    public ExtractedValueContainer() {
        extractedValueList = new LinkedList<>();
        this.type = null;
    }

    /**
     * Creates a new ExtractedValueContainer for the specified TrackableValue type.
     *
     * @param type The type of values this container will track
     */
    public ExtractedValueContainer(TrackableValue type) {
        extractedValueList = new LinkedList<>();
        this.type = type;
    }

    /**
     * Checks if all extracted values in the container are identical.
     *
     * @return true if all values are identical, false otherwise
     */
    @SuppressWarnings("unchecked")
    public boolean areAllValuesIdentical() {
        if (extractedValueList.size() > 0) {
            ValueT value = (ValueT) extractedValueList.get(0);
            for (int i = 1; i < extractedValueList.size(); i++) {
                if (!extractedValueList.get(i).equals(value)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if all extracted values in the container are different from each other.
     *
     * @return true if all values are different, false otherwise
     */
    public boolean areAllValuesDifferent() {
        Set<Object> set = new HashSet<>(extractedValueList);
        return set.size() == extractedValueList.size();
    }

    /**
     * Returns the list of extracted values.
     *
     * @return The list of extracted values of type ValueT
     */
    @SuppressWarnings("unchecked")
    public List<ValueT> getExtractedValueList() {
        return (List<ValueT>) extractedValueList;
    }

    /**
     * Returns the list of extracted values cast to the specified class type.
     *
     * @param <S> The target type to cast values to
     * @param valueClass The class to cast values to
     * @return A list of values cast to type S
     */
    public <S> List<S> getExtractedValueList(Class<S> valueClass) {
        return extractedValueList.stream().map(valueClass::cast).collect(Collectors.toList());
    }

    /**
     * Returns the number of extracted values in the container.
     *
     * @return The number of extracted values
     */
    public int getNumberOfExtractedValues() {
        return extractedValueList.size();
    }

    /**
     * Adds a new value to the container.
     *
     * @param value The value to add to the container
     */
    public void put(ValueT value) {
        extractedValueList.add(value);
    }

    /**
     * Returns the type of values this container tracks.
     *
     * @return The TrackableValue type of this container
     */
    public TrackableValue getType() {
        return type;
    }
}
