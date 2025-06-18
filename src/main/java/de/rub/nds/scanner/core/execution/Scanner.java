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
import de.rub.nds.scanner.core.config.ExecutorConfig;
import de.rub.nds.scanner.core.guideline.Guideline;
import de.rub.nds.scanner.core.guideline.GuidelineChecker;
import de.rub.nds.scanner.core.passive.StatsWriter;
import de.rub.nds.scanner.core.probe.ScannerProbe;
import de.rub.nds.scanner.core.report.ScanReport;
import de.rub.nds.scanner.core.report.rating.ScoreReport;
import de.rub.nds.scanner.core.report.rating.SiteReportRater;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Abstract base class for implementing scanners.
 * This class provides the core scanning framework, including probe execution,
 * report generation, scoring, and guideline evaluation.
 *
 * @param <ReportT> the type of scan report
 * @param <ProbeT> the type of scanner probe
 * @param <AfterProbeT> the type of after-probe
 * @param <StateT> the type of state object used by probes
 */
public abstract class Scanner<
                ReportT extends ScanReport,
                ProbeT extends ScannerProbe<ReportT, StateT>,
                AfterProbeT extends AfterProbe<ReportT>,
                StateT>
        implements AutoCloseable {

    private static final Logger LOGGER = LogManager.getLogger();

    private final ExecutorConfig executorConfig;
    private final List<ProbeT> probeList;
    private final List<AfterProbeT> afterList;
    private final boolean fillProbeListsAtScanStart;

    /**
     * Creates a new scanner instance.
     *
     * @param executorConfig The executor configuration to use.
     */
    public Scanner(ExecutorConfig executorConfig) {
        this.executorConfig = executorConfig;
        probeList = new LinkedList<>();
        afterList = new LinkedList<>();
        fillProbeListsAtScanStart = true;
    }

    /**
     * Creates a new scanner instance.
     *
     * @param executorConfig The executor configuration to use.
     * @param probeList The list of probes to execute.
     * @param afterList The list of after probes to execute.
     */
    public Scanner(
            ExecutorConfig executorConfig, List<ProbeT> probeList, List<AfterProbeT> afterList) {
        this.executorConfig = executorConfig;
        this.probeList = new LinkedList<>(probeList);
        this.afterList = new LinkedList<>(afterList);
        fillProbeListsAtScanStart = false;
    }

    /**
     * Fills the probe list and the after list with the probes and after probes that should be
     * executed.
     */
    protected abstract void fillProbeLists();

    private void setDefaultProbeWriter() {
        for (ProbeT probe : probeList) {
            probe.setWriter(getDefaultProbeWriter());
        }
    }

    /**
     * Gets the default probe writer that will be used to capture passive probe results across all
     * probes.
     *
     * @return An instance of StatsWriter to use.
     */
    protected abstract StatsWriter<StateT> getDefaultProbeWriter();

    /**
     * Creates a new report instance.
     *
     * @return A new report instance.
     */
    protected abstract ReportT getEmptyReport();

    /**
     * Checks whether the scan prerequisites are fulfilled. If they are not fulfilled, the scan will
     * not be executed.
     *
     * @param report The report that will be used to store the scan results.
     * @return True if the scan prerequisites are fulfilled, false otherwise.
     */
    protected abstract boolean checkScanPrerequisites(ReportT report);

    /**
     * Get the SiteReportRater that will be used to rate the site report and compute the score
     * report.
     *
     * @return An instance of SiteReportRater.
     */
    protected SiteReportRater getSiteReportRater() {
        return null;
    }

    /**
     * Get a set of guidelines that will be evaluated after the scan has been executed.
     *
     * @return A list of guidelines.
     */
    protected List<Guideline<ReportT>> getGuidelines() {
        return List.of();
    }

    /**
     * Performs the scan. It will take care of all the necessary steps to perform a scan, including
     * filling the probe list by calling {@link #fillProbeLists}, checking the scan prerequisites by
     * calling {@link #checkScanPrerequisites}, and executing the scan. After the scan has been
     * executed, the site report will be rated by using the SiteReportRate returned by {@link
     * #getSiteReportRater}. Finally, the guidelines returned by {@link #getGuidelines} will be
     * evaluated. The result is serialized to a file if configured.
     *
     * @return The scan report.
     */
    public ReportT scan() {
        // Scan Preparation
        LOGGER.debug("Calling onScanStart() event hook");
        onScanStart();
        ReportT report = getEmptyReport();
        if (fillProbeListsAtScanStart) {
            fillProbeLists();
        }
        setDefaultProbeWriter();

        // Check Scan Prerequisites
        if (!checkScanPrerequisites(report)) {
            LOGGER.debug("Scan cannot be performed due to prerequisites not being fulfilled");
            return report;
        }

        // Scan Execution
        LOGGER.debug("Starting scan execution");
        ScanJob<ReportT, ProbeT, AfterProbeT, StateT> scanJob = new ScanJob<>(probeList, afterList);
        try (ThreadedScanJobExecutor<ReportT, ProbeT, AfterProbeT, StateT> scanJobExecutor =
                new ThreadedScanJobExecutor<>(
                        executorConfig,
                        scanJob,
                        executorConfig.getParallelProbes(),
                        "ScannerProbeExecutor " + report.getRemoteName())) {
            report.setScanStartTime(System.currentTimeMillis());
            scanJobExecutor.execute(report);
        } catch (InterruptedException e) {
            LOGGER.warn("Scan execution interrupted");
            report.setScanEndTime(System.currentTimeMillis());
            Thread.currentThread().interrupt();
            return report;
        }
        LOGGER.debug("Scan execution complete");

        // Rating
        LOGGER.debug("Retrieving site report rater for score evaluation");
        SiteReportRater rater = getSiteReportRater();
        if (rater != null) {
            LOGGER.debug("Site report rater set, computing score");
            ScoreReport scoreReport = rater.getScoreReport(report.getResultMap());
            report.setScore(scoreReport.getScore());
            report.setScoreReport(scoreReport);
        }

        // Guideline Evaluation
        LOGGER.debug("Retrieving guidelines for evaluation");
        List<Guideline<ReportT>> guidelines = getGuidelines();
        LOGGER.debug("Got a total of {} guidelines to evaluate", guidelines.size());
        for (Guideline<ReportT> guideline : guidelines) {
            LOGGER.debug("Executing evaluation of guideline '{}'", guideline.getName());
            GuidelineChecker<ReportT> checker = new GuidelineChecker<>(guideline);
            checker.fillReport(report);
        }

        // Scan Completion
        report.setScanEndTime(System.currentTimeMillis());
        LOGGER.debug(
                "Scan finished, took a total of {} milliseconds",
                report.getScanEndTime() - report.getScanStartTime());

        // Serialize report to file
        if (executorConfig.isWriteReportToFile()) {
            LOGGER.debug("Writing report to file");
            try (FileOutputStream fos = new FileOutputStream(executorConfig.getOutputFile())) {
                report.serializeToJson(fos);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Could not serialize report to file", e);
            } catch (IOException e) {
                throw new RuntimeException("Could not write report to file", e);
            }
        }

        LOGGER.debug("Calling onScanEnd() event hook");
        onScanEnd();
        return report;
    }

    /** This method is called before the scan is started. */
    protected void onScanStart() {}

    /** This method is called after the scan is finished. */
    protected void onScanEnd() {}

    /**
     * Register a probe for execution.
     *
     * @param probe The probe to register.
     */
    protected void registerProbeForExecution(ProbeT probe) {
        registerProbeForExecution(probe, true);
    }

    /**
     * Register a probe for execution.
     *
     * @param probe The probe to register.
     * @param executeByDefault Whether the probe should be executed by default.
     */
    protected void registerProbeForExecution(ProbeT probe, boolean executeByDefault) {
        if ((executorConfig.getProbes() == null && executeByDefault)
                || (executorConfig.getProbes() != null
                        && executorConfig.getProbes().contains(probe.getType()))) {
            if (executorConfig.getExcludedProbes().contains(probe.getType())) {
                LOGGER.debug("Probe {} is excluded from the scan", probe.getType());
            } else {
                probeList.add(probe);
            }
        }
    }

    /**
     * Register an after probe for execution.
     *
     * @param afterProbe The after probe to register.
     */
    protected void registerProbeForExecution(AfterProbeT afterProbe) {
        afterList.add(afterProbe);
    }
}
