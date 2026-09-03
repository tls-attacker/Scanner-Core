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
import java.util.List;
import java.util.stream.Collectors;

/**
 * A named, JSON-defined scan profile. A profile declares the set of {@link ProbeType}s that should
 * be executed during a scan, and may additionally inherit probes from other profiles, referenced by
 * file path, via {@link #getInheritedFromProfiles()}.
 */
public final class ScanProfile {

    private String name;

    private List<String> inheritedFromProfiles = new ArrayList<>();

    private List<ProbeReference> probes = new ArrayList<>();

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
     * Returns the raw probe references declared directly by this profile (not including inherited
     * ones).
     *
     * @return the list of probe references declared by this profile
     */
    public List<ProbeReference> getProbes() {
        return probes;
    }

    /**
     * Sets the raw probe references declared directly by this profile.
     *
     * @param probes the list of probe references to declare
     */
    public void setProbes(List<ProbeReference> probes) {
        this.probes = probes == null ? new ArrayList<>() : probes;
    }

    /**
     * Resolves the probes declared directly by this profile (not including inherited ones) to their
     * concrete {@link ProbeType} enum constants.
     *
     * @return the resolved list of probes declared by this profile
     */
    public List<ProbeType> resolveProbes() {
        return probes.stream().map(ProbeReference::resolve).collect(Collectors.toList());
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
