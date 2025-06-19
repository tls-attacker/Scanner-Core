/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.execution;

import de.rub.nds.scanner.core.report.ScanReport;

/**
 * Abstract base class for executing scan jobs. Implementations of this class define specific
 * strategies for executing scan jobs and managing their lifecycle.
 *
 * @param <ReportT> the type of scan report this executor works with
 */
public abstract class ScanJobExecutor<ReportT extends ScanReport> {

    /**
     * Executes scan jobs and populates the provided report with results.
     *
     * @param report the report to populate with scan results
     * @throws InterruptedException if the execution is interrupted
     */
    public abstract void execute(ReportT report) throws InterruptedException;

    /**
     * Shuts down the executor and releases any resources. This method should be called when the
     * executor is no longer needed.
     */
    public abstract void shutdown();
}
