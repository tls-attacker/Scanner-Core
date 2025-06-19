/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ScannerDetailTest {

    @Test
    public void testValueOf() {
        assertEquals(ScannerDetail.ALL, ScannerDetail.valueOf("ALL"));
        assertEquals(ScannerDetail.DETAILED, ScannerDetail.valueOf("DETAILED"));
        assertEquals(ScannerDetail.NORMAL, ScannerDetail.valueOf("NORMAL"));
        assertEquals(ScannerDetail.QUICK, ScannerDetail.valueOf("QUICK"));
    }

    @Test
    public void testGetLevelValue() {
        assertEquals(100, ScannerDetail.ALL.getLevelValue());
        assertEquals(75, ScannerDetail.DETAILED.getLevelValue());
        assertEquals(50, ScannerDetail.NORMAL.getLevelValue());
        assertEquals(25, ScannerDetail.QUICK.getLevelValue());
    }

    @Test
    public void testIsGreaterEqualToSameLevel() {
        assertTrue(ScannerDetail.ALL.isGreaterEqualTo(ScannerDetail.ALL));
        assertTrue(ScannerDetail.DETAILED.isGreaterEqualTo(ScannerDetail.DETAILED));
        assertTrue(ScannerDetail.NORMAL.isGreaterEqualTo(ScannerDetail.NORMAL));
        assertTrue(ScannerDetail.QUICK.isGreaterEqualTo(ScannerDetail.QUICK));
    }

    @Test
    public void testIsGreaterEqualToGreaterLevel() {
        assertTrue(ScannerDetail.ALL.isGreaterEqualTo(ScannerDetail.DETAILED));
        assertTrue(ScannerDetail.ALL.isGreaterEqualTo(ScannerDetail.NORMAL));
        assertTrue(ScannerDetail.ALL.isGreaterEqualTo(ScannerDetail.QUICK));

        assertTrue(ScannerDetail.DETAILED.isGreaterEqualTo(ScannerDetail.NORMAL));
        assertTrue(ScannerDetail.DETAILED.isGreaterEqualTo(ScannerDetail.QUICK));

        assertTrue(ScannerDetail.NORMAL.isGreaterEqualTo(ScannerDetail.QUICK));
    }

    @Test
    public void testIsGreaterEqualToLowerLevel() {
        assertFalse(ScannerDetail.QUICK.isGreaterEqualTo(ScannerDetail.NORMAL));
        assertFalse(ScannerDetail.QUICK.isGreaterEqualTo(ScannerDetail.DETAILED));
        assertFalse(ScannerDetail.QUICK.isGreaterEqualTo(ScannerDetail.ALL));

        assertFalse(ScannerDetail.NORMAL.isGreaterEqualTo(ScannerDetail.DETAILED));
        assertFalse(ScannerDetail.NORMAL.isGreaterEqualTo(ScannerDetail.ALL));

        assertFalse(ScannerDetail.DETAILED.isGreaterEqualTo(ScannerDetail.ALL));
    }
}
