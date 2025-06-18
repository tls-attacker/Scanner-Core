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

public class RequirementTest {

    private static class TestScanReport extends ScanReport {
        @Override
        public void serializeToJson(OutputStream stream) {}

        @Override
        public String getRemoteName() {
            return "test";
        }
    }

    private static class TestRequirement extends Requirement<ScanReport> {
        private final boolean evaluationResult;
        private final String name;

        public TestRequirement(boolean evaluationResult, String name) {
            this.evaluationResult = evaluationResult;
            this.name = name;
        }

        @Override
        public boolean evaluate(ScanReport report) {
            return evaluationResult;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @Test
    public void testAndMethodFlattening() {
        TestRequirement req1 = new TestRequirement(true, "req1");
        TestRequirement req2 = new TestRequirement(true, "req2");
        TestRequirement req3 = new TestRequirement(true, "req3");
        
        // Test flattening of nested AND requirements
        AndRequirement<ScanReport> and1 = req1.and(req2);
        AndRequirement<ScanReport> and2 = and1.and(req3);
        
        assertEquals(3, and2.getContainedRequirements().size());
        
        // Test with another AndRequirement
        AndRequirement<ScanReport> and3 = new AndRequirement<>(List.of(req1, req2));
        AndRequirement<ScanReport> and4 = and3.and(req3);
        
        assertEquals(3, and4.getContainedRequirements().size());
    }

    @Test
    public void testAndMethodNoFlattening() {
        TestRequirement req1 = new TestRequirement(true, "req1");
        TestRequirement req2 = new TestRequirement(true, "req2");
        TestRequirement req3 = new TestRequirement(true, "req3");
        
        AndRequirement<ScanReport> and1 = req1.and(req2);
        AndRequirement<ScanReport> and2 = and1.and(req3, false);
        
        assertEquals(2, and2.getContainedRequirements().size());
        assertTrue(and2.getContainedRequirements().get(0) instanceof AndRequirement);
        assertEquals(req3, and2.getContainedRequirements().get(1));
    }

    @Test
    public void testOrMethodFlattening() {
        TestRequirement req1 = new TestRequirement(true, "req1");
        TestRequirement req2 = new TestRequirement(true, "req2");
        TestRequirement req3 = new TestRequirement(true, "req3");
        
        // Test flattening of nested OR requirements
        OrRequirement<ScanReport> or1 = req1.or(req2);
        OrRequirement<ScanReport> or2 = or1.or(req3);
        
        assertEquals(3, or2.getContainedRequirements().size());
        
        // Test with another OrRequirement
        OrRequirement<ScanReport> or3 = new OrRequirement<>(List.of(req1, req2));
        OrRequirement<ScanReport> or4 = or3.or(req3);
        
        assertEquals(3, or4.getContainedRequirements().size());
    }

    @Test
    public void testOrMethodNoFlattening() {
        TestRequirement req1 = new TestRequirement(true, "req1");
        TestRequirement req2 = new TestRequirement(true, "req2");
        TestRequirement req3 = new TestRequirement(true, "req3");
        
        OrRequirement<ScanReport> or1 = req1.or(req2);
        OrRequirement<ScanReport> or2 = or1.or(req3, false);
        
        assertEquals(2, or2.getContainedRequirements().size());
        assertTrue(or2.getContainedRequirements().get(0) instanceof OrRequirement);
        assertEquals(req3, or2.getContainedRequirements().get(1));
    }

    @Test
    public void testNotMethodFlattening() {
        TestRequirement req = new TestRequirement(true, "req");
        
        // Test double negation flattening
        Requirement<ScanReport> not1 = req.not();
        Requirement<ScanReport> not2 = not1.not();
        
        assertSame(req, not2);
        
        // Test that first NOT creates NotRequirement
        assertTrue(not1 instanceof NotRequirement);
    }

    @Test
    public void testNotMethodNoFlattening() {
        TestRequirement req = new TestRequirement(true, "req");
        
        Requirement<ScanReport> not1 = req.not();
        Requirement<ScanReport> not2 = not1.not(false);
        
        assertTrue(not2 instanceof NotRequirement);
        assertNotSame(req, not2);
    }

    @Test
    public void testXorMethod() {
        TestRequirement req1 = new TestRequirement(true, "req1");
        TestRequirement req2 = new TestRequirement(false, "req2");
        
        XorRequirement<ScanReport> xor = req1.xor(req2);
        
        assertNotNull(xor);
        assertEquals(2, xor.getContainedRequirements().size());
        assertEquals(req1, xor.getContainedRequirements().get(0));
        assertEquals(req2, xor.getContainedRequirements().get(1));
    }

    @Test
    public void testGetUnfulfilledRequirementsDefault() {
        TestScanReport report = new TestScanReport();
        
        // Test when requirement evaluates to true
        TestRequirement fulfilledReq = new TestRequirement(true, "fulfilled");
        List<Requirement<ScanReport>> unfulfilled = fulfilledReq.getUnfulfilledRequirements(report);
        assertTrue(unfulfilled.isEmpty());
        
        // Test when requirement evaluates to false
        TestRequirement unfulfilledReq = new TestRequirement(false, "unfulfilled");
        unfulfilled = unfulfilledReq.getUnfulfilledRequirements(report);
        assertEquals(1, unfulfilled.size());
        assertSame(unfulfilledReq, unfulfilled.get(0));
    }

    @Test
    public void testComplexLogicalOperations() {
        TestRequirement req1 = new TestRequirement(true, "req1");
        TestRequirement req2 = new TestRequirement(false, "req2");
        TestRequirement req3 = new TestRequirement(true, "req3");
        TestScanReport report = new TestScanReport();
        
        // Test: (req1 AND req2) OR req3
        Requirement<ScanReport> complex1 = req1.and(req2).or(req3);
        assertTrue(complex1.evaluate(report));
        
        // Test: req1 AND (req2 OR req3)
        Requirement<ScanReport> complex2 = req1.and(req2.or(req3));
        assertTrue(complex2.evaluate(report));
        
        // Test: NOT (req1 AND req3)
        Requirement<ScanReport> complex3 = req1.and(req3).not();
        assertFalse(complex3.evaluate(report));
        
        // Test: req1 XOR req3 (both true, should be false)
        Requirement<ScanReport> complex4 = req1.xor(req3);
        assertFalse(complex4.evaluate(report));
    }

    @Test
    public void testMixedFlatteningBehavior() {
        TestRequirement req1 = new TestRequirement(true, "req1");
        TestRequirement req2 = new TestRequirement(true, "req2");
        TestRequirement req3 = new TestRequirement(true, "req3");
        TestRequirement req4 = new TestRequirement(true, "req4");
        
        // Create nested structure: ((req1 AND req2) AND req3) AND req4
        AndRequirement<ScanReport> and1 = req1.and(req2);
        AndRequirement<ScanReport> and2 = and1.and(req3);
        AndRequirement<ScanReport> and3 = and2.and(req4);
        
        // Should be flattened to 4 requirements
        assertEquals(4, and3.getContainedRequirements().size());
        
        // Now test with mixed flattening
        AndRequirement<ScanReport> mixed1 = req1.and(req2, false);
        AndRequirement<ScanReport> mixed2 = mixed1.and(req3);
        
        // Should have 2 requirements: mixed1 and req3
        assertEquals(2, mixed2.getContainedRequirements().size());
        assertTrue(mixed2.getContainedRequirements().get(0) instanceof AndRequirement);
    }
}