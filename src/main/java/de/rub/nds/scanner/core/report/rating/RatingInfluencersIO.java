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
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import java.util.Set;

public class RatingInfluencersIO extends JaxbSerializer<RatingInfluencers> {

    public RatingInfluencersIO(Class<? extends AnalyzedProperty> analyzedPropertyClass)
            throws JAXBException {
        super(createContext(analyzedPropertyClass));
    }

    private static JAXBContext createContext(
            Class<? extends AnalyzedProperty> analyzedPropertyClass) throws JAXBException {
        Set<Class<?>> classes =
                Set.of(
                        RatingInfluencers.class,
                        RatingInfluencer.class,
                        PropertyResultRatingInfluencer.class,
                        analyzedPropertyClass);
        return JAXBContext.newInstance(classes.toArray(new Class[0]));
    }
}
