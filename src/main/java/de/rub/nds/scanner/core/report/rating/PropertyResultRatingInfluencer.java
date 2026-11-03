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
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a rating influencer for a specific property result. This class encapsulates how a
 * particular test result affects the individual scores, using one {@link ScoreModifier} per
 * affected score, and optional references to other properties.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"result", "modifiers", "referencedProperty", "referencedPropertyResult"})
public class PropertyResultRatingInfluencer implements Serializable {

    @XmlElement(type = TestResults.class, name = "result")
    @JsonIgnore
    private TestResult result;

    @XmlElement(name = "modifier")
    private List<ScoreModifier> modifiers = new LinkedList<>();

    @XmlAnyElement(lax = true)
    private AnalyzedProperty referencedProperty;

    @XmlElement(type = TestResults.class, name = "referencedPropertyResult")
    @JsonIgnore
    private TestResult referencedPropertyResult;

    /** Private no-arg constructor to please JAXB */
    @SuppressWarnings("unused")
    private PropertyResultRatingInfluencer() {}

    /**
     * Constructs a PropertyResultRatingInfluencer with the specified result and score modifiers.
     * Scores without a modifier are not influenced by the result.
     *
     * @param result the test result that triggers this influence
     * @param modifiers the modifiers of the scores influenced by the result
     */
    public PropertyResultRatingInfluencer(TestResult result, ScoreModifier... modifiers) {
        this(result, Arrays.asList(modifiers));
    }

    /**
     * Constructs a PropertyResultRatingInfluencer with the specified result and score modifiers.
     * Scores without a modifier are not influenced by the result.
     *
     * @param result the test result that triggers this influence
     * @param modifiers the modifiers of the scores influenced by the result
     */
    public PropertyResultRatingInfluencer(TestResult result, List<ScoreModifier> modifiers) {
        this.result = result;
        this.modifiers = new LinkedList<>(modifiers);
    }

    /**
     * Constructs a PropertyResultRatingInfluencer with the specified impact on a single score. An
     * impact of {@link RatingImpact#NONE} does not add a modifier, so the result does not influence
     * any score.
     *
     * @param result the test result that triggers this influence
     * @param scoreType the score influenced by the result
     * @param impact the impact on the score
     */
    public PropertyResultRatingInfluencer(
            TestResult result, ScoreType scoreType, RatingImpact impact) {
        this.result = result;
        if (impact != RatingImpact.NONE) {
            modifiers.add(new ScoreModifier(scoreType, impact));
        }
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
     * Returns a comparator ordering influencers by their impact on the given score. Influencers
     * capping the score are ordered before those without a cap, lower caps first. Among influencers
     * with the same cap, they are ordered by their influence values.
     *
     * @param scoreType the score to compare the influencers by
     * @return the comparator
     */
    public static Comparator<PropertyResultRatingInfluencer> comparing(ScoreType scoreType) {
        return Comparator.comparing(
                        (PropertyResultRatingInfluencer influencer) ->
                                influencer.getScoreCap(scoreType),
                        Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparingInt(influencer -> influencer.getInfluence(scoreType));
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
     * Sets the test result that triggers this rating influence.
     *
     * @param result the test result to set
     */
    public void setResult(TestResult result) {
        this.result = result;
    }

    /**
     * Gets the modifiers of all scores influenced by the result.
     *
     * @return the score modifiers
     */
    public List<ScoreModifier> getModifiers() {
        return modifiers;
    }

    /**
     * Sets the modifiers of all scores influenced by the result.
     *
     * @param modifiers the score modifiers to set
     */
    public void setModifiers(List<ScoreModifier> modifiers) {
        this.modifiers = modifiers;
    }

    /**
     * Adds a score modifier.
     *
     * @param modifier the score modifier to add
     */
    public void addModifier(ScoreModifier modifier) {
        this.modifiers.add(modifier);
    }

    /**
     * Gets the modifier of the given score.
     *
     * @param scoreType the score to get the modifier for
     * @return the modifier of the score, or an empty optional if the score is not influenced
     */
    public Optional<ScoreModifier> getModifier(ScoreType scoreType) {
        return modifiers.stream()
                .filter(modifier -> Objects.equals(modifier.getScoreType(), scoreType))
                .findFirst();
    }

    /**
     * Gets the influence value that this result has on the given score.
     *
     * @param scoreType the score to get the influence for
     * @return the influence value (positive or negative), or 0 if the score is not influenced
     */
    public int getInfluence(ScoreType scoreType) {
        return getModifier(scoreType).map(ScoreModifier::getInfluence).orElse(0);
    }

    /**
     * Gets the sum of the influences on all scores. This weighs up the influences of a result that,
     * for example, harms the security but improves the interoperability.
     *
     * @return the summed influence values (positive or negative), or 0 if no score is influenced
     */
    @JsonIgnore
    public int getTotalInfluence() {
        return modifiers.stream()
                .map(ScoreModifier::getInfluence)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();
    }

    /**
     * Gets the score cap value of the given score. When applied, this caps the maximum possible
     * score.
     *
     * @param scoreType the score to get the cap for
     * @return the score cap value, or null if no cap is set
     */
    public Integer getScoreCap(ScoreType scoreType) {
        return getModifier(scoreType).map(ScoreModifier::getScoreCap).orElse(null);
    }

    /**
     * Checks whether this influencer has a score cap defined for the given score.
     *
     * @param scoreType the score to check
     * @return true if a score cap is set and is not zero, false otherwise
     */
    public boolean hasScoreCap(ScoreType scoreType) {
        return getModifier(scoreType).map(ScoreModifier::hasScoreCap).orElse(false);
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
     * Determines whether this influencer has a negative impact on any score.
     *
     * @return true if at least one modifier is a bad influence, false otherwise
     * @see ScoreModifier#isBadInfluence()
     */
    @JsonIgnore
    public boolean isBadInfluence() {
        return modifiers.stream().anyMatch(ScoreModifier::isBadInfluence);
    }

    /**
     * Determines whether this influencer has a negative impact on the given score.
     *
     * @param scoreType the score to check
     * @return true if the modifier of the score is a bad influence, false otherwise
     * @see ScoreModifier#isBadInfluence()
     */
    public boolean isBadInfluence(ScoreType scoreType) {
        return getModifier(scoreType).map(ScoreModifier::isBadInfluence).orElse(false);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PropertyResultRatingInfluencer that = (PropertyResultRatingInfluencer) obj;
        return Objects.equals(new HashSet<>(modifiers), new HashSet<>(that.modifiers));
    }

    @Override
    public int hashCode() {
        return new HashSet<>(modifiers).hashCode();
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
                + ", modifiers="
                + modifiers
                + ", referencedProperty="
                + referencedProperty
                + ", referencedPropertyResult="
                + referencedPropertyResult
                + '}';
    }
}
