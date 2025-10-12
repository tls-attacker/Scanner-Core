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
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;

@XmlRootElement
@XmlSeeAlso({TestResults.class})
@XmlAccessorType(XmlAccessType.FIELD)
public class ConditionalRecommendation extends Recommendation {

    private AnalyzedProperty propertyCondition;
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

    // if(conditionResult == True){
    //     if(actualProperty == False){
    //     then go ahead and recommend
    //     }
    // }

}
