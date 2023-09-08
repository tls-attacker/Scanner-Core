/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.probe.result;

import de.rub.nds.scanner.core.probe.AnalyzedProperty;
import java.util.List;

/**
 * Represents {@link TestResult}s of type {@link List} with objects of type T.
 *
 * @param <T> the type of the list elements.
 */
public class ListResult<T> extends CollectionResult<T> {

    public ListResult(AnalyzedProperty property, List<T> list) {
        super(property, list);
    }

    /**
     * @return the list of the listResult object.
     */
    public List<T> getList() {
        return (List<T>) collection;
    }
}
