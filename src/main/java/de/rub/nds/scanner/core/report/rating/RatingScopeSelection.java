/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.report.rating;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Selects scoped rating influencers based on their scopes. An influencer is selected if at least
 * one of its scopes is included and none of its scopes is excluded. Scopes that are neither
 * included nor excluded do not affect the selection.
 *
 * @param <S> the enum type defining the available scopes
 */
public class RatingScopeSelection<S extends Enum<S>> {

    private final Set<S> include;

    private final Set<S> exclude;

    /**
     * Creates a new selection.
     *
     * @param include the scopes to include, must not be empty
     * @param exclude the scopes to exclude, must not overlap with the included scopes
     * @throws IllegalArgumentException if no scope is included or a scope is both included and
     *     excluded
     */
    public RatingScopeSelection(Set<S> include, Set<S> exclude) {
        if (include.isEmpty()) {
            throw new IllegalArgumentException("At least one scope must be included");
        }
        if (!Collections.disjoint(include, exclude)) {
            throw new IllegalArgumentException(
                    "Scopes must not be included and excluded at the same time");
        }
        this.include = Set.copyOf(include);
        this.exclude = Set.copyOf(exclude);
    }

    /**
     * Checks whether the given scopes are selected.
     *
     * @param scopes the scopes to check
     * @return true if at least one scope is included and no scope is excluded
     */
    public boolean selects(Set<S> scopes) {
        return !Collections.disjoint(include, scopes) && Collections.disjoint(exclude, scopes);
    }

    /**
     * Checks whether the given influencer is selected.
     *
     * @param influencer the influencer to check
     * @return true if at least one scope of the influencer is included and none is excluded
     */
    public boolean selects(ScopedRatingInfluencer<S> influencer) {
        return selects(influencer.getScopes());
    }

    /**
     * Returns all scopes that are neither included nor excluded by this selection.
     *
     * @param scopeClass the enum type defining the available scopes
     * @return the scopes that do not affect the selection
     */
    public Set<S> getUnassignedScopes(Class<S> scopeClass) {
        Set<S> unassigned = EnumSet.allOf(scopeClass);
        unassigned.removeAll(include);
        unassigned.removeAll(exclude);
        return unassigned;
    }

    /**
     * Returns the included scopes.
     *
     * @return an unmodifiable set of the included scopes
     */
    public Set<S> getInclude() {
        return include;
    }

    /**
     * Returns the excluded scopes.
     *
     * @return an unmodifiable set of the excluded scopes
     */
    public Set<S> getExclude() {
        return exclude;
    }
}
