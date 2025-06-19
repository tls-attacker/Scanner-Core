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

public class IntegerResult extends ObjectResult<Integer> {

    @SuppressWarnings("unused")
    // Default constructor for deserialization
    private IntegerResult() {
        super(null, null);
    }

    /**
     * Constructs an IntegerResult with the specified property and value.
     *
     * @param property the analyzed property associated with this result
     * @param value the Integer value of this result
     */
    public IntegerResult(AnalyzedProperty property, Integer value) {
        super(property, value);
    }
}
