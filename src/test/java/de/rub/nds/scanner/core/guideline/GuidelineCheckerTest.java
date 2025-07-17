/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.guideline;

import static org.junit.jupiter.api.Assertions.*;

import de.rub.nds.scanner.core.probe.AnalyzedProperty;
import de.rub.nds.scanner.core.probe.AnalyzedPropertyCategory;
import de.rub.nds.scanner.core.probe.result.TestResult;
import de.rub.nds.scanner.core.probe.result.TestResults;
import de.rub.nds.scanner.core.report.ScanReport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GuidelineCheckerTest {

    // Mock implementations
    private static class TestAnalyzedProperty implements AnalyzedProperty {
        private final String name;

        public TestAnalyzedProperty(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public AnalyzedPropertyCategory getCategory() {
            return new TestPropertyCategory();
        }
    }

    private static class TestScanReport extends ScanReport {
        private final Map<AnalyzedProperty, TestResult> results = new HashMap<>();
        private GuidelineReport addedReport;

        public void putResult(AnalyzedProperty property, TestResult result) {
            results.put(property, result);
        }

        @Override
        public TestResult getResult(AnalyzedProperty property) {
            return results.get(property);
        }

        @Override
        public void addGuidelineReport(GuidelineReport report) {
            this.addedReport = report;
        }

        public GuidelineReport getAddedReport() {
            return addedReport;
        }

        @Override
        public void serializeToJson(java.io.OutputStream outputStream) {
            // Test implementation - do nothing
        }

        @Override
        public String getRemoteName() {
            return "TestRemote";
        }
    }

    private static class PassingCheck extends GuidelineCheck {
        public PassingCheck(String name) {
            super(name, RequirementLevel.MUST);
        }

        @Override
        public <ReportT extends ScanReport> GuidelineCheckResult evaluate(ReportT report) {
            return new FailedCheckGuidelineResult(getName(), GuidelineAdherence.ADHERED);
        }
    }

    private static class FailingCheck extends GuidelineCheck {
        public FailingCheck(String name) {
            super(name, RequirementLevel.MUST);
        }

        @Override
        public <ReportT extends ScanReport> GuidelineCheckResult evaluate(ReportT report) {
            return new FailedCheckGuidelineResult(getName(), GuidelineAdherence.VIOLATED);
        }
    }

    private static class ConditionalCheck extends GuidelineCheck {
        public ConditionalCheck(String name, GuidelineCheckCondition condition) {
            super(name, RequirementLevel.SHOULD, condition);
        }

        @Override
        public <ReportT extends ScanReport> GuidelineCheckResult evaluate(ReportT report) {
            return new FailedCheckGuidelineResult(getName(), GuidelineAdherence.ADHERED);
        }
    }

    private static class ExceptionThrowingCheck extends GuidelineCheck {
        private final RuntimeException exception;

        public ExceptionThrowingCheck(String name, RuntimeException exception) {
            super(name, RequirementLevel.MUST);
            this.exception = exception;
        }

        @Override
        public <ReportT extends ScanReport> GuidelineCheckResult evaluate(ReportT report) {
            throw exception;
        }
    }

    private Guideline guideline;
    private TestScanReport report;

    @BeforeEach
    void setUp() {
        report = new TestScanReport();
    }

    @Test
    void testFillReportWithPassingChecks() {
        List<GuidelineCheck> checks =
                Arrays.asList(new PassingCheck("Check1"), new PassingCheck("Check2"));
        guideline = new Guideline("Test Guideline", "https://test.com", checks);
        GuidelineChecker<TestScanReport> checker = new GuidelineChecker<>(guideline);

        checker.fillReport(report);

        GuidelineReport addedReport = report.getAddedReport();
        assertNotNull(addedReport);
        assertEquals("Test Guideline", addedReport.getName());
        assertEquals("https://test.com", addedReport.getLink());
        assertEquals(2, addedReport.getResults().size());
        assertEquals(2, addedReport.getAdhered().size());
        assertEquals(0, addedReport.getViolated().size());
    }

    @Test
    void testFillReportWithFailingChecks() {
        List<GuidelineCheck> checks =
                Arrays.asList(
                        new PassingCheck("Check1"),
                        new FailingCheck("Check2"),
                        new FailingCheck("Check3"));
        guideline = new Guideline("Test Guideline", "https://test.com", checks);
        GuidelineChecker<TestScanReport> checker = new GuidelineChecker<>(guideline);

        checker.fillReport(report);

        GuidelineReport addedReport = report.getAddedReport();
        assertNotNull(addedReport);
        assertEquals(3, addedReport.getResults().size());
        assertEquals(1, addedReport.getAdhered().size());
        assertEquals(2, addedReport.getViolated().size());
    }

    @Test
    void testFillReportWithUnmetConditions() {
        TestAnalyzedProperty property = new TestAnalyzedProperty("TestProp");
        GuidelineCheckCondition condition = new GuidelineCheckCondition(property, TestResults.TRUE);

        List<GuidelineCheck> checks =
                Arrays.asList(
                        new PassingCheck("Check1"),
                        new ConditionalCheck("ConditionalCheck", condition));
        guideline = new Guideline("Test Guideline", "https://test.com", checks);
        GuidelineChecker<TestScanReport> checker = new GuidelineChecker<>(guideline);

        // Set condition to false
        report.putResult(property, TestResults.FALSE);

        checker.fillReport(report);

        GuidelineReport addedReport = report.getAddedReport();
        assertNotNull(addedReport);
        assertEquals(2, addedReport.getResults().size());
        assertEquals(1, addedReport.getAdhered().size());
        assertEquals(1, addedReport.getConditionNotMet().size());

        GuidelineCheckResult conditionNotMetResult = addedReport.getConditionNotMet().get(0);
        assertEquals("ConditionalCheck", conditionNotMetResult.getCheckName());
        assertEquals(GuidelineAdherence.CONDITION_NOT_MET, conditionNotMetResult.getAdherence());
    }

    @Test
    void testFillReportWithMetConditions() {
        TestAnalyzedProperty property = new TestAnalyzedProperty("TestProp");
        GuidelineCheckCondition condition = new GuidelineCheckCondition(property, TestResults.TRUE);

        List<GuidelineCheck> checks =
                Arrays.asList(new ConditionalCheck("ConditionalCheck", condition));
        guideline = new Guideline("Test Guideline", "https://test.com", checks);
        GuidelineChecker<TestScanReport> checker = new GuidelineChecker<>(guideline);

        // Set condition to true
        report.putResult(property, TestResults.TRUE);

        checker.fillReport(report);

        GuidelineReport addedReport = report.getAddedReport();
        assertNotNull(addedReport);
        assertEquals(1, addedReport.getResults().size());
        assertEquals(1, addedReport.getAdhered().size());
        assertEquals(0, addedReport.getConditionNotMet().size());
    }

    @Test
    void testFillReportWithExceptionThrowingCheck() {
        RuntimeException exception = new RuntimeException("Test exception");
        List<GuidelineCheck> checks =
                Arrays.asList(
                        new PassingCheck("Check1"),
                        new ExceptionThrowingCheck("ExceptionCheck", exception),
                        new PassingCheck("Check3"));
        guideline = new Guideline("Test Guideline", "https://test.com", checks);
        GuidelineChecker<TestScanReport> checker = new GuidelineChecker<>(guideline);

        checker.fillReport(report);

        GuidelineReport addedReport = report.getAddedReport();
        assertNotNull(addedReport);
        assertEquals(3, addedReport.getResults().size());
        assertEquals(2, addedReport.getAdhered().size());
        assertEquals(1, addedReport.getFailedChecks().size());

        GuidelineCheckResult failedResult = addedReport.getFailedChecks().get(0);
        assertEquals("ExceptionCheck", failedResult.getCheckName());
        assertEquals(GuidelineAdherence.CHECK_FAILED, failedResult.getAdherence());
        assertEquals("Test exception", failedResult.getHint());
    }

    @Test
    void testFillReportWithError() {
        Error error = new OutOfMemoryError("Test error");
        List<GuidelineCheck> checks =
                Arrays.asList(
                        new ExceptionThrowingCheck("ErrorCheck", new RuntimeException(error)));
        guideline = new Guideline("Test Guideline", "https://test.com", checks);
        GuidelineChecker<TestScanReport> checker = new GuidelineChecker<>(guideline);

        checker.fillReport(report);

        GuidelineReport addedReport = report.getAddedReport();
        assertNotNull(addedReport);
        assertEquals(1, addedReport.getFailedChecks().size());

        GuidelineCheckResult failedResult = addedReport.getFailedChecks().get(0);
        assertEquals(GuidelineAdherence.CHECK_FAILED, failedResult.getAdherence());
    }

    @Test
    void testFillReportWithEmptyChecks() {
        guideline = new Guideline("Test Guideline", "https://test.com", new ArrayList<>());
        GuidelineChecker<TestScanReport> checker = new GuidelineChecker<>(guideline);

        checker.fillReport(report);

        GuidelineReport addedReport = report.getAddedReport();
        assertNotNull(addedReport);
        assertEquals(0, addedReport.getResults().size());
    }

    @Test
    void testFillReportWithMixedResults() {
        TestAnalyzedProperty property = new TestAnalyzedProperty("TestProp");
        GuidelineCheckCondition condition = new GuidelineCheckCondition(property, TestResults.TRUE);

        List<GuidelineCheck> checks =
                Arrays.asList(
                        new PassingCheck("PassCheck"),
                        new FailingCheck("FailCheck"),
                        new ConditionalCheck("UnmetConditionCheck", condition),
                        new ExceptionThrowingCheck("ExceptionCheck", new RuntimeException("Test")));
        guideline = new Guideline("Mixed Guideline", "https://mixed.com", checks);
        GuidelineChecker<TestScanReport> checker = new GuidelineChecker<>(guideline);

        // Set condition to false
        report.putResult(property, TestResults.FALSE);

        checker.fillReport(report);

        GuidelineReport addedReport = report.getAddedReport();
        assertNotNull(addedReport);
        assertEquals(4, addedReport.getResults().size());
        assertEquals(1, addedReport.getAdhered().size());
        assertEquals(1, addedReport.getViolated().size());
        assertEquals(1, addedReport.getConditionNotMet().size());
        assertEquals(1, addedReport.getFailedChecks().size());
    }
}
