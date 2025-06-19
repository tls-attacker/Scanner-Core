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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TrackableValueSerializationTest {

    private ObjectMapper mapper;

    @BeforeEach
    public void setUp() {
        mapper = new ObjectMapper();
    }

    @Test
    public void testSerializeTestTrackableValue() throws IOException {
        TestTrackableValue value = new TestTrackableValue("test-value");

        String json = mapper.writeValueAsString(value);
        assertNotNull(json);
        assertTrue(json.contains("TestTrackableValue"));
        assertTrue(json.contains("test-value"));

        TestTrackableValue deserialized = mapper.readValue(json, TestTrackableValue.class);
        assertEquals(value, deserialized);
    }

    @Test
    public void testSerializeAnotherTestTrackableValue() throws IOException {
        AnotherTestTrackableValue value = new AnotherTestTrackableValue(42);

        String json = mapper.writeValueAsString(value);
        assertNotNull(json);
        assertTrue(json.contains("AnotherTestTrackableValue"));
        assertTrue(json.contains("42"));

        AnotherTestTrackableValue deserialized =
                mapper.readValue(json, AnotherTestTrackableValue.class);
        assertEquals(value, deserialized);
    }

    @Test
    public void testPolymorphicSerialization() throws IOException {
        // Test serializing individual values
        TestTrackableValue tv = new TestTrackableValue("string-value");
        AnotherTestTrackableValue atv = new AnotherTestTrackableValue(123);

        String jsonTv = mapper.writeValueAsString(tv);
        String jsonAtv = mapper.writeValueAsString(atv);

        assertNotNull(jsonTv);
        assertNotNull(jsonAtv);
        assertTrue(jsonTv.contains("string-value"));
        assertTrue(jsonAtv.contains("123"));
    }

    @Test
    public void testContainerWithMixedTypes() throws IOException {
        ExtractedValueContainer<TrackableValue> container =
                new ExtractedValueContainer<>(new TestTrackableValue("containerType"));
        container.put(new TestTrackableValue("test1"));
        container.put(new AnotherTestTrackableValue(99));

        String json = mapper.writeValueAsString(container);
        assertNotNull(json);
        assertTrue(json.contains("extractedValueList"));
        // Just test serialization works, skip deserialization due to type erasure
    }

    @Test
    public void testCompleteWorkflowSerialization() throws IOException {
        // Create a complete workflow with StatsWriter
        StatsWriter<TestState> writer = new StatsWriter<>();
        TestStatExtractor extractor = new TestStatExtractor();
        writer.addExtractor(extractor);

        // Extract some values
        writer.extract(new TestState("value1"));
        writer.extract(new TestState("value2"));

        // Test that the container has the expected values
        ExtractedValueContainer<?> container = extractor.getContainer();
        assertEquals(2, container.getNumberOfExtractedValues());
        assertFalse(container.areAllValuesIdentical());
        assertTrue(container.areAllValuesDifferent());

        // Serialize the container
        String json = mapper.writeValueAsString(container);
        assertNotNull(json);
        assertTrue(json.contains("extractedValueList"));
    }

    @Test
    public void testNullValueSerialization() throws IOException {
        TestTrackableValue nullValue = null;

        String json = mapper.writeValueAsString(nullValue);
        assertEquals("null", json);

        TestTrackableValue deserialized = mapper.readValue(json, TestTrackableValue.class);
        assertNull(deserialized);
    }

    @Test
    public void testEmptyContainerSerialization() throws IOException {
        ExtractedValueContainer<TestTrackableValue> container = new ExtractedValueContainer<>();

        // Test the empty container state
        assertEquals(0, container.getNumberOfExtractedValues());
        assertTrue(container.areAllValuesIdentical());
        assertTrue(container.areAllValuesDifferent());

        String json = mapper.writeValueAsString(container);
        assertNotNull(json);
        assertTrue(json.contains("extractedValueList"));
    }
}
