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
import java.util.List;
import java.util.Map;

/**
 * Decides whether a candidate {@link ProbeType} is selected by a scan profile's {@code probes}
 * declaration (merged across its inheritance chain), by comparing the profile's raw {@code
 * "className": ["TOKEN", ...]} entries directly against the candidate's actual declaring class and
 * constant name.
 *
 * <p>This never resolves a class or constant by name via reflection: a candidate is only ever
 * matched against classes and constants that some already-instantiated {@link ProbeType} actually
 * belongs to, so a typo or a class that no longer exists in a profile simply never matches instead
 * of failing to load.
 */
final class ProbeTypeSelector {

    private final Map<String, List<String>> tokensByClassName;

    ProbeTypeSelector(Map<String, List<String>> tokensByClassName) {
        this.tokensByClassName = tokensByClassName;
    }

    /**
     * Determines whether {@code probeType} is selected, by replaying the tokens declared for its
     * declaring class in order: {@code "*"} selects it, a matching constant name selects it, and a
     * matching {@code "!CONSTANT_NAME"} deselects it again — whichever applies last wins.
     *
     * @param probeType the candidate probe type
     * @return true if the tokens declared for this probe's class select it
     */
    boolean matches(ProbeType probeType) {
        if (!(probeType instanceof Enum<?>)) {
            return false;
        }
        Enum<?> constant = (Enum<?>) probeType;
        List<String> tokens = tokensByClassName.get(constant.getDeclaringClass().getName());
        if (tokens == null) {
            return false;
        }
        boolean selected = false;
        for (String token : tokens) {
            if ("*".equals(token)) {
                selected = true;
            } else if (token.startsWith("!")) {
                if (token.substring(1).equals(constant.name())) {
                    selected = false;
                }
            } else if (token.equals(constant.name())) {
                selected = true;
            }
        }
        return selected;
    }
}
