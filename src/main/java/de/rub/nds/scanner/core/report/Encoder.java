/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.report;

/**
 * Abstract base class for encoding objects to their string representation.
 *
 * @param <T> the type of objects this encoder can encode
 */
public abstract class Encoder<T> {

    /**
     * Encodes the given object to its string representation.
     *
     * @param t the object to encode
     * @return the string representation of the object
     */
    public abstract String encode(T t);
}
