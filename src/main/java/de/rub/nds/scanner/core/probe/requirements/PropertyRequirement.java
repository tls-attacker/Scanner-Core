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
import de.rub.nds.scanner.core.probe.result.TestResult;
import de.rub.nds.scanner.core.probe.result.TestResults;
import de.rub.nds.scanner.core.report.ScanReport;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Represents a {@link Requirement} for evaluated {@link AnalyzedProperty} properties. */
public class PropertyRequirement<R extends ScanReport>
        extends PrimitiveRequirement<R, AnalyzedProperty> {

    /**
     * Constructs a new PropertyRequirement with the specified list of properties.
     *
     * @param properties the properties that must be evaluated
     */
    public PropertyRequirement(List<AnalyzedProperty> properties) {
        super(properties);
    }

    /**
     * Constructs a new PropertyRequirement with the specified properties.
     *
     * @param properties the properties that must be evaluated
     */
    public PropertyRequirement(AnalyzedProperty... properties) {
        super(Arrays.asList(properties));
    }

    @Override
    public boolean evaluate(R report) {
        if (parameters.size() == 0) {
            return true;
        }
        Map<AnalyzedProperty, TestResult> propertyMap = report.getResultMap();
        for (AnalyzedProperty property : parameters) {
            if (!propertyMap.containsKey(property)
                    || propertyMap.get(property) == TestResults.UNASSIGNED_ERROR) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format(
                "PropertyRequirement[%s]",
                parameters.stream().map(Object::toString).collect(Collectors.joining(" ")));
    }
}
