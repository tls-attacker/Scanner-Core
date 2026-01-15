/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.probe.result;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.rub.nds.scanner.core.TestAnalyzedProperty;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SetResultTest {

    @Test
    void roundTripKeepsSetType() throws Exception {
        SetResult<String> original =
                new SetResult<>(TestAnalyzedProperty.TEST_ANALYZED_PROPERTY, Set.of("a", "b"));

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String json = mapper.writeValueAsString(original);

        SetResult<?> restored = mapper.readValue(json, SetResult.class);

        assertNotNull(restored.getSet());
        assertTrue(restored.getSet() instanceof Set);
        assertEquals(Set.of("a", "b"), restored.getSet());
    }

    @Test
    void deserializesArrayPayloadIntoSet() throws Exception {
        String json =
                """
                {"@class":"de.rub.nds.scanner.core.probe.result.SetResult",
                 "property":"TEST_ANALYZED_PROPERTY",
                 "value":["x","y"]}
                """;

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SetResult<?> restored = mapper.readValue(json, SetResult.class);

        assertNotNull(restored.getSet());
        assertTrue(restored.getSet() instanceof Set);
        assertEquals(Set.of("x", "y"), restored.getSet());
    }

    @Test
    void deserializesEmptySetValue() throws Exception {
        String json =
                """
                {"@class":"de.rub.nds.scanner.core.probe.result.SetResult",
                 "property":"TEST_ANALYZED_PROPERTY",
                 "value":[]}
                """;

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SetResult<?> restored = mapper.readValue(json, SetResult.class);

        assertNotNull(restored.getSet());
        assertTrue(restored.getSet().isEmpty());
    }

    @Test
    void failsDeserializationWithoutAnnotations() throws Exception {
        String json =
                """
                {"@class":"de.rub.nds.scanner.core.probe.result.SetResult",
                 "property":"TEST_ANALYZED_PROPERTY",
                 "value":["x"]}
                """;

        // Disable annotations to simulate the constructor missing @JsonProperty
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(com.fasterxml.jackson.databind.MapperFeature.USE_ANNOTATIONS, false);

        assertThrows(Exception.class, () -> mapper.readValue(json, SetResult.class));
    }
}
