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

import de.rub.nds.scanner.core.report.ScanReport;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class GuidelineTest {

    // Mock implementation for testing
    private static class TestScanReport extends ScanReport {
        @Override
        public void serializeToJson(java.io.OutputStream outputStream) {
            // Test implementation - do nothing
        }

        @Override
        public String getRemoteName() {
            return "TestRemote";
        }
    }

    private static class TestGuidelineCheck extends GuidelineCheck {
        public TestGuidelineCheck(String name) {
            super(name, RequirementLevel.MUST);
        }

        @Override
        public <ReportT extends ScanReport> GuidelineCheckResult evaluate(ReportT report) {
            return new FailedCheckGuidelineResult(getName(), GuidelineAdherence.ADHERED);
        }
    }

    @Test
    void testConstructorWithParameters() {
        String name = "Test Guideline";
        String link = "https://example.com/guideline";
        List<GuidelineCheck> checks = new ArrayList<>();
        checks.add(new TestGuidelineCheck("Check1"));
        checks.add(new TestGuidelineCheck("Check2"));

        Guideline guideline = new Guideline(name, link, checks);

        assertEquals(name, guideline.getName());
        assertEquals(link, guideline.getLink());
        assertEquals(2, guideline.getChecks().size());

        // Verify defensive copy
        checks.clear();
        assertEquals(2, guideline.getChecks().size());
    }

    @Test
    void testSettersAndGetters() {
        Guideline guideline = new Guideline("Initial", "https://initial.com", new ArrayList<>());

        guideline.setName("Updated Name");
        guideline.setLink("https://updated.com");

        assertEquals("Updated Name", guideline.getName());
        assertEquals("https://updated.com", guideline.getLink());
    }

    @Test
    void testAddCheck() {
        Guideline guideline = new Guideline("Test", "https://test.com", new ArrayList<>());

        assertEquals(0, guideline.getChecks().size());

        guideline.addCheck(new TestGuidelineCheck("NewCheck"));
        assertEquals(1, guideline.getChecks().size());
        assertEquals("NewCheck", guideline.getChecks().get(0).getName());
    }

    @Test
    void testGetChecksReturnsUnmodifiableList() {
        List<GuidelineCheck> checks = Arrays.asList(new TestGuidelineCheck("Check1"));
        Guideline guideline = new Guideline("Test", "https://test.com", checks);

        List<GuidelineCheck> returnedChecks = guideline.getChecks();
        assertThrows(
                UnsupportedOperationException.class,
                () -> returnedChecks.add(new TestGuidelineCheck("NewCheck")));
    }

    @Test
    void testImplementsSerializable() {
        Guideline guideline = new Guideline("Test", "https://test.com", new ArrayList<>());
        assertTrue(guideline instanceof Serializable);
    }

    @Test
    void testDefaultConstructorUsedInReflection() throws Exception {
        // Test that default constructor works via reflection (used by JAXB)
        Class<?> clazz = Guideline.class;
        Constructor<?> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object instance = constructor.newInstance();

        assertNotNull(instance);
        Guideline guideline = (Guideline) instance;
        assertNull(guideline.getName());
        assertNull(guideline.getLink());
        // Note: checks field will be null after default constructor
    }
}
