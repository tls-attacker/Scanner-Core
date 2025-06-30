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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class KeyValueContainer extends ReportContainer {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final int PADDED_KEY_LENGTH = 30;

    private String key;
    private AnsiColor keyColor;

    private String value;
    private AnsiColor valueColor;

    public KeyValueContainer(String key, AnsiColor keyColor, String value, AnsiColor valueColor) {
        super(ScannerDetail.NORMAL);
        this.key = key;
        this.keyColor = keyColor;
        this.value = value;
        this.valueColor = valueColor;
    }

    @Override
    public void print(StringBuilder builder, int depth, boolean useColor) {
        addDepth(builder, depth);
        addColor(builder, keyColor, pad(key, PADDED_KEY_LENGTH), useColor);
        builder.append(":    "); // $NON-NLS-1$
        addColor(builder, valueColor, value, useColor);
        builder.append("\n"); // $NON-NLS-1$
    }

    private String pad(String text, int size) {
        if (text == null) {
            text = ""; // $NON-NLS-1$
        }
        if (text.length() < size) {
            return text + " ".repeat(size - text.length()); // $NON-NLS-1$
        } else if (text.length() > size) {
            LOGGER.warn(
                    "KeyValue 'Key' size is bigger than PADDED_KEY_LENGTH:{} - which breaks the layout. Consider choosing a shorter name or raising PADDED_KEY_LEGNTH",
                    PADDED_KEY_LENGTH);
            return text;
        } else {
            return text;
        }
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public AnsiColor getKeyColor() {
        return keyColor;
    }

    public void setKeyColor(AnsiColor keyColor) {
        this.keyColor = keyColor;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public AnsiColor getValueColor() {
        return valueColor;
    }

    public void setValueColor(AnsiColor valueColor) {
        this.valueColor = valueColor;
    }
}
