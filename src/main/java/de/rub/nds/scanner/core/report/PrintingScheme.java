/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.report;

import de.rub.nds.scanner.core.probe.AnalyzedProperty;
import de.rub.nds.scanner.core.probe.AnalyzedPropertyCategory;
import de.rub.nds.scanner.core.probe.result.TestResult;
import java.util.HashMap;

/**
 * Defines the visual presentation scheme for scan report results, including text encoding and color
 * encoding configurations for different property types and categories.
 */
public class PrintingScheme {

    private HashMap<AnalyzedProperty, ColorEncoding> valueColorEncodings;

    private HashMap<AnalyzedPropertyCategory, TestResultTextEncoder> valueTextEncodings;

    private HashMap<AnalyzedProperty, TestResultTextEncoder> specialValueTextEncoding;

    private HashMap<AnalyzedProperty, Encoder<AnalyzedProperty>> keyTextEncoding;

    private TestResultTextEncoder defaultTextEncoding;

    private ColorEncoding defaultColorEncoding;

    /** Constructs a new PrintingScheme with default settings. */
    public PrintingScheme() {}

    /**
     * Constructs a new PrintingScheme with specified encoding configurations.
     *
     * @param colorEncodings property-specific color encodings
     * @param textEncodings category-specific text encodings
     * @param defaultTextEncoding default text encoder for values without specific encodings
     * @param defaultColorEncoding default color encoder for values without specific encodings
     * @param specialTextEncoding property-specific text encodings that override category encodings
     * @param keyTextEncoding encoders for property keys/names
     */
    public PrintingScheme(
            HashMap<AnalyzedProperty, ColorEncoding> colorEncodings,
            HashMap<AnalyzedPropertyCategory, TestResultTextEncoder> textEncodings,
            TestResultTextEncoder defaultTextEncoding,
            ColorEncoding defaultColorEncoding,
            HashMap<AnalyzedProperty, TestResultTextEncoder> specialTextEncoding,
            HashMap<AnalyzedProperty, Encoder<AnalyzedProperty>> keyTextEncoding) {
        this.valueColorEncodings = colorEncodings;
        this.valueTextEncodings = textEncodings;
        this.defaultTextEncoding = defaultTextEncoding;
        this.defaultColorEncoding = defaultColorEncoding;
        this.specialValueTextEncoding = specialTextEncoding;
        this.keyTextEncoding = keyTextEncoding;
    }

    /**
     * Returns the map of property-specific color encodings.
     *
     * @return map of color encodings for specific properties
     */
    public HashMap<AnalyzedProperty, ColorEncoding> getValueColorEncodings() {
        return valueColorEncodings;
    }

    /**
     * Returns the map of category-specific text encodings.
     *
     * @return map of text encodings for property categories
     */
    public HashMap<AnalyzedPropertyCategory, TestResultTextEncoder> getValueTextEncodings() {
        return valueTextEncodings;
    }

    /**
     * Encodes the result value for a property as a string with optional color formatting.
     *
     * @param report the scan report containing the result
     * @param property the property whose result should be encoded
     * @param useColors whether to apply color encoding to the result
     * @return the encoded string representation of the result
     */
    public String getEncodedString(
            ScanReport report, AnalyzedProperty property, boolean useColors) {
        TestResult result = report.getResult(property);
        TestResultTextEncoder textEncoding = specialValueTextEncoding.get(property);
        if (textEncoding == null) {
            textEncoding =
                    valueTextEncodings.getOrDefault(property.getCategory(), defaultTextEncoding);
        }
        ColorEncoding colorEncoding =
                valueColorEncodings.getOrDefault(property, defaultColorEncoding);
        String encodedText = textEncoding.encode(result);
        if (useColors) {
            return colorEncoding.encode(result, encodedText);
        } else {
            return encodedText;
        }
    }

    /**
     * Encodes the result value for a property as plain text without color formatting.
     *
     * @param report the scan report containing the result
     * @param property the property whose result should be encoded
     * @return the plain text representation of the result
     */
    public String getEncodedValueText(ScanReport report, AnalyzedProperty property) {
        TestResult result = report.getResult(property);
        TestResultTextEncoder textEncoding = specialValueTextEncoding.get(property);
        if (textEncoding == null) {
            textEncoding =
                    valueTextEncodings.getOrDefault(property.getCategory(), defaultTextEncoding);
        }
        return textEncoding.encode(result);
    }

    /**
     * Encodes the property key/name as text.
     *
     * @param report the scan report (currently unused but kept for API consistency)
     * @param property the property whose key should be encoded
     * @return the encoded text representation of the property key
     */
    public String getEncodedKeyText(ScanReport report, AnalyzedProperty property) {
        Encoder<AnalyzedProperty> textEncoding =
                keyTextEncoding.getOrDefault(property, new AnalyzedPropertyTextEncoder(null));

        return textEncoding.encode(property);
    }

    /**
     * Determines the appropriate color for displaying a property's result value.
     *
     * @param report the scan report containing the result
     * @param property the property whose result color should be determined
     * @return the ANSI color to use for the result value
     */
    public AnsiColor getValueColor(ScanReport report, AnalyzedProperty property) {
        TestResult result = report.getResult(property);
        ColorEncoding colorEncoding =
                valueColorEncodings.getOrDefault(property, defaultColorEncoding);
        return colorEncoding.getColor(result);
    }

    /**
     * Determines the color for displaying a property key. Currently always returns the default
     * color.
     *
     * @param report the scan report (currently unused)
     * @param property the property (currently unused)
     * @return the default ANSI color
     */
    public AnsiColor getKeyColor(ScanReport report, AnalyzedProperty property) {
        return AnsiColor.DEFAULT_COLOR;
    }
}
