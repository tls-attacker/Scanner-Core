/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.passive;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StatsWriterTest {

    private StatsWriter<TestState> statsWriter;
    private TestStatExtractor extractor1;
    private TestStatExtractor extractor2;
    private TestState state1;
    private TestState state2;

    @BeforeEach
    public void setUp() {
        statsWriter = new StatsWriter<>();
        extractor1 = new TestStatExtractor();
        extractor2 = new TestStatExtractor();
        state1 = new TestState("value1");
        state2 = new TestState("value2");
    }

    @Test
    public void testConstructor() {
        assertNotNull(statsWriter);
        assertEquals(0, statsWriter.getStateCounter());
        assertTrue(statsWriter.getCumulatedExtractedValues().isEmpty());
    }

    @Test
    public void testAddExtractor() {
        statsWriter.addExtractor(extractor1);
        assertEquals(0, statsWriter.getStateCounter());

        // Extract a state to verify the extractor was added
        statsWriter.extract(state1);
        assertEquals(1, statsWriter.getStateCounter());
        assertEquals(1, extractor1.getContainer().getNumberOfExtractedValues());
    }

    @Test
    public void testAddMultipleExtractors() {
        statsWriter.addExtractor(extractor1);
        statsWriter.addExtractor(extractor2);

        statsWriter.extract(state1);
        assertEquals(1, extractor1.getContainer().getNumberOfExtractedValues());
        assertEquals(1, extractor2.getContainer().getNumberOfExtractedValues());
    }

    @Test
    public void testExtractWithNoExtractors() {
        // Should not throw exception
        statsWriter.extract(state1);
        assertEquals(1, statsWriter.getStateCounter());
    }

    @Test
    public void testExtractWithNullState() {
        statsWriter.addExtractor(extractor1);
        statsWriter.extract(null);

        assertEquals(1, statsWriter.getStateCounter());
        assertEquals(0, extractor1.getContainer().getNumberOfExtractedValues());
    }

    @Test
    public void testExtractMultipleStates() {
        statsWriter.addExtractor(extractor1);

        statsWriter.extract(state1);
        statsWriter.extract(state2);

        assertEquals(2, statsWriter.getStateCounter());
        List<TestTrackableValue> values = extractor1.getContainer().getExtractedValueList();
        assertEquals(2, values.size());
        assertEquals("value1", values.get(0).getValue());
        assertEquals("value2", values.get(1).getValue());
    }

    @Test
    public void testGetCumulatedExtractedValues() {
        statsWriter.addExtractor(extractor1);
        statsWriter.addExtractor(extractor2);

        List<ExtractedValueContainer<?>> containers = statsWriter.getCumulatedExtractedValues();
        assertEquals(2, containers.size());
        assertTrue(containers.contains(extractor1.getContainer()));
        assertTrue(containers.contains(extractor2.getContainer()));
    }

    @Test
    public void testGetCumulatedExtractedValuesAfterExtraction() {
        statsWriter.addExtractor(extractor1);
        statsWriter.addExtractor(extractor2);

        statsWriter.extract(state1);
        statsWriter.extract(state2);

        List<ExtractedValueContainer<?>> containers = statsWriter.getCumulatedExtractedValues();
        assertEquals(2, containers.size());

        for (ExtractedValueContainer<?> container : containers) {
            assertEquals(2, container.getNumberOfExtractedValues());
        }
    }

    @Test
    public void testGetStateCounter() {
        assertEquals(0, statsWriter.getStateCounter());

        statsWriter.extract(state1);
        assertEquals(1, statsWriter.getStateCounter());

        statsWriter.extract(state2);
        assertEquals(2, statsWriter.getStateCounter());

        statsWriter.extract(null);
        assertEquals(3, statsWriter.getStateCounter());
    }

    @Test
    public void testExtractorOrderPreserved() {
        TestStatExtractor extractor3 = new TestStatExtractor();

        statsWriter.addExtractor(extractor1);
        statsWriter.addExtractor(extractor2);
        statsWriter.addExtractor(extractor3);

        List<ExtractedValueContainer<?>> containers = statsWriter.getCumulatedExtractedValues();
        assertEquals(3, containers.size());
        assertEquals(extractor1.getContainer(), containers.get(0));
        assertEquals(extractor2.getContainer(), containers.get(1));
        assertEquals(extractor3.getContainer(), containers.get(2));
    }

    @Test
    public void testComplexScenario() {
        // Create different types of extractors
        TestStatExtractor normalExtractor = new TestStatExtractor();
        TestStatExtractor nullExtractor = new TestStatExtractor();
        nullExtractor.setShouldExtractNull(true);

        statsWriter.addExtractor(normalExtractor);
        statsWriter.addExtractor(nullExtractor);

        // Extract multiple states
        for (int i = 0; i < 10; i++) {
            statsWriter.extract(new TestState("value" + i));
        }

        assertEquals(10, statsWriter.getStateCounter());
        assertEquals(10, normalExtractor.getContainer().getNumberOfExtractedValues());
        assertEquals(10, nullExtractor.getContainer().getNumberOfExtractedValues());

        // Verify normal extractor has all different values
        assertTrue(normalExtractor.getContainer().areAllValuesDifferent());

        // Verify null extractor has all identical (null) values
        // First value is null, then compare the rest
        boolean allNull = true;
        for (Object value : nullExtractor.getContainer().getExtractedValueList()) {
            if (value != null) {
                allNull = false;
                break;
            }
        }
        assertTrue(allNull);
    }
}
