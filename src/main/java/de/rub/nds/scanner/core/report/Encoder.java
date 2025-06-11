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
 * Abstract base class for encoding objects of type T into string representations for reporting
 * purposes.
 *
 * <p>The Encoder framework provides a consistent approach for converting various data types into
 * formatted strings suitable for different output formats (console, HTML, JSON, etc.). This
 * abstraction allows for pluggable encoding strategies and consistent formatting across the
 * reporting system.
 *
 * <p>Encoders are typically used in conjunction with the reporting infrastructure to transform:
 *
 * <ul>
 *   <li>Test results into human-readable strings
 *   <li>Complex data structures into formatted representations
 *   <li>Raw values into domain-specific formats
 *   <li>Objects into protocol-specific encodings
 * </ul>
 *
 * <p>Common implementations might include:
 *
 * <ul>
 *   <li>Boolean encoders (TRUE/FALSE → "Yes"/"No")
 *   <li>Numeric encoders (integers → formatted strings)
 *   <li>Collection encoders (lists → comma-separated values)
 *   <li>Color encoders (results → ANSI-colored text)
 * </ul>
 *
 * <p>Usage example:
 *
 * <pre>{@code
 * public class BooleanEncoder extends Encoder<Boolean> {
 *     @Override
 *     public String encode(Boolean value) {
 *         if (value == null) {
 *             return "Unknown";
 *         }
 *         return value ? "Yes" : "No";
 *     }
 * }
 *
 * // Usage
 * Encoder<Boolean> encoder = new BooleanEncoder();
 * String result = encoder.encode(true); // Returns "Yes"
 * }</pre>
 *
 * @param <T> the type of object this encoder can process
 * @see de.rub.nds.scanner.core.report.ColorEncoding
 */
public abstract class Encoder<T> {

    /**
     * Encodes the given object into a string representation.
     *
     * <p>Implementations must handle null values appropriately and return a meaningful string
     * representation. The returned string should be suitable for the intended output format.
     *
     * @param t the object to encode, may be null
     * @return the string representation of the object, never null
     */
    public abstract String encode(T t);
}
