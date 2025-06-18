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

import org.junit.jupiter.api.Test;

class FailedCheckGuidelineResultTest {

    @Test
    void testConstructorWithNameAndAdherence() {
        String checkName = "TestCheck";
        GuidelineAdherence adherence = GuidelineAdherence.CHECK_FAILED;

        FailedCheckGuidelineResult result = new FailedCheckGuidelineResult(checkName, adherence);

        assertEquals(checkName, result.getCheckName());
        assertEquals(adherence, result.getAdherence());
        assertNull(result.getHint());
    }

    @Test
    void testConstructorWithNameAdherenceAndHint() {
        String checkName = "TestCheck";
        GuidelineAdherence adherence = GuidelineAdherence.CHECK_FAILED;
        String hint = "This check failed due to an exception";

        FailedCheckGuidelineResult result =
                new FailedCheckGuidelineResult(checkName, adherence, hint);

        assertEquals(checkName, result.getCheckName());
        assertEquals(adherence, result.getAdherence());
        assertEquals(hint, result.getHint());
    }

    @Test
    void testDefaultConstructorUsedInReflection() throws Exception {
        // Test that default constructor works via reflection (used by deserialization)
        Class<?> clazz = FailedCheckGuidelineResult.class;
        java.lang.reflect.Constructor<?> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object instance = constructor.newInstance();

        assertNotNull(instance);
        FailedCheckGuidelineResult result = (FailedCheckGuidelineResult) instance;
        assertNull(result.getCheckName());
        assertNull(result.getAdherence());
    }

    @Test
    void testInheritanceFromGuidelineCheckResult() {
        FailedCheckGuidelineResult result =
                new FailedCheckGuidelineResult("Test", GuidelineAdherence.CHECK_FAILED);

        // Test inherited setters
        result.setCheckName("NewName");
        result.setAdherence(GuidelineAdherence.VIOLATED);
        result.setHint("New hint");

        assertEquals("NewName", result.getCheckName());
        assertEquals(GuidelineAdherence.VIOLATED, result.getAdherence());
        assertEquals("New hint", result.getHint());
    }
}
