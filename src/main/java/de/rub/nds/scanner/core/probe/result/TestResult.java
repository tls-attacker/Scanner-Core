/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.probe.result;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;

/** The interface for TestResults */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public interface TestResult extends Serializable {

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

    /**
     * Function used to check if the actual result is equal to the expected result. This is used in
     * the requirement system (cf. {@link
     * de.rub.nds.scanner.core.probe.requirements.PropertyValueRequirement}). By default, this
     * function checks if the actual result and the expected result are of the same type and then
     * uses the default equals implementation. If the types do not match, equals is not called and
     * an exception is thrown.
     *
     * <p>It can be useful to overwrite this to allow a more complex result to evaluate to a simple
     * result.
     *
     * <p>Example: A result might be checked for each version of a protocol. Each of these checks
     * results in a TestResults enum. The actual result can overwrite this function to say that it
     * is equal to TRUE if it is TRUE in one version.
     *
     * @param expectedResult The expected result stated in the requirement.
     * @return Whether the actual result is equal to the expected result.
     */
    default boolean equalsExpectedResult(TestResult expectedResult) {
        if (!getClass().equals(expectedResult.getClass())) {
            throw new IllegalArgumentException(
                    String.format(
                            "Cannot Compare actual result with expected result (type mismatch: found %s but expected %s)" //$NON-NLS-1$
                                    + " - Consider overwriting equalsExpectedResult if the type mismatch is intended.", //$NON-NLS-1$
                            this.getClass(), expectedResult.getClass()));
        }
        return this.equals(expectedResult);
    }
}
