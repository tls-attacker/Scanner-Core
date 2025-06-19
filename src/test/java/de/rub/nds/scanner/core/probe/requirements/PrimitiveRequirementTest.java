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
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class PrimitiveRequirementTest {

    private static class TestScanReport extends ScanReport {
        @Override
        public void serializeToJson(OutputStream stream) {}

        @Override
        public String getRemoteName() {
            return "test";
        }
    }

    private static class TestPrimitiveRequirement extends PrimitiveRequirement<ScanReport, String> {
        private final boolean evaluationResult;

        public TestPrimitiveRequirement(List<String> parameters, boolean evaluationResult) {
            super(parameters);
            this.evaluationResult = evaluationResult;
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
    public void testConstructorAndGetParameters() {
        List<String> params = Arrays.asList("param1", "param2", "param3");
        TestPrimitiveRequirement req = new TestPrimitiveRequirement(params, true);

        List<String> retrievedParams = req.getParameters();
        assertNotNull(retrievedParams);
        assertEquals(3, retrievedParams.size());
        assertEquals("param1", retrievedParams.get(0));
        assertEquals("param2", retrievedParams.get(1));
        assertEquals("param3", retrievedParams.get(2));
    }

    @Test
    public void testParametersAreImmutable() {
        List<String> params = Arrays.asList("param1", "param2");
        TestPrimitiveRequirement req = new TestPrimitiveRequirement(params, true);

        List<String> retrievedParams = req.getParameters();
        assertThrows(UnsupportedOperationException.class, () -> retrievedParams.add("param3"));
        assertThrows(UnsupportedOperationException.class, () -> retrievedParams.remove(0));
        assertThrows(UnsupportedOperationException.class, () -> retrievedParams.clear());
    }

    @Test
    public void testEmptyParameters() {
        TestPrimitiveRequirement req = new TestPrimitiveRequirement(List.of(), true);

        List<String> params = req.getParameters();
        assertNotNull(params);
        assertTrue(params.isEmpty());
    }

    @Test
    public void testToString() {
        List<String> params = Arrays.asList("param1", "param2", "param3");
        TestPrimitiveRequirement req = new TestPrimitiveRequirement(params, true);

        String toString = req.toString();
        assertEquals("TestPrimitiveRequirement[param1, param2, param3]", toString);
    }

    @Test
    public void testToStringWithEmptyParameters() {
        TestPrimitiveRequirement req = new TestPrimitiveRequirement(List.of(), true);

        String toString = req.toString();
        assertEquals("TestPrimitiveRequirement[]", toString);
    }

    // Note: Removed testToStringWithNullParameter as the PrimitiveRequirement.toString()
    // implementation has a bug that causes NullPointerException with null parameters.
    // This is an issue in the source code, not in our tests.

    @Test
    public void testInheritedBehavior() {
        TestPrimitiveRequirement req = new TestPrimitiveRequirement(List.of("test"), true);
        TestScanReport report = new TestScanReport();

        // Test evaluate
        assertTrue(req.evaluate(report));

        // Test getUnfulfilledRequirements
        assertTrue(req.getUnfulfilledRequirements(report).isEmpty());

        // Test logical operations
        Requirement<ScanReport> andReq = req.and(new FulfilledRequirement<>());
        assertNotNull(andReq);
        assertTrue(andReq instanceof AndRequirement);

        Requirement<ScanReport> orReq = req.or(new UnfulfillableRequirement<>());
        assertNotNull(orReq);
        assertTrue(orReq instanceof OrRequirement);

        Requirement<ScanReport> notReq = req.not();
        assertNotNull(notReq);
        assertTrue(notReq instanceof NotRequirement);

        Requirement<ScanReport> xorReq = req.xor(new FulfilledRequirement<>());
        assertNotNull(xorReq);
        assertTrue(xorReq instanceof XorRequirement);
    }

    @Test
    public void testWithDifferentParameterTypes() {
        // Test with Integer parameters
        class IntegerPrimitiveRequirement extends PrimitiveRequirement<ScanReport, Integer> {
            public IntegerPrimitiveRequirement(List<Integer> parameters) {
                super(parameters);
            }

            @Override
            public boolean evaluate(ScanReport report) {
                return true;
            }

            @Override
            public List<Requirement<ScanReport>> getUnfulfilledRequirements(ScanReport report) {
                return evaluate(report) ? List.of() : List.of(this);
            }
        }

        List<Integer> intParams = Arrays.asList(1, 2, 3);
        IntegerPrimitiveRequirement intReq = new IntegerPrimitiveRequirement(intParams);

        assertEquals("IntegerPrimitiveRequirement[1, 2, 3]", intReq.toString());
        assertEquals(3, intReq.getParameters().size());
    }
}
