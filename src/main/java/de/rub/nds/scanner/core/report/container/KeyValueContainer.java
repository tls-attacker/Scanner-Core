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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Container for displaying key-value pairs in scanner reports. Provides formatted output with
 * configurable colors for both keys and values.
 */
public class KeyValueContainer extends ReportContainer {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final int PADDED_KEY_LENGTH = 30;

    private String key;
    private Markup keyMarkup;

    private String value;
    private Markup valueMarkup;

    /**
     * Creates a new KeyValueContainer with specified colors.
     *
     * @param key The key text to display
     * @param keyMarkup The color for the key
     * @param value The value text to display
     * @param valueMarkup The color for the value
     */
    public KeyValueContainer(String key, Markup keyMarkup, String value, Markup valueMarkup) {
        super(ScannerDetail.NORMAL);
        this.key = key;
        this.keyMarkup = keyMarkup;
        this.value = value;
        this.valueMarkup = valueMarkup;
    }

    /**
     * Prints the key-value pair to the provided StringBuilder. The key is padded to ensure
     * consistent alignment.
     *
     * @param builder The StringBuilder to append the output to
     * @param depth The indentation depth level
     * @param useColor Whether to use ANSI color codes in the output
     */
    @Override
    public void print(StringBuilder builder, int depth, boolean useColor) {
        addDepth(builder, depth);
        addColor(builder, keyMarkup, pad(key, PADDED_KEY_LENGTH), useColor);
        builder.append(":    ");
        addColor(builder, valueMarkup, value, useColor);
        builder.append("\n");
    }

    private static String pad(String text, int size) {
        if (text == null) {
            text = "";
        }
        if (text.length() < size) {
            return text + " ".repeat(size - text.length());
        } else if (text.length() > size) {
            LOGGER.warn(
                    "KeyValue 'Key' size is bigger than PADDED_KEY_LENGTH:{} - which breaks the layout. Consider choosing a shorter name or raising PADDED_KEY_LEGNTH",
                    PADDED_KEY_LENGTH);
            return text;
        } else {
            return text;
        }
    }

    /**
     * Gets the key text.
     *
     * @return The key text
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the key text.
     *
     * @param key The new key text
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Gets the key markup.
     *
     * @return The markup used for the key
     */
    public Markup getKeyMarkup() {
        return keyMarkup;
    }

    /**
     * Sets the key markup.
     *
     * @param keyMarkup The new markup for the key
     */
    public void setKeyMarkup(Markup keyMarkup) {
        this.keyMarkup = keyMarkup;
    }

    /**
     * Gets the value text.
     *
     * @return The value text
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value text.
     *
     * @param value The new value text
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value markup.
     *
     * @return The markup used for the value
     */
    public Markup getValueMarkup() {
        return valueMarkup;
    }

    /**
     * Sets the value markup.
     *
     * @param valueMarkup The new markup for the value
     */
    public void setValueMarkup(Markup valueMarkup) {
        this.valueMarkup = valueMarkup;
    }
}
