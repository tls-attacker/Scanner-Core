/*
 * Scanner Core - A modular framework for probe definition, execution, and result analysis.
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

    public StatsWriter() {
        extractorList = new LinkedList<>();
    }

    public void addExtractor(StatExtractor<StateT, ?> extractor) {
        extractorList.add(extractor);
    }

    public void extract(StateT state) {
        for (StatExtractor<StateT, ?> extractor : extractorList) {
            extractor.extract(state);
        }
        stateCounter++;
    }

    public List<ExtractedValueContainer<?>> getCumulatedExtractedValues() {
        return extractorList.stream().map(StatExtractor::getContainer).collect(Collectors.toList());
    }

    public int getStateCounter() {
        return stateCounter;
    }
}
