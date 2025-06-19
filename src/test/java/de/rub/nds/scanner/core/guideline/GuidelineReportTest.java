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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GuidelineReportTest {

    private List<GuidelineCheckResult> sampleResults;

    @BeforeEach
    void setUp() {
        sampleResults = new ArrayList<>();
        sampleResults.add(new FailedCheckGuidelineResult("Check1", GuidelineAdherence.ADHERED));
        sampleResults.add(new FailedCheckGuidelineResult("Check2", GuidelineAdherence.VIOLATED));
        sampleResults.add(
                new MissingRequirementGuidelineResult(
                        "Check3", GuidelineAdherence.CONDITION_NOT_MET));
        sampleResults.add(
                new FailedCheckGuidelineResult("Check4", GuidelineAdherence.CHECK_FAILED));
        sampleResults.add(new FailedCheckGuidelineResult("Check5", GuidelineAdherence.ADHERED));
        sampleResults.add(new FailedCheckGuidelineResult("Check6", GuidelineAdherence.VIOLATED));
    }

    @Test
    void testConstructorWithParameters() {
        String name = "Test Guideline";
        String link = "https://example.com/guideline";

        GuidelineReport report = new GuidelineReport(name, link, sampleResults);

        assertEquals(name, report.getName());
        assertEquals(link, report.getLink());
        assertEquals(6, report.getResults().size());

        // Verify it creates a defensive copy
        sampleResults.clear();
        assertEquals(6, report.getResults().size());
    }

    @Test
    void testSettersAndGetters() {
        GuidelineReport report =
                new GuidelineReport("Initial", "https://initial.com", new ArrayList<>());

        report.setName("Updated Name");
        report.setLink("https://updated.com");

        assertEquals("Updated Name", report.getName());
        assertEquals("https://updated.com", report.getLink());
    }

    @Test
    void testAddResult() {
        GuidelineReport report = new GuidelineReport("Test", "https://test.com", new ArrayList<>());

        assertEquals(0, report.getResults().size());

        report.addResult(new FailedCheckGuidelineResult("NewCheck", GuidelineAdherence.ADHERED));
        assertEquals(1, report.getResults().size());
        assertEquals("NewCheck", report.getResults().get(0).getCheckName());
    }

    @Test
    void testGetResultsReturnsUnmodifiableList() {
        GuidelineReport report = new GuidelineReport("Test", "https://test.com", sampleResults);

        List<GuidelineCheckResult> results = report.getResults();
        // Verify it returns an unmodifiable list
        assertThrows(
                UnsupportedOperationException.class,
                () ->
                        results.add(
                                new FailedCheckGuidelineResult("New", GuidelineAdherence.ADHERED)));
    }

    @Test
    void testGetAdhered() {
        GuidelineReport report = new GuidelineReport("Test", "https://test.com", sampleResults);

        List<GuidelineCheckResult> adhered = report.getAdhered();
        assertEquals(2, adhered.size());
        assertTrue(adhered.stream().allMatch(r -> r.getAdherence() == GuidelineAdherence.ADHERED));
        // Verify it returns an unmodifiable list
        assertThrows(
                UnsupportedOperationException.class,
                () ->
                        adhered.add(
                                new FailedCheckGuidelineResult("New", GuidelineAdherence.ADHERED)));
    }

    @Test
    void testGetViolated() {
        GuidelineReport report = new GuidelineReport("Test", "https://test.com", sampleResults);

        List<GuidelineCheckResult> violated = report.getViolated();
        assertEquals(2, violated.size());
        assertTrue(
                violated.stream().allMatch(r -> r.getAdherence() == GuidelineAdherence.VIOLATED));
        // Verify it returns an unmodifiable list
        assertThrows(
                UnsupportedOperationException.class,
                () ->
                        violated.add(
                                new FailedCheckGuidelineResult(
                                        "New", GuidelineAdherence.VIOLATED)));
    }

    @Test
    void testGetConditionNotMet() {
        GuidelineReport report = new GuidelineReport("Test", "https://test.com", sampleResults);

        List<GuidelineCheckResult> conditionNotMet = report.getConditionNotMet();
        assertEquals(1, conditionNotMet.size());
        assertTrue(
                conditionNotMet.stream()
                        .allMatch(r -> r.getAdherence() == GuidelineAdherence.CONDITION_NOT_MET));
        // Verify it returns an unmodifiable list
        assertThrows(
                UnsupportedOperationException.class,
                () ->
                        conditionNotMet.add(
                                new MissingRequirementGuidelineResult(
                                        "New", GuidelineAdherence.CONDITION_NOT_MET)));
    }

    @Test
    void testGetFailedChecks() {
        GuidelineReport report = new GuidelineReport("Test", "https://test.com", sampleResults);

        List<GuidelineCheckResult> failed = report.getFailedChecks();
        assertEquals(1, failed.size());
        assertTrue(
                failed.stream().allMatch(r -> r.getAdherence() == GuidelineAdherence.CHECK_FAILED));
        assertThrows(
                UnsupportedOperationException.class,
                () ->
                        failed.add(
                                new FailedCheckGuidelineResult(
                                        "New", GuidelineAdherence.CHECK_FAILED)));
    }

    @Test
    void testEmptyFilteredLists() {
        GuidelineReport report = new GuidelineReport("Test", "https://test.com", new ArrayList<>());

        assertEquals(0, report.getAdhered().size());
        assertEquals(0, report.getViolated().size());
        assertEquals(0, report.getConditionNotMet().size());
        assertEquals(0, report.getFailedChecks().size());
    }

    @Test
    void testDefaultConstructorUsedInReflection() throws Exception {
        // Test that default constructor works via reflection (used by deserialization)
        Class<?> clazz = GuidelineReport.class;
        Constructor<?> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object instance = constructor.newInstance();

        assertNotNull(instance);
        GuidelineReport report = (GuidelineReport) instance;
        assertNull(report.getName());
        assertNull(report.getLink());
        assertNotNull(report.getResults());
        assertEquals(0, report.getResults().size());
    }
}
