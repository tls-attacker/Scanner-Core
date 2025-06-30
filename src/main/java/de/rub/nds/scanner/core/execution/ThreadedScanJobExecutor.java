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

/**
 * A threaded implementation of ScanJobExecutor that executes scan probes concurrently. This
 * executor manages probe dependencies, schedules executable probes, and collects results.
 *
 * @param <ReportT> the type of scan report
 * @param <ProbeT> the type of scanner probe
 * @param <AfterProbeT> the type of after-probe
 * @param <StateT> the type of state object used by probes
 */
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

    /**
     * Creates a new ThreadedScanJobExecutor with a custom thread pool.
     *
     * @param config the executor configuration
     * @param scanJob the scan job containing probes to execute
     * @param threadCount the number of threads in the pool
     * @param prefix the prefix for thread names
     */
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

    /**
     * Creates a new ThreadedScanJobExecutor with an existing thread pool executor.
     *
     * @param config the executor configuration
     * @param scanJob the scan job containing probes to execute
     * @param executor the thread pool executor to use
     */
    public ThreadedScanJobExecutor(
            ExecutorConfig config,
            ScanJob<ReportT, ProbeT, AfterProbeT, StateT> scanJob,
            ThreadPoolExecutor executor) {
        this.executor = executor;
        this.config = config;
        this.scanJob = scanJob;
        this.futureResults = new LinkedList<>();
    }

    /**
     * Executes the scan job by running probes concurrently and populating the report with results.
     * This method manages probe dependencies and ensures probes are executed in the correct order.
     *
     * @param report the report to populate with scan results
     * @throws InterruptedException if the execution is interrupted
     */
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
                    } catch (ExecutionException e) {
                        LOGGER.error("Some probe execution failed", e);
                        throw new RuntimeException(e);
                    }
                    finishedFutures.add(result);
                    probeResult.merge(report);
                    report.markProbeAsExecuted(probeResult);
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
        LOGGER.debug("Did not execute the following probes:"); // $NON-NLS-1$
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
        LOGGER.debug("Finished analysis"); // $NON-NLS-1$
    }

    /**
     * Closes this executor by shutting down the underlying thread pool. This method is called
     * automatically when used in a try-with-resources statement.
     */
    @Override
    public void close() {
        shutdown();
    }

    /**
     * Shuts down the underlying thread pool executor. No new tasks will be accepted after this
     * method is called.
     */
    @Override
    public void shutdown() {
        executor.shutdown();
    }

    /**
     * Handles property change events from the scan report. When probe support status changes, this
     * method re-evaluates which probes can be executed.
     *
     * @param event the property change event
     */
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

    /**
     * Checks which probes can be executed based on their requirements and schedules them for
     * execution. This method is called whenever the report state changes to re-evaluate probe
     * eligibility.
     *
     * @param report the current scan report
     */
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
