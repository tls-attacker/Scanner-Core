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

import de.rub.nds.scanner.core.guideline.testutil.IOTestGuidelineCheck;
import java.lang.reflect.Constructor;
import org.junit.jupiter.api.Test;

class GuidelineCheckResultTest {

    // Concrete implementation for testing
    private static class ConcreteGuidelineCheckResult extends GuidelineCheckResult {
        public ConcreteGuidelineCheckResult(GuidelineCheck check, GuidelineAdherence adherence) {
            super(check, adherence);
        }

        public ConcreteGuidelineCheckResult(
                GuidelineCheck check, GuidelineAdherence adherence, String hint) {
            super(check, adherence, hint);
        }

        @SuppressWarnings("unused")
        private ConcreteGuidelineCheckResult() {
            super(null, null);
        }
    }

    @Test
    void testConstructorWithCheckAndAdherence() {
        GuidelineCheck check = new IOTestGuidelineCheck("Test name", RequirementLevel.MUST);
        GuidelineAdherence adherence = GuidelineAdherence.ADHERED;

        ConcreteGuidelineCheckResult result = new ConcreteGuidelineCheckResult(check, adherence);

        assertEquals(check.getName(), result.getCheckName());
        assertEquals(check.getRequirementLevel(), result.getLevel());
        assertEquals(adherence, result.getAdherence());
        assertNull(result.getHint());
    }

    @Test
    void testConstructorWithCheckAdherenceAndHint() {
        GuidelineCheck check = new IOTestGuidelineCheck("Test name", RequirementLevel.MUST);
        GuidelineAdherence adherence = GuidelineAdherence.VIOLATED;
        String hint = "Test hint";

        ConcreteGuidelineCheckResult result =
                new ConcreteGuidelineCheckResult(check, adherence, hint);

        assertEquals(check.getName(), result.getCheckName());
        assertEquals(check.getRequirementLevel(), result.getLevel());
        assertEquals(adherence, result.getAdherence());
        assertEquals(hint, result.getHint());
    }

    @Test
    void testSettersAndGetters() {
        GuidelineCheck check = new IOTestGuidelineCheck("Test name", RequirementLevel.MUST);
        ConcreteGuidelineCheckResult result =
                new ConcreteGuidelineCheckResult(check, GuidelineAdherence.ADHERED);

        result.setCheckName("UpdatedName");
        result.setAdherence(GuidelineAdherence.VIOLATED);
        result.setLevel(RequirementLevel.MAY);
        result.setHint("Updated hint");

        assertEquals("UpdatedName", result.getCheckName());
        assertEquals(RequirementLevel.MAY, result.getLevel());
        assertEquals(GuidelineAdherence.VIOLATED, result.getAdherence());
        assertEquals("Updated hint", result.getHint());
    }

    @Test
    void testDefaultConstructorUsedInReflection() throws Exception {
        // Test that default constructor works via reflection (used by deserialization)
        Class<?> clazz = ConcreteGuidelineCheckResult.class;
        Constructor<?> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object instance = constructor.newInstance();

        assertNotNull(instance);
        ConcreteGuidelineCheckResult result = (ConcreteGuidelineCheckResult) instance;
        assertNull(result.getCheckName());
        assertNull(result.getAdherence());
        assertNull(result.getLevel());
        assertNull(result.getHint());
    }
}
