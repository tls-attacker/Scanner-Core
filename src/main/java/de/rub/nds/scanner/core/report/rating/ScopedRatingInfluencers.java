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
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A collection of scoped rating influencers from which the rating influencers of individual scanner
 * variants can be assembled using {@link RatingScopeSelection}s.
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
     * Assembles the rating influencers selected by the given selection.
     *
     * @param selection the selection to apply
     * @return the selected rating influencers in insertion order
     */
    public RatingInfluencers select(RatingScopeSelection<S> selection) {
        return new RatingInfluencers(
                influencers.stream()
                        .filter(selection::selects)
                        .map(ScopedRatingInfluencer::getInfluencer)
                        .collect(Collectors.toCollection(LinkedList::new)));
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
}
