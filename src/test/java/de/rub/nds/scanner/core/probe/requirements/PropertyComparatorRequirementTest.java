/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.probe.requirements;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.rub.nds.scanner.core.TestAnalyzedProperty;
import de.rub.nds.scanner.core.probe.result.ListResult;
import de.rub.nds.scanner.core.report.ScanReport;
import java.io.OutputStream;
import java.util.List;
import org.junit.jupiter.api.Test;

public class PropertyComparatorRequirementTest {

    @Test
    public void testPropertyComparatorRequirement() {
        TestAnalyzedProperty property = TestAnalyzedProperty.TEST_ANALYZED_PROPERTY;
        ScanReport report0 =
                new ScanReport() {
                    @Override
                    public void serializeToJson(OutputStream stream) {}

                    @Override
                    public String getRemoteName() {
                        return "";
                    }
                };
        ScanReport report1 =
                new ScanReport() {
                    @Override
                    public void serializeToJson(OutputStream stream) {}

                    @Override
                    public String getRemoteName() {
                        return "";
                    }
                };
        ScanReport report2 =
                new ScanReport() {
                    @Override
                    public void serializeToJson(OutputStream stream) {}

                    @Override
                    public String getRemoteName() {
                        return "";
                    }
                };
        ListResult<Integer> listResult1 = new ListResult<>(property, List.of(0));
        ListResult<Integer> listResult2 = new ListResult<>(property, List.of(0, 1));
        report1.putResult(property, listResult1);
        report2.putResult(property, listResult2);

        // normal values
        Requirement<ScanReport> requirementGreater =
                new PropertyComparatorRequirement<>(
                        PropertyComparatorRequirement.Operator.GREATER, property, 2);
        Requirement<ScanReport> requirementSmaller =
                new PropertyComparatorRequirement<>(
                        PropertyComparatorRequirement.Operator.SMALLER, property, 2);
        Requirement<ScanReport> requirementEqual =
                new PropertyComparatorRequirement<>(
                        PropertyComparatorRequirement.Operator.EQUAL, property, 2);

        // illegal
        Requirement<ScanReport> requirementNegative =
                new PropertyComparatorRequirement<>(
                        PropertyComparatorRequirement.Operator.EQUAL, property, -2);
        Requirement<ScanReport> requirementNullValue =
                new PropertyComparatorRequirement<>(
                        PropertyComparatorRequirement.Operator.EQUAL, property, null);

        // true cases
        assertTrue(requirementEqual.evaluate(report2)); // 2 == 2
        assertTrue(requirementSmaller.evaluate(report1)); // 1 < 2

        // false cases
        assertFalse(requirementEqual.evaluate(report0)); // property not set in report
        assertFalse(requirementGreater.evaluate(report0)); // property not set in report
        assertFalse(requirementSmaller.evaluate(report0)); // property not set in report

        assertFalse(requirementSmaller.evaluate(report2)); // not 2<2
        assertFalse(requirementEqual.evaluate(report1)); // not 2=1
        assertFalse(requirementGreater.evaluate(report1)); // not 1>2
        assertFalse(requirementGreater.evaluate(report2)); // not 2>2

        // illegal and false
        assertFalse(requirementNegative.evaluate(report2));
        assertFalse(requirementNullValue.evaluate(report2));
    }
}
