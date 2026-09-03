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
 * A {@link ProbeType} implementation with several constants, used to test the {@code "*"} and
 * {@code "!CONSTANT_NAME"} tokens supported by {@link ScanProfile#resolveProbes()}.
 */
public enum MultiConstantTestProbeType implements ProbeType {
    FIRST,
    SECOND,
    THIRD;

    @Override
    public String getName() {
        return name();
    }
}
