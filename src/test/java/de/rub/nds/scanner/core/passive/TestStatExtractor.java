/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.passive;

public class TestStatExtractor extends StatExtractor<TestState, TestTrackableValue> {

    private boolean shouldExtractNull = false;

    public TestStatExtractor() {
        super(new TestTrackableValue("type"));
    }

    public void setShouldExtractNull(boolean shouldExtractNull) {
        this.shouldExtractNull = shouldExtractNull;
    }

    @Override
    public void extract(TestState state) {
        if (shouldExtractNull) {
            put(null);
        } else if (state != null && state.getValue() != null) {
            put(new TestTrackableValue(state.getValue()));
        }
    }
}
