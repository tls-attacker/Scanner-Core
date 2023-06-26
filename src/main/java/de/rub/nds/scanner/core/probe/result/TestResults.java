/*
 * Scanner Core - A modular framework for probe definition, execution, and result analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.probe.result;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Enum for simple {@link TestResult}s which hold a value for the result of the evaluation of a
 * property.
 */
@XmlRootElement(name = "result")
public enum TestResults implements TestResult {
    TRUE,
    FALSE,
    PARTIALLY,
    CANNOT_BE_TESTED,
    COULD_NOT_TEST,
    ERROR_DURING_TEST,
    UNCERTAIN,
    UNSUPPORTED,
    NOT_TESTED_YET,
    UNASSIGNED_ERROR,
    TIMEOUT;

    @Override
    public String getName() {
        return name();
    }

    /**
     * @param value evaluation of a boolean to TestResults.
     * @return TestResults.TRUE if true and TestResults.FALSE if false
     */
    public static TestResults of(boolean value) {
        return value ? TRUE : FALSE;
    }
}
