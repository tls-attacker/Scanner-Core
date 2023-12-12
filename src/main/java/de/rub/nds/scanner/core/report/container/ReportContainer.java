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

public abstract class ReportContainer {

    private final ScannerDetail detail;

    public ReportContainer(ScannerDetail detail) {
        this.detail = detail;
    }

    public abstract void print(StringBuilder builder, int depth, boolean useColor);

    protected StringBuilder addDepth(StringBuilder builder, int depth) {
        builder.append("  ".repeat(Math.max(0, depth)));
        return builder;
    }

    protected StringBuilder addHeadlineDepth(StringBuilder builder, int depth) {
        builder.append("--".repeat(Math.max(0, depth)));
        if (depth > 0) {
            builder.append("|");
        }
        return builder;
    }

    protected StringBuilder addColor(
            StringBuilder builder, AnsiColor color, String text, boolean useColor) {
        if (useColor) {
            builder.append(color.getCode()).append(text).append(AnsiColor.RESET.getCode());
        } else {
            builder.append(text);
        }
        return builder;
    }

    public ScannerDetail getDetail() {
        return detail;
    }
}
