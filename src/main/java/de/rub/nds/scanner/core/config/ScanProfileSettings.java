/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.config;

/**
 * Optional {@link ExecutorConfig} overrides that a {@link ScanProfile} may declare. Any field left
 * {@code null} (i.e. not present in the profile's JSON) keeps {@link ExecutorConfig}'s built-in
 * default instead of being overridden.
 *
 * <p>Unlike {@link ScanProfile#getProbes()}, these settings are never inherited from profiles
 * listed in {@link ScanProfile#getInheritedFromProfiles()} — only the settings declared directly on
 * the profile that was activated via {@code -profile} apply.
 */
public final class ScanProfileSettings {

    private Boolean noColor;

    private ScannerDetail scanDetail;

    private ScannerDetail postAnalysisDetail;

    private ScannerDetail reportDetail;

    private String outputFile;

    private Integer probeTimeout;

    private Integer parallelProbes;

    private Integer overallThreads;

    public ScanProfileSettings() {
        // Default constructor for Jackson
    }

    /**
     * Returns the {@code noColor} override, or null if not set by the profile.
     *
     * @return the override, or null
     */
    public Boolean getNoColor() {
        return noColor;
    }

    /**
     * Sets the {@code noColor} override.
     *
     * @param noColor the override, or null to leave the default in place
     */
    public void setNoColor(Boolean noColor) {
        this.noColor = noColor;
    }

    /**
     * Returns the {@code scanDetail} override, or null if not set by the profile.
     *
     * @return the override, or null
     */
    public ScannerDetail getScanDetail() {
        return scanDetail;
    }

    /**
     * Sets the {@code scanDetail} override.
     *
     * @param scanDetail the override, or null to leave the default in place
     */
    public void setScanDetail(ScannerDetail scanDetail) {
        this.scanDetail = scanDetail;
    }

    /**
     * Returns the {@code postAnalysisDetail} override, or null if not set by the profile.
     *
     * @return the override, or null
     */
    public ScannerDetail getPostAnalysisDetail() {
        return postAnalysisDetail;
    }

    /**
     * Sets the {@code postAnalysisDetail} override.
     *
     * @param postAnalysisDetail the override, or null to leave the default in place
     */
    public void setPostAnalysisDetail(ScannerDetail postAnalysisDetail) {
        this.postAnalysisDetail = postAnalysisDetail;
    }

    /**
     * Returns the {@code reportDetail} override, or null if not set by the profile.
     *
     * @return the override, or null
     */
    public ScannerDetail getReportDetail() {
        return reportDetail;
    }

    /**
     * Sets the {@code reportDetail} override.
     *
     * @param reportDetail the override, or null to leave the default in place
     */
    public void setReportDetail(ScannerDetail reportDetail) {
        this.reportDetail = reportDetail;
    }

    /**
     * Returns the {@code outputFile} override, or null if not set by the profile.
     *
     * @return the override, or null
     */
    public String getOutputFile() {
        return outputFile;
    }

    /**
     * Sets the {@code outputFile} override.
     *
     * @param outputFile the override, or null to leave the default in place
     */
    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    /**
     * Returns the {@code probeTimeout} override, or null if not set by the profile.
     *
     * @return the override, or null
     */
    public Integer getProbeTimeout() {
        return probeTimeout;
    }

    /**
     * Sets the {@code probeTimeout} override.
     *
     * @param probeTimeout the override, or null to leave the default in place
     */
    public void setProbeTimeout(Integer probeTimeout) {
        this.probeTimeout = probeTimeout;
    }

    /**
     * Returns the {@code parallelProbes} override, or null if not set by the profile.
     *
     * @return the override, or null
     */
    public Integer getParallelProbes() {
        return parallelProbes;
    }

    /**
     * Sets the {@code parallelProbes} override.
     *
     * @param parallelProbes the override, or null to leave the default in place
     */
    public void setParallelProbes(Integer parallelProbes) {
        this.parallelProbes = parallelProbes;
    }

    /**
     * Returns the {@code overallThreads} override, or null if not set by the profile.
     *
     * @return the override, or null
     */
    public Integer getOverallThreads() {
        return overallThreads;
    }

    /**
     * Sets the {@code overallThreads} override.
     *
     * @param overallThreads the override, or null to leave the default in place
     */
    public void setOverallThreads(Integer overallThreads) {
        this.overallThreads = overallThreads;
    }
}
