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
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Objects;

/**
 * Describes how a property result affects a single score. The influence is added to the score of
 * the given score type, and the optional score cap limits the maximum score that can be reached.
 */
@XmlRootElement(name = "modifier")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"scoreType", "influence", "scoreCap"})
public class ScoreModifier implements Serializable {

    @XmlAnyElement(lax = true)
    private ScoreType scoreType;

    private Integer influence;

    private Integer scoreCap;

    /** Private no-arg constructor to please JAXB */
    @SuppressWarnings("unused")
    private ScoreModifier() {}

    /**
     * Constructs a ScoreModifier with the specified influence on the given score.
     *
     * @param scoreType the score this modifier applies to
     * @param influence the influence value (positive or negative) on the score
     */
    public ScoreModifier(ScoreType scoreType, Integer influence) {
        this(scoreType, influence, null);
    }

    /**
     * Constructs a ScoreModifier with the specified influence and score cap on the given score.
     *
     * @param scoreType the score this modifier applies to
     * @param influence the influence value (positive or negative) on the score
     * @param scoreCap the maximum score when this modifier is applied, or null if not capped
     */
    public ScoreModifier(ScoreType scoreType, Integer influence, Integer scoreCap) {
        this.scoreType = scoreType;
        this.influence = influence;
        this.scoreCap = scoreCap;
    }

    /**
     * Constructs a ScoreModifier with the influence and score cap of the specified rating impact.
     *
     * @param scoreType the score this modifier applies to
     * @param impact the rating impact defining the influence and score cap
     */
    public ScoreModifier(ScoreType scoreType, RatingImpact impact) {
        this(scoreType, impact.getInfluence(), impact.getScoreCap());
    }

    /**
     * Gets the score this modifier applies to.
     *
     * @return the score type
     */
    public ScoreType getScoreType() {
        return scoreType;
    }

    /**
     * Sets the score this modifier applies to.
     *
     * @param scoreType the score type to set
     */
    public void setScoreType(ScoreType scoreType) {
        this.scoreType = scoreType;
    }

    /**
     * Gets the influence value that this modifier has on the score.
     *
     * @return the influence value (positive or negative)
     */
    public Integer getInfluence() {
        return influence;
    }

    /**
     * Sets the influence value that this modifier has on the score.
     *
     * @param influence the influence value to set (positive or negative)
     */
    public void setInfluence(Integer influence) {
        this.influence = influence;
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
     * Sets the score cap value. When applied, this caps the maximum possible score.
     *
     * @param scoreCap the score cap value to set
     */
    public void setScoreCap(Integer scoreCap) {
        this.scoreCap = scoreCap;
    }

    /**
     * Checks whether this modifier has a score cap defined.
     *
     * @return true if a score cap is set and is not zero, false otherwise
     */
    public boolean hasScoreCap() {
        return scoreCap != null && scoreCap != 0;
    }

    /**
     * Determines whether this modifier has a negative impact on the score. A modifier is considered
     * bad if it has a negative influence value or if it sets a score cap.
     *
     * @return true if this is a bad influence, false otherwise
     */
    @JsonIgnore
    public boolean isBadInfluence() {
        return influence != null && influence < 0 || scoreCap != null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ScoreModifier that = (ScoreModifier) obj;
        return Objects.equals(scoreType, that.scoreType)
                && Objects.equals(influence, that.influence)
                && Objects.equals(scoreCap, that.scoreCap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scoreType, influence, scoreCap);
    }

    /**
     * Returns a string representation of this ScoreModifier.
     *
     * @return a string representation containing all field values
     */
    @Override
    public String toString() {
        return "ScoreModifier{"
                + "scoreType="
                + scoreType
                + ", influence="
                + influence
                + ", scoreCap="
                + scoreCap
                + '}';
    }
}
