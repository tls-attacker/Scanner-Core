/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.util;

import de.rub.nds.protocol.util.SilentByteArrayOutputStream;
import jakarta.xml.bind.*;
import jakarta.xml.bind.util.JAXBSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class JaxbSerializer<T> {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final Map<Integer, JAXBContext> contextMap = new HashMap<>();

    protected JAXBContext context;

    protected JaxbSerializer() {}

    protected JaxbSerializer(Set<Class<?>> classesToBeBound) {
        try {
            this.context = getJAXBContext(classesToBeBound);
        } catch (JAXBException e) {
            throw new IllegalStateException("Failed to create JAXB context", e);
        }
    }

    protected synchronized JAXBContext getJAXBContext(Set<Class<?>> classesToBeBound)
            throws JAXBException {
        int classesHash = classesToBeBound.hashCode();
        if (contextMap.containsKey(classesHash)) {
            return contextMap.get(classesHash);
        }
        JAXBContext context = JAXBContext.newInstance(classesToBeBound.toArray(new Class[0]));
        contextMap.put(classesHash, context);
        return context;
    }

    /**
     * Writes the specified object to a file using JAXB marshalling.
     *
     * @param file the file to write to
     * @param obj the object to serialize
     * @throws IOException if an I/O error occurs during writing
     * @throws JAXBException if an error occurs during JAXB marshalling
     */
    public void write(File file, T obj) throws IOException, JAXBException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            write(fos, obj);
        }
    }

    /**
     * Writes the specified object to an output stream using JAXB marshalling.
     *
     * @param outputStream the output stream to write to
     * @param obj the object to serialize
     * @throws JAXBException if an error occurs during JAXB marshalling
     * @throws IOException if an I/O error occurs during writing
     */
    public void write(OutputStream outputStream, T obj) throws JAXBException, IOException {
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        try (SilentByteArrayOutputStream tempStream = new SilentByteArrayOutputStream()) {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(new JAXBSource(context, obj), new StreamResult(tempStream));
            String xmlText = tempStream.toString().replaceAll("\r?\n", System.lineSeparator());
            outputStream.write(xmlText.getBytes(StandardCharsets.UTF_8));
        } catch (TransformerException e) {
            LOGGER.warn(e);
        }
    }

    /**
     * Reads an object from a file using JAXB unmarshalling.
     *
     * @param file the file to read from
     * @return the deserialized object
     * @throws IOException if an I/O error occurs during reading
     * @throws JAXBException if an error occurs during JAXB unmarshalling
     * @throws XMLStreamException if an error occurs during XML stream processing
     */
    public T read(File file) throws IOException, JAXBException, XMLStreamException {
        try (FileInputStream fis = new FileInputStream(file)) {
            return read(fis);
        }
    }

    /**
     * Reads an object from an input stream using JAXB unmarshalling.
     *
     * @param inputStream the input stream to read from
     * @return the deserialized object
     * @throws JAXBException if an error occurs during JAXB unmarshalling
     * @throws XMLStreamException if an error occurs during XML stream processing
     */
    public T read(InputStream inputStream) throws JAXBException, XMLStreamException {
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setEventHandler(
                event -> {
                    // raise an Exception also on Warnings
                    return false;
                });
        XMLInputFactory xif = XMLInputFactory.newFactory();
        xif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        xif.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        XMLStreamReader xsr = xif.createXMLStreamReader(inputStream);
        try {
            return (T) unmarshaller.unmarshal(xsr);
        } finally {
            xsr.close();
        }
    }
}
