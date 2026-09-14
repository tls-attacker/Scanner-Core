/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.probe.result;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.rub.nds.scanner.core.TestAnalyzedProperty;
import java.util.List;
import org.junit.jupiter.api.Test;

class FaultListResultTest {

    private static FaultListResult<String> faultsFor(List<String> faultyFeatures) {
        return new FaultListResult<>(TestAnalyzedProperty.TEST_ANALYZED_PROPERTY, faultyFeatures);
    }

    private static FaultListResult<String> summarizedAs(TestResults explicitSummary) {
        return new FaultListResult<>(TestAnalyzedProperty.TEST_ANALYZED_PROPERTY, explicitSummary);
    }

    @Test
    void emptyListSummarizesToFalse() {
        assertEquals(TestResults.FALSE, faultsFor(List.of()).getSummarizedResult());
    }

    @Test
    void nonEmptyListSummarizesToTrue() {
        assertEquals(TestResults.TRUE, faultsFor(List.of("SECP256R1")).getSummarizedResult());
    }

    @Test
    void missingListSummarizesToNotTestedYet() {
        assertEquals(TestResults.NOT_TESTED_YET, faultsFor(null).getSummarizedResult());
    }

    @Test
    void summaryIsNotExplicit() {
        assertFalse(faultsFor(List.of()).isExplicitSummary());
        assertNull(faultsFor(List.of()).getExplicitSummary());
    }

    @Test
    void explicitSummaryIsReportedAsSet() {
        FaultListResult<String> cannotBeTested = summarizedAs(TestResults.CANNOT_BE_TESTED);

        assertTrue(cannotBeTested.isExplicitSummary());
        assertEquals(TestResults.CANNOT_BE_TESTED, cannotBeTested.getExplicitSummary());
        assertEquals(TestResults.CANNOT_BE_TESTED, cannotBeTested.getSummarizedResult());
        assertEquals(TestResults.CANNOT_BE_TESTED.getName(), cannotBeTested.getName());
    }

    @Test
    void explicitSummaryCollectsNoFaults() {
        assertNull(summarizedAs(TestResults.CANNOT_BE_TESTED).getList());
    }

    @Test
    void explicitSummaryTakesPrecedenceOverListedFaults() {
        FaultListResult<String> result =
                new FaultListResult<>(
                        TestAnalyzedProperty.TEST_ANALYZED_PROPERTY,
                        List.of("SECP256R1"),
                        TestResults.ERROR_DURING_TEST);

        assertTrue(result.isExplicitSummary());
        assertEquals(TestResults.ERROR_DURING_TEST, result.getSummarizedResult());
        assertEquals(List.of("SECP256R1"), result.getList());
    }

    @Test
    void explicitSummaryMatchesExpectedTestResults() {
        FaultListResult<String> cannotBeTested = summarizedAs(TestResults.CANNOT_BE_TESTED);

        assertTrue(cannotBeTested.equalsExpectedResult(TestResults.CANNOT_BE_TESTED));
        assertFalse(cannotBeTested.equalsExpectedResult(TestResults.FALSE));
        assertFalse(cannotBeTested.equalsExpectedResult(TestResults.TRUE));
    }

    @Test
    void nameIsTakenFromSummary() {
        assertEquals(TestResults.TRUE.getName(), faultsFor(List.of("SECP256R1")).getName());
        assertEquals(TestResults.FALSE.getName(), faultsFor(List.of()).getName());
    }

    @Test
    void matchesExpectedTestResultsOfSummary() {
        FaultListResult<String> withFaults = faultsFor(List.of("SECP256R1"));
        FaultListResult<String> withoutFaults = faultsFor(List.of());

        assertTrue(withFaults.equalsExpectedResult(TestResults.TRUE));
        assertFalse(withFaults.equalsExpectedResult(TestResults.FALSE));
        assertTrue(withoutFaults.equalsExpectedResult(TestResults.FALSE));
        assertFalse(withoutFaults.equalsExpectedResult(TestResults.TRUE));
    }

    @Test
    void listStaysAccessible() {
        assertEquals(
                List.of("SECP256R1", "SECP384R1"),
                faultsFor(List.of("SECP256R1", "SECP384R1")).getList());
    }

    @Test
    void roundTripKeepsFaultsAndSummary() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String json = mapper.writeValueAsString(faultsFor(List.of("SECP256R1")));

        FaultListResult<?> restored = mapper.readValue(json, FaultListResult.class);

        assertNotNull(restored.getList());
        assertEquals(List.of("SECP256R1"), restored.getList());
        assertEquals(TestResults.TRUE, restored.getSummarizedResult());
    }

    @Test
    void roundTripKeepsExplicitSummary() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String json = mapper.writeValueAsString(summarizedAs(TestResults.CANNOT_BE_TESTED));

        FaultListResult<?> restored = mapper.readValue(json, FaultListResult.class);

        assertTrue(restored.isExplicitSummary());
        assertEquals(TestResults.CANNOT_BE_TESTED, restored.getSummarizedResult());
        assertNull(restored.getList());
    }

    @Test
    void roundTripKeepsEmptyFaultList() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String json = mapper.writeValueAsString(faultsFor(List.of()));

        FaultListResult<?> restored = mapper.readValue(json, FaultListResult.class);

        assertNotNull(restored.getList());
        assertTrue(restored.getList().isEmpty());
        assertEquals(TestResults.FALSE, restored.getSummarizedResult());
    }
}
