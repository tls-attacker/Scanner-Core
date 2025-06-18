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

public class FulfilledRequirementTest {

    private static class TestScanReport extends ScanReport {
        @Override
        public void serializeToJson(OutputStream stream) {}

        @Override
        public String getRemoteName() {
            return "test";
        }
    }

    @Test
    public void testEvaluateAlwaysReturnsTrue() {
        FulfilledRequirement<ScanReport> requirement = new FulfilledRequirement<>();
        TestScanReport report = new TestScanReport();
        
        assertTrue(requirement.evaluate(report));
        assertTrue(requirement.evaluate(null)); // Should work even with null
    }

    @Test
    public void testGetUnfulfilledRequirementsReturnsEmptyList() {
        FulfilledRequirement<ScanReport> requirement = new FulfilledRequirement<>();
        TestScanReport report = new TestScanReport();
        
        List<Requirement<ScanReport>> unfulfilled = requirement.getUnfulfilledRequirements(report);
        assertNotNull(unfulfilled);
        assertTrue(unfulfilled.isEmpty());
        
        // Should work even with null
        unfulfilled = requirement.getUnfulfilledRequirements(null);
        assertNotNull(unfulfilled);
        assertTrue(unfulfilled.isEmpty());
    }

    @Test
    public void testToString() {
        FulfilledRequirement<ScanReport> requirement = new FulfilledRequirement<>();
        assertEquals("FulfilledRequirement", requirement.toString());
    }

    @Test
    public void testInLogicalOperations() {
        FulfilledRequirement<ScanReport> fulfilled = new FulfilledRequirement<>();
        UnfulfillableRequirement<ScanReport> unfulfillable = new UnfulfillableRequirement<>();
        TestScanReport report = new TestScanReport();
        
        // Test AND operations
        assertTrue(fulfilled.and(fulfilled).evaluate(report));
        assertFalse(fulfilled.and(unfulfillable).evaluate(report));
        
        // Test OR operations
        assertTrue(fulfilled.or(fulfilled).evaluate(report));
        assertTrue(fulfilled.or(unfulfillable).evaluate(report));
        
        // Test NOT operation
        assertFalse(fulfilled.not().evaluate(report));
        
        // Test XOR operation
        assertFalse(fulfilled.xor(fulfilled).evaluate(report));
        assertTrue(fulfilled.xor(unfulfillable).evaluate(report));
    }
}