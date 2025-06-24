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

import de.rub.nds.scanner.core.TestProbeType;
import de.rub.nds.scanner.core.probe.ProbeType;
import de.rub.nds.scanner.core.probe.ScannerProbe;
import de.rub.nds.scanner.core.report.ScanReport;
import java.io.OutputStream;
import org.junit.jupiter.api.Test;

class ProbeRequirementTest {

    @Test
    void testProbeRequirement() {
        ScanReport report =
                new ScanReport() {
                    @Override
                    void serializeToJson(OutputStream stream) {}

                    @Override
                    String getRemoteName() {
                        return "";
                    }
                };
        TestProbe probe = new TestProbe();

        ProbeRequirement<ScanReport> requirement = new ProbeRequirement<>();
        assertTrue(requirement.evaluate(report));

        requirement = new ProbeRequirement<>(new TestProbeType[0]);
        assertTrue(requirement.evaluate(report));

        requirement = new ProbeRequirement<>(TestProbeType.TEST_PROBE_TYPE);
        assertArrayEquals(
                requirement.getParameters().toArray(new ProbeType[0]),
                new TestProbeType[] {TestProbeType.TEST_PROBE_TYPE});
        assertFalse(requirement.evaluate(report));

        report.markProbeAsExecuted(probe);
        assertTrue(requirement.evaluate(report));
    }

    private static class TestProbe extends ScannerProbe<ScanReport, Object> {

        TestProbe() {
            super(TestProbeType.TEST_PROBE_TYPE);
        }

        @Override
        Requirement<ScanReport> getRequirements() {
            return null;
        }

        @Override
        void adjustConfig(ScanReport report) {}

        @Override
        protected void executeTest() {}

        @Override
        protected void mergeData(ScanReport report) {}
    }
}
