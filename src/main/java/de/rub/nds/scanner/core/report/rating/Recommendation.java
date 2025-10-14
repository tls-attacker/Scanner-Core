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
import de.rub.nds.scanner.core.report.ScanReport;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a comprehensive recommendation for an analyzed property. This class contains all
 * information needed to provide guidance about a specific property, including descriptions,
 * documentation, links, and specific recommendations for different test results.
 */
@XmlRootElement
@XmlSeeAlso({ConditionalRecommendation.class})
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
public class Recommendation implements Serializable {

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
     * Constructs an empty Recommendation with initialized empty lists for property recommendations
     * and links.
     */
    public Recommendation() {
        propertyRecommendations = new LinkedList<>();
        links = new LinkedList<>();
    }

    /**
     * Constructs a Recommendation with the specified analyzed property and list of property
     * recommendations.
     *
     * @param analyzedProperty the property this recommendation applies to
     * @param propertyRecommendations the list of recommendations for different test results
     */
    public Recommendation(
            AnalyzedProperty analyzedProperty,
            List<PropertyResultRecommendation> propertyRecommendations) {
        this.analyzedProperty = analyzedProperty;
        this.propertyRecommendations = propertyRecommendations;
    }

    /**
     * Constructs a Recommendation with the specified analyzed property and short name.
     *
     * @param analyzedProperty the property this recommendation applies to
     * @param shortName a short, human-readable name for the property
     */
    public Recommendation(AnalyzedProperty analyzedProperty, String shortName) {
        this();
        this.analyzedProperty = analyzedProperty;
        this.shortName = shortName;
    }

    /**
     * Constructs a Recommendation with descriptions and links.
     *
     * @param analyzedProperty the property this recommendation applies to
     * @param shortName a short, human-readable name for the property
     * @param shortDescription a brief description of the property
     * @param detailedDescription a comprehensive description of the property
     * @param links relevant links for more information
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
     * @param analyzedProperty the property this recommendation applies to
     * @param shortName a short, human-readable name for the property
     * @param shortDescription a brief description of the property
     * @param propertyRecommendation a recommendation for a specific test result
     * @param links relevant links for more information
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
     * Constructs a Recommendation with complete descriptions and a single property recommendation.
     *
     * @param analyzedProperty the property this recommendation applies to
     * @param shortName a short, human-readable name for the property
     * @param shortDescription a brief description of the property
     * @param detailedDescription a comprehensive description of the property
     * @param propertyRecommendation a recommendation for a specific test result
     * @param links relevant links for more information
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
     * Constructs a fully specified Recommendation with all fields.
     *
     * @param analyzedProperty the property this recommendation applies to
     * @param shortName a short, human-readable name for the property
     * @param shortDescription a brief description of the property
     * @param detailedDescription a comprehensive description of the property
     * @param testDocumentation documentation about how the property is tested
     * @param links relevant links for more information
     * @param propertyRecommendations list of recommendations for different test results
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
     * Gets the analyzed property that this recommendation applies to.
     *
     * @return the analyzed property
     */
    public AnalyzedProperty getAnalyzedProperty() {
        return analyzedProperty;
    }

    /**
     * Sets the analyzed property that this recommendation applies to.
     *
     * @param analyzedProperty the analyzed property to set
     */
    public void setAnalyzedProperty(AnalyzedProperty analyzedProperty) {
        this.analyzedProperty = analyzedProperty;
    }

    /**
     * Gets the short name for the property. If no short name is set, returns the string
     * representation of the analyzed property.
     *
     * @return the short name or property string representation
     */
    public String getShortName() {
        if (shortName == null || shortName.equals("")) {
            return analyzedProperty.toString();
        } else {
            return shortName;
        }
    }

    /**
     * Sets the short name for the property.
     *
     * @param shortName the short name to set
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * Gets the brief description of the property.
     *
     * @return the short description
     */
    public String getShortDescription() {
        return shortDescription;
    }

    /**
     * Sets the brief description of the property.
     *
     * @param shortDescription the short description to set
     */
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    /**
     * Gets the comprehensive description of the property.
     *
     * @return the detailed description
     */
    public String getDetailedDescription() {
        return detailedDescription;
    }

    /**
     * Sets the comprehensive description of the property.
     *
     * @param detailedDescription the detailed description to set
     */
    public void setDetailedDescription(String detailedDescription) {
        this.detailedDescription = detailedDescription;
    }

    /**
     * Gets the documentation about how the property is tested.
     *
     * @return the test documentation
     */
    public String getTestDocumentation() {
        return testDocumentation;
    }

    /**
     * Sets the documentation about how the property is tested.
     *
     * @param testDocumentation the test documentation to set
     */
    public void setTestDocumentation(String testDocumentation) {
        this.testDocumentation = testDocumentation;
    }

    /**
     * Gets the list of recommendations for different test results.
     *
     * @return the list of property result recommendations
     */
    public List<PropertyResultRecommendation> getPropertyRecommendations() {
        return propertyRecommendations;
    }

    /**
     * Sets the list of recommendations for different test results.
     *
     * @param propertyRecommendations the list of property result recommendations to set
     */
    public void setPropertyRecommendations(
            List<PropertyResultRecommendation> propertyRecommendations) {
        this.propertyRecommendations = propertyRecommendations;
    }

    /**
     * Gets the recommendation for a specific test result. If no recommendation is found, returns a
     * default recommendation with no information available message.
     *
     * @param result the test result to find a recommendation for
     * @return the matching recommendation or a default recommendation if not found
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
     * Gets the recommendation for a specific test result with access to the full scan report. This
     * method allows subclasses to implement conditional logic based on other properties in the
     * report. The default implementation ignores the report and delegates to {@link
     * #getPropertyResultRecommendation(TestResult)}.
     *
     * @param result the test result to find a recommendation for
     * @param report the scan report containing all property test results
     * @return the matching recommendation or a default recommendation if not found
     */
    public PropertyResultRecommendation getPropertyResultRecommendation(
            TestResult result, ScanReport report) {
        return getPropertyResultRecommendation(result);
    }

    /**
     * Gets the list of relevant links for more information.
     *
     * @return the list of links
     */
    public List<String> getLinks() {
        return links;
    }

    /**
     * Sets the list of relevant links for more information.
     *
     * @param links the list of links to set
     */
    public void setLinks(List<String> links) {
        this.links = links;
    }
}
