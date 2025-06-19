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

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Recommendations implements Serializable {

    @XmlElement(name = "recommendation")
    private List<Recommendation> recommendations;

    /** Private no-arg constructor to please JAXB */
    @SuppressWarnings("unused")
    private Recommendations() {}

    /**
     * Constructs a Recommendations with the specified list of recommendations.
     *
     * @param recommendations the list of recommendations
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
     * Sets the recommendations list.
     *
     * @param recommendations the list of recommendations to set
     */
    public void setRecommendations(List<Recommendation> recommendations) {
        this.recommendations = recommendations;
    }

    /**
     * Gets the property result recommendation for a specific property and test result. If no
     * matching recommendation is found, returns a default recommendation with
     * NO_RECOMMENDATION_FOUND messages.
     *
     * @param property the analyzed property to find a recommendation for
     * @param result the test result to find a recommendation for
     * @return the matching property result recommendation or a default one
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
     * found, returns a default recommendation with the property's string representation as the
     * short name.
     *
     * @param property the analyzed property to find a recommendation for
     * @return the matching recommendation or a default one
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
