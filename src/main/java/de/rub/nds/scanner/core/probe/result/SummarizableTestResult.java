/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.probe.result;

/**
 * A complicated test result that can still be summarized into a single TestResults. This summary
 * might be generated on the fly (from the contained details), or set explicitly (e.g. in case of an
 * error)
 */
public interface SummarizableTestResult extends TestResult {
    /**
     * Returns the summarized result of this test result.
     *
     * @return the summarized TestResults value
     */
    TestResults getSummarizedResult();

    /**
     * Indicates whether the summary was explicitly set instead of generated on the fly.
     *
     * @return true if the summary was explicitly set, false if generated dynamically
     */
    boolean isExplicitSummary();

    @Override
    default boolean equalsExpectedResult(TestResult other) {
        if (other instanceof TestResults) {
            return getSummarizedResult().equals(other);
        }
        return this.equals(other);
    }

    @Override
    default String getName() {
        return getSummarizedResult().getName();
    }
}
