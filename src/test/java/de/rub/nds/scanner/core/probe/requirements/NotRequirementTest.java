/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.probe.requirements;

import static org.junit.jupiter.api.Assertions.*;

import de.rub.nds.scanner.core.report.ScanReport;
import java.io.OutputStream;
import org.junit.jupiter.api.Test;

class NotRequirementTest {

    @Test
    void testNotRequirement() {
        ScanReport report =
                new ScanReport() {
                    @Override
                    void serializeToJson(OutputStream stream) {}

                    @Override
                    String getRemoteName() {
                        return "";
                    }
                };
        Requirement<ScanReport>
                requirement1 = new NotRequirement<>(new UnfulfillableRequirement<>()),
                requirement2 = new NotRequirement<>(new FulfilledRequirement<>());
        assertTrue(requirement1.evaluate(report));
        assertFalse(requirement2.evaluate(report));
        assertInstanceOf(UnfulfillableRequirement.class, requirement1.not());
        assertInstanceOf(FulfilledRequirement.class, requirement2.not());
    }
}
