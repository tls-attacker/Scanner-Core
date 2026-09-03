/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.rub.nds.scanner.core.probe.ProbeType;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Loads {@link ScanProfile}s from JSON files and builds a {@link ProbeTypeSelector} for a profile's
 * fully inherited {@code probes} declaration.
 */
public final class ScanProfileIO {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ScanProfileIO() {
        // Utility class
    }

    /**
     * Reads a single {@link ScanProfile} from a JSON file.
     *
     * @param profilePath the path to the profile's JSON file
     * @return the parsed profile
     * @throws IOException if the file cannot be read or parsed
     */
    public static ScanProfile read(Path profilePath) throws IOException {
        return MAPPER.readValue(profilePath.toFile(), ScanProfile.class);
    }

    /**
     * Builds a {@link ProbeTypeSelector} for the profile stored at {@code profilePath}, merging its
     * {@code probes} declaration with that of every (transitively) inherited profile. Each entry of
     * {@link ScanProfile#getInheritedFromProfiles()} is a path to another profile's JSON file,
     * resolved relative to the directory of the profile declaring it (an absolute entry is used
     * as-is).
     *
     * <p>For a given {@link ProbeType} class, the token lists declared for it by every profile in
     * the inheritance chain are concatenated in inheritance order (parents first, most-derived
     * profile last) before being handed to {@link ProbeTypeSelector}, so a profile can use {@code
     * "!CONSTANT_NAME"} to exclude a probe that an inherited profile selected via {@code "*"} or by
     * name.
     *
     * @param profilePath the path to the active profile's JSON file
     * @return a selector matching every probe selected by the profile and its inherited profiles
     * @throws IOException if a profile file cannot be read or parsed
     */
    public static ProbeTypeSelector resolveProbeSelector(Path profilePath) throws IOException {
        Map<String, List<String>> mergedTokens =
                resolveProbeTokens(profilePath.toAbsolutePath().normalize(), new LinkedHashSet<>());
        return new ProbeTypeSelector(mergedTokens);
    }

    private static Map<String, List<String>> resolveProbeTokens(
            Path profilePath, Set<Path> visiting) throws IOException {
        if (!visiting.add(profilePath)) {
            throw new IllegalStateException(
                    "Cyclic scan profile inheritance detected involving profile '"
                            + profilePath
                            + "'");
        }
        ScanProfile profile;
        try {
            profile = read(profilePath);
        } catch (IOException e) {
            throw new IOException("Could not parse scan profile file '" + profilePath + "'", e);
        }
        Path directory = profilePath.getParent();
        Map<String, List<String>> merged = new LinkedHashMap<>();
        for (String inheritedPath : profile.getInheritedFromProfiles()) {
            Path parentPath = directory.resolve(inheritedPath).normalize();
            mergeInto(merged, resolveProbeTokens(parentPath, visiting));
        }
        mergeInto(merged, profile.getProbes());
        visiting.remove(profilePath);
        return merged;
    }

    private static void mergeInto(
            Map<String, List<String>> target, Map<String, List<String>> source) {
        for (Map.Entry<String, List<String>> entry : source.entrySet()) {
            target.computeIfAbsent(entry.getKey(), key -> new ArrayList<>())
                    .addAll(entry.getValue());
        }
    }
}
