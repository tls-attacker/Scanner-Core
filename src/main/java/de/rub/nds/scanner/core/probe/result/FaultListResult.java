/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.probe.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.rub.nds.scanner.core.probe.AnalyzedProperty;
import java.util.List;

/**
 * Represents {@link TestResult}s which list the features for which a fault has been observed. The
 * list holds the affected features only, the summary states whether any fault was found at all: an
 * empty list summarizes to {@link TestResults#FALSE}, a non-empty list to {@link TestResults#TRUE}.
 * An explicit TestResult can be set, for example, to communicate that the test could not be
 * applied.
 *
 * @param <T> the type of the listed faulty features.
 */
@JsonIncludeProperties({"type", "value", "summary"})
@JsonPropertyOrder({"type", "value", "summary"})
public class FaultListResult<T> extends ListResult<T> implements SummarizableTestResult {

    @JsonProperty("summary")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final TestResults explicitSummary;

    @SuppressWarnings("unused")
    private FaultListResult() {
        // Default constructor for deserialization
        this(null, null, null);
    }

    /**
     * Constructs a FaultListResult which summarizes the listed faulty features.
     *
     * @param property the analyzed property associated with this result
     * @param faultyFeatures the features for which a fault has been observed, may be empty but
     *     should not be null
     */
    public FaultListResult(AnalyzedProperty property, List<T> faultyFeatures) {
        this(property, faultyFeatures, null);
    }

    /**
     * Constructs a FaultListResult which reports the given summary instead of summarizing listed
     * faulty features. Use this if the features could not be examined, e.g. {@link
     * TestResults#CANNOT_BE_TESTED} if a precondition of the probe was not met.
     *
     * @param property the analyzed property associated with this result
     * @param explicitSummary the summary to report, must not be null
     */
    public FaultListResult(AnalyzedProperty property, TestResults explicitSummary) {
        this(property, null, explicitSummary);
    }

    /**
     * Constructs a FaultListResult with the specified property, list of faulty features and
     * explicit summary. If the explicit summary is null, the summary is derived from the listed
     * faulty features, otherwise the explicit summary takes precedence.
     *
     * @param property the analyzed property associated with this result
     * @param faultyFeatures the features for which a fault has been observed
     * @param explicitSummary the summary to report, or null to derive it from the listed features
     */
    public FaultListResult(
            AnalyzedProperty property, List<T> faultyFeatures, TestResults explicitSummary) {
        super(property, faultyFeatures);
        this.explicitSummary = explicitSummary;
    }

    /**
     * Returns the explicitly set summary of this result.
     *
     * @return the explicit summary, or null if the summary is derived from the listed faulty
     *     features
     */
    public TestResults getExplicitSummary() {
        return explicitSummary;
    }

    /**
     * Summarizes the listed faults. Returns the explicit summary if one has been set. Otherwise
     * returns {@link TestResults#TRUE} if at least one faulty feature has been listed, {@link
     * TestResults#FALSE} if the list is empty and {@link TestResults#NOT_TESTED_YET} if no list has
     * been set at all.
     *
     * @return the summarized TestResults value
     */
    @Override
    public TestResults getSummarizedResult() {
        if (explicitSummary != null) {
            return explicitSummary;
        }
        if (collection == null) {
            return TestResults.NOT_TESTED_YET;
        }
        return TestResults.of(!collection.isEmpty());
    }

    /**
     * Indicates whether the summary was explicitly set.
     *
     * @return true if an explicit summary has been set, false if the summary is derived from the
     *     listed faulty features
     */
    @Override
    @JsonIgnore
    public boolean isExplicitSummary() {
        return explicitSummary != null;
    }

    @Override
    public String getName() {
        return SummarizableTestResult.super.getName();
    }
}
