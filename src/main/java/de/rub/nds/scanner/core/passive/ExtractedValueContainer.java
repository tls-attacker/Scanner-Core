/*
 * Scanner Core - A modular framework for probe definition, execution, and result analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.passive;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ExtractedValueContainer<ValueT> {

    private final List<ValueT> extractedValueList;

    private final TrackableValue type;

    public ExtractedValueContainer(TrackableValue type) {
        extractedValueList = new LinkedList<>();
        this.type = type;
    }

    public boolean areAllValuesIdentical() {
        if (extractedValueList.size() > 0) {
            ValueT value = extractedValueList.get(0);
            for (int i = 1; i < extractedValueList.size(); i++) {
                if (!extractedValueList.get(i).equals(value)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean areAllValuesDifferent() {
        Set<ValueT> set = new HashSet<>(extractedValueList);
        return set.size() == extractedValueList.size();
    }

    @JsonValue
    public List<ValueT> getExtractedValueList() {
        return extractedValueList;
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
}
