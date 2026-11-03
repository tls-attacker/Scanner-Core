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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Calculates rating scores for site reports based on property test results and configured rating
 * influencers. This class evaluates how different property results affect the individual scores of
 * a site. A score is computed for each {@link ScoreType} for which at least one modifier is
 * configured.
 */
public class SiteReportRater {

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
     * each property result against the configured rating influencers and calculates the score of
     * each score type.
     *
     * @param resultMap a map of analyzed properties to their test results
     * @return a ScoreReport containing the calculated scores and applied influencers
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

        LinkedHashMap<ScoreType, Integer> scores = new LinkedHashMap<>();
        for (ScoreType scoreType : getScoreTypes()) {
            scores.put(scoreType, computeScore(scoreType, ratingInfluencers));
        }
        return new ScoreReport(scores, ratingInfluencers);
    }

    /**
     * Gets all score types for which at least one modifier is configured.
     *
     * @return the score types in order of their first occurrence
     */
    public Set<ScoreType> getScoreTypes() {
        Set<ScoreType> scoreTypes = new LinkedHashSet<>();
        for (RatingInfluencer ratingInfluencer : influencers.getRatingInfluencers()) {
            for (PropertyResultRatingInfluencer propertyRatingInfluencer :
                    ratingInfluencer.getPropertyRatingInfluencers()) {
                for (ScoreModifier modifier : propertyRatingInfluencer.getModifiers()) {
                    scoreTypes.add(modifier.getScoreType());
                }
            }
        }
        return scoreTypes;
    }

    private static int computeScore(
            ScoreType scoreType,
            Map<AnalyzedProperty, PropertyResultRatingInfluencer> influencers) {
        int score = 0;
        for (PropertyResultRatingInfluencer influencer : influencers.values()) {
            score += influencer.getInfluence(scoreType);
        }
        for (PropertyResultRatingInfluencer influencer : influencers.values()) {
            Integer scoreCap = influencer.getScoreCap(scoreType);
            if (scoreCap != null && score >= scoreCap) {
                score = scoreCap;
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
