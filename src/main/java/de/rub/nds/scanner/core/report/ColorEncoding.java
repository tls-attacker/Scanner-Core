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
 * Manages color mapping and encoding for test results in terminal output.
 *
 * <p>ColorEncoding provides a systematic way to apply ANSI colors to test results based on their
 * values. This enables consistent, visually appealing terminal output where different result types
 * are automatically colored according to predefined rules.
 *
 * <p>The class maintains a mapping between {@link TestResult} objects and {@link AnsiColor} values,
 * allowing automatic color application when rendering results. Colors are applied with proper
 * reset sequences to avoid affecting subsequent output.
 *
 * <p>Key features:
 *
 * <ul>
 *   <li><strong>Automatic Color Application:</strong> Results are automatically colored based on
 *       their value
 *   <li><strong>Safe Reset Handling:</strong> Colors are properly reset after application
 *   <li><strong>Default Color Support:</strong> Graceful handling of unmapped results and default
 *       colors
 *   <li><strong>Flexible Mapping:</strong> Customizable color assignments for different result
 *       types
 * </ul>
 *
 * <p>Common color mapping strategies:
 *
 * <ul>
 *   <li>Success/True results → GREEN
 *   <li>Failure/False results → RED
 *   <li>Warning/Uncertain results → YELLOW
 *   <li>Error results → RED with BOLD
 * </ul>
 *
 * <p>Usage example:
 *
 * <pre>{@code
 * // Create color mapping
 * HashMap<TestResult, AnsiColor> colorMap = new HashMap<>();
 * colorMap.put(TestResults.TRUE, AnsiColor.GREEN);
 * colorMap.put(TestResults.FALSE, AnsiColor.RED);
 * colorMap.put(TestResults.UNCERTAIN, AnsiColor.YELLOW);
 *
 * ColorEncoding encoding = new ColorEncoding(colorMap);
 *
 * // Apply colors to results
 * String greenText = encoding.encode(TestResults.TRUE, "Supported");
 * String redText = encoding.encode(TestResults.FALSE, "Not Supported");
 * String yellowText = encoding.encode(TestResults.UNCERTAIN, "Unknown");
 *
 * // Output: "Supported" in green, "Not Supported" in red, "Unknown" in yellow
 * }</pre>
 *
 * @see AnsiColor
 * @see TestResult
 * @see de.rub.nds.scanner.core.probe.result.TestResults
 */
public class ColorEncoding {

    private final HashMap<TestResult, AnsiColor> colorMap;

    /**
     * Creates a new ColorEncoding with the specified color mapping.
     *
     * @param colorMap a mapping from test results to their corresponding colors, must not be null
     */
    public ColorEncoding(HashMap<TestResult, AnsiColor> colorMap) {
        this.colorMap = colorMap;
    }

    /**
     * Retrieves the color associated with a specific test result.
     *
     * @param result the test result to look up
     * @return the associated AnsiColor, or null if no mapping exists
     */
    public AnsiColor getColor(TestResult result) {
        return colorMap.get(result);
    }

    /**
     * Encodes text with the appropriate color for the given test result.
     *
     * <p>This method applies the color associated with the test result to the provided text,
     * automatically adding the necessary ANSI escape sequences. If no color is mapped to the
     * result, or if the mapped color is DEFAULT_COLOR, the text is returned unchanged.
     *
     * <p>The method ensures proper color reset by automatically appending the RESET sequence
     * after colored text, preventing color bleeding to subsequent output.
     *
     * @param result the test result that determines the color to apply
     * @param encodedText the text to be colored
     * @return the text with appropriate color codes applied, or the original text if no color
     *     should be applied
     */
    public String encode(TestResult result, String encodedText) {
        AnsiColor color = this.getColor(result);
        if (color != null && color != AnsiColor.DEFAULT_COLOR) {
            return color.getCode() + encodedText + AnsiColor.RESET.getCode();
        } else {
            return encodedText;
        }
    }
}
