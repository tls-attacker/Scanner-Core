/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.guideline;

import de.rub.nds.scanner.core.report.ScanReport;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract class GuidelineCheck<ReportT extends ScanReport> {

    private static final Logger LOGGER = LogManager.getLogger();

    private String name;

    private RequirementLevel requirementLevel;

    private GuidelineCheckCondition condition;

    /** Private no-arg constructor to please JAXB */
    @SuppressWarnings("unused")
    private GuidelineCheck() {}

    public GuidelineCheck(String name, RequirementLevel requirementLevel) {
        this(name, requirementLevel, null);
    }

    public GuidelineCheck(
            String name, RequirementLevel requirementLevel, GuidelineCheckCondition condition) {
        this.name = name;
        this.requirementLevel = requirementLevel;
        this.condition = condition;
    }

    /**
     * Evaluates this guideline check against the provided report.
     *
     * @param report the scan report to evaluate
     * @return the result of the guideline check evaluation
     */
    public abstract GuidelineCheckResult evaluate(ReportT report);

    /**
     * Checks if the report satisfies the condition required for this guideline check.
     *
     * @param report the scan report to check against the condition
     * @return true if the condition is satisfied or no condition is set, false otherwise
     */
    public boolean passesCondition(ReportT report) {
        return this.passesCondition(report, this.condition);
    }

    private boolean passesCondition(ReportT report, GuidelineCheckCondition condition) {
        if (condition == null) {
            return true;
        }
        if (condition.getAnd() != null) {
            for (GuidelineCheckCondition andCondition : condition.getAnd()) {
                if (!this.passesCondition(report, andCondition)) {
                    return false;
                }
            }
            return true;
        } else if (condition.getOr() != null) {
            for (GuidelineCheckCondition orCondition : condition.getOr()) {
                if (this.passesCondition(report, orCondition)) {
                    return true;
                }
            }
            return false;
        } else if (condition.getAnalyzedProperty() != null && condition.getResult() != null) {
            return condition.getResult().equals(report.getResult(condition.getAnalyzedProperty()));
        }
        LOGGER.warn("Invalid condition object.");
        return false;
    }

    /**
     * Gets the name of this guideline check.
     *
     * @return the check name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the requirement level (e.g., MUST, SHOULD) for this guideline check.
     *
     * @return the requirement level
     */
    public RequirementLevel getRequirementLevel() {
        return requirementLevel;
    }

    /**
     * Gets the condition that must be satisfied for this check to be applicable.
     *
     * @return the condition, or null if no condition is set
     */
    public GuidelineCheckCondition getCondition() {
        return condition;
    }
}
