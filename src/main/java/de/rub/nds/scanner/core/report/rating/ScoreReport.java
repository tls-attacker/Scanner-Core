/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.report.rating;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.rub.nds.scanner.core.probe.AnalyzedProperty;
import java.util.Map;
import java.util.Set;

/**
 * Represents a score report containing the calculated scores and the influencers that contributed
 * to them. This class provides a summary of how different property results affected the individual
 * scores.
 */
public class ScoreReport {

    private final Map<ScoreType, Integer> scores;

    private final Map<AnalyzedProperty, PropertyResultRatingInfluencer> influencers;

    @SuppressWarnings("unused")
    // Default constructor for deserialization
    private ScoreReport() {
        this.scores = Map.of();
        this.influencers = Map.of();
    }

    /**
     * Constructs a ScoreReport with the specified scores and map of influencers.
     *
     * @param scores a map of score types to their calculated scores
     * @param influencers a map of analyzed properties to their rating influencers
     */
    public ScoreReport(
            Map<ScoreType, Integer> scores,
            Map<AnalyzedProperty, PropertyResultRatingInfluencer> influencers) {
        this.scores = scores;
        this.influencers = influencers;
    }

    /**
     * Gets the calculated score of the given score type.
     *
     * @param scoreType the score type
     * @return the score, or null if no score was calculated for the score type
     */
    public Integer getScore(ScoreType scoreType) {
        return scores.get(scoreType);
    }

    /**
     * Gets all calculated scores.
     *
     * @return the map of score types to their calculated scores
     */
    public Map<ScoreType, Integer> getScores() {
        return scores;
    }

    /**
     * Gets the score types for which a score was calculated.
     *
     * @return the score types
     */
    @JsonIgnore
    public Set<ScoreType> getScoreTypes() {
        return scores.keySet();
    }

    /**
     * Gets the map of influencers that contributed to the scores. The map contains analyzed
     * properties as keys and their corresponding rating influencers as values.
     *
     * @return the map of analyzed properties to rating influencers
     */
    public Map<AnalyzedProperty, PropertyResultRatingInfluencer> getInfluencers() {
        return influencers;
    }
}
