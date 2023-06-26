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
import java.util.List;

/**
 * Represents {@link TestResult}s of type {@link List} with objects of type T.
 *
 * @param <T> the type of the list elements.
 */
@XmlRootElement(name = "result")
@XmlAccessorType(XmlAccessType.FIELD)
public class ListResult<T> extends CollectionResult<T> {

    /**
     * The constructor for the ListResult. Use property.getName() for the name parameter.
     *
     * @param list the list of the ListResult.
     * @param name the name of the ListResult.
     */
    public ListResult(List<T> list, String name) {
        super(list, name);
    }

    /**
     * @return the list of the listResult object.
     */
    public List<T> getList() {
        return (List<T>) collection;
    }
}
