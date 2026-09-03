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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Loads {@link ScanProfile}s from JSON files and resolves a profile's fully inherited set of {@link
 * ProbeType}s.
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
     * Resolves the fully inherited, deduplicated list of probes for the profile stored at {@code
     * profilePath}. Sibling {@code *.json} files in the same directory are parsed as candidate
     * profiles to resolve entries of {@link ScanProfile#getInheritedFromProfiles()} against,
     * matched by their {@code name} field.
     *
     * @param profilePath the path to the active profile's JSON file
     * @return the deduplicated, order-preserving list of probes declared by the profile and all of
     *     its (transitively) inherited profiles
     * @throws IOException if a profile file cannot be read or parsed
     */
    public static List<ProbeType> resolveProbes(Path profilePath) throws IOException {
        ScanProfile rootProfile = read(profilePath);
        Path directory = profilePath.toAbsolutePath().getParent();
        Map<String, ScanProfile> profilesByName = readProfileDirectory(directory);
        profilesByName.put(rootProfile.getName(), rootProfile);
        return resolveProbes(rootProfile.getName(), profilesByName, new LinkedHashSet<>());
    }

    private static Map<String, ScanProfile> readProfileDirectory(Path directory)
            throws IOException {
        Map<String, ScanProfile> profilesByName = new LinkedHashMap<>();
        if (directory == null || !Files.isDirectory(directory)) {
            return profilesByName;
        }
        List<Path> jsonFiles;
        try (Stream<Path> files = Files.list(directory)) {
            jsonFiles =
                    files.filter(p -> p.toString().endsWith(".json")).collect(Collectors.toList());
        }
        for (Path file : jsonFiles) {
            ScanProfile profile;
            try {
                profile = read(file);
            } catch (IOException e) {
                throw new IOException("Could not parse scan profile file '" + file + "'", e);
            }
            if (profile.getName() == null) {
                continue;
            }
            ScanProfile previous = profilesByName.putIfAbsent(profile.getName(), profile);
            if (previous != null) {
                throw new IOException(
                        "Duplicate scan profile name '"
                                + profile.getName()
                                + "' found in directory '"
                                + directory
                                + "'");
            }
        }
        return profilesByName;
    }

    private static List<ProbeType> resolveProbes(
            String profileName, Map<String, ScanProfile> profilesByName, Set<String> visiting) {
        if (!visiting.add(profileName)) {
            throw new IllegalStateException(
                    "Cyclic scan profile inheritance detected involving profile '"
                            + profileName
                            + "'");
        }
        ScanProfile profile = profilesByName.get(profileName);
        if (profile == null) {
            throw new IllegalArgumentException("Unknown scan profile: '" + profileName + "'");
        }
        LinkedHashSet<ProbeType> resolved = new LinkedHashSet<>();
        for (String parentName : profile.getInheritedFromProfiles()) {
            resolved.addAll(resolveProbes(parentName, profilesByName, visiting));
        }
        resolved.addAll(profile.resolveProbes());
        visiting.remove(profileName);
        return new ArrayList<>(resolved);
    }
}
