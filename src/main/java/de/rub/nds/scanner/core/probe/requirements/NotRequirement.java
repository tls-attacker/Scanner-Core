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

public final class NotRequirement<ReportT extends ScanReport> extends LogicalRequirement<ReportT> {

    private final Requirement<ReportT> requirement;

    /**
     * Constructs a new NotRequirement that negates the evaluation of the given requirement.
     *
     * @param requirement the requirement to negate
     */
    public NotRequirement(Requirement<ReportT> requirement) {
        this.requirement = requirement;
    }

    @Override
    public boolean evaluate(ReportT report) {
        return requirement != null && !requirement.evaluate(report);
    }

    /**
     * Returns a list containing the single requirement that is being negated.
     *
     * @return list containing the negated requirement
     */
    @Override
    public List<Requirement<ReportT>> getContainedRequirements() {
        return List.of(requirement);
    }

    /**
     * Returns a string representation of this NOT requirement in the format "not(requirement)".
     *
     * @return string representation of the NOT requirement
     */
    @Override
    public String toString() {
        return String.format("not(%s)", requirement);
    }
}
