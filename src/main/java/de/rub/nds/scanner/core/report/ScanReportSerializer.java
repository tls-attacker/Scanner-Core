/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.report;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.rub.nds.scanner.core.util.ByteArrayHexSerializer;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** This class serializes a scan report to a JSON file. */
public class ScanReportSerializer {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();

        SimpleModule builtInModule = new SimpleModule("ScannerCoreBuiltInModule");
        builtInModule.addKeySerializer(byte[].class, new ByteArrayHexSerializer(true));
        builtInModule.addSerializer(new ByteArrayHexSerializer(false));

        mapper.registerModule(builtInModule);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.configOverride(BigDecimal.class)
                .setFormat(JsonFormat.Value.forShape(JsonFormat.Shape.STRING));
    }

    /**
     * Serializes a scan report to a JSON file.
     *
     * @param outputFile The file to write the report to.
     * @param scanReport The scan report to serialize.
     */
    public static void serialize(File outputFile, ScanReport scanReport) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(outputFile, scanReport);
        } catch (IOException e) {
            LOGGER.error("Could not serialize scan report", e);
        }
    }

    /**
     * Registers a module with the underlying object mapper.
     *
     * @param module The module to register.
     */
    public static void registerModuleWithMapper(Module module) {
        mapper.registerModule(module);
    }
}
