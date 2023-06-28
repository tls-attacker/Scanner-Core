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
import de.rub.nds.scanner.core.passive.ExtractedValueContainer;
import de.rub.nds.scanner.core.passive.TrackableValue;
import de.rub.nds.scanner.core.probe.ScannerProbe;
import de.rub.nds.scanner.core.report.ScanReport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThreadedScanJobExecutor<
                ReportT extends ScanReport,
                ProbeT extends ScannerProbe<ReportT, StateT>,
                AfterProbeT extends AfterProbe<ReportT>,
                StateT>
        extends ScanJobExecutor<ReportT> implements Observer {

    private static final Logger LOGGER = LogManager.getLogger();

    private final ExecutorConfig config;

    private final ScanJob<ReportT, ProbeT, AfterProbeT, StateT> scanJob;

    private List<ProbeT> notScheduledTasks;

    private final List<Future<ScannerProbe<ReportT, StateT>>> futureResults;

    private final ThreadPoolExecutor executor;

    // Used for waiting for Threads in the ThreadPoolExecutor
    private final Semaphore semaphore = new Semaphore(0);

    public ThreadedScanJobExecutor(
            ExecutorConfig config,
            ScanJob<ReportT, ProbeT, AfterProbeT, StateT> scanJob,
            int threadCount,
            String prefix) {
        long probeTimeout = config.getProbeTimeout();
        executor =
                new ScannerThreadPoolExecutor(
                        threadCount, new NamedThreadFactory(prefix), semaphore, probeTimeout);
        this.config = config;
        this.scanJob = scanJob;
        this.futureResults = new LinkedList<>();
    }

    public ThreadedScanJobExecutor(
            ExecutorConfig config,
            ScanJob<ReportT, ProbeT, AfterProbeT, StateT> scanJob,
            ThreadPoolExecutor executor) {
        this.executor = executor;
        this.config = config;
        this.scanJob = scanJob;
        this.futureResults = new LinkedList<>();
    }

    @Override
    public void execute(ReportT report) {
        notScheduledTasks = new ArrayList<>(scanJob.getProbeList());
        report.addObserver(this);

        checkForExecutableProbes(report);
        executeProbesTillNoneCanBeExecuted(report);
        updateReportWithNotExecutedProbes(report);
        reportAboutNotExecutedProbes();
        collectStatistics(report);
        executeAfterProbes(report);

        LOGGER.info("Finished scan");
        report.deleteObserver(this);
    }

    private void updateReportWithNotExecutedProbes(ReportT report) {
        for (ProbeT probe : notScheduledTasks) {
            probe.merge(report);
            report.markProbeAsUnexecuted(probe);
        }
    }

    private void checkForExecutableProbes(ReportT report) {
        update(report, null);
    }

    private void executeProbesTillNoneCanBeExecuted(ReportT report) {
        boolean probesQueued = true;
        while (probesQueued) {
            // handle all Finished Results
            List<Future<ScannerProbe<ReportT, StateT>>> finishedFutures = new LinkedList<>();
            for (Future<ScannerProbe<ReportT, StateT>> result : futureResults) {
                if (result.isDone()) {
                    try {
                        ScannerProbe<ReportT, StateT> probeResult = result.get();
                        LOGGER.info(probeResult.getType().getName() + " probe executed");
                        finishedFutures.add(result);
                        report.markProbeAsExecuted(result.get());
                        probeResult.merge(report);
                    } catch (InterruptedException | ExecutionException ex) {
                        LOGGER.error(
                                "Encountered an exception before we could merge the result. Killing the task.",
                                ex);
                        result.cancel(true);
                        finishedFutures.add(result);
                    } catch (CancellationException ex) {
                        LOGGER.info(
                                "Could not retrieve a task because it was cancelled after {} milliseconds",
                                config.getProbeTimeout());
                        finishedFutures.add(result);
                    }
                }
            }
            futureResults.removeAll(finishedFutures);
            // execute possible new probes
            update(report, this);
            if (futureResults.isEmpty()) {
                // nothing can be executed anymore
                probesQueued = false;
            } else {
                try {
                    // wait for at least one probe to finish executing before checking again
                    semaphore.acquire();
                } catch (Exception e) {
                    LOGGER.info("Interrupted while waiting for probe execution");
                }
            }
        }
    }

    private void reportAboutNotExecutedProbes() {
        LOGGER.debug("Did not execute the following probes:");
        for (ProbeT probe : notScheduledTasks) {
            LOGGER.debug(probe.getProbeName());
        }
    }

    private void collectStatistics(ReportT report) {
        LOGGER.debug("Evaluating executed handshakes...");
        List<ProbeT> allProbes = scanJob.getProbeList();
        HashMap<TrackableValue, ExtractedValueContainer<?>> containerMap = new HashMap<>();
        int stateCounter = 0;
        for (ProbeT probe : allProbes) {
            List<ExtractedValueContainer<?>> tempContainerList =
                    probe.getWriter().getCumulatedExtractedValues();
            for (ExtractedValueContainer<?> tempContainer : tempContainerList) {
                if (containerMap.containsKey(tempContainer.getType())) {
                    // This cast should not fail because we only combine containers of the same type
                    //noinspection unchecked
                    ((List<Object>)
                                    containerMap
                                            .get(tempContainer.getType())
                                            .getExtractedValueList())
                            .addAll(tempContainer.getExtractedValueList());
                } else {
                    containerMap.put(tempContainer.getType(), tempContainer);
                }
            }
            stateCounter += probe.getWriter().getStateCounter();
        }
        report.setPerformedConnections(stateCounter);
        report.putAllExtractedValueContainers(containerMap);
        LOGGER.debug("Finished evaluation");
    }

    private void executeAfterProbes(ReportT report) {
        LOGGER.debug("Analyzing data...");
        for (AfterProbe<ReportT> afterProbe : scanJob.getAfterList()) {
            afterProbe.analyze(report);
        }
        LOGGER.debug("Finished analysis");
    }

    @Override
    public void shutdown() {
        executor.shutdown();
    }

    @Override
    public synchronized void update(Observable o, Object o1) {
        if (o instanceof ScanReport) {
            ReportT report = (ReportT) o;
            List<ProbeT> newNotSchedulesTasksList = new LinkedList<>();
            for (ProbeT probe : notScheduledTasks) {
                if (probe.canBeExecuted(report)) {
                    probe.adjustConfig(report);
                    LOGGER.debug("Scheduling: " + probe.getProbeName());
                    Future<ScannerProbe<ReportT, StateT>> future = executor.submit(probe);
                    futureResults.add(future);
                } else {
                    newNotSchedulesTasksList.add(probe);
                }
            }
            this.notScheduledTasks = newNotSchedulesTasksList;
        } else {
            LOGGER.error(this.getClass().getName() + " received an update from a non-siteReport");
        }
    }
}
