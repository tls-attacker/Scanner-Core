/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.config;

import static org.junit.jupiter.api.Assertions.*;

import de.rub.nds.scanner.core.probe.ProbeType;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExecutorConfigTest {

    private ExecutorConfig config;

    @BeforeEach
    void setUp() {
        config = new ExecutorConfig();
    }

    @Test
    void testNoColorGetterSetter() {
        assertFalse(config.isNoColor());
        config.setNoColor(true);
        assertTrue(config.isNoColor());
        config.setNoColor(false);
        assertFalse(config.isNoColor());
    }

    @Test
    void testScanDetailGetterSetter() {
        assertEquals(ScannerDetail.NORMAL, config.getScanDetail());
        config.setScanDetail(ScannerDetail.ALL);
        assertEquals(ScannerDetail.ALL, config.getScanDetail());
        config.setScanDetail(ScannerDetail.QUICK);
        assertEquals(ScannerDetail.QUICK, config.getScanDetail());
    }

    @Test
    void testPostAnalysisDetailGetterSetter() {
        assertEquals(ScannerDetail.NORMAL, config.getPostAnalysisDetail());
        config.setPostAnalysisDetail(ScannerDetail.DETAILED);
        assertEquals(ScannerDetail.DETAILED, config.getPostAnalysisDetail());
    }

    @Test
    void testReportDetailGetterSetter() {
        assertEquals(ScannerDetail.NORMAL, config.getReportDetail());
        config.setReportDetail(ScannerDetail.ALL);
        assertEquals(ScannerDetail.ALL, config.getReportDetail());
    }

    @Test
    void testOutputFileGetterSetter() {
        assertNull(config.getOutputFile());
        assertFalse(config.isWriteReportToFile());

        config.setOutputFile("test.json");
        assertEquals("test.json", config.getOutputFile());
        assertTrue(config.isWriteReportToFile());

        config.setOutputFile(null);
        assertNull(config.getOutputFile());
        assertFalse(config.isWriteReportToFile());
    }

    @Test
    void testProbeTimeoutGetterSetter() {
        assertEquals(1800000, config.getProbeTimeout());
        config.setProbeTimeout(3600000);
        assertEquals(3600000, config.getProbeTimeout());
    }

    @Test
    void testParallelProbesGetterSetter() {
        assertEquals(1, config.getParallelProbes());
        config.setParallelProbes(4);
        assertEquals(4, config.getParallelProbes());
    }

    @Test
    void testOverallThreadsGetterSetter() {
        assertEquals(1, config.getOverallThreads());
        config.setOverallThreads(8);
        assertEquals(8, config.getOverallThreads());
    }

    @Test
    void testIsMultithreadedWithParallelProbes() {
        assertFalse(config.isMultithreaded());

        config.setParallelProbes(2);
        assertTrue(config.isMultithreaded());

        config.setParallelProbes(1);
        assertFalse(config.isMultithreaded());
    }

    @Test
    void testIsMultithreadedWithOverallThreads() {
        assertFalse(config.isMultithreaded());

        config.setOverallThreads(2);
        assertTrue(config.isMultithreaded());

        config.setOverallThreads(1);
        assertFalse(config.isMultithreaded());
    }

    @Test
    void testIsMultithreadedWithBoth() {
        config.setParallelProbes(2);
        config.setOverallThreads(2);
        assertTrue(config.isMultithreaded());
    }

    @Test
    void testExcludedProbesGetterSetter() {
        assertTrue(config.getExcludedProbes().isEmpty());

        List<ProbeType> excludedProbes = new LinkedList<>();
        excludedProbes.add(new TestProbeType("probe1"));
        excludedProbes.add(new TestProbeType("probe2"));

        config.setExcludedProbes(excludedProbes);
        assertEquals(2, config.getExcludedProbes().size());

        // Test that it returns a copy
        config.getExcludedProbes().clear();
        assertEquals(2, config.getExcludedProbes().size());
    }

    @Test
    void testProbesGetterSetterWithList() {
        assertNull(config.getProbes());

        List<ProbeType> probes = new LinkedList<>();
        probes.add(new TestProbeType("probe1"));
        probes.add(new TestProbeType("probe2"));

        config.setProbes(probes);
        assertNotNull(config.getProbes());
        assertEquals(2, config.getProbes().size());

        // Test that it returns a copy
        config.getProbes().clear();
        assertEquals(2, config.getProbes().size());
    }

    @Test
    void testProbesSetterWithNull() {
        config.setProbes((List<ProbeType>) null);
        assertNull(config.getProbes());
    }

    @Test
    void testProbesSetterWithVarargs() {
        ProbeType probe1 = new TestProbeType("probe1");
        ProbeType probe2 = new TestProbeType("probe2");

        config.setProbes(probe1, probe2);
        assertNotNull(config.getProbes());
        assertEquals(2, config.getProbes().size());
    }

    @Test
    void testAddProbesWithList() {
        assertNull(config.getProbes());

        List<ProbeType> probes1 =
                Arrays.asList(new TestProbeType("probe1"), new TestProbeType("probe2"));
        config.addProbes(probes1);
        assertEquals(2, config.getProbes().size());

        List<ProbeType> probes2 =
                Arrays.asList(new TestProbeType("probe3"), new TestProbeType("probe4"));
        config.addProbes(probes2);
        assertEquals(4, config.getProbes().size());
    }

    @Test
    void testAddProbesWithVarargs() {
        assertNull(config.getProbes());

        ProbeType probe1 = new TestProbeType("probe1");
        ProbeType probe2 = new TestProbeType("probe2");
        config.addProbes(probe1, probe2);
        assertEquals(2, config.getProbes().size());

        ProbeType probe3 = new TestProbeType("probe3");
        config.addProbes(probe3);
        assertEquals(3, config.getProbes().size());
    }

    @Test
    void testAddProbesInitializesListIfNull() {
        assertNull(config.getProbes());
        config.addProbes(new TestProbeType("probe1"));
        assertNotNull(config.getProbes());
        assertEquals(1, config.getProbes().size());
    }

    // Test ProbeType implementation for testing purposes
    private static class TestProbeType implements ProbeType {
        private final String name;

        TestProbeType(String name) {
            this.name = name;
        }

        @Override
        String getName() {
            return name;
        }
    }
}
