/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.passive;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ExtractedValueContainer<ValueT> {

    @JsonIgnore // TODO fix this
    private List<Object> extractedValueList;

    private TrackableValue type;

    @SuppressWarnings("unused")
    // Default constructor for Jackson deserialization
    public ExtractedValueContainer() {
        extractedValueList = new LinkedList<>();
        this.type = null;
    }

    public ExtractedValueContainer(TrackableValue type) {
        extractedValueList = new LinkedList<>();
        this.type = type;
    }

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

    public boolean areAllValuesDifferent() {
        Set<Object> set = new HashSet<>(extractedValueList);
        return set.size() == extractedValueList.size();
    }

    @SuppressWarnings("unchecked")
    @JsonValue
    public List<ValueT> getExtractedValueList() {
        return (List<ValueT>) extractedValueList;
    }

    public <S> List<S> getExtractedValueList(Class<S> valueClass) {
        return extractedValueList.stream().map(valueClass::cast).collect(Collectors.toList());
    }

    public int getNumberOfExtractedValues() {
        return extractedValueList.size();
    }

    public void put(ValueT value) {
        extractedValueList.add(value);
    }

    public TrackableValue getType() {
        return type;
    }

    /**
     * Sets the type of this container. This is used during deserialization.
     *
     * @param type The TrackableValue type
     */
    public void setType(TrackableValue type) {
        this.type = type;
    }
}
