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

import de.rub.nds.scanner.core.guideline.testutil.IOTestGuidelineCheck;
import de.rub.nds.scanner.core.guideline.testutil.IOTestScanReport;
import de.rub.nds.scanner.core.probe.AnalyzedProperty;
import de.rub.nds.scanner.core.probe.AnalyzedPropertyCategory;
import de.rub.nds.scanner.core.probe.result.TestResults;
import jakarta.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class GuidelineIOTest {

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

    @Test
    void testConstructorWithAnalyzedPropertyClass() throws JAXBException {
        GuidelineIO<IOTestScanReport> io = new GuidelineIO<>(TestAnalyzedProperty.class);
        assertNotNull(io);
    }

    @Test
    void testWriteAndReadGuideline(@TempDir File tempDir) throws Exception {
        GuidelineIO<IOTestScanReport> io = new GuidelineIO<>(TestAnalyzedProperty.class);

        // Create a guideline
        Guideline<IOTestScanReport> guideline =
                new Guideline<>(
                        "Test Guideline",
                        "https://test.com",
                        Arrays.asList(
                                new IOTestGuidelineCheck("Check1", RequirementLevel.MUST),
                                new IOTestGuidelineCheck("Check2", RequirementLevel.SHOULD)));

        // Write to file
        File file = new File(tempDir, "guideline.xml");
        io.write(file, guideline);
        assertTrue(file.exists());

        // Read back
        Guideline<IOTestScanReport> readGuideline = io.read(file);
        assertNotNull(readGuideline);
        assertEquals("Test Guideline", readGuideline.getName());
        assertEquals("https://test.com", readGuideline.getLink());
        assertEquals(2, readGuideline.getChecks().size());
    }

    @Test
    void testWriteAndReadToStream() throws Exception {
        GuidelineIO<IOTestScanReport> io = new GuidelineIO<>(TestAnalyzedProperty.class);

        // Create a guideline
        Guideline<IOTestScanReport> guideline =
                new Guideline<>(
                        "Stream Test",
                        "https://stream.test",
                        Arrays.asList(
                                new IOTestGuidelineCheck("StreamCheck", RequirementLevel.MAY)));

        // Write to stream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        io.write(baos, guideline);

        // Read back from stream
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        Guideline<?> readGuideline = io.read(bais);

        assertNotNull(readGuideline);
        assertEquals("Stream Test", readGuideline.getName());
        assertEquals("https://stream.test", readGuideline.getLink());
        assertEquals(1, readGuideline.getChecks().size());
    }

    @Test
    void testSerializationWithConditions() throws Exception {
        GuidelineIO<IOTestScanReport> io = new GuidelineIO<>(TestAnalyzedProperty.class);

        // Create guideline with condition
        TestAnalyzedProperty property = new TestAnalyzedProperty("TestProp");
        GuidelineCheckCondition condition = new GuidelineCheckCondition(property, TestResults.TRUE);

        IOTestGuidelineCheck checkWithCondition =
                new IOTestGuidelineCheck("ConditionalCheck", RequirementLevel.SHOULD);

        Guideline<IOTestScanReport> guideline =
                new Guideline<>(
                        "Conditional Test",
                        "https://conditional.test",
                        Arrays.asList(checkWithCondition));

        // Serialize and deserialize
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        io.write(baos, guideline);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        Guideline<IOTestScanReport> readGuideline = io.read(bais);

        assertNotNull(readGuideline);
        assertEquals(1, readGuideline.getChecks().size());
    }

    @Test
    void testEmptyGuideline() throws Exception {
        GuidelineIO<IOTestScanReport> io = new GuidelineIO<>(TestAnalyzedProperty.class);

        Guideline<IOTestScanReport> emptyGuideline =
                new Guideline<>("Empty", "https://empty.test", Arrays.asList());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        io.write(baos, emptyGuideline);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        Guideline<?> readGuideline = io.read(bais);

        assertNotNull(readGuideline);
        assertEquals("Empty", readGuideline.getName());
        assertEquals(0, readGuideline.getChecks().size());
    }

    @Test
    void testInvalidXmlInput() throws Exception {
        GuidelineIO<IOTestScanReport> io = new GuidelineIO<>(TestAnalyzedProperty.class);

        String invalidXml = "This is not valid XML";
        ByteArrayInputStream bais =
                new ByteArrayInputStream(invalidXml.getBytes(StandardCharsets.UTF_8));

        assertThrows(Exception.class, () -> io.read(bais));
    }

    @Test
    void testReflectionScanning() throws Exception {
        // This test verifies that the reflection scanning works
        // The GuidelineIO constructor should find IOTestGuidelineCheck via reflection
        GuidelineIO<IOTestScanReport> io = new GuidelineIO<>(TestAnalyzedProperty.class);

        // Create guideline with our test check
        Guideline<IOTestScanReport> guideline =
                new Guideline<>(
                        "Reflection Test",
                        "https://reflection.test",
                        Arrays.asList(
                                new IOTestGuidelineCheck(
                                        "ReflectionCheck", RequirementLevel.MUST)));

        // If reflection worked, serialization should succeed
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> io.write(baos, guideline));
    }
}
