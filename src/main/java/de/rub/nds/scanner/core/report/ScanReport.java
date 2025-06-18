/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.rub.nds.scanner.core.guideline.GuidelineReport;
import de.rub.nds.scanner.core.passive.ExtractedValueContainer;
import de.rub.nds.scanner.core.passive.TrackableValue;
import de.rub.nds.scanner.core.probe.AnalyzedProperty;
import de.rub.nds.scanner.core.probe.ProbeType;
import de.rub.nds.scanner.core.probe.ScannerProbe;
import de.rub.nds.scanner.core.probe.result.BigIntegerResult;
import de.rub.nds.scanner.core.probe.result.CollectionResult;
import de.rub.nds.scanner.core.probe.result.IntegerResult;
import de.rub.nds.scanner.core.probe.result.ListResult;
import de.rub.nds.scanner.core.probe.result.LongResult;
import de.rub.nds.scanner.core.probe.result.MapResult;
import de.rub.nds.scanner.core.probe.result.ObjectResult;
import de.rub.nds.scanner.core.probe.result.SetResult;
import de.rub.nds.scanner.core.probe.result.StringResult;
import de.rub.nds.scanner.core.probe.result.TestResult;
import de.rub.nds.scanner.core.probe.result.TestResults;
import de.rub.nds.scanner.core.report.rating.ScoreReport;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Abstract base class representing the results of a security scan. Contains analyzed properties,
 * extracted values, guideline compliance reports, and performance metrics.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class ScanReport {

    @JsonIgnore
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    @JsonProperty("results")
    private Map<AnalyzedProperty, TestResult> resultMap;

    @JsonProperty("extractedValues")
    private final Map<TrackableValue, ExtractedValueContainer<?>> extractedValueContainerMap;

    private final List<GuidelineReport> guidelineReports;
    private Integer score;
    private ScoreReport scoreReport;

    @JsonIgnore private final Set<ScannerProbe<?, ?>> executedProbes;
    @JsonIgnore private final Set<ScannerProbe<?, ?>> unexecutedProbes;

    private final List<PerformanceData> probePerformanceData;
    private Integer performedConnections;
    private Long scanStartTime;
    private Long scanEndTime;

    /**
     * Constructs a new ScanReport with empty collections.
     */
    protected ScanReport() {
        resultMap = new HashMap<>();
        extractedValueContainerMap = new HashMap<>();
        guidelineReports = new ArrayList<>();
        probePerformanceData = new ArrayList<>();
        executedProbes = new HashSet<>();
        unexecutedProbes = new HashSet<>();
    }

    /**
     * Serializes this report to JSON format.
     *
     * @param outputStream the output stream to write the JSON to
     */
    public abstract void serializeToJson(OutputStream outputStream);

    /**
     * Returns the name of the remote entity that was scanned.
     *
     * @return the remote name (e.g., hostname, IP address)
     */
    public abstract String getRemoteName();

    /**
     * Returns an unmodifiable view of all test results.
     *
     * @return map of analyzed properties to their test results
     */
    public synchronized Map<AnalyzedProperty, TestResult> getResultMap() {
        return Collections.unmodifiableMap(resultMap);
    }

    /**
     * Returns the test result for a specific property.
     *
     * @param property the property to get the result for
     * @return the test result, or NOT_TESTED_YET if not found
     */
    public synchronized TestResult getResult(AnalyzedProperty property) {
        return resultMap.getOrDefault(property, TestResults.NOT_TESTED_YET);
    }

    /**
     * Returns the result as an ObjectResult if it is of that type.
     *
     * @param property the property to get the result for
     * @return the ObjectResult, or null if not found or not an ObjectResult
     */
    public synchronized ObjectResult<?> getObjectResult(AnalyzedProperty property) {
        TestResult result = resultMap.get(property);
        return result instanceof ObjectResult ? (ObjectResult<?>) result : null;
    }

    /**
     * Returns the result as a typed ObjectResult if it matches the expected type.
     *
     * @param <T> the expected value type
     * @param property the property to get the result for
     * @param valueClass the expected class of the value
     * @return the typed ObjectResult, or null if not found or type mismatch
     */
    public synchronized <T> ObjectResult<T> getObjectResult(
            AnalyzedProperty property, Class<T> valueClass) {
        ObjectResult<?> result = getObjectResult(property);
        try {
            return result != null
                    ? new ObjectResult<>(result.getProperty(), valueClass.cast(result.getValue()))
                    : null;
        } catch (ClassCastException e) {
            return null;
        }
    }

    /**
     * Returns the result as a BigIntegerResult if it is of that type.
     *
     * @param property the property to get the result for
     * @return the BigIntegerResult, or null if not found or not a BigIntegerResult
     */
    public synchronized BigIntegerResult getBigIntegerResult(AnalyzedProperty property) {
        TestResult result = resultMap.get(property);
        return result instanceof BigIntegerResult ? (BigIntegerResult) result : null;
    }

    /**
     * Returns the result as an IntegerResult if it is of that type.
     *
     * @param property the property to get the result for
     * @return the IntegerResult, or null if not found or not an IntegerResult
     */
    public synchronized IntegerResult getIntegerResult(AnalyzedProperty property) {
        TestResult result = resultMap.get(property);
        return result instanceof IntegerResult ? (IntegerResult) result : null;
    }

    /**
     * Returns the result as a LongResult if it is of that type.
     *
     * @param property the property to get the result for
     * @return the LongResult, or null if not found or not a LongResult
     */
    public synchronized LongResult getLongResult(AnalyzedProperty property) {
        TestResult result = resultMap.get(property);
        return result instanceof LongResult ? (LongResult) result : null;
    }

    /**
     * Returns the result as a StringResult if it is of that type.
     *
     * @param property the property to get the result for
     * @return the StringResult, or null if not found or not a StringResult
     */
    public synchronized StringResult getStringResult(AnalyzedProperty property) {
        TestResult result = resultMap.get(property);
        return result instanceof StringResult ? (StringResult) result : null;
    }

    /**
     * Returns the result as a CollectionResult if it is of that type.
     *
     * @param property the property to get the result for
     * @return the CollectionResult, or null if not found or not a CollectionResult
     */
    public synchronized CollectionResult<?> getCollectionResult(AnalyzedProperty property) {
        TestResult result = resultMap.get(property);
        return result instanceof CollectionResult ? (CollectionResult<?>) result : null;
    }

    /**
     * Returns the result as a typed CollectionResult if it matches the expected element type.
     *
     * @param <V> the expected element type
     * @param property the property to get the result for
     * @param valueClass the expected class of collection elements
     * @return the typed CollectionResult, or null if not found or type mismatch
     */
    public synchronized <V> CollectionResult<V> getCollectionResult(
            AnalyzedProperty property, Class<V> valueClass) {
        CollectionResult<?> result = getCollectionResult(property);
        if (result == null) {
            return null;
        }
        if (result.getCollection() == null) {
            return new CollectionResult<>(result.getProperty(), null);
        }
        try {
            return new CollectionResult<>(
                    result.getProperty(),
                    result.getCollection().stream()
                            .map(valueClass::cast)
                            .collect(Collectors.toUnmodifiableList()));
        } catch (ClassCastException e) {
            return null;
        }
    }

    /**
     * Returns the result as a ListResult if it is of that type.
     *
     * @param property the property to get the result for
     * @return the ListResult, or null if not found or not a ListResult
     */
    public synchronized ListResult<?> getListResult(AnalyzedProperty property) {
        TestResult result = resultMap.get(property);
        return result instanceof ListResult ? (ListResult<?>) result : null;
    }

    /**
     * Returns the result as a typed ListResult if it matches the expected element type.
     *
     * @param <V> the expected element type
     * @param property the property to get the result for
     * @param valueClass the expected class of list elements
     * @return the typed ListResult, or null if not found or type mismatch
     */
    public synchronized <V> ListResult<V> getListResult(
            AnalyzedProperty property, Class<V> valueClass) {
        ListResult<?> result = getListResult(property);
        if (result == null) {
            return null;
        }
        if (result.getList() == null) {
            return new ListResult<>(result.getProperty(), null);
        }
        try {
            return new ListResult<>(
                    result.getProperty(),
                    result.getList().stream()
                            .map(valueClass::cast)
                            .collect(Collectors.toUnmodifiableList()));
        } catch (ClassCastException e) {
            return null;
        }
    }

    /**
     * Returns the result as a MapResult if it is of that type.
     *
     * @param property the property to get the result for
     * @return the MapResult, or null if not found or not a MapResult
     */
    public synchronized MapResult<?, ?> getMapResult(AnalyzedProperty property) {
        TestResult result = resultMap.get(property);
        return result instanceof MapResult ? (MapResult<?, ?>) result : null;
    }

    /**
     * Returns the result as a MapResult with typed values.
     *
     * @param <V> the expected value type
     * @param property the property to get the result for
     * @param valueClass the expected class of map values
     * @return the typed MapResult, or null if not found or type mismatch
     */
    public synchronized <V> MapResult<?, V> getMapResult(
            AnalyzedProperty property, Class<V> valueClass) {
        return getMapResult(property, Object.class, valueClass);
    }

    /**
     * Returns the result as a fully typed MapResult.
     *
     * @param <K> the expected key type
     * @param <V> the expected value type
     * @param property the property to get the result for
     * @param keyClass the expected class of map keys
     * @param valueClass the expected class of map values
     * @return the typed MapResult, or null if not found or type mismatch
     */
    public synchronized <K, V> MapResult<K, V> getMapResult(
            AnalyzedProperty property, Class<K> keyClass, Class<V> valueClass) {
        MapResult<?, ?> result = getMapResult(property);
        if (result == null) {
            return null;
        }
        if (result.getMap() == null) {
            return new MapResult<>(result.getProperty(), null);
        }
        Map<K, V> typedMap = new HashMap<>();
        try {
            result.getMap()
                    .forEach(
                            (key, value) ->
                                    typedMap.put(keyClass.cast(key), valueClass.cast(value)));
        } catch (ClassCastException e) {
            return null;
        }
        return new MapResult<>(result.getProperty(), Collections.unmodifiableMap(typedMap));
    }

    /**
     * Returns the result as a SetResult if it is of that type.
     *
     * @param property the property to get the result for
     * @return the SetResult, or null if not found or not a SetResult
     */
    public synchronized SetResult<?> getSetResult(AnalyzedProperty property) {
        TestResult result = resultMap.get(property);
        return result instanceof SetResult ? (SetResult<?>) result : null;
    }

    /**
     * Returns the result as a typed SetResult if it matches the expected element type.
     *
     * @param <V> the expected element type
     * @param property the property to get the result for
     * @param valueClass the expected class of set elements
     * @return the typed SetResult, or null if not found or type mismatch
     */
    public synchronized <V> SetResult<V> getSetResult(
            AnalyzedProperty property, Class<V> valueClass) {
        SetResult<?> result = getSetResult(property);
        if (result == null) {
            return null;
        }
        if (result.getSet() == null) {
            return new SetResult<>(result.getProperty(), null);
        }
        try {
            return new SetResult<>(
                    result.getProperty(),
                    result.getSet().stream()
                            .map(valueClass::cast)
                            .collect(Collectors.toUnmodifiableSet()));
        } catch (ClassCastException e) {
            return null;
        }
    }

    /**
     * Stores a test result for a property and notifies listeners of the change.
     *
     * @param property the property to store the result for
     * @param result the test result to store
     */
    public synchronized void putResult(AnalyzedProperty property, TestResult result) {
        TestResult oldResult = resultMap.get(property);
        resultMap.put(property, result);
        propertyChangeSupport.firePropertyChange(property.toString(), oldResult, result);
    }

    /**
     * Stores a Boolean result for a property, converting it to appropriate TestResult.
     *
     * @param property the property to store the result for
     * @param result the Boolean value (TRUE, FALSE, or null for UNCERTAIN)
     */
    public synchronized void putResult(AnalyzedProperty property, Boolean result) {
        this.putResult(
                property,
                Objects.equals(result, Boolean.TRUE)
                        ? TestResults.TRUE
                        : Objects.equals(result, Boolean.FALSE)
                                ? TestResults.FALSE
                                : TestResults.UNCERTAIN);
    }

    /**
     * Stores a BigInteger result for a property.
     *
     * @param property the property to store the result for
     * @param result the BigInteger value
     */
    public synchronized void putResult(AnalyzedProperty property, BigInteger result) {
        this.putResult(property, new BigIntegerResult(property, result));
    }

    /**
     * Stores an Integer result for a property.
     *
     * @param property the property to store the result for
     * @param result the Integer value
     */
    public synchronized void putResult(AnalyzedProperty property, Integer result) {
        this.putResult(property, new IntegerResult(property, result));
    }

    /**
     * Stores a Long result for a property.
     *
     * @param property the property to store the result for
     * @param result the Long value
     */
    public synchronized void putResult(AnalyzedProperty property, Long result) {
        this.putResult(property, new LongResult(property, result));
    }

    /**
     * Stores a String result for a property.
     *
     * @param property the property to store the result for
     * @param result the String value
     */
    public synchronized void putResult(AnalyzedProperty property, String result) {
        this.putResult(property, new StringResult(property, result));
    }

    /**
     * Stores a List result for a property.
     *
     * @param property the property to store the result for
     * @param result the List value
     */
    public synchronized void putResult(AnalyzedProperty property, List<?> result) {
        this.putResult(property, new ListResult<>(property, result));
    }

    /**
     * Stores a Set result for a property.
     *
     * @param property the property to store the result for
     * @param result the Set value
     */
    public synchronized void putResult(AnalyzedProperty property, Set<?> result) {
        this.putResult(property, new SetResult<>(property, result));
    }

    /**
     * Stores a Map result for a property.
     *
     * @param property the property to store the result for
     * @param result the Map value
     */
    public synchronized void putResult(AnalyzedProperty property, Map<?, ?> result) {
        this.putResult(property, new MapResult<>(property, result));
    }

    /**
     * Stores a generic object result for a property.
     *
     * @param <T> the type of the result
     * @param property the property to store the result for
     * @param result the object value
     */
    public synchronized <T> void putResult(AnalyzedProperty property, T result) {
        this.putResult(property, new ObjectResult<>(property, result));
    }

    /**
     * Removes a test result for a property and notifies listeners.
     *
     * @param property the property to remove the result for
     */
    public synchronized void removeResult(AnalyzedProperty property) {
        TestResult oldResult = resultMap.remove(property);
        propertyChangeSupport.firePropertyChange(property.toString(), oldResult, null);
    }

    /**
     * Returns an unmodifiable view of all extracted values from the scan.
     *
     * @return map of trackable values to their containers
     */
    public synchronized Map<TrackableValue, ExtractedValueContainer<?>>
            getExtractedValueContainerMap() {
        return Collections.unmodifiableMap(extractedValueContainerMap);
    }

    /**
     * Returns the extracted value container for a specific trackable value.
     *
     * @param trackableValue the value to get the container for
     * @return the extracted value container, or null if not found
     */
    public synchronized ExtractedValueContainer<?> getExtractedValueContainer(
            TrackableValue trackableValue) {
        return extractedValueContainerMap.get(trackableValue);
    }

    /**
     * Returns the extracted value container for a specific trackable value with type casting.
     *
     * @param <T> the expected value type
     * @param trackableValue the value to get the container for
     * @param valueClass the expected class of the value (for type safety)
     * @return the typed extracted value container, or null if not found
     */
    public synchronized <T> ExtractedValueContainer<T> getExtractedValueContainer(
            TrackableValue trackableValue, Class<T> valueClass) {
        //noinspection unchecked
        return (ExtractedValueContainer<T>) extractedValueContainerMap.get(trackableValue);
    }

    /**
     * Stores an extracted value container.
     *
     * @param trackableValue the trackable value key
     * @param extractedValueContainer the container to store
     */
    public synchronized void putExtractedValueContainer(
            TrackableValue trackableValue, ExtractedValueContainer<?> extractedValueContainer) {
        extractedValueContainerMap.put(trackableValue, extractedValueContainer);
    }

    /**
     * Stores multiple extracted value containers at once.
     *
     * @param extractedValueContainerMap map of containers to store
     */
    public synchronized void putAllExtractedValueContainers(
            Map<TrackableValue, ExtractedValueContainer<?>> extractedValueContainerMap) {
        this.extractedValueContainerMap.putAll(extractedValueContainerMap);
    }

    /**
     * Returns an unmodifiable list of guideline compliance reports.
     *
     * @return list of guideline reports
     */
    public synchronized List<GuidelineReport> getGuidelineReports() {
        return Collections.unmodifiableList(guidelineReports);
    }

    /**
     * Adds a guideline compliance report to this scan report.
     *
     * @param guidelineReport the guideline report to add
     */
    public synchronized void addGuidelineReport(GuidelineReport guidelineReport) {
        guidelineReports.add(guidelineReport);
    }

    /**
     * Returns the overall security score for the scan.
     *
     * @return the score value, or null if not calculated
     */
    public synchronized Integer getScore() {
        return score;
    }

    /**
     * Sets the overall security score for the scan.
     *
     * @param score the score value
     */
    public synchronized void setScore(Integer score) {
        this.score = score;
    }

    /**
     * Returns the detailed score report.
     *
     * @return the score report, or null if not generated
     */
    public synchronized ScoreReport getScoreReport() {
        return scoreReport;
    }

    /**
     * Sets the detailed score report.
     *
     * @param scoreReport the score report to set
     */
    public synchronized void setScoreReport(ScoreReport scoreReport) {
        this.scoreReport = scoreReport;
    }

    /**
     * Checks if a probe of the specified type has already been executed.
     *
     * @param type the probe type to check
     * @return true if a probe of this type was executed, false otherwise
     */
    public synchronized boolean isProbeAlreadyExecuted(ProbeType type) {
        return getExecutedProbeTypes().contains(type);
    }

    /**
     * Returns an unmodifiable list of performance data for executed probes.
     *
     * @return list of performance data
     */
    public synchronized List<PerformanceData> getProbePerformanceData() {
        return Collections.unmodifiableList(probePerformanceData);
    }

    /**
     * Records performance data for a probe execution.
     *
     * @param performanceData the performance data to record
     */
    public synchronized void recordProbePerformance(PerformanceData performanceData) {
        probePerformanceData.add(performanceData);
    }

    /**
     * Marks a probe as executed and notifies listeners.
     *
     * @param probe the probe that was executed
     */
    public synchronized void markProbeAsExecuted(ScannerProbe<?, ?> probe) {
        executedProbes.add(probe);
        propertyChangeSupport.firePropertyChange("supportedProbe", null, probe.getProbeName());
    }

    /**
     * Marks a probe as unexecuted (skipped) and notifies listeners.
     *
     * @param probe the probe that was not executed
     */
    public synchronized void markProbeAsUnexecuted(ScannerProbe<?, ?> probe) {
        unexecutedProbes.add(probe);
        propertyChangeSupport.firePropertyChange("unsupportedProbe", null, probe.getProbeName());
    }

    /**
     * Returns an unmodifiable set of all executed probes.
     *
     * @return set of executed probes
     */
    public synchronized Set<ScannerProbe<?, ?>> getExecutedProbes() {
        return Collections.unmodifiableSet(executedProbes);
    }

    /**
     * Returns the types of all executed probes.
     *
     * @return set of executed probe types
     */
    public synchronized Set<ProbeType> getExecutedProbeTypes() {
        return executedProbes.stream()
                .map(ScannerProbe::getType)
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Returns an unmodifiable set of all unexecuted (skipped) probes.
     *
     * @return set of unexecuted probes
     */
    public synchronized Set<ScannerProbe<?, ?>> getUnexecutedProbes() {
        return Collections.unmodifiableSet(unexecutedProbes);
    }

    /**
     * Returns the types of all unexecuted probes.
     *
     * @return set of unexecuted probe types
     */
    public synchronized Set<ProbeType> getUnexecutedProbeTypes() {
        return unexecutedProbes.stream()
                .map(ScannerProbe::getType)
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Returns the total number of network connections performed during the scan.
     *
     * @return the connection count, or null if not tracked
     */
    public synchronized Integer getPerformedConnections() {
        return performedConnections;
    }

    /**
     * Sets the total number of network connections performed during the scan.
     *
     * @param performedConnections the connection count
     */
    public synchronized void setPerformedConnections(Integer performedConnections) {
        this.performedConnections = performedConnections;
    }

    /**
     * Returns the timestamp when the scan started.
     *
     * @return the start time in milliseconds since epoch, or null if not set
     */
    public synchronized Long getScanStartTime() {
        return scanStartTime;
    }

    /**
     * Sets the timestamp when the scan started.
     *
     * @param scanStartTime the start time in milliseconds since epoch
     */
    public synchronized void setScanStartTime(Long scanStartTime) {
        this.scanStartTime = scanStartTime;
    }

    /**
     * Returns the timestamp when the scan ended.
     *
     * @return the end time in milliseconds since epoch, or null if not set
     */
    public synchronized Long getScanEndTime() {
        return scanEndTime;
    }

    /**
     * Sets the timestamp when the scan ended.
     *
     * @param scanStopTime the end time in milliseconds since epoch
     */
    public synchronized void setScanEndTime(Long scanStopTime) {
        this.scanEndTime = scanStopTime;
    }

    /**
     * Adds a property change listener to be notified of report updates.
     *
     * @param listener the listener to add
     */
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Removes a property change listener.
     *
     * @param listener the listener to remove
     */
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
}
