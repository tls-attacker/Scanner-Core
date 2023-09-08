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

public class ColorEncoding {

    private final HashMap<TestResult, AnsiColor> colorMap;

    public ColorEncoding(HashMap<TestResult, AnsiColor> colorMap) {
        this.colorMap = colorMap;
    }

    public AnsiColor getColor(TestResult result) {
        return colorMap.get(result);
    }

    public String encode(TestResult result, String encodedText) {
        AnsiColor color = this.getColor(result);
        if (color != null && color != AnsiColor.DEFAULT_COLOR) {
            return color.getCode() + encodedText + AnsiColor.RESET.getCode();
        } else {
            return encodedText;
        }
    }
}
