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
import de.rub.nds.scanner.core.probe.result.TestResult;
import java.io.*;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Test to verify serialization functionality of rating-related classes */
public class SerializationTest {

    @Test
    public void testRatingInfluencersSerializable() throws IOException, ClassNotFoundException {
        // Create test data
        LinkedList<RatingInfluencer> influencers = new LinkedList<>();
        RatingInfluencers ratingInfluencers = new RatingInfluencers(influencers);

        // Test serialization
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(ratingInfluencers);
        oos.close();

        // Test deserialization
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        RatingInfluencers deserialized = (RatingInfluencers) ois.readObject();
        ois.close();

        assertNotNull(deserialized);
        assertNotNull(deserialized.getRatingInfluencers());
    }

    @Test
    public void testRecommendationsSerializable() throws IOException, ClassNotFoundException {
        // Create test data
        List<Recommendation> recs = new LinkedList<>();
        Recommendations recommendations = new Recommendations(recs);

        // Test serialization
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(recommendations);
        oos.close();

        // Test deserialization
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        Recommendations deserialized = (Recommendations) ois.readObject();
        ois.close();

        assertNotNull(deserialized);
        assertNotNull(deserialized.getRecommendations());
    }

    @Test
    public void testRatingInfluencerSerializable() throws IOException, ClassNotFoundException {
        // Create test data
        RatingInfluencer influencer = new RatingInfluencer();

        // Test serialization
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(influencer);
        oos.close();

        // Test deserialization
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        RatingInfluencer deserialized = (RatingInfluencer) ois.readObject();
        ois.close();

        assertNotNull(deserialized);
    }

    @Test
    public void testRecommendationSerializable() throws IOException, ClassNotFoundException {
        // Create test data
        Recommendation recommendation = new Recommendation();

        // Test serialization
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(recommendation);
        oos.close();

        // Test deserialization
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        Recommendation deserialized = (Recommendation) ois.readObject();
        ois.close();

        assertNotNull(deserialized);
    }

    @Test
    public void testPropertyResultRatingInfluencerSerializable()
            throws IOException, ClassNotFoundException {
        // Create test data with a mock TestResult implementation
        TestResult mockResult = new MockTestResult();
        PropertyResultRatingInfluencer influencer =
                new PropertyResultRatingInfluencer(mockResult, 10);

        // Test serialization
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(influencer);
        oos.close();

        // Test deserialization
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        PropertyResultRatingInfluencer deserialized =
                (PropertyResultRatingInfluencer) ois.readObject();
        ois.close();

        assertNotNull(deserialized);
        assertEquals(10, deserialized.getInfluence());
    }

    @Test
    public void testPropertyResultRecommendationSerializable()
            throws IOException, ClassNotFoundException {
        // Create test data with a mock TestResult implementation
        TestResult mockResult = new MockTestResult();
        PropertyResultRecommendation recommendation =
                new PropertyResultRecommendation(mockResult, "Status", "Handle it");

        // Test serialization
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(recommendation);
        oos.close();

        // Test deserialization
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        PropertyResultRecommendation deserialized = (PropertyResultRecommendation) ois.readObject();
        ois.close();

        assertNotNull(deserialized);
        assertEquals("Status", deserialized.getShortDescription());
        assertEquals("Handle it", deserialized.getHandlingRecommendation());
    }

    @Test
    public void testComplexSerializationScenario() throws IOException, ClassNotFoundException {
        // Create a complex hierarchy
        TestResult mockResult = new MockTestResult();
        PropertyResultRatingInfluencer propInfluencer =
                new PropertyResultRatingInfluencer(mockResult, 5);
        List<PropertyResultRatingInfluencer> propInfluencers = new LinkedList<>();
        propInfluencers.add(propInfluencer);

        RatingInfluencer ratingInfluencer =
                new RatingInfluencer(new MockAnalyzedProperty(), propInfluencers);
        LinkedList<RatingInfluencer> influencers = new LinkedList<>();
        influencers.add(ratingInfluencer);
        RatingInfluencers ratingInfluencers = new RatingInfluencers(influencers);

        // Test serialization
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(ratingInfluencers);
        oos.close();

        // Test deserialization
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        RatingInfluencers deserialized = (RatingInfluencers) ois.readObject();
        ois.close();

        assertNotNull(deserialized);
        assertEquals(1, deserialized.getRatingInfluencers().size());
        assertEquals(
                1,
                deserialized.getRatingInfluencers().get(0).getPropertyRatingInfluencers().size());
    }

    // Mock implementations for testing
    private static class MockTestResult implements TestResult, Serializable {
        @Override
        public String getName() {
            return "MockTestResult";
        }

        @Override
        public boolean equalsExpectedResult(TestResult expectedResult) {
            return this.equals(expectedResult);
        }

        @Override
        public String toString() {
            return "MockTestResult";
        }
    }

    private static class MockAnalyzedProperty implements AnalyzedProperty, Serializable {
        @Override
        public String getName() {
            return "MockProperty";
        }

        @Override
        public AnalyzedPropertyCategory getCategory() {
            return new MockAnalyzedPropertyCategory();
        }

        @Override
        public String toString() {
            return getName();
        }
    }

    private static class MockAnalyzedPropertyCategory
            implements AnalyzedPropertyCategory, Serializable {
        @Override
        public String toString() {
            return "MockCategory";
        }
    }
}
