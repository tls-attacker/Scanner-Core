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

public class UnfulfillableRequirementTest {

    private static class TestScanReport extends ScanReport {
        @Override
        public void serializeToJson(OutputStream stream) {}

        @Override
        public String getRemoteName() {
            return "test";
        }
    }

    @Test
    public void testEvaluateAlwaysReturnsFalse() {
        UnfulfillableRequirement<ScanReport> requirement = new UnfulfillableRequirement<>();
        TestScanReport report = new TestScanReport();
        
        assertFalse(requirement.evaluate(report));
        assertFalse(requirement.evaluate(null)); // Should work even with null
    }

    @Test
    public void testGetUnfulfilledRequirementsReturnsSelf() {
        UnfulfillableRequirement<ScanReport> requirement = new UnfulfillableRequirement<>();
        TestScanReport report = new TestScanReport();
        
        List<Requirement<ScanReport>> unfulfilled = requirement.getUnfulfilledRequirements(report);
        assertNotNull(unfulfilled);
        assertEquals(1, unfulfilled.size());
        assertSame(requirement, unfulfilled.get(0));
        
        // Should work even with null
        unfulfilled = requirement.getUnfulfilledRequirements(null);
        assertNotNull(unfulfilled);
        assertEquals(1, unfulfilled.size());
        assertSame(requirement, unfulfilled.get(0));
    }

    @Test
    public void testToString() {
        UnfulfillableRequirement<ScanReport> requirement = new UnfulfillableRequirement<>();
        assertEquals("UnfulfillableRequirement", requirement.toString());
    }

    @Test
    public void testInLogicalOperations() {
        UnfulfillableRequirement<ScanReport> unfulfillable = new UnfulfillableRequirement<>();
        FulfilledRequirement<ScanReport> fulfilled = new FulfilledRequirement<>();
        TestScanReport report = new TestScanReport();
        
        // Test AND operations
        assertFalse(unfulfillable.and(unfulfillable).evaluate(report));
        assertFalse(unfulfillable.and(fulfilled).evaluate(report));
        
        // Test OR operations
        assertFalse(unfulfillable.or(unfulfillable).evaluate(report));
        assertTrue(unfulfillable.or(fulfilled).evaluate(report));
        
        // Test NOT operation
        assertTrue(unfulfillable.not().evaluate(report));
        
        // Test XOR operation
        assertFalse(unfulfillable.xor(unfulfillable).evaluate(report));
        assertTrue(unfulfillable.xor(fulfilled).evaluate(report));
    }

    @Test
    public void testMultipleInstances() {
        UnfulfillableRequirement<ScanReport> req1 = new UnfulfillableRequirement<>();
        UnfulfillableRequirement<ScanReport> req2 = new UnfulfillableRequirement<>();
        TestScanReport report = new TestScanReport();
        
        // Both should behave identically
        assertFalse(req1.evaluate(report));
        assertFalse(req2.evaluate(report));
        
        assertEquals(req1.toString(), req2.toString());
        
        // But they are different instances
        assertNotSame(req1, req2);
    }

    @Test
    public void testAsPartOfComplexRequirement() {
        UnfulfillableRequirement<ScanReport> unfulfillable = new UnfulfillableRequirement<>();
        FulfilledRequirement<ScanReport> fulfilled1 = new FulfilledRequirement<>();
        FulfilledRequirement<ScanReport> fulfilled2 = new FulfilledRequirement<>();
        TestScanReport report = new TestScanReport();
        
        // Complex requirement: (fulfilled1 AND fulfilled2) OR unfulfillable
        Requirement<ScanReport> complex = fulfilled1.and(fulfilled2).or(unfulfillable);
        assertTrue(complex.evaluate(report));
        
        // Complex requirement: (fulfilled1 OR fulfilled2) AND unfulfillable
        complex = fulfilled1.or(fulfilled2).and(unfulfillable);
        assertFalse(complex.evaluate(report));
        
        // Complex requirement: NOT(unfulfillable) AND fulfilled1
        complex = unfulfillable.not().and(fulfilled1);
        assertTrue(complex.evaluate(report));
    }
}