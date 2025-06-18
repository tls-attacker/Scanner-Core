/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.config;

public enum ScannerDetail {
    ALL(100),
    DETAILED(75),
    NORMAL(50),
    QUICK(25);

    private final int levelValue;

    ScannerDetail(int levelValue) {
        this.levelValue = levelValue;
    }

    /**
     * Returns the numeric level value associated with this scanner detail level.
     *
     * @return the level value as an integer
     */
    public int getLevelValue() {
        return levelValue;
    }

    /**
     * Checks if this scanner detail level is greater than or equal to the specified detail level.
     *
     * @param detail the scanner detail level to compare against
     * @return true if this level is greater than or equal to the specified level, false otherwise
     */
    public boolean isGreaterEqualTo(ScannerDetail detail) {
        return levelValue >= detail.getLevelValue();
    }
}
