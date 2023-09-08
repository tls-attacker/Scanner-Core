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

public final class XorRequirement<ReportT extends ScanReport> extends LogicalRequirement<ReportT> {

    private final Requirement<ReportT> a, b;

    public XorRequirement(Requirement<ReportT> a, Requirement<ReportT> b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public boolean evaluate(ReportT report) {
        return a.evaluate(report) ^ b.evaluate(report);
    }

    @Override
    public List<Requirement<ReportT>> getContainedRequirements() {
        return List.of(a, b);
    }

    @Override
    public String toString() {
        return String.format("(%s xor %s)", a, b);
    }
}
