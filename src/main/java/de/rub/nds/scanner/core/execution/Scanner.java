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
import de.rub.nds.scanner.core.config.ExecutorConfig;
import de.rub.nds.scanner.core.guideline.Guideline;
import de.rub.nds.scanner.core.guideline.GuidelineChecker;
import de.rub.nds.scanner.core.passive.StatsWriter;
import de.rub.nds.scanner.core.probe.ScannerProbe;
import de.rub.nds.scanner.core.report.ScanReport;
import de.rub.nds.scanner.core.report.rating.ScoreReport;
import de.rub.nds.scanner.core.report.rating.SiteReportRater;
import java.util.LinkedList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    public Scanner(ExecutorConfig executorConfig) {
        this.executorConfig = executorConfig;
        probeList = new LinkedList<>();
        afterList = new LinkedList<>();
        fillProbeListsAtScanStart = true;
    }

    public Scanner(
            ExecutorConfig executorConfig, List<ProbeT> probeList, List<AfterProbeT> afterList) {
        this.executorConfig = executorConfig;
        this.probeList = new LinkedList<>(probeList);
        this.afterList = new LinkedList<>(afterList);
        fillProbeListsAtScanStart = false;
    }

    protected abstract void fillProbeLists();

    private void setDefaultProbeWriter() {
        for (ProbeT probe : probeList) {
            probe.setWriter(getDefaultProbeWriter());
        }
    }

    protected abstract StatsWriter<StateT> getDefaultProbeWriter();

    protected abstract ReportT getEmptyReport();

    protected abstract boolean checkScanPrerequisites(ReportT report);

    protected SiteReportRater getSiteReportRater() {
        return null;
    }

    protected List<Guideline<ReportT>> getGuidelines() {
        return List.of();
    }

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
        ThreadedScanJobExecutor<ReportT, ProbeT, AfterProbeT, StateT> executor =
                new ThreadedScanJobExecutor<>(
                        executorConfig, scanJob, executorConfig.getParallelProbes(), "");
        report.setScanStartTime(System.currentTimeMillis());
        executor.execute(report);
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
        executor.shutdown();
        LOGGER.debug("Calling onScanEnd() event hook");
        onScanEnd();
        return report;
    }

    protected void onScanStart() {}

    protected void onScanEnd() {}

    protected void registerProbeForExecution(ProbeT probe) {
        registerProbeForExecution(probe, true);
    }

    protected void registerProbeForExecution(ProbeT probe, boolean executeByDefault) {
        if ((executorConfig.getProbes() == null && executeByDefault)
                || (executorConfig.getProbes() != null
                        && executorConfig.getProbes().contains(probe.getType()))) {
            probeList.add(probe);
        }
    }

    protected void registerProbeForExecution(AfterProbeT afterProbe) {
        afterList.add(afterProbe);
    }
}
