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
import java.util.Map;

/**
 * Represents a score report containing the calculated rating score and the influencers that
 * contributed to that score. This class provides a summary of how different property results
 * affected the final rating.
 */
public class ScoreReport {

    private final int score;

    private final Map<AnalyzedProperty, PropertyResultRatingInfluencer> influencers;

    @SuppressWarnings("unused")
    // Default constructor for deserialization
    private ScoreReport() {
        this.score = 0;
        this.influencers = Map.of();
    }

    /**
     * Constructs a ScoreReport with the specified score and map of influencers.
     *
     * @param score the calculated rating score
     * @param influencers a map of analyzed properties to their rating influencers
     */
    public ScoreReport(
            int score, Map<AnalyzedProperty, PropertyResultRatingInfluencer> influencers) {
        this.score = score;
        this.influencers = influencers;
    }

    /**
     * Gets the calculated rating score.
     *
     * @return the rating score
     */
    public int getScore() {
        return score;
    }

    /**
     * Gets the map of influencers that contributed to the score. The map contains analyzed
     * properties as keys and their corresponding rating influencers as values.
     *
     * @return the map of analyzed properties to rating influencers
     */
    public Map<AnalyzedProperty, PropertyResultRatingInfluencer> getInfluencers() {
        return influencers;
    }
}
