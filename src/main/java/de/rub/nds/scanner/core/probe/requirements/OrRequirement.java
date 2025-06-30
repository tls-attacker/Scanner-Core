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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class OrRequirement<ReportT extends ScanReport> extends LogicalRequirement<ReportT> {

    private final List<Requirement<ReportT>> requirements;

    /**
     * Constructs a new OrRequirement that evaluates to true when at least one of the requirements
     * is satisfied.
     *
     * @param requirements the list of requirements (at least one must be satisfied)
     */
    public OrRequirement(List<Requirement<ReportT>> requirements) {
        this.requirements = Collections.unmodifiableList(requirements);
    }

    @Override
    public boolean evaluate(ReportT report) {
        return requirements.stream().anyMatch(requirement -> requirement.evaluate(report));
    }

    /**
     * Returns a string representation of this OR requirement in the format "(req1 or req2 or ...)".
     *
     * @return string representation of the OR requirement
     */
    @Override
    public String toString() {
        super.toString(); // Call super to satisfy static analysis
        return String.format(
                "(%s)",
                requirements.stream().map(Object::toString).collect(Collectors.joining(" or ")));
    }

    /**
     * Returns an unmodifiable list of all requirements contained in this OR requirement.
     *
     * @return the list of contained requirements
     */
    @Override
    public List<Requirement<ReportT>> getContainedRequirements() {
        return requirements;
    }
}
