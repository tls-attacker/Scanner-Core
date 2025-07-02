/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.probe.result;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Enum for simple {@link TestResult}s which hold a value for the result of the evaluation of a
 * property.
 */
@XmlRootElement(name = "result")
public enum TestResults implements SummarizableTestResult {
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

    TestResults() {}

    @Override
    public String getName() {
        return name();
    }

    /**
     * Creates a TestResults enum value from a string representation.
     *
     * @param value the string representation of the TestResults
     * @return the corresponding TestResults enum value
     * @throws IllegalArgumentException if the value does not match any TestResults constant
     */
    @JsonCreator
    public static TestResults fromString(String value) {
        return TestResults.valueOf(value);
    }

    /**
     * Converts a boolean value to a TestResults enum value.
     *
     * @param value the boolean value to convert
     * @return TestResults.TRUE if true, TestResults.FALSE if false
     */
    public static TestResults of(boolean value) {
        return value ? TRUE : FALSE;
    }

    @Override
    public TestResults getSummarizedResult() {
        return this;
    }

    @Override
    public boolean isExplicitSummary() {
        return true;
    }
}
