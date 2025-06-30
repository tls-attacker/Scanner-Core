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
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class GuidelineSerializationTest {

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

    private static class SerializableGuidelineCheck extends GuidelineCheck<TestScanReport>
            implements Serializable {
        public SerializableGuidelineCheck(String name) {
            super(name, RequirementLevel.MUST);
        }

        @Override
        public GuidelineCheckResult evaluate(TestScanReport report) {
            return new FailedCheckGuidelineResult(getName(), GuidelineAdherence.ADHERED);
        }
    }

    @Test
    void testGuidelineSerializationWithTransientChecks()
            throws IOException, ClassNotFoundException {
        // Create a guideline with checks
        String name = "Test Guideline";
        String link = "https://example.com/guideline";
        List<GuidelineCheck<TestScanReport>> checks = new ArrayList<>();
        checks.add(new SerializableGuidelineCheck("Check1"));
        checks.add(new SerializableGuidelineCheck("Check2"));

        Guideline<TestScanReport> originalGuideline = new Guideline<>(name, link, checks);

        // Serialize the guideline
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(originalGuideline);
        oos.close();

        // Deserialize the guideline
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        @SuppressWarnings("unchecked")
        Guideline<TestScanReport> deserializedGuideline =
                (Guideline<TestScanReport>) ois.readObject();
        ois.close();

        // Verify that name and link are preserved
        assertEquals(name, deserializedGuideline.getName());
        assertEquals(link, deserializedGuideline.getLink());

        // Verify that checks list is empty after deserialization (as it's transient)
        assertNotNull(deserializedGuideline.getChecks());
        assertTrue(deserializedGuideline.getChecks().isEmpty());
    }

    @Test
    void testGuidelineSerializationWithoutChecks() throws IOException, ClassNotFoundException {
        // Create a guideline without checks
        String name = "Empty Guideline";
        String link = "https://example.com/empty";

        Guideline<TestScanReport> originalGuideline =
                new Guideline<>(name, link, new ArrayList<>());

        // Serialize the guideline
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(originalGuideline);
        oos.close();

        // Deserialize the guideline
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        @SuppressWarnings("unchecked")
        Guideline<TestScanReport> deserializedGuideline =
                (Guideline<TestScanReport>) ois.readObject();
        ois.close();

        // Verify that name and link are preserved
        assertEquals(name, deserializedGuideline.getName());
        assertEquals(link, deserializedGuideline.getLink());

        // Verify that checks list is empty
        assertNotNull(deserializedGuideline.getChecks());
        assertTrue(deserializedGuideline.getChecks().isEmpty());
    }
}
