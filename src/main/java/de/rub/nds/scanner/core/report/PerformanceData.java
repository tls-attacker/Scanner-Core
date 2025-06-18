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

/**
 * Container for performance data of a single probe execution.
 * Tracks timing information for performance analysis.
 */
public class PerformanceData {

    private ProbeType type;
    private long startTime;
    private long stopTime;

    @SuppressWarnings("unused")
    private PerformanceData() {
        // Default constructor for deserialization
        this.type = null;
        this.startTime = 0;
        this.stopTime = 0;
    }

    /**
     * Creates a new PerformanceData instance with the specified parameters.
     *
     * @param type the type of probe
     * @param startTime the start time in milliseconds
     * @param stopTime the stop time in milliseconds
     */
    public PerformanceData(ProbeType type, long startTime, long stopTime) {
        this.type = type;
        this.startTime = startTime;
        this.stopTime = stopTime;
    }

    /**
     * Returns the probe type.
     *
     * @return the probe type
     */
    public ProbeType getType() {
        return type;
    }

    /**
     * Sets the probe type.
     *
     * @param type the probe type to set
     */
    public void setType(ProbeType type) {
        this.type = type;
    }

    /**
     * Returns the start time in milliseconds.
     *
     * @return the start time
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Sets the start time in milliseconds.
     *
     * @param startTime the start time to set
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * Returns the stop time in milliseconds.
     *
     * @return the stop time
     */
    public long getStopTime() {
        return stopTime;
    }

    /**
     * Sets the stop time in milliseconds.
     *
     * @param stopTime the stop time to set
     */
    public void setStopTime(long stopTime) {
        this.stopTime = stopTime;
    }
}
