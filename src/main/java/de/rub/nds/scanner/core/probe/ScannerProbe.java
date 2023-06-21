/*
 * Scanner Core - A modular framework for probe definition, execution, and result analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.probe;

import de.rub.nds.scanner.core.constants.*;
import de.rub.nds.scanner.core.passive.StatsWriter;
import de.rub.nds.scanner.core.probe.requirements.Requirement;
import de.rub.nds.scanner.core.report.PerformanceData;
import de.rub.nds.scanner.core.report.ScanReport;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ScannerProbe<R extends ScanReport<R>, P extends ScannerProbe<R, P, S>, S>
        implements Callable<P> {

    private static final Logger LOGGER = LogManager.getLogger();

    private final ProbeType type;
    private final Map<AnalyzedProperty, TestResult> propertiesMap = new HashMap<>();
    private StatsWriter<S> writer;

    private long startTime;
    private long stopTime;

    public ScannerProbe(ProbeType type) {
        this.type = type;
    }

    @Override
    public P call() {
        LOGGER.debug("Executing: {}", getProbeName());
        this.startTime = System.currentTimeMillis();
        executeTest();
        this.stopTime = System.currentTimeMillis();

        LOGGER.debug("Finished {} -  Took {}s", getProbeName(), (stopTime - startTime) / 1000);
        return (P) this;
    }

    public final boolean canBeExecuted(R report) {
        return getRequirements().evaluate(report);
    }

    protected final void register(AnalyzedProperty... properties) {
        for (AnalyzedProperty property : properties) {
            propertiesMap.put(property, TestResults.UNASSIGNED_ERROR);
        }
    }

    protected final void put(AnalyzedProperty property, Object value) {
        TestResult result = null;
        if (value != null) {
            if (value instanceof TestResult) {
                result = (TestResult) value;
            } else if (value instanceof List<?>) {
                result = new ListResult<>((List<?>) value, property.getName());
            } else if (value instanceof Map<?, ?>) {
                result = new MapResult<>((Map<?, ?>) value, property.getName());
            } else if (value instanceof Set<?>) {
                result = new SetResult<>((Set<?>) value, property.getName());
            } else {
                result = TestResults.ERROR_DURING_TEST;
            }
        }
        if (propertiesMap.containsKey(property)) {
            propertiesMap.replace(property, result);
        } else {
            LOGGER.error(
                    "{} was set in {} but had not been registered!",
                    property,
                    getClass().getSimpleName());
            propertiesMap.put(property, result);
        }
    }

    protected final void setPropertiesCouldNotTest() {
        for (AnalyzedProperty property : propertiesMap.keySet()) {
            if (propertiesMap.get(property) == TestResults.UNASSIGNED_ERROR) {
                propertiesMap.put(property, TestResults.COULD_NOT_TEST);
            }
        }
    }

    public final void merge(R report) {
        if (getStartTime() != 0 && getStopTime() != 0) {
            report.getPerformanceList()
                    .add(new PerformanceData(getType(), getStartTime(), getStopTime()));
        }
        mergeData(report);
        TestResult result;
        for (AnalyzedProperty property : propertiesMap.keySet()) {
            result = propertiesMap.get(property);
            report.putResult(property, result);
            if (result == TestResults.UNASSIGNED_ERROR) {
                LOGGER.error(
                        "{} in {} had not been assigned!", property, getClass().getSimpleName());
            }
        }
    }

    public final void extractStats(List<S> states) {
        if (writer != null) {
            for (S state : states) {
                getWriter().extract(state);
            }
        }
    }

    public abstract Requirement<R> getRequirements();

    public abstract void adjustConfig(R report);

    public abstract void executeTest();

    public abstract void mergeData(R report);

    public ProbeType getType() {
        return type;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getStopTime() {
        return stopTime;
    }

    public String getProbeName() {
        return getType().getName();
    }

    public StatsWriter<S> getWriter() {
        return writer;
    }

    public void setWriter(StatsWriter<S> writer) {
        this.writer = writer;
    }
}
