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

public class PropertyRequirementTest {

    private static enum TestProperty implements AnalyzedProperty {
        PROPERTY_1,
        PROPERTY_2,
        PROPERTY_3;

        @Override
        public AnalyzedPropertyCategory getCategory() {
            return AnalyzedPropertyCategory.SECURITY;
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

    @Test
    public void testConstructorWithList() {
        List<AnalyzedProperty> properties = Arrays.asList(
                TestProperty.PROPERTY_1, TestProperty.PROPERTY_2);
        PropertyRequirement<ScanReport> requirement = new PropertyRequirement<>(properties);
        
        assertEquals(2, requirement.getParameters().size());
        assertEquals(TestProperty.PROPERTY_1, requirement.getParameters().get(0));
        assertEquals(TestProperty.PROPERTY_2, requirement.getParameters().get(1));
    }

    @Test
    public void testConstructorWithVarargs() {
        PropertyRequirement<ScanReport> requirement = new PropertyRequirement<>(
                TestProperty.PROPERTY_1, TestProperty.PROPERTY_2, TestProperty.PROPERTY_3);
        
        assertEquals(3, requirement.getParameters().size());
    }

    @Test
    public void testEvaluateWithEmptyProperties() {
        PropertyRequirement<ScanReport> requirement = new PropertyRequirement<>();
        TestScanReport report = new TestScanReport(new HashMap<>());
        
        assertTrue(requirement.evaluate(report));
    }

    @Test
    public void testEvaluateWithAllPropertiesPresent() {
        Map<AnalyzedProperty, TestResult> resultMap = new HashMap<>();
        resultMap.put(TestProperty.PROPERTY_1, TestResults.TRUE);
        resultMap.put(TestProperty.PROPERTY_2, TestResults.FALSE);
        
        PropertyRequirement<ScanReport> requirement = new PropertyRequirement<>(
                TestProperty.PROPERTY_1, TestProperty.PROPERTY_2);
        TestScanReport report = new TestScanReport(resultMap);
        
        assertTrue(requirement.evaluate(report));
    }

    @Test
    public void testEvaluateWithMissingProperty() {
        Map<AnalyzedProperty, TestResult> resultMap = new HashMap<>();
        resultMap.put(TestProperty.PROPERTY_1, TestResults.TRUE);
        // PROPERTY_2 is missing
        
        PropertyRequirement<ScanReport> requirement = new PropertyRequirement<>(
                TestProperty.PROPERTY_1, TestProperty.PROPERTY_2);
        TestScanReport report = new TestScanReport(resultMap);
        
        assertFalse(requirement.evaluate(report));
    }

    @Test
    public void testEvaluateWithUnassignedError() {
        Map<AnalyzedProperty, TestResult> resultMap = new HashMap<>();
        resultMap.put(TestProperty.PROPERTY_1, TestResults.TRUE);
        resultMap.put(TestProperty.PROPERTY_2, TestResults.UNASSIGNED_ERROR);
        
        PropertyRequirement<ScanReport> requirement = new PropertyRequirement<>(
                TestProperty.PROPERTY_1, TestProperty.PROPERTY_2);
        TestScanReport report = new TestScanReport(resultMap);
        
        assertFalse(requirement.evaluate(report));
    }

    @Test
    public void testToString() {
        PropertyRequirement<ScanReport> requirement = new PropertyRequirement<>(
                TestProperty.PROPERTY_1, TestProperty.PROPERTY_2);
        
        String expected = "PropertyRequirement[PROPERTY_1 PROPERTY_2]";
        assertEquals(expected, requirement.toString());
    }

    @Test
    public void testToStringWithEmptyProperties() {
        PropertyRequirement<ScanReport> requirement = new PropertyRequirement<>();
        
        String expected = "PropertyRequirement[]";
        assertEquals(expected, requirement.toString());
    }

    @Test
    public void testGetUnfulfilledRequirements() {
        Map<AnalyzedProperty, TestResult> resultMap = new HashMap<>();
        resultMap.put(TestProperty.PROPERTY_1, TestResults.TRUE);
        
        PropertyRequirement<ScanReport> requirement = new PropertyRequirement<>(
                TestProperty.PROPERTY_1);
        TestScanReport report = new TestScanReport(resultMap);
        
        // When requirement is fulfilled
        assertTrue(requirement.getUnfulfilledRequirements(report).isEmpty());
        
        // When requirement is not fulfilled
        PropertyRequirement<ScanReport> unfulfilledReq = new PropertyRequirement<>(
                TestProperty.PROPERTY_2);
        List<Requirement<ScanReport>> unfulfilled = unfulfilledReq.getUnfulfilledRequirements(report);
        assertEquals(1, unfulfilled.size());
        assertSame(unfulfilledReq, unfulfilled.get(0));
    }
}