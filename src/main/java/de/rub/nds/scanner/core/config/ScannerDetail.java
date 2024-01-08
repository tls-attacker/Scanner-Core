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

    public int getLevelValue() {
        return levelValue;
    }

    public boolean isGreaterEqualTo(ScannerDetail detail) {
        return levelValue >= detail.getLevelValue();
    }
}
