/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.report;

import de.rub.nds.scanner.core.config.ScannerDetail;
import de.rub.nds.scanner.core.probe.AnalyzedProperty;

/**
 * Abstract base class for generating formatted text representations of scan reports. Provides
 * utility methods for formatting output with optional ANSI color codes.
 *
 * @param <ReportT> the type of scan report this printer works with
 */
public abstract class ReportPrinter<ReportT extends ScanReport> {

    protected final ScannerDetail detail;
    private int depth;

    private final PrintingScheme scheme;
    protected final boolean printColorful;

    protected final ReportT report;

    /**
     * Constructs a new ReportPrinter with the specified configuration.
     *
     * @param detail the level of detail for report generation
     * @param scheme the printing scheme defining formatting and colors
     * @param printColorful whether to include ANSI color codes in the output
     * @param scanReport the scan report to print
     */
    public ReportPrinter(
            ScannerDetail detail,
            PrintingScheme scheme,
            boolean printColorful,
            ReportT scanReport) {
        this.detail = detail;
        this.scheme = scheme;
        this.printColorful = printColorful;
        this.report = scanReport;
    }

    /**
     * Generates the complete formatted report as a string.
     *
     * @return the full report text with optional color formatting
     */
    public abstract String getFullReport();

    /**
     * Formats a string value without color (black text).
     *
     * @param value the value to format, or null
     * @param format the format string for String.format()
     * @return the formatted string, with "Unknown" if value is null
     */
    protected String getBlackString(String value, String format) {
        return String.format(format, value == null ? "Unknown" : value);
    }

    /**
     * Formats a string value with green color if colors are enabled.
     *
     * @param value the value to format, or null
     * @param format the format string for String.format()
     * @return the formatted string with optional green color, "Unknown" if value is null
     */
    protected String getGreenString(String value, String format) {
        return (printColorful ? AnsiColor.GREEN.getCode() : AnsiColor.RESET.getCode())
                + String.format(format, value == null ? "Unknown" : value)
                + AnsiColor.RESET.getCode();
    }

    /**
     * Formats a string value with yellow color if colors are enabled.
     *
     * @param value the value to format, or null
     * @param format the format string for String.format()
     * @return the formatted string with optional yellow color, "Unknown" if value is null
     */
    protected String getYellowString(String value, String format) {
        return (printColorful ? AnsiColor.YELLOW.getCode() : AnsiColor.RESET.getCode())
                + String.format(format, value == null ? "Unknown" : value)
                + AnsiColor.RESET.getCode();
    }

    /**
     * Formats a string value with red color if colors are enabled.
     *
     * @param value the value to format, or null
     * @param format the format string for String.format()
     * @return the formatted string with optional red color, "Unknown" if value is null
     */
    protected String getRedString(String value, String format) {
        return (printColorful ? AnsiColor.RED.getCode() : AnsiColor.RESET.getCode())
                + String.format(format, value == null ? "Unknown" : value)
                + AnsiColor.RESET.getCode();
    }

    /**
     * Appends a value to the builder with a newline.
     *
     * @param builder the StringBuilder to append to
     * @param value the value to append, or null
     * @return the builder for method chaining
     */
    protected StringBuilder prettyAppend(StringBuilder builder, String value) {
        return builder.append(value == null ? "Unknown" : value).append("\n");
    }

    /**
     * Appends a value to the builder with optional color and a newline.
     *
     * @param builder the StringBuilder to append to
     * @param value the value to append
     * @param color the color to apply if colors are enabled
     * @return the builder for method chaining
     */
    protected StringBuilder prettyAppend(StringBuilder builder, String value, AnsiColor color) {
        if (printColorful) {
            builder.append(color.getCode());
        }
        builder.append(value);
        if (printColorful) {
            builder.append(AnsiColor.RESET.getCode());
        }
        builder.append("\n");
        return builder;
    }

    /**
     * Appends a name-value pair with the value formatted as hexadecimal.
     *
     * @param builder the StringBuilder to append to
     * @param name the property name
     * @param value the hexadecimal value (without "0x" prefix), or null
     * @return the builder for method chaining
     */
    protected StringBuilder prettyAppendHexString(
            StringBuilder builder, String name, String value) {
        return builder.append(addIndentations(name))
                .append(": ")
                .append(value == null ? "Unknown" : "0x" + value)
                .append("\n");
    }

    /**
     * Appends a name-value pair with proper indentation.
     *
     * @param builder the StringBuilder to append to
     * @param name the property name
     * @param value the property value, or null
     * @return the builder for method chaining
     */
    protected StringBuilder prettyAppend(StringBuilder builder, String name, String value) {
        return builder.append(addIndentations(name))
                .append(": ")
                .append(value == null ? "Unknown" : value)
                .append("\n");
    }

    /**
     * Appends a name-value pair for a Long value with proper indentation.
     *
     * @param builder the StringBuilder to append to
     * @param name the property name
     * @param value the Long value, or null
     * @return the builder for method chaining
     */
    protected StringBuilder prettyAppend(StringBuilder builder, String name, Long value) {
        return builder.append(addIndentations(name))
                .append(": ")
                .append(value == null ? "Unknown" : value)
                .append("\n");
    }

    /**
     * Appends a name-value pair for a Boolean value with proper indentation.
     *
     * @param builder the StringBuilder to append to
     * @param name the property name
     * @param value the Boolean value, or null
     * @return the builder for method chaining
     */
    protected StringBuilder prettyAppend(StringBuilder builder, String name, Boolean value) {
        return builder.append(addIndentations(name))
                .append(": ")
                .append(value == null ? "Unknown" : value)
                .append("\n");
    }

    /**
     * Appends a name-value pair for an analyzed property using the printing scheme.
     *
     * @param builder the StringBuilder to append to
     * @param name the display name
     * @param property the analyzed property to format
     * @return the builder for method chaining
     */
    protected StringBuilder prettyAppend(
            StringBuilder builder, String name, AnalyzedProperty property) {
        builder.append(addIndentations(name)).append(": ");
        builder.append(scheme.getEncodedString(report, property, printColorful));
        builder.append("\n");
        return builder;
    }

    /**
     * Appends a name-value pair for a Boolean with optional color.
     *
     * @param builder the StringBuilder to append to
     * @param name the property name
     * @param value the Boolean value
     * @param color the color to apply if colors are enabled
     * @return the builder for method chaining
     */
    protected StringBuilder prettyAppend(
            StringBuilder builder, String name, Boolean value, AnsiColor color) {
        return prettyAppend(builder, name, String.valueOf(value), color);
    }

    /**
     * Appends a name-value pair with optional color.
     *
     * @param builder the StringBuilder to append to
     * @param name the property name
     * @param value the property value
     * @param color the color to apply if colors are enabled
     * @return the builder for method chaining
     */
    protected StringBuilder prettyAppend(
            StringBuilder builder, String name, String value, AnsiColor color) {
        builder.append(addIndentations(name)).append(": ");
        if (printColorful) {
            builder.append(color.getCode());
        }
        builder.append(value);
        if (printColorful) {
            builder.append(AnsiColor.RESET.getCode());
        }
        builder.append("\n");
        return builder;
    }

    /**
     * Appends a main heading with formatting and resets the indentation depth.
     *
     * @param builder the StringBuilder to append to
     * @param value the heading text
     * @return the builder for method chaining
     */
    protected StringBuilder prettyAppendHeading(StringBuilder builder, String value) {
        depth = 0;

        return builder.append(
                        printColorful
                                ? AnsiColor.BOLD.getCode() + AnsiColor.BLUE.getCode()
                                : AnsiColor.RESET.getCode())
                .append("\n------------------------------------------------------------\n")
                .append(value)
                .append("\n\n")
                .append(AnsiColor.RESET.getCode());
    }

    /**
     * Appends a name-value pair with the value underlined if colors are enabled.
     *
     * @param builder the StringBuilder to append to
     * @param name the property name
     * @param value the property value
     * @return the builder for method chaining
     */
    protected StringBuilder prettyAppendUnderlined(
            StringBuilder builder, String name, String value) {
        return builder.append(addIndentations(name))
                .append(": ")
                .append(
                        printColorful
                                ? AnsiColor.UNDERLINE.getCode() + value + AnsiColor.RESET.getCode()
                                : value)
                .append("\n");
    }

    /**
     * Appends a name-value pair for a boolean with the value underlined if colors are enabled.
     *
     * @param builder the StringBuilder to append to
     * @param name the property name
     * @param value the boolean value
     * @return the builder for method chaining
     */
    protected StringBuilder prettyAppendUnderlined(
            StringBuilder builder, String name, boolean value) {
        return builder.append(addIndentations(name))
                .append(": ")
                .append(
                        printColorful
                                ? AnsiColor.UNDERLINE.getCode() + value + AnsiColor.RESET.getCode()
                                : value)
                .append("\n");
    }

    /**
     * Appends a name-value pair for a long with the value underlined if colors are enabled.
     *
     * @param builder the StringBuilder to append to
     * @param name the property name
     * @param value the long value
     * @return the builder for method chaining
     */
    protected StringBuilder prettyAppendUnderlined(StringBuilder builder, String name, long value) {
        return builder.append(addIndentations(name))
                .append(": ")
                .append(
                        !printColorful
                                ? AnsiColor.UNDERLINE.getCode() + value + AnsiColor.RESET.getCode()
                                : value)
                .append("\n");
    }

    /**
     * Appends a subheading with formatting and sets indentation depth to 1.
     *
     * @param builder the StringBuilder to append to
     * @param name the subheading text
     * @return the builder for method chaining
     */
    protected StringBuilder prettyAppendSubheading(StringBuilder builder, String name) {
        depth = 1;
        return builder.append("--|")
                .append(
                        printColorful
                                ? AnsiColor.BOLD.getCode()
                                        + AnsiColor.PURPLE.getCode()
                                        + AnsiColor.UNDERLINE.getCode()
                                        + name
                                        + "\n\n"
                                        + AnsiColor.RESET.getCode()
                                : name + "\n\n");
    }

    /**
     * Appends a sub-subheading with formatting and sets indentation depth to 2.
     *
     * @param builder the StringBuilder to append to
     * @param name the sub-subheading text
     * @return the builder for method chaining
     */
    protected StringBuilder prettyAppendSubSubheading(StringBuilder builder, String name) {
        depth = 2;
        return builder.append("----|")
                .append(
                        printColorful
                                ? AnsiColor.BOLD.getCode()
                                        + AnsiColor.PURPLE.getCode()
                                        + AnsiColor.UNDERLINE.getCode()
                                        + name
                                        + "\n\n"
                                        + AnsiColor.RESET.getCode()
                                : name + "\n\n");
    }

    /**
     * Appends a sub-sub-subheading with formatting and sets indentation depth to 3.
     *
     * @param builder the StringBuilder to append to
     * @param name the sub-sub-subheading text
     * @return the builder for method chaining
     */
    protected StringBuilder prettyAppendSubSubSubheading(StringBuilder builder, String name) {
        depth = 3;
        return builder.append("------|")
                .append(
                        printColorful
                                ? AnsiColor.BOLD.getCode()
                                        + AnsiColor.PURPLE.getCode()
                                        + AnsiColor.UNDERLINE.getCode()
                                        + name
                                        + "\n\n"
                                        + AnsiColor.RESET.getCode()
                                : name + "\n\n");
    }

    /**
     * Pads a string to the specified length with spaces.
     *
     * @param value the string to pad
     * @param length the desired length
     * @return the padded string
     */
    protected String padToLength(String value, int length) {
        StringBuilder builder = new StringBuilder(value);
        while (builder.length() < length) {
            builder.append(" ");
        }
        return builder.toString();
    }

    /**
     * Adds indentation based on the current depth and tabs for alignment.
     *
     * @param value the string to indent
     * @return the indented string with appropriate tabs for alignment
     */
    protected String addIndentations(String value) {
        StringBuilder builder = new StringBuilder();
        builder.append(" ".repeat(Math.max(0, depth)));
        builder.append(value);
        if (value.length() + depth < 8) {
            builder.append("\t\t\t\t ");
        } else if (value.length() + depth < 16) {
            builder.append("\t\t\t ");
        } else if (value.length() + depth < 24) {
            builder.append("\t\t ");
        } else if (value.length() + depth < 32) {
            builder.append("\t ");
        } else {
            builder.append(" ");
        }
        return builder.toString();
    }

    /**
     * Sets the current indentation depth for formatting.
     *
     * @param depth the indentation depth (0 for no indent, higher values for deeper nesting)
     */
    public void setDepth(int depth) {
        this.depth = depth;
    }
}
