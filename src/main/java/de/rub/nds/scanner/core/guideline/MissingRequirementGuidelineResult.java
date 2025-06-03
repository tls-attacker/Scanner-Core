/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.guideline;

public class MissingRequirementGuidelineResult extends GuidelineCheckResult {

    // Default constructor for deserialization
    @SuppressWarnings("unused")
    private MissingRequirementGuidelineResult() {
        // Default constructor for deserialization
        super(null , null);
    }

    public MissingRequirementGuidelineResult(String checkName, GuidelineAdherence adherence) {
        super(checkName, adherence);
    }

    public MissingRequirementGuidelineResult(
            String checkName, GuidelineAdherence adherence, String hint) {
        super(checkName, adherence, hint);
    }
}
