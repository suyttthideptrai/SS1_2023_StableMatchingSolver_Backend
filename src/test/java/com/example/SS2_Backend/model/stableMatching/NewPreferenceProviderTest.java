package com.example.SS2_Backend.model.stableMatching;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NewPreferenceProviderTest {

    private StableMatchingRBOProblem newStableMatchingRBOProblem;

    @BeforeEach
    public void setUp() {
        String[][] individualRequirements = {
                {"0.15", "0.15"},
                {"0.15", "0.15"},
                {"0.15", "0.15"},
                {"0.15", "0.15"},
                {"0.15", "0.15"}
        };
        double[][] individualWeights = {
                {0.5, 0.5},
                {0.4, 0.6},
                {0.3, 0.7},
                {0.6, 0.4},
                {0.7, 0.3}
        };
        double[][] individualProperties = {
                {10, 5},
                {8, 6},
                {12, 4},
                {9, 7},
                {11, 3}
        };
        int[] individualSetIndices = {0, 1, 1, 1, 1};

        newStableMatchingRBOProblem = new StableMatchingRBOProblem();
    }

    @Test
    public void testDefaultFitnessEvaluation() {
        double[] satisfactions = {0.8, 0.9, 0.7, 0.6, 0.5};
        double expectedFitness = 0.8 + 0.9 + 0.7 + 0.6 + 0.5;

        double actualFitness = newStableMatchingRBOProblem.defaultFitnessEvaluation(satisfactions);

        assertEquals(expectedFitness, actualFitness, 0.001);
    }

    @Test
    public void testSigmaCalculate() {
        double[] satisfactions = {0.8, 0.9, 0.7, 0.6, 0.5};
        String expression = "P0*W0 + P1*W1";
        newStableMatchingRBOProblem.setEvaluateFunctionForSet1(expression);
        double expectedSigma = 0.8 * 0.5 + 0.9 * 0.6 + 0.7 * 0.7 + 0.6 * 0.4 + 0.5 * 0.3;

        double actualSigma = newStableMatchingRBOProblem.sigmaCalculate(satisfactions, expression);

        assertEquals(expectedSigma, actualSigma, 0.001);
    }

    @Test
    public void testWithFitnessFunctionEvaluation() {
        double[] satisfactions = {0.8, 0.9, 0.7, 0.6, 0.5};
        String fitnessFunction = "P0*W0 + P1*W1";
        double expectedFitness = 0.8 * 0.5 + 0.9 * 0.6 + 0.7 * 0.7 + 0.6 * 0.4 + 0.5 * 0.3;

        double actualFitness = newStableMatchingRBOProblem.withFitnessFunctionEvaluation(satisfactions, fitnessFunction);

        assertEquals(expectedFitness, actualFitness, 0.001);
    }

    @Test
    public void testIsPreferredOver() {
        int newNode = 2;
        int currentNode = 1;
        int selectorNode = 0;

        boolean isPreferred = newStableMatchingRBOProblem.isPreferredOver(newNode, currentNode, selectorNode);

        // Assuming the preference decision is based on the sum of weighted properties
        double newPreferenceValue = 12 * 0.3 + 4 * 0.7;
        double currentPreferenceValue = 8 * 0.4 + 6 * 0.6;
        boolean expectedIsPreferred = newPreferenceValue > currentPreferenceValue;

        assertEquals(expectedIsPreferred, isPreferred);
    }

    @Test
    public void testFilterVariable() {
        String evaluateFunction = "P1*W2 + P3*R4";
        Map<String, Set<Integer>> expectedVariables = new HashMap<>();
        expectedVariables.put("P", new HashSet<>(Set.of(1, 3)));
        expectedVariables.put("W", new HashSet<>(Set.of(2)));
        expectedVariables.put("R", new HashSet<>(Set.of(4)));

        Map<String, Set<Integer>> actualVariables = newStableMatchingRBOProblem.filterVariable(evaluateFunction);

        assertEquals(expectedVariables, actualVariables);
    }
}
