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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class GuidelineCheckTest {

    // Mock implementations for testing
    private static class TestAnalyzedProperty implements AnalyzedProperty {
        private final String name;

        TestAnalyzedProperty(String name) {
            this.name = name;
        }

        @Override
        String getName() {
            return name;
        }

        @Override
        AnalyzedPropertyCategory getCategory() {
            return new TestPropertyCategory();
        }
    }

    private static class TestScanReport extends ScanReport {
        private final Map<AnalyzedProperty, TestResult> results = new HashMap<>();

        void putResult(AnalyzedProperty property, TestResult result) {
            results.put(property, result);
        }

        @Override
        TestResult getResult(AnalyzedProperty property) {
            return results.get(property);
        }

        @Override
        void serializeToJson(java.io.OutputStream outputStream) {
            // Test implementation - do nothing
        }

        @Override
        String getRemoteName() {
            return "TestRemote";
        }
    }

    private static class ConcreteGuidelineCheck extends GuidelineCheck<TestScanReport> {
        private final GuidelineAdherence fixedResult;

        ConcreteGuidelineCheck(String name, RequirementLevel level) {
            super(name, level);
            this.fixedResult = GuidelineAdherence.ADHERED;
        }

        ConcreteGuidelineCheck(
                String name, RequirementLevel level, GuidelineCheckCondition condition) {
            super(name, level, condition);
            this.fixedResult = GuidelineAdherence.ADHERED;
        }

        ConcreteGuidelineCheck(
                String name, RequirementLevel level, GuidelineAdherence fixedResult) {
            super(name, level);
            this.fixedResult = fixedResult;
        }

        @Override
        GuidelineCheckResult evaluate(TestScanReport report) {
            return new FailedCheckGuidelineResult(getName(), fixedResult);
        }
    }

    @Test
    void testConstructorWithNameAndLevel() {
        String name = "TestCheck";
        RequirementLevel level = RequirementLevel.MUST;

        ConcreteGuidelineCheck check = new ConcreteGuidelineCheck(name, level);

        assertEquals(name, check.getName());
        assertEquals(level, check.getRequirementLevel());
        assertNull(check.getCondition());
    }

    @Test
    void testConstructorWithNameLevelAndCondition() {
        String name = "TestCheck";
        RequirementLevel level = RequirementLevel.SHOULD;
        GuidelineCheckCondition condition =
                new GuidelineCheckCondition(new TestAnalyzedProperty("prop"), TestResults.TRUE);

        ConcreteGuidelineCheck check = new ConcreteGuidelineCheck(name, level, condition);

        assertEquals(name, check.getName());
        assertEquals(level, check.getRequirementLevel());
        assertEquals(condition, check.getCondition());
    }

    @Test
    void testPassesConditionWithNoCondition() {
        ConcreteGuidelineCheck check = new ConcreteGuidelineCheck("Test", RequirementLevel.MUST);
        TestScanReport report = new TestScanReport();

        assertTrue(check.passesCondition(report));
    }

    @Test
    void testPassesConditionWithSimpleCondition() {
        TestAnalyzedProperty property = new TestAnalyzedProperty("TestProp");
        GuidelineCheckCondition condition = new GuidelineCheckCondition(property, TestResults.TRUE);
        ConcreteGuidelineCheck check =
                new ConcreteGuidelineCheck("Test", RequirementLevel.MUST, condition);

        TestScanReport report = new TestScanReport();
        report.putResult(property, TestResults.TRUE);

        assertTrue(check.passesCondition(report));

        report.putResult(property, TestResults.FALSE);
        assertFalse(check.passesCondition(report));
    }

    @Test
    void testPassesConditionWithAndConditions() {
        TestAnalyzedProperty prop1 = new TestAnalyzedProperty("Prop1");
        TestAnalyzedProperty prop2 = new TestAnalyzedProperty("Prop2");

        GuidelineCheckCondition andCondition =
                GuidelineCheckCondition.and(
                        Arrays.asList(
                                new GuidelineCheckCondition(prop1, TestResults.TRUE),
                                new GuidelineCheckCondition(prop2, TestResults.TRUE)));

        ConcreteGuidelineCheck check =
                new ConcreteGuidelineCheck("Test", RequirementLevel.MUST, andCondition);
        TestScanReport report = new TestScanReport();

        // Both true - should pass
        report.putResult(prop1, TestResults.TRUE);
        report.putResult(prop2, TestResults.TRUE);
        assertTrue(check.passesCondition(report));

        // One false - should fail
        report.putResult(prop2, TestResults.FALSE);
        assertFalse(check.passesCondition(report));

        // Both false - should fail
        report.putResult(prop1, TestResults.FALSE);
        assertFalse(check.passesCondition(report));
    }

    @Test
    void testPassesConditionWithOrConditions() {
        TestAnalyzedProperty prop1 = new TestAnalyzedProperty("Prop1");
        TestAnalyzedProperty prop2 = new TestAnalyzedProperty("Prop2");

        GuidelineCheckCondition orCondition =
                GuidelineCheckCondition.or(
                        Arrays.asList(
                                new GuidelineCheckCondition(prop1, TestResults.TRUE),
                                new GuidelineCheckCondition(prop2, TestResults.TRUE)));

        ConcreteGuidelineCheck check =
                new ConcreteGuidelineCheck("Test", RequirementLevel.MUST, orCondition);
        TestScanReport report = new TestScanReport();

        // Both true - should pass
        report.putResult(prop1, TestResults.TRUE);
        report.putResult(prop2, TestResults.TRUE);
        assertTrue(check.passesCondition(report));

        // One false - should still pass
        report.putResult(prop2, TestResults.FALSE);
        assertTrue(check.passesCondition(report));

        // Both false - should fail
        report.putResult(prop1, TestResults.FALSE);
        assertFalse(check.passesCondition(report));
    }

    @Test
    void testPassesConditionWithNestedConditions() {
        TestAnalyzedProperty prop1 = new TestAnalyzedProperty("Prop1");
        TestAnalyzedProperty prop2 = new TestAnalyzedProperty("Prop2");
        TestAnalyzedProperty prop3 = new TestAnalyzedProperty("Prop3");

        // (Prop1 AND Prop2) OR Prop3
        GuidelineCheckCondition nestedCondition =
                GuidelineCheckCondition.or(
                        Arrays.asList(
                                GuidelineCheckCondition.and(
                                        Arrays.asList(
                                                new GuidelineCheckCondition(
                                                        prop1, TestResults.TRUE),
                                                new GuidelineCheckCondition(
                                                        prop2, TestResults.TRUE))),
                                new GuidelineCheckCondition(prop3, TestResults.TRUE)));

        ConcreteGuidelineCheck check =
                new ConcreteGuidelineCheck("Test", RequirementLevel.MUST, nestedCondition);
        TestScanReport report = new TestScanReport();

        // Prop3 true, others false - should pass
        report.putResult(prop1, TestResults.FALSE);
        report.putResult(prop2, TestResults.FALSE);
        report.putResult(prop3, TestResults.TRUE);
        assertTrue(check.passesCondition(report));

        // Prop1 and Prop2 true, Prop3 false - should pass
        report.putResult(prop1, TestResults.TRUE);
        report.putResult(prop2, TestResults.TRUE);
        report.putResult(prop3, TestResults.FALSE);
        assertTrue(check.passesCondition(report));

        // All false - should fail
        report.putResult(prop1, TestResults.FALSE);
        report.putResult(prop2, TestResults.FALSE);
        report.putResult(prop3, TestResults.FALSE);
        assertFalse(check.passesCondition(report));
    }

    @Test
    void testPassesConditionWithInvalidCondition() {
        // Create a condition without any valid fields set
        GuidelineCheckCondition invalidCondition = new GuidelineCheckCondition(null, null);
        ConcreteGuidelineCheck check =
                new ConcreteGuidelineCheck("Test", RequirementLevel.MUST, invalidCondition);
        TestScanReport report = new TestScanReport();

        // Should return false and log warning
        assertFalse(check.passesCondition(report));
    }

    @Test
    void testPassesConditionWithEmptyOrList() {
        GuidelineCheckCondition emptyOr = GuidelineCheckCondition.or(Arrays.asList());
        ConcreteGuidelineCheck check =
                new ConcreteGuidelineCheck("Test", RequirementLevel.MUST, emptyOr);
        TestScanReport report = new TestScanReport();

        // Empty OR should return true (vacuous truth)
        assertTrue(check.passesCondition(report));
    }

    @Test
    void testPassesConditionWithEmptyAndList() {
        GuidelineCheckCondition emptyAnd = GuidelineCheckCondition.and(Arrays.asList());
        ConcreteGuidelineCheck check =
                new ConcreteGuidelineCheck("Test", RequirementLevel.MUST, emptyAnd);
        TestScanReport report = new TestScanReport();

        // Empty AND should return true (vacuous truth)
        assertTrue(check.passesCondition(report));
    }

    @Test
    void testEvaluateMethod() {
        ConcreteGuidelineCheck check =
                new ConcreteGuidelineCheck(
                        "Test", RequirementLevel.MUST, GuidelineAdherence.VIOLATED);
        TestScanReport report = new TestScanReport();

        GuidelineCheckResult result = check.evaluate(report);

        assertNotNull(result);
        assertEquals("Test", result.getCheckName());
        assertEquals(GuidelineAdherence.VIOLATED, result.getAdherence());
    }
}
