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
 * Container for displaying headlines in scanner reports. Supports different depth levels with
 * corresponding visual formatting and colors.
 */
public class HeadlineContainer extends ReportContainer {

    private static final int NUMBER_OF_DASHES_IN_H_LINE = 50;

    private final String headline;

    /**
     * Creates a new HeadlineContainer with normal detail level.
     *
     * @param headline The headline text to display
     */
    public HeadlineContainer(String headline) {
        super(ScannerDetail.NORMAL);
        this.headline = headline;
    }

    /**
     * Creates a new HeadlineContainer with specified detail level.
     *
     * @param headline The headline text to display
     * @param detail The detail level for this container
     */
    public HeadlineContainer(String headline, ScannerDetail detail) {
        super(detail);
        this.headline = headline;
    }

    /**
     * Prints the headline to the provided StringBuilder with appropriate formatting. Depth 0
     * headlines include a horizontal line separator.
     *
     * @param builder The StringBuilder to append the output to
     * @param depth The indentation depth level
     * @param useColor Whether to use ANSI color codes in the output
     */
    @Override
    public void print(StringBuilder builder, int depth, boolean useColor) {
        if (useColor) {
            builder.append(AnsiColor.BOLD.getCode());
            builder.append(getColorByDepth(depth).getCode());
        }
        if (depth == 0) {
            addHLine(builder);
        }
        addHeadlineDepth(builder, depth);
        builder.append(headline);
        if (useColor) {
            builder.append(AnsiColor.RESET.getCode());
        }
        builder.append("\n");
        builder.append("\n");
    }

    private static AnsiColor getColorByDepth(int depth) {
        switch (depth) {
            case 0:
                return AnsiColor.PURPLE;
            case 1:
                return AnsiColor.BLUE;
            case 2:
                return AnsiColor.CYAN;
            case 3:
                return AnsiColor.WHITE;
            default:
                return AnsiColor.YELLOW;
        }
    }

    /**
     * Gets the headline text.
     *
     * @return The headline text
     */
    public String getHeadline() {
        return headline;
    }

    private static void addHLine(StringBuilder builder) {
        builder.append("-".repeat(Math.max(0, NUMBER_OF_DASHES_IN_H_LINE)));
        builder.append("\n");
    }
}
