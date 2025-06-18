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
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@XmlRootElement
@XmlType(
        propOrder = {
            "analyzedProperty",
            "shortName",
            "shortDescription",
            "detailedDescription",
            "testDocumentation",
            "links",
            "propertyRecommendations"
        })
@XmlAccessorType(XmlAccessType.FIELD)
public class Recommendation {

    static final String NO_INFORMATION_FOUND = "No detailed information available";

    static final String NO_RECOMMENDATION_FOUND = "No recommendation available";

    @XmlAnyElement(lax = true)
    private AnalyzedProperty analyzedProperty;

    private String shortName;

    private String shortDescription;

    private String detailedDescription;

    private String testDocumentation;

    private List<String> links;

    private List<PropertyResultRecommendation> propertyRecommendations;

    /**
     * Constructs an empty Recommendation with empty lists for property recommendations and links.
     */
    public Recommendation() {
        propertyRecommendations = new LinkedList<>();
        links = new LinkedList<>();
    }

    /**
     * Constructs a Recommendation with the specified property and recommendations list.
     *
     * @param analyzedProperty the analyzed property this recommendation applies to
     * @param propertyRecommendations the list of property result recommendations
     */
    public Recommendation(
            AnalyzedProperty analyzedProperty,
            List<PropertyResultRecommendation> propertyRecommendations) {
        this.analyzedProperty = analyzedProperty;
        this.propertyRecommendations = propertyRecommendations;
    }

    /**
     * Constructs a Recommendation with a property and short name.
     *
     * @param analyzedProperty the analyzed property this recommendation applies to
     * @param shortName the short name for this recommendation
     */
    public Recommendation(AnalyzedProperty analyzedProperty, String shortName) {
        this();
        this.analyzedProperty = analyzedProperty;
        this.shortName = shortName;
    }

    /**
     * Constructs a Recommendation with detailed information.
     *
     * @param analyzedProperty the analyzed property this recommendation applies to
     * @param shortName the short name for this recommendation
     * @param shortDescription the short description of the recommendation
     * @param detailedDescription the detailed description of the recommendation
     * @param links additional reference links
     */
    public Recommendation(
            AnalyzedProperty analyzedProperty,
            String shortName,
            String shortDescription,
            String detailedDescription,
            String... links) {
        this();
        this.analyzedProperty = analyzedProperty;
        this.shortName = shortName;
        this.shortDescription = shortDescription;
        this.detailedDescription = detailedDescription;
        this.links.addAll(Arrays.asList(links));
    }

    /**
     * Constructs a Recommendation with a single property recommendation.
     *
     * @param analyzedProperty the analyzed property this recommendation applies to
     * @param shortName the short name for this recommendation
     * @param shortDescription the short description of the recommendation
     * @param propertyRecommendation the property result recommendation to add
     * @param links additional reference links
     */
    public Recommendation(
            AnalyzedProperty analyzedProperty,
            String shortName,
            String shortDescription,
            PropertyResultRecommendation propertyRecommendation,
            String... links) {
        this();
        this.analyzedProperty = analyzedProperty;
        this.shortName = shortName;
        this.shortDescription = shortDescription;
        propertyRecommendations.add(propertyRecommendation);
        this.links.addAll(Arrays.asList(links));
    }

    /**
     * Constructs a Recommendation with detailed information and a single property recommendation.
     *
     * @param analyzedProperty the analyzed property this recommendation applies to
     * @param shortName the short name for this recommendation
     * @param shortDescription the short description of the recommendation
     * @param detailedDescription the detailed description of the recommendation
     * @param propertyRecommendation the property result recommendation to add
     * @param links additional reference links
     */
    public Recommendation(
            AnalyzedProperty analyzedProperty,
            String shortName,
            String shortDescription,
            String detailedDescription,
            PropertyResultRecommendation propertyRecommendation,
            String... links) {
        this();
        this.analyzedProperty = analyzedProperty;
        this.shortName = shortName;
        this.shortDescription = shortDescription;
        this.detailedDescription = detailedDescription;
        propertyRecommendations.add(propertyRecommendation);
        this.links.addAll(Arrays.asList(links));
    }

    /**
     * Constructs a fully-specified Recommendation with all fields.
     *
     * @param analyzedProperty the analyzed property this recommendation applies to
     * @param shortName the short name for this recommendation
     * @param shortDescription the short description of the recommendation
     * @param detailedDescription the detailed description of the recommendation
     * @param testDocumentation the test documentation
     * @param links the list of reference links
     * @param propertyRecommendations the list of property result recommendations
     */
    public Recommendation(
            AnalyzedProperty analyzedProperty,
            String shortName,
            String shortDescription,
            String detailedDescription,
            String testDocumentation,
            List<String> links,
            List<PropertyResultRecommendation> propertyRecommendations) {
        this.analyzedProperty = analyzedProperty;
        this.shortName = shortName;
        this.shortDescription = shortDescription;
        this.detailedDescription = detailedDescription;
        this.testDocumentation = testDocumentation;
        this.links = links;
        this.propertyRecommendations = propertyRecommendations;
    }

    /**
     * Gets the analyzed property this recommendation applies to.
     *
     * @return the analyzed property
     */
    public AnalyzedProperty getAnalyzedProperty() {
        return analyzedProperty;
    }

    /**
     * Sets the analyzed property.
     *
     * @param analyzedProperty the analyzed property to set
     */
    public void setAnalyzedProperty(AnalyzedProperty analyzedProperty) {
        this.analyzedProperty = analyzedProperty;
    }

    /**
     * Gets the short name of this recommendation.
     * If no short name is set, returns the analyzed property's string representation.
     *
     * @return the short name or property string if short name is null or empty
     */
    public String getShortName() {
        if (shortName == null || shortName.equals("")) {
            return analyzedProperty.toString();
        } else {
            return shortName;
        }
    }

    /**
     * Sets the short name.
     *
     * @param shortName the short name to set
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * Gets the short description of this recommendation.
     *
     * @return the short description
     */
    public String getShortDescription() {
        return shortDescription;
    }

    /**
     * Sets the short description.
     *
     * @param shortDescription the short description to set
     */
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    /**
     * Gets the detailed description of this recommendation.
     *
     * @return the detailed description
     */
    public String getDetailedDescription() {
        return detailedDescription;
    }

    /**
     * Sets the detailed description.
     *
     * @param detailedDescription the detailed description to set
     */
    public void setDetailedDescription(String detailedDescription) {
        this.detailedDescription = detailedDescription;
    }

    /**
     * Gets the test documentation.
     *
     * @return the test documentation
     */
    public String getTestDocumentation() {
        return testDocumentation;
    }

    /**
     * Sets the test documentation.
     *
     * @param testDocumentation the test documentation to set
     */
    public void setTestDocumentation(String testDocumentation) {
        this.testDocumentation = testDocumentation;
    }

    /**
     * Gets the list of property result recommendations.
     *
     * @return the list of property recommendations
     */
    public List<PropertyResultRecommendation> getPropertyRecommendations() {
        return propertyRecommendations;
    }

    /**
     * Sets the property recommendations.
     *
     * @param propertyRecommendations the list of property recommendations to set
     */
    public void setPropertyRecommendations(
            List<PropertyResultRecommendation> propertyRecommendations) {
        this.propertyRecommendations = propertyRecommendations;
    }

    /**
     * Gets the property result recommendation for a specific test result.
     * If no matching recommendation is found, returns a default recommendation
     * with NO_INFORMATION_FOUND and NO_RECOMMENDATION_FOUND messages.
     *
     * @param result the test result to find a recommendation for
     * @return the matching property result recommendation or a default one
     */
    public PropertyResultRecommendation getPropertyResultRecommendation(TestResult result) {
        for (PropertyResultRecommendation r : propertyRecommendations) {
            if (r.getResult() == result) {
                return r;
            }
        }
        return new PropertyResultRecommendation(
                result, NO_INFORMATION_FOUND, NO_RECOMMENDATION_FOUND);
    }

    /**
     * Gets the list of reference links.
     *
     * @return the list of links
     */
    public List<String> getLinks() {
        return links;
    }

    /**
     * Sets the reference links.
     *
     * @param links the list of links to set
     */
    public void setLinks(List<String> links) {
        this.links = links;
    }
}
