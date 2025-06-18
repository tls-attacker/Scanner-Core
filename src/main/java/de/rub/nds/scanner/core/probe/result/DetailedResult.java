/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.probe.result;

import java.io.Serializable;

public class DetailedResult<T extends Serializable> implements SummarizableTestResult {

    @SuppressWarnings("unused")
    // Default constructor for deserialization
    private DetailedResult() {
        this(null, null);
    }

    /**
     * Creates a DetailedResult with TRUE summary and no details.
     *
     * @param <T> the type of details
     * @return a DetailedResult with TRUE summary
     */
    public static <T extends Serializable> DetailedResult<T> TRUE() {
        return new DetailedResult<>(TestResults.TRUE);
    }

    /**
     * Creates a DetailedResult with TRUE summary and specified details.
     *
     * @param details the details to include
     * @param <T> the type of details
     * @return a DetailedResult with TRUE summary and details
     */
    public static <T extends Serializable> DetailedResult<T> TRUE(T details) {
        return new DetailedResult<>(TestResults.TRUE, details);
    }

    /**
     * Creates a DetailedResult with FALSE summary and no details.
     *
     * @param <T> the type of details
     * @return a DetailedResult with FALSE summary
     */
    public static <T extends Serializable> DetailedResult<T> FALSE() {
        return new DetailedResult<>(TestResults.FALSE);
    }

    /**
     * Creates a DetailedResult with FALSE summary and specified details.
     *
     * @param details the details to include
     * @param <T> the type of details
     * @return a DetailedResult with FALSE summary and details
     */
    public static <T extends Serializable> DetailedResult<T> FALSE(T details) {
        return new DetailedResult<>(TestResults.FALSE, details);
    }

    private final T details;
    private final TestResults summary;

    /**
     * Constructs a new DetailedResult with the specified summary and details.
     *
     * @param summary the test result summary
     * @param details the additional details
     */
    public DetailedResult(TestResults summary, T details) {
        this.details = details;
        this.summary = summary;
    }

    /**
     * Constructs a new DetailedResult with the specified summary and no details.
     *
     * @param summary the test result summary
     */
    public DetailedResult(TestResults summary) {
        this(summary, null);
    }

    /**
     * Gets the details associated with this result.
     *
     * @return the details, may be null
     */
    public T getDetails() {
        return details;
    }

    @Override
    public TestResults getSummarizedResult() {
        return summary;
    }

    @Override
    public boolean isExplicitSummary() {
        return true;
    }
}
