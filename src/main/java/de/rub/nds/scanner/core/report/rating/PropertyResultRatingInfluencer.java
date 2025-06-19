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

/**
 * Represents a rating influencer for a specific property result. This class encapsulates how a
 * particular test result affects the overall rating score, including potential score caps and
 * references to other properties.
 */
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
     * Constructs a PropertyResultRatingInfluencer with the specified result and influence.
     *
     * @param result the test result that triggers this influence
     * @param influence the influence value (positive or negative) on the rating score
     */
    public PropertyResultRatingInfluencer(TestResult result, Integer influence) {
        this.result = result;
        this.influence = influence;
    }

    /**
     * Constructs a PropertyResultRatingInfluencer with a reference to another property.
     *
     * @param result the test result that triggers this influence
     * @param referencedProperty the property that this influencer references
     * @param referencedPropertyResult the expected result of the referenced property
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
     * Constructs a PropertyResultRatingInfluencer with the specified result, influence, and score
     * cap.
     *
     * @param result the test result that triggers this influence
     * @param influence the influence value (positive or negative) on the rating score
     * @param scoreCap the maximum score cap when this influencer is applied
     */
    public PropertyResultRatingInfluencer(TestResult result, Integer influence, Integer scoreCap) {
        this.result = result;
        this.influence = influence;
        this.scoreCap = scoreCap;
    }

    /**
     * Gets the test result that triggers this rating influence.
     *
     * @return the test result
     */
    public TestResult getResult() {
        return result;
    }

    /**
     * Gets the influence value that this result has on the rating score.
     *
     * @return the influence value (positive or negative)
     */
    public Integer getInfluence() {
        return influence;
    }

    /**
     * Gets the score cap value. When applied, this caps the maximum possible score.
     *
     * @return the score cap value, or null if no cap is set
     */
    public Integer getScoreCap() {
        return scoreCap;
    }

    /**
     * Checks whether this influencer has a score cap defined.
     *
     * @return true if a score cap is set and is not zero, false otherwise
     */
    public boolean hasScoreCap() {
        return scoreCap != null && scoreCap != 0;
    }

    /**
     * Sets the test result that triggers this rating influence.
     *
     * @param result the test result to set
     */
    public void setResult(TestResult result) {
        this.result = result;
    }

    /**
     * Sets the influence value that this result has on the rating score.
     *
     * @param influence the influence value to set (positive or negative)
     */
    public void setInfluence(Integer influence) {
        this.influence = influence;
    }

    /**
     * Sets the score cap value. When applied, this caps the maximum possible score.
     *
     * @param scoreCap the score cap value to set
     */
    public void setScoreCap(Integer scoreCap) {
        this.scoreCap = scoreCap;
    }

    /**
     * Gets the property that this influencer references.
     *
     * @return the referenced property, or null if no property is referenced
     */
    public AnalyzedProperty getReferencedProperty() {
        return referencedProperty;
    }

    /**
     * Sets the property that this influencer references.
     *
     * @param referencedProperty the property to reference
     */
    public void setReferencedProperty(AnalyzedProperty referencedProperty) {
        this.referencedProperty = referencedProperty;
    }

    /**
     * Gets the expected result of the referenced property.
     *
     * @return the expected test result of the referenced property
     */
    public TestResult getReferencedPropertyResult() {
        return referencedPropertyResult;
    }

    /**
     * Sets the expected result of the referenced property.
     *
     * @param referencedPropertyResult the expected test result to set
     */
    public void setReferencedPropertyResult(TestResult referencedPropertyResult) {
        this.referencedPropertyResult = referencedPropertyResult;
    }

    /**
     * Determines whether this influencer has a negative impact on the rating. An influencer is
     * considered bad if it has a negative influence value or if it sets a score cap.
     *
     * @return true if this is a bad influence, false otherwise
     */
    @JsonIgnore
    public boolean isBadInfluence() {
        return influence != null && influence < 0 || scoreCap != null;
    }

    /**
     * Compares this PropertyResultRatingInfluencer with another based on score cap and influence.
     * Influencers with score caps are ordered before those without. Among influencers with the same
     * score cap status, they are ordered by their influence values.
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
     * @return a string representation containing all field values
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
