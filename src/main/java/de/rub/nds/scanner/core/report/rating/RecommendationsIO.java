/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.report.rating;

import de.rub.nds.scanner.core.probe.AnalyzedProperty;
import de.rub.nds.scanner.core.util.JaxbSerializer;
import jakarta.xml.bind.JAXBException;
import java.util.Set;

/**
 * Provides XML serialization and deserialization functionality for Recommendations objects.
 * This class extends JaxbSerializer to handle the marshalling and unmarshalling of recommendation
 * configurations to and from XML format.
 */
public class RecommendationsIO extends JaxbSerializer<Recommendations> {

    /**
     * Constructs a RecommendationsIO serializer for the specified analyzed property class.
     * This constructor initializes the JAXB context with all necessary classes for serialization.
     *
     * @param analyzedPropertyClass the class of analyzed properties to include in the JAXB context
     * @throws JAXBException if there is an error creating the JAXB context
     */
    public RecommendationsIO(Class<? extends AnalyzedProperty> analyzedPropertyClass)
            throws JAXBException {
        super(
                Set.of(
                        Recommendations.class,
                        Recommendation.class,
                        PropertyResultRecommendation.class,
                        analyzedPropertyClass));
    }
}
