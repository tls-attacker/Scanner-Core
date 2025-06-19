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

    /** Comparison operators for property size evaluation. */
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

    /**
     * Returns the comparison operator used in this requirement.
     *
     * @return the comparison operator
     */
    public Operator getOperator() {
        return operator;
    }

    /**
     * Returns the value against which the property collection size is compared.
     *
     * @return the comparison value
     */
    public Integer getComparisonValue() {
        return comparisonValue;
    }

    /**
     * Returns a string representation of this requirement in the format
     * "PropertyComparatorRequirement[property operator value]".
     *
     * @return string representation of the comparator requirement
     */
    @Override
    public String toString() {
        return String.format(
                "PropertyComparatorRequirement[%s %s %s]",
                parameters.isEmpty() ? "(no parameter)" : parameters.get(0),
                operator != null ? operator : "(no operator)",
                comparisonValue != null ? comparisonValue : "(no value)");
    }
}
