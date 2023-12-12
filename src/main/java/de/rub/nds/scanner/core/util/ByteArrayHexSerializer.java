/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;

/**
 * Serializer for byte arrays in key and value positions. The serializer will output the byte array
 * as a hex string. Behaviour (key or value) can be specified in the constructor.
 */
public class ByteArrayHexSerializer extends StdSerializer<byte[]> {

    private final boolean keySerializer;

    /**
     * Create a new serializer. Behaviour (key or value) can be specified via the keySerializer
     * parameter.
     *
     * @param keySerializer true if the serializer should be used for keys, false if it should be
     *     used for values
     */
    public ByteArrayHexSerializer(boolean keySerializer) {
        super(byte[].class);
        this.keySerializer = keySerializer;
    }

    @Override
    public void serialize(
            byte[] bytes, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        if (keySerializer) {
            jsonGenerator.writeFieldName(bytesToRawHexString(bytes));
        } else {
            jsonGenerator.writeString(bytesToRawHexString(bytes));
        }
    }

    private static String bytesToRawHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
