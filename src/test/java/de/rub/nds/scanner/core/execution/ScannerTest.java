/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.execution;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import de.rub.nds.scanner.core.afterprobe.AfterProbe;
import de.rub.nds.scanner.core.config.ExecutorConfig;
import de.rub.nds.scanner.core.passive.StatsWriter;
import de.rub.nds.scanner.core.probe.ProbeType;
import de.rub.nds.scanner.core.probe.ScannerProbe;
import de.rub.nds.scanner.core.probe.requirements.FulfilledRequirement;
import de.rub.nds.scanner.core.probe.requirements.Requirement;
import de.rub.nds.scanner.core.report.ScanReport;
import org.junit.jupiter.api.Test;

class ScannerTest {

    @Test
    void testCloseMethod() {
        // Test that the Scanner's close() method can be called without throwing exceptions
        ExecutorConfig config = new ExecutorConfig();
        TestScanner scanner = new TestScanner(config);

        assertDoesNotThrow(
                () -> {
                    try (TestScanner s = scanner) {
                        // Using try-with-resources to ensure close() is called
                    }
                });
    }

    // Test implementation of Scanner for testing purposes
    private static class TestScanner
            extends Scanner<TestReport, TestProbe, TestAfterProbe, TestState> {

        public TestScanner(ExecutorConfig executorConfig) {
            super(executorConfig);
        }

        @Override
        protected void fillProbeLists() {
            // Empty implementation for testing
        }

        @Override
        protected StatsWriter<TestState> getDefaultProbeWriter() {
            return null;
        }

        @Override
        protected TestReport getEmptyReport() {
            return new TestReport();
        }

        @Override
        protected boolean checkScanPrerequisites(TestReport report) {
            return true;
        }
    }

    // Test implementations of required types
    private static class TestReport extends ScanReport {
        @Override
        public String getRemoteName() {
            return "test";
        }

        @Override
        public void serializeToJson(java.io.OutputStream outputStream) {
            // Empty implementation for testing
        }
    }

    private static class TestProbe extends ScannerProbe<TestReport, TestState> {
        public TestProbe() {
            super(new TestProbeType());
        }

        @Override
        public void executeTest() {
            // Empty implementation
        }

        @Override
        public void adjustConfig(TestReport report) {
            // Empty implementation
        }

        @Override
        public void mergeData(TestReport report) {
            // Empty implementation for testing
        }

        @Override
        public Requirement<TestReport> getRequirements() {
            return new FulfilledRequirement<>();
        }
    }

    private static class TestProbeType implements ProbeType {
        @Override
        public String getName() {
            return "TEST_PROBE";
        }
    }

    private static class TestAfterProbe extends AfterProbe<TestReport> {
        @Override
        public void analyze(TestReport report) {
            // Empty implementation
        }
    }

    private static class TestState {
        // Empty state class for testing
    }
}
