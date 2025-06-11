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

/**
 * Core interface representing the result of a test or probe execution within the Scanner Core
 * framework.
 *
 * <p>TestResult serves as the foundation for all test outcomes, providing a consistent contract
 * for result handling, comparison, and serialization. All probe results must implement this
 * interface to ensure compatibility with the scanning and reporting infrastructure.
 *
 * <p>The interface provides several key capabilities:
 *
 * <ul>
 *   <li><strong>Identification:</strong> Each result has a unique name for identification
 *   <li><strong>Validation:</strong> Results can indicate whether they contain actual information
 *   <li><strong>Comparison:</strong> Support for requirement-based evaluation through {@link
 *       #equalsExpectedResult(TestResult)}
 *   <li><strong>Serialization:</strong> All results are serializable for persistence and
 *       transmission
 * </ul>
 *
 * <p>Common implementations include:
 *
 * <ul>
 *   <li>{@link TestResults} - Enumerated test outcomes (TRUE, FALSE, UNCERTAIN, etc.)
 *   <li>{@link CollectionResult} - Results containing collections of values
 *   <li>{@link SummarizableTestResult} - Complex results that can be summarized
 * </ul>
 *
 * <p>Usage example:
 *
 * <pre>{@code
 * public class CustomTestResult implements TestResult {
 *     private final String name;
 *     private final boolean value;
 *
 *     @Override
 *     public String getName() {
 *         return name;
 *     }
 *
 *     @Override
 *     public boolean isRealResult() {
 *         return true; // This result contains actual data
 *     }
 * }
 * }</pre>
 *
 * @see TestResults
 * @see SummarizableTestResult
 * @see CollectionResult
 * @see de.rub.nds.scanner.core.probe.requirements.PropertyValueRequirement
 */
public interface TestResult extends Serializable {

    /**
     * @return the name of the TestResult.
     */
    String getName();

    /**
     * Returns true if the result stored for the property contains actual information. True by
     * default.
     *
     * @return Whether the result contains actual information.
     */
    default boolean isRealResult() {
        return true;
    }

    default String getType() {
        return this.getClass().getSimpleName();
    }

    /**
     * Function used to check if the actual result is equal to the expected result. This is used in
     * the requirement system (cf. {@link
     * de.rub.nds.scanner.core.probe.requirements.PropertyValueRequirement}). By default, this
     * function checks if the actual result and the expected result are of the same type and then
     * uses the default equals implementation. If the types do not match, equals is not called and
     * an exception is thrown.
     *
     * <p>It can be useful to overwrite this to allow a more complex result to evaluate to a simple
     * result.
     *
     * <p>Example: A result might be checked for each version of a protocol. Each of these checks
     * results in a TestResults enum. The actual result can overwrite this function to say that it
     * is equal to TRUE if it is TRUE in one version.
     *
     * @param expectedResult The expected result stated in the requirement.
     * @return Whether the actual result is equal to the expected result.
     */
    default boolean equalsExpectedResult(TestResult expectedResult) {
        if (!getClass().equals(expectedResult.getClass())) {
            throw new IllegalArgumentException(
                    String.format(
                            "Cannot Compare actual result with expected result (type mismatch: found %s but expected %s)"
                                    + " - Consider overwriting equalsExpectedResult if the type mismatch is intended.",
                            this.getClass(), expectedResult.getClass()));
        }
        return this.equals(expectedResult);
    }
}
