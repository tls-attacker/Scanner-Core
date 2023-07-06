/*
 * Scanner Core - A modular framework for probe definition, execution, and result analysis.
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
