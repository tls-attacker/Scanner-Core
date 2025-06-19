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
import de.rub.nds.scanner.core.report.AnsiColor;

/**
 * Abstract base class for all report containers in the scanner framework. Provides common
 * functionality for printing formatted reports with indentation and color support.
 */
public abstract class ReportContainer {

    private final ScannerDetail detail;

    /**
     * Creates a new ReportContainer with the specified detail level.
     *
     * @param detail The detail level for this container
     */
    public ReportContainer(ScannerDetail detail) {
        this.detail = detail;
    }

    /**
     * Prints this container's content to the provided StringBuilder.
     *
     * @param builder The StringBuilder to append the output to
     * @param depth The indentation depth level
     * @param useColor Whether to use ANSI color codes in the output
     */
    public abstract void print(StringBuilder builder, int depth, boolean useColor);

    /**
     * Adds indentation spaces based on the specified depth. Each depth level adds two spaces.
     *
     * @param builder The StringBuilder to append to
     * @param depth The indentation depth level
     * @return The modified StringBuilder for method chaining
     */
    protected StringBuilder addDepth(StringBuilder builder, int depth) {
        builder.append("  ".repeat(Math.max(0, depth)));
        return builder;
    }

    /**
     * Adds headline-specific indentation based on the specified depth. Uses dashes and pipes for
     * visual hierarchy.
     *
     * @param builder The StringBuilder to append to
     * @param depth The indentation depth level
     * @return The modified StringBuilder for method chaining
     */
    protected StringBuilder addHeadlineDepth(StringBuilder builder, int depth) {
        builder.append("--".repeat(Math.max(0, depth)));
        if (depth > 0) {
            builder.append("|");
        }
        return builder;
    }

    /**
     * Adds colored text to the StringBuilder if color is enabled.
     *
     * @param builder The StringBuilder to append to
     * @param color The ANSI color to apply
     * @param text The text to colorize
     * @param useColor Whether to apply color codes
     * @return The modified StringBuilder for method chaining
     */
    protected StringBuilder addColor(
            StringBuilder builder, AnsiColor color, String text, boolean useColor) {
        if (useColor) {
            builder.append(color.getCode()).append(text).append(AnsiColor.RESET.getCode());
        } else {
            builder.append(text);
        }
        return builder;
    }

    /**
     * Gets the detail level for this container.
     *
     * @return The scanner detail level
     */
    public ScannerDetail getDetail() {
        return detail;
    }
}
