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
 * positively evaluated (TestResults.TRUE).
 */
public class PropertyTrueRequirement<R extends ScanReport> extends PropertyValueRequirement<R> {
    /**
     * Constructs a new PropertyTrueRequirement that checks if the specified properties evaluate to TRUE.
     *
     * @param properties the list of properties that must evaluate to TRUE
     */
    public PropertyTrueRequirement(List<AnalyzedProperty> properties) {
        super(TestResults.TRUE, properties);
    }

    /**
     * Constructs a new PropertyTrueRequirement that checks if the specified properties evaluate to TRUE.
     *
     * @param properties varargs of properties that must evaluate to TRUE
     */
    public PropertyTrueRequirement(AnalyzedProperty... properties) {
        super(TestResults.TRUE, properties);
    }
}
