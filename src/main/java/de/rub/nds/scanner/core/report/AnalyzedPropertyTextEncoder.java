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
import java.util.HashMap;

public class AnalyzedPropertyTextEncoder extends Encoder<AnalyzedProperty> {

    private final HashMap<AnalyzedProperty, String> map;

    public AnalyzedPropertyTextEncoder(HashMap<AnalyzedProperty, String> map) {
        this.map = map;
    }

    @Override
    public String encode(AnalyzedProperty analyzedProperty) {
        if (map == null) {
            return analyzedProperty.getName();
        } else {
            String name = map.get(analyzedProperty);
            if (name == null) {
                return analyzedProperty.getName();
            } else {
                return name;
            }
        }
    }
}
