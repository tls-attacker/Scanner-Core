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
import de.rub.nds.scanner.core.report.container.KeyValueContainer;
import de.rub.nds.scanner.core.report.container.ReportContainer;
import de.rub.nds.scanner.core.report.container.TextContainer;

/**
 * Base class for creating report containers from scan results. Provides utility methods for
 * creating various types of report containers with appropriate formatting and color schemes.
 *
 * @param <ReportT> the type of scan report this creator works with
 */
public class ReportCreator<ReportT extends ScanReport> {

    protected final PrintingScheme printingScheme;
    protected final ScannerDetail detail;

    /**
     * Constructs a new ReportCreator with the specified detail level and printing scheme.
     *
     * @param detail the level of detail for report generation
     * @param scheme the printing scheme defining formatting and colors
     */
    public ReportCreator(ScannerDetail detail, PrintingScheme scheme) {
        this.printingScheme = scheme;
        this.detail = detail;
    }

    /**
     * Creates a key-value container for a property with appropriate formatting from the printing
     * scheme.
     *
     * @param property the analyzed property to create a container for
     * @param report the scan report containing the property result
     * @return a key-value container with formatted text and colors
     */
    protected ReportContainer createKeyValueContainer(AnalyzedProperty property, ReportT report) {
        String key = printingScheme.getEncodedKeyText(report, property);
        String value = printingScheme.getEncodedValueText(report, property);
        AnsiColor keyColour = printingScheme.getKeyColor(report, property);
        AnsiColor valueColour = printingScheme.getValueColor(report, property);
        return new KeyValueContainer(key, keyColour, value, valueColour);
    }

    /**
     * Creates a key-value container with default colors for both key and value.
     *
     * @param key the key text
     * @param value the value text
     * @return a key-value container with default colors
     */
    protected ReportContainer createDefaultKeyValueContainer(String key, String value) {
        return new KeyValueContainer(key, AnsiColor.DEFAULT_COLOR, value, AnsiColor.DEFAULT_COLOR);
    }

    /**
     * Creates a key-value container with a hexadecimal formatted value and default colors.
     *
     * @param key the key text
     * @param value the hexadecimal value (without "0x" prefix)
     * @return a key-value container with the value prefixed with "0x"
     */
    protected ReportContainer createDefaultKeyHexValueContainer(String key, String value) {
        return new KeyValueContainer(
                key, AnsiColor.DEFAULT_COLOR, "0x" + value, AnsiColor.DEFAULT_COLOR);
    }

    /**
     * Creates a text container with default color.
     *
     * @param text the text content
     * @return a text container with default color
     */
    protected TextContainer createDefaultTextContainer(String text) {
        return new TextContainer(text, AnsiColor.DEFAULT_COLOR);
    }
}
