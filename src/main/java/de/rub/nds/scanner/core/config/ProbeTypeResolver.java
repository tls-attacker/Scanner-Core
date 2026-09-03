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

/**
 * Resolves {@link ProbeType} enum constants from their declaring class's fully qualified name and
 * the constant's own name, as used by {@link ScanProfile#getProbes()}.
 */
final class ProbeTypeResolver {

    private ProbeTypeResolver() {
        // Utility class
    }

    /**
     * Resolves the class named {@code className}, verifying that it is an enum implementing {@link
     * ProbeType}.
     *
     * @param className the fully qualified name of an enum class implementing {@link ProbeType}
     * @return the resolved class
     */
    static Class<?> resolveClass(String className) {
        Class<?> probeTypeClass;
        try {
            probeTypeClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(
                    "Could not find ProbeType class '" + className + "'", e);
        }
        if (!ProbeType.class.isAssignableFrom(probeTypeClass) || !probeTypeClass.isEnum()) {
            throw new IllegalArgumentException(
                    "Class '" + className + "' does not implement ProbeType as an enum");
        }
        return probeTypeClass;
    }

    /**
     * Resolves the {@link ProbeType} enum constant named {@code constantName} declared by the enum
     * class {@code className}.
     *
     * @param className the fully qualified name of an enum class implementing {@link ProbeType}
     * @param constantName the enum constant's name
     * @return the resolved probe type
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    static ProbeType resolve(String className, String constantName) {
        Class<?> probeTypeClass = resolveClass(className);
        try {
            return (ProbeType) Enum.valueOf((Class<? extends Enum>) probeTypeClass, constantName);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "'"
                            + constantName
                            + "' is not a valid constant of ProbeType enum '"
                            + className
                            + "'",
                    e);
        }
    }

    /**
     * Returns the names of every constant declared by the enum class {@code className}, in
     * declaration order.
     *
     * @param className the fully qualified name of an enum class implementing {@link ProbeType}
     * @return the names of all of its constants
     */
    static List<String> allConstantNames(String className) {
        Class<?> probeTypeClass = resolveClass(className);
        List<String> names = new ArrayList<>();
        for (Object constant : probeTypeClass.getEnumConstants()) {
            names.add(((Enum<?>) constant).name());
        }
        return names;
    }
}
