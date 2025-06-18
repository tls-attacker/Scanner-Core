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
import de.rub.nds.scanner.core.report.ScanReport;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a {@link Requirement} for required {@link AnalyzedProperty} properties which evaluated
 * to the expected values.
 */
public class PropertyValueRequirement<R extends ScanReport>
        extends PrimitiveRequirement<R, AnalyzedProperty> {

    private final TestResult requiredTestResult;

    /**
     * Constructs a new PropertyValueRequirement with the specified expected test result and properties to check.
     *
     * @param requiredTestResult the expected test result for all specified properties
     * @param properties the list of properties that must have the expected test result
     */
    public PropertyValueRequirement(
            TestResult requiredTestResult, List<AnalyzedProperty> properties) {
        super(properties);
        this.requiredTestResult = requiredTestResult;
    }

    /**
     * Constructs a new PropertyValueRequirement with the specified expected test result and properties to check.
     *
     * @param requiredTestResult the expected test result for all specified properties
     * @param properties varargs of properties that must have the expected test result
     */
    public PropertyValueRequirement(TestResult requiredTestResult, AnalyzedProperty... properties) {
        super(List.of(properties));
        this.requiredTestResult = requiredTestResult;
    }

    @Override
    public boolean evaluate(R report) {
        if (parameters.size() == 0) {
            return true;
        }
        Map<AnalyzedProperty, TestResult> propertyMap = report.getResultMap();
        for (AnalyzedProperty property : parameters) {
            if (!propertyMap.containsKey(property)) {
                return false;
            }
            TestResult actualResult = propertyMap.get(property);
            try {
                if (actualResult == null
                        || !actualResult.equalsExpectedResult(requiredTestResult)) {
                    return false;
                }
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        String.format("Cannot evaluate Requirement for Property \"%s\"", property),
                        e);
            }
        }
        return true;
    }

    /**
     * Returns the expected test result that all properties must have for this requirement to be satisfied.
     *
     * @return the required test result
     */
    public TestResult getRequiredTestResult() {
        return requiredTestResult;
    }

    /**
     * Returns a string representation of this requirement including the required test result and properties.
     *
     * @return string representation in the format "PropertyValueRequirement[TestResult: property1 property2 ...]"
     */
    @Override
    public String toString() {
        return String.format(
                "PropertyValueRequirement[%s: %s]",
                requiredTestResult,
                parameters.stream().map(Object::toString).collect(Collectors.joining(" ")));
    }
}
