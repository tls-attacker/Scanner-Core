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

public class XorRequirementTest {

    private static class TestScanReport extends ScanReport {
        @Override
        public void serializeToJson(OutputStream stream) {}

        @Override
        public String getRemoteName() {
            return "test";
        }
    }

    @Test
    public void testConstructor() {
        FulfilledRequirement<ScanReport> req1 = new FulfilledRequirement<>();
        UnfulfillableRequirement<ScanReport> req2 = new UnfulfillableRequirement<>();

        XorRequirement<ScanReport> xor = new XorRequirement<>(req1, req2);

        List<Requirement<ScanReport>> contained = xor.getContainedRequirements();
        assertEquals(2, contained.size());
        assertSame(req1, contained.getFirst());
        assertSame(req2, contained.get(1));
    }

    @Test
    public void testEvaluateTrueXorTrue() {
        FulfilledRequirement<ScanReport> req1 = new FulfilledRequirement<>();
        FulfilledRequirement<ScanReport> req2 = new FulfilledRequirement<>();
        TestScanReport report = new TestScanReport();

        XorRequirement<ScanReport> xor = new XorRequirement<>(req1, req2);

        // true XOR true = false
        assertFalse(xor.evaluate(report));
    }

    @Test
    public void testEvaluateTrueXorFalse() {
        FulfilledRequirement<ScanReport> req1 = new FulfilledRequirement<>();
        UnfulfillableRequirement<ScanReport> req2 = new UnfulfillableRequirement<>();
        TestScanReport report = new TestScanReport();

        XorRequirement<ScanReport> xor = new XorRequirement<>(req1, req2);

        // true XOR false = true
        assertTrue(xor.evaluate(report));
    }

    @Test
    public void testEvaluateFalseXorTrue() {
        UnfulfillableRequirement<ScanReport> req1 = new UnfulfillableRequirement<>();
        FulfilledRequirement<ScanReport> req2 = new FulfilledRequirement<>();
        TestScanReport report = new TestScanReport();

        XorRequirement<ScanReport> xor = new XorRequirement<>(req1, req2);

        // false XOR true = true
        assertTrue(xor.evaluate(report));
    }

    @Test
    public void testEvaluateFalseXorFalse() {
        UnfulfillableRequirement<ScanReport> req1 = new UnfulfillableRequirement<>();
        UnfulfillableRequirement<ScanReport> req2 = new UnfulfillableRequirement<>();
        TestScanReport report = new TestScanReport();

        XorRequirement<ScanReport> xor = new XorRequirement<>(req1, req2);

        // false XOR false = false
        assertFalse(xor.evaluate(report));
    }

    @Test
    public void testToString() {
        FulfilledRequirement<ScanReport> req1 = new FulfilledRequirement<>();
        UnfulfillableRequirement<ScanReport> req2 = new UnfulfillableRequirement<>();

        XorRequirement<ScanReport> xor = new XorRequirement<>(req1, req2);

        String expected = "(FulfilledRequirement xor UnfulfillableRequirement)";
        assertEquals(expected, xor.toString());
    }

    @Test
    public void testGetUnfulfilledRequirements() {
        FulfilledRequirement<ScanReport> req1 = new FulfilledRequirement<>();
        UnfulfillableRequirement<ScanReport> req2 = new UnfulfillableRequirement<>();
        TestScanReport report = new TestScanReport();

        // When XOR evaluates to true
        XorRequirement<ScanReport> xorTrue = new XorRequirement<>(req1, req2);
        List<Requirement<ScanReport>> unfulfilledTrue = xorTrue.getUnfulfilledRequirements(report);
        assertTrue(unfulfilledTrue.isEmpty());

        // When XOR evaluates to false
        XorRequirement<ScanReport> xorFalse = new XorRequirement<>(req1, req1);
        List<Requirement<ScanReport>> unfulfilledFalse =
                xorFalse.getUnfulfilledRequirements(report);
        assertEquals(1, unfulfilledFalse.size());
        assertSame(xorFalse, unfulfilledFalse.getFirst());
    }

    @Test
    public void testWithComplexRequirements() {
        // Create complex requirements: (A AND B) XOR (C OR D)
        FulfilledRequirement<ScanReport> reqA = new FulfilledRequirement<>();
        FulfilledRequirement<ScanReport> reqB = new FulfilledRequirement<>();
        UnfulfillableRequirement<ScanReport> reqC = new UnfulfillableRequirement<>();
        UnfulfillableRequirement<ScanReport> reqD = new UnfulfillableRequirement<>();
        TestScanReport report = new TestScanReport();

        AndRequirement<ScanReport> andReq = new AndRequirement<>(List.of(reqA, reqB));
        OrRequirement<ScanReport> orReq = new OrRequirement<>(List.of(reqC, reqD));

        XorRequirement<ScanReport> xor = new XorRequirement<>(andReq, orReq);

        // (true AND true) XOR (false OR false) = true XOR false = true
        assertTrue(xor.evaluate(report));
        assertEquals(2, xor.getContainedRequirements().size());
    }

    @Test
    public void testNestedXorRequirements() {
        FulfilledRequirement<ScanReport> req1 = new FulfilledRequirement<>();
        UnfulfillableRequirement<ScanReport> req2 = new UnfulfillableRequirement<>();
        FulfilledRequirement<ScanReport> req3 = new FulfilledRequirement<>();
        TestScanReport report = new TestScanReport();

        // Create nested XOR: (req1 XOR req2) XOR req3
        XorRequirement<ScanReport> xor1 = new XorRequirement<>(req1, req2);
        XorRequirement<ScanReport> xor2 = new XorRequirement<>(xor1, req3);

        // (true XOR false) XOR true = true XOR true = false
        assertFalse(xor2.evaluate(report));

        String expectedString =
                "((FulfilledRequirement xor UnfulfillableRequirement) xor FulfilledRequirement)";
        assertEquals(expectedString, xor2.toString());
    }

    @Test
    public void testInheritedMethods() {
        FulfilledRequirement<ScanReport> req1 = new FulfilledRequirement<>();
        UnfulfillableRequirement<ScanReport> req2 = new UnfulfillableRequirement<>();
        TestScanReport report = new TestScanReport();

        XorRequirement<ScanReport> xor = new XorRequirement<>(req1, req2);

        // Test logical operations on XorRequirement
        Requirement<ScanReport> andReq = xor.and(new FulfilledRequirement<>());
        assertInstanceOf(AndRequirement.class, andReq);
        assertTrue(andReq.evaluate(report)); // true AND true = true

        Requirement<ScanReport> orReq = xor.or(new UnfulfillableRequirement<>());
        assertInstanceOf(OrRequirement.class, orReq);
        assertTrue(orReq.evaluate(report)); // true OR false = true

        Requirement<ScanReport> notReq = xor.not();
        assertInstanceOf(NotRequirement.class, notReq);
        assertFalse(notReq.evaluate(report)); // NOT(true) = false

        Requirement<ScanReport> xorReq = xor.xor(new FulfilledRequirement<>());
        assertInstanceOf(XorRequirement.class, xorReq);
        assertFalse(xorReq.evaluate(report)); // true XOR true = false
    }
}
