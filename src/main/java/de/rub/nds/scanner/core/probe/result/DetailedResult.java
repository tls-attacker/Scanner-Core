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
     * Creates a DetailedResult with a TRUE summary and no details.
     *
     * @param <T> the type of details
     * @return a DetailedResult with TRUE summary
     */
    public static <T extends Serializable> DetailedResult<T> TRUE() {
        return new DetailedResult<>(TestResults.TRUE);
    }

    /**
     * Creates a DetailedResult with a TRUE summary and specified details.
     *
     * @param <T> the type of details
     * @param details the details to include
     * @return a DetailedResult with TRUE summary and details
     */
    public static <T extends Serializable> DetailedResult<T> TRUE(T details) {
        return new DetailedResult<>(TestResults.TRUE, details);
    }

    /**
     * Creates a DetailedResult with a FALSE summary and no details.
     *
     * @param <T> the type of details
     * @return a DetailedResult with FALSE summary
     */
    public static <T extends Serializable> DetailedResult<T> FALSE() {
        return new DetailedResult<>(TestResults.FALSE);
    }

    /**
     * Creates a DetailedResult with a FALSE summary and specified details.
     *
     * @param <T> the type of details
     * @param details the details to include
     * @return a DetailedResult with FALSE summary and details
     */
    public static <T extends Serializable> DetailedResult<T> FALSE(T details) {
        return new DetailedResult<>(TestResults.FALSE, details);
    }

    private final T details;
    private final TestResults summary;

    /**
     * Constructs a DetailedResult with the specified summary and details.
     *
     * @param summary the summary result
     * @param details the details associated with this result
     */
    public DetailedResult(TestResults summary, T details) {
        this.details = details;
        this.summary = summary;
    }

    /**
     * Constructs a DetailedResult with the specified summary and no details.
     *
     * @param summary the summary result
     */
    public DetailedResult(TestResults summary) {
        this(summary, null);
    }

    /**
     * Returns the details associated with this result.
     *
     * @return the details, or null if no details were provided
     */
    public T getDetails() {
        return details;
    }

    /**
     * Returns the summarized result.
     *
     * @return the summary TestResults value
     */
    @Override
    public TestResults getSummarizedResult() {
        return summary;
    }

    /**
     * Indicates whether the summary was explicitly set.
     *
     * @return always true for DetailedResult
     */
    @Override
    public boolean isExplicitSummary() {
        return true;
    }
}
