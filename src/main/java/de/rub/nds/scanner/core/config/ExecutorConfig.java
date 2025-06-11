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

/**
 * Central configuration class for controlling scanner execution behavior and performance.
 *
 * <p>ExecutorConfig provides comprehensive configuration options for the Scanner Core framework,
 * controlling everything from probe execution behavior to output formatting and threading. This
 * class uses JCommander annotations to enable command-line configuration while also supporting
 * programmatic configuration for embedded usage.
 *
 * <p><b>Key Configuration Areas:</b>
 *
 * <ul>
 *   <li><b>Threading & Performance:</b> Control parallel probe execution and timeouts
 *   <li><b>Scan Granularity:</b> Configure detail levels for scanning, analysis, and reporting
 *   <li><b>Probe Selection:</b> Include/exclude specific probe types from execution
 *   <li><b>Output Control:</b> Configure report formatting, files, and color output
 *   <li><b>Execution Behavior:</b> Control error handling, retry logic, and scan flow
 * </ul>
 *
 * <p><b>Threading Configuration:</b> The framework supports multi-level parallelism:
 *
 * <ul>
 *   <li>{@code parallelProbes}: Number of different probe types that can execute simultaneously
 *   <li>{@code overallThreads}: Total thread pool size for all scanning operations
 *   <li>Probe-specific threading: Individual probes may use additional internal threading
 * </ul>
 *
 * <p><b>Detail Level Control:</b> Three independent detail levels can be configured via {@link
 * ScannerDetail}:
 *
 * <ul>
 *   <li>{@code scanDetail}: Controls depth of probe execution and data collection
 *   <li>{@code postAnalysisDetail}: Controls thoroughness of post-probe analysis
 *   <li>{@code reportDetail}: Controls verbosity and completeness of output reports
 * </ul>
 *
 * <p><b>Probe Filtering:</b> Probes can be selectively included or excluded:
 *
 * <ul>
 *   <li>{@code includedProbes}: If specified, only these probe types will execute
 *   <li>{@code excludedProbes}: These probe types will be skipped during execution
 *   <li>Both lists use {@link ProbeType} identifiers for type-safe configuration
 * </ul>
 *
 * <p><b>Example Usage:</b>
 *
 * <pre>{@code
 * // Programmatic configuration
 * ExecutorConfig config = new ExecutorConfig();
 * config.setParallelProbes(4);
 * config.setScanDetail(ScannerDetail.DETAILED);
 * config.setProbeTimeout(300000); // 5 minutes
 * config.setOutputFile("/path/to/report.json");
 *
 * // Command-line usage
 * // java -jar scanner.jar -parallelProbes 4 -scanDetail DETAILED -outputFile report.json
 * }</pre>
 *
 * <p><b>Thread Safety:</b> This configuration class is not thread-safe. It should be configured
 * before being passed to scanner instances, and not modified during execution.
 *
 * @see ScannerDetail
 * @see ProbeType
 * @see de.rub.nds.scanner.core.execution.ScanJobExecutor
 */
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

    public List<ProbeType> getExcludedProbes() {
        return excludedProbes;
    }

    public void setExcludedProbes(List<ProbeType> excludedProbes) {
        this.excludedProbes = excludedProbes;
    }

    public ScannerDetail getScanDetail() {
        return scanDetail;
    }

    public void setScanDetail(ScannerDetail scanDetail) {
        this.scanDetail = scanDetail;
    }

    public ScannerDetail getPostAnalysisDetail() {
        return postAnalysisDetail;
    }

    public void setPostAnalysisDetail(ScannerDetail postAnalysisDetail) {
        this.postAnalysisDetail = postAnalysisDetail;
    }

    public ScannerDetail getReportDetail() {
        return reportDetail;
    }

    public void setReportDetail(ScannerDetail reportDetail) {
        this.reportDetail = reportDetail;
    }

    public boolean isNoColor() {
        return noColor;
    }

    public void setNoColor(boolean noColor) {
        this.noColor = noColor;
    }

    public List<ProbeType> getProbes() {
        return probes;
    }

    public void setProbes(List<ProbeType> probes) {
        this.probes = probes;
    }

    public void setProbes(ProbeType... probes) {
        this.probes = Arrays.asList(probes);
    }

    public void addProbes(List<ProbeType> probes) {
        if (this.probes == null) {
            this.probes = new LinkedList<>();
        }
        this.probes.addAll(probes);
    }

    public void addProbes(ProbeType... probes) {
        if (this.probes == null) {
            this.probes = new LinkedList<>();
        }
        this.probes.addAll(Arrays.asList(probes));
    }

    public int getProbeTimeout() {
        return probeTimeout;
    }

    public void setProbeTimeout(int probeTimeout) {
        this.probeTimeout = probeTimeout;
    }

    public boolean isWriteReportToFile() {
        return outputFile != null;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public int getParallelProbes() {
        return parallelProbes;
    }

    public void setParallelProbes(int parallelProbes) {
        this.parallelProbes = parallelProbes;
    }

    public int getOverallThreads() {
        return overallThreads;
    }

    public void setOverallThreads(int overallThreads) {
        this.overallThreads = overallThreads;
    }

    public boolean isMultithreaded() {
        return (parallelProbes > 1 || overallThreads > 1);
    }
}
