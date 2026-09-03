/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.config;

import de.rub.nds.scanner.core.probe.ProbeType;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * A named, JSON-defined scan profile. A profile declares the set of {@link ProbeType}s that should
 * be executed during a scan, and may additionally inherit probes from other profiles, referenced by
 * file path, via {@link #getInheritedFromProfiles()}.
 */
public final class ScanProfile {

    private String name;

    private List<String> inheritedFromProfiles = new ArrayList<>();

    private Map<String, List<String>> probes = new LinkedHashMap<>();

    private ScanProfileSettings settings;

    public ScanProfile() {
        // Default constructor for Jackson
    }

    /**
     * Returns the name of this profile. Purely informational (e.g. for error messages) — it plays
     * no role in resolving {@link #getInheritedFromProfiles()}.
     *
     * @return the profile name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this profile.
     *
     * @param name the profile name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the paths of the profiles this profile inherits probes from, each resolved relative
     * to the directory of the file this profile itself was loaded from (an absolute path is used
     * as-is).
     *
     * @return the list of inherited profile paths
     */
    public List<String> getInheritedFromProfiles() {
        return inheritedFromProfiles;
    }

    /**
     * Sets the paths of the profiles this profile inherits probes from.
     *
     * @param inheritedFromProfiles the list of inherited profile paths
     */
    public void setInheritedFromProfiles(List<String> inheritedFromProfiles) {
        this.inheritedFromProfiles =
                inheritedFromProfiles == null ? new ArrayList<>() : inheritedFromProfiles;
    }

    /**
     * Returns the raw probes declared directly by this profile (not including inherited ones), as a
     * map from the fully qualified name of an enum class implementing {@link ProbeType} to the list
     * of its constant names to run — e.g. {@code {"de.rub.nds.tlsscanner.core.constants
     * .TlsProbeType": ["CIPHER_SUITE", "CERTIFICATE"]}}. This groups probes by type instead of
     * repeating the type for every single probe, while still letting a profile freely combine
     * probes from different {@link ProbeType} implementations.
     *
     * <p>Each per-type list also accepts two special tokens, processed in order: {@code "*"} adds
     * every constant declared by that type, and {@code "!CONSTANT_NAME"} removes a constant
     * previously added (by name or by {@code "*"}) from that type's set. This lets an "everything"
     * profile be written as {@code {"...TlsProbeType": ["*"]}} without enumerating every constant,
     * and still exclude a few via {@code {"...TlsProbeType": ["*", "!TLS_LATENCY"]}}.
     *
     * @return the raw probes declared by this profile
     */
    public Map<String, List<String>> getProbes() {
        return probes;
    }

    /**
     * Sets the raw probes declared directly by this profile.
     *
     * @param probes the probes to declare, grouped by {@link ProbeType} class name
     */
    public void setProbes(Map<String, List<String>> probes) {
        this.probes = probes == null ? new LinkedHashMap<>() : probes;
    }

    /**
     * Resolves the probes declared directly by this profile (not including inherited ones) to their
     * concrete {@link ProbeType} enum constants, expanding {@code "*"} and {@code "!CONSTANT_NAME"}
     * tokens as documented on {@link #getProbes()}.
     *
     * @return the resolved list of probes declared by this profile
     */
    public List<ProbeType> resolveProbes() {
        List<ProbeType> resolved = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : probes.entrySet()) {
            String className = entry.getKey();
            LinkedHashSet<String> constantNames = new LinkedHashSet<>();
            for (String token : entry.getValue()) {
                if ("*".equals(token)) {
                    constantNames.addAll(ProbeTypeResolver.allConstantNames(className));
                } else if (token.startsWith("!")) {
                    String excludedName = token.substring(1);
                    ProbeTypeResolver.resolve(className, excludedName); // validate, catch typos
                    constantNames.remove(excludedName);
                } else {
                    ProbeTypeResolver.resolve(className, token); // validate, catch typos
                    constantNames.add(token);
                }
            }
            for (String constantName : constantNames) {
                resolved.add(ProbeTypeResolver.resolve(className, constantName));
            }
        }
        return resolved;
    }

    /**
     * Returns the {@link ExecutorConfig} setting overrides declared directly by this profile, or
     * null if the profile declares none. Unlike {@link #getProbes()}, these are never inherited
     * from {@link #getInheritedFromProfiles()}.
     *
     * @return the setting overrides, or null
     */
    public ScanProfileSettings getSettings() {
        return settings;
    }

    /**
     * Sets the {@link ExecutorConfig} setting overrides declared directly by this profile.
     *
     * @param settings the setting overrides, or null
     */
    public void setSettings(ScanProfileSettings settings) {
        this.settings = settings;
    }
}
