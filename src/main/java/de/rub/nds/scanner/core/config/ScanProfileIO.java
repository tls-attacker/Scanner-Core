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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
     * profilePath}. Each entry of {@link ScanProfile#getInheritedFromProfiles()} is a path to
     * another profile's JSON file, resolved relative to the directory of the profile declaring it
     * (an absolute entry is used as-is).
     *
     * @param profilePath the path to the active profile's JSON file
     * @return the deduplicated, order-preserving list of probes declared by the profile and all of
     *     its (transitively) inherited profiles
     * @throws IOException if a profile file cannot be read or parsed
     */
    public static List<ProbeType> resolveProbes(Path profilePath) throws IOException {
        return resolveProbes(profilePath.toAbsolutePath().normalize(), new LinkedHashSet<>());
    }

    private static List<ProbeType> resolveProbes(Path profilePath, Set<Path> visiting)
            throws IOException {
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
        LinkedHashSet<ProbeType> resolved = new LinkedHashSet<>();
        for (String inheritedPath : profile.getInheritedFromProfiles()) {
            Path parentPath = directory.resolve(inheritedPath).normalize();
            resolved.addAll(resolveProbes(parentPath, visiting));
        }
        resolved.addAll(profile.resolveProbes());
        visiting.remove(profilePath);
        return new ArrayList<>(resolved);
    }
}
