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
 * Enumeration of ANSI color codes and text formatting options for terminal output.
 *
 * <p>This enum provides a comprehensive set of ANSI escape sequences for colorizing and formatting
 * text in terminal applications. It supports both foreground colors, background colors, and text
 * decorations like bold and underline.
 *
 * <p>The enum includes:
 *
 * <ul>
 *   <li><strong>Foreground Colors:</strong> BLACK, RED, GREEN, YELLOW, BLUE, PURPLE, CYAN, WHITE
 *   <li><strong>Background Colors:</strong> *_BACKGROUND variants of the foreground colors
 *   <li><strong>Text Formatting:</strong> BOLD, UNDERLINE
 *   <li><strong>Control:</strong> RESET (clears all formatting), DEFAULT_COLOR (no color)
 * </ul>
 *
 * <p>ANSI color codes work by sending escape sequences to ANSI-compatible terminals. These codes
 * are widely supported in Unix-like systems and modern Windows terminals.
 *
 * <p>Usage examples:
 *
 * <pre>{@code
 * // Basic color application
 * String coloredText = AnsiColor.RED.getCode() + "Error message" + AnsiColor.RESET.getCode();
 *
 * // Using with ColorEncoding for automatic reset
 * ColorEncoding encoding = new ColorEncoding(colorMap);
 * String encoded = encoding.encode(result, "Success");
 *
 * // Looking up colors by escape code
 * AnsiColor color = AnsiColor.getAnsiColor("\u001B[31m"); // Returns RED
 * }</pre>
 *
 * <p><strong>Note:</strong> Always use {@link #RESET} after applying colors to avoid affecting
 * subsequent output, or use {@link ColorEncoding} which handles this automatically.
 *
 * @see ColorEncoding
 * @see AnsiEscapeSequence
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
        for (AnsiColor c : AnsiColor.values()) {
            MAP.put(c.code, c);
        }
    }

    /**
     * Retrieves an AnsiColor enum value by its ANSI escape code.
     *
     * <p>This method allows reverse lookup of color constants from their escape sequence strings.
     * It's useful when parsing ANSI-formatted text or when working with color codes from external
     * sources.
     *
     * @param code the ANSI escape code string to look up (e.g., "\u001B[31m")
     * @return the corresponding AnsiColor enum value, or null if not found
     */
    public static AnsiColor getAnsiColor(String code) {
        return MAP.get(code);
    }

    /**
     * Returns the ANSI escape code string for this color.
     *
     * <p>The returned string can be directly output to an ANSI-compatible terminal to apply the
     * color or formatting. For DEFAULT_COLOR, this returns an empty string.
     *
     * @return the ANSI escape sequence for this color, never null
     */
    public String getCode() {
        return code;
    }
}
