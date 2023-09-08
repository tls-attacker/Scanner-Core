/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.probe.result;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.rub.nds.scanner.core.probe.AnalyzedProperty;
import java.io.Serializable;

@JsonIncludeProperties({"type", "reason"})
@JsonPropertyOrder({"type", "reason"})
public class NotApplicableResult implements TestResult, Serializable {

    private final AnalyzedProperty property;
    private final String reason;

    public NotApplicableResult(AnalyzedProperty property, String reason) {
        this.property = property;
        this.reason = reason;
    }

    @Override
    public String getName() {
        return property.getName();
    }

    @Override
    public boolean isRealResult() {
        return false;
    }

    public String getReason() {
        return reason;
    }
}
