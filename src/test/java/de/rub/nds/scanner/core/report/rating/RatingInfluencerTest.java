/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.report.rating;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.rub.nds.scanner.core.TestAnalyzedProperty;
import de.rub.nds.scanner.core.probe.result.FaultListResult;
import de.rub.nds.scanner.core.probe.result.ListResult;
import de.rub.nds.scanner.core.probe.result.TestResult;
import de.rub.nds.scanner.core.probe.result.TestResults;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class RatingInfluencerTest {

    private static final RatingInfluencer INFLUENCER =
            new RatingInfluencer(
                    TestAnalyzedProperty.TEST_ANALYZED_PROPERTY,
                    new PropertyResultRatingInfluencer(TestResults.TRUE, -200),
                    new PropertyResultRatingInfluencer(TestResults.FALSE, 50),
                    new PropertyResultRatingInfluencer(TestResults.CANNOT_BE_TESTED, -10));

    private static int influenceFor(TestResult result) {
        return INFLUENCER.getPropertyRatingInfluencer(result).getInfluence();
    }

    private static FaultListResult<String> faultsFor(List<String> faultyFeatures) {
        return new FaultListResult<>(TestAnalyzedProperty.TEST_ANALYZED_PROPERTY, faultyFeatures);
    }

    @Test
    void plainResultsAreMatched() {
        assertEquals(-200, influenceFor(TestResults.TRUE));
        assertEquals(50, influenceFor(TestResults.FALSE));
    }

    @Test
    void listedFaultsAreMatchedAsTrue() {
        assertEquals(-200, influenceFor(faultsFor(List.of("SECP256R1"))));
    }

    @Test
    void absentFaultsAreMatchedAsFalse() {
        assertEquals(50, influenceFor(faultsFor(List.of())));
    }

    @Test
    void explicitSummaryIsMatched() {
        assertEquals(
                -10,
                influenceFor(
                        new FaultListResult<String>(
                                TestAnalyzedProperty.TEST_ANALYZED_PROPERTY,
                                TestResults.CANNOT_BE_TESTED)));
    }

    @Test
    void unconfiguredResultYieldsNeutralInfluence() {
        assertEquals(0, influenceFor(TestResults.PARTIALLY));
    }

    @Test
    void resultWithoutSummaryYieldsNeutralInfluence() {
        // a plain ListResult cannot be compared to the TestResults of the configuration, which
        // must not fail the rating of the report as a whole
        assertEquals(
                0,
                influenceFor(
                        new ListResult<>(
                                TestAnalyzedProperty.TEST_ANALYZED_PROPERTY,
                                List.of("SECP256R1"))));
    }

    @Test
    void faultsInfluenceTheScore() {
        SiteReportRater rater =
                new SiteReportRater(
                        new RatingInfluencers(new LinkedList<>(List.of(INFLUENCER))),
                        new Recommendations(List.of()));

        assertEquals(
                -200,
                rater.getScoreReport(
                                Map.of(
                                        TestAnalyzedProperty.TEST_ANALYZED_PROPERTY,
                                        faultsFor(List.of("SECP256R1"))))
                        .getScore());
    }
}
