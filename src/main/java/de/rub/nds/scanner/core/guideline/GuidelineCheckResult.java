/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.guideline;

public class GuidelineCheckResult {

    private String checkName;
    private GuidelineAdherence adherence;
    private String hint;

    public GuidelineCheckResult(String checkName, GuidelineAdherence adherence) {
        this.checkName = checkName;
        this.adherence = adherence;
        this.hint = null;
    }

    public GuidelineCheckResult(String checkName, GuidelineAdherence adherence, String hint) {
        this.checkName = checkName;
        this.adherence = adherence;
        this.hint = hint;
    }

    public String getCheckName() {
        return checkName;
    }

    public void setCheckName(String checkName) {
        this.checkName = checkName;
    }

    public GuidelineAdherence getAdherence() {
        return adherence;
    }

    public void setAdherence(GuidelineAdherence adherence) {
        this.adherence = adherence;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }
}
