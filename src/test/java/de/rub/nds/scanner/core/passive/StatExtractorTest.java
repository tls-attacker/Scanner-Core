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

public class StatExtractorTest {

    private TestStatExtractor extractor;
    private TestState state1;
    private TestState state2;

    @BeforeEach
    public void setUp() {
        extractor = new TestStatExtractor();
        state1 = new TestState("value1");
        state2 = new TestState("value2");
    }

    @Test
    public void testConstructor() {
        assertNotNull(extractor.getContainer());
        assertNotNull(extractor.getValueType());
        assertEquals(0, extractor.getContainer().getNumberOfExtractedValues());
    }

    @Test
    public void testGetValueType() {
        TrackableValue valueType = extractor.getValueType();
        assertNotNull(valueType);
        assertTrue(valueType instanceof TestTrackableValue);
    }

    @Test
    public void testPut() {
        TestTrackableValue value = new TestTrackableValue("test");
        extractor.put(value);

        assertEquals(1, extractor.getContainer().getNumberOfExtractedValues());
        assertEquals(value, extractor.getContainer().getExtractedValueList().get(0));
    }

    @Test
    public void testPutNull() {
        extractor.put(null);

        assertEquals(1, extractor.getContainer().getNumberOfExtractedValues());
        assertNull(extractor.getContainer().getExtractedValueList().get(0));
    }

    @Test
    public void testExtract() {
        extractor.extract(state1);
        extractor.extract(state2);

        List<TestTrackableValue> values = extractor.getContainer().getExtractedValueList();
        assertEquals(2, values.size());
        assertEquals("value1", values.get(0).getValue());
        assertEquals("value2", values.get(1).getValue());
    }

    @Test
    public void testExtractWithNullState() {
        extractor.extract(null);
        assertEquals(0, extractor.getContainer().getNumberOfExtractedValues());
    }

    @Test
    public void testExtractWithNullValue() {
        TestState nullValueState = new TestState(null);
        extractor.extract(nullValueState);
        assertEquals(0, extractor.getContainer().getNumberOfExtractedValues());
    }

    @Test
    public void testExtractNullValue() {
        extractor.setShouldExtractNull(true);
        extractor.extract(state1);

        assertEquals(1, extractor.getContainer().getNumberOfExtractedValues());
        assertNull(extractor.getContainer().getExtractedValueList().get(0));
    }

    @Test
    public void testGetContainer() {
        ExtractedValueContainer<TestTrackableValue> container = extractor.getContainer();
        assertNotNull(container);

        // Verify it's the same container
        extractor.put(new TestTrackableValue("test"));
        assertEquals(1, container.getNumberOfExtractedValues());
    }

    @Test
    public void testMultipleExtractions() {
        // Test multiple extractions maintain order
        for (int i = 0; i < 5; i++) {
            extractor.extract(new TestState("value" + i));
        }

        List<TestTrackableValue> values = extractor.getContainer().getExtractedValueList();
        assertEquals(5, values.size());
        for (int i = 0; i < 5; i++) {
            assertEquals("value" + i, values.get(i).getValue());
        }
    }
}
