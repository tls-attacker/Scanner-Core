/*
 * Scanner Core - A modular framework for probe definition, execution, and result analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.probe.result;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Map;

/**
 * Represents {@link TestResult}s of type {@link Map} with pairs of type S and T.
 *
 * @param <S> the key types of the map.
 * @param <T> the value types of the map.
 */
@XmlRootElement(name = "result")
@XmlAccessorType(XmlAccessType.FIELD)
public class MapResult<S, T> implements TestResult, Serializable {

    private final String name;
    private final Map<S, T> map;

    /**
     * The constructor for the MapResult. Use property.getName() for the name parameter.
     *
     * @param map the map.
     * @param name the name of the MapResult.
     */
    public MapResult(Map<S, T> map, String name) {
        this.name = name;
        this.map = map;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * @return the map of the MapResult object.
     */
    public Map<S, T> getMap() {
        return map;
    }
}
