/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.execution;

import static org.junit.jupiter.api.Assertions.*;

import de.rub.nds.scanner.core.afterprobe.AfterProbe;
import de.rub.nds.scanner.core.probe.ScannerProbe;
import de.rub.nds.scanner.core.report.ScanReport;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ScanJobTest {

    // Mock classes for testing
    static class TestReport extends ScanReport {
        @Override
        public String getRemoteName() {
            return "TestHost";
        }
    }

    static class TestState {}

    static class TestProbe extends ScannerProbe<TestReport, TestState> {
        private final String name;

        TestProbe(String name) {
            super(new TestProbeType(name));
            this.name = name;
        }

        @Override
        public void executeTest() {}

        @Override
        public de.rub.nds.scanner.core.probe.requirements.Requirement<TestReport> getRequirements() {
            return report -> true;
        }

        @Override
        public void adjustConfig(TestReport report) {}

        @Override
        protected void mergeData(TestReport report) {}
    }

    static class TestAfterProbe extends AfterProbe<TestReport> {
        private final String name;

        TestAfterProbe(String name) {
            this.name = name;
        }

        @Override
        public void analyze(TestReport report) {}

        public String getName() {
            return name;
        }
    }

    static class TestProbeType implements de.rub.nds.scanner.core.probe.ProbeType {
        private final String name;

        TestProbeType(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    @Test
    public void testConstructorWithEmptyLists() {
        List<TestProbe> probeList = new ArrayList<>();
        List<TestAfterProbe> afterList = new ArrayList<>();

        ScanJob<TestReport, TestProbe, TestAfterProbe, TestState> scanJob =
                new ScanJob<>(probeList, afterList);

        assertNotNull(scanJob.getProbeList());
        assertNotNull(scanJob.getAfterList());
        assertTrue(scanJob.getProbeList().isEmpty());
        assertTrue(scanJob.getAfterList().isEmpty());
    }

    @Test
    public void testConstructorWithNonEmptyLists() {
        List<TestProbe> probeList = new ArrayList<>();
        probeList.add(new TestProbe("probe1"));
        probeList.add(new TestProbe("probe2"));

        List<TestAfterProbe> afterList = new ArrayList<>();
        afterList.add(new TestAfterProbe("after1"));

        ScanJob<TestReport, TestProbe, TestAfterProbe, TestState> scanJob =
                new ScanJob<>(probeList, afterList);

        assertEquals(2, scanJob.getProbeList().size());
        assertEquals(1, scanJob.getAfterList().size());
    }

    @Test
    public void testGetProbeListReturnsCopy() {
        List<TestProbe> probeList = new ArrayList<>();
        TestProbe probe = new TestProbe("probe1");
        probeList.add(probe);

        ScanJob<TestReport, TestProbe, TestAfterProbe, TestState> scanJob =
                new ScanJob<>(probeList, new ArrayList<>());

        List<TestProbe> returnedList = scanJob.getProbeList();
        returnedList.add(new TestProbe("probe2"));

        // Original list should not be modified
        assertEquals(1, scanJob.getProbeList().size());
        assertEquals(2, returnedList.size());
    }

    @Test
    public void testGetAfterListReturnsCopy() {
        List<TestAfterProbe> afterList = new ArrayList<>();
        TestAfterProbe afterProbe = new TestAfterProbe("after1");
        afterList.add(afterProbe);

        ScanJob<TestReport, TestProbe, TestAfterProbe, TestState> scanJob =
                new ScanJob<>(new ArrayList<>(), afterList);

        List<TestAfterProbe> returnedList = scanJob.getAfterList();
        returnedList.add(new TestAfterProbe("after2"));

        // Original list should not be modified
        assertEquals(1, scanJob.getAfterList().size());
        assertEquals(2, returnedList.size());
    }

    @Test
    public void testImmutabilityOfInternalLists() {
        List<TestProbe> probeList = new ArrayList<>();
        probeList.add(new TestProbe("probe1"));

        List<TestAfterProbe> afterList = new ArrayList<>();
        afterList.add(new TestAfterProbe("after1"));

        ScanJob<TestReport, TestProbe, TestAfterProbe, TestState> scanJob =
                new ScanJob<>(probeList, afterList);

        // Modify original lists
        probeList.add(new TestProbe("probe2"));
        afterList.add(new TestAfterProbe("after2"));

        // ScanJob should not be affected
        assertEquals(1, scanJob.getProbeList().size());
        assertEquals(1, scanJob.getAfterList().size());
    }

    @Test
    public void testPreservesListOrder() {
        List<TestProbe> probeList = new ArrayList<>();
        TestProbe probe1 = new TestProbe("probe1");
        TestProbe probe2 = new TestProbe("probe2");
        TestProbe probe3 = new TestProbe("probe3");
        probeList.add(probe1);
        probeList.add(probe2);
        probeList.add(probe3);

        ScanJob<TestReport, TestProbe, TestAfterProbe, TestState> scanJob =
                new ScanJob<>(probeList, new ArrayList<>());

        List<TestProbe> returnedList = scanJob.getProbeList();
        assertEquals("probe1", returnedList.get(0).getProbeName());
        assertEquals("probe2", returnedList.get(1).getProbeName());
        assertEquals("probe3", returnedList.get(2).getProbeName());
    }
}
