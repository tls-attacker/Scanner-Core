/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.probe.result;

/** The interface for TestResults */
public interface TestResult {

    /**
     * @return the name of the TestResult.
     */
    String getName();

    /**
     * Returns true if the result stored for the property contains actual information. True by
     * default.
     *
     * @return Whether the result contains actual information.
     */
    default boolean isRealResult() {
        return true;
    }

    default String getType() {
        return this.getClass().getSimpleName();
    }
}
