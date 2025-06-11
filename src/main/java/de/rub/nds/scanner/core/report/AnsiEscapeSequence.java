/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.report;

/**
 * Utility class providing ANSI escape sequences for terminal cursor control and line manipulation.
 *
 * <p>This class contains constants for ANSI escape sequences that control terminal cursor
 * positioning and line operations. These sequences are essential for creating dynamic terminal
 * interfaces, progress indicators, and updating output in place.
 *
 * <p>The provided sequences include:
 *
 * <ul>
 *   <li><strong>Cursor Movement:</strong> Moving the cursor up one line
 *   <li><strong>Line Operations:</strong> Erasing the current line content
 * </ul>
 *
 * <p>These sequences are commonly used together to create updating displays, such as progress
 * bars or status indicators that overwrite previous output rather than scrolling.
 *
 * <p>Usage example:
 *
 * <pre>{@code
 * // Update a progress indicator
 * System.out.print("Processing: 50%");
 * // Later, update the same line
 * System.out.print(AnsiEscapeSequence.ANSI_ONE_LINE_UP);
 * System.out.print(AnsiEscapeSequence.ANSI_ERASE_LINE);
 * System.out.print("Processing: 75%");
 *
 * // Or create a simple progress updater
 * public void updateProgress(int percentage) {
 *     System.out.print(ANSI_ONE_LINE_UP + ANSI_ERASE_LINE + "Progress: " + percentage + "%\n");
 * }
 * }</pre>
 *
 * <p><strong>Note:</strong> These escape sequences work only in ANSI-compatible terminals. They
 * are widely supported in Unix-like systems and modern Windows terminals but may not work in
 * all environments.
 *
 * @see AnsiColor
 */
public class AnsiEscapeSequence {

    /**
     * ANSI escape sequence to move the cursor up one line.
     *
     * <p>This sequence moves the terminal cursor to the beginning of the line above the current
     * position. It's commonly used to overwrite previously printed text.
     */
    public static final String ANSI_ONE_LINE_UP = "\033[1A";

    /**
     * ANSI escape sequence to erase the entire current line.
     *
     * <p>This sequence clears all content on the current line where the cursor is positioned,
     * leaving the cursor at the beginning of the now-empty line. The line itself remains
     * (cursor doesn't move to previous line).
     */
    public static final String ANSI_ERASE_LINE = "\033[2K";
}
