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

    /**
     * Constructs a PropertyResultRatingInfluencer with the given test result and influence.
     *
     * @param result the test result associated with this rating influencer
     * @param influence the influence value to apply to the rating
     */
    public PropertyResultRatingInfluencer(TestResult result, Integer influence) {
        this.result = result;
        this.influence = influence;
    }

    /**
     * Constructs a PropertyResultRatingInfluencer with a referenced property.
     *
     * @param result the test result associated with this rating influencer
     * @param referencedProperty the property being referenced
     * @param referencedPropertyResult the test result of the referenced property
     */
    public PropertyResultRatingInfluencer(
            TestResult result,
            AnalyzedProperty referencedProperty,
            TestResult referencedPropertyResult) {
        this.result = result;
        this.referencedProperty = referencedProperty;
        this.referencedPropertyResult = referencedPropertyResult;
    }

    /**
     * Constructs a PropertyResultRatingInfluencer with a score cap.
     *
     * @param result the test result associated with this rating influencer
     * @param influence the influence value to apply to the rating
     * @param scoreCap the maximum score that can be influenced
     */
    public PropertyResultRatingInfluencer(TestResult result, Integer influence, Integer scoreCap) {
        this.result = result;
        this.influence = influence;
        this.scoreCap = scoreCap;
    }

    /**
     * Gets the test result associated with this rating influencer.
     *
     * @return the test result
     */
    public TestResult getResult() {
        return result;
    }

    /**
     * Gets the influence value.
     *
     * @return the influence value
     */
    public Integer getInfluence() {
        return influence;
    }

    /**
     * Gets the score cap value.
     *
     * @return the score cap value
     */
    public Integer getScoreCap() {
        return scoreCap;
    }

    /**
     * Checks if this influencer has a non-zero score cap.
     *
     * @return true if the score cap exists and is non-zero, false otherwise
     */
    public boolean hasScoreCap() {
        return scoreCap != null && scoreCap != 0;
    }

    /**
     * Sets the test result.
     *
     * @param result the test result to set
     */
    public void setResult(TestResult result) {
        this.result = result;
    }

    /**
     * Sets the influence value.
     *
     * @param influence the influence value to set
     */
    public void setInfluence(Integer influence) {
        this.influence = influence;
    }

    /**
     * Sets the score cap value.
     *
     * @param scoreCap the score cap value to set
     */
    public void setScoreCap(Integer scoreCap) {
        this.scoreCap = scoreCap;
    }

    /**
     * Gets the referenced property.
     *
     * @return the referenced property
     */
    public AnalyzedProperty getReferencedProperty() {
        return referencedProperty;
    }

    /**
     * Sets the referenced property.
     *
     * @param referencedProperty the referenced property to set
     */
    public void setReferencedProperty(AnalyzedProperty referencedProperty) {
        this.referencedProperty = referencedProperty;
    }

    /**
     * Gets the test result of the referenced property.
     *
     * @return the referenced property test result
     */
    public TestResult getReferencedPropertyResult() {
        return referencedPropertyResult;
    }

    /**
     * Sets the test result of the referenced property.
     *
     * @param referencedPropertyResult the referenced property test result to set
     */
    public void setReferencedPropertyResult(TestResult referencedPropertyResult) {
        this.referencedPropertyResult = referencedPropertyResult;
    }

    /**
     * Checks if this is a bad influence (negative influence or has a score cap).
     *
     * @return true if this has a negative influence or a score cap, false otherwise
     */
    @JsonIgnore
    public boolean isBadInfluence() {
        return influence != null && influence < 0 || scoreCap != null;
    }

    /**
     * Compares this PropertyResultRatingInfluencer with another based on score cap and influence.
     * Objects with score caps are ordered before those without. When both have score caps, they are
     * compared by score cap value. When score caps are equal or both absent, they are compared by
     * influence value.
     *
     * @param t the PropertyResultRatingInfluencer to compare to
     * @return a negative integer, zero, or a positive integer as this object is less than, equal
     *     to, or greater than the specified object
     */
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

    /**
     * Returns a string representation of this PropertyResultRatingInfluencer.
     *
     * @return a string representation of the object
     */
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
