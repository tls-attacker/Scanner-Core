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
import de.rub.nds.scanner.core.passive.ExtractedValueContainer;
import de.rub.nds.scanner.core.passive.TrackableValue;
import de.rub.nds.scanner.core.probe.ScannerProbe;
import de.rub.nds.scanner.core.report.ScanReport;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
        extends ScanJobExecutor<ReportT> implements PropertyChangeListener, AutoCloseable {

    private static final Logger LOGGER = LogManager.getLogger();

    @SuppressWarnings("unused")
    private final ExecutorConfig config;

    private final ScanJob<ReportT, ProbeT, AfterProbeT, StateT> scanJob;

    private List<ProbeT> notScheduledTasks;

    private final List<Future<ScannerProbe<ReportT, StateT>>> futureResults;

    private final ThreadPoolExecutor executor;

    // Used for waiting for Threads in the ThreadPoolExecutor
    private final Semaphore semaphore = new Semaphore(0);

    private int probeCount;
    private int finishedProbes = 0;

    // Connection failure tracking
    private int consecutiveConnectionFailures = 0;
    private static final int MAX_CONSECUTIVE_CONNECTION_FAILURES = 3;

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
    public void execute(ReportT report) throws InterruptedException {
        probeCount = scanJob.getProbeList().size();
        notScheduledTasks = new ArrayList<>(scanJob.getProbeList());
        report.addPropertyChangeListener(this);

        checkExecutableProbesAndSchedule(report);
        executeProbesTillNoneCanBeExecuted(report);
        updateReportWithNotExecutedProbes(report);
        reportAboutNotExecutedProbes();
        collectStatistics(report);
        executeAfterProbes(report);

        LOGGER.info("Finished scan");
        report.removePropertyChangeListener(this);
    }

    private void updateReportWithNotExecutedProbes(ReportT report) {
        for (ProbeT probe : notScheduledTasks) {
            probe.merge(report);
            report.markProbeAsUnexecuted(probe);
        }
    }

    private void executeProbesTillNoneCanBeExecuted(ReportT report) throws InterruptedException {
        boolean probesQueued = true;
        while (probesQueued) {
            // handle all Finished Results
            List<Future<ScannerProbe<ReportT, StateT>>> finishedFutures = new ArrayList<>();
            for (Future<ScannerProbe<ReportT, StateT>> result : new ArrayList<>(futureResults)) {
                if (result.isDone()) {
                    finishedProbes++;
                    ScannerProbe<ReportT, StateT> probeResult = null;
                    try {
                        probeResult = result.get();
                        LOGGER.info(
                                "[{}/{}] {} probe executed",
                                finishedProbes,
                                probeCount,
                                probeResult.getType().getName());
                        // Reset consecutive connection failures on successful execution
                        consecutiveConnectionFailures = 0;
                    } catch (ExecutionException e) {
                        // Check if this is a connection failure
                        Throwable cause = e.getCause();
                        boolean isConnectionFailure = false;

                        // Check the exception chain for connection-related issues
                        while (cause != null) {
                            String className = cause.getClass().getName();
                            if (className.contains("TransportHandlerConnectException")
                                    || className.contains("IOException")
                                    || className.contains("SocketException")
                                    || className.contains("ConnectException")) {
                                isConnectionFailure = true;
                                break;
                            }
                            cause = cause.getCause();
                        }

                        if (isConnectionFailure) {
                            consecutiveConnectionFailures++;
                            LOGGER.warn(
                                    "Connection failure during probe execution ({}/{}): {}",
                                    consecutiveConnectionFailures,
                                    MAX_CONSECUTIVE_CONNECTION_FAILURES,
                                    e.getCause().getMessage());

                            if (consecutiveConnectionFailures
                                    >= MAX_CONSECUTIVE_CONNECTION_FAILURES) {
                                LOGGER.error(
                                        "Maximum consecutive connection failures reached. "
                                                + "Target appears to be unreachable. Aborting scan.");
                                // Mark all remaining probes as not executable
                                for (ProbeT probe : notScheduledTasks) {
                                    probe.merge(report);
                                    report.markProbeAsUnexecuted(probe);
                                }
                                notScheduledTasks.clear();
                                return;
                            }
                        } else {
                            LOGGER.error("Probe execution failed with non-connection error", e);
                        }

                        // Mark the failed probe as finished so it's removed from futureResults
                        finishedFutures.add(result);
                        // Continue execution without throwing RuntimeException
                        continue;
                    }
                    finishedFutures.add(result);
                    if (probeResult != null) {
                        probeResult.merge(report);
                        report.markProbeAsExecuted(probeResult);
                    }
                }
            }
            futureResults.removeAll(finishedFutures);
            // execute possible new probes
            checkExecutableProbesAndSchedule(report);
            if (futureResults.isEmpty()) {
                // nothing can be executed anymore
                probesQueued = false;
            } else {
                // wait for at least one probe to finish executing before checking again
                semaphore.acquire();
            }
        }
    }

    private void reportAboutNotExecutedProbes() {
        LOGGER.info("{} scheduled probes were not executed", notScheduledTasks.size());
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
    public void close() {
        shutdown();
    }

    @Override
    public void shutdown() {
        executor.shutdown();
    }

    @Override
    public synchronized void propertyChange(PropertyChangeEvent event) {
        if (!event.getPropertyName().equals("supportedProbe")
                || !event.getPropertyName().equals("unsupportedProbe")) {
            return;
        }
        if (event.getSource() instanceof ScanReport) {
            checkExecutableProbesAndSchedule((ReportT) event.getSource());
        } else {
            LOGGER.error("{} received an update from a non-siteReport", this.getClass().getName());
        }
    }

    public synchronized void checkExecutableProbesAndSchedule(ReportT report) {
        List<ProbeT> newNotSchedulesTasksList = new LinkedList<>();
        for (ProbeT probe : notScheduledTasks) {
            if (probe.canBeExecuted(report)) {
                probe.adjustConfig(report);
                LOGGER.debug("Scheduling: {}", probe.getProbeName());
                Future<ScannerProbe<ReportT, StateT>> future = executor.submit(probe);
                futureResults.add(future);
            } else {
                newNotSchedulesTasksList.add(probe);
            }
        }
        this.notScheduledTasks = newNotSchedulesTasksList;
    }
}
