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

/**
 * An encoder that converts AnalyzedProperty instances to their text representation.
 * Uses a provided mapping to translate properties to custom strings.
 */
public class AnalyzedPropertyTextEncoder extends Encoder<AnalyzedProperty> {

    private final HashMap<AnalyzedProperty, String> map;

    /**
     * Creates a new AnalyzedPropertyTextEncoder with the specified property-to-text mapping.
     *
     * @param map a HashMap mapping AnalyzedProperty instances to their string representations
     */
    public AnalyzedPropertyTextEncoder(HashMap<AnalyzedProperty, String> map) {
        this.map = map;
    }

    /**
     * Encodes an AnalyzedProperty to its string representation.
     * If a mapping exists, returns the mapped value; otherwise returns the property's name.
     *
     * @param analyzedProperty the property to encode
     * @return the string representation of the property
     */
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
