/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
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
        try {
            return getRequirements().evaluate(report);
        } catch (IllegalArgumentException e) {
            LOGGER.warn(
                    "Cannot evaluate Requirements for Probe \"{}\" ({})",
                    getProbeName(),
                    getClass().getCanonicalName(),
                    e);
            return false;
        }
    }

    protected final void register(AnalyzedProperty... properties) {
        for (AnalyzedProperty property : properties) {
            propertiesMap.put(property, TestResults.UNASSIGNED_ERROR);
        }
    }

    public final <T> void putIfFalse(
            AnalyzedProperty determiningProperty,
            AnalyzedProperty propertyToSet,
            T actualResult,
            String notApplicableReason) {
        putIfEqual(
                determiningProperty,
                propertyToSet,
                actualResult,
                notApplicableReason,
                TestResults.FALSE);
    }

    public final <T> void putIfTrue(
            AnalyzedProperty determiningProperty,
            AnalyzedProperty propertyToSet,
            T actualResult,
            String notApplicableReason) {
        putIfEqual(
                determiningProperty,
                propertyToSet,
                actualResult,
                notApplicableReason,
                TestResults.TRUE);
    }

    public final <T> void putIfEqual(
            AnalyzedProperty determiningProperty,
            AnalyzedProperty propertyToSet,
            T actualResult,
            String notApplicableReason,
            TestResult expectedValue) {
        if (!propertiesMap.containsKey(determiningProperty)
                || propertiesMap.get(determiningProperty) == null
                || !propertiesMap.get(determiningProperty).equals(expectedValue)) {
            put(propertyToSet, new NotApplicableResult(propertyToSet, notApplicableReason));
        } else {
            put(propertyToSet, actualResult);
        }
    }

    private TestResult convertToResult(AnalyzedProperty property, Object result) {
        return switch (result) {
            case null -> new ObjectResult<>(property, null);
            case TestResult testResult -> testResult;
            case String stringValue -> new StringResult(property, stringValue);
            case Long longValue -> new LongResult(property, longValue);
            case Integer intValue -> new IntegerResult(property, intValue);
            case BigInteger bigIntValue -> new BigIntegerResult(property, bigIntValue);
            case Set<?> setValue -> new SetResult<>(property, setValue);
            case Map<?, ?> mapValue -> new MapResult<>(property, mapValue);
            case List<?> listValue -> new ListResult<>(property, listValue);
            default -> new ObjectResult<>(property, result);
        };
    }

    public final <T> void put(AnalyzedProperty property, T result) {
        TestResult internalResult = convertToResult(property, result);

        if (propertiesMap.containsKey(property)) {
            propertiesMap.replace(property, internalResult);
        } else {
            LOGGER.error(
                    "{} was set in {} but had not been registered!",
                    property,
                    getClass().getSimpleName());
            propertiesMap.put(property, internalResult);
        }
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

    public final void setPropertiesToCannotBeTested() {
        for (AnalyzedProperty property : propertiesMap.keySet()) {
            if (propertiesMap.get(property) == TestResults.UNASSIGNED_ERROR) {
                propertiesMap.put(property, TestResults.CANNOT_BE_TESTED);
            }
        }
    }

    public final void merge(ReportT report) {
        if (getStartTime() != 0 && getStopTime() != 0) {
            report.recordProbePerformance(
                    new PerformanceData(getType(), getStartTime(), getStopTime()));
        }
        boolean wasExecuted = getStartTime() != 0;
        mergeData(report);
        for (AnalyzedProperty property : propertiesMap.keySet()) {
            TestResult result = propertiesMap.get(property);
            if (result == TestResults.UNASSIGNED_ERROR || result == null) {
                if (wasExecuted) {
                    LOGGER.error(
                            "{} in {} had not been assigned (or was set to null)!",
                            property,
                            getClass().getSimpleName());
                } else {
                    result = TestResults.COULD_NOT_TEST;
                }
            }
            report.putResult(property, result);
        }
    }

    protected final void extractStats(Iterable<StateT> states) {
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
