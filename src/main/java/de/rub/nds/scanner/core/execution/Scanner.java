/*
 * Scanner Core - A modular framework for probe definition, execution, and result analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.execution;

import de.rub.nds.scanner.core.afterprobe.AfterProbe;
import de.rub.nds.scanner.core.probe.ProbeType;
import de.rub.nds.scanner.core.probe.ScannerProbe;
import de.rub.nds.scanner.core.report.ScanReport;
import java.util.LinkedList;
import java.util.List;

public abstract class Scanner<
        ReportT extends ScanReport,
        ProbeT extends ScannerProbe<ReportT, StateT>,
        AfterProbeT extends AfterProbe<ReportT>,
        StateT> {
    protected final List<ProbeT> probeList;
    protected final List<AfterProbeT> afterList;
    protected final List<ProbeType> probeTypesToExecute;

    public Scanner(List<ProbeType> probesToExecute) {
        this(new LinkedList<>(), new LinkedList<>(), probesToExecute);
    }

    public Scanner(
            List<ProbeT> probeList,
            List<AfterProbeT> afterList,
            List<ProbeType> probeTypesToExecute) {
        this.probeTypesToExecute = probeTypesToExecute;
        this.afterList = afterList;
        this.probeList = probeList;
    }

    protected abstract void fillProbeLists();

    protected void addProbeToProbeList(ProbeT probe) {
        addProbeToProbeList(probe, true);
    }

    protected void addProbeToProbeList(ProbeT probe, boolean addByDefault) {
        if ((probeTypesToExecute == null && addByDefault)
                || (probeTypesToExecute != null && probeTypesToExecute.contains(probe.getType()))) {
            probeList.add(probe);
        }
    }
}
