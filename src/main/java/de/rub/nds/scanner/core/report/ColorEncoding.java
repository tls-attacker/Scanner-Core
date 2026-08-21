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
import de.rub.nds.scanner.core.report.markup.SemanticMarkup;
import java.util.HashMap;

/**
 * Handles color encoding for test results in terminal output. Maps test results to Semantic colors
 * for visual differentiation.
 */
public class ColorEncoding {

    private final HashMap<TestResult, SemanticMarkup> colorMap;

    /**
     * Creates a new ColorEncoding with the specified result-to-color mapping.
     *
     * @param colorMap a HashMap mapping TestResult instances to SemanticMarkup codes
     */
    public ColorEncoding(HashMap<TestResult, SemanticMarkup> colorMap) {
        this.colorMap = colorMap;
    }

    /**
     * Returns the SemanticMarkup associated with a given test result.
     *
     * @param result the test result
     * @return the associated SemanticMarkup, or null if no mapping exists
     */
    public SemanticMarkup getColor(TestResult result) {
        return colorMap.get(result);
    }

    /**
     * Encodes the given text with the color associated with the test result. If a color mapping
     * exists and is not DEFAULT_COLOR, wraps the text with appropriate ANSI codes.
     *
     * @param result the test result determining the color
     * @param encodedText the text to be colored
     * @return the color-encoded text string
     */
    public String encode(TestResult result, String encodedText) {
        SemanticMarkup color = this.getColor(result);
        if (color != null && color != SemanticMarkup.NEUTRAL) {
            return color.applyAnsi(encodedText);
        } else {
            return encodedText;
        }
    }
}
