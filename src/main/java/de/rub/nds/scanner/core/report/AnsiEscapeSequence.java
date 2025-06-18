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
 * Utility class containing ANSI escape sequences for terminal manipulation.
 * Provides constants for cursor movement and line manipulation.
 */
public class AnsiEscapeSequence {

    /** ANSI escape sequence to move cursor up one line */
    public static final String ANSI_ONE_LINE_UP = "\033[1A";
    /** ANSI escape sequence to erase the current line */
    public static final String ANSI_ERASE_LINE = "\033[2K";
}
