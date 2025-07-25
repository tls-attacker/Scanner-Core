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

class MissingRequirementGuidelineResultTest {

    @Test
    void testConstructorWithCheckAndAdherence() {
        GuidelineCheck check = new IOTestGuidelineCheck("TestCheck", RequirementLevel.MUST);
        GuidelineAdherence adherence = GuidelineAdherence.CONDITION_NOT_MET;

        MissingRequirementGuidelineResult result =
                new MissingRequirementGuidelineResult(check, adherence);

        assertEquals(check.getName(), result.getCheckName());
        assertEquals(check.getRequirementLevel(), result.getLevel());
        assertEquals(adherence, result.getAdherence());
        assertNull(result.getHint());
    }

    @Test
    void testConstructorWithNameAdherenceAndHint() {
        GuidelineCheck check = new IOTestGuidelineCheck("TestCheck", RequirementLevel.MUST);
        GuidelineAdherence adherence = GuidelineAdherence.CONDITION_NOT_MET;
        String hint = "Precondition not satisfied";

        MissingRequirementGuidelineResult result =
                new MissingRequirementGuidelineResult(check, adherence, hint);

        assertEquals(check.getName(), result.getCheckName());
        assertEquals(check.getRequirementLevel(), result.getLevel());
        assertEquals(adherence, result.getAdherence());
        assertEquals(hint, result.getHint());
    }

    @Test
    void testDefaultConstructorUsedInReflection() throws Exception {
        // Test that default constructor works via reflection (used by deserialization)
        Class<?> clazz = MissingRequirementGuidelineResult.class;
        Constructor<?> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object instance = constructor.newInstance();

        assertNotNull(instance);
        MissingRequirementGuidelineResult result = (MissingRequirementGuidelineResult) instance;
        assertNull(result.getCheckName());
        assertNull(result.getAdherence());
    }

    @Test
    void testInheritanceFromGuidelineCheckResult() {
        GuidelineCheck check = new IOTestGuidelineCheck("Test", RequirementLevel.MUST);
        MissingRequirementGuidelineResult result =
                new MissingRequirementGuidelineResult(check, GuidelineAdherence.CONDITION_NOT_MET);

        // Test inherited setters
        result.setCheckName("NewName");
        result.setLevel(RequirementLevel.MAY);
        result.setAdherence(GuidelineAdherence.ADHERED);
        result.setHint("New hint");

        assertEquals("NewName", result.getCheckName());
        assertEquals(RequirementLevel.MAY, result.getLevel());
        assertEquals(GuidelineAdherence.ADHERED, result.getAdherence());
        assertEquals("New hint", result.getHint());
    }
}
