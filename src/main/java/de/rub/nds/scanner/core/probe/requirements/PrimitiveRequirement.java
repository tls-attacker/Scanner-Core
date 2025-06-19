/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.probe.requirements;

import de.rub.nds.scanner.core.report.ScanReport;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Abstract subclass of {@link Requirement} to represent a requirement which can be evaluated
 * directly to a boolean value. Requirements of this type contain one or more parameters of any
 * type.
 */
public abstract class PrimitiveRequirement<ReportT extends ScanReport, ParameterT>
        extends Requirement<ReportT> {
    protected final List<ParameterT> parameters;

    /**
     * Constructs a new PrimitiveRequirement with the specified parameters.
     *
     * @param parameters the list of parameters for this requirement (made unmodifiable)
     */
    protected PrimitiveRequirement(List<ParameterT> parameters) {
        this.parameters = Collections.unmodifiableList(parameters);
    }

    /**
     * Returns an unmodifiable list of parameters for this requirement.
     *
     * @return the list of parameters
     */
    public List<ParameterT> getParameters() {
        return parameters;
    }

    /**
     * Returns a string representation of this requirement in the format "ClassName[param1, param2,
     * ...]".
     *
     * @return string representation of the primitive requirement
     */
    @Override
    public String toString() {
        return String.format(
                "%s[%s]",
                this.getClass().getSimpleName(),
                parameters.stream().map(Object::toString).collect(Collectors.joining(", ")));
    }
}
