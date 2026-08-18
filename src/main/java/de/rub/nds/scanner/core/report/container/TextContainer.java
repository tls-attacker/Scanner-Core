/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.report.container;

import de.rub.nds.scanner.core.config.ScannerDetail;
import de.rub.nds.scanner.core.report.markup.Markup;

/**
 * Container for displaying simple text in scanner reports. Supports colored text output with
 * configurable detail levels.
 */
public class TextContainer extends ReportContainer {

    private final String text;
    private final Markup markup;

    /**
     * Creates a new TextContainer with normal detail level.
     *
     * @param text The text to display
     * @param markup The ANSI color for the text
     */
    public TextContainer(String text, Markup markup) {
        super(ScannerDetail.NORMAL);
        this.text = text;
        this.markup = markup;
    }

    /**
     * Creates a new TextContainer with specified detail level.
     *
     * @param text The text to display
     * @param markup The ANSI color for the text
     * @param detail The detail level for this container
     */
    public TextContainer(String text, Markup markup, ScannerDetail detail) {
        super(detail);
        this.text = text;
        this.markup = markup;
    }

    /**
     * Prints the text to the provided StringBuilder with a trailing newline.
     *
     * @param builder The StringBuilder to append the output to
     * @param depth The indentation depth level
     * @param useColor Whether to use ANSI color codes in the output
     */
    @Override
    public void print(StringBuilder builder, int depth, boolean useColor) {
        println(builder, depth, useColor);
        builder.append("\n");
    }

    /**
     * Prints the text to the provided StringBuilder without a trailing newline.
     *
     * @param builder The StringBuilder to append the output to
     * @param depth The indentation depth level
     * @param useColor Whether to use ANSI color codes in the output
     */
    public void println(StringBuilder builder, int depth, boolean useColor) {
        addDepth(builder, depth);
        addColor(builder, markup, text, useColor);
    }

    /**
     * Gets the text content.
     *
     * @return The text to be displayed
     */
    public String getText() {
        return text;
    }
}
