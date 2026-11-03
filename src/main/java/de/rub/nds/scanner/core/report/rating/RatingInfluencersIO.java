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
import java.util.HashSet;
import java.util.Set;

/**
 * Provides XML serialization and deserialization functionality for RatingInfluencers objects. This
 * class extends JaxbSerializer to handle the marshalling and unmarshalling of rating influencer
 * configurations to and from XML format.
 */
public class RatingInfluencersIO extends JaxbSerializer<RatingInfluencers> {

    /**
     * Constructs a RatingInfluencersIO serializer for the specified analyzed property and score
     * type classes. This constructor initializes the JAXB context with all necessary classes for
     * serialization.
     *
     * @param analyzedPropertyClass the class of analyzed properties to include in the JAXB context
     * @param scoreTypeClass the class of score types to include in the JAXB context
     * @throws JAXBException if there is an error creating the JAXB context
     */
    public RatingInfluencersIO(
            Class<? extends AnalyzedProperty> analyzedPropertyClass,
            Class<? extends ScoreType> scoreTypeClass)
            throws JAXBException {
        this(Set.of(analyzedPropertyClass), Set.of(scoreTypeClass));
    }

    /**
     * Constructs a RatingInfluencersIO serializer for the specified analyzed property and score
     * type classes. This allows mixing properties and score types of different classes in one file.
     * Each class must use a distinct XML root element name, as the element name determines the
     * class of a property or score type during deserialization.
     *
     * @param analyzedPropertyClasses the classes of analyzed properties to include in the JAXB
     *     context
     * @param scoreTypeClasses the classes of score types to include in the JAXB context
     * @throws JAXBException if there is an error creating the JAXB context
     */
    public RatingInfluencersIO(
            Set<Class<? extends AnalyzedProperty>> analyzedPropertyClasses,
            Set<Class<? extends ScoreType>> scoreTypeClasses)
            throws JAXBException {
        super(getClassesToBeBound(analyzedPropertyClasses, scoreTypeClasses));
    }

    private static Set<Class<?>> getClassesToBeBound(
            Set<Class<? extends AnalyzedProperty>> analyzedPropertyClasses,
            Set<Class<? extends ScoreType>> scoreTypeClasses) {
        Set<Class<?>> classesToBeBound =
                new HashSet<>(
                        Set.of(
                                RatingInfluencers.class,
                                RatingInfluencer.class,
                                PropertyResultRatingInfluencer.class,
                                ScoreModifier.class));
        classesToBeBound.addAll(analyzedPropertyClasses);
        classesToBeBound.addAll(scoreTypeClasses);
        return classesToBeBound;
    }
}
