/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.rub.nds.scanner.core.probe.ProbeType;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Renders the constants of one or more {@link ProbeType} enum classes as the same JSON syntax used
 * by a {@link ScanProfile}'s {@code probes} field, so it can be copy-pasted straight into a profile
 * (e.g. behind a {@code -listProbes} CLI flag implemented by a concrete scanner, which knows which
 * {@link ProbeType} classes it registers).
 */
public final class ProbeTypeCatalog {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ProbeTypeCatalog() {
        // Utility class
    }

    /**
     * Renders every constant of the given {@link ProbeType} enum classes as pretty-printed JSON, in
     * the exact shape expected by a scan profile's {@code probes} field — a map from each class's
     * fully qualified name to the list of its constant names, in declaration order.
     *
     * @param probeTypeClasses the {@link ProbeType} enum classes to list, e.g. {@code
     *     List.of(TlsProbeType.class, QuicProbeType.class)}
     * @return the pretty-printed JSON, ready to paste as (or into) a profile's {@code probes} field
     */
    public static String toProfileProbesJson(List<Class<? extends ProbeType>> probeTypeClasses) {
        Map<String, List<String>> probesByType = new LinkedHashMap<>();
        for (Class<? extends ProbeType> probeTypeClass : probeTypeClasses) {
            probesByType.put(
                    probeTypeClass.getName(),
                    ProbeTypeResolver.allConstantNames(probeTypeClass.getName()));
        }
        try {
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(probesByType);
        } catch (JsonProcessingException e) {
            // Unreachable: probesByType only ever contains plain strings and lists thereof.
            throw new IllegalStateException("Could not render probe type catalog", e);
        }
    }
}
