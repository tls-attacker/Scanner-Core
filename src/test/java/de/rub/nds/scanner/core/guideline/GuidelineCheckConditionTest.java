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

import de.rub.nds.scanner.core.probe.AnalyzedProperty;
import de.rub.nds.scanner.core.probe.result.TestResult;
import de.rub.nds.scanner.core.probe.result.TestResults;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class GuidelineCheckConditionTest {

    // Mock implementations for testing
    private static class TestAnalyzedProperty implements AnalyzedProperty {
        private final String name;

        public TestAnalyzedProperty(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    @Test
    void testConstructorWithAnalyzedPropertyAndResult() {
        AnalyzedProperty property = new TestAnalyzedProperty("TestProperty");
        TestResult result = TestResults.TRUE;
        
        GuidelineCheckCondition condition = new GuidelineCheckCondition(property, result);
        
        assertEquals(property, condition.getAnalyzedProperty());
        assertEquals(result, condition.getResult());
        assertNull(condition.getAnd());
        assertNull(condition.getOr());
    }

    @Test
    void testSetAnalyzedProperty() {
        GuidelineCheckCondition condition = new GuidelineCheckCondition(null, TestResults.FALSE);
        AnalyzedProperty property = new TestAnalyzedProperty("NewProperty");
        
        condition.setAnalyzedProperty(property);
        
        assertEquals(property, condition.getAnalyzedProperty());
    }

    @Test
    void testStaticAndFactory() {
        List<GuidelineCheckCondition> conditions = Arrays.asList(
            new GuidelineCheckCondition(new TestAnalyzedProperty("Prop1"), TestResults.TRUE),
            new GuidelineCheckCondition(new TestAnalyzedProperty("Prop2"), TestResults.FALSE)
        );
        
        GuidelineCheckCondition andCondition = GuidelineCheckCondition.and(conditions);
        
        assertNotNull(andCondition.getAnd());
        assertEquals(2, andCondition.getAnd().size());
        assertNull(andCondition.getOr());
        assertNull(andCondition.getAnalyzedProperty());
        assertNull(andCondition.getResult());
    }

    @Test
    void testStaticOrFactory() {
        List<GuidelineCheckCondition> conditions = Arrays.asList(
            new GuidelineCheckCondition(new TestAnalyzedProperty("Prop1"), TestResults.TRUE),
            new GuidelineCheckCondition(new TestAnalyzedProperty("Prop2"), TestResults.FALSE),
            new GuidelineCheckCondition(new TestAnalyzedProperty("Prop3"), TestResults.NOT_TESTED_YET)
        );
        
        GuidelineCheckCondition orCondition = GuidelineCheckCondition.or(conditions);
        
        assertNotNull(orCondition.getOr());
        assertEquals(3, orCondition.getOr().size());
        assertNull(orCondition.getAnd());
        assertNull(orCondition.getAnalyzedProperty());
        assertNull(orCondition.getResult());
    }

    @Test
    void testGetAndReturnsUnmodifiableList() {
        List<GuidelineCheckCondition> conditions = new ArrayList<>();
        conditions.add(new GuidelineCheckCondition(new TestAnalyzedProperty("Prop1"), TestResults.TRUE));
        
        GuidelineCheckCondition andCondition = GuidelineCheckCondition.and(conditions);
        
        List<GuidelineCheckCondition> andList = andCondition.getAnd();
        assertThrows(UnsupportedOperationException.class, () -> 
            andList.add(new GuidelineCheckCondition(new TestAnalyzedProperty("New"), TestResults.FALSE))
        );
    }

    @Test
    void testGetOrReturnsUnmodifiableList() {
        List<GuidelineCheckCondition> conditions = new ArrayList<>();
        conditions.add(new GuidelineCheckCondition(new TestAnalyzedProperty("Prop1"), TestResults.TRUE));
        
        GuidelineCheckCondition orCondition = GuidelineCheckCondition.or(conditions);
        
        List<GuidelineCheckCondition> orList = orCondition.getOr();
        assertThrows(UnsupportedOperationException.class, () -> 
            orList.add(new GuidelineCheckCondition(new TestAnalyzedProperty("New"), TestResults.FALSE))
        );
    }

    @Test
    void testGetAndWithNullList() {
        GuidelineCheckCondition condition = new GuidelineCheckCondition(new TestAnalyzedProperty("Prop"), TestResults.TRUE);
        
        assertNull(condition.getAnd());
    }

    @Test
    void testGetOrWithNullList() {
        GuidelineCheckCondition condition = new GuidelineCheckCondition(new TestAnalyzedProperty("Prop"), TestResults.TRUE);
        
        assertNull(condition.getOr());
    }

    @Test
    void testDefaultConstructorUsedInReflection() throws Exception {
        // Test that default constructor works via reflection (used by JAXB)
        Class<?> clazz = GuidelineCheckCondition.class;
        java.lang.reflect.Constructor<?> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object instance = constructor.newInstance();
        
        assertNotNull(instance);
        GuidelineCheckCondition condition = (GuidelineCheckCondition) instance;
        assertNull(condition.getAnalyzedProperty());
        assertNull(condition.getResult());
        assertNull(condition.getAnd());
        assertNull(condition.getOr());
    }

    @Test
    void testXmlAnnotations() {
        Class<?> clazz = GuidelineCheckCondition.class;
        
        // Verify XML annotations
        assertNotNull(clazz.getAnnotation(XmlRootElement.class));
        XmlAccessorType accessorType = clazz.getAnnotation(XmlAccessorType.class);
        assertNotNull(accessorType);
        assertEquals(XmlAccessType.FIELD, accessorType.value());
    }

    @Test
    void testNestedAndOrConditions() {
        // Create nested conditions to test complex scenarios
        GuidelineCheckCondition innerAnd = GuidelineCheckCondition.and(Arrays.asList(
            new GuidelineCheckCondition(new TestAnalyzedProperty("A"), TestResults.TRUE),
            new GuidelineCheckCondition(new TestAnalyzedProperty("B"), TestResults.TRUE)
        ));
        
        GuidelineCheckCondition innerOr = GuidelineCheckCondition.or(Arrays.asList(
            new GuidelineCheckCondition(new TestAnalyzedProperty("C"), TestResults.FALSE),
            new GuidelineCheckCondition(new TestAnalyzedProperty("D"), TestResults.TRUE)
        ));
        
        GuidelineCheckCondition complex = GuidelineCheckCondition.and(Arrays.asList(innerAnd, innerOr));
        
        assertNotNull(complex.getAnd());
        assertEquals(2, complex.getAnd().size());
        assertNotNull(complex.getAnd().get(0).getAnd());
        assertNotNull(complex.getAnd().get(1).getOr());
    }
}