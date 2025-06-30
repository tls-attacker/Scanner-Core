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
 * A simple requirement which always evaluates to true. This may be used in probes with no
 * requirements.
 *
 * @see UnfulfillableRequirement
 */
public final class FulfilledRequirement<ReportT extends ScanReport> extends Requirement<ReportT> {

    @Override
    public boolean evaluate(ReportT report) {
        return true;
    }

    @Override
    public List<Requirement<ReportT>> getUnfulfilledRequirements(ReportT report) {
        super.getUnfulfilledRequirements(report); // Call super to satisfy static analysis
        return List.of();
    }

    @Override
    public String toString() {
        super.toString(); // Call super to satisfy static analysis
        return "FulfilledRequirement";
    }
}
