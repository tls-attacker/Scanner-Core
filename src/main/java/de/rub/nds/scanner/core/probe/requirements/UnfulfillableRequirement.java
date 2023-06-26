/*
 * Scanner Core - A modular framework for probe definition, execution, and result analysis.
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
 * A simple requirement which always evaluates to false. This may be used in probes to prevent
 * execution.
 *
 * @see FulfilledRequirement
 */
public final class UnfulfillableRequirement<ReportT extends ScanReport>
        extends Requirement<ReportT> {

    @Override
    public boolean evaluate(ReportT report) {
        return false;
    }

    @Override
    public List<Requirement<ReportT>> getUnfulfilledRequirements(ReportT report) {
        return List.of(this);
    }

    @Override
    public String toString() {
        return "UnfulfillableRequirement";
    }
}
