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

import com.fasterxml.jackson.databind.ObjectMapper;
import de.rub.nds.scanner.core.TestProbeType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ProbeTypeCatalogTest {

    @TempDir Path tempDir;

    @Test
    public void testRendersOneClassPerEntryWithAllConstants() throws Exception {
        String json =
                ProbeTypeCatalog.toProfileProbesJson(
                        List.of(TestProbeType.class, MultiConstantTestProbeType.class));

        @SuppressWarnings("unchecked")
        Map<String, List<String>> parsed = new ObjectMapper().readValue(json, Map.class);

        assertEquals(List.of("TEST_PROBE_TYPE"), parsed.get(TestProbeType.class.getName()));
        assertEquals(
                List.of("FIRST", "SECOND", "THIRD"),
                parsed.get(MultiConstantTestProbeType.class.getName()));
    }

    @Test
    public void testOutputIsDirectlyUsableAsProfileProbesField() throws IOException {
        String json =
                ProbeTypeCatalog.toProfileProbesJson(List.of(MultiConstantTestProbeType.class));
        Path profilePath = tempDir.resolve("generated.json");
        Files.writeString(profilePath, "{\"probes\": " + json + "}");

        ProbeTypeSelector selector = ScanProfileIO.resolveProbeSelector(profilePath);

        assertTrue(selector.matches(MultiConstantTestProbeType.FIRST));
        assertTrue(selector.matches(MultiConstantTestProbeType.SECOND));
        assertTrue(selector.matches(MultiConstantTestProbeType.THIRD));
    }
}
