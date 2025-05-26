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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.rub.nds.scanner.core.probe.AnalyzedProperty;
import java.util.Map;

/**
 * Represents {@link TestResult}s of type {@link Map} with pairs of type S and T.
 *
 * @param <S> the key types of the map.
 * @param <T> the value types of the map.
 */
@JsonIncludeProperties({"type", "value"})
@JsonPropertyOrder({"type", "value"})
public class MapResult<S, T> implements TestResult {

    private final AnalyzedProperty property;
    private final Map<S, T> map;

    @SuppressWarnings("unused")
    // Default constructor for deserialization
    private MapResult() {
        // Default constructor for deserialization
        this.property = null;
        this.map = null;
    }

    public MapResult(AnalyzedProperty property, Map<S, T> map) {
        this.property = property;
        this.map = map;
    }

    public AnalyzedProperty getProperty() {
        return property;
    }

    @Override
    public String getName() {
        return property.getName();
    }

    /**
     * @return the map of the MapResult object.
     */
    @JsonProperty("value")
    public Map<S, T> getMap() {
        return map;
    }
}
