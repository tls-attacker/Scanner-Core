/*
 * Scanner Core - A modular framework for probe definition, execution, and result analysis.
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

    public ScoreReport(
            int score, Map<AnalyzedProperty, PropertyResultRatingInfluencer> influencers) {
        this.score = score;
        this.influencers = influencers;
    }

    public int getScore() {
        return score;
    }

    public Map<AnalyzedProperty, PropertyResultRatingInfluencer> getInfluencers() {
        return influencers;
    }
}
