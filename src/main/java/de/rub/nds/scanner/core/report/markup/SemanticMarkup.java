/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.report.markup;

import de.rub.nds.scanner.core.report.AnsiColor;

public enum SemanticMarkup implements Markup {
    /** Represents a severely bad result, where the server is vulnerable. */
    RESULT_BAD(AnsiColor.RED),
    /** Represents a bad result, where the server is not as secure as it could be. */
    RESULT_MEDIUM(AnsiColor.YELLOW),
    /** Represents a result, where the scanner was unsure whether it is good or bad. */
    RESULT_UNSURE(AnsiColor.YELLOW_BACKGROUND),
    /** Represents a good result, where the server is secure. */
    RESULT_GOOD(AnsiColor.GREEN),
    /** Used for structuring the output of the report, e.g., for headings or separators. */
    REPORT_STRUCTURE_HEADING(AnsiColor.BOLD, AnsiColor.CYAN),
    REPORT_STRUCTURE_SUBHEADINGS(AnsiColor.BOLD, AnsiColor.UNDERLINE, AnsiColor.CYAN),
    REPORT_STRUCTURE_PARAGRAPH(AnsiColor.BOLD),
    REPORT_STRUCTURE_TABLE_HEADING(AnsiColor.BOLD),
    REPORT_STRUCTURE_LINK(AnsiColor.UNDERLINE),
    /**
     * Represents an information that led to the scanner being unable to determine the result
     * because the server does not support a certain feature.
     */
    SCANNER_INFO_SERVER_DEPENDENT(AnsiColor.BLUE),
    /**
     * Represents an error that led to the scanner being unable to determine the result because the
     * server misbehaved.
     */
    SCANNER_ERROR_SERVER_DEPENDENT(AnsiColor.BLUE_BACKGROUND),
    /**
     * Represents an information that led to the scanner being unable to determine the result
     * because of an error during evaluation.
     */
    SCANNER_ERROR(AnsiColor.PURPLE),
    /**
     * Represents an information that led to the scanner being unable to determine the result
     * because of a programming error in the scanner.
     */
    SCANNER_ERROR_PROGRAMMING(AnsiColor.PURPLE_BACKGROUND),
    /** Represents a no markup. */
    NEUTRAL();

    private final String RESET = AnsiColor.RESET.getCode();
    private final String ansiCode;

    SemanticMarkup(AnsiColor... ansiColor) {
        if (ansiColor.length == 0) {
            this.ansiCode = null;
        } else {
            StringBuilder codeBuilder = new StringBuilder();
            for (AnsiColor color : ansiColor) {
                codeBuilder.append(color.getCode());
            }
            this.ansiCode = codeBuilder.toString();
        }
    }

    /**
     * Gets the underlying ANSI code(s) associated with this markup. Prefer using the applyAnsi
     * methods for applying formatting to text, as they handle reset codes automatically.
     *
     * @return The ANSI code as a string, or null if no formatting is applied
     */
    public String getAnsiCode() {
        return ansiCode;
    }

    @Override
    public String applyAnsi(String text) {
        if (ansiCode == null) {
            // no need to append a reset code if no color was applied
            return text;
        }
        return ansiCode + text + RESET;
    }

    @Override
    public StringBuilder applyAnsi(StringBuilder builder, String... texts) {
        if (ansiCode != null) {
            builder.append(ansiCode);
        }
        for (String text : texts) {
            builder.append(text);
        }
        if (ansiCode != null) {
            builder.append(RESET);
        }
        return builder;
    }
}
