/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.probe.requirements;

import de.rub.nds.scanner.core.probe.AnalyzedProperty;
import de.rub.nds.scanner.core.probe.result.TestResults;
import de.rub.nds.scanner.core.report.ScanReport;
import java.util.List;

/**
 * Represents a {@link Requirement} for required {@link AnalyzedProperty} properties which were
 * negatively evaluated (TestResults.FALSE).
 */
public class PropertyFalseRequirement<R extends ScanReport> extends PropertyValueRequirement<R> {
    /**
     * Constructs a new PropertyFalseRequirement that checks if the specified properties evaluate to
     * FALSE.
     *
     * @param properties the list of properties that must evaluate to FALSE
     */
    public PropertyFalseRequirement(List<AnalyzedProperty> properties) {
        super(TestResults.FALSE, properties);
    }

    /**
     * Constructs a new PropertyFalseRequirement that checks if the specified properties evaluate to
     * FALSE.
     *
     * @param properties varargs of properties that must evaluate to FALSE
     */
    public PropertyFalseRequirement(AnalyzedProperty... properties) {
        super(TestResults.FALSE, properties);
    }
}
