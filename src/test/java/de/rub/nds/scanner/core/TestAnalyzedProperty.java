/*
 * Scanner Core - A modular framework for probe definition, execution, and result analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core;

import de.rub.nds.scanner.core.probe.AnalyzedProperty;
import de.rub.nds.scanner.core.probe.AnalyzedPropertyCategory;

public enum TestAnalyzedProperty implements AnalyzedProperty {
    TEST_ANALYZED_PROPERTY;

    @Override
    public AnalyzedPropertyCategory getCategory() {
        return null;
    }

    @Override
    public String getName() {
        return name();
    }
}
