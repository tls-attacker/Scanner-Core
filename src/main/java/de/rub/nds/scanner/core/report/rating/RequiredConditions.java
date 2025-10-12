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
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Represents the required conditions that must be met for a conditional recommendation to apply.
 * This class encapsulates a property and its expected result value.
 */
@XmlRootElement(name = "requiredConditions")
@XmlSeeAlso({TestResults.class})
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"property", "result"})
public class RequiredConditions implements Serializable {

    @XmlAnyElement(lax = true)
    private AnalyzedProperty property;

    @XmlElement(type = TestResults.class)
    private TestResult result;

    /** Private no-arg constructor for JAXB */
    public RequiredConditions() {}

    /**
     * Constructs RequiredConditions with the specified property and result.
     *
     * @param property the property to check
     * @param result the expected result value
     */
    public RequiredConditions(AnalyzedProperty property, TestResult result) {
        this.property = property;
        this.result = result;
    }

    /**
     * Gets the property to check.
     *
     * @return the property
     */
    public AnalyzedProperty getProperty() {
        return property;
    }

    /**
     * Sets the property to check.
     *
     * @param property the property to set
     */
    public void setProperty(AnalyzedProperty property) {
        this.property = property;
    }

    /**
     * Gets the expected result value.
     *
     * @return the expected result
     */
    public TestResult getResult() {
        return result;
    }

    /**
     * Sets the expected result value.
     *
     * @param result the expected result to set
     */
    public void setResult(TestResult result) {
        this.result = result;
    }
}
