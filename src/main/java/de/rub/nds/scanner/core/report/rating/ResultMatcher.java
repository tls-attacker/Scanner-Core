/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.report.rating;

import de.rub.nds.scanner.core.probe.result.SummarizableTestResult;
import de.rub.nds.scanner.core.probe.result.TestResult;

/**
 * Matches the {@link TestResult} a scan yielded for a property against the result a {@link
 * RatingInfluencer} or {@link Recommendation} has been configured for.
 */
final class ResultMatcher {

    private ResultMatcher() {
        // Private constructor to prevent instantiation of utility class
    }

    /**
     * Determines whether the actual result of a property matches the result a rating influencer or
     * recommendation has been configured for. The comparison is performed by the actual result, as
     * this may be a {@link SummarizableTestResult}) which compares against its own summary result.
     *
     * @param actualResult the result the scan yielded, may be null
     * @param configuredResult the result the influencer or recommendation is configured for, may be
     *     null
     * @return true if the actual result matches the configured result
     */
    static boolean matches(TestResult actualResult, TestResult configuredResult) {
        if (actualResult == null || configuredResult == null) {
            return false;
        }
        try {
            return actualResult.equalsExpectedResult(configuredResult);
        } catch (IllegalArgumentException e) {
            // The actual result is a complex result which does not know how to compare itself to
            // the configured result.
            return false;
        }
    }
}
