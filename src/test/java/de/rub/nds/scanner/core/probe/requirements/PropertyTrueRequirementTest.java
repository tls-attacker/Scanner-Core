/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.probe.requirements;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.rub.nds.scanner.core.TestAnalyzedProperty;
import de.rub.nds.scanner.core.probe.AnalyzedProperty;
import de.rub.nds.scanner.core.probe.result.TestResults;
import de.rub.nds.scanner.core.report.ScanReport;
import java.io.OutputStream;
import org.junit.jupiter.api.Test;

class PropertyTrueRequirementTest {

    @Test
    void testPropertyRequirement() {
        ScanReport report =
                new ScanReport() {
                    @Override
                    void serializeToJson(OutputStream stream) {}

                    @Override
                    String getRemoteName() {
                        return "";
                    }
                };
        AnalyzedProperty[] property =
                new AnalyzedProperty[] {TestAnalyzedProperty.TEST_ANALYZED_PROPERTY};

        PropertyValueRequirement<ScanReport> requirement = new PropertyTrueRequirement<>();
        assertTrue(requirement.evaluate(report));

        requirement = new PropertyTrueRequirement<>();
        assertTrue(requirement.evaluate(report));

        requirement = new PropertyTrueRequirement<>(property);
        assertArrayEquals(requirement.getParameters().toArray(), property);
        assertFalse(requirement.evaluate(report));

        report.putResult(TestAnalyzedProperty.TEST_ANALYZED_PROPERTY, TestResults.FALSE);
        assertFalse(requirement.evaluate(report));
        report.putResult(TestAnalyzedProperty.TEST_ANALYZED_PROPERTY, TestResults.TRUE);
        assertTrue(requirement.evaluate(report));
    }
}
