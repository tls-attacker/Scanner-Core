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

    protected JaxbSerializer(Set<Class<?>> classesToBeBound) throws JAXBException {
        this.context = getJAXBContext(classesToBeBound);
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

    public void write(File file, T obj) throws IOException, JAXBException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            write(fos, obj);
        }
    }

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
            outputStream.write(xmlText.getBytes());
        } catch (TransformerException e) {
            LOGGER.warn(e);
        }
    }

    public T read(File file) throws IOException, JAXBException, XMLStreamException {
        try (FileInputStream fis = new FileInputStream(file)) {
            return read(fis);
        }
    }

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
        return (T) unmarshaller.unmarshal(xsr);
    }
}
