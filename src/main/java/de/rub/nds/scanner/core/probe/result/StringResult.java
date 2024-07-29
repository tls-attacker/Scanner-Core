/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.probe.result;

import de.rub.nds.scanner.core.probe.AnalyzedProperty;

public class StringResult extends ObjectResult<String> {
    public StringResult(AnalyzedProperty property, String value) {
        super(property, value);
    }

    @Override
    public String toString() {
        return "" + value + "(" + getProperty().getName() + ")";
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        StringResult otherResult = (StringResult) otherObject;
        return value.equals(otherResult.value) && getProperty().equals(otherResult.getProperty());
    }

    @Override
    public int hashCode() {
        return value.hashCode() + getProperty().hashCode();
    }

}
