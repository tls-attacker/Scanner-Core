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

@JsonIncludeProperties({"type", "reason"})
@JsonPropertyOrder({"type", "reason"})
public class NotApplicableResult implements TestResult {

    private final AnalyzedProperty property;
    private final String reason;

    @SuppressWarnings("unused")
    private NotApplicableResult() {
        // Default constructor for deserialization
        this.property = null;
        this.reason = null;
    }

    /**
     * Constructs a NotApplicableResult with the specified property and reason.
     *
     * @param property the analyzed property associated with this result
     * @param reason the reason why the test is not applicable
     */
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

    /**
     * Returns the reason why the test is not applicable.
     *
     * @return the reason string
     */
    public String getReason() {
        return reason;
    }
}
