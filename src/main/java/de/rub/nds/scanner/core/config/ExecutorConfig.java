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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.rub.nds.scanner.core.probe.ProbeType;
import de.rub.nds.scanner.core.probe.ProbeTypeConverter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public final class ExecutorConfig {

    private static final ObjectMapper SETTINGS_MAPPER = new ObjectMapper();

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

    @Parameter(
            names = "-profile",
            description =
                    "Path to a scan profile JSON file. Only probes declared by this profile (and"
                            + " any profiles it inherits from via 'inheritedFromProfiles') will be"
                            + " executed. Entries in 'inheritedFromProfiles' are paths to other"
                            + " profile JSON files, resolved relative to this profile's own"
                            + " directory. Probes excluded via -exclude are removed regardless of"
                            + " where they came from.")
    private String profile = null;

    @Parameter(
            names = "-listProbes",
            description =
                    "Print every available probe, grouped by ProbeType class, in the same JSON"
                            + " syntax used by a scan profile's 'probes' field, then exit without"
                            + " scanning.")
    private boolean listProbes = false;

    private List<ProbeType> probes = null;

    private ProbeTypeSelector profileProbeSelector = null;
    private boolean profileProbeSelectorResolved = false;
    private boolean settingsResolved = false;

    public ExecutorConfig() {
        // Default constructor
    }

    /**
     * Returns a copy of the list of probe types that are excluded from scanning, regardless of
     * whether they were selected via {@link #setProbes(List)} or via a scan profile.
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
     * Returns the path to the scan profile JSON file, if one was configured.
     *
     * @return the scan profile path, or null if not set
     */
    public String getProfile() {
        return profile;
    }

    /**
     * Sets the path to the scan profile JSON file to use for this scan. Takes effect the next time
     * {@link #isProbeIncluded(ProbeType, boolean)} or one of the setting getters (e.g. {@link
     * #getScanDetail()}) is called.
     *
     * @param profile the scan profile path, or null to clear
     */
    public void setProfile(String profile) {
        this.profile = profile;
        this.profileProbeSelectorResolved = false;
        this.settingsResolved = false;
    }

    /**
     * Returns whether {@code -listProbes} was requested, i.e. whether every available probe should
     * be printed instead of running a scan.
     *
     * @return true if the available probes should be listed and no scan performed
     */
    public boolean isListProbes() {
        return listProbes;
    }

    /**
     * Sets whether every available probe should be printed instead of running a scan.
     *
     * @param listProbes true to list probes instead of scanning
     */
    public void setListProbes(boolean listProbes) {
        this.listProbes = listProbes;
    }

    /**
     * Applies the settings declared directly by the active scan profile (not including any
     * inherited profiles) on top of the current values, the first time this is called after {@link
     * #setProfile(String)}. Fields the profile does not declare are left untouched. This works by
     * deserializing the profile's {@code settings} JSON object directly onto this instance, so
     * adding a new overridable setting only requires adding the corresponding {@code @Parameter}
     * field and its getter/setter above — no separate mapping to maintain.
     */
    private void resolveSettingsFromProfileIfNecessary() {
        if (settingsResolved || profile == null) {
            return;
        }
        JsonNode settings;
        try {
            settings = ScanProfileIO.read(Path.of(profile)).getSettings();
        } catch (IOException e) {
            throw new UncheckedIOException("Could not load scan profile '" + profile + "'", e);
        }
        if (settings != null) {
            try {
                SETTINGS_MAPPER.readerForUpdating(this).readValue(settings);
            } catch (IOException e) {
                throw new UncheckedIOException(
                        "Could not apply settings from scan profile '" + profile + "'", e);
            }
        }
        settingsResolved = true;
    }

    /**
     * Returns the scanner detail level for the scan operation.
     *
     * @return the current scanner detail level
     */
    public ScannerDetail getScanDetail() {
        resolveSettingsFromProfileIfNecessary();
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
        resolveSettingsFromProfileIfNecessary();
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
        resolveSettingsFromProfileIfNecessary();
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
        resolveSettingsFromProfileIfNecessary();
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
     * Returns a copy of the list of probe types to be executed, as set via {@link
     * #setProbes(List)}/{@link #addProbes(List)}. This is independent of any scan profile
     * configured via {@link #setProfile(String)} — see {@link #isProbeIncluded(ProbeType, boolean)}
     * for the combined effect of both mechanisms.
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
    @JsonIgnore
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
     * Determines whether a candidate probe should be executed, combining every selection mechanism
     * this config supports:
     *
     * <ol>
     *   <li>If {@link #setProbes(List)}/{@link #addProbes(List)} configured an explicit inclusion
     *       list, {@code probeType} must be contained in it.
     *   <li>Otherwise, if a scan profile is configured via {@link #setProfile(String)}, {@code
     *       probeType} must be matched by it (resolved lazily, once, against the actual candidate
     *       probes passed here rather than by reflectively resolving class names up front).
     *   <li>Otherwise, {@code executeByDefault} decides.
     * </ol>
     *
     * In every case, a probe named via {@code -exclude} ({@link #getExcludedProbes()}) is always
     * removed, regardless of how it was otherwise selected.
     *
     * @param probeType the candidate probe's type
     * @param executeByDefault whether the probe should run when neither an explicit probe list nor
     *     a profile is configured
     * @return true if the probe should be executed
     */
    public boolean isProbeIncluded(ProbeType probeType, boolean executeByDefault) {
        if (excludedProbes.contains(probeType)) {
            return false;
        }
        if (probes != null) {
            return probes.contains(probeType);
        }
        resolveProfileProbeSelectorIfNecessary();
        if (profileProbeSelector != null) {
            return profileProbeSelector.matches(probeType);
        }
        return executeByDefault;
    }

    private void resolveProfileProbeSelectorIfNecessary() {
        if (profileProbeSelectorResolved || profile == null) {
            return;
        }
        try {
            profileProbeSelector = ScanProfileIO.resolveProbeSelector(Path.of(profile));
        } catch (IOException e) {
            throw new UncheckedIOException("Could not load scan profile '" + profile + "'", e);
        }
        profileProbeSelectorResolved = true;
    }

    /**
     * Returns the timeout value for each probe execution in milliseconds.
     *
     * @return the probe timeout in milliseconds
     */
    public int getProbeTimeout() {
        resolveSettingsFromProfileIfNecessary();
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
        resolveSettingsFromProfileIfNecessary();
        return outputFile != null;
    }

    /**
     * Returns the path to the output file for the report.
     *
     * @return the output file path, or null if not specified
     */
    public String getOutputFile() {
        resolveSettingsFromProfileIfNecessary();
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
        resolveSettingsFromProfileIfNecessary();
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
        resolveSettingsFromProfileIfNecessary();
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
        return getParallelProbes() > 1 || getOverallThreads() > 1;
    }
}
