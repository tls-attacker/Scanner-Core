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

/**
 * A simple requirement combining two or more requirements with a logical AND.
 *
 * @param <ReportT> the type of scan report this requirement operates on
 */
public final class AndRequirement<ReportT extends ScanReport> extends LogicalRequirement<ReportT> {

    /** The list of requirements to be combined with logical AND. */
    private final List<Requirement<ReportT>> requirements;

    /**
     * Creates a new AndRequirement with the specified list of requirements.
     *
     * @param requirements the list of requirements to combine with logical AND
     */
    public AndRequirement(List<Requirement<ReportT>> requirements) {
        this.requirements = Collections.unmodifiableList(requirements);
    }

    @Override
    public boolean evaluate(ReportT report) {
        return requirements.stream().allMatch(requirement -> requirement.evaluate(report));
    }

    @Override
    public List<Requirement<ReportT>> getUnfulfilledRequirements(ReportT report) {
        return requirements.stream()
                .filter(requirement -> !requirement.evaluate(report))
                .flatMap(requirement -> requirement.getUnfulfilledRequirements(report).stream())
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<Requirement<ReportT>> getContainedRequirements() {
        return requirements;
    }

    @Override
    public String toString() {
        return String.format(
                "(%s)",
                requirements.stream().map(Object::toString).collect(Collectors.joining(" and ")));
    }
}
