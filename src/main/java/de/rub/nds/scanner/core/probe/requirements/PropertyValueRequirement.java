/*
 * Scanner Core - A modular framework for probe definition, execution, and result analysis.
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

    public PropertyValueRequirement(
            TestResult requiredTestResult, List<AnalyzedProperty> properties) {
        super(properties);
        this.requiredTestResult = requiredTestResult;
    }

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
            if (!propertyMap.containsKey(property)
                    || propertyMap.get(property) == null
                    || !propertyMap.get(property).equals(requiredTestResult)) {
                checkPropertyValuePair(
                        property.toString(), propertyMap.get(property), requiredTestResult);
                return false;
            }
        }
        return true;
    }

    public TestResult getRequiredTestResult() {
        return requiredTestResult;
    }

    private void checkPropertyValuePair(
            String propertyString, TestResult listedResult, TestResult expectedResult) {
        if (listedResult != null && listedResult.getClass() != expectedResult.getClass()) {
            throw new IllegalArgumentException(
                    String.format(
                            "Requirement set for property %s expects wrong type of result (found %s but expected %s)",
                            propertyString, listedResult.getClass(), expectedResult.getClass()));
        }
    }

    @Override
    public String toString() {
        return String.format(
                "PropertyValueRequirement[%s: %s]",
                requiredTestResult,
                parameters.stream().map(Object::toString).collect(Collectors.joining(" ")));
    }
}
