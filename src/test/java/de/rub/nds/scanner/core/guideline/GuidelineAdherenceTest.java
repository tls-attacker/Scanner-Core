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

import org.junit.jupiter.api.Test;

class GuidelineAdherenceTest {

    @Test
    void testOfMethodWithTrue() {
        assertEquals(GuidelineAdherence.ADHERED, GuidelineAdherence.of(true));
    }

    @Test
    void testOfMethodWithFalse() {
        assertEquals(GuidelineAdherence.VIOLATED, GuidelineAdherence.of(false));
    }

    @Test
    void testEnumValues() {
        GuidelineAdherence[] values = GuidelineAdherence.values();
        assertEquals(4, values.length);
        assertEquals(GuidelineAdherence.ADHERED, values[0]);
        assertEquals(GuidelineAdherence.VIOLATED, values[1]);
        assertEquals(GuidelineAdherence.CONDITION_NOT_MET, values[2]);
        assertEquals(GuidelineAdherence.CHECK_FAILED, values[3]);
    }

    @Test
    void testValueOf() {
        assertEquals(GuidelineAdherence.ADHERED, GuidelineAdherence.valueOf("ADHERED"));
        assertEquals(GuidelineAdherence.VIOLATED, GuidelineAdherence.valueOf("VIOLATED"));
        assertEquals(GuidelineAdherence.CONDITION_NOT_MET, GuidelineAdherence.valueOf("CONDITION_NOT_MET"));
        assertEquals(GuidelineAdherence.CHECK_FAILED, GuidelineAdherence.valueOf("CHECK_FAILED"));
    }
}