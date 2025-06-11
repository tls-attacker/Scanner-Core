/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.probe.requirements;

import de.rub.nds.scanner.core.report.ScanReport;
import java.util.List;

/**
 * A requirement combining one or more requirements using a logical operation.
 *
 * @param <ReportT> the type of scan report this requirement operates on
 */
public abstract class LogicalRequirement<ReportT extends ScanReport> extends Requirement<ReportT> {
    /**
     * Returns a list of requirements contained in this logical requirement.
     *
     * @return A list of requirements contained in this logical requirement.
     */
    public abstract List<Requirement<ReportT>> getContainedRequirements();
}
