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
import de.rub.nds.scanner.core.probe.result.TestResults;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * A rating influencer annotated with the scopes it is applicable to. Scopes are defined by the
 * concrete scanner, e.g., to distinguish between protocol variants, and allow to assemble dedicated
 * rating influencers for each variant using a {@link RatingScopeSelection}.
 *
 * @param <S> the enum type defining the available scopes
 */
public class ScopedRatingInfluencer<S extends Enum<S>> {

    private final Set<S> scopes;

    private final RatingInfluencer influencer;

    /**
     * Creates a rating influencer that is applicable to a single scope.
     *
     * @param scope the scope the influencer is applicable to
     * @param property the analyzed property the influencer rates
     * @param propertyRatingInfluencers the rating influences of the property results
     */
    public ScopedRatingInfluencer(
            S scope,
            AnalyzedProperty property,
            PropertyResultRatingInfluencer... propertyRatingInfluencers) {
        this(EnumSet.of(scope), property, propertyRatingInfluencers);
    }

    /**
     * Creates a rating influencer that is applicable to multiple scopes.
     *
     * @param scopes the scopes the influencer is applicable to, must not be empty
     * @param property the analyzed property the influencer rates
     * @param propertyRatingInfluencers the rating influences of the property results
     * @throws IllegalArgumentException if no scope is given
     */
    public ScopedRatingInfluencer(
            Set<S> scopes,
            AnalyzedProperty property,
            PropertyResultRatingInfluencer... propertyRatingInfluencers) {
        if (scopes.isEmpty()) {
            throw new IllegalArgumentException("No rating scope set for " + property);
        }
        this.scopes = EnumSet.copyOf(scopes);
        this.influencer = new RatingInfluencer(property, propertyRatingInfluencers);
    }

    /**
     * Creates influencers with the same rating influences for all given properties.
     *
     * @param <S> the enum type defining the available scopes
     * @param scope the scope the influencers are applicable to
     * @param properties the analyzed properties
     * @param propertyRatingInfluencers the rating influences of the property results
     * @return a list containing one influencer per property
     */
    public static <S extends Enum<S>> List<ScopedRatingInfluencer<S>> forProperties(
            S scope,
            List<? extends AnalyzedProperty> properties,
            PropertyResultRatingInfluencer... propertyRatingInfluencers) {
        return forProperties(EnumSet.of(scope), properties, propertyRatingInfluencers);
    }

    /**
     * Creates influencers with the same rating influences for all given properties.
     *
     * @param <S> the enum type defining the available scopes
     * @param scopes the scopes the influencers are applicable to, must not be empty
     * @param properties the analyzed properties
     * @param propertyRatingInfluencers the rating influences of the property results
     * @return a list containing one influencer per property
     */
    public static <S extends Enum<S>> List<ScopedRatingInfluencer<S>> forProperties(
            Set<S> scopes,
            List<? extends AnalyzedProperty> properties,
            PropertyResultRatingInfluencer... propertyRatingInfluencers) {
        return properties.stream()
                .map(
                        property ->
                                new ScopedRatingInfluencer<>(
                                        scopes, property, propertyRatingInfluencers.clone()))
                .toList();
    }

    /**
     * Creates influencers for boolean properties whose results have impacts of the same level but
     * opposite signs. The impact of {@link TestResults#FALSE} is the opposite of the given impact.
     *
     * @param <S> the enum type defining the available scopes
     * @param scope the scope the influencers are applicable to
     * @param impactIfTrue the impact if the property is {@link TestResults#TRUE}
     * @param properties the analyzed properties
     * @return a list containing one influencer per property
     */
    public static <S extends Enum<S>> List<ScopedRatingInfluencer<S>> symmetric(
            S scope, RatingImpact impactIfTrue, AnalyzedProperty... properties) {
        return symmetric(EnumSet.of(scope), impactIfTrue, properties);
    }

    /**
     * Creates influencers for boolean properties whose results have impacts of the same level but
     * opposite signs. The impact of {@link TestResults#FALSE} is the opposite of the given impact.
     *
     * @param <S> the enum type defining the available scopes
     * @param scopes the scopes the influencers are applicable to, must not be empty
     * @param impactIfTrue the impact if the property is {@link TestResults#TRUE}
     * @param properties the analyzed properties
     * @return a list containing one influencer per property
     */
    public static <S extends Enum<S>> List<ScopedRatingInfluencer<S>> symmetric(
            Set<S> scopes, RatingImpact impactIfTrue, AnalyzedProperty... properties) {
        return forProperties(
                scopes,
                Arrays.asList(properties),
                new PropertyResultRatingInfluencer(TestResults.TRUE, impactIfTrue),
                new PropertyResultRatingInfluencer(TestResults.FALSE, impactIfTrue.getOpposite()));
    }

    /**
     * Creates influencers without impact on the rating for all given properties.
     *
     * @param <S> the enum type defining the available scopes
     * @param scope the scope the influencers are applicable to
     * @param properties the analyzed properties
     * @return a list containing one neutral influencer per property
     */
    public static <S extends Enum<S>> List<ScopedRatingInfluencer<S>> neutral(
            S scope, AnalyzedProperty... properties) {
        return forProperties(scope, Arrays.asList(properties));
    }

    /**
     * Creates influencers without impact on the rating for all given properties.
     *
     * @param <S> the enum type defining the available scopes
     * @param scopes the scopes the influencers are applicable to, must not be empty
     * @param properties the analyzed properties
     * @return a list containing one neutral influencer per property
     */
    public static <S extends Enum<S>> List<ScopedRatingInfluencer<S>> neutral(
            Set<S> scopes, AnalyzedProperty... properties) {
        return forProperties(scopes, Arrays.asList(properties));
    }

    /**
     * Returns the scopes this influencer is applicable to.
     *
     * @return the scopes of this influencer
     */
    public Set<S> getScopes() {
        return scopes;
    }

    /**
     * Returns the wrapped rating influencer.
     *
     * @return the rating influencer
     */
    public RatingInfluencer getInfluencer() {
        return influencer;
    }

    /**
     * Returns the analyzed property rated by this influencer.
     *
     * @return the analyzed property
     */
    public AnalyzedProperty getAnalyzedProperty() {
        return influencer.getAnalyzedProperty();
    }
}
