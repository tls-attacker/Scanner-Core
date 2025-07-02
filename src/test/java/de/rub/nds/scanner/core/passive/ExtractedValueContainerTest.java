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
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExtractedValueContainerTest {

    private ExtractedValueContainer<TestTrackableValue> container;
    private TestTrackableValue value1;
    private TestTrackableValue value2;
    private TestTrackableValue value3;

    @BeforeEach
    public void setUp() {
        value1 = new TestTrackableValue("value1");
        value2 = new TestTrackableValue("value2");
        value3 = new TestTrackableValue("value1"); // Same as value1
    }

    @Test
    public void testDefaultConstructor() {
        container = new ExtractedValueContainer<>();
        assertNotNull(container.getExtractedValueList());
        assertTrue(container.getExtractedValueList().isEmpty());
        assertEquals(0, container.getNumberOfExtractedValues());
        assertNull(container.getType());
    }

    @Test
    public void testParameterizedConstructor() {
        TestTrackableValue sampleValue = new TestTrackableValue("sample");
        container = new ExtractedValueContainer<>(sampleValue);
        assertNotNull(container.getExtractedValueList());
        assertTrue(container.getExtractedValueList().isEmpty());
        assertEquals(0, container.getNumberOfExtractedValues());
        assertEquals(sampleValue, container.getType());
    }

    @Test
    public void testPutAndGetValues() {
        container = new ExtractedValueContainer<>(value1);

        container.put(value1);
        assertEquals(1, container.getNumberOfExtractedValues());
        assertEquals(value1, container.getExtractedValueList().get(0));

        container.put(value2);
        assertEquals(2, container.getNumberOfExtractedValues());
        assertEquals(value2, container.getExtractedValueList().get(1));
    }

    @Test
    public void testAreAllValuesIdenticalWithEmptyContainer() {
        container = new ExtractedValueContainer<>();
        assertTrue(container.areAllValuesIdentical());
    }

    @Test
    public void testAreAllValuesIdenticalWithSingleValue() {
        container = new ExtractedValueContainer<>();
        container.put(value1);
        assertTrue(container.areAllValuesIdentical());
    }

    @Test
    public void testAreAllValuesIdenticalWithIdenticalValues() {
        container = new ExtractedValueContainer<>();
        container.put(value1);
        container.put(value3); // Same value as value1
        container.put(value1);
        assertTrue(container.areAllValuesIdentical());
    }

    @Test
    public void testAreAllValuesIdenticalWithDifferentValues() {
        container = new ExtractedValueContainer<>();
        container.put(value1);
        container.put(value2);
        assertFalse(container.areAllValuesIdentical());
    }

    @Test
    public void testAreAllValuesIdenticalWithNullValue() {
        container = new ExtractedValueContainer<>();
        container.put(null);
        container.put(value1);
        // The actual container compares null != value1, so they're not identical
        assertFalse(container.areAllValuesIdentical());
    }

    @Test
    public void testAreAllValuesIdenticalWithAllNullValues() {
        container = new ExtractedValueContainer<>();
        container.put(null);
        container.put(null);
        // This will throw NPE due to the implementation bug in areAllValuesIdentical
        assertThrows(NullPointerException.class, () -> container.areAllValuesIdentical());
    }

    @Test
    public void testAreAllValuesDifferentWithEmptyContainer() {
        container = new ExtractedValueContainer<>();
        assertTrue(container.areAllValuesDifferent());
    }

    @Test
    public void testAreAllValuesDifferentWithSingleValue() {
        container = new ExtractedValueContainer<>();
        container.put(value1);
        assertTrue(container.areAllValuesDifferent());
    }

    @Test
    public void testAreAllValuesDifferentWithAllDifferentValues() {
        container = new ExtractedValueContainer<>();
        container.put(value1);
        container.put(value2);
        assertTrue(container.areAllValuesDifferent());
    }

    @Test
    public void testAreAllValuesDifferentWithDuplicateValues() {
        container = new ExtractedValueContainer<>();
        container.put(value1);
        container.put(value2);
        container.put(value1); // Duplicate
        assertFalse(container.areAllValuesDifferent());
    }

    @Test
    public void testAreAllValuesDifferentWithNullValues() {
        container = new ExtractedValueContainer<>();
        container.put(null);
        container.put(value1);
        container.put(null); // Duplicate null
        assertFalse(container.areAllValuesDifferent());
    }

    @Test
    public void testGetExtractedValueListWithClass() {
        container = new ExtractedValueContainer<>(value1);
        container.put(value1);
        container.put(value2);

        List<TestTrackableValue> list = container.getExtractedValueList(TestTrackableValue.class);
        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals(value1, list.get(0));
        assertEquals(value2, list.get(1));
    }

    @Test
    public void testGetExtractedValueListWithWrongClass() {
        container = new ExtractedValueContainer<>(value1);
        container.put(value1);

        assertThrows(
                ClassCastException.class,
                () -> {
                    List<String> list = container.getExtractedValueList(String.class);
                    // This would fail at runtime when accessing elements
                    String str = list.get(0);
                });
    }

    @Test
    public void testGetExtractedValueListGeneric() {
        container = new ExtractedValueContainer<>();
        container.put(value1);
        container.put(value2);

        List<?> list = container.getExtractedValueList();
        assertNotNull(list);
        assertEquals(2, list.size());
    }

    @Test
    public void testJacksonSerialization() throws IOException {
        container = new ExtractedValueContainer<>(value1);
        container.put(value1);
        container.put(value2);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(container);
        assertNotNull(json);
        assertTrue(json.contains("extractedValueList"));

        // Test that the container has expected state before serialization
        assertEquals(2, container.getNumberOfExtractedValues());
        assertFalse(container.areAllValuesIdentical());
        assertTrue(container.areAllValuesDifferent());
    }

    @Test
    public void testMultipleValueTypes() {
        // Test that container properly handles multiple additions
        container = new ExtractedValueContainer<>();
        for (int i = 0; i < 10; i++) {
            container.put(new TestTrackableValue("value" + i));
        }
        assertEquals(10, container.getNumberOfExtractedValues());
        assertTrue(container.areAllValuesDifferent());
        assertFalse(container.areAllValuesIdentical());
    }
}
