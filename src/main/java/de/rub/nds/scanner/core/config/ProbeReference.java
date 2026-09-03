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

/**
 * A JSON-friendly reference to a single {@link ProbeType} constant, e.g. {@code {"type":
 * "de.rub.nds.tlsattacker.core.probe.TlsProbeType", "name": "CIPHER_SUITE"}}.
 *
 * <p>{@code type} must be the fully qualified name of an enum class implementing {@link ProbeType},
 * and {@code name} must be one of its enum constant names. Representing probes this way (rather
 * than relying on Jackson's polymorphic type handling on {@link ProbeType} itself) allows a single
 * scan profile to freely combine probes from different {@link ProbeType} implementations, e.g.
 * probes from different scanner modules.
 */
public final class ProbeReference {

    private String type;

    private String name;

    public ProbeReference() {
        // Default constructor for Jackson
    }

    /**
     * Returns the fully qualified name of the enum class implementing {@link ProbeType}.
     *
     * @return the probe type's class name
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the fully qualified name of the enum class implementing {@link ProbeType}.
     *
     * @param type the probe type's class name
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the referenced enum constant's name.
     *
     * @return the probe's constant name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the referenced enum constant's name.
     *
     * @param name the probe's constant name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Resolves this reference to the concrete {@link ProbeType} enum constant it identifies.
     *
     * @return the resolved probe type
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public ProbeType resolve() {
        if (type == null || name == null) {
            throw new IllegalArgumentException(
                    "Invalid probe reference: expected an object with 'type' and 'name' fields");
        }
        Class<?> probeTypeClass;
        try {
            probeTypeClass = Class.forName(type);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Could not find ProbeType class '" + type + "'", e);
        }
        if (!ProbeType.class.isAssignableFrom(probeTypeClass) || !probeTypeClass.isEnum()) {
            throw new IllegalArgumentException(
                    "Class '" + type + "' does not implement ProbeType as an enum");
        }
        try {
            return (ProbeType) Enum.valueOf((Class<? extends Enum>) probeTypeClass, name);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "'" + name + "' is not a valid constant of ProbeType enum '" + type + "'", e);
        }
    }
}
