/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.probe.result;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.rub.nds.scanner.core.probe.AnalyzedProperty;
import java.util.Collection;

/**
 * A test result implementation that wraps a collection of objects as the result value.
 *
 * <p>CollectionResult provides a standardized way to represent test outcomes that consist of
 * multiple related values. This is particularly useful for tests that gather lists of supported
 * features, discovered vulnerabilities, or enumerated properties.
 *
 * <p>The class is designed to work seamlessly with the Scanner Core framework:
 *
 * <ul>
 *   <li><strong>JSON Serialization:</strong> Configured to serialize cleanly with type and value
 *       properties
 *   <li><strong>Property Association:</strong> Links the result to its corresponding {@link
 *       AnalyzedProperty}
 *   <li><strong>Type Safety:</strong> Generic implementation ensures type-safe collection handling
 * </ul>
 *
 * <p>Common use cases include:
 *
 * <ul>
 *   <li>Lists of supported cipher suites
 *   <li>Collections of discovered certificates
 *   <li>Sets of enabled protocol versions
 *   <li>Arrays of detected vulnerabilities
 * </ul>
 *
 * <p>Usage example:
 *
 * <pre>{@code
 * // Creating a result with supported cipher suites
 * List<CipherSuite> supportedSuites = Arrays.asList(
 *     CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA,
 *     CipherSuite.TLS_RSA_WITH_AES_256_CBC_SHA
 * );
 * CollectionResult<CipherSuite> result = new CollectionResult<>(
 *     TlsAnalyzedProperty.SUPPORTED_CIPHER_SUITES,
 *     supportedSuites
 * );
 *
 * // The result can be serialized and contains:
 * // {
 * //   "type": "CollectionResult",
 * //   "value": ["TLS_RSA_WITH_AES_128_CBC_SHA", "TLS_RSA_WITH_AES_256_CBC_SHA"]
 * // }
 * }</pre>
 *
 * @param <T> the type of objects contained in the collection
 * @see TestResult
 * @see AnalyzedProperty
 * @see ListResult
 * @see SetResult
 */
@JsonIncludeProperties({"type", "value"})
@JsonPropertyOrder({"type", "value"})
public class CollectionResult<T> implements TestResult {

    private final AnalyzedProperty property;
    protected final Collection<T> collection;

    /**
     * Creates a new CollectionResult with the specified property and collection.
     *
     * @param property the analyzed property this result represents, must not be null
     * @param collection the collection of values for this result, must not be null
     */
    public CollectionResult(AnalyzedProperty property, Collection<T> collection) {
        this.property = property;
        this.collection = collection;
    }

    /**
     * Returns the analyzed property associated with this result.
     *
     * @return the analyzed property, never null
     */
    public AnalyzedProperty getProperty() {
        return property;
    }

    /**
     * Returns the collection of values contained in this result.
     *
     * <p>This method provides access to the actual collection data. The returned collection should
     * be treated as read-only to maintain result immutability.
     *
     * @return the collection of values of type T, never null
     */
    @JsonGetter("value")
    public Collection<T> getCollection() {
        return collection;
    }

    @Override
    public String getName() {
        return property.getName();
    }
}
