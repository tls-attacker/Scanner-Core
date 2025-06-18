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

    /**
     * Constructs a new ScannerProbe with the specified probe type.
     *
     * @param type the type of this probe
     */
    public ScannerProbe(ProbeType type) {
        this.type = type;
    }

    /**
     * Executes this probe and returns itself after completion.
     *
     * @return this probe instance after execution
     */
    @Override
    public ScannerProbe<ReportT, StateT> call() {
        LOGGER.debug("Executing: {}", getProbeName());
        this.startTime = System.currentTimeMillis();
        executeTest();
        this.stopTime = System.currentTimeMillis();

        LOGGER.debug("Finished {} -  Took {}s", getProbeName(), (stopTime - startTime) / 1000);
        return this;
    }

    /**
     * Determines whether this probe can be executed based on the requirements evaluation.
     *
     * @param report the report to evaluate requirements against
     * @return true if the probe can be executed, false otherwise
     */
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

    /**
     * Sets a property value if the determining property is FALSE.
     *
     * @param determiningProperty the property to check
     * @param propertyToSet the property to set
     * @param actualResult the result to set if condition is met
     * @param notApplicableReason reason to provide if condition is not met
     * @param <T> the type of the actual result
     */
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

    /**
     * Sets a property value if the determining property is TRUE.
     *
     * @param determiningProperty the property to check
     * @param propertyToSet the property to set
     * @param actualResult the result to set if condition is met
     * @param notApplicableReason reason to provide if condition is not met
     * @param <T> the type of the actual result
     */
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

    /**
     * Sets a property value if the determining property equals the expected value.
     *
     * @param determiningProperty the property to check
     * @param propertyToSet the property to set
     * @param actualResult the result to set if condition is met
     * @param notApplicableReason reason to provide if condition is not met
     * @param expectedValue the expected value to compare against
     * @param <T> the type of the actual result
     */
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

    /**
     * Sets a property to the specified result value.
     *
     * @param property the property to set
     * @param result the result value to set
     * @param <T> the type of the result
     */
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

    /**
     * Sets all unassigned properties to CANNOT_BE_TESTED status.
     */
    public final void setPropertiesToCannotBeTested() {
        for (AnalyzedProperty property : propertiesMap.keySet()) {
            if (propertiesMap.get(property) == TestResults.UNASSIGNED_ERROR) {
                propertiesMap.put(property, TestResults.CANNOT_BE_TESTED);
            }
        }
    }

    /**
     * Merges the results of this probe into the provided report.
     *
     * @param report the report to merge results into
     */
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

    /**
     * Gets the requirements that must be satisfied for this probe to execute.
     *
     * @return the requirements for this probe
     */
    public abstract Requirement<ReportT> getRequirements();

    /**
     * Adjusts the configuration based on the provided report.
     *
     * @param report the report used to adjust the configuration
     */
    public abstract void adjustConfig(ReportT report);

    protected abstract void executeTest();

    protected abstract void mergeData(ReportT report);

    /**
     * Gets the type of this probe.
     *
     * @return the probe type
     */
    public ProbeType getType() {
        return type;
    }

    /**
     * Gets the start time of the probe execution in milliseconds.
     *
     * @return the start time
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Gets the stop time of the probe execution in milliseconds.
     *
     * @return the stop time
     */
    public long getStopTime() {
        return stopTime;
    }

    /**
     * Gets the name of this probe.
     *
     * @return the probe name
     */
    public String getProbeName() {
        return getType().getName();
    }

    /**
     * Gets the stats writer associated with this probe.
     *
     * @return the stats writer
     */
    public StatsWriter<StateT> getWriter() {
        return writer;
    }

    /**
     * Sets the stats writer for this probe.
     *
     * @param writer the stats writer to set
     */
    public void setWriter(StatsWriter<StateT> writer) {
        this.writer = writer;
    }
}
