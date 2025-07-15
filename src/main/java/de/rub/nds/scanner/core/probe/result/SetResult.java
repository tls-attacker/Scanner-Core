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
import java.util.Set;

/**
 * Represents {@link TestResult}s of type {@link Set} with objects of type T.
 *
 * @param <T> the type of which the SetResult consists.
 */
public class SetResult<T> extends CollectionResult<T> {

    private final Set<T> set;

    @SuppressWarnings("unused")
    private SetResult() {
        // Default constructor for deserialization
        super(null, null);
        this.set = null;
    }

    /**
     * Constructs a SetResult with the specified property and set.
     *
     * @param property the analyzed property associated with this result
     * @param set the set of values for this result
     */
    public SetResult(AnalyzedProperty property, Set<T> set) {
        super(property, set);
        this.set = set;
    }

    /**
     * @return The set of the SetResult.
     */
    public Set<T> getSet() {
        return set;
    }
}
