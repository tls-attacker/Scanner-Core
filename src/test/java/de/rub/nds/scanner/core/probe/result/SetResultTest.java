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
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.rub.nds.scanner.core.probe.AnalyzedProperty;
import de.rub.nds.scanner.core.probe.AnalyzedPropertyCategory;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SetResultTest {

    @Test
    void testConstructor() {
        AnalyzedProperty property = new TestAnalyzedProperty();
        Set<String> testSet = new HashSet<>();
        testSet.add("test1");
        testSet.add("test2");

        SetResult<String> result = new SetResult<>(property, testSet);

        assertNotNull(result);
        assertEquals(property, result.getProperty());
        assertNotNull(result.getSet());
        assertEquals(2, result.getSet().size());
        assertTrue(result.getSet().contains("test1"));
        assertTrue(result.getSet().contains("test2"));
    }

    @Test
    void testGetSetReturnsSameInstance() {
        AnalyzedProperty property = new TestAnalyzedProperty();
        Set<Integer> testSet = new HashSet<>();
        testSet.add(1);
        testSet.add(2);

        SetResult<Integer> result = new SetResult<>(property, testSet);

        Set<Integer> returnedSet = result.getSet();
        assertSame(testSet, returnedSet);
    }

    @Test
    void testGetCollectionReturnsCorrectCollection() {
        AnalyzedProperty property = new TestAnalyzedProperty();
        Set<String> testSet = new HashSet<>();
        testSet.add("a");
        testSet.add("b");

        SetResult<String> result = new SetResult<>(property, testSet);

        assertEquals(testSet, result.getCollection());
    }

    @Test
    void testEmptySet() {
        AnalyzedProperty property = new TestAnalyzedProperty();
        Set<String> emptySet = new HashSet<>();

        SetResult<String> result = new SetResult<>(property, emptySet);

        assertNotNull(result.getSet());
        assertEquals(0, result.getSet().size());
    }

    @Test
    void testNullSet() {
        AnalyzedProperty property = new TestAnalyzedProperty();

        SetResult<String> result = new SetResult<>(property, null);

        assertNull(result.getSet());
        assertNull(result.getCollection());
    }

    @Test
    void testSetUniqueness() {
        AnalyzedProperty property = new TestAnalyzedProperty();
        Set<String> testSet = new HashSet<>();
        testSet.add("duplicate");
        testSet.add("duplicate");
        testSet.add("unique");

        SetResult<String> result = new SetResult<>(property, testSet);

        assertEquals(2, result.getSet().size());
        assertTrue(result.getSet().contains("duplicate"));
        assertTrue(result.getSet().contains("unique"));
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
