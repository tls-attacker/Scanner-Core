/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.probe;

import com.beust.jcommander.IStringConverter;
import java.util.Set;
import java.util.stream.Collectors;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

public class ProbeTypeConverter implements IStringConverter<ProbeType> {

    private Set<Class<? extends ProbeType>> probeTypeClasses;

    public ProbeTypeConverter() {
        String packageName = "de.rub";
        Reflections reflections =
                new Reflections(
                        new ConfigurationBuilder()
                                .setUrls(ClasspathHelper.forPackage(packageName))
                                .filterInputsBy(new FilterBuilder().includePackage(packageName)));
        probeTypeClasses =
                reflections.getSubTypesOf(ProbeType.class).stream()
                        .filter(listed -> !listed.isInterface())
                        .collect(Collectors.toSet());
    }

    @Override
    public ProbeType convert(String value) {
        for (Class<? extends ProbeType> probeTypeClass : probeTypeClasses) {
            // Call valueof method of each enum class
            try {
                ProbeType convertedType =
                        (ProbeType)
                                probeTypeClass
                                        .getMethod("valueOf", String.class)
                                        .invoke(null, value);
                if (convertedType != null) {
                    return convertedType;
                }
            } catch (Exception e) {
                // Ignore
            }
        }
        return null;
    }
}
