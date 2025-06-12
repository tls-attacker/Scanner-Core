/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.probe;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

/**
 * Marker interface for categorizing analyzed properties within the Scanner Core framework.
 *
 * <p>This interface serves as a common type for grouping related analyzed properties into
 * categories. It allows for better organization and classification of test results, enabling
 * structured reporting and analysis.
 *
 * <p>Implementations of this interface typically represent different categories or domains of
 * security properties that can be analyzed, such as:
 *
 * <ul>
 *   <li>Certificate-related properties
 *   <li>Cipher suite properties
 *   <li>Protocol version properties
 *   <li>Extension-related properties
 *   <li>Vulnerability categories
 * </ul>
 *
 * <p>The interface is designed to work with JAXB for XML serialization, allowing property
 * categories to be included in XML reports and configurations.
 *
 * <p>Usage example:
 *
 * <pre>{@code
 * public enum TlsPropertyCategory implements AnalyzedPropertyCategory {
 *     CERTIFICATE,
 *     CIPHER_SUITE,
 *     PROTOCOL_VERSION,
 *     EXTENSIONS,
 *     VULNERABILITIES
 * }
 * }</pre>
 *
 * @see de.rub.nds.scanner.core.probe.AnalyzedProperty
 */
@XmlAccessorType(XmlAccessType.FIELD)
public interface AnalyzedPropertyCategory {}
