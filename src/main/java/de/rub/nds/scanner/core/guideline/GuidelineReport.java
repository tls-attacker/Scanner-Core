/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.guideline;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@JsonIncludeProperties({"name", "link", "results"})
@JsonPropertyOrder({"name", "link", "results"})
public class GuidelineReport {

    private String name;
    private String link;
    private final List<GuidelineCheckResult> results;

    @SuppressWarnings("unused")
    // Default constructor for deserialization
    private GuidelineReport() {
        this.name = null;
        this.link = null;
        this.results = new ArrayList<>();
    }

    public GuidelineReport(String name, String link, List<GuidelineCheckResult> results) {
        this.name = name;
        this.link = link;
        this.results = new ArrayList<>(results);
    }

    /**
     * Gets the name of the guideline that was evaluated.
     *
     * @return the guideline name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the guideline that was evaluated.
     *
     * @param name the guideline name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the link (URL) to the guideline documentation.
     *
     * @return the guideline documentation link
     */
    public String getLink() {
        return link;
    }

    /**
     * Sets the link (URL) to the guideline documentation.
     *
     * @param link the guideline documentation link to set
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * Gets an unmodifiable list of all check results for this guideline.
     *
     * @return an unmodifiable list of all guideline check results
     */
    public List<GuidelineCheckResult> getResults() {
        return Collections.unmodifiableList(results);
    }

    /**
     * Adds a new check result to this guideline report.
     *
     * @param result the guideline check result to add
     */
    public void addResult(GuidelineCheckResult result) {
        results.add(result);
    }

    /**
     * Gets all check results where the guideline was adhered to.
     *
     * @return an unmodifiable list of adhered check results
     */
    public List<GuidelineCheckResult> getAdhered() {
        return results.stream()
                .filter(result -> result.getAdherence() == GuidelineAdherence.ADHERED)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Gets all check results where the guideline was violated.
     *
     * @return an unmodifiable list of violated check results
     */
    public List<GuidelineCheckResult> getViolated() {
        return results.stream()
                .filter(result -> result.getAdherence() == GuidelineAdherence.VIOLATED)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Gets all check results where the condition for evaluation was not met.
     *
     * @return an unmodifiable list of check results with unmet conditions
     */
    public List<GuidelineCheckResult> getConditionNotMet() {
        return results.stream()
                .filter(result -> result.getAdherence() == GuidelineAdherence.CONDITION_NOT_MET)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Gets all check results where the check evaluation failed with an error.
     *
     * @return an unmodifiable list of failed check results
     */
    public List<GuidelineCheckResult> getFailedChecks() {
        return results.stream()
                .filter(result -> result.getAdherence() == GuidelineAdherence.CHECK_FAILED)
                .collect(Collectors.toUnmodifiableList());
    }
}
