/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.probe.result;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import de.rub.nds.scanner.core.probe.AnalyzedProperty;
import de.rub.nds.scanner.core.probe.AnalyzedPropertyCategory;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ListResultTest {

    @Test
    void testConstructor() {
        AnalyzedProperty property = new TestAnalyzedProperty();
        List<String> testList = new ArrayList<>();
        testList.add("test1");
        testList.add("test2");

        ListResult<String> result = new ListResult<>(property, testList);

        assertNotNull(result);
        assertEquals(property, result.getProperty());
        assertNotNull(result.getList());
        assertEquals(2, result.getList().size());
        assertEquals("test1", result.getList().get(0));
        assertEquals("test2", result.getList().get(1));
    }

    @Test
    void testGetListReturnsSameInstance() {
        AnalyzedProperty property = new TestAnalyzedProperty();
        List<Integer> testList = new ArrayList<>();
        testList.add(1);
        testList.add(2);

        ListResult<Integer> result = new ListResult<>(property, testList);

        List<Integer> returnedList = result.getList();
        assertSame(testList, returnedList);
    }

    @Test
    void testGetCollectionReturnsCorrectCollection() {
        AnalyzedProperty property = new TestAnalyzedProperty();
        List<String> testList = new ArrayList<>();
        testList.add("a");
        testList.add("b");

        ListResult<String> result = new ListResult<>(property, testList);

        assertEquals(testList, result.getCollection());
    }

    @Test
    void testEmptyList() {
        AnalyzedProperty property = new TestAnalyzedProperty();
        List<String> emptyList = new ArrayList<>();

        ListResult<String> result = new ListResult<>(property, emptyList);

        assertNotNull(result.getList());
        assertEquals(0, result.getList().size());
    }

    @Test
    void testNullList() {
        AnalyzedProperty property = new TestAnalyzedProperty();

        ListResult<String> result = new ListResult<>(property, null);

        assertNull(result.getList());
        assertNull(result.getCollection());
    }

    // Test implementation of AnalyzedProperty for testing
    private static class TestAnalyzedProperty implements AnalyzedProperty {
        @Override
        public String getName() {
            return "TestProperty";
        }

        @Override
        public AnalyzedPropertyCategory getCategory() {
            return new TestAnalyzedPropertyCategory();
        }
    }

    // Test implementation of AnalyzedPropertyCategory for testing
    private static class TestAnalyzedPropertyCategory implements AnalyzedPropertyCategory {}
}
