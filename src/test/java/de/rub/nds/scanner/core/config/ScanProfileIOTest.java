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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

        ProbeTypeSelector selector =
                ScanProfileIO.resolveProbeSelector(tempDir.resolve("solo.json"));

        assertTrue(selector.matches(TestProbeType.TEST_PROBE_TYPE));
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

        ProbeTypeSelector selector =
                ScanProfileIO.resolveProbeSelector(tempDir.resolve("combined.json"));

        assertTrue(selector.matches(TestProbeType.TEST_PROBE_TYPE));
        assertTrue(selector.matches(SecondTestProbeType.SECOND_TEST_PROBE_TYPE));
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

        ProbeTypeSelector selector =
                ScanProfileIO.resolveProbeSelector(tempDir.resolve("grouped.json"));

        assertTrue(selector.matches(TestProbeType.TEST_PROBE_TYPE));
        assertTrue(selector.matches(SecondTestProbeType.SECOND_TEST_PROBE_TYPE));
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

        ProbeTypeSelector selector =
                ScanProfileIO.resolveProbeSelector(tempDir.resolve("child.json"));

        assertTrue(selector.matches(TestProbeType.TEST_PROBE_TYPE));
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

        ProbeTypeSelector selector =
                ScanProfileIO.resolveProbeSelector(tempDir.resolve("nested/child.json"));

        assertTrue(selector.matches(TestProbeType.TEST_PROBE_TYPE));
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
                () -> ScanProfileIO.resolveProbeSelector(tempDir.resolve("a.json")));
    }

    @Test
    public void testUnknownInheritedProfileThrows() throws IOException {
        writeProfile(
                "orphan.json",
                "{\"name\": \"orphan\", \"inheritedFromProfiles\": [\"doesNotExist.json\"],"
                        + " \"probes\": {}}");

        assertThrows(
                IOException.class,
                () -> ScanProfileIO.resolveProbeSelector(tempDir.resolve("orphan.json")));
    }

    @Test
    public void testWildcardMatchesEveryConstantOfThatType() throws IOException {
        writeProfile(
                "everything.json",
                "{\"name\": \"everything\", \"probes\": {\""
                        + MultiConstantTestProbeType.class.getName()
                        + "\": [\"*\"]}}");

        ProbeTypeSelector selector =
                ScanProfileIO.resolveProbeSelector(tempDir.resolve("everything.json"));

        assertTrue(selector.matches(MultiConstantTestProbeType.FIRST));
        assertTrue(selector.matches(MultiConstantTestProbeType.SECOND));
        assertTrue(selector.matches(MultiConstantTestProbeType.THIRD));
    }

    @Test
    public void testNegationExcludesConstantSelectedByWildcard() throws IOException {
        writeProfile(
                "mostly.json",
                "{\"name\": \"mostly\", \"probes\": {\""
                        + MultiConstantTestProbeType.class.getName()
                        + "\": [\"*\", \"!SECOND\"]}}");

        ProbeTypeSelector selector =
                ScanProfileIO.resolveProbeSelector(tempDir.resolve("mostly.json"));

        assertTrue(selector.matches(MultiConstantTestProbeType.FIRST));
        assertFalse(selector.matches(MultiConstantTestProbeType.SECOND));
        assertTrue(selector.matches(MultiConstantTestProbeType.THIRD));
    }

    @Test
    public void testNegationExcludesExplicitlyListedConstant() throws IOException {
        writeProfile(
                "explicit.json",
                "{\"name\": \"explicit\", \"probes\": {\""
                        + MultiConstantTestProbeType.class.getName()
                        + "\": [\"FIRST\", \"SECOND\", \"!FIRST\"]}}");

        ProbeTypeSelector selector =
                ScanProfileIO.resolveProbeSelector(tempDir.resolve("explicit.json"));

        assertFalse(selector.matches(MultiConstantTestProbeType.FIRST));
        assertTrue(selector.matches(MultiConstantTestProbeType.SECOND));
        assertFalse(selector.matches(MultiConstantTestProbeType.THIRD));
    }

    @Test
    public void testChildProfileCanExcludeConstantSelectedByParent() throws IOException {
        writeProfile(
                "base.json",
                "{\"name\": \"base\", \"probes\": {\""
                        + MultiConstantTestProbeType.class.getName()
                        + "\": [\"*\"]}}");
        writeProfile(
                "child.json",
                "{\"name\": \"child\", \"inheritedFromProfiles\": [\"base.json\"], \"probes\":"
                        + " {\""
                        + MultiConstantTestProbeType.class.getName()
                        + "\": [\"!SECOND\"]}}");

        ProbeTypeSelector selector =
                ScanProfileIO.resolveProbeSelector(tempDir.resolve("child.json"));

        assertTrue(selector.matches(MultiConstantTestProbeType.FIRST));
        assertFalse(selector.matches(MultiConstantTestProbeType.SECOND));
        assertTrue(selector.matches(MultiConstantTestProbeType.THIRD));
    }

    @Test
    public void testUnknownClassNameNeverMatchesInsteadOfThrowing() throws IOException {
        writeProfile(
                "badType.json",
                "{\"name\": \"badType\", \"probes\": {\"does.not.Exist\": [\"FOO\"]}}");

        ProbeTypeSelector selector =
                ScanProfileIO.resolveProbeSelector(tempDir.resolve("badType.json"));

        assertFalse(selector.matches(TestProbeType.TEST_PROBE_TYPE));
    }

    @Test
    public void testTypoedConstantNameNeverMatchesInsteadOfThrowing() throws IOException {
        writeProfile(
                "badConstant.json",
                "{\"name\": \"badConstant\", \"probes\": {\""
                        + TestProbeType.class.getName()
                        + "\": [\"DOES_NOT_EXIST\"]}}");

        ProbeTypeSelector selector =
                ScanProfileIO.resolveProbeSelector(tempDir.resolve("badConstant.json"));

        assertFalse(selector.matches(TestProbeType.TEST_PROBE_TYPE));
    }

    @Test
    public void testUnmentionedTypeNeverMatches() throws IOException {
        writeProfile(
                "onlyFirst.json",
                "{\"name\": \"onlyFirst\", \"probes\": {\""
                        + TestProbeType.class.getName()
                        + "\": [\"*\"]}}");

        ProbeTypeSelector selector =
                ScanProfileIO.resolveProbeSelector(tempDir.resolve("onlyFirst.json"));

        assertFalse(selector.matches(SecondTestProbeType.SECOND_TEST_PROBE_TYPE));
    }
}
