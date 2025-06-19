/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.probe.requirements;

import static org.junit.jupiter.api.Assertions.*;

import de.rub.nds.scanner.core.probe.AnalyzedProperty;
import de.rub.nds.scanner.core.probe.AnalyzedPropertyCategory;
import de.rub.nds.scanner.core.probe.result.TestResult;
import de.rub.nds.scanner.core.probe.result.TestResults;
import de.rub.nds.scanner.core.report.ScanReport;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class PropertyValueRequirementTest {

    private static enum TestPropertyCategory implements AnalyzedPropertyCategory {
        TEST_CATEGORY
    }

    private static enum TestProperty implements AnalyzedProperty {
        PROPERTY_1,
        PROPERTY_2,
        PROPERTY_3;

        @Override
        public AnalyzedPropertyCategory getCategory() {
            return TestPropertyCategory.TEST_CATEGORY;
        }

        @Override
        public String getName() {
            return name();
        }
    }

    private static class TestScanReport extends ScanReport {
        private final Map<AnalyzedProperty, TestResult> resultMap;

        public TestScanReport(Map<AnalyzedProperty, TestResult> resultMap) {
            this.resultMap = resultMap;
        }

        @Override
        public Map<AnalyzedProperty, TestResult> getResultMap() {
            return resultMap;
        }

        @Override
        public void serializeToJson(OutputStream stream) {}

        @Override
        public String getRemoteName() {
            return "test";
        }
    }

    private static class CustomTestResult implements TestResult {
        private final String name;

        public CustomTestResult(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof CustomTestResult) {
                return name.equals(((CustomTestResult) obj).name);
            }
            return false;
        }
    }

    @Test
    public void testConstructorWithList() {
        List<AnalyzedProperty> properties =
                Arrays.asList(TestProperty.PROPERTY_1, TestProperty.PROPERTY_2);
        PropertyValueRequirement<ScanReport> requirement =
                new PropertyValueRequirement<>(TestResults.TRUE, properties);

        assertEquals(TestResults.TRUE, requirement.getRequiredTestResult());
        assertEquals(2, requirement.getParameters().size());
    }

    @Test
    public void testConstructorWithVarargs() {
        PropertyValueRequirement<ScanReport> requirement =
                new PropertyValueRequirement<>(
                        TestResults.FALSE, TestProperty.PROPERTY_1, TestProperty.PROPERTY_2);

        assertEquals(TestResults.FALSE, requirement.getRequiredTestResult());
        assertEquals(2, requirement.getParameters().size());
    }

    @Test
    public void testEvaluateWithEmptyProperties() {
        PropertyValueRequirement<ScanReport> requirement =
                new PropertyValueRequirement<>(TestResults.TRUE);
        TestScanReport report = new TestScanReport(new HashMap<>());

        assertTrue(requirement.evaluate(report));
    }

    @Test
    public void testEvaluateWithMatchingValues() {
        Map<AnalyzedProperty, TestResult> resultMap = new HashMap<>();
        resultMap.put(TestProperty.PROPERTY_1, TestResults.TRUE);
        resultMap.put(TestProperty.PROPERTY_2, TestResults.TRUE);

        PropertyValueRequirement<ScanReport> requirement =
                new PropertyValueRequirement<>(
                        TestResults.TRUE, TestProperty.PROPERTY_1, TestProperty.PROPERTY_2);
        TestScanReport report = new TestScanReport(resultMap);

        assertTrue(requirement.evaluate(report));
    }

    @Test
    public void testEvaluateWithNonMatchingValue() {
        Map<AnalyzedProperty, TestResult> resultMap = new HashMap<>();
        resultMap.put(TestProperty.PROPERTY_1, TestResults.TRUE);
        resultMap.put(TestProperty.PROPERTY_2, TestResults.FALSE); // Doesn't match required TRUE

        PropertyValueRequirement<ScanReport> requirement =
                new PropertyValueRequirement<>(
                        TestResults.TRUE, TestProperty.PROPERTY_1, TestProperty.PROPERTY_2);
        TestScanReport report = new TestScanReport(resultMap);

        assertFalse(requirement.evaluate(report));
    }

    @Test
    public void testEvaluateWithMissingProperty() {
        Map<AnalyzedProperty, TestResult> resultMap = new HashMap<>();
        resultMap.put(TestProperty.PROPERTY_1, TestResults.TRUE);
        // PROPERTY_2 is missing

        PropertyValueRequirement<ScanReport> requirement =
                new PropertyValueRequirement<>(
                        TestResults.TRUE, TestProperty.PROPERTY_1, TestProperty.PROPERTY_2);
        TestScanReport report = new TestScanReport(resultMap);

        assertFalse(requirement.evaluate(report));
    }

    @Test
    public void testEvaluateWithNullValue() {
        Map<AnalyzedProperty, TestResult> resultMap = new HashMap<>();
        resultMap.put(TestProperty.PROPERTY_1, TestResults.TRUE);
        resultMap.put(TestProperty.PROPERTY_2, null);

        PropertyValueRequirement<ScanReport> requirement =
                new PropertyValueRequirement<>(
                        TestResults.TRUE, TestProperty.PROPERTY_1, TestProperty.PROPERTY_2);
        TestScanReport report = new TestScanReport(resultMap);

        assertFalse(requirement.evaluate(report));
    }

    @Test
    public void testEvaluateWithTypeMismatchThrowsException() {
        Map<AnalyzedProperty, TestResult> resultMap = new HashMap<>();
        resultMap.put(TestProperty.PROPERTY_1, new CustomTestResult("custom"));

        PropertyValueRequirement<ScanReport> requirement =
                new PropertyValueRequirement<>(TestResults.TRUE, TestProperty.PROPERTY_1);
        TestScanReport report = new TestScanReport(resultMap);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> requirement.evaluate(report));

        assertTrue(exception.getMessage().contains("Cannot evaluate Requirement for Property"));
        assertTrue(exception.getMessage().contains("PROPERTY_1"));
        assertNotNull(exception.getCause());
    }

    @Test
    public void testToString() {
        PropertyValueRequirement<ScanReport> requirement =
                new PropertyValueRequirement<>(
                        TestResults.TRUE, TestProperty.PROPERTY_1, TestProperty.PROPERTY_2);

        String expected = "PropertyValueRequirement[TRUE: PROPERTY_1 PROPERTY_2]";
        assertEquals(expected, requirement.toString());
    }

    @Test
    public void testToStringWithEmptyProperties() {
        PropertyValueRequirement<ScanReport> requirement =
                new PropertyValueRequirement<>(TestResults.FALSE);

        String expected = "PropertyValueRequirement[FALSE: ]";
        assertEquals(expected, requirement.toString());
    }

    @Test
    public void testGetRequiredTestResult() {
        PropertyValueRequirement<ScanReport> requirement1 =
                new PropertyValueRequirement<>(TestResults.TRUE);
        assertEquals(TestResults.TRUE, requirement1.getRequiredTestResult());

        PropertyValueRequirement<ScanReport> requirement2 =
                new PropertyValueRequirement<>(TestResults.PARTIALLY);
        assertEquals(TestResults.PARTIALLY, requirement2.getRequiredTestResult());
    }

    @Test
    public void testWithAllTestResultTypes() {
        Map<AnalyzedProperty, TestResult> resultMap = new HashMap<>();

        // Test with different TestResults enum values
        for (TestResults testResult : TestResults.values()) {
            resultMap.put(TestProperty.PROPERTY_1, testResult);

            PropertyValueRequirement<ScanReport> requirement =
                    new PropertyValueRequirement<>(testResult, TestProperty.PROPERTY_1);
            TestScanReport report = new TestScanReport(resultMap);

            assertTrue(requirement.evaluate(report), "Failed for TestResult: " + testResult);
        }
    }
}
