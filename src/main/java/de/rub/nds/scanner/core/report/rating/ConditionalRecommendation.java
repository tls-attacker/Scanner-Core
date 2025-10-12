/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.report.rating;

import de.rub.nds.scanner.core.probe.AnalyzedProperty;
import de.rub.nds.scanner.core.probe.result.TestResult;
import de.rub.nds.scanner.core.probe.result.TestResults;
import de.rub.nds.scanner.core.report.ScanReport;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;

@XmlRootElement
@XmlSeeAlso({TestResults.class})
@XmlAccessorType(XmlAccessType.FIELD)
public class ConditionalRecommendation extends Recommendation {

    @XmlAnyElement(lax = true)
    private AnalyzedProperty propertyCondition;

    @XmlElement(type = TestResults.class)
    private TestResult conditionResult;

    public ConditionalRecommendation() {
        super();
    }

    public ConditionalRecommendation(
            AnalyzedProperty propertyCondition,
            TestResult conditionResult,
            AnalyzedProperty actualProperty,
            String shortName,
            String shortDescription,
            PropertyResultRecommendation propertyRecommendation,
            String... links) {
        super(actualProperty, shortName, shortDescription, propertyRecommendation, links);
        this.propertyCondition = propertyCondition;
        this.conditionResult = conditionResult;
    }

    public AnalyzedProperty getPropertyCondition() {
        return propertyCondition;
    }

    public void setPropertyCondition(AnalyzedProperty propertyCondition) {
        this.propertyCondition = propertyCondition;
    }

    public TestResult getConditionResult() {
        return conditionResult;
    }

    public void setConditionResult(TestResult conditionResult) {
        this.conditionResult = conditionResult;
    }

    @Override
    public PropertyResultRecommendation getPropertyResultRecommendation(
            TestResult result, ScanReport report) {
        // Check if the condition is satisfied by looking up the condition property in the report
        TestResult actualConditionResult = report.getResult(propertyCondition);

        // If the condition is not met, don't provide a recommendation
        if (actualConditionResult != conditionResult) {
            return new PropertyResultRecommendation(
                    result, NO_INFORMATION_FOUND, NO_RECOMMENDATION_FOUND);
        }

        // If the condition is met, return the normal recommendation
        return super.getPropertyResultRecommendation(result);
    }
}
