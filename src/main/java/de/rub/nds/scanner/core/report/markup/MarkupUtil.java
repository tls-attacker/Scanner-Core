/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.report.markup;

public class MarkupUtil {
    private static class _None implements Markup {
        @Override
        public String applyAnsi(String text) {
            return text;
        }

        @Override
        public StringBuilder applyAnsi(StringBuilder builder, String... texts) {
            for (String text : texts) {
                builder.append(text);
            }
            return builder;
        }
    }

    public static final Markup NONE = new _None();
}
