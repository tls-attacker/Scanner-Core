/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.guideline;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class GuidelineCheckResult {

    private String checkName;
    private RequirementLevel level;
    private GuidelineAdherence adherence;
    private String hint;

    @SuppressWarnings("unused")
    // Default constructor for deserialization
    private GuidelineCheckResult() {
        this.checkName = null;
        this.level = null;
        this.adherence = null;
        this.hint = null;
    }

    protected GuidelineCheckResult(GuidelineCheck check, GuidelineAdherence adherence) {
        this.checkName = check != null ? check.getName() : null;
        this.level = check != null ? check.getRequirementLevel() : null;
        this.adherence = adherence;
        this.hint = null;
    }

    protected GuidelineCheckResult(
            GuidelineCheck check, GuidelineAdherence adherence, String hint) {
        this.checkName = check.getName();
        this.level = check.getRequirementLevel();
        this.adherence = adherence;
        this.hint = hint;
    }

    /**
     * Gets the name of the guideline check that produced this result.
     *
     * @return the check name
     */
    public String getCheckName() {
        return checkName;
    }

    /**
     * Sets the name of the guideline check that produced this result.
     *
     * @param checkName the check name to set
     */
    public void setCheckName(String checkName) {
        this.checkName = checkName;
    }

    /**
     * Gets the adherence status indicating whether the guideline check passed or failed.
     *
     * @return the adherence status
     */
    public GuidelineAdherence getAdherence() {
        return adherence;
    }

    /**
     * Sets the adherence status indicating whether the guideline check passed or failed.
     *
     * @param adherence the adherence status to set
     */
    public void setAdherence(GuidelineAdherence adherence) {
        this.adherence = adherence;
    }

    /**
     * Gets an optional hint providing additional information about the check result.
     *
     * @return the hint, or null if no hint is provided
     */
    public String getHint() {
        return hint;
    }

    /**
     * Sets an optional hint providing additional information about the check result.
     *
     * @param hint the hint to set
     */
    public void setHint(String hint) {
        this.hint = hint;
    }

    /**
     * Sets the requirement level of the check.
     *
     * @return the requirementLevel to set
     */
    public RequirementLevel getLevel() {
        return level;
    }

    /**
     * Sets the requirement level of the check.
     *
     * @param level the requirementLevel or null if non is set
     */
    public void setLevel(RequirementLevel level) {
        this.level = level;
    }
}
