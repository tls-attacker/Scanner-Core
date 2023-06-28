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

public final class NotRequirement<ReportT extends ScanReport> extends LogicalRequirement<ReportT> {

    private final Requirement<ReportT> requirement;

    public NotRequirement(Requirement<ReportT> requirement) {
        this.requirement = requirement;
    }

    @Override
    public boolean evaluate(ReportT report) {
        return requirement != null && !requirement.evaluate(report);
    }

    @Override
    public List<Requirement<ReportT>> getContainedRequirements() {
        return List.of(requirement);
    }

    @Override
    public String toString() {
        return String.format("not(%s)", requirement);
    }
}
