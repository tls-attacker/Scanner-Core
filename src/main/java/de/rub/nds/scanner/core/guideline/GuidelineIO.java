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
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;
import javax.xml.stream.XMLStreamException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

public final class GuidelineIO extends JaxbSerializer<Guideline> {

    private Logger LOGGER = LogManager.getLogger();

    public GuidelineIO(Class<? extends AnalyzedProperty> analyzedPropertyClass)
            throws JAXBException {
        // analyzedPropertyClass parameter kept for API compatibility
        this.context = getJAXBContext();
    }

    private JAXBContext getJAXBContext() throws JAXBException {
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

    private static List<String> listXmlFiles(ClassLoader classLoader, String folder)
            throws IOException, URISyntaxException {
        List<String> xmlFilePaths = new ArrayList<>();
        URL url = classLoader.getResource(folder);

        if (url == null) {
            throw new IOException("Folder not found: " + folder);
        }

        String protocol = url.getProtocol();

        if ("file".equals(protocol)) {
            // Development mode - reading directly from filesystem
            Path path = Paths.get(url.toURI());
            try (Stream<Path> paths = Files.list(path)) {
                paths.filter(p -> p.toString().endsWith(".xml"))
                        .forEach(p -> xmlFilePaths.add(folder + "/" + p.getFileName().toString()));
            }
        } else if ("jar".equals(protocol)) {
            // Running from a jar
            JarURLConnection jarConnection = (JarURLConnection) url.openConnection();
            try (JarFile jarFile = jarConnection.getJarFile()) {
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();
                    if (name.startsWith(folder + "/")
                            && name.endsWith(".xml")
                            && !entry.isDirectory()) {
                        xmlFilePaths.add(name);
                    }
                }
            }
        } else {
            throw new IOException("Unsupported protocol: " + protocol);
        }

        return xmlFilePaths;
    }

    public List<Guideline> readGuidelines(ClassLoader classLoader, String subFolder) {

        LOGGER.debug("Loading guidelines from files...");

        List<Guideline> guidelines = new ArrayList<>();

        try {
            // Get all files in guideline folder
            List<String> xmlFilePaths = listXmlFiles(classLoader, subFolder);

            for (String path : xmlFilePaths) {
                try (InputStream input = classLoader.getResourceAsStream(path)) {
                    if (input != null) {
                        guidelines.add(read(input));
                    }
                }
            }

        } catch (XMLStreamException | IOException | JAXBException | URISyntaxException e) {
            LOGGER.error("Error reading guideline reports", e);
            return new ArrayList<>();
        }

        return guidelines;
    }
}
