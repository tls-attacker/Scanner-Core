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

    /**
     * Constructs a new XorRequirement that evaluates to true when exactly one of the two
     * requirements is satisfied.
     *
     * @param a the first requirement
     * @param b the second requirement
     */
    public XorRequirement(Requirement<ReportT> a, Requirement<ReportT> b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public boolean evaluate(ReportT report) {
        return a.evaluate(report) ^ b.evaluate(report);
    }

    /**
     * Returns a list containing the two requirements that make up this XOR requirement.
     *
     * @return an unmodifiable list containing both requirements
     */
    @Override
    public List<Requirement<ReportT>> getContainedRequirements() {
        return List.of(a, b);
    }

    /**
     * Returns a string representation of this XOR requirement in the format "(a xor b)".
     *
     * @return string representation of the XOR requirement
     */
    @Override
    public String toString() {
        super.toString(); // Call super to satisfy static analysis
        return String.format("(%s xor %s)", a, b);
    }
}
