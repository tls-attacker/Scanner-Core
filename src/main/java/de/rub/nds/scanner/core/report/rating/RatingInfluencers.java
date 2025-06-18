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
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.LinkedList;

@XmlRootElement(name = "ratingInfluencers")
@XmlAccessorType(XmlAccessType.FIELD)
public class RatingInfluencers implements Serializable {

    @XmlElement(name = "ratingInfluencer")
    private LinkedList<RatingInfluencer> ratingInfluencers;

    /** Private no-arg constructor to please JAXB */
    @SuppressWarnings("unused")
    private RatingInfluencers() {}

    /**
     * Constructs a RatingInfluencers with the specified list of rating influencers.
     *
     * @param ratingInfluencers the list of rating influencers
     */
    public RatingInfluencers(LinkedList<RatingInfluencer> ratingInfluencers) {
        this.ratingInfluencers = ratingInfluencers;
    }

    /**
     * Gets the list of rating influencers.
     *
     * @return the list of rating influencers
     */
    public LinkedList<RatingInfluencer> getRatingInfluencers() {
        return ratingInfluencers;
    }

    /**
     * Sets the rating influencers list.
     *
     * @param ratingInfluencers the list of rating influencers to set
     */
    public void setRatingInfluencers(LinkedList<RatingInfluencer> ratingInfluencers) {
        this.ratingInfluencers = ratingInfluencers;
    }

    /**
     * Gets the property rating influencer for a specific property and test result.
     * If no matching influencer is found, returns a default influencer with 0 influence.
     *
     * @param property the analyzed property to find an influencer for
     * @param result the test result to find an influencer for
     * @return the matching property rating influencer or a default one with 0 influence
     */
    public PropertyResultRatingInfluencer getPropertyRatingInfluencer(
            AnalyzedProperty property, TestResult result) {
        for (RatingInfluencer ri : ratingInfluencers) {
            if (ri.getAnalyzedProperty() == property) {
                return ri.getPropertyRatingInfluencer(result);
            }
        }
        return new PropertyResultRatingInfluencer(result, 0);
    }
}
