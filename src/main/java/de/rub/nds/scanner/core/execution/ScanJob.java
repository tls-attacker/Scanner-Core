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

/**
 * Represents a collection of probes and after-probes to be executed during a scan. A scan job
 * encapsulates the work to be performed as part of a scanning operation.
 *
 * @param <ReportT> the type of scan report
 * @param <ProbeT> the type of scanner probe
 * @param <AfterProbeT> the type of after-probe
 * @param <StateT> the type of state object used by probes
 */
public class ScanJob<
        ReportT extends ScanReport,
        ProbeT extends ScannerProbe<ReportT, StateT>,
        AfterProbeT extends AfterProbe<ReportT>,
        StateT> {

    private final List<ProbeT> probeList;
    private final List<AfterProbeT> afterList;

    /**
     * Creates a new scan job with the specified probes and after-probes.
     *
     * @param probeList the list of probes to execute
     * @param afterList the list of after-probes to execute after all probes complete
     */
    public ScanJob(List<ProbeT> probeList, List<AfterProbeT> afterList) {
        this.probeList = new LinkedList<>(probeList);
        this.afterList = new LinkedList<>(afterList);
    }

    /**
     * Returns a copy of the probe list.
     *
     * @return a new list containing all probes in this scan job
     */
    public List<ProbeT> getProbeList() {
        return new LinkedList<>(probeList);
    }

    /**
     * Returns a copy of the after-probe list.
     *
     * @return a new list containing all after-probes in this scan job
     */
    public List<AfterProbeT> getAfterList() {
        return new LinkedList<>(afterList);
    }
}
