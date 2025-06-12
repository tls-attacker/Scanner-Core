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
 * Identifies and categorizes different types of scanner probes within the framework.
 *
 * <p>ProbeType serves as a classification and identification mechanism for scanner probes,
 * providing human-readable names for different probe implementations. This interface is typically
 * implemented by enum constants that represent distinct categories of scanning operations, such as:
 *
 * <ul>
 *   <li>Protocol feature detection (e.g., "TLS Version Support", "Extension Detection")
 *   <li>Vulnerability assessment (e.g., "Heartbleed", "POODLE", "BEAST")
 *   <li>Configuration analysis (e.g., "Certificate Analysis", "Cipher Suite Order")
 *   <li>Performance testing (e.g., "Connection Speed", "Handshake Timing")
 * </ul>
 *
 * <p>ProbeTypes are used throughout the framework for:
 *
 * <ul>
 *   <li>Probe registration and management
 *   <li>Execution scheduling and ordering
 *   <li>Performance monitoring and reporting
 *   <li>Configuration and filtering
 *   <li>Logging and debugging
 * </ul>
 *
 * <p><b>Naming Conventions:</b>
 *
 * <ul>
 *   <li>Names should be descriptive and user-friendly
 *   <li>Use title case for multi-word names (e.g., "Certificate Analysis")
 *   <li>Avoid technical jargon when possible
 *   <li>Keep names concise but meaningful
 * </ul>
 *
 * <p><b>XML Serialization:</b> This interface is configured for JAXB serialization to support
 * configuration files and report generation that includes probe type information.
 *
 * <p><b>Example Implementation:</b>
 *
 * <pre>{@code
 * public enum TlsProbeType implements ProbeType {
 *     CERTIFICATE("Certificate"),
 *     CIPHER_SUITE("Cipher suite"),
 *     HEARTBLEED("Heartbleed"),
 *     PROTOCOL_VERSION("Protocol version"),
 *     EXTENSIONS("Extensions");
 *
 *     private final String humanReadableName;
 *
 *     TlsProbeType(String humanReadableName) {
 *         this.humanReadableName = humanReadableName;
 *     }
 *
 *     @Override
 *     public String getName() {
 *         return humanReadableName;
 *     }
 * }
 * }</pre>
 *
 * @see ScannerProbe
 * @see de.rub.nds.scanner.core.execution.ScanJob
 * @see de.rub.nds.scanner.core.report.PerformanceData
 */
@XmlAccessorType(XmlAccessType.FIELD)
public interface ProbeType {

    /**
     * Returns the human-readable name of this probe type.
     *
     * <p>This name is used in:
     *
     * <ul>
     *   <li>User interfaces and reports
     *   <li>Configuration files and command-line output
     *   <li>Logging and debugging messages
     *   <li>Performance monitoring data
     *   <li>Error messages and diagnostics
     * </ul>
     *
     * <p>The name should be descriptive enough for users to understand what the probe does without
     * requiring technical knowledge of the implementation.
     *
     * @return the human-readable name of this probe type, never null or empty
     */
    String getName();
}
