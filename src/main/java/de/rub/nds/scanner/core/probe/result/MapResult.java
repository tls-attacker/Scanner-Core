/*
 * Scanner Core - A modular framework for probe definition, execution, and result analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.probe.result;

import de.rub.nds.scanner.core.probe.AnalyzedProperty;
import java.io.Serializable;
import java.util.Map;

/**
 * Represents {@link TestResult}s of type {@link Map} with pairs of type S and T.
 *
 * @param <S> the key types of the map.
 * @param <T> the value types of the map.
 */
public class MapResult<S, T> implements TestResult, Serializable {

    private final AnalyzedProperty property;
    private final Map<S, T> map;

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
    public Map<S, T> getMap() {
        return map;
    }
}
