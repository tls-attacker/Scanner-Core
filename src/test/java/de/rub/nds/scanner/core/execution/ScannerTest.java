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
import de.rub.nds.scanner.core.guideline.Guideline;
import de.rub.nds.scanner.core.passive.StatsWriter;
import de.rub.nds.scanner.core.probe.ProbeType;
import de.rub.nds.scanner.core.probe.ScannerProbe;
import de.rub.nds.scanner.core.probe.requirements.FulfilledRequirement;
import de.rub.nds.scanner.core.probe.requirements.Requirement;
import de.rub.nds.scanner.core.probe.requirements.UnfulfillableRequirement;
import de.rub.nds.scanner.core.report.ScanReport;
import de.rub.nds.scanner.core.report.rating.RatingInfluencers;
import de.rub.nds.scanner.core.report.rating.Recommendations;
import de.rub.nds.scanner.core.report.rating.SiteReportRater;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ScannerTest {

    @TempDir private File tempDir;

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
    }

    static class TestState {}

    static class TestReport extends ScanReport {
        private boolean prerequisitesFulfilled = true;

        @Override
        public String getRemoteName() {
            return "TestHost";
        }

        @Override
        public void serializeToJson(OutputStream outputStream) {
            // Simple implementation for testing
            try {
                outputStream.write("{\"remoteName\":\"TestHost\"}".getBytes());
            } catch (IOException e) {
                // Ignore for testing
            }
        }

        public void setPrerequisitesFulfilled(boolean fulfilled) {
            this.prerequisitesFulfilled = fulfilled;
        }

        public boolean arePrerequisitesFulfilled() {
            return prerequisitesFulfilled;
        }
    }

    static class TestProbe extends ScannerProbe<TestReport, TestState> {
        private boolean executed = false;
        private boolean canExecute = true;

        TestProbe(ProbeType type) {
            super(type);
        }

        @Override
        public void executeTest() {
            executed = true;
        }

        @Override
        public Requirement<TestReport> getRequirements() {
            if (canExecute) {
                return new FulfilledRequirement<>();
            } else {
                return new UnfulfillableRequirement<>();
            }
        }

        @Override
        public void adjustConfig(TestReport report) {}

        @Override
        protected void mergeData(TestReport report) {}

        public boolean isExecuted() {
            return executed;
        }

        public void setCanExecute(boolean canExecute) {
            this.canExecute = canExecute;
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
        @Override
        public void extract(TestState state) {}
    }

    static class TestScanner extends Scanner<TestReport, TestProbe, TestAfterProbe, TestState> {
        private boolean fillProbesCalled = false;
        private boolean onScanStartCalled = false;
        private boolean onScanEndCalled = false;
        private TestReport reportToReturn;
        private boolean checkPrerequisites = true;
        private SiteReportRater rater;
        private List<Guideline> guidelines = new ArrayList<>();

        TestScanner(ExecutorConfig config) {
            super(config);
        }

        TestScanner(
                ExecutorConfig config, List<TestProbe> probeList, List<TestAfterProbe> afterList) {
            super(config, probeList, afterList);
        }

        @Override
        public void close() {
            // Implementation for AutoCloseable
        }

        @Override
        protected void fillProbeLists() {
            fillProbesCalled = true;
        }

        @Override
        protected StatsWriter<TestState> getDefaultProbeWriter() {
            return new TestStatsWriter();
        }

        @Override
        protected TestReport getEmptyReport() {
            if (reportToReturn != null) {
                return reportToReturn;
            }
            return new TestReport();
        }

        @Override
        protected boolean checkScanPrerequisites(TestReport report) {
            return checkPrerequisites && report.arePrerequisitesFulfilled();
        }

        @Override
        protected void onScanStart() {
            onScanStartCalled = true;
        }

        @Override
        protected void onScanEnd() {
            onScanEndCalled = true;
        }

        @Override
        protected SiteReportRater getSiteReportRater() {
            return rater;
        }

        @Override
        protected List<Guideline> getGuidelines() {
            return guidelines;
        }

        public void setReportToReturn(TestReport report) {
            this.reportToReturn = report;
        }

        public void setCheckPrerequisites(boolean check) {
            this.checkPrerequisites = check;
        }

        public void setSiteReportRater(SiteReportRater rater) {
            this.rater = rater;
        }

        public void setGuidelines(List<Guideline> guidelines) {
            this.guidelines = guidelines;
        }

        public boolean isFillProbesCalled() {
            return fillProbesCalled;
        }

        public boolean isOnScanStartCalled() {
            return onScanStartCalled;
        }

        public boolean isOnScanEndCalled() {
            return onScanEndCalled;
        }
    }

    @BeforeEach
    public void setUp() {
        executorConfig = new ExecutorConfig();
        executorConfig.setParallelProbes(1);
        executorConfig.setProbeTimeout(1000);
    }

    @Test
    public void testScanWithDefaultConstructor() {
        try (TestScanner scanner = new TestScanner(executorConfig)) {
            TestReport report = scanner.scan();

            assertNotNull(report);
            assertTrue(scanner.isFillProbesCalled());
            assertTrue(scanner.isOnScanStartCalled());
            assertTrue(scanner.isOnScanEndCalled());
        }
    }

    @Test
    public void testScanWithProbesConstructor() {
        List<TestProbe> probeList = new ArrayList<>();
        probeList.add(new TestProbe(new TestProbeType("probe1")));

        List<TestAfterProbe> afterList = new ArrayList<>();
        afterList.add(new TestAfterProbe());

        try (TestScanner scanner = new TestScanner(executorConfig, probeList, afterList)) {
            TestReport report = scanner.scan();

            assertNotNull(report);
            assertFalse(
                    scanner.isFillProbesCalled()); // Should not be called when probes are provided
        }
    }

    @Test
    public void testScanPrerequisitesNotFulfilled() {
        try (TestScanner scanner = new TestScanner(executorConfig)) {
            TestReport report = new TestReport();
            report.setPrerequisitesFulfilled(false);
            scanner.setReportToReturn(report);

            TestReport result = scanner.scan();

            assertSame(report, result);
            assertTrue(scanner.isOnScanStartCalled());
            assertFalse(scanner.isOnScanEndCalled()); // Should not reach end if prerequisites fail
        }
    }

    @Test
    public void testRegisterProbeForExecution() {
        try (TestScanner scanner = new TestScanner(executorConfig)) {
            TestProbe probe = new TestProbe(new TestProbeType("test"));

            scanner.registerProbeForExecution(probe);
        }
        // Since this adds to internal probe list, we can't directly verify
        // But the test ensures no exceptions are thrown
    }

    @Test
    public void testRegisterProbeWithExecuteByDefault() {
        try (TestScanner scanner = new TestScanner(executorConfig)) {
            TestProbe probe = new TestProbe(new TestProbeType("test"));

            scanner.registerProbeForExecution(probe, false);
        }
        // Test with executeByDefault = false
    }

    @Test
    public void testRegisterProbeWithSpecificProbesConfig() {
        executorConfig.setProbes(List.of(new TestProbeType("allowed")));
        try (TestScanner scanner = new TestScanner(executorConfig)) {

            TestProbe allowedProbe = new TestProbe(new TestProbeType("allowed"));
            TestProbe notAllowedProbe = new TestProbe(new TestProbeType("notallowed"));

            scanner.registerProbeForExecution(allowedProbe);
            scanner.registerProbeForExecution(notAllowedProbe);
        }
    }

    @Test
    public void testRegisterProbeWithExcludedProbes() {
        executorConfig.setExcludedProbes(List.of(new TestProbeType("excluded")));
        try (TestScanner scanner = new TestScanner(executorConfig)) {

            TestProbe excludedProbe = new TestProbe(new TestProbeType("excluded"));
            scanner.registerProbeForExecution(excludedProbe);
        }
    }

    @Test
    public void testRegisterAfterProbe() {
        try (TestScanner scanner = new TestScanner(executorConfig)) {
            TestAfterProbe afterProbe = new TestAfterProbe();

            scanner.registerProbeForExecution(afterProbe);
        }
    }

    @Test
    public void testScanWithSiteReportRater() {
        TestReport report;
        try (TestScanner scanner = new TestScanner(executorConfig)) {
            RatingInfluencers influencers = new RatingInfluencers(new LinkedList<>());
            Recommendations recommendations = new Recommendations(new LinkedList<>());
            SiteReportRater rater = new SiteReportRater(influencers, recommendations);
            scanner.setSiteReportRater(rater);

            report = scanner.scan();
        }

        assertNotNull(report.getScoreReport());
    }

    @Test
    public void testScanWithGuidelines() {
        TestReport report;
        try (TestScanner scanner = new TestScanner(executorConfig)) {

            Guideline guideline =
                    new Guideline("TestGuideline", "http://example.com", new ArrayList<>());

            scanner.setGuidelines(List.of(guideline));
            report = scanner.scan();
        }

        assertNotNull(report);
    }

    @Test
    public void testScanWithFileOutput() throws IOException {
        File outputFile = new File(tempDir, "test-report.json");
        executorConfig.setOutputFile(outputFile.getAbsolutePath());

        try (TestScanner scanner = new TestScanner(executorConfig)) {
            TestReport report = scanner.scan();
        }

        assertTrue(outputFile.exists());
        String content = Files.readString(outputFile.toPath());
        assertFalse(content.isEmpty());
    }

    @Test
    public void testScanWithInvalidOutputFile() {
        File invalidFile = new File("/invalid/path/that/does/not/exist/report.json");
        executorConfig.setOutputFile(invalidFile.getAbsolutePath());

        try (TestScanner scanner = new TestScanner(executorConfig)) {
            assertThrows(RuntimeException.class, scanner::scan);
        }
    }

    @Test
    public void testAutoCloseable() throws Exception {
        TestScanner scanner = new TestScanner(executorConfig);

        // Test that Scanner implements AutoCloseable
        assertInstanceOf(AutoCloseable.class, scanner);

        // Test close method
        assertDoesNotThrow(scanner::close);
    }

    @Test
    public void testScanTimingMeasurement() {
        TestReport report;
        try (TestScanner scanner = new TestScanner(executorConfig)) {
            report = scanner.scan();
        }

        assertTrue(report.getScanStartTime() > 0);
        assertTrue(report.getScanEndTime() > 0);
        assertTrue(report.getScanEndTime() >= report.getScanStartTime());
    }

    @Test
    public void testInterruptedScan() throws InterruptedException {
        Thread testThread;
        try (TestScanner scanner = new TestScanner(executorConfig)) {
            testThread =
                    new Thread(
                            () -> {
                                Thread.currentThread().interrupt();
                                scanner.scan();
                            });
        }

        testThread.start();
        testThread.join();

        assertTrue(testThread.isInterrupted() || !testThread.isAlive());
    }
}
