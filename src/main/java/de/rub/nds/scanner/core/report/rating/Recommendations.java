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
import java.util.List;

/**
 * Container class for a collection of recommendations. This class manages multiple Recommendation
 * instances and provides methods to retrieve specific recommendations based on analyzed properties
 * and test results.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Recommendations implements Serializable {

    @XmlElement(name = "recommendation")
    private List<Recommendation> recommendations;

    /** Private no-arg constructor to please JAXB */
    @SuppressWarnings("unused")
    private Recommendations() {}

    /**
     * Constructs a Recommendations container with the specified list of recommendations.
     *
     * @param recommendations the list of recommendations to manage
     */
    public Recommendations(List<Recommendation> recommendations) {
        this.recommendations = recommendations;
    }

    /**
     * Gets the list of recommendations.
     *
     * @return the list of recommendations
     */
    public List<Recommendation> getRecommendations() {
        return recommendations;
    }

    /**
     * Sets the list of recommendations.
     *
     * @param recommendations the list of recommendations to set
     */
    public void setRecommendations(List<Recommendation> recommendations) {
        this.recommendations = recommendations;
    }

    /**
     * Gets the property result recommendation for a specific analyzed property and test result. If
     * no matching recommendation is found, returns a default recommendation with "No recommendation
     * found" messages.
     *
     * @param property the analyzed property to search for
     * @param result the test result to match
     * @return the matching property result recommendation or a default if not found
     */
    public PropertyResultRecommendation getPropertyRecommendation(
            AnalyzedProperty property, TestResult result) {
        for (Recommendation r : recommendations) {
            if (r.getAnalyzedProperty() == property) {
                return r.getPropertyResultRecommendation(result);
            }
        }
        return new PropertyResultRecommendation(
                result,
                Recommendation.NO_RECOMMENDATION_FOUND,
                Recommendation.NO_RECOMMENDATION_FOUND);
    }

    /**
     * Gets the recommendation for a specific analyzed property. If no matching recommendation is
     * found, returns a new recommendation with the property's string representation as the short
     * name.
     *
     * @param property the analyzed property to search for
     * @return the matching recommendation or a new basic recommendation if not found
     */
    public Recommendation getRecommendation(AnalyzedProperty property) {
        for (Recommendation r : recommendations) {
            if (r.getAnalyzedProperty() == property) {
                return r;
            }
        }
        return new Recommendation(property, property.toString());
    }
}
