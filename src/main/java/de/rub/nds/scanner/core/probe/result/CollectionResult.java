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
import java.util.Collection;

/**
 * Represents {@link TestResult}s of type {@link Collection} with objects of type T.
 *
 * @param <T> the type of which the CollectionResult consists.
 */
public class CollectionResult<T> implements TestResult, Serializable {

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
    public Collection<T> getCollection() {
        return collection;
    }

    @Override
    public String getName() {
        return property.getName();
    }
}
