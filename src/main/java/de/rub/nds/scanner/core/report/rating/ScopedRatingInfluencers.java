/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.report.rating;

import de.rub.nds.scanner.core.probe.AnalyzedProperty;
import de.rub.nds.scanner.core.probe.result.TestResult;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A collection of scoped rating influencers from which the rating influencers of individual scanner
 * variants can be assembled using {@link RatingScopeSelection}s. A property may have multiple
 * influencers, e.g., one per score, which are merged into a single {@link RatingInfluencer} upon
 * selection.
 *
 * @param <S> the enum type defining the available scopes
 */
public class ScopedRatingInfluencers<S extends Enum<S>> {

    private final List<ScopedRatingInfluencer<S>> influencers = new LinkedList<>();

    /**
     * Adds a scoped rating influencer.
     *
     * @param influencer the influencer to add
     */
    public void add(ScopedRatingInfluencer<S> influencer) {
        influencers.add(influencer);
    }

    /**
     * Adds all given scoped rating influencers.
     *
     * @param influencers the influencers to add
     */
    public void addAll(Collection<ScopedRatingInfluencer<S>> influencers) {
        this.influencers.addAll(influencers);
    }

    /**
     * Returns all scoped rating influencers.
     *
     * @return the scoped rating influencers in insertion order
     */
    public List<ScopedRatingInfluencer<S>> getInfluencers() {
        return influencers;
    }

    /**
     * Assembles the rating influencers selected by the given selection. Selected influencers of the
     * same property are merged by combining the score modifiers of identical results.
     *
     * @param selection the selection to apply
     * @return the selected rating influencers in insertion order of their properties
     * @throws IllegalStateException if the merged influencers define multiple modifiers for the
     *     same result and score, or different references for the same result
     */
    public RatingInfluencers select(RatingScopeSelection<S> selection) {
        Map<AnalyzedProperty, List<RatingInfluencer>> influencersByProperty =
                influencers.stream()
                        .filter(selection::selects)
                        .map(ScopedRatingInfluencer::getInfluencer)
                        .collect(
                                Collectors.groupingBy(
                                        RatingInfluencer::getAnalyzedProperty,
                                        LinkedHashMap::new,
                                        Collectors.toList()));
        return new RatingInfluencers(
                influencersByProperty.entrySet().stream()
                        .map(entry -> merge(entry.getKey(), entry.getValue()))
                        .collect(Collectors.toCollection(LinkedList::new)));
    }

    private static RatingInfluencer merge(
            AnalyzedProperty property, List<RatingInfluencer> influencers) {
        Map<TestResult, PropertyResultRatingInfluencer> mergedByResult = new LinkedHashMap<>();
        for (RatingInfluencer influencer : influencers) {
            for (PropertyResultRatingInfluencer resultInfluencer :
                    influencer.getPropertyRatingInfluencers()) {
                PropertyResultRatingInfluencer merged =
                        mergedByResult.computeIfAbsent(
                                resultInfluencer.getResult(), PropertyResultRatingInfluencer::new);
                for (ScoreModifier modifier : resultInfluencer.getModifiers()) {
                    if (merged.getModifier(modifier.getScoreType()).isPresent()) {
                        throw new IllegalStateException(
                                "Multiple modifiers of score "
                                        + modifier.getScoreType()
                                        + " for result "
                                        + resultInfluencer.getResult()
                                        + " of "
                                        + property);
                    }
                    merged.addModifier(modifier);
                }
                if (resultInfluencer.getReferencedProperty() != null) {
                    if (merged.getReferencedProperty() != null) {
                        throw new IllegalStateException(
                                "Multiple references for result "
                                        + resultInfluencer.getResult()
                                        + " of "
                                        + property);
                    }
                    merged.setReferencedProperty(resultInfluencer.getReferencedProperty());
                    merged.setReferencedPropertyResult(
                            resultInfluencer.getReferencedPropertyResult());
                }
            }
        }
        return new RatingInfluencer(property, new LinkedList<>(mergedByResult.values()));
    }

    /**
     * Returns all expected properties for which no influencer exists.
     *
     * @param expectedProperties the properties that require an influencer
     * @return the properties without influencer
     */
    public List<AnalyzedProperty> getMissingProperties(
            Collection<? extends AnalyzedProperty> expectedProperties) {
        Set<AnalyzedProperty> present =
                influencers.stream()
                        .map(ScopedRatingInfluencer::getAnalyzedProperty)
                        .collect(Collectors.toSet());
        return expectedProperties.stream()
                .filter(property -> !present.contains(property))
                .collect(Collectors.toList());
    }

    /**
     * Returns all properties that have multiple influencers with differing scopes.
     *
     * @return the properties with inconsistent scopes
     */
    public List<AnalyzedProperty> getPropertiesWithInconsistentScopes() {
        Map<AnalyzedProperty, Set<Set<S>>> scopesByProperty =
                influencers.stream()
                        .collect(
                                Collectors.groupingBy(
                                        ScopedRatingInfluencer::getAnalyzedProperty,
                                        LinkedHashMap::new,
                                        Collectors.mapping(
                                                ScopedRatingInfluencer::getScopes,
                                                Collectors.toSet())));
        return scopesByProperty.entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Returns all properties for which multiple modifiers of the same score are defined for the
     * same result, regardless of their scopes.
     *
     * @return the properties with duplicate modifiers
     */
    public List<AnalyzedProperty> getPropertiesWithDuplicateModifiers() {
        Map<AnalyzedProperty, Set<Map.Entry<TestResult, ScoreType>>> modifiersByProperty =
                new LinkedHashMap<>();
        Set<AnalyzedProperty> duplicates = new LinkedHashSet<>();
        for (ScopedRatingInfluencer<S> influencer : influencers) {
            Set<Map.Entry<TestResult, ScoreType>> seen =
                    modifiersByProperty.computeIfAbsent(
                            influencer.getAnalyzedProperty(), property -> new HashSet<>());
            for (PropertyResultRatingInfluencer resultInfluencer :
                    influencer.getInfluencer().getPropertyRatingInfluencers()) {
                for (ScoreModifier modifier : resultInfluencer.getModifiers()) {
                    if (!seen.add(
                            Map.entry(resultInfluencer.getResult(), modifier.getScoreType()))) {
                        duplicates.add(influencer.getAnalyzedProperty());
                    }
                }
            }
        }
        return new LinkedList<>(duplicates);
    }
}
