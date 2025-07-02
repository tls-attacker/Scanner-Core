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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Calculates rating scores for site reports based on property test results and configured rating
 * influencers. This class evaluates how different property results affect the overall security
 * rating of a site.
 */
public class SiteReportRater {

    private static final Logger LOGGER = LogManager.getLogger();

    private final RatingInfluencers influencers;

    private final Recommendations recommendations;

    /**
     * Constructs a SiteReportRater with the specified rating influencers and recommendations.
     *
     * @param influencers the rating influencers configuration
     * @param recommendations the recommendations configuration
     */
    public SiteReportRater(RatingInfluencers influencers, Recommendations recommendations) {
        this.influencers = influencers;
        this.recommendations = recommendations;
    }

    /**
     * Generates a score report based on the provided property test results. This method evaluates
     * each property result against the configured rating influencers and calculates the overall
     * score.
     *
     * @param resultMap a map of analyzed properties to their test results
     * @return a ScoreReport containing the calculated score and applied influencers
     */
    public ScoreReport getScoreReport(Map<AnalyzedProperty, TestResult> resultMap) {
        LinkedHashMap<AnalyzedProperty, PropertyResultRatingInfluencer> ratingInfluencers =
                new LinkedHashMap<>();

        for (RatingInfluencer ratingInfluencer : influencers.getRatingInfluencers()) {
            TestResult result = resultMap.get(ratingInfluencer.getAnalyzedProperty());
            if (result != null) {
                PropertyResultRatingInfluencer propertyRatingInfluencer =
                        ratingInfluencer.getPropertyRatingInfluencer(result);
                ratingInfluencers.put(
                        ratingInfluencer.getAnalyzedProperty(), propertyRatingInfluencer);
            }
        }

        int score = computeScore(ratingInfluencers);
        return new ScoreReport(score, ratingInfluencers);
    }

    private static int computeScore(
            HashMap<AnalyzedProperty, PropertyResultRatingInfluencer> influencers) {
        int score = 0;
        for (PropertyResultRatingInfluencer influencer : influencers.values()) {
            if (influencer.getInfluence() != null) {
                score += influencer.getInfluence();
            } else {
                LOGGER.warn("Influencer has 'null' influence");
            }
        }
        for (PropertyResultRatingInfluencer influencer : influencers.values()) {
            if (influencer.getScoreCap() != null && score >= influencer.getScoreCap()) {
                score = influencer.getScoreCap();
            }
        }
        return score;
    }

    /**
     * Gets the recommendations configuration used by this rater.
     *
     * @return the recommendations configuration
     */
    public Recommendations getRecommendations() {
        return recommendations;
    }

    /**
     * Gets the rating influencers configuration used by this rater.
     *
     * @return the rating influencers configuration
     */
    public RatingInfluencers getRatingInfluencers() {
        return influencers;
    }
}
