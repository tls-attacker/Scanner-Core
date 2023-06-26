/*
 * Scanner Core - A modular framework for probe definition, execution, and result analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.report;

import de.rub.nds.scanner.core.constants.AnalyzedProperty;
import de.rub.nds.scanner.core.constants.CollectionResult;
import de.rub.nds.scanner.core.constants.ListResult;
import de.rub.nds.scanner.core.constants.MapResult;
import de.rub.nds.scanner.core.constants.ProbeType;
import de.rub.nds.scanner.core.constants.ScannerDetail;
import de.rub.nds.scanner.core.constants.SetResult;
import de.rub.nds.scanner.core.constants.TestResult;
import de.rub.nds.scanner.core.constants.TestResults;
import de.rub.nds.scanner.core.guideline.GuidelineReport;
import de.rub.nds.scanner.core.passive.ExtractedValueContainer;
import de.rub.nds.scanner.core.passive.TrackableValue;
import de.rub.nds.scanner.core.probe.ScannerProbe;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public abstract class ScanReport<R extends ScanReport<R>> extends Observable
        implements Serializable {

    private final HashMap<String, TestResult> resultMap;

    private List<GuidelineReport> guidelineReports;

    private final Set<ProbeType> executedProbes;
    private final Set<ScannerProbe<R, ?>> unexecutedProbes;

    private final List<PerformanceData> performanceList;

    private Map<TrackableValue, ExtractedValueContainer<?>> extractedValueContainerMap;

    private int performedTcpConnections = 0;

    public ScanReport() {
        performanceList = new LinkedList<>();
        resultMap = new HashMap<>();
        executedProbes = new HashSet<>();
        unexecutedProbes = new HashSet<>();
        extractedValueContainerMap = new HashMap<>();
    }

    public synchronized int getPerformedTcpConnections() {
        return performedTcpConnections;
    }

    public synchronized void setPerformedTcpConnections(int performedTcpConnections) {
        this.performedTcpConnections = performedTcpConnections;
    }

    public synchronized Map<TrackableValue, ExtractedValueContainer<?>>
            getExtractedValueContainerMap() {
        return extractedValueContainerMap;
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

    public synchronized void setExtractedValueContainerMap(
            Map<TrackableValue, ExtractedValueContainer<?>> extractedValueContainerMap) {
        this.extractedValueContainerMap = extractedValueContainerMap;
    }

    public synchronized HashMap<String, TestResult> getResultMap() {
        return resultMap;
    }

    public synchronized TestResult getResult(AnalyzedProperty property) {
        return getResult(property.toString());
    }

    public synchronized TestResult getResult(String property) {
        TestResult result = resultMap.get(property);
        return (result == null) ? TestResults.NOT_TESTED_YET : result;
    }

    public synchronized CollectionResult<?> getCollectionResult(AnalyzedProperty property) {
        return getCollectionResult(property.getName());
    }

    public synchronized CollectionResult<?> getCollectionResult(String property) {
        TestResult result = resultMap.get(property);
        return result instanceof CollectionResult ? (CollectionResult<?>) result : null;
    }

    public synchronized <T> CollectionResult<T> getCollectionResult(
            AnalyzedProperty property, Class<T> valueClass) {
        return getCollectionResult(property.getName(), valueClass);
    }

    public synchronized <T> CollectionResult<T> getCollectionResult(
            String property, Class<T> valueClass) {
        CollectionResult<?> result = getCollectionResult(property);
        return result != null
                ? new ListResult<>(
                        result.getCollection().stream()
                                .map(valueClass::cast)
                                .collect(Collectors.toUnmodifiableList()),
                        result.name())
                : null;
    }

    public synchronized ListResult<?> getListResult(AnalyzedProperty property) {
        return getListResult(property.getName());
    }

    public synchronized ListResult<?> getListResult(String property) {
        TestResult result = resultMap.get(property);
        return result instanceof ListResult ? (ListResult<?>) result : null;
    }

    public synchronized <T> ListResult<T> getListResult(
            AnalyzedProperty property, Class<T> valueClass) {
        return getListResult(property.getName(), valueClass);
    }

    public synchronized <T> ListResult<T> getListResult(String property, Class<T> valueClass) {
        ListResult<?> result = getListResult(property);
        return result != null
                ? new ListResult<>(
                        result.getList().stream()
                                .map(valueClass::cast)
                                .collect(Collectors.toUnmodifiableList()),
                        result.name())
                : null;
    }

    public synchronized MapResult<?, ?> getMapResult(AnalyzedProperty property) {
        return getMapResult(property.getName());
    }

    public synchronized MapResult<?, ?> getMapResult(String property) {
        TestResult result = resultMap.get(property);
        return result instanceof MapResult ? (MapResult<?, ?>) result : null;
    }

    public synchronized <T> MapResult<?, T> getMapResult(
            AnalyzedProperty property, Class<T> valueClass) {
        return getMapResult(property.getName(), Object.class, valueClass);
    }

    public synchronized <T> MapResult<?, T> getMapResult(String property, Class<T> valueClass) {
        return getMapResult(property, Object.class, valueClass);
    }

    public synchronized <S, T> MapResult<S, T> getMapResult(
            AnalyzedProperty property, Class<S> keyClass, Class<T> valueClass) {
        return getMapResult(property.getName(), keyClass, valueClass);
    }

    public synchronized <S, T> MapResult<S, T> getMapResult(
            String property, Class<S> keyClass, Class<T> valueClass) {
        MapResult<?, ?> result = getMapResult(property);
        if (result == null) {
            return null;
        }
        Map<S, T> typedMap = new HashMap<>();
        result.getMap()
                .forEach(
                        (key, value) -> {
                            typedMap.put(keyClass.cast(key), valueClass.cast(value));
                        });
        return new MapResult<>(Collections.unmodifiableMap(typedMap), result.name());
    }

    public synchronized SetResult<?> getSetResult(AnalyzedProperty property) {
        return getSetResult(property.getName());
    }

    public synchronized SetResult<?> getSetResult(String property) {
        TestResult result = resultMap.get(property);
        return result instanceof SetResult ? (SetResult<?>) result : null;
    }

    public synchronized <T> SetResult<T> getSetResult(
            AnalyzedProperty property, Class<T> valueClass) {
        return getSetResult(property.getName(), valueClass);
    }

    public synchronized <T> SetResult<T> getSetResult(String property, Class<T> valueClass) {
        SetResult<?> result = getSetResult(property);
        return result != null
                ? new SetResult<>(
                        result.getSet().stream()
                                .map(valueClass::cast)
                                .collect(Collectors.toUnmodifiableSet()),
                        result.name())
                : null;
    }

    public synchronized List<GuidelineReport> getGuidelineReports() {
        return guidelineReports;
    }

    public synchronized void setGuidelineReports(List<GuidelineReport> guidelineReports) {
        this.guidelineReports = guidelineReports;
    }

    public synchronized void removeResult(AnalyzedProperty property) {
        resultMap.remove(property.toString());
    }

    public synchronized void putResult(AnalyzedProperty property, TestResult result) {
        resultMap.put(property.toString(), result);
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

    public synchronized void putResult(AnalyzedProperty property, List<?> result) {
        this.putResult(property, new ListResult<>((List<?>) result, property.getName()));
    }

    public synchronized void putResult(AnalyzedProperty property, Set<?> result) {
        this.putResult(property, new SetResult<>((Set<?>) result, property.getName()));
    }

    public synchronized void putResult(AnalyzedProperty property, Map<?, ?> result) {
        this.putResult(property, new MapResult<>((Map<?, ?>) result, property.getName()));
    }

    public synchronized void markAsChangedAndNotify() {
        this.hasChanged();
        this.notifyObservers();
    }

    public synchronized boolean isProbeAlreadyExecuted(ProbeType type) {
        return executedProbes.stream().collect(Collectors.toSet()).contains(type);
    }

    public synchronized void markProbeAsExecuted(ProbeType probe) {
        executedProbes.add(probe);
    }

    public synchronized void markProbeAsUnexecuted(ScannerProbe<R, ?> probe) {
        unexecutedProbes.add(probe);
    }

    public synchronized List<PerformanceData> getPerformanceList() {
        return performanceList;
    }

    public synchronized Set<ProbeType> getUnexecutesProbeTypes() {
        return unexecutedProbes.stream().map(probe -> probe.getType()).collect(Collectors.toSet());
    }

    public synchronized Set<ProbeType> getExecutedProbes() {
        return executedProbes;
    }

    public synchronized Set<ScannerProbe<R, ?>> getUnexecutedProbes() {
        return unexecutedProbes;
    }

    public abstract String getFullReport(ScannerDetail detail, boolean printColorful);
}
