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
 * Interface for complex test results that can be summarized into a single {@link TestResults}
 * value while maintaining detailed information.
 *
 * <p>SummarizableTestResult bridges the gap between detailed, complex test outcomes and simple
 * boolean or enumerated results. This interface allows sophisticated test results to participate
 * in the requirement system while preserving their detailed nature for reporting and analysis.
 *
 * <p>The summarization can occur in two ways:
 *
 * <ul>
 *   <li><strong>Dynamic Summarization:</strong> The summary is computed on-the-fly from the
 *       detailed results (e.g., aggregating multiple sub-results)
 *   <li><strong>Explicit Summarization:</strong> The summary is set explicitly, typically in error
 *       conditions or when detailed analysis is unavailable
 * </ul>
 *
 * <p>This interface is particularly useful for:
 *
 * <ul>
 *   <li>Multi-version protocol tests (summarizing results across TLS versions)
 *   <li>Vulnerability assessments with detailed findings
 *   <li>Certificate validation results with multiple checks
 *   <li>Cipher suite analysis with comprehensive details
 * </ul>
 *
 * <p>The interface provides enhanced support for requirement evaluation by allowing comparison
 * against simple {@link TestResults} values, enabling complex results to be used in conditional
 * probe execution.
 *
 * <p>Usage example:
 *
 * <pre>{@code
 * public class CipherSuiteResult implements SummarizableTestResult {
 *     private final Map<TlsVersion, List<CipherSuite>> supportedSuites;
 *     private TestResults explicitSummary;
 *
 *     @Override
 *     public TestResults getSummarizedResult() {
 *         if (explicitSummary != null) {
 *             return explicitSummary;
 *         }
 *         // Dynamic summarization
 *         return supportedSuites.isEmpty() ? TestResults.FALSE : TestResults.TRUE;
 *     }
 *
 *     @Override
 *     public boolean isExplicitSummary() {
 *         return explicitSummary != null;
 *     }
 * }
 * }</pre>
 *
 * @see TestResult
 * @see TestResults
 * @see de.rub.nds.scanner.core.probe.requirements.PropertyValueRequirement
 */
public interface SummarizableTestResult extends TestResult {
    /**
     * Returns the summarized version of this complex test result.
     *
     * <p>This method provides a simplified {@link TestResults} representation of the detailed
     * result. The summary can be computed dynamically from the contained data or set explicitly.
     *
     * @return the summarized result as a TestResults enum value, never null
     * @see #isExplicitSummary()
     */
    TestResults getSummarizedResult();

    /**
     * Indicates whether the summary was explicitly set or computed dynamically.
     *
     * <p>This method helps distinguish between two types of summarization:
     *
     * <ul>
     *   <li><strong>Explicit (true):</strong> The summary was manually set, typically in error
     *       conditions or when detailed analysis is not possible
     *   <li><strong>Dynamic (false):</strong> The summary is computed on-the-fly from the detailed
     *       data contained within this result
     * </ul>
     *
     * @return true if the summary was explicitly set, false if computed dynamically
     * @see #getSummarizedResult()
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
