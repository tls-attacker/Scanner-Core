/*
 * Scanner Core - A modular framework for probe definition, execution, and result analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.probe.result;

import java.io.Serializable;
import java.util.Collection;

/**
 * Represents {@link TestResult}s of type {@link Collection} with objects of type T.
 *
 * @param <T> the type of which the CollectionResult consists.
 */
public class CollectionResult<T> implements TestResult, Serializable {

    private final String name;
    protected final Collection<T> collection;

    /**
     * The constructor for the CollectionResult. Use property.getName() for the name parameter.
     *
     * @param collection The result collection.
     * @param name The name of the CollectionResult object.
     */
    public CollectionResult(Collection<T> collection, String name) {
        this.collection = collection;
        this.name = name;
    }

    /**
     * @return the collection of the CollectionResult object of type T.
     */
    public Collection<T> getCollection() {
        return collection;
    }

    @Override
    public String getName() {
        return name;
    }
}