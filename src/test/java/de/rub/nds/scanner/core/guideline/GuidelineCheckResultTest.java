/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.guideline;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.junit.jupiter.api.Test;

class GuidelineCheckResultTest {

    // Concrete implementation for testing
    private static class ConcreteGuidelineCheckResult extends GuidelineCheckResult {
        public ConcreteGuidelineCheckResult(String checkName, GuidelineAdherence adherence) {
            super(checkName, adherence);
        }

        public ConcreteGuidelineCheckResult(
                String checkName, GuidelineAdherence adherence, String hint) {
            super(checkName, adherence, hint);
        }

        @SuppressWarnings("unused")
        private ConcreteGuidelineCheckResult() {
            super(null, null);
        }
    }

    @Test
    void testConstructorWithNameAndAdherence() {
        String checkName = "TestCheck";
        GuidelineAdherence adherence = GuidelineAdherence.ADHERED;

        ConcreteGuidelineCheckResult result =
                new ConcreteGuidelineCheckResult(checkName, adherence);

        assertEquals(checkName, result.getCheckName());
        assertEquals(adherence, result.getAdherence());
        assertNull(result.getHint());
    }

    @Test
    void testConstructorWithNameAdherenceAndHint() {
        String checkName = "TestCheck";
        GuidelineAdherence adherence = GuidelineAdherence.VIOLATED;
        String hint = "Test hint";

        ConcreteGuidelineCheckResult result =
                new ConcreteGuidelineCheckResult(checkName, adherence, hint);

        assertEquals(checkName, result.getCheckName());
        assertEquals(adherence, result.getAdherence());
        assertEquals(hint, result.getHint());
    }

    @Test
    void testSettersAndGetters() {
        ConcreteGuidelineCheckResult result =
                new ConcreteGuidelineCheckResult("InitialName", GuidelineAdherence.ADHERED);

        result.setCheckName("UpdatedName");
        result.setAdherence(GuidelineAdherence.VIOLATED);
        result.setHint("Updated hint");

        assertEquals("UpdatedName", result.getCheckName());
        assertEquals(GuidelineAdherence.VIOLATED, result.getAdherence());
        assertEquals("Updated hint", result.getHint());
    }

    @Test
    void testDefaultConstructorUsedInReflection() throws Exception {
        // Test that default constructor works via reflection (used by deserialization)
        Class<?> clazz = ConcreteGuidelineCheckResult.class;
        java.lang.reflect.Constructor<?> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object instance = constructor.newInstance();

        assertNotNull(instance);
        ConcreteGuidelineCheckResult result = (ConcreteGuidelineCheckResult) instance;
        assertNull(result.getCheckName());
        assertNull(result.getAdherence());
        assertNull(result.getHint());
    }

    @Test
    void testJsonTypeInfoAnnotation() {
        // Verify the class has the JsonTypeInfo annotation for polymorphic deserialization
        Class<?> clazz = GuidelineCheckResult.class;
        JsonTypeInfo annotation = clazz.getAnnotation(JsonTypeInfo.class);

        assertNotNull(annotation);
        assertEquals(JsonTypeInfo.Id.CLASS, annotation.use());
        assertEquals(JsonTypeInfo.As.PROPERTY, annotation.include());
        assertEquals("@class", annotation.property());
    }
}
