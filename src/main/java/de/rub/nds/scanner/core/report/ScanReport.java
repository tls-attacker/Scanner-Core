/*
 * Scanner Core - A modular framework for probe definition, execution, and result analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.report;

import de.rub.nds.scanner.core.guideline.GuidelineReport;
import de.rub.nds.scanner.core.passive.ExtractedValueContainer;
import de.rub.nds.scanner.core.passive.TrackableValue;
import de.rub.nds.scanner.core.probe.AnalyzedProperty;
import de.rub.nds.scanner.core.probe.ProbeType;
import de.rub.nds.scanner.core.probe.result.*;
import de.rub.nds.scanner.core.report.rating.ScoreReport;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class ScanReport extends Observable {

    private final HashMap<AnalyzedProperty, TestResult> resultMap;
    private final Map<TrackableValue, ExtractedValueContainer<?>> extractedValueContainerMap;

    private final List<GuidelineReport> guidelineReports;
    private int score;
    private ScoreReport scoreReport;

    private final Set<ProbeType> executedProbes;
    private final Set<ProbeType> unexecutedProbes;

    private final List<PerformanceData> probePerformanceData;
    private int performedConnections;
    private long scanStartTime;
    private long scanEndTime;

    public ScanReport() {
        resultMap = new HashMap<>();
        extractedValueContainerMap = new HashMap<>();
        guidelineReports = new ArrayList<>();
        probePerformanceData = new ArrayList<>();
        executedProbes = new HashSet<>();
        unexecutedProbes = new HashSet<>();
    }

    public synchronized Map<AnalyzedProperty, TestResult> getResultMap() {
        return Collections.unmodifiableMap(resultMap);
    }

    public synchronized TestResult getResult(AnalyzedProperty property) {
        return resultMap.getOrDefault(property, TestResults.NOT_TESTED_YET);
    }

    public synchronized ObjectResult<?> getObjectResult(AnalyzedProperty property) {
        TestResult result = resultMap.get(property);
        return result instanceof ObjectResult ? (ObjectResult<?>) result : null;
    }

    public synchronized <T> ObjectResult<T> getObjectResult(
            AnalyzedProperty property, Class<T> valueClass) {
        ObjectResult<?> result = getObjectResult(property);
        return result != null
                ? new ObjectResult<>(result.getProperty(), valueClass.cast(result.getValue()))
                : null;
    }

    public synchronized BigIntegerResult getBigIntegerResult(AnalyzedProperty property) {
        TestResult result = resultMap.get(property);
        return result instanceof BigIntegerResult ? (BigIntegerResult) result : null;
    }

    public synchronized IntegerResult getIntegerResult(AnalyzedProperty property) {
        TestResult result = resultMap.get(property);
        return result instanceof IntegerResult ? (IntegerResult) result : null;
    }

    public synchronized LongResult getLongResult(AnalyzedProperty property) {
        TestResult result = resultMap.get(property);
        return result instanceof LongResult ? (LongResult) result : null;
    }

    public synchronized StringResult getStringResult(AnalyzedProperty property) {
        TestResult result = resultMap.get(property);
        return result instanceof StringResult ? (StringResult) result : null;
    }

    public synchronized CollectionResult<?> getCollectionResult(AnalyzedProperty property) {
        TestResult result = resultMap.get(property);
        return result instanceof CollectionResult ? (CollectionResult<?>) result : null;
    }

    public synchronized <V> CollectionResult<V> getCollectionResult(
            AnalyzedProperty property, Class<V> valueClass) {
        CollectionResult<?> result = getCollectionResult(property);
        return result != null
                ? new ListResult<>(
                        result.getProperty(),
                        result.getCollection().stream()
                                .map(valueClass::cast)
                                .collect(Collectors.toUnmodifiableList()))
                : null;
    }

    public synchronized ListResult<?> getListResult(AnalyzedProperty property) {
        TestResult result = resultMap.get(property);
        return result instanceof ListResult ? (ListResult<?>) result : null;
    }

    public synchronized <V> ListResult<V> getListResult(
            AnalyzedProperty property, Class<V> valueClass) {
        ListResult<?> result = getListResult(property);
        return result != null
                ? new ListResult<>(
                        result.getProperty(),
                        result.getList().stream()
                                .map(valueClass::cast)
                                .collect(Collectors.toUnmodifiableList()))
                : null;
    }

    public synchronized MapResult<?, ?> getMapResult(AnalyzedProperty property) {
        TestResult result = resultMap.get(property);
        return result instanceof MapResult ? (MapResult<?, ?>) result : null;
    }

    public synchronized <V> MapResult<?, V> getMapResult(
            AnalyzedProperty property, Class<V> valueClass) {
        return getMapResult(property, Object.class, valueClass);
    }

    public synchronized <K, V> MapResult<K, V> getMapResult(
            AnalyzedProperty property, Class<K> keyClass, Class<V> valueClass) {
        MapResult<?, ?> result = getMapResult(property);
        if (result == null) {
            return null;
        }
        Map<K, V> typedMap = new HashMap<>();
        result.getMap()
                .forEach((key, value) -> typedMap.put(keyClass.cast(key), valueClass.cast(value)));
        return new MapResult<>(result.getProperty(), Collections.unmodifiableMap(typedMap));
    }

    public synchronized SetResult<?> getSetResult(AnalyzedProperty property) {
        TestResult result = resultMap.get(property);
        return result instanceof SetResult ? (SetResult<?>) result : null;
    }

    public synchronized <V> SetResult<V> getSetResult(
            AnalyzedProperty property, Class<V> valueClass) {
        SetResult<?> result = getSetResult(property);
        return result != null
                ? new SetResult<>(
                        result.getProperty(),
                        result.getSet().stream()
                                .map(valueClass::cast)
                                .collect(Collectors.toUnmodifiableSet()))
                : null;
    }

    public synchronized void putResult(AnalyzedProperty property, TestResult result) {
        resultMap.put(property, result);
    }

    public synchronized void putResult(AnalyzedProperty property, Boolean result) {
        this.putResult(
                property,
                Objects.equals(result, Boolean.TRUE)
                        ? TestResults.TRUE
                        : Objects.equals(result, Boolean.FALSE)
                                ? TestResults.FALSE
                                : TestResults.UNCERTAIN);
    }

    public synchronized void putResult(AnalyzedProperty property, BigInteger result) {
        this.putResult(property, new BigIntegerResult(property, result));
    }

    public synchronized void putResult(AnalyzedProperty property, Integer result) {
        this.putResult(property, new IntegerResult(property, result));
    }

    public synchronized void putResult(AnalyzedProperty property, Long result) {
        this.putResult(property, new LongResult(property, result));
    }

    public synchronized void putResult(AnalyzedProperty property, String result) {
        this.putResult(property, new StringResult(property, result));
    }

    public synchronized void putResult(AnalyzedProperty property, List<?> result) {
        this.putResult(property, new ListResult<>(property, result));
    }

    public synchronized void putResult(AnalyzedProperty property, Set<?> result) {
        this.putResult(property, new SetResult<>(property, result));
    }

    public synchronized void putResult(AnalyzedProperty property, Map<?, ?> result) {
        this.putResult(property, new MapResult<>(property, result));
    }

    public synchronized <T> void putResult(AnalyzedProperty property, T result) {
        this.putResult(property, new ObjectResult<>(property, result));
    }

    public synchronized void removeResult(AnalyzedProperty property) {
        resultMap.remove(property);
    }

    public synchronized Map<TrackableValue, ExtractedValueContainer<?>>
            getExtractedValueContainerMap() {
        return Collections.unmodifiableMap(extractedValueContainerMap);
    }

    public synchronized ExtractedValueContainer<?> getExtractedValueContainer(
            TrackableValue trackableValue) {
        return extractedValueContainerMap.get(trackableValue);
    }

    public synchronized <T> ExtractedValueContainer<T> getExtractedValueContainer(
            TrackableValue trackableValue, Class<T> valueClass) {
        //noinspection unchecked
        return (ExtractedValueContainer<T>) extractedValueContainerMap.get(trackableValue);
    }

    public synchronized void putExtractedValueContainer(
            TrackableValue trackableValue, ExtractedValueContainer<?> extractedValueContainer) {
        extractedValueContainerMap.put(trackableValue, extractedValueContainer);
    }

    public synchronized void putAllExtractedValueContainers(
            Map<TrackableValue, ExtractedValueContainer<?>> extractedValueContainerMap) {
        this.extractedValueContainerMap.putAll(extractedValueContainerMap);
    }

    public synchronized List<GuidelineReport> getGuidelineReports() {
        return Collections.unmodifiableList(guidelineReports);
    }

    public synchronized void addGuidelineReport(GuidelineReport guidelineReport) {
        guidelineReports.add(guidelineReport);
    }

    public synchronized int getScore() {
        return score;
    }

    public synchronized void setScore(int score) {
        this.score = score;
    }

    public synchronized ScoreReport getScoreReport() {
        return scoreReport;
    }

    public synchronized void setScoreReport(ScoreReport scoreReport) {
        this.scoreReport = scoreReport;
    }

    public synchronized boolean isProbeAlreadyExecuted(ProbeType type) {
        return executedProbes.contains(type);
    }

    public synchronized void markProbeAsExecuted(ProbeType probe) {
        executedProbes.add(probe);
    }

    public synchronized void markProbeAsUnexecuted(ProbeType probe) {
        unexecutedProbes.add(probe);
    }

    public synchronized List<PerformanceData> getProbePerformanceData() {
        return Collections.unmodifiableList(probePerformanceData);
    }

    public synchronized void recordProbePerformance(PerformanceData performanceData) {
        probePerformanceData.add(performanceData);
    }

    public synchronized Set<ProbeType> getExecutedProbes() {
        return Collections.unmodifiableSet(executedProbes);
    }

    public synchronized Set<ProbeType> getUnexecutedProbes() {
        return Collections.unmodifiableSet(unexecutedProbes);
    }

    public int getPerformedConnections() {
        return performedConnections;
    }

    public void setPerformedConnections(int performedConnections) {
        this.performedConnections = performedConnections;
    }

    public long getScanStartTime() {
        return scanStartTime;
    }

    public void setScanStartTime(long scanStartTime) {
        this.scanStartTime = scanStartTime;
    }

    public long getScanEndTime() {
        return scanEndTime;
    }

    public void setScanEndTime(long scanStopTime) {
        this.scanEndTime = scanStopTime;
    }
}
