/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.execution;

import de.rub.nds.scanner.core.probe.ScannerProbe;
import de.rub.nds.scanner.core.report.ScanReport;

/**
 * Callback interface for receiving probe execution progress updates. This interface allows external
 * components to be notified when individual probes complete during a scan, enabling real-time
 * progress monitoring and streaming of partial results.
 *
 * @param <ReportT> the type of scan report
 * @param <StateT> the type of state object used by probes
 */
@FunctionalInterface
public interface ProbeProgressCallback<ReportT extends ScanReport, StateT> {

    /**
     * Called when a probe has completed execution and merged its results into the report.
     *
     * @param probe the probe that completed execution
     * @param report the scan report with the probe's results merged in
     * @param completedProbes the number of probes that have completed so far
     * @param totalProbes the total number of probes scheduled for this scan
     */
    void onProbeCompleted(
            ScannerProbe<ReportT, StateT> probe,
            ReportT report,
            int completedProbes,
            int totalProbes);

    /**
     * Creates a no-op callback that does nothing when probes complete. Useful as a default when no
     * progress tracking is needed.
     *
     * @param <R> the type of scan report
     * @param <S> the type of state object
     * @return a callback that performs no operations
     */
    static <R extends ScanReport, S> ProbeProgressCallback<R, S> noOp() {
        return (probe, report, completedProbes, totalProbes) -> {
            // No operation
        };
    }
}
