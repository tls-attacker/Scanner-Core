/*
 * Scanner Core - A modular framework for probe definition, execution, and result analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.probe;

import de.rub.nds.scanner.core.passive.StatsWriter;
import de.rub.nds.scanner.core.probe.requirements.Requirement;
import de.rub.nds.scanner.core.probe.result.*;
import de.rub.nds.scanner.core.report.PerformanceData;
import de.rub.nds.scanner.core.report.ScanReport;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ScannerProbe<ReportT extends ScanReport, StateT>
        implements Callable<ScannerProbe<ReportT, StateT>> {

    private static final Logger LOGGER = LogManager.getLogger();

    private final ProbeType type;
    private final Map<AnalyzedProperty, TestResult> propertiesMap = new HashMap<>();
    private StatsWriter<StateT> writer;

    private long startTime;
    private long stopTime;

    public ScannerProbe(ProbeType type) {
        this.type = type;
    }

    @Override
    public ScannerProbe<ReportT, StateT> call() {
        LOGGER.debug("Executing: {}", getProbeName());
        this.startTime = System.currentTimeMillis();
        executeTest();
        this.stopTime = System.currentTimeMillis();

        LOGGER.debug("Finished {} -  Took {}s", getProbeName(), (stopTime - startTime) / 1000);
        return this;
    }

    public final boolean canBeExecuted(ReportT report) {
        return getRequirements().evaluate(report);
    }

    protected final void register(AnalyzedProperty... properties) {
        for (AnalyzedProperty property : properties) {
            propertiesMap.put(property, TestResults.UNASSIGNED_ERROR);
        }
    }

    public final void put(AnalyzedProperty property, TestResult result) {
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

    public final void put(AnalyzedProperty property, List<?> result) {
        put(property, new ListResult<>(property, result));
    }

    public final void put(AnalyzedProperty property, Map<?, ?> result) {
        put(property, new MapResult<>(property, result));
    }

    public final void put(AnalyzedProperty property, Set<?> result) {
        put(property, new SetResult<>(property, result));
    }

    public final void put(AnalyzedProperty property, BigInteger result) {
        put(property, new BigIntegerResult(property, result));
    }

    public final void put(AnalyzedProperty property, Integer result) {
        put(property, new IntegerResult(property, result));
    }

    public final void put(AnalyzedProperty property, Long result) {
        put(property, new LongResult(property, result));
    }

    public final void put(AnalyzedProperty property, String result) {
        put(property, new StringResult(property, result));
    }

    public final void put(AnalyzedProperty property, Object result) {
        put(property, new ObjectResult<>(property, result));
    }

    protected final <T> void addToList(AnalyzedProperty property, List<T> result) {
        if (property == null) {
            LOGGER.error("Property to add (addToList) to in " + getClass() + " is null!");
            return;
        }
        if (propertiesMap.containsKey(property)) {
            if (result != null) {
                if (propertiesMap.get(property) instanceof ListResult) {
                    //noinspection unchecked
                    result.addAll(((ListResult<T>) propertiesMap.get(property)).getList());
                    put(property, new ListResult<>(property, result));
                } else {
                    put(property, new ListResult<>(property, result));
                }
            }
        } else {
            LOGGER.error(
                    property.getName()
                            + " was set in "
                            + getClass()
                            + " but had not been registered!");
            propertiesMap.put(property, new ListResult<>(property, result));
        }
    }

    protected final void setPropertiesToCouldNotTest() {
        for (AnalyzedProperty property : propertiesMap.keySet()) {
            if (propertiesMap.get(property) == TestResults.UNASSIGNED_ERROR) {
                propertiesMap.put(property, TestResults.COULD_NOT_TEST);
            }
        }
    }

    public final void merge(ReportT report) {
        if (getStartTime() != 0 && getStopTime() != 0) {
            report.recordProbePerformance(
                    new PerformanceData(getType(), getStartTime(), getStopTime()));
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

    protected final void extractStats(List<StateT> states) {
        if (writer != null) {
            for (StateT state : states) {
                getWriter().extract(state);
            }
        }
    }

    public abstract Requirement<ReportT> getRequirements();

    public abstract void adjustConfig(ReportT report);

    protected abstract void executeTest();

    protected abstract void mergeData(ReportT report);

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

    public StatsWriter<StateT> getWriter() {
        return writer;
    }

    public void setWriter(StatsWriter<StateT> writer) {
        this.writer = writer;
    }
}
