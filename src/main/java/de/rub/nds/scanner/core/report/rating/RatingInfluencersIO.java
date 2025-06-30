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
import java.util.Set;

public class RatingInfluencersIO extends JaxbSerializer<RatingInfluencers> {

    /**
     * Constructs a RatingInfluencersIO with support for the specified AnalyzedProperty class.
     * Creates a JAXB serializer configured to handle RatingInfluencers and related classes.
     *
     * @param analyzedPropertyClass the class of AnalyzedProperty to support in serialization
     */
    public RatingInfluencersIO(Class<? extends AnalyzedProperty> analyzedPropertyClass) {
        super(
                Set.of(
                        RatingInfluencers.class,
                        RatingInfluencer.class,
                        PropertyResultRatingInfluencer.class,
                        analyzedPropertyClass));
    }
}
