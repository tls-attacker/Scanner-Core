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
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;

/**
 * Represents a recommendation for a specific property test result. This class encapsulates
 * recommendations about how to handle specific test results, including short descriptions,
 * handling recommendations, and detailed explanations.
 */
@XmlRootElement
@XmlSeeAlso({TestResults.class})
@XmlAccessorType(XmlAccessType.FIELD)
public class PropertyResultRecommendation {

    @XmlAnyElement(lax = true)
    private TestResult result;

    private String shortDescription;

    private String handlingRecommendation;

    private String detailedDescription;

    /** Private no-arg constructor to please JAXB */
    @SuppressWarnings("unused")
    private PropertyResultRecommendation() {}

    /**
     * Constructs a PropertyResultRecommendation with the specified result and recommendations.
     *
     * @param result the test result that this recommendation applies to
     * @param resultStatus a short description of the result status
     * @param handlingRecommendation the recommended action for handling this result
     */
    public PropertyResultRecommendation(
            TestResult result, String resultStatus, String handlingRecommendation) {
        this.result = result;
        this.shortDescription = resultStatus;
        this.handlingRecommendation = handlingRecommendation;
    }

    /**
     * Constructs a PropertyResultRecommendation with the specified result, recommendations, and detailed description.
     *
     * @param result the test result that this recommendation applies to
     * @param resultStatus a short description of the result status
     * @param handlingRecommendation the recommended action for handling this result
     * @param detailedDescription a detailed explanation of the result and recommendation
     */
    public PropertyResultRecommendation(
            TestResult result,
            String resultStatus,
            String handlingRecommendation,
            String detailedDescription) {
        this(result, resultStatus, handlingRecommendation);
        this.detailedDescription = detailedDescription;
    }

    /**
     * Gets the test result that this recommendation applies to.
     *
     * @return the test result
     */
    public TestResult getResult() {
        return result;
    }

    /**
     * Sets the test result that this recommendation applies to.
     *
     * @param result the test result to set
     */
    public void setResult(TestResult result) {
        this.result = result;
    }

    /**
     * Gets the short description of the result status.
     *
     * @return the short description
     */
    public String getShortDescription() {
        return shortDescription;
    }

    /**
     * Sets the short description of the result status.
     *
     * @param shortDescription the short description to set
     */
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    /**
     * Gets the recommended action for handling this result.
     *
     * @return the handling recommendation
     */
    public String getHandlingRecommendation() {
        return handlingRecommendation;
    }

    /**
     * Sets the recommended action for handling this result.
     *
     * @param handlingRecommendation the handling recommendation to set
     */
    public void setHandlingRecommendation(String handlingRecommendation) {
        this.handlingRecommendation = handlingRecommendation;
    }

    /**
     * Gets the detailed explanation of the result and recommendation.
     *
     * @return the detailed description, or null if not set
     */
    public String getDetailedDescription() {
        return detailedDescription;
    }

    /**
     * Sets the detailed explanation of the result and recommendation.
     *
     * @param detailedDescription the detailed description to set
     */
    public void setDetailedDescription(String detailedDescription) {
        this.detailedDescription = detailedDescription;
    }
}
