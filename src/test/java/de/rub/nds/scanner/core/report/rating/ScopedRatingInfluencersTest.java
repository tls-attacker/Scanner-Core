/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.report.rating;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.rub.nds.scanner.core.probe.AnalyzedProperty;
import de.rub.nds.scanner.core.probe.AnalyzedPropertyCategory;
import de.rub.nds.scanner.core.probe.result.TestResults;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class ScopedRatingInfluencersTest {

    private enum Scope {
        ANY,
        NEW_VERSION,
        OLD_VERSION,
        VARIANT_A,
        VARIANT_B
    }

    private enum Property implements AnalyzedProperty {
        ANY_PROPERTY,
        NEW_VERSION_PROPERTY,
        OLD_VERSION_PROPERTY,
        NEW_VERSION_A_PROPERTY,
        B_PROPERTY;

        @Override
        public AnalyzedPropertyCategory getCategory() {
            return null;
        }

        @Override
        public String getName() {
            return name();
        }
    }

    private static final RatingScopeSelection<Scope> VARIANT_A =
            new RatingScopeSelection<>(
                    EnumSet.of(Scope.ANY, Scope.NEW_VERSION, Scope.OLD_VERSION, Scope.VARIANT_A),
                    EnumSet.of(Scope.VARIANT_B));

    private static final RatingScopeSelection<Scope> VARIANT_B =
            new RatingScopeSelection<>(
                    EnumSet.of(Scope.ANY, Scope.NEW_VERSION, Scope.VARIANT_B),
                    EnumSet.of(Scope.OLD_VERSION, Scope.VARIANT_A));

    private static ScopedRatingInfluencers<Scope> createInfluencers() {
        ScopedRatingInfluencers<Scope> influencers = new ScopedRatingInfluencers<>();
        influencers.add(
                new ScopedRatingInfluencer<>(
                        Scope.ANY,
                        Property.ANY_PROPERTY,
                        new PropertyResultRatingInfluencer(TestResults.TRUE, 100)));
        influencers.add(
                new ScopedRatingInfluencer<>(
                        Scope.NEW_VERSION,
                        Property.NEW_VERSION_PROPERTY,
                        new PropertyResultRatingInfluencer(TestResults.TRUE, 100)));
        influencers.add(
                new ScopedRatingInfluencer<>(
                        Scope.OLD_VERSION,
                        Property.OLD_VERSION_PROPERTY,
                        new PropertyResultRatingInfluencer(TestResults.FALSE, 50)));
        influencers.add(
                new ScopedRatingInfluencer<>(
                        EnumSet.of(Scope.NEW_VERSION, Scope.VARIANT_A),
                        Property.NEW_VERSION_A_PROPERTY,
                        new PropertyResultRatingInfluencer(TestResults.TRUE, 100)));
        influencers.addAll(ScopedRatingInfluencer.neutral(Scope.VARIANT_B, Property.B_PROPERTY));
        return influencers;
    }

    private static List<AnalyzedProperty> selectedProperties(
            ScopedRatingInfluencers<Scope> influencers, RatingScopeSelection<Scope> selection) {
        return influencers.select(selection).getRatingInfluencers().stream()
                .map(RatingInfluencer::getAnalyzedProperty)
                .toList();
    }

    @Test
    public void testSelect() {
        ScopedRatingInfluencers<Scope> influencers = createInfluencers();

        assertEquals(
                List.of(
                        Property.ANY_PROPERTY,
                        Property.NEW_VERSION_PROPERTY,
                        Property.OLD_VERSION_PROPERTY,
                        Property.NEW_VERSION_A_PROPERTY),
                selectedProperties(influencers, VARIANT_A));
        assertEquals(
                List.of(Property.ANY_PROPERTY, Property.NEW_VERSION_PROPERTY, Property.B_PROPERTY),
                selectedProperties(influencers, VARIANT_B));
    }

    @Test
    public void testSelectsIgnoresUnassignedScopes() {
        RatingScopeSelection<Scope> selection =
                new RatingScopeSelection<>(
                        EnumSet.of(Scope.NEW_VERSION), EnumSet.of(Scope.OLD_VERSION));

        assertTrue(selection.selects(EnumSet.of(Scope.NEW_VERSION, Scope.VARIANT_A)));
        assertFalse(selection.selects(EnumSet.of(Scope.NEW_VERSION, Scope.OLD_VERSION)));
        assertFalse(selection.selects(EnumSet.of(Scope.VARIANT_A)));
        assertEquals(
                EnumSet.of(Scope.ANY, Scope.VARIANT_A, Scope.VARIANT_B),
                selection.getUnassignedScopes(Scope.class));
        assertTrue(VARIANT_A.getUnassignedScopes(Scope.class).isEmpty());
    }

    @Test
    public void testInvalidSelection() {
        Set<Scope> none = EnumSet.noneOf(Scope.class);
        Set<Scope> any = EnumSet.of(Scope.ANY);
        assertThrows(IllegalArgumentException.class, () -> new RatingScopeSelection<>(none, any));
        assertThrows(IllegalArgumentException.class, () -> new RatingScopeSelection<>(any, any));
    }

    @Test
    public void testInfluencerRequiresScope() {
        Set<Scope> none = EnumSet.noneOf(Scope.class);
        assertThrows(
                IllegalArgumentException.class,
                () -> new ScopedRatingInfluencer<>(none, Property.ANY_PROPERTY));
    }

    @Test
    public void testGetMissingProperties() {
        ScopedRatingInfluencers<Scope> influencers = new ScopedRatingInfluencers<>();
        influencers.addAll(
                ScopedRatingInfluencer.neutral(
                        Scope.ANY, Property.ANY_PROPERTY, Property.B_PROPERTY));

        assertEquals(
                List.of(
                        Property.NEW_VERSION_PROPERTY,
                        Property.OLD_VERSION_PROPERTY,
                        Property.NEW_VERSION_A_PROPERTY),
                influencers.getMissingProperties(List.of(Property.values())));
        assertTrue(createInfluencers().getMissingProperties(List.of(Property.values())).isEmpty());
    }

    @Test
    public void testGetPropertiesWithInconsistentScopes() {
        ScopedRatingInfluencers<Scope> influencers = createInfluencers();
        assertTrue(influencers.getPropertiesWithInconsistentScopes().isEmpty());

        influencers.add(new ScopedRatingInfluencer<>(Scope.ANY, Property.ANY_PROPERTY));
        assertTrue(influencers.getPropertiesWithInconsistentScopes().isEmpty());

        influencers.add(new ScopedRatingInfluencer<>(Scope.OLD_VERSION, Property.ANY_PROPERTY));
        assertEquals(
                List.of(Property.ANY_PROPERTY), influencers.getPropertiesWithInconsistentScopes());
    }

    @Test
    public void testForProperties() {
        List<ScopedRatingInfluencer<Scope>> influencers =
                ScopedRatingInfluencer.forProperties(
                        EnumSet.of(Scope.NEW_VERSION, Scope.VARIANT_A),
                        List.of(Property.NEW_VERSION_PROPERTY, Property.NEW_VERSION_A_PROPERTY),
                        new PropertyResultRatingInfluencer(TestResults.TRUE, -100, 500),
                        new PropertyResultRatingInfluencer(TestResults.FALSE, 50));

        assertEquals(2, influencers.size());
        assertEquals(Property.NEW_VERSION_PROPERTY, influencers.get(0).getAnalyzedProperty());
        assertEquals(Property.NEW_VERSION_A_PROPERTY, influencers.get(1).getAnalyzedProperty());
        for (ScopedRatingInfluencer<Scope> influencer : influencers) {
            assertEquals(EnumSet.of(Scope.NEW_VERSION, Scope.VARIANT_A), influencer.getScopes());
            assertEquals(
                    -100,
                    influencer
                            .getInfluencer()
                            .getPropertyRatingInfluencer(TestResults.TRUE)
                            .getInfluence());
            assertEquals(
                    500,
                    influencer
                            .getInfluencer()
                            .getPropertyRatingInfluencer(TestResults.TRUE)
                            .getScoreCap());
            assertEquals(
                    50,
                    influencer
                            .getInfluencer()
                            .getPropertyRatingInfluencer(TestResults.FALSE)
                            .getInfluence());
        }
    }

    @Test
    public void testSymmetric() {
        List<ScopedRatingInfluencer<Scope>> influencers =
                ScopedRatingInfluencer.symmetric(
                        Scope.ANY,
                        RatingImpact.PENALTY_HIGH,
                        Property.ANY_PROPERTY,
                        Property.B_PROPERTY);

        assertEquals(2, influencers.size());
        for (ScopedRatingInfluencer<Scope> influencer : influencers) {
            PropertyResultRatingInfluencer ifTrue =
                    influencer.getInfluencer().getPropertyRatingInfluencer(TestResults.TRUE);
            PropertyResultRatingInfluencer ifFalse =
                    influencer.getInfluencer().getPropertyRatingInfluencer(TestResults.FALSE);
            assertEquals(RatingImpact.PENALTY_HIGH.getInfluence(), ifTrue.getInfluence());
            assertEquals(RatingImpact.PENALTY_HIGH.getScoreCap(), ifTrue.getScoreCap());
            assertEquals(RatingImpact.BONUS_HIGH.getInfluence(), ifFalse.getInfluence());
            assertEquals(null, ifFalse.getScoreCap());
        }
    }
}
