/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.probe.result;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.rub.nds.scanner.core.probe.AnalyzedProperty;
import java.util.Collection;

/**
 * Represents {@link TestResult}s of type {@link Collection} with objects of type T.
 *
 * @param <T> the type of which the CollectionResult consists.
 */
@JsonIncludeProperties({"type", "value"})
@JsonPropertyOrder({"type", "value"})
public class CollectionResult<T> implements TestResult {

    private final AnalyzedProperty property;
    protected final Collection<T> collection;

    public CollectionResult(AnalyzedProperty property, Collection<T> collection) {
        this.property = property;
        this.collection = collection;
    }

    public AnalyzedProperty getProperty() {
        return property;
    }

    /**
     * @return the collection of the CollectionResult object of type T.
     */
    @JsonGetter("value")
    public Collection<T> getCollection() {
        return collection;
    }

    @Override
    public String getName() {
        return property.getName();
    }
}
