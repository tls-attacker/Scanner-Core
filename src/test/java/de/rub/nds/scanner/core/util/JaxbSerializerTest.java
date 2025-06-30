/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.util;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.Set;
import org.junit.jupiter.api.Test;

class JaxbSerializerTest {

    @XmlRootElement
    static class ValidTestClass {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    // Interface which cannot be instantiated by JAXB
    interface InvalidTestClass {
        String getValue();
    }

    static class TestSerializer extends JaxbSerializer<ValidTestClass> {
        public TestSerializer(Set<Class<?>> classes) {
            super(classes);
        }
    }

    @Test
    void testConstructorWithValidClass() {
        assertDoesNotThrow(() -> new TestSerializer(Set.of(ValidTestClass.class)));
    }

    @Test
    void testConstructorWithInvalidClassThrowsIllegalStateException() {
        // This should throw IllegalStateException instead of JAXBException
        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> new TestSerializer(Set.of(InvalidTestClass.class)));

        assertEquals("Failed to create JAXB context", exception.getMessage());
        assertNotNull(exception.getCause());
    }

    @Test
    void testConstructorWithMultipleClasses() {
        // Test with multiple valid classes
        assertDoesNotThrow(() -> new TestSerializer(Set.of(ValidTestClass.class, String.class)));
    }

    @Test
    void testConstructorWithEmptyClassSet() {
        // Empty set should still work
        assertDoesNotThrow(() -> new TestSerializer(Set.of()));
    }
}
