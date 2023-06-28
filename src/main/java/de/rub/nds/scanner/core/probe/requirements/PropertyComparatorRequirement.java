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
import de.rub.nds.scanner.core.probe.result.CollectionResult;
import de.rub.nds.scanner.core.report.ScanReport;
import java.util.Collection;
import java.util.List;

/**
 * Represents a {@link Requirement} which requires a certain size of a {@link AnalyzedProperty} in
 * the report. It contains the operators greater, smaller and equal. The comparison is [parameter
 * value] [Operator] [value to compare]. Furthermore, the evaluation function returns false for
 * illegal inputs.
 */
public class PropertyComparatorRequirement<R extends ScanReport>
        extends PrimitiveRequirement<R, AnalyzedProperty> {

    private final Operator operator;
    private final Integer comparisonValue;

    public enum Operator {
        GREATER,
        SMALLER,
        EQUAL
    }

    /**
     * @param operator the operator for the requirement.
     * @param parameter the property to check of type {@link AnalyzedProperty}.
     * @param comparisonValue the value to compare with.
     */
    public PropertyComparatorRequirement(
            Operator operator, AnalyzedProperty parameter, Integer comparisonValue) {
        super(List.of(parameter));
        this.operator = operator;
        this.comparisonValue = comparisonValue;
    }

    @Override
    public boolean evaluate(R report) {
        if (parameters.size() == 0 || operator == null || comparisonValue == null) {
            return false;
        }
        CollectionResult<?> collectionResult = report.getCollectionResult(parameters.get(0));
        if (collectionResult == null) {
            return false;
        }
        Collection<?> collection = collectionResult.getCollection();
        if (collection == null) {
            return false;
        }
        switch (operator) {
            case EQUAL:
                return collection.size() == comparisonValue;
            case GREATER:
                return collection.size() > comparisonValue;
            case SMALLER:
                return collection.size() < comparisonValue;
        }
        throw new IllegalArgumentException(
                String.format(
                        "Encountered unsupported operator (%s) in PropertyComparatorRequirement",
                        operator));
    }

    public Operator getOperator() {
        return operator;
    }

    public Integer getComparisonValue() {
        return comparisonValue;
    }

    @Override
    public String toString() {
        return String.format(
                "PropertyComparatorRequirement[%s %s %s]",
                parameters.get(0), operator, comparisonValue);
    }
}
