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

public class PrintingScheme {

    private HashMap<AnalyzedProperty, ColorEncoding> valueColorEncodings;

    private HashMap<AnalyzedPropertyCategory, TestResultTextEncoder> valueTextEncodings;

    private HashMap<AnalyzedProperty, TestResultTextEncoder> specialValueTextEncoding;

    private HashMap<AnalyzedProperty, Encoder<AnalyzedProperty>> keyTextEncoding;

    private TestResultTextEncoder defaultTextEncoding;

    private ColorEncoding defaultColorEncoding;

    public PrintingScheme() {}

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

    public HashMap<AnalyzedProperty, ColorEncoding> getValueColorEncodings() {
        return valueColorEncodings;
    }

    public HashMap<AnalyzedPropertyCategory, TestResultTextEncoder> getValueTextEncodings() {
        return valueTextEncodings;
    }

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

    public String getEncodedValueText(ScanReport report, AnalyzedProperty property) {
        TestResult result = report.getResult(property);
        TestResultTextEncoder textEncoding = specialValueTextEncoding.get(property);
        if (textEncoding == null) {
            textEncoding =
                    valueTextEncodings.getOrDefault(property.getCategory(), defaultTextEncoding);
        }
        return textEncoding.encode(result);
    }

    public String getEncodedKeyText(ScanReport report, AnalyzedProperty property) {
        Encoder<AnalyzedProperty> textEncoding =
                keyTextEncoding.getOrDefault(property, new AnalyzedPropertyTextEncoder(null));

        return textEncoding.encode(property);
    }

    public AnsiColor getValueColor(ScanReport report, AnalyzedProperty property) {
        TestResult result = report.getResult(property);
        ColorEncoding colorEncoding =
                valueColorEncodings.getOrDefault(property, defaultColorEncoding);
        return colorEncoding.getColor(result);
    }

    public AnsiColor getKeyColor(ScanReport report, AnalyzedProperty property) {
        return AnsiColor.DEFAULT_COLOR;
    }
}
