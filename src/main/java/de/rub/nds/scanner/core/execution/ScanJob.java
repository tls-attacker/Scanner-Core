/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.execution;

import de.rub.nds.scanner.core.afterprobe.AfterProbe;
import de.rub.nds.scanner.core.probe.ScannerProbe;
import de.rub.nds.scanner.core.report.ScanReport;
import java.util.LinkedList;
import java.util.List;

public class ScanJob<
        ReportT extends ScanReport,
        ProbeT extends ScannerProbe<ReportT, StateT>,
        AfterProbeT extends AfterProbe<ReportT>,
        StateT> {

    private final List<ProbeT> probeList;
    private final List<AfterProbeT> afterList;

    public ScanJob(List<ProbeT> probeList, List<AfterProbeT> afterList) {
        this.probeList = new LinkedList<>(probeList);
        this.afterList = new LinkedList<>(afterList);
    }

    public List<ProbeT> getProbeList() {
        return new LinkedList<>(probeList);
    }

    public List<AfterProbeT> getAfterList() {
        return new LinkedList<>(afterList);
    }
}
