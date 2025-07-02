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
import java.util.List;
import org.junit.jupiter.api.Test;

public class LogicalRequirementTest {

    private static class TestScanReport extends ScanReport {
        @Override
        public void serializeToJson(OutputStream stream) {}

        @Override
        public String getRemoteName() {
            return "test";
        }
    }

    private static class TestLogicalRequirement extends LogicalRequirement<ScanReport> {
        private final List<Requirement<ScanReport>> requirements;
        private final boolean evaluationResult;

        public TestLogicalRequirement(
                List<Requirement<ScanReport>> requirements, boolean evaluationResult) {
            this.requirements = requirements;
            this.evaluationResult = evaluationResult;
        }

        @Override
        public List<Requirement<ScanReport>> getContainedRequirements() {
            return requirements;
        }

        @Override
        public boolean evaluate(ScanReport report) {
            return evaluationResult;
        }

        @Override
        public List<Requirement<ScanReport>> getUnfulfilledRequirements(ScanReport report) {
            return evaluate(report) ? List.of() : List.of(this);
        }
    }

    @Test
    public void testGetContainedRequirements() {
        FulfilledRequirement<ScanReport> req1 = new FulfilledRequirement<>();
        UnfulfillableRequirement<ScanReport> req2 = new UnfulfillableRequirement<>();
        List<Requirement<ScanReport>> requirements = List.of(req1, req2);

        TestLogicalRequirement logicalReq = new TestLogicalRequirement(requirements, true);

        List<Requirement<ScanReport>> contained = logicalReq.getContainedRequirements();
        assertNotNull(contained);
        assertEquals(2, contained.size());
        assertSame(req1, contained.get(0));
        assertSame(req2, contained.get(1));
    }

    @Test
    public void testEmptyContainedRequirements() {
        TestLogicalRequirement logicalReq = new TestLogicalRequirement(List.of(), true);

        List<Requirement<ScanReport>> contained = logicalReq.getContainedRequirements();
        assertNotNull(contained);
        assertTrue(contained.isEmpty());
    }

    @Test
    public void testInheritedMethods() {
        TestLogicalRequirement logicalReq =
                new TestLogicalRequirement(List.of(new FulfilledRequirement<>()), true);
        TestScanReport report = new TestScanReport();

        // Test evaluate
        assertTrue(logicalReq.evaluate(report));

        // Test getUnfulfilledRequirements
        assertTrue(logicalReq.getUnfulfilledRequirements(report).isEmpty());

        // Test logical operations
        Requirement<ScanReport> andReq = logicalReq.and(new FulfilledRequirement<>());
        assertNotNull(andReq);
        assertInstanceOf(AndRequirement.class, andReq);

        Requirement<ScanReport> orReq = logicalReq.or(new UnfulfillableRequirement<>());
        assertNotNull(orReq);
        assertInstanceOf(OrRequirement.class, orReq);

        Requirement<ScanReport> notReq = logicalReq.not();
        assertNotNull(notReq);
        assertInstanceOf(NotRequirement.class, notReq);

        Requirement<ScanReport> xorReq = logicalReq.xor(new FulfilledRequirement<>());
        assertNotNull(xorReq);
        assertInstanceOf(XorRequirement.class, xorReq);
    }
}
