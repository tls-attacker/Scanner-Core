/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.report;

import de.rub.nds.scanner.core.probe.ProbeType;

public class PerformanceData {

    private ProbeType type;
    private long startTime;
    private long stopTime;

    @SuppressWarnings("unused")
    // Default constructor for deserialization
    private PerformanceData() {
        // Default constructor for deserialization
        this.type = null;
        this.startTime = 0;
        this.stopTime = 0;
    }

    public PerformanceData(ProbeType type, long startTime, long stopTime) {
        this.type = type;
        this.startTime = startTime;
        this.stopTime = stopTime;
    }

    public ProbeType getType() {
        return type;
    }

    public void setType(ProbeType type) {
        this.type = type;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getStopTime() {
        return stopTime;
    }

    public void setStopTime(long stopTime) {
        this.stopTime = stopTime;
    }
}
