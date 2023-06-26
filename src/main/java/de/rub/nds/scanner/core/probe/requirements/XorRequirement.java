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

public final class XorRequirement<R extends ScanReport> extends LogicalRequirement<R> {

    private final Requirement<R> a, b;

    public XorRequirement(Requirement<R> a, Requirement<R> b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public boolean evaluate(R report) {
        return a.evaluate(report) ^ b.evaluate(report);
    }

    @Override
    public List<Requirement<R>> getContainedRequirements() {
        return List.of(a, b);
    }

    @Override
    public String toString() {
        return String.format("(%s xor %s)", a, b);
    }
}
