/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.report.rating;

import de.rub.nds.scanner.core.probe.result.TestResult;
import de.rub.nds.scanner.core.probe.result.TestResults;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;

/**
 * Represents a conditional property result recommendation that includes conditions under which the
 * recommendation applies. This class extends PropertyResultRecommendation to add conditional logic
 * based on other properties.
 */
@XmlRootElement(name = "conditionalPropertyResultRecommendation")
@XmlSeeAlso({TestResults.class, RequiredConditions.class})
@XmlAccessorType(XmlAccessType.FIELD)
public class ConditionalPropertyResultRecommendation extends PropertyResultRecommendation {

    private RequiredConditions requiredConditions;

    /** Private no-arg constructor for JAXB */
    public ConditionalPropertyResultRecommendation() {
        super();
    }

    /**
     * Constructs a ConditionalPropertyResultRecommendation with required conditions.
     *
     * @param result the test result that this recommendation applies to
     * @param resultStatus a short description of the result status
     * @param handlingRecommendation the recommended action for handling this result
     * @param requiredConditions the conditions that must be met for this recommendation to apply
     */
    public ConditionalPropertyResultRecommendation(
            TestResult result,
            String resultStatus,
            String handlingRecommendation,
            RequiredConditions requiredConditions) {
        super(result, resultStatus, handlingRecommendation);
        this.requiredConditions = requiredConditions;
    }

    /**
     * Constructs a ConditionalPropertyResultRecommendation with detailed description and required
     * conditions.
     *
     * @param result the test result that this recommendation applies to
     * @param resultStatus a short description of the result status
     * @param handlingRecommendation the recommended action for handling this result
     * @param detailedDescription a detailed explanation of the result and recommendation
     * @param requiredConditions the conditions that must be met for this recommendation to apply
     */
    public ConditionalPropertyResultRecommendation(
            TestResult result,
            String resultStatus,
            String handlingRecommendation,
            String detailedDescription,
            RequiredConditions requiredConditions) {
        super(result, resultStatus, handlingRecommendation, detailedDescription);
        this.requiredConditions = requiredConditions;
    }

    /**
     * Gets the required conditions for this recommendation.
     *
     * @return the required conditions
     */
    public RequiredConditions getRequiredConditions() {
        return requiredConditions;
    }

    /**
     * Sets the required conditions for this recommendation.
     *
     * @param requiredConditions the required conditions to set
     */
    public void setRequiredConditions(RequiredConditions requiredConditions) {
        this.requiredConditions = requiredConditions;
    }
}
