/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.passive;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class StatsWriter<StateT> {

    private final List<StatExtractor<StateT, ?>> extractorList;

    private int stateCounter = 0;

    /**
     * Creates a new StatsWriter with an empty list of extractors.
     */
    public StatsWriter() {
        extractorList = new LinkedList<>();
    }

    /**
     * Adds a StatExtractor to this writer.
     *
     * @param extractor The StatExtractor to add
     */
    public void addExtractor(StatExtractor<StateT, ?> extractor) {
        extractorList.add(extractor);
    }

    /**
     * Extracts values from the given state using all registered extractors.
     *
     * @param state The state object to extract values from
     */
    public void extract(StateT state) {
        for (StatExtractor<StateT, ?> extractor : extractorList) {
            extractor.extract(state);
        }
        stateCounter++;
    }

    /**
     * Returns a list of all containers from all registered extractors.
     *
     * @return List of ExtractedValueContainers from all extractors
     */
    public List<ExtractedValueContainer<?>> getCumulatedExtractedValues() {
        return extractorList.stream().map(StatExtractor::getContainer).collect(Collectors.toList());
    }

    /**
     * Returns the number of states that have been processed.
     *
     * @return The count of processed states
     */
    public int getStateCounter() {
        return stateCounter;
    }
}
