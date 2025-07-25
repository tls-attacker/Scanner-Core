/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.guideline;

public class FailedCheckGuidelineResult extends GuidelineCheckResult {

    // Default constructor for deserialization
    @SuppressWarnings("unused")
    private FailedCheckGuidelineResult() {
        super(null, null);
    }

    public FailedCheckGuidelineResult(GuidelineCheck check, GuidelineAdherence adherence) {
        super(check, adherence);
    }

    public FailedCheckGuidelineResult(
            GuidelineCheck check, GuidelineAdherence adherence, String hint) {
        super(check, adherence, hint);
    }

    @Override
    public String toString() {
        return "Failed guideline check: " + getHint();
    }
}
