/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.execution;

import static org.junit.jupiter.api.Assertions.*;

import de.rub.nds.scanner.core.afterprobe.AfterProbe;
import de.rub.nds.scanner.core.config.ExecutorConfig;
import de.rub.nds.scanner.core.passive.ExtractedValueContainer;
import de.rub.nds.scanner.core.passive.StatsWriter;
import de.rub.nds.scanner.core.passive.TrackableValue;
import de.rub.nds.scanner.core.probe.ProbeType;
import de.rub.nds.scanner.core.probe.ScannerProbe;
import de.rub.nds.scanner.core.probe.requirements.Requirement;
import de.rub.nds.scanner.core.report.ScanReport;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ThreadedScanJobExecutorTest {

    private ExecutorConfig executorConfig;

    // Test implementations
    static class TestProbeType implements ProbeType {
        private final String name;

        TestProbeType(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof TestProbeType) {
                return name.equals(((TestProbeType) obj).name);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }

    static class TestState {}

    static class TestReport extends ScanReport {
        private final List<ProbeType> executedProbeTypes = new ArrayList<>();
        private final List<ProbeType> unexecutedProbeTypes = new ArrayList<>();
        private final Map<TrackableValue, ExtractedValueContainer<?>> extractedContainers =
                new HashMap<>();

        @Override
        public String getRemoteName() {
            return "TestHost";
        }

        @Override
        public void serializeToJson(java.io.OutputStream outputStream) {
            // Simple implementation for testing
        }

        @Override
        public void markProbeAsExecuted(ScannerProbe<?, ?> probe) {
            executedProbeTypes.add(probe.getType());
            super.markProbeAsExecuted(probe);
        }

        @Override
        public void markProbeAsUnexecuted(ScannerProbe<?, ?> probe) {
            unexecutedProbeTypes.add(probe.getType());
            super.markProbeAsUnexecuted(probe);
        }

        public List<ProbeType> getExecutedProbeTypes() {
            return executedProbeTypes;
        }

        public List<ProbeType> getUnexecutedProbeTypes() {
            return unexecutedProbeTypes;
        }

        public Map<TrackableValue, ExtractedValueContainer<?>> getExtractedValueContainers() {
            return extractedContainers;
        }

        @Override
        public void putAllExtractedValueContainers(
                Map<TrackableValue, ExtractedValueContainer<?>> containers) {
            extractedContainers.putAll(containers);
        }
    }

    static class TestProbe extends ScannerProbe<TestReport, TestState> {
        private boolean canExecute = true;
        private boolean wasExecuted = false;
        private boolean shouldThrowException = false;
        private Requirement<TestReport> requirement = new de.rub.nds.scanner.core.probe.requirements.FulfilledRequirement<>();

        TestProbe(ProbeType type) {
            super(type);
            setWriter(new TestStatsWriter());
        }

        @Override
        public TestProbe call() {
            if (shouldThrowException) {
                throw new RuntimeException("Test exception");
            }
            super.call();
            wasExecuted = true;
            return this;
        }

        @Override
        public void executeTest() {
            wasExecuted = true;
        }

        @Override
        public Requirement<TestReport> getRequirements() {
            return requirement;
        }

        @Override
        public void adjustConfig(TestReport report) {}

        @Override
        protected void mergeData(TestReport report) {}

        public void setCanExecute(boolean canExecute) {
            this.canExecute = canExecute;
            if (canExecute) {
                this.requirement = new de.rub.nds.scanner.core.probe.requirements.FulfilledRequirement<>();
            } else {
                this.requirement = new de.rub.nds.scanner.core.probe.requirements.UnfulfillableRequirement<>();
            }
        }

        public boolean wasExecuted() {
            return wasExecuted;
        }

        public void setShouldThrowException(boolean shouldThrow) {
            this.shouldThrowException = shouldThrow;
        }

        public void addRequirement(Requirement<TestReport> requirement) {
            this.requirement = requirement;
        }
    }

    static class TestAfterProbe extends AfterProbe<TestReport> {
        private boolean analyzed = false;

        @Override
        public void analyze(TestReport report) {
            analyzed = true;
        }

        public boolean isAnalyzed() {
            return analyzed;
        }
    }

    static class TestStatsWriter extends StatsWriter<TestState> {
        private int stateCount = 0;

        @Override
        public void extract(TestState state) {}

        @Override
        public int getStateCounter() {
            return stateCount;
        }

        public void setStateCount(int count) {
            this.stateCount = count;
        }

        @Override
        public List<ExtractedValueContainer<?>> getCumulatedExtractedValues() {
            List<ExtractedValueContainer<?>> containers = new ArrayList<>();
            containers.add(new TestExtractedValueContainer());
            return containers;
        }
    }

    static class TestExtractedValueContainer extends ExtractedValueContainer<String> {
        TestExtractedValueContainer() {
            super(TestTrackableValue.TEST_VALUE);
            put("test1");
            put("test2");
        }
    }

    enum TestTrackableValue implements TrackableValue {
        TEST_VALUE
    }

    @BeforeEach
    public void setUp() {
        executorConfig = new ExecutorConfig();
        executorConfig.setParallelProbes(2);
        executorConfig.setProbeTimeout(5000);
    }

    @Test
    public void testBasicExecution() throws InterruptedException {
        List<TestProbe> probeList =
                Arrays.asList(
                        new TestProbe(new TestProbeType("probe1")),
                        new TestProbe(new TestProbeType("probe2")));
        List<TestAfterProbe> afterList = Arrays.asList(new TestAfterProbe());

        ScanJob<TestReport, TestProbe, TestAfterProbe, TestState> scanJob =
                new ScanJob<>(probeList, afterList);

        try (ThreadedScanJobExecutor<TestReport, TestProbe, TestAfterProbe, TestState> executor =
                new ThreadedScanJobExecutor<>(executorConfig, scanJob, 2, "Test")) {

            TestReport report = new TestReport();
            executor.execute(report);

            assertEquals(2, report.getExecutedProbeTypes().size());
            assertTrue(probeList.get(0).wasExecuted());
            assertTrue(probeList.get(1).wasExecuted());
            assertTrue(afterList.get(0).isAnalyzed());
        }
    }

    @Test
    public void testProbesThatCannotBeExecuted() throws InterruptedException {
        TestProbe executableProbe = new TestProbe(new TestProbeType("executable"));
        TestProbe nonExecutableProbe = new TestProbe(new TestProbeType("nonExecutable"));
        nonExecutableProbe.setCanExecute(false);

        List<TestProbe> probeList = Arrays.asList(executableProbe, nonExecutableProbe);
        List<TestAfterProbe> afterList = new ArrayList<>();

        ScanJob<TestReport, TestProbe, TestAfterProbe, TestState> scanJob =
                new ScanJob<>(probeList, afterList);

        try (ThreadedScanJobExecutor<TestReport, TestProbe, TestAfterProbe, TestState> executor =
                new ThreadedScanJobExecutor<>(executorConfig, scanJob, 1, "Test")) {

            TestReport report = new TestReport();
            executor.execute(report);

            assertEquals(1, report.getExecutedProbeTypes().size());
            assertEquals(1, report.getUnexecutedProbeTypes().size());
            assertTrue(executableProbe.wasExecuted());
            assertFalse(nonExecutableProbe.wasExecuted());
        }
    }

    @Test
    public void testProbeExecutionException() throws InterruptedException {
        TestProbe normalProbe = new TestProbe(new TestProbeType("normal"));
        TestProbe failingProbe = new TestProbe(new TestProbeType("failing"));
        failingProbe.setShouldThrowException(true);

        List<TestProbe> probeList = Arrays.asList(normalProbe, failingProbe);
        List<TestAfterProbe> afterList = new ArrayList<>();

        ScanJob<TestReport, TestProbe, TestAfterProbe, TestState> scanJob =
                new ScanJob<>(probeList, afterList);

        try (ThreadedScanJobExecutor<TestReport, TestProbe, TestAfterProbe, TestState> executor =
                new ThreadedScanJobExecutor<>(executorConfig, scanJob, 2, "Test")) {

            TestReport report = new TestReport();

            assertThrows(RuntimeException.class, () -> executor.execute(report));
        }
    }

    @Test
    public void testPropertyChangeListener() throws InterruptedException {
        TestProbe probe1 = new TestProbe(new TestProbeType("probe1"));
        TestProbe probe2 = new TestProbe(new TestProbeType("probe2"));
        probe2.setCanExecute(false); // Initially cannot execute

        // Add requirement that probe1 must be executed first
        probe2.addRequirement(
                new de.rub.nds.scanner.core.probe.requirements.Requirement<TestReport>() {
                    @Override
                    public boolean evaluate(TestReport report) {
                        return report.getExecutedProbeTypes().contains(new TestProbeType("probe1"));
                    }
                });

        List<TestProbe> probeList = Arrays.asList(probe1, probe2);
        List<TestAfterProbe> afterList = new ArrayList<>();

        ScanJob<TestReport, TestProbe, TestAfterProbe, TestState> scanJob =
                new ScanJob<>(probeList, afterList);

        try (ThreadedScanJobExecutor<TestReport, TestProbe, TestAfterProbe, TestState> executor =
                new ThreadedScanJobExecutor<>(executorConfig, scanJob, 1, "Test")) {

            TestReport report = new TestReport();

            // Simulate property change after probe1 executes
            report.addPropertyChangeListener(
                    evt -> {
                        if ("supportedProbe".equals(evt.getPropertyName())
                                && evt.getNewValue().equals(new TestProbeType("probe1"))) {
                            probe2.setCanExecute(true);
                        }
                    });

            executor.execute(report);

            assertEquals(2, report.getExecutedProbeTypes().size());
            assertTrue(probe1.wasExecuted());
            assertTrue(probe2.wasExecuted());
        }
    }

    @Test
    public void testStatisticsCollection() throws InterruptedException {
        TestProbe probe1 = new TestProbe(new TestProbeType("probe1"));
        TestProbe probe2 = new TestProbe(new TestProbeType("probe2"));

        TestStatsWriter writer1 = (TestStatsWriter) probe1.getWriter();
        TestStatsWriter writer2 = (TestStatsWriter) probe2.getWriter();
        writer1.setStateCount(5);
        writer2.setStateCount(3);

        List<TestProbe> probeList = Arrays.asList(probe1, probe2);
        List<TestAfterProbe> afterList = new ArrayList<>();

        ScanJob<TestReport, TestProbe, TestAfterProbe, TestState> scanJob =
                new ScanJob<>(probeList, afterList);

        try (ThreadedScanJobExecutor<TestReport, TestProbe, TestAfterProbe, TestState> executor =
                new ThreadedScanJobExecutor<>(executorConfig, scanJob, 2, "Test")) {

            TestReport report = new TestReport();
            executor.execute(report);

            assertEquals(8, report.getPerformedConnections()); // 5 + 3
            assertFalse(report.getExtractedValueContainers().isEmpty());
        }
    }

    @Test
    public void testShutdown() throws InterruptedException {
        List<TestProbe> probeList = Arrays.asList(new TestProbe(new TestProbeType("probe1")));
        List<TestAfterProbe> afterList = new ArrayList<>();

        ScanJob<TestReport, TestProbe, TestAfterProbe, TestState> scanJob =
                new ScanJob<>(probeList, afterList);

        ThreadedScanJobExecutor<TestReport, TestProbe, TestAfterProbe, TestState> executor =
                new ThreadedScanJobExecutor<>(executorConfig, scanJob, 1, "Test");

        TestReport report = new TestReport();
        executor.execute(report);

        executor.shutdown();
        // Should not throw exception
    }

    @Test
    public void testAutoCloseable() throws Exception {
        List<TestProbe> probeList = Arrays.asList(new TestProbe(new TestProbeType("probe1")));
        List<TestAfterProbe> afterList = new ArrayList<>();

        ScanJob<TestReport, TestProbe, TestAfterProbe, TestState> scanJob =
                new ScanJob<>(probeList, afterList);

        ThreadedScanJobExecutor<TestReport, TestProbe, TestAfterProbe, TestState> executor =
                new ThreadedScanJobExecutor<>(executorConfig, scanJob, 1, "Test");

        TestReport report = new TestReport();
        executor.execute(report);

        executor.close(); // Test AutoCloseable
    }

    @Test
    public void testConstructorWithCustomExecutor() throws InterruptedException {
        List<TestProbe> probeList = Arrays.asList(new TestProbe(new TestProbeType("probe1")));
        List<TestAfterProbe> afterList = new ArrayList<>();

        ScanJob<TestReport, TestProbe, TestAfterProbe, TestState> scanJob =
                new ScanJob<>(probeList, afterList);

        ThreadPoolExecutor customExecutor =
                new ScannerThreadPoolExecutor(
                        1, new NamedThreadFactory("Custom"), new Semaphore(0), 5000);

        ThreadedScanJobExecutor<TestReport, TestProbe, TestAfterProbe, TestState> executor =
                new ThreadedScanJobExecutor<>(executorConfig, scanJob, customExecutor);

        TestReport report = new TestReport();
        executor.execute(report);

        assertEquals(1, report.getExecutedProbeTypes().size());

        executor.shutdown();
        customExecutor.shutdown();
    }

    @Test
    public void testPropertyChangeWithInvalidSource() {
        List<TestProbe> probeList = new ArrayList<>();
        List<TestAfterProbe> afterList = new ArrayList<>();

        ScanJob<TestReport, TestProbe, TestAfterProbe, TestState> scanJob =
                new ScanJob<>(probeList, afterList);

        try (ThreadedScanJobExecutor<TestReport, TestProbe, TestAfterProbe, TestState> executor =
                new ThreadedScanJobExecutor<>(executorConfig, scanJob, 1, "Test")) {

            // Test with non-ScanReport source
            PropertyChangeEvent event =
                    new PropertyChangeEvent(
                            new Object(), "supportedProbe", null, new TestProbeType("test"));

            executor.propertyChange(event); // Should log error but not throw
        }
    }

    @Test
    public void testEmptyProbeList() throws InterruptedException {
        List<TestProbe> probeList = new ArrayList<>();
        List<TestAfterProbe> afterList = Arrays.asList(new TestAfterProbe());

        ScanJob<TestReport, TestProbe, TestAfterProbe, TestState> scanJob =
                new ScanJob<>(probeList, afterList);

        try (ThreadedScanJobExecutor<TestReport, TestProbe, TestAfterProbe, TestState> executor =
                new ThreadedScanJobExecutor<>(executorConfig, scanJob, 1, "Test")) {

            TestReport report = new TestReport();
            executor.execute(report);

            assertTrue(report.getExecutedProbeTypes().isEmpty());
            assertTrue(afterList.get(0).isAnalyzed()); // After probes should still run
        }
    }

    @Test
    public void testMultipleExtractedValueContainersOfSameType() throws InterruptedException {
        TestProbe probe1 = new TestProbe(new TestProbeType("probe1"));
        TestProbe probe2 = new TestProbe(new TestProbeType("probe2"));

        List<TestProbe> probeList = Arrays.asList(probe1, probe2);
        List<TestAfterProbe> afterList = new ArrayList<>();

        ScanJob<TestReport, TestProbe, TestAfterProbe, TestState> scanJob =
                new ScanJob<>(probeList, afterList);

        try (ThreadedScanJobExecutor<TestReport, TestProbe, TestAfterProbe, TestState> executor =
                new ThreadedScanJobExecutor<>(executorConfig, scanJob, 2, "Test")) {

            TestReport report = new TestReport();
            executor.execute(report);

            // Should merge containers of same type
            assertEquals(1, report.getExtractedValueContainers().size());
            ExtractedValueContainer<?> container =
                    report.getExtractedValueContainers().get(TestTrackableValue.TEST_VALUE);
            assertNotNull(container);
            // Each probe contributes 2 values, so we should have 4 total
            assertEquals(4, container.getExtractedValueList().size());
        }
    }
}
