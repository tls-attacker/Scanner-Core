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

import de.rub.nds.scanner.core.report.ScanReport;
import org.junit.jupiter.api.Test;

public class ScanJobExecutorTest {

    // Mock ScanReport for testing
    static class TestReport extends ScanReport {
        private boolean executed = false;

        @Override
        public String getRemoteName() {
            return "TestHost";
        }

        @Override
        public void serializeToJson(java.io.OutputStream outputStream) {
            // Simple implementation for testing
        }

        public boolean isExecuted() {
            return executed;
        }

        public void setExecuted(boolean executed) {
            this.executed = executed;
        }
    }

    // Test implementation of ScanJobExecutor
    static class TestScanJobExecutor extends ScanJobExecutor<TestReport> {
        private boolean shutdownCalled = false;
        private int executeCallCount = 0;

        @Override
        public void execute(TestReport report) throws InterruptedException {
            executeCallCount++;
            report.setExecuted(true);
        }

        @Override
        public void shutdown() {
            shutdownCalled = true;
        }

        public boolean isShutdownCalled() {
            return shutdownCalled;
        }

        public int getExecuteCallCount() {
            return executeCallCount;
        }
    }

    // Test implementation that throws InterruptedException
    static class InterruptingExecutor extends ScanJobExecutor<TestReport> {
        @Override
        public void execute(TestReport report) throws InterruptedException {
            throw new InterruptedException("Test interruption");
        }

        @Override
        public void shutdown() {}
    }

    @Test
    public void testExecuteMethod() throws InterruptedException {
        TestScanJobExecutor executor = new TestScanJobExecutor();
        TestReport report = new TestReport();

        assertFalse(report.isExecuted());
        executor.execute(report);

        assertTrue(report.isExecuted());
        assertEquals(1, executor.getExecuteCallCount());
    }

    @Test
    public void testShutdownMethod() {
        TestScanJobExecutor executor = new TestScanJobExecutor();

        assertFalse(executor.isShutdownCalled());
        executor.shutdown();
        assertTrue(executor.isShutdownCalled());
    }

    @Test
    public void testMultipleExecuteCalls() throws InterruptedException {
        TestScanJobExecutor executor = new TestScanJobExecutor();
        TestReport report1 = new TestReport();
        TestReport report2 = new TestReport();
        TestReport report3 = new TestReport();

        executor.execute(report1);
        executor.execute(report2);
        executor.execute(report3);

        assertEquals(3, executor.getExecuteCallCount());
        assertTrue(report1.isExecuted());
        assertTrue(report2.isExecuted());
        assertTrue(report3.isExecuted());
    }

    @Test
    public void testExecuteThrowsInterruptedException() {
        InterruptingExecutor executor = new InterruptingExecutor();
        TestReport report = new TestReport();

        assertThrows(InterruptedException.class, () -> executor.execute(report));
    }

    @Test
    public void testAbstractClass() {
        // Verify that ScanJobExecutor is abstract and cannot be instantiated directly
        assertTrue(java.lang.reflect.Modifier.isAbstract(ScanJobExecutor.class.getModifiers()));
    }

    @Test
    public void testExecuteMethodSignature() throws NoSuchMethodException {
        // Verify the execute method signature
        var method = ScanJobExecutor.class.getDeclaredMethod("execute", ScanReport.class);
        assertTrue(java.lang.reflect.Modifier.isAbstract(method.getModifiers()));
        assertEquals(void.class, method.getReturnType());

        // Check that it declares InterruptedException
        Class<?>[] exceptionTypes = method.getExceptionTypes();
        assertEquals(1, exceptionTypes.length);
        assertEquals(InterruptedException.class, exceptionTypes[0]);
    }

    @Test
    public void testShutdownMethodSignature() throws NoSuchMethodException {
        // Verify the shutdown method signature
        var method = ScanJobExecutor.class.getDeclaredMethod("shutdown");
        assertTrue(java.lang.reflect.Modifier.isAbstract(method.getModifiers()));
        assertEquals(void.class, method.getReturnType());
        assertEquals(0, method.getExceptionTypes().length);
    }
}
