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
import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.rub.nds.scanner.core.probe.AnalyzedProperty;
import de.rub.nds.scanner.core.probe.AnalyzedPropertyCategory;
import de.rub.nds.scanner.core.probe.result.TestResults;
import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class SerializationTest {

    private static class TestPropertyCategory implements AnalyzedPropertyCategory {
        private static final long serialVersionUID = 1L;
    }

    private static class TestProperty implements AnalyzedProperty {
        private static final long serialVersionUID = 1L;
        private final String name;

        public TestProperty(String name) {
            this.name = name;
        }

        @Override
        public AnalyzedPropertyCategory getCategory() {
            return new TestPropertyCategory();
        }

        @Override
        public String getName() {
            return name;
        }
    }

    @Test
    public void testRatingInfluencersSerialization() throws IOException, ClassNotFoundException {
        // Create test data
        TestProperty property = new TestProperty("TEST_PROPERTY");
        PropertyResultRatingInfluencer influencer1 =
                new PropertyResultRatingInfluencer(TestResults.TRUE, 10);
        PropertyResultRatingInfluencer influencer2 =
                new PropertyResultRatingInfluencer(TestResults.FALSE, -5);
        List<PropertyResultRatingInfluencer> influencerList =
                Arrays.asList(influencer1, influencer2);

        RatingInfluencer ratingInfluencer = new RatingInfluencer(property, influencerList);
        LinkedList<RatingInfluencer> ratingInfluencersList = new LinkedList<>();
        ratingInfluencersList.add(ratingInfluencer);

        RatingInfluencers ratingInfluencers = new RatingInfluencers(ratingInfluencersList);

        // Serialize
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(ratingInfluencers);
        oos.close();

        // Deserialize
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        RatingInfluencers deserialized = (RatingInfluencers) ois.readObject();
        ois.close();

        // Verify
        assertNotNull(deserialized);
        assertNotNull(deserialized.getRatingInfluencers());
        assertEquals(1, deserialized.getRatingInfluencers().size());
    }

    @Test
    public void testRecommendationsSerialization() throws IOException, ClassNotFoundException {
        // Create test data
        TestProperty property = new TestProperty("TEST_PROPERTY");
        PropertyResultRecommendation recommendation1 =
                new PropertyResultRecommendation(
                        TestResults.TRUE, "Short desc", "Handling recommendation");
        PropertyResultRecommendation recommendation2 =
                new PropertyResultRecommendation(
                        TestResults.FALSE, "Another desc", "Another recommendation");

        Recommendation recommendation =
                new Recommendation(
                        property,
                        "Test Recommendation",
                        "Short description",
                        "Detailed description",
                        recommendation1,
                        "https://example.com");
        recommendation.getPropertyRecommendations().add(recommendation2);

        List<Recommendation> recommendationList = Arrays.asList(recommendation);
        Recommendations recommendations = new Recommendations(recommendationList);

        // Serialize
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(recommendations);
        oos.close();

        // Deserialize
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        Recommendations deserialized = (Recommendations) ois.readObject();
        ois.close();

        // Verify
        assertNotNull(deserialized);
        assertNotNull(deserialized.getRecommendations());
        assertEquals(1, deserialized.getRecommendations().size());
    }
}
