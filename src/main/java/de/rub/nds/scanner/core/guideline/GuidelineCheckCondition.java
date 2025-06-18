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

    /**
     * Creates a new condition that requires all of the provided conditions to be satisfied.
     *
     * @param conditions the list of conditions that must all be satisfied
     * @return a new AND condition
     */
    public static GuidelineCheckCondition and(List<GuidelineCheckCondition> conditions) {
        return new GuidelineCheckCondition(conditions, null);
    }

    /**
     * Creates a new condition that requires at least one of the provided conditions to be satisfied.
     *
     * @param conditions the list of conditions where at least one must be satisfied
     * @return a new OR condition
     */
    public static GuidelineCheckCondition or(List<GuidelineCheckCondition> conditions) {
        return new GuidelineCheckCondition(null, conditions);
    }

    /**
     * Gets the analyzed property that this condition checks.
     *
     * @return the analyzed property
     */
    public AnalyzedProperty getAnalyzedProperty() {
        return analyzedProperty;
    }

    /**
     * Sets the analyzed property that this condition checks.
     *
     * @param analyzedProperty the analyzed property to set
     */
    public void setAnalyzedProperty(AnalyzedProperty analyzedProperty) {
        this.analyzedProperty = analyzedProperty;
    }

    /**
     * Gets the expected test result for the analyzed property.
     *
     * @return the expected test result
     */
    public TestResult getResult() {
        return result;
    }

    /**
     * Gets the list of conditions that must all be satisfied (AND operation).
     *
     * @return an unmodifiable list of AND conditions, or null if this is not an AND condition
     */
    public List<GuidelineCheckCondition> getAnd() {
        return and != null ? Collections.unmodifiableList(and) : null;
    }

    /**
     * Gets the list of conditions where at least one must be satisfied (OR operation).
     *
     * @return an unmodifiable list of OR conditions, or null if this is not an OR condition
     */
    public List<GuidelineCheckCondition> getOr() {
        return or != null ? Collections.unmodifiableList(or) : null;
    }
}
