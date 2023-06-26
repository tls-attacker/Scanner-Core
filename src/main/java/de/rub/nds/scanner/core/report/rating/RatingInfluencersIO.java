/*
 * Scanner Core - A modular framework for probe definition, execution, and result analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.report.rating;

import de.rub.nds.scanner.core.probe.AnalyzedProperty;
import de.rub.nds.scanner.core.io.JAXBIO;
import jakarta.xml.bind.JAXBException;
import java.util.Set;

public class RatingInfluencersIO extends JAXBIO<RatingInfluencers> {

    public RatingInfluencersIO(Class<? extends AnalyzedProperty> analyzedPropertyClass)
            throws JAXBException {
        super(
                Set.of(
                        RatingInfluencers.class,
                        RatingInfluencer.class,
                        PropertyResultRatingInfluencer.class,
                        analyzedPropertyClass));
    }
}
