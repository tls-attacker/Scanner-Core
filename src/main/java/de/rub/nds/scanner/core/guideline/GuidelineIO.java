/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.guideline;

import de.rub.nds.scanner.core.probe.AnalyzedProperty;
import de.rub.nds.scanner.core.util.JaxbSerializer;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import java.util.HashSet;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

public final class GuidelineIO extends JaxbSerializer<Guideline<?>> {

    private Logger LOGGER = LogManager.getLogger();

    public GuidelineIO(Class<? extends AnalyzedProperty> analyzedPropertyClass)
            throws JAXBException {
        // analyzedPropertyClass parameter kept for API compatibility
        this.context = getJAXBContext();
    }

    private JAXBContext getJAXBContext()
            throws JAXBException {
        if (context == null) {
            // TODO we could do this scanning during building and then just collect the
            // results
            // TODO it would also be good if we didn't have to hardcode the package name
            // here, but I could not get it work without it. Hours wasted: 3
            String packageName = "de.rub";
            Reflections reflections =
                    new Reflections(
                            new ConfigurationBuilder()
                                    .setUrls(ClasspathHelper.forPackage(packageName))
                                    .filterInputsBy(
                                            new FilterBuilder().includePackage(packageName)));
            Set<Class<? extends GuidelineCheck>> guidelineCheckClasses =
                    reflections.getSubTypesOf(GuidelineCheck.class);
            Set<Class<?>> classes = new HashSet<>();
            classes.add(Guideline.class);
            classes.addAll(guidelineCheckClasses);
            LOGGER.debug("Registering GuidelineClasses in JAXBContext:");
            for (Class tempClass : classes) {
                LOGGER.debug(tempClass.getName());
            }
            context = JAXBContext.newInstance(classes.toArray(new Class[classes.size()]));
        }

        return context;
    }
}
