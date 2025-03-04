/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.report.rating;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.rub.nds.scanner.core.probe.AnalyzedProperty;
import de.rub.nds.scanner.core.probe.result.TestResult;
import de.rub.nds.scanner.core.probe.result.TestResults;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.Objects;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        propOrder = {
            "result",
            "influence",
            "scoreCap",
            "referencedProperty",
            "referencedPropertyResult"
        })
public class PropertyResultRatingInfluencer implements Comparable<PropertyResultRatingInfluencer> {

    @XmlElement(type = TestResults.class, name = "result")
    @JsonIgnore
    private TestResult result;

    private Integer influence;

    private Integer scoreCap;

    @XmlAnyElement(lax = true)
    private AnalyzedProperty referencedProperty;

    @XmlElement(type = TestResults.class, name = "referencedPropertyResult")
    @JsonIgnore
    private TestResult referencedPropertyResult;

    /** Private no-arg constructor to please JAXB */
    @SuppressWarnings("unused")
    private PropertyResultRatingInfluencer() {}

    public PropertyResultRatingInfluencer(TestResult result, Integer influence) {
        this.result = result;
        this.influence = influence;
    }

    public PropertyResultRatingInfluencer(
            TestResult result,
            AnalyzedProperty referencedProperty,
            TestResult referencedPropertyResult) {
        this.result = result;
        this.referencedProperty = referencedProperty;
        this.referencedPropertyResult = referencedPropertyResult;
    }

    public PropertyResultRatingInfluencer(TestResult result, Integer influence, Integer scoreCap) {
        this.result = result;
        this.influence = influence;
        this.scoreCap = scoreCap;
    }

    public TestResult getResult() {
        return result;
    }

    public Integer getInfluence() {
        return influence;
    }

    public Integer getScoreCap() {
        return scoreCap;
    }

    public boolean hasScoreCap() {
        return (scoreCap != null && scoreCap != 0);
    }

    public void setResult(TestResult result) {
        this.result = result;
    }

    public void setInfluence(Integer influence) {
        this.influence = influence;
    }

    public void setScoreCap(Integer scoreCap) {
        this.scoreCap = scoreCap;
    }

    public AnalyzedProperty getReferencedProperty() {
        return referencedProperty;
    }

    public void setReferencedProperty(AnalyzedProperty referencedProperty) {
        this.referencedProperty = referencedProperty;
    }

    public TestResult getReferencedPropertyResult() {
        return referencedPropertyResult;
    }

    public void setReferencedPropertyResult(TestResult referencedPropertyResult) {
        this.referencedPropertyResult = referencedPropertyResult;
    }

    @JsonIgnore
    public boolean isBadInfluence() {
        return (influence != null && influence < 0 || scoreCap != null);
    }

    @Override
    public int compareTo(PropertyResultRatingInfluencer t) {
        if (Objects.equals(this.getScoreCap(), t.getScoreCap())) {
            return Integer.compare(this.getInfluence(), t.getInfluence());
        }
        if (this.getScoreCap() != null && t.getScoreCap() == null) {
            return -1;
        }
        if (t.getScoreCap() != null && this.getScoreCap() == null) {
            return 1;
        }
        return this.getScoreCap().compareTo(t.getScoreCap());
    }

    @Override
    public String toString() {
        return "PropertyResultRatingInfluencer{"
                + "result="
                + result
                + ", influence="
                + influence
                + ", scoreCap="
                + scoreCap
                + ", referencedProperty="
                + referencedProperty
                + ", referencedPropertyResult="
                + referencedPropertyResult
                + '}';
    }
}
