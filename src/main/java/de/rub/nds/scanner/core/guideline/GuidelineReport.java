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

    public GuidelineReport(String name, String link, List<GuidelineCheckResult> results) {
        this.name = name;
        this.link = link;
        this.results = new ArrayList<>(results);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public List<GuidelineCheckResult> getResults() {
        return Collections.unmodifiableList(results);
    }

    public void addResult(GuidelineCheckResult result) {
        results.add(result);
    }

    public List<GuidelineCheckResult> getAdhered() {
        return results.stream()
                .filter(result -> result.getAdherence() == GuidelineAdherence.ADHERED)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<GuidelineCheckResult> getViolated() {
        return results.stream()
                .filter(result -> result.getAdherence() == GuidelineAdherence.VIOLATED)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<GuidelineCheckResult> getConditionNotMet() {
        return results.stream()
                .filter(result -> result.getAdherence() == GuidelineAdherence.CONDITION_NOT_MET)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<GuidelineCheckResult> getFailedChecks() {
        return results.stream()
                .filter(result -> result.getAdherence() == GuidelineAdherence.CHECK_FAILED)
                .collect(Collectors.toUnmodifiableList());
    }
}
