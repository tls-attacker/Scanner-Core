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
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.rub.nds.scanner.core.TestScoreType;
import de.rub.nds.scanner.core.probe.AnalyzedProperty;
import de.rub.nds.scanner.core.probe.AnalyzedPropertyCategory;
import de.rub.nds.scanner.core.probe.result.TestResults;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class MultiPropertyClassIOTest {

    @XmlRootElement(name = "firstProperty")
    @XmlAccessorType(XmlAccessType.FIELD)
    public enum FirstProperty implements AnalyzedProperty {
        FIRST;

        @Override
        public AnalyzedPropertyCategory getCategory() {
            return null;
        }

        @Override
        public String getName() {
            return name();
        }
    }

    @XmlRootElement(name = "secondProperty")
    @XmlAccessorType(XmlAccessType.FIELD)
    public enum SecondProperty implements AnalyzedProperty {
        SECOND;

        @Override
        public AnalyzedPropertyCategory getCategory() {
            return null;
        }

        @Override
        public String getName() {
            return name();
        }
    }

    @Test
    public void testRatingInfluencersWithMultiplePropertyClasses() throws Exception {
        LinkedList<RatingInfluencer> influencers = new LinkedList<>();
        influencers.add(
                new RatingInfluencer(
                        FirstProperty.FIRST,
                        new PropertyResultRatingInfluencer(
                                TestResults.TRUE, new ScoreModifier(TestScoreType.SECURITY, 100))));
        influencers.add(
                new RatingInfluencer(
                        SecondProperty.SECOND,
                        new PropertyResultRatingInfluencer(
                                TestResults.FALSE,
                                new ScoreModifier(TestScoreType.SECURITY, -100))));
        RatingInfluencersIO io =
                new RatingInfluencersIO(
                        Set.of(FirstProperty.class, SecondProperty.class),
                        Set.of(TestScoreType.class));

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        io.write(stream, new RatingInfluencers(influencers));
        RatingInfluencers result = io.read(new ByteArrayInputStream(stream.toByteArray()));

        assertEquals(
                List.of(FirstProperty.FIRST, SecondProperty.SECOND),
                result.getRatingInfluencers().stream()
                        .map(RatingInfluencer::getAnalyzedProperty)
                        .toList());
    }

    @Test
    public void testRecommendationsWithMultiplePropertyClasses() throws Exception {
        List<Recommendation> recommendations =
                List.of(
                        new Recommendation(FirstProperty.FIRST, "first"),
                        new Recommendation(SecondProperty.SECOND, "second"));
        RecommendationsIO io =
                new RecommendationsIO(Set.of(FirstProperty.class, SecondProperty.class));

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        io.write(stream, new Recommendations(recommendations));
        Recommendations result = io.read(new ByteArrayInputStream(stream.toByteArray()));

        assertEquals(
                List.of(FirstProperty.FIRST, SecondProperty.SECOND),
                result.getRecommendations().stream()
                        .map(Recommendation::getAnalyzedProperty)
                        .toList());
    }

    @Test
    public void testModifiersAreSeparateElements() throws Exception {
        LinkedList<RatingInfluencer> influencers = new LinkedList<>();
        influencers.add(
                new RatingInfluencer(
                        FirstProperty.FIRST,
                        new PropertyResultRatingInfluencer(
                                TestResults.TRUE,
                                new ScoreModifier(
                                        TestScoreType.SECURITY, RatingImpact.PENALTY_HIGH),
                                new ScoreModifier(TestScoreType.INTEROPERABILITY, 50))));
        RatingInfluencersIO io =
                new RatingInfluencersIO(Set.of(FirstProperty.class), Set.of(TestScoreType.class));

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        io.write(stream, new RatingInfluencers(influencers));
        String xml = stream.toString(StandardCharsets.UTF_8);
        RatingInfluencers result = io.read(new ByteArrayInputStream(stream.toByteArray()));

        assertEquals(2, xml.split("<modifier>", -1).length - 1);
        assertTrue(xml.contains("<score>SECURITY</score>"));
        assertTrue(xml.contains("<score>INTEROPERABILITY</score>"));
        PropertyResultRatingInfluencer read =
                result.getRatingInfluencers().get(0).getPropertyRatingInfluencer(TestResults.TRUE);
        assertEquals(
                influencers.get(0).getPropertyRatingInfluencer(TestResults.TRUE).getModifiers(),
                read.getModifiers());
    }
}
