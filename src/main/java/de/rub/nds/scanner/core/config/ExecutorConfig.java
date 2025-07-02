/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.config;

import com.beust.jcommander.Parameter;
import de.rub.nds.scanner.core.probe.ProbeType;
import de.rub.nds.scanner.core.probe.ProbeTypeConverter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public final class ExecutorConfig {

    @Parameter(names = "-noColor", description = "If you use Windows or don't want colored text.")
    private boolean noColor = false;

    @Parameter(names = "-scanDetail", description = "How detailed do you want to scan?")
    private ScannerDetail scanDetail = ScannerDetail.NORMAL;

    @Parameter(
            names = "-postAnalysisDetail",
            description = "How detailed do you want the post analysis to be")
    private ScannerDetail postAnalysisDetail = ScannerDetail.NORMAL;

    @Parameter(names = "-reportDetail", description = "How detailed do you want the report to be?")
    private ScannerDetail reportDetail = ScannerDetail.NORMAL;

    @Parameter(
            names = "-outputFile",
            description = "Specify a file to write the site report in JSON to")
    private String outputFile = null;

    @Parameter(
            names = "-probeTimeout",
            description = "The timeout for each probe in ms (default 1800000)")
    private int probeTimeout = 1800000;

    @Parameter(
            names = "-parallelProbes",
            description =
                    "Defines the number of threads responsible for different probes. If set to 1, only one specific probe can be run in time.")
    private int parallelProbes = 1;

    @Parameter(
            names = "-threads",
            description =
                    "The maximum number of threads used to execute probes located in the queue.")
    private int overallThreads = 1;

    @Parameter(
            names = "-exclude",
            description =
                    "A list of probes that should be excluded from the scan. The list is separated by commas.",
            converter = ProbeTypeConverter.class)
    private List<ProbeType> excludedProbes = new LinkedList<>();

    private List<ProbeType> probes = null;

    public ExecutorConfig() {
        // Default constructor
    }

    /**
     * Returns a copy of the list of probe types that are excluded from scanning.
     *
     * @return a new list containing the excluded probe types
     */
    public List<ProbeType> getExcludedProbes() {
        return new LinkedList<>(excludedProbes);
    }

    /**
     * Sets the list of probe types to be excluded from scanning.
     *
     * @param excludedProbes the list of probe types to exclude
     */
    public void setExcludedProbes(List<ProbeType> excludedProbes) {
        this.excludedProbes = new LinkedList<>(excludedProbes);
    }

    /**
     * Returns the scanner detail level for the scan operation.
     *
     * @return the current scanner detail level
     */
    public ScannerDetail getScanDetail() {
        return scanDetail;
    }

    /**
     * Sets the scanner detail level for the scan operation.
     *
     * @param scanDetail the scanner detail level to set
     */
    public void setScanDetail(ScannerDetail scanDetail) {
        this.scanDetail = scanDetail;
    }

    /**
     * Returns the scanner detail level for post-analysis operations.
     *
     * @return the current post-analysis detail level
     */
    public ScannerDetail getPostAnalysisDetail() {
        return postAnalysisDetail;
    }

    /**
     * Sets the scanner detail level for post-analysis operations.
     *
     * @param postAnalysisDetail the post-analysis detail level to set
     */
    public void setPostAnalysisDetail(ScannerDetail postAnalysisDetail) {
        this.postAnalysisDetail = postAnalysisDetail;
    }

    /**
     * Returns the scanner detail level for report generation.
     *
     * @return the current report detail level
     */
    public ScannerDetail getReportDetail() {
        return reportDetail;
    }

    /**
     * Sets the scanner detail level for report generation.
     *
     * @param reportDetail the report detail level to set
     */
    public void setReportDetail(ScannerDetail reportDetail) {
        this.reportDetail = reportDetail;
    }

    /**
     * Checks if colored text output is disabled.
     *
     * @return true if colored text is disabled, false otherwise
     */
    public boolean isNoColor() {
        return noColor;
    }

    /**
     * Sets whether colored text output should be disabled.
     *
     * @param noColor true to disable colored text, false to enable it
     */
    public void setNoColor(boolean noColor) {
        this.noColor = noColor;
    }

    /**
     * Returns a copy of the list of probe types to be executed.
     *
     * @return a new list containing the probe types, or null if not set
     */
    public List<ProbeType> getProbes() {
        return probes == null ? null : new LinkedList<>(probes);
    }

    /**
     * Sets the list of probe types to be executed.
     *
     * @param probes the list of probe types to execute, or null to clear
     */
    public void setProbes(List<ProbeType> probes) {
        this.probes = probes == null ? null : new LinkedList<>(probes);
    }

    /**
     * Sets the probe types to be executed using a varargs parameter.
     *
     * @param probes the probe types to execute
     */
    public void setProbes(ProbeType... probes) {
        this.probes = Arrays.asList(probes);
    }

    /**
     * Adds additional probe types to the existing list of probes to be executed.
     *
     * @param probes the list of probe types to add
     */
    public void addProbes(List<ProbeType> probes) {
        if (this.probes == null) {
            this.probes = new LinkedList<>();
        }
        this.probes.addAll(probes);
    }

    /**
     * Adds additional probe types to the existing list using a varargs parameter.
     *
     * @param probes the probe types to add
     */
    public void addProbes(ProbeType... probes) {
        if (this.probes == null) {
            this.probes = new LinkedList<>();
        }
        this.probes.addAll(Arrays.asList(probes));
    }

    /**
     * Returns the timeout value for each probe execution in milliseconds.
     *
     * @return the probe timeout in milliseconds
     */
    public int getProbeTimeout() {
        return probeTimeout;
    }

    /**
     * Sets the timeout value for each probe execution in milliseconds.
     *
     * @param probeTimeout the probe timeout in milliseconds
     */
    public void setProbeTimeout(int probeTimeout) {
        this.probeTimeout = probeTimeout;
    }

    /**
     * Checks if the report should be written to a file.
     *
     * @return true if an output file is specified, false otherwise
     */
    public boolean isWriteReportToFile() {
        return outputFile != null;
    }

    /**
     * Returns the path to the output file for the report.
     *
     * @return the output file path, or null if not specified
     */
    public String getOutputFile() {
        return outputFile;
    }

    /**
     * Sets the path to the output file for the report.
     *
     * @param outputFile the output file path
     */
    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    /**
     * Returns the number of threads used for executing different probes in parallel.
     *
     * @return the number of parallel probe threads
     */
    public int getParallelProbes() {
        return parallelProbes;
    }

    /**
     * Sets the number of threads used for executing different probes in parallel.
     *
     * @param parallelProbes the number of parallel probe threads
     */
    public void setParallelProbes(int parallelProbes) {
        this.parallelProbes = parallelProbes;
    }

    /**
     * Returns the maximum number of threads used to execute probes.
     *
     * @return the maximum number of overall threads
     */
    public int getOverallThreads() {
        return overallThreads;
    }

    /**
     * Sets the maximum number of threads used to execute probes.
     *
     * @param overallThreads the maximum number of overall threads
     */
    public void setOverallThreads(int overallThreads) {
        this.overallThreads = overallThreads;
    }

    /**
     * Checks if the scanner is configured to run in multithreaded mode.
     *
     * @return true if either parallel probes or overall threads is greater than 1
     */
    public boolean isMultithreaded() {
        return parallelProbes > 1 || overallThreads > 1;
    }
}
