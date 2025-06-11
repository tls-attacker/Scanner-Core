/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.probe;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Represents a measurable property or characteristic that can be analyzed by scanner probes.
 *
 * <p>AnalyzedProperty is a fundamental interface in the Scanner Core framework that defines what
 * can be measured, tested, or observed during a scan. Properties are typically enum constants that
 * represent specific aspects of the target being scanned, such as:
 *
 * <ul>
 *   <li>Protocol version support (e.g., "SUPPORTS_TLS_1_3")
 *   <li>Cryptographic capabilities (e.g., "SUPPORTS_AES_256")
 *   <li>Security vulnerabilities (e.g., "VULNERABLE_TO_HEARTBLEED")
 *   <li>Configuration details (e.g., "CERTIFICATE_CHAIN_LENGTH")
 * </ul>
 *
 * <p>Properties are associated with {@link de.rub.nds.scanner.core.probe.result.TestResult} values in scan reports, creating a key-value
 * mapping that represents the complete scan findings. They are organized into categories via {@link
 * AnalyzedPropertyCategory} for better organization and reporting.
 *
 * <p><b>Serialization:</b> This interface is designed for both JSON and XML serialization:
 *
 * <ul>
 *   <li>JSON serialization uses the {@code @JsonValue} on {@link #getName()}
 *   <li>XML serialization is configured via JAXB annotations
 *   <li>Property names should be human-readable and consistent
 * </ul>
 *
 * <p><b>Implementation Guidelines:</b>
 *
 * <ul>
 *   <li>Properties are typically implemented as enum constants
 *   <li>Names should be descriptive and follow UPPER_SNAKE_CASE convention
 *   <li>Categories should logically group related properties
 *   <li>Consider the target audience when designing property granularity
 * </ul>
 *
 * <p><b>Example Implementation:</b>
 *
 * <pre>{@code
 * public enum TlsAnalyzedProperty implements AnalyzedProperty {
 *     SUPPORTS_TLS_1_2(TlsAnalyzedPropertyCategory.VERSIONS),
 *     SUPPORTS_TLS_1_3(TlsAnalyzedPropertyCategory.VERSIONS),
 *     VULNERABLE_TO_HEARTBLEED(TlsAnalyzedPropertyCategory.ATTACKS);
 *
 *     private final TlsAnalyzedPropertyCategory category;
 *
 *     TlsAnalyzedProperty(TlsAnalyzedPropertyCategory category) {
 *         this.category = category;
 *     }
 *
 *     @Override
 *     public AnalyzedPropertyCategory getCategory() {
 *         return category;
 *     }
 *
 *     @Override
 *     public String getName() {
 *         return name();
 *     }
 * }
 * }</pre>
 *
 * @see AnalyzedPropertyCategory
 * @see de.rub.nds.scanner.core.probe.result.TestResult
 * @see de.rub.nds.scanner.core.report.ScanReport
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public interface AnalyzedProperty {

    /**
     * Returns the category this property belongs to for organizational purposes.
     *
     * <p>Categories are used to group related properties together in reports and UIs. They provide
     * logical organization and help with filtering and navigation of large numbers of analyzed
     * properties.
     *
     * @return the category of this analyzed property, never null
     */
    AnalyzedPropertyCategory getCategory();

    /**
     * Returns the human-readable name of this property.
     *
     * <p>This name is used for:
     *
     * <ul>
     *   <li>JSON serialization (via {@code @JsonValue})
     *   <li>Report generation and display
     *   <li>Logging and debugging output
     *   <li>Configuration file references
     * </ul>
     *
     * <p>Names should be descriptive, consistent, and follow the project's naming conventions
     * (typically UPPER_SNAKE_CASE for enum-based implementations).
     *
     * @return the name of this property, never null or empty
     */
    @JsonValue
    String getName();
}
