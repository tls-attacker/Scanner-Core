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

/**
 * Container class for a collection of rating influencers. This class manages multiple
 * RatingInfluencer instances and provides methods to retrieve specific property rating influencers
 * based on analyzed properties and test results.
 */
@XmlRootElement(name = "ratingInfluencers")
@XmlAccessorType(XmlAccessType.FIELD)
public class RatingInfluencers implements Serializable {

    @XmlElement(name = "ratingInfluencer")
    private LinkedList<RatingInfluencer> ratingInfluencers;

    /** Private no-arg constructor to please JAXB */
    @SuppressWarnings("unused")
    private RatingInfluencers() {}

    /**
     * Constructs a RatingInfluencers container with the specified list of rating influencers.
     *
     * @param ratingInfluencers the list of rating influencers to manage
     */
    public RatingInfluencers(LinkedList<RatingInfluencer> ratingInfluencers) {
        this.ratingInfluencers = ratingInfluencers;
    }

    /**
     * Gets the list of rating influencers.
     *
     * @return the linked list of rating influencers
     */
    public LinkedList<RatingInfluencer> getRatingInfluencers() {
        return ratingInfluencers;
    }

    /**
     * Sets the list of rating influencers.
     *
     * @param ratingInfluencers the linked list of rating influencers to set
     */
    public void setRatingInfluencers(LinkedList<RatingInfluencer> ratingInfluencers) {
        this.ratingInfluencers = ratingInfluencers;
    }

    /**
     * Gets the property rating influencer for a specific analyzed property and test result. If no
     * matching rating influencer is found, returns a new influencer with zero influence.
     *
     * @param property the analyzed property to search for
     * @param result the test result to match
     * @return the matching property rating influencer, or a new one with zero influence if not
     *     found
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
