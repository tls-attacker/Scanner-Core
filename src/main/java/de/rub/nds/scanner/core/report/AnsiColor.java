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

/**
 * Enum representing ANSI color codes for terminal output. Provides foreground colors, background
 * colors, and text formatting options.
 */
public enum AnsiColor {
    RESET("\u001B[0m"),
    BLACK("\u001B[30m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    PURPLE("\u001B[35m"),
    CYAN("\u001B[36m"),
    WHITE("\u001B[37m"),
    BLACK_BACKGROUND("\u001B[40m"),
    RED_BACKGROUND("\u001B[41m"),
    GREEN_BACKGROUND("\u001B[42m"),
    YELLOW_BACKGROUND("\u001B[43m"),
    BLUE_BACKGROUND("\u001B[44m"),
    PURPLE_BACKGROUND("\u001B[45m"),
    CYAN_BACKGROUND("\u001B[46m"),
    WHITE_BACKGROUND("\u001B[47m"),
    BOLD("\033[0;1m"),
    UNDERLINE("\033[4m"),
    DEFAULT_COLOR("");

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

    /**
     * Returns the AnsiColor corresponding to the given ANSI code string.
     *
     * @param code the ANSI code string
     * @return the corresponding AnsiColor, or null if no match is found
     */
    public static AnsiColor getAnsiColor(String code) {
        return MAP.get(code);
    }

    /**
     * Returns the ANSI code string for this color.
     *
     * @return the ANSI code string
     */
    public String getCode() {
        return code;
    }
}
