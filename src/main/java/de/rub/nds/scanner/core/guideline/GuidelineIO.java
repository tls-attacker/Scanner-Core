/*
 * Scanner Core - A modular framework for probe definition, execution, and result analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.guideline;

import de.rub.nds.scanner.core.probe.AnalyzedProperty;
import de.rub.nds.scanner.core.report.ScanReport;
import de.rub.nds.scanner.core.util.JaxbSerializer;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import java.util.HashSet;
import java.util.Set;

public final class GuidelineIO<ReportT extends ScanReport>
        extends JaxbSerializer<Guideline<ReportT>> {

    public GuidelineIO(
            Class<? extends AnalyzedProperty> analyzedPropertyClass,
            Set<Class<? extends GuidelineCheck<ReportT>>> supportedGuidelineCheckClasses)
            throws JAXBException {
        this.context = getJAXBContext(analyzedPropertyClass, supportedGuidelineCheckClasses);
    }

    private JAXBContext getJAXBContext(
            Class<? extends AnalyzedProperty> analyzedPropertyClass,
            Set<Class<? extends GuidelineCheck<ReportT>>> supportedGuidelineCheckClasses)
            throws JAXBException {
        Set<Class<?>> classesToBeBound = new HashSet<>(supportedGuidelineCheckClasses);
        classesToBeBound.add(analyzedPropertyClass);
        classesToBeBound.add(Guideline.class);
        return getJAXBContext(classesToBeBound);
    }
}
