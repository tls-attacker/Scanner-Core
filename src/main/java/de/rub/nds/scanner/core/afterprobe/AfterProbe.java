/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.afterprobe;

import de.rub.nds.scanner.core.report.ScanReport;

/**
 * Abstract base class for post-probe analysis components in the Scanner Core framework.
 *
 * <p>AfterProbes are executed after all regular probe execution has completed, allowing for complex
 * analysis that requires data from multiple probes. They are commonly used for:
 *
 * <ul>
 *   <li>Cross-probe analysis and correlation
 *   <li>Complex vulnerability pattern detection
 *   <li>Derived property computation based on multiple probe results
 *   <li>Statistical analysis and scoring
 * </ul>
 *
 * <p>AfterProbes run in a separate phase from regular probes and have access to the complete scan
 * results. They can modify the report by adding additional analyzed properties or derived
 * conclusions.
 *
 * <p><b>Thread Safety:</b> AfterProbes are executed sequentially after probe completion, so
 * implementations do not need to be thread-safe for accessing the report. However, any shared state
 * between AfterProbe instances should be properly synchronized.
 *
 * <p><b>Example Implementation:</b>
 *
 * <pre>{@code
 * public class VulnerabilityCorrelationAfterProbe extends AfterProbe<ServerReport> {
 *     @Override
 *     public void analyze(ServerReport report) {
 *         boolean hasWeakCrypto = report.getResult(TlsAnalyzedProperty.SUPPORTS_WEAK_CIPHERS) == TestResults.TRUE;
 *         boolean hasOldTls = report.getResult(TlsAnalyzedProperty.SUPPORTS_TLS_1_0) == TestResults.TRUE;
 *
 *         if (hasWeakCrypto && hasOldTls) {
 *             report.putResult(TlsAnalyzedProperty.HIGH_RISK_CONFIGURATION, TestResults.TRUE);
 *         }
 *     }
 * }
 * }</pre>
 *
 * @param <ReportT> the type of scan report this AfterProbe operates on
 * @see de.rub.nds.scanner.core.probe.ScannerProbe
 * @see de.rub.nds.scanner.core.execution.ScanJobExecutor
 */
public abstract class AfterProbe<ReportT extends ScanReport> {

    /**
     * Analyzes the completed scan report and potentially modifies it with additional findings.
     *
     * <p>This method is called after all regular probes have completed execution and merged their
     * results into the report. The implementation can:
     *
     * <ul>
     *   <li>Read any probe results from the report
     *   <li>Add new analyzed properties to the report
     *   <li>Modify existing results (though this should be done carefully)
     *   <li>Access extracted values from passive analysis
     * </ul>
     *
     * <p><b>Implementation Notes:</b>
     *
     * <ul>
     *   <li>Do not assume specific probe execution order or presence
     *   <li>Check for null/undefined results before using probe data
     *   <li>Use appropriate TestResult values when setting new properties
     *   <li>Consider adding logging for complex analysis logic
     * </ul>
     *
     * @param report the scan report containing all probe results and extracted data
     * @throws RuntimeException if analysis fails in an unrecoverable way
     */
    public abstract void analyze(ReportT report);
}
