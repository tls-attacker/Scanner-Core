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
import jakarta.xml.bind.annotation.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a rating influencer for a specific analyzed property. This class manages how different
 * test results for a property affect the overall rating score.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RatingInfluencer {

    @XmlAnyElement(lax = true)
    private AnalyzedProperty analyzedProperty;

    private List<PropertyResultRatingInfluencer> propertyRatingInfluencers;

    /** Constructs an empty RatingInfluencer with an empty list of property rating influencers. */
    public RatingInfluencer() {
        this.propertyRatingInfluencers = new LinkedList<>();
    }

    /**
     * Constructs a RatingInfluencer with the specified analyzed property and list of property
     * rating influencers.
     *
     * @param influencerConstant the analyzed property that this influencer applies to
     * @param propertyRatingInfluencers the list of property result rating influencers
     */
    public RatingInfluencer(
            AnalyzedProperty influencerConstant,
            List<PropertyResultRatingInfluencer> propertyRatingInfluencers) {
        this.analyzedProperty = influencerConstant;
        this.propertyRatingInfluencers = propertyRatingInfluencers;
    }

    /**
     * Constructs a RatingInfluencer with the specified analyzed property and array of property
     * rating influencers.
     *
     * @param influencerConstant the analyzed property that this influencer applies to
     * @param propertyRatingInfluencers the array of property result rating influencers
     */
    public RatingInfluencer(
            AnalyzedProperty influencerConstant,
            PropertyResultRatingInfluencer... propertyRatingInfluencers) {
        this.analyzedProperty = influencerConstant;
        this.propertyRatingInfluencers = Arrays.asList(propertyRatingInfluencers);
    }

    /**
     * Gets the analyzed property that this rating influencer applies to.
     *
     * @return the analyzed property
     */
    public AnalyzedProperty getAnalyzedProperty() {
        return analyzedProperty;
    }

    /**
     * Sets the analyzed property that this rating influencer applies to.
     *
     * @param analyzedProperty the analyzed property to set
     */
    public void setAnalyzedProperty(AnalyzedProperty analyzedProperty) {
        this.analyzedProperty = analyzedProperty;
    }

    /**
     * Gets the list of property result rating influencers.
     *
     * @return the list of property rating influencers
     */
    public List<PropertyResultRatingInfluencer> getPropertyRatingInfluencers() {
        return propertyRatingInfluencers;
    }

    /**
     * Sets the list of property result rating influencers.
     *
     * @param propertyRatingInfluencers the list of property rating influencers to set
     */
    public void setPropertyRatingInfluencers(
            List<PropertyResultRatingInfluencer> propertyRatingInfluencers) {
        this.propertyRatingInfluencers = propertyRatingInfluencers;
    }

    /**
     * Adds a property result rating influencer to the list.
     *
     * @param ratingInfluence the property rating influencer to add
     */
    public void addPropertyRatingInfluencer(PropertyResultRatingInfluencer ratingInfluence) {
        this.propertyRatingInfluencers.add(ratingInfluence);
    }

    /**
     * Gets the property rating influencer for a specific test result. If no influencer is found for
     * the given result, returns a new influencer with zero influence.
     *
     * @param result the test result to find an influencer for
     * @return the matching property rating influencer, or a new one with zero influence if not
     *     found
     */
    public PropertyResultRatingInfluencer getPropertyRatingInfluencer(TestResult result) {
        for (PropertyResultRatingInfluencer ri : propertyRatingInfluencers) {
            if (ri.getResult().equalsExpectedResult(result)) {
                return ri;
            }
        }
        return new PropertyResultRatingInfluencer(result, 0);
    }
}
