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
 * A second, distinct {@link ProbeType} implementation used to verify that a single scan profile can
 * combine probes from different {@link ProbeType} implementations (e.g. probes from different
 * scanner modules).
 */
public enum SecondTestProbeType implements ProbeType {
    SECOND_TEST_PROBE_TYPE;

    @Override
    public String getName() {
        return name();
    }
}
