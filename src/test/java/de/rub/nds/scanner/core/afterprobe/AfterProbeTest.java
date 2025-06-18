/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.afterprobe;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.rub.nds.scanner.core.report.ScanReport;
import java.io.OutputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AfterProbeTest {

    private TestAfterProbe testAfterProbe;
    private TestScanReport testReport;
    private boolean analyzeCalled;

    @BeforeEach
    void setUp() {
        analyzeCalled = false;
        testAfterProbe = new TestAfterProbe();
        testReport = new TestScanReport();
    }

    @Test
    void testAnalyzeIsCalled() {
        assertDoesNotThrow(() -> testAfterProbe.analyze(testReport));
        assertTrue(analyzeCalled);
    }

    @Test
    void testAnalyzeWithNullReport() {
        assertDoesNotThrow(() -> testAfterProbe.analyze(null));
        assertTrue(analyzeCalled);
    }

    @Test
    void testMultipleAnalyzeCalls() {
        testAfterProbe.analyze(testReport);
        assertTrue(analyzeCalled);

        analyzeCalled = false;
        testAfterProbe.analyze(testReport);
        assertTrue(analyzeCalled);
    }

    @Test
    void testInstanceCreation() {
        assertNotNull(testAfterProbe);
    }

    @Test
    void testAnalyzeWithDifferentReports() {
        TestScanReport report1 = new TestScanReport();
        TestScanReport report2 = new TestScanReport();

        testAfterProbe.analyze(report1);
        assertTrue(analyzeCalled);

        analyzeCalled = false;
        testAfterProbe.analyze(report2);
        assertTrue(analyzeCalled);
    }

    private class TestAfterProbe extends AfterProbe<TestScanReport> {
        @Override
        public void analyze(TestScanReport report) {
            analyzeCalled = true;
        }
    }

    private static class TestScanReport extends ScanReport {
        @Override
        public String getRemoteName() {
            return "test-remote";
        }

        @Override
        public void serializeToJson(OutputStream outputStream) {
            // Empty implementation for test
        }
    }
}
