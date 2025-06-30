/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.report;

import java.util.HashMap;
import java.util.Map;

public enum AnsiColor {
    RESET("\u001B[0m"), // $NON-NLS-1$
    BLACK("\u001B[30m"), // $NON-NLS-1$
    RED("\u001B[31m"), // $NON-NLS-1$
    GREEN("\u001B[32m"), // $NON-NLS-1$
    YELLOW("\u001B[33m"), // $NON-NLS-1$
    BLUE("\u001B[34m"), // $NON-NLS-1$
    PURPLE("\u001B[35m"), // $NON-NLS-1$
    CYAN("\u001B[36m"), // $NON-NLS-1$
    WHITE("\u001B[37m"), // $NON-NLS-1$
    BLACK_BACKGROUND("\u001B[40m"), // $NON-NLS-1$
    RED_BACKGROUND("\u001B[41m"), // $NON-NLS-1$
    GREEN_BACKGROUND("\u001B[42m"), // $NON-NLS-1$
    YELLOW_BACKGROUND("\u001B[43m"), // $NON-NLS-1$
    BLUE_BACKGROUND("\u001B[44m"), // $NON-NLS-1$
    PURPLE_BACKGROUND("\u001B[45m"), // $NON-NLS-1$
    CYAN_BACKGROUND("\u001B[46m"), // $NON-NLS-1$
    WHITE_BACKGROUND("\u001B[47m"), // $NON-NLS-1$
    BOLD("\033[0;1m"), // $NON-NLS-1$
    UNDERLINE("\033[4m"), // $NON-NLS-1$
    DEFAULT_COLOR(""); // $NON-NLS-1$

    private final String code;

    private static final Map<String, AnsiColor> MAP;

    AnsiColor(String code) {
        this.code = code;
    }

    static {
        MAP = new HashMap<>();
        for (AnsiColor c : values()) {
            MAP.put(c.code, c);
        }
    }

    public static AnsiColor getAnsiColor(String code) {
        return MAP.get(code);
    }

    public String getCode() {
        return code;
    }
}
