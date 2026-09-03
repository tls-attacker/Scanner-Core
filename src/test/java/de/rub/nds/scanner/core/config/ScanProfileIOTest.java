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

import de.rub.nds.scanner.core.TestProbeType;
import de.rub.nds.scanner.core.probe.ProbeType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ScanProfileIOTest {

    @TempDir Path tempDir;

    private void writeProfile(String fileName, String content) throws IOException {
        Path path = tempDir.resolve(fileName);
        Files.createDirectories(path.getParent());
        Files.writeString(path, content);
    }

    @Test
    public void testResolveSingleProfileWithoutInheritance() throws IOException {
        writeProfile(
                "solo.json",
                "{"
                        + "\"name\": \"solo\","
                        + "\"inheritedFromProfiles\": [],"
                        + "\"probes\": {\""
                        + TestProbeType.class.getName()
                        + "\": [\"TEST_PROBE_TYPE\"]}"
                        + "}");

        List<ProbeType> probes = ScanProfileIO.resolveProbes(tempDir.resolve("solo.json"));

        assertEquals(1, probes.size());
        assertEquals(TestProbeType.TEST_PROBE_TYPE, probes.get(0));
    }

    @Test
    public void testResolveProfileWithInheritanceCombinesDifferentProbeTypeImplementations()
            throws IOException {
        writeProfile(
                "base.json",
                "{"
                        + "\"name\": \"base\","
                        + "\"probes\": {\""
                        + TestProbeType.class.getName()
                        + "\": [\"TEST_PROBE_TYPE\"]}"
                        + "}");
        writeProfile(
                "combined.json",
                "{"
                        + "\"name\": \"combined\","
                        + "\"inheritedFromProfiles\": [\"base.json\"],"
                        + "\"probes\": {\""
                        + SecondTestProbeType.class.getName()
                        + "\": [\"SECOND_TEST_PROBE_TYPE\"]}"
                        + "}");

        List<ProbeType> probes = ScanProfileIO.resolveProbes(tempDir.resolve("combined.json"));

        assertEquals(2, probes.size());
        assertTrue(probes.contains(TestProbeType.TEST_PROBE_TYPE));
        assertTrue(probes.contains(SecondTestProbeType.SECOND_TEST_PROBE_TYPE));
    }

    @Test
    public void testProbesAreGroupedByTypeWithMultipleConstantsPerType() throws IOException {
        writeProfile(
                "grouped.json",
                "{"
                        + "\"name\": \"grouped\","
                        + "\"probes\": {\""
                        + TestProbeType.class.getName()
                        + "\": [\"TEST_PROBE_TYPE\"], \""
                        + SecondTestProbeType.class.getName()
                        + "\": [\"SECOND_TEST_PROBE_TYPE\"]}"
                        + "}");

        List<ProbeType> probes = ScanProfileIO.resolveProbes(tempDir.resolve("grouped.json"));

        assertEquals(2, probes.size());
        assertTrue(probes.contains(TestProbeType.TEST_PROBE_TYPE));
        assertTrue(probes.contains(SecondTestProbeType.SECOND_TEST_PROBE_TYPE));
    }

    @Test
    public void testInheritedFromProfilesResolvesRelativeToDeclaringFilesDirectory()
            throws IOException {
        writeProfile(
                "parents/base.json",
                "{"
                        + "\"name\": \"base\","
                        + "\"probes\": {\""
                        + TestProbeType.class.getName()
                        + "\": [\"TEST_PROBE_TYPE\"]}"
                        + "}");
        writeProfile(
                "child.json",
                "{\"name\": \"child\", \"inheritedFromProfiles\": [\"parents/base.json\"],"
                        + " \"probes\": {}}");

        List<ProbeType> probes = ScanProfileIO.resolveProbes(tempDir.resolve("child.json"));

        assertEquals(1, probes.size());
        assertEquals(TestProbeType.TEST_PROBE_TYPE, probes.get(0));
    }

    @Test
    public void testInheritedFromProfilesAcceptsAbsolutePath() throws IOException {
        writeProfile(
                "base.json",
                "{"
                        + "\"name\": \"base\","
                        + "\"probes\": {\""
                        + TestProbeType.class.getName()
                        + "\": [\"TEST_PROBE_TYPE\"]}"
                        + "}");
        writeProfile(
                "nested/child.json",
                "{\"name\": \"child\", \"inheritedFromProfiles\": [\""
                        + tempDir.resolve("base.json")
                                .toAbsolutePath()
                                .toString()
                                .replace("\\", "\\\\")
                        + "\"], \"probes\": {}}");

        List<ProbeType> probes = ScanProfileIO.resolveProbes(tempDir.resolve("nested/child.json"));

        assertEquals(1, probes.size());
        assertEquals(TestProbeType.TEST_PROBE_TYPE, probes.get(0));
    }

    @Test
    public void testResolveProfileDeduplicatesProbesSharedByMultipleParents() throws IOException {
        writeProfile(
                "base.json",
                "{"
                        + "\"name\": \"base\","
                        + "\"probes\": {\""
                        + TestProbeType.class.getName()
                        + "\": [\"TEST_PROBE_TYPE\"]}"
                        + "}");
        writeProfile(
                "middleA.json",
                "{\"name\": \"middleA\", \"inheritedFromProfiles\": [\"base.json\"], \"probes\":"
                        + " {}}");
        writeProfile(
                "middleB.json",
                "{\"name\": \"middleB\", \"inheritedFromProfiles\": [\"base.json\"], \"probes\":"
                        + " {}}");
        writeProfile(
                "diamond.json",
                "{\"name\": \"diamond\", \"inheritedFromProfiles\": [\"middleA.json\","
                        + " \"middleB.json\"], \"probes\": {}}");

        List<ProbeType> probes = ScanProfileIO.resolveProbes(tempDir.resolve("diamond.json"));

        assertEquals(1, probes.size());
        assertEquals(TestProbeType.TEST_PROBE_TYPE, probes.get(0));
    }

    @Test
    public void testCyclicInheritanceThrows() throws IOException {
        writeProfile(
                "a.json",
                "{\"name\": \"a\", \"inheritedFromProfiles\": [\"b.json\"], \"probes\": {}}");
        writeProfile(
                "b.json",
                "{\"name\": \"b\", \"inheritedFromProfiles\": [\"a.json\"], \"probes\": {}}");

        assertThrows(
                IllegalStateException.class,
                () -> ScanProfileIO.resolveProbes(tempDir.resolve("a.json")));
    }

    @Test
    public void testUnknownInheritedProfileThrows() throws IOException {
        writeProfile(
                "orphan.json",
                "{\"name\": \"orphan\", \"inheritedFromProfiles\": [\"doesNotExist.json\"],"
                        + " \"probes\": {}}");

        assertThrows(
                IOException.class,
                () -> ScanProfileIO.resolveProbes(tempDir.resolve("orphan.json")));
    }

    @Test
    public void testUnknownProbeTypeClassThrows() throws IOException {
        writeProfile(
                "badType.json",
                "{\"name\": \"badType\", \"probes\": {\"does.not.Exist\": [\"FOO\"]}}");

        assertThrows(
                IllegalArgumentException.class,
                () -> ScanProfileIO.resolveProbes(tempDir.resolve("badType.json")));
    }

    @Test
    public void testUnknownProbeConstantThrows() throws IOException {
        writeProfile(
                "badConstant.json",
                "{\"name\": \"badConstant\", \"probes\": {\""
                        + TestProbeType.class.getName()
                        + "\": [\"DOES_NOT_EXIST\"]}}");

        assertThrows(
                IllegalArgumentException.class,
                () -> ScanProfileIO.resolveProbes(tempDir.resolve("badConstant.json")));
    }

    @Test
    public void testWildcardExpandsToAllConstants() throws IOException {
        writeProfile(
                "everything.json",
                "{\"name\": \"everything\", \"probes\": {\""
                        + MultiConstantTestProbeType.class.getName()
                        + "\": [\"*\"]}}");

        List<ProbeType> probes = ScanProfileIO.resolveProbes(tempDir.resolve("everything.json"));

        assertEquals(3, probes.size());
        assertTrue(probes.contains(MultiConstantTestProbeType.FIRST));
        assertTrue(probes.contains(MultiConstantTestProbeType.SECOND));
        assertTrue(probes.contains(MultiConstantTestProbeType.THIRD));
    }

    @Test
    public void testNegationExcludesConstantAddedByWildcard() throws IOException {
        writeProfile(
                "mostly.json",
                "{\"name\": \"mostly\", \"probes\": {\""
                        + MultiConstantTestProbeType.class.getName()
                        + "\": [\"*\", \"!SECOND\"]}}");

        List<ProbeType> probes = ScanProfileIO.resolveProbes(tempDir.resolve("mostly.json"));

        assertEquals(2, probes.size());
        assertTrue(probes.contains(MultiConstantTestProbeType.FIRST));
        assertTrue(probes.contains(MultiConstantTestProbeType.THIRD));
        assertFalse(probes.contains(MultiConstantTestProbeType.SECOND));
    }

    @Test
    public void testNegationExcludesExplicitlyListedConstant() throws IOException {
        writeProfile(
                "explicit.json",
                "{\"name\": \"explicit\", \"probes\": {\""
                        + MultiConstantTestProbeType.class.getName()
                        + "\": [\"FIRST\", \"SECOND\", \"!FIRST\"]}}");

        List<ProbeType> probes = ScanProfileIO.resolveProbes(tempDir.resolve("explicit.json"));

        assertEquals(1, probes.size());
        assertEquals(MultiConstantTestProbeType.SECOND, probes.get(0));
    }

    @Test
    public void testNegatingUnknownConstantThrows() throws IOException {
        writeProfile(
                "badNegation.json",
                "{\"name\": \"badNegation\", \"probes\": {\""
                        + MultiConstantTestProbeType.class.getName()
                        + "\": [\"*\", \"!DOES_NOT_EXIST\"]}}");

        assertThrows(
                IllegalArgumentException.class,
                () -> ScanProfileIO.resolveProbes(tempDir.resolve("badNegation.json")));
    }
}
