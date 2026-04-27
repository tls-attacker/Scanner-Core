/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.guideline;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.rub.nds.scanner.core.guideline.testutil.IOTestGuidelineCheck;
import de.rub.nds.scanner.core.probe.AnalyzedProperty;
import de.rub.nds.scanner.core.probe.AnalyzedPropertyCategory;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Verifies that {@link GuidelineIO#readGuidelines} is safe under concurrent access from JAR
 * protocol.
 *
 * <p><b>Root cause (fixed):</b> {@code JarURLConnection} caches {@code JarFile} instances JVM-wide
 * via {@code JarFileFactory}. All concurrent callers receive the <em>same</em> {@code JarFile}
 * object. The original code wrapped it in a {@code try-with-resources}, so the first thread to
 * finish closed the shared instance and other threads mid-iteration got {@code
 * IllegalStateException: zip file closed} (JDK ≤17) or {@code FileNotFoundException} (JDK 21).
 *
 * <p><b>Fix:</b> {@code listXmlFiles} no longer closes the {@code JarFile}. The JVM's {@code
 * JarFileFactory} owns the lifecycle of cached instances, and closing them from user code is the
 * bug. Keeping the cache also preserves performance — reopening the jar per call would be expensive
 * in high-throughput scanning (millions of connections).
 *
 * <p>Two tests:
 *
 * <ol>
 *   <li>{@link #testUnderlyingJdkCachingStillCausesErrorWithoutFix} — demonstrates the JDK-level
 *       behaviour: when {@code useCaches=true} (the default), closing the {@code JarFile} returned
 *       by {@code getJarFile()} breaks any other thread iterating the same shared instance. This is
 *       why {@code GuidelineIO.listXmlFiles} must not call {@code close()}.
 *   <li>{@link #testConcurrentReadGuidelinesIsCorrectAfterFix} — verifies that all concurrent calls
 *       to {@link GuidelineIO#readGuidelines} return the correct results with the fix in place.
 * </ol>
 */
class GuidelineIOConcurrencyTest {

    private static final int GUIDELINE_FILE_COUNT = 20;

    private static class TestAnalyzedProperty implements AnalyzedProperty {
        private final String name;

        TestAnalyzedProperty(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public AnalyzedPropertyCategory getCategory() {
            return new TestPropertyCategory();
        }
    }

    private Path buildTestJar(Path jarPath, byte[] xmlBytes, int xmlCount) throws Exception {
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(jarPath))) {
            // Explicit directory entry required for JarURLConnection.connect() to resolve the
            // folder URL via getJarEntry("test-guidelines/") without FileNotFoundException
            ZipEntry dirEntry = new ZipEntry("test-guidelines/");
            zos.putNextEntry(dirEntry);
            zos.closeEntry();

            for (int i = 0; i < xmlCount; i++) {
                ZipEntry entry = new ZipEntry("test-guidelines/guideline" + i + ".xml");
                zos.putNextEntry(entry);
                zos.write(xmlBytes);
                zos.closeEntry();
            }
        }
        return jarPath;
    }

    // -------------------------------------------------------------------------
    // TEST 1 — Proves the underlying JDK caching issue still exists.
    //
    // This test operates directly on JarURLConnection with useCaches=true (the
    // default). It shows that two connections to the same URL share one JarFile,
    // and closing it via one reference breaks the other's iterator.
    //
    // This is the mechanism that made the original bug possible and justifies
    // why GuidelineIO.listXmlFiles must not call close() on the cached JarFile.
    //
    //   Thread A  → openConnection (useCaches=true) → getJarFile() → shared JarFile@X
    //             → entries() → nextElement() OK → barrier-1
    //   Thread B  → barrier-1 → openConnection (useCaches=true) → getJarFile() → same JarFile@X
    //             → close() → barrier-2
    //   Thread A  → barrier-2 → entries.nextElement() → THROWS (zip file closed / FNFE)
    // -------------------------------------------------------------------------
    @Test
    void testUnderlyingJdkCachingStillCausesErrorWithoutFix(@TempDir File tempDir)
            throws Exception {

        GuidelineIO io = new GuidelineIO(TestAnalyzedProperty.class);
        Guideline guideline =
                new Guideline(
                        "Barrier Test Guideline",
                        "https://example.com",
                        Arrays.asList(new IOTestGuidelineCheck("Check", RequirementLevel.MUST)));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        io.write(baos, guideline);

        Path jarPath =
                buildTestJar(tempDir.toPath().resolve("barrier-test.jar"), baos.toByteArray(), 5);

        try (URLClassLoader classLoader =
                new URLClassLoader(new java.net.URL[] {jarPath.toUri().toURL()}, null)) {

            URL folderUrl = classLoader.getResource("test-guidelines");
            assertNotNull(folderUrl, "Directory entry must exist in the JAR");
            assertTrue(
                    "jar".equals(folderUrl.getProtocol()),
                    "Expected jar: protocol, got: " + folderUrl.getProtocol());

            CyclicBarrier barrier = new CyclicBarrier(2);
            AtomicReference<Throwable> threadAError = new AtomicReference<>();

            Thread threadA =
                    new Thread(
                            () -> {
                                try {
                                    JarURLConnection conn =
                                            (JarURLConnection) folderUrl.openConnection();
                                    // useCaches defaults to true — gets shared cached JarFile
                                    JarFile jarFile = conn.getJarFile();
                                    Enumeration<JarEntry> entries = jarFile.entries();
                                    assertTrue(entries.hasMoreElements());
                                    entries.nextElement(); // first entry OK

                                    barrier.await(); // signal: I am mid-iteration
                                    barrier.await(); // wait: JarFile is now closed

                                    entries.nextElement(); // must throw
                                } catch (Throwable t) {
                                    threadAError.set(t);
                                }
                            },
                            "ThreadA-iterating");

            Thread threadB =
                    new Thread(
                            () -> {
                                try {
                                    barrier.await(); // wait: Thread A is mid-iteration
                                    JarURLConnection conn =
                                            (JarURLConnection) folderUrl.openConnection();
                                    // useCaches=true → same JarFile instance as Thread A
                                    JarFile sharedJarFile = conn.getJarFile();
                                    sharedJarFile.close(); // closes the shared instance
                                    barrier.await(); // signal: done
                                } catch (Throwable t) {
                                    threadAError.compareAndSet(null, t);
                                }
                            },
                            "ThreadB-closer");

            threadA.start();
            threadB.start();
            threadA.join(10_000);
            threadB.join(10_000);

            Throwable caught = threadAError.get();
            assertNotNull(caught, "Thread A must throw after the shared JarFile is closed");
            boolean isExpectedType =
                    (caught instanceof IllegalStateException
                                    && caught.getMessage() != null
                                    && caught.getMessage().contains("zip file closed"))
                            || (caught instanceof java.io.FileNotFoundException);
            assertTrue(
                    isExpectedType,
                    "Expected IllegalStateException('zip file closed') [JDK ≤17] or "
                            + "FileNotFoundException [JDK 21] but got: "
                            + caught.getClass().getName()
                            + ": "
                            + caught.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // TEST 2 — Verifies the fix: concurrent calls to readGuidelines must all
    // return the correct number of guidelines with no errors and no data loss.
    //
    // The fix (listXmlFiles no longer closes the cached JarFile) means all
    // threads share the JVM-cached instance and nobody closes it from under
    // anyone else. The JVM owns the lifecycle.
    // -------------------------------------------------------------------------
    @Test
    void testConcurrentReadGuidelinesIsCorrectAfterFix(@TempDir File tempDir) throws Exception {

        GuidelineIO io = new GuidelineIO(TestAnalyzedProperty.class);
        Guideline guideline =
                new Guideline(
                        "Concurrency Fix Test Guideline",
                        "https://example.com",
                        Arrays.asList(
                                new IOTestGuidelineCheck("Check1", RequirementLevel.MUST),
                                new IOTestGuidelineCheck("Check2", RequirementLevel.SHOULD)));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        io.write(baos, guideline);

        Path jarPath =
                buildTestJar(
                        tempDir.toPath().resolve("concurrent-test.jar"),
                        baos.toByteArray(),
                        GUIDELINE_FILE_COUNT);

        try (URLClassLoader classLoader =
                new URLClassLoader(new URL[] {jarPath.toUri().toURL()}, null)) {

            int threadCount = 50;
            int iterations = 200;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch startGate = new CountDownLatch(1);

            List<Throwable> errors = Collections.synchronizedList(new ArrayList<>());
            List<Integer> resultCounts = Collections.synchronizedList(new ArrayList<>());
            List<Future<?>> futures = new ArrayList<>();

            for (int i = 0; i < iterations; i++) {
                futures.add(
                        executor.submit(
                                () -> {
                                    try {
                                        startGate.await();
                                        List<Guideline> result =
                                                io.readGuidelines(classLoader, "test-guidelines");
                                        resultCounts.add(result.size());
                                    } catch (InterruptedException e) {
                                        Thread.currentThread().interrupt();
                                    } catch (Exception e) {
                                        errors.add(e);
                                    }
                                }));
            }

            startGate.countDown();
            for (Future<?> future : futures) {
                future.get(30, TimeUnit.SECONDS);
            }
            executor.shutdown();

            assertEquals(
                    0,
                    errors.size(),
                    "No exceptions expected after fix, but got: " + errors.size());

            long wrongCount = resultCounts.stream().filter(c -> c != GUIDELINE_FILE_COUNT).count();
            assertEquals(
                    0,
                    wrongCount,
                    String.format(
                            "All %d calls must return exactly %d guidelines, but %d returned a"
                                    + " different count",
                            iterations, GUIDELINE_FILE_COUNT, wrongCount));
        }
    }
}
