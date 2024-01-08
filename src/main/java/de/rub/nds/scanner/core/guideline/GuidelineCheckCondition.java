/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.guideline;

import de.rub.nds.scanner.core.probe.AnalyzedProperty;
import de.rub.nds.scanner.core.probe.result.TestResult;
import de.rub.nds.scanner.core.probe.result.TestResults;
import jakarta.xml.bind.annotation.*;
import java.util.Collections;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GuidelineCheckCondition {

    @XmlElement(name = "condition")
    @XmlElementWrapper(name = "and")
    private List<GuidelineCheckCondition> and;

    @XmlElement(name = "condition")
    @XmlElementWrapper(name = "or")
    private List<GuidelineCheckCondition> or;

    @XmlAnyElement(lax = true)
    private AnalyzedProperty analyzedProperty;

    @XmlElement(type = TestResults.class)
    private TestResult result;

    /** Private no-arg constructor to please JAXB */
    @SuppressWarnings("unused")
    private GuidelineCheckCondition() {}

    private GuidelineCheckCondition(
            List<GuidelineCheckCondition> and, List<GuidelineCheckCondition> or) {
        this.and = and;
        this.or = or;
    }

    public GuidelineCheckCondition(AnalyzedProperty analyzedProperty, TestResult result) {
        this.analyzedProperty = analyzedProperty;
        this.result = result;
    }

    public static GuidelineCheckCondition and(List<GuidelineCheckCondition> conditions) {
        return new GuidelineCheckCondition(conditions, null);
    }

    public static GuidelineCheckCondition or(List<GuidelineCheckCondition> conditions) {
        return new GuidelineCheckCondition(null, conditions);
    }

    public AnalyzedProperty getAnalyzedProperty() {
        return analyzedProperty;
    }

    public void setAnalyzedProperty(AnalyzedProperty analyzedProperty) {
        this.analyzedProperty = analyzedProperty;
    }

    public TestResult getResult() {
        return result;
    }

    public List<GuidelineCheckCondition> getAnd() {
        return and != null ? Collections.unmodifiableList(and) : null;
    }

    public List<GuidelineCheckCondition> getOr() {
        return or != null ? Collections.unmodifiableList(or) : null;
    }
}
