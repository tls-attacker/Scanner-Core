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
import java.math.BigInteger;

public class BigIntegerResult extends ObjectResult<BigInteger> {

    @SuppressWarnings("unused")
    // Default constructor for deserialization
    private BigIntegerResult() {
        super(null, null);
    }

    /**
     * Constructs a BigIntegerResult with the specified property and value.
     *
     * @param property the analyzed property associated with this result
     * @param value the BigInteger value of this result
     */
    public BigIntegerResult(AnalyzedProperty property, BigInteger value) {
        super(property, value);
    }
}
