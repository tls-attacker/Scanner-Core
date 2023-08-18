/*
 * Scanner Core - A modular framework for probe definition, execution, and result analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.guideline;

public enum GuidelineAdherence {
    ADHERED,
    VIOLATED,
    CONDITION_NOT_MET,
    CHECK_FAILED;

    /**
     * @param value evaluation of a boolean to GuidelineAdherence.
     * @return GuidelineAdherence.ADHERED if true and GuidelineAdherence.VIOLATED if false
     */
    public static GuidelineAdherence of(boolean value) {
        return value ? ADHERED : VIOLATED;
    }
}
