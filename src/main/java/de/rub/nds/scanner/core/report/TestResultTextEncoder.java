/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.report;

import de.rub.nds.scanner.core.probe.result.TestResult;
import java.util.HashMap;

/**
 * Encoder that converts TestResult objects to text representations using a configurable mapping. If
 * no mapping is provided or no mapping exists for a result, falls back to the result's name.
 */
public class TestResultTextEncoder extends Encoder<TestResult> {

    private HashMap<TestResult, String> textEncodingMap = null;

    /**
     * Constructs a new TestResultTextEncoder with no text mappings. Results will be encoded using
     * their getName() method.
     */
    public TestResultTextEncoder() {}

    /**
     * Constructs a new TestResultTextEncoder with the specified text mappings.
     *
     * @param textEncodingMap map of TestResult objects to their text representations
     */
    public TestResultTextEncoder(HashMap<TestResult, String> textEncodingMap) {
        this.textEncodingMap = textEncodingMap;
    }

    /**
     * Returns the text encoding map used by this encoder.
     *
     * @return the map of TestResult to text mappings, or null if no map is set
     */
    public HashMap<TestResult, String> getTextEncodingMap() {
        return textEncodingMap;
    }

    /**
     * Encodes a TestResult to its text representation. Uses the text encoding map if available and
     * contains a mapping for the result, otherwise returns the result's name.
     *
     * @param result the TestResult to encode
     * @return the text representation of the result
     */
    @Override
    public String encode(TestResult result) {
        if (textEncodingMap == null) {
            return result.getName();
        }
        String string = textEncodingMap.get(result);
        if (string == null) {
            return result.getName();
        } else {
            return string;
        }
    }
}
