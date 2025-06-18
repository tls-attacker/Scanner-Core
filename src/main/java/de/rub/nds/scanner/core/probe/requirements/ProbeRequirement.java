/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.probe.requirements;

import de.rub.nds.scanner.core.probe.ProbeType;
import de.rub.nds.scanner.core.report.ScanReport;
import java.util.List;
import java.util.stream.Collectors;

/** Represents a {@link Requirement} for required executed {@link ProbeType}s. */
public class ProbeRequirement<ReportT extends ScanReport>
        extends PrimitiveRequirement<ReportT, ProbeType> {

    /**
     * Constructs a new ProbeRequirement with the specified list of probe types.
     *
     * @param probes the probe types that must be executed
     */
    public ProbeRequirement(List<ProbeType> probes) {
        super(probes);
    }

    /**
     * Constructs a new ProbeRequirement with the specified probe types.
     *
     * @param probes the probe types that must be executed
     */
    public ProbeRequirement(ProbeType... probes) {
        super(List.of(probes));
    }

    @Override
    public boolean evaluate(ReportT report) {
        if (parameters.size() == 0) {
            return true;
        }
        for (ProbeType probe : parameters) {
            if (!report.isProbeAlreadyExecuted(probe)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format(
                "ProbeRequirement[%s]",
                parameters.stream().map(Object::toString).collect(Collectors.joining(", ")));
    }
}
