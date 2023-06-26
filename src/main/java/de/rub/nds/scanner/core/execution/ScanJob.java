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
import de.rub.nds.scanner.core.probe.ScannerProbe;
import de.rub.nds.scanner.core.report.ScanReport;
import java.util.List;

public class ScanJob<
        R extends ScanReport<R>, P extends ScannerProbe<R, S>, AP extends AfterProbe<R>, S> {

    private final List<P> probeList;
    private final List<AP> afterList;

    public ScanJob(List<P> probeList, List<AP> afterList) {
        this.probeList = probeList;
        this.afterList = afterList;
    }

    public List<P> getProbeList() {
        return probeList;
    }

    public List<AP> getAfterList() {
        return afterList;
    }
}
