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
     * Constructs a ScoreReport with the specified score and influencers.
     *
     * @param score the calculated score
     * @param influencers the map of properties to their rating influencers that contributed to the
     *     score
     */
    public ScoreReport(
            int score, Map<AnalyzedProperty, PropertyResultRatingInfluencer> influencers) {
        this.score = score;
        this.influencers = influencers;
    }

    /**
     * Gets the calculated score.
     *
     * @return the score value
     */
    public int getScore() {
        return score;
    }

    /**
     * Gets the map of properties to their rating influencers.
     *
     * @return the map of analyzed properties to property result rating influencers
     */
    public Map<AnalyzedProperty, PropertyResultRatingInfluencer> getInfluencers() {
        return influencers;
    }
}
