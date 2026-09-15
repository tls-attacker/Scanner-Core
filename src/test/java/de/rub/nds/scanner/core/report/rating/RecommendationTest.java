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
import de.rub.nds.scanner.core.probe.result.TestResult;
import de.rub.nds.scanner.core.probe.result.TestResults;
import java.util.List;
import org.junit.jupiter.api.Test;

class RecommendationTest {

    private static final Recommendation RECOMMENDATION =
            new Recommendation(
                    TestAnalyzedProperty.TEST_ANALYZED_PROPERTY,
                    List.of(
                            new PropertyResultRecommendation(
                                    TestResults.TRUE, "A fault was observed", "Fix it"),
                            new PropertyResultRecommendation(
                                    TestResults.FALSE,
                                    "No fault was observed",
                                    "Keep it that way")));

    private static String statusFor(TestResult result) {
        return RECOMMENDATION.getPropertyResultRecommendation(result).getShortDescription();
    }

    private static FaultListResult<String> faultsFor(List<String> faultyFeatures) {
        return new FaultListResult<>(TestAnalyzedProperty.TEST_ANALYZED_PROPERTY, faultyFeatures);
    }

    @Test
    void plainResultsAreMatched() {
        assertEquals("A fault was observed", statusFor(TestResults.TRUE));
        assertEquals("No fault was observed", statusFor(TestResults.FALSE));
    }

    @Test
    void listedFaultsAreMatchedAsTrue() {
        assertEquals("A fault was observed", statusFor(faultsFor(List.of("SECP256R1"))));
    }

    @Test
    void absentFaultsAreMatchedAsFalse() {
        assertEquals("No fault was observed", statusFor(faultsFor(List.of())));
    }

    @Test
    void unconfiguredResultYieldsDefaultRecommendation() {
        assertEquals(Recommendation.NO_INFORMATION_FOUND, statusFor(TestResults.PARTIALLY));
    }
}
