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

public class ExecutorConfigTest {

    private ExecutorConfig config;

    @BeforeEach
    public void setUp() {
        config = new ExecutorConfig();
    }

    @Test
    public void testDefaultConstructor() {
        assertNotNull(config);
        assertFalse(config.isNoColor());
        assertEquals(ScannerDetail.NORMAL, config.getScanDetail());
        assertEquals(ScannerDetail.NORMAL, config.getPostAnalysisDetail());
        assertEquals(ScannerDetail.NORMAL, config.getReportDetail());
        assertNull(config.getOutputFile());
        assertEquals(1800000, config.getProbeTimeout());
        assertEquals(1, config.getParallelProbes());
        assertEquals(1, config.getOverallThreads());
        assertNotNull(config.getExcludedProbes());
        assertTrue(config.getExcludedProbes().isEmpty());
        assertNull(config.getProbes());
    }

    @Test
    public void testNoColorGetterSetter() {
        assertFalse(config.isNoColor());
        config.setNoColor(true);
        assertTrue(config.isNoColor());
        config.setNoColor(false);
        assertFalse(config.isNoColor());
    }

    @Test
    public void testScanDetailGetterSetter() {
        assertEquals(ScannerDetail.NORMAL, config.getScanDetail());
        config.setScanDetail(ScannerDetail.ALL);
        assertEquals(ScannerDetail.ALL, config.getScanDetail());
        config.setScanDetail(ScannerDetail.QUICK);
        assertEquals(ScannerDetail.QUICK, config.getScanDetail());
    }

    @Test
    public void testPostAnalysisDetailGetterSetter() {
        assertEquals(ScannerDetail.NORMAL, config.getPostAnalysisDetail());
        config.setPostAnalysisDetail(ScannerDetail.DETAILED);
        assertEquals(ScannerDetail.DETAILED, config.getPostAnalysisDetail());
    }

    @Test
    public void testReportDetailGetterSetter() {
        assertEquals(ScannerDetail.NORMAL, config.getReportDetail());
        config.setReportDetail(ScannerDetail.ALL);
        assertEquals(ScannerDetail.ALL, config.getReportDetail());
    }

    @Test
    public void testOutputFileGetterSetter() {
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
    public void testProbeTimeoutGetterSetter() {
        assertEquals(1800000, config.getProbeTimeout());
        config.setProbeTimeout(3600000);
        assertEquals(3600000, config.getProbeTimeout());
    }

    @Test
    public void testParallelProbesGetterSetter() {
        assertEquals(1, config.getParallelProbes());
        config.setParallelProbes(4);
        assertEquals(4, config.getParallelProbes());
    }

    @Test
    public void testOverallThreadsGetterSetter() {
        assertEquals(1, config.getOverallThreads());
        config.setOverallThreads(8);
        assertEquals(8, config.getOverallThreads());
    }

    @Test
    public void testIsMultithreadedWithParallelProbes() {
        assertFalse(config.isMultithreaded());

        config.setParallelProbes(2);
        assertTrue(config.isMultithreaded());

        config.setParallelProbes(1);
        assertFalse(config.isMultithreaded());
    }

    @Test
    public void testIsMultithreadedWithOverallThreads() {
        assertFalse(config.isMultithreaded());

        config.setOverallThreads(2);
        assertTrue(config.isMultithreaded());

        config.setOverallThreads(1);
        assertFalse(config.isMultithreaded());
    }

    @Test
    public void testIsMultithreadedWithBoth() {
        config.setParallelProbes(2);
        config.setOverallThreads(2);
        assertTrue(config.isMultithreaded());
    }

    @Test
    public void testExcludedProbesGetterSetter() {
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
    public void testProbesGetterSetterWithList() {
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
    public void testProbesSetterWithNull() {
        config.setProbes((List<ProbeType>) null);
        assertNull(config.getProbes());
    }

    @Test
    public void testProbesSetterWithVarargs() {
        ProbeType probe1 = new TestProbeType("probe1");
        ProbeType probe2 = new TestProbeType("probe2");

        config.setProbes(probe1, probe2);
        assertNotNull(config.getProbes());
        assertEquals(2, config.getProbes().size());
    }

    @Test
    public void testAddProbesWithList() {
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
    public void testAddProbesWithVarargs() {
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
    public void testAddProbesInitializesListIfNull() {
        assertNull(config.getProbes());
        config.addProbes(new TestProbeType("probe1"));
        assertNotNull(config.getProbes());
        assertEquals(1, config.getProbes().size());
    }

    // Test ProbeType implementation for testing purposes
    private static class TestProbeType implements ProbeType {
        private final String name;

        public TestProbeType(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
