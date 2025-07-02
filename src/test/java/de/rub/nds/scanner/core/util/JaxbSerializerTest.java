/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.util;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.xml.bind.*;
import jakarta.xml.bind.annotation.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import javax.xml.stream.XMLStreamException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class JaxbSerializerTest {

    @TempDir private File tempDir;

    private TestJaxbSerializer serializer;

    @BeforeEach
    void setUp() throws JAXBException {
        serializer = new TestJaxbSerializer();
    }

    @Test
    void testWriteToStreamWithUTF8Characters() throws JAXBException, IOException {
        TestObject obj = new TestObject();
        obj.setText("Test with UTF-8 characters: é, ñ, ü, 中文");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        serializer.write(baos, obj);

        String result = baos.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("Test with UTF-8 characters: é, ñ, ü, 中文"));
    }

    @Test
    void testWriteToFileWithUTF8Characters() throws JAXBException, IOException, XMLStreamException {
        TestObject obj = new TestObject();
        obj.setText("Test with UTF-8 characters: é, ñ, ü, 中文");

        File file = new File(tempDir, "test-utf8.xml");
        serializer.write(file, obj);

        TestObject readObj = serializer.read(file);
        assertEquals("Test with UTF-8 characters: é, ñ, ü, 中文", readObj.getText());
    }

    @Test
    void testReadFromStreamWithUTF8Characters() throws JAXBException, XMLStreamException {
        String xml =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<testObject>\n"
                        + "    <text>Test with UTF-8 characters: é, ñ, ü, 中文</text>\n"
                        + "</testObject>";

        ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
        TestObject obj = serializer.read(bais);

        assertEquals("Test with UTF-8 characters: é, ñ, ü, 中文", obj.getText());
    }

    @XmlRootElement(name = "testObject")
    @XmlAccessorType(XmlAccessType.FIELD)
    static class TestObject {
        @XmlElement private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    static class TestJaxbSerializer extends JaxbSerializer<TestObject> {
        public TestJaxbSerializer() throws JAXBException {
            super(Set.of(TestObject.class));
        }
    }
}
