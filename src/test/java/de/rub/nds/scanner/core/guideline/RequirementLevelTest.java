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

class RequirementLevelTest {

    @Test
    void testValueOf() {
        assertEquals(RequirementLevel.MUST, RequirementLevel.valueOf("MUST"));
        assertEquals(RequirementLevel.MUST_NOT, RequirementLevel.valueOf("MUST_NOT"));
        assertEquals(RequirementLevel.SHOULD, RequirementLevel.valueOf("SHOULD"));
        assertEquals(RequirementLevel.SHOULD_NOT, RequirementLevel.valueOf("SHOULD_NOT"));
        assertEquals(RequirementLevel.MAY, RequirementLevel.valueOf("MAY"));
    }
}
