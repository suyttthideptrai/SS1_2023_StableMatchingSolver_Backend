package com.example.SS2_Backend.model.stableMatching;

import com.example.SS2_Backend.dto.request.NewStableMatchingProblemDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SMTProblemUnitTesting {

    private StableMatchingRBOProblem stableMatchingRBOProblem;

    @BeforeEach
    public void setUp() {
        stableMatchingRBOProblem = new StableMatchingRBOProblem();

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

        NewStableMatchingProblemDTO request = new NewStableMatchingProblemDTO();
        request.setIndividualRequirements(Arrays.asList(individualRequirements));
        request.setIndividualWeights(Arrays.asList(individualWeights));
        request.setIndividualProperties(Arrays.asList(individualProperties));
        request.setIndividualSetIndices(individualSetIndices);
        request.setNumberOfIndividuals(5);
        request.setIndividualCapacities(new int[]{1, 1, 1, 1, 1});

        stableMatchingRBOProblem.setPopulation(request);
    }

    @Test
    public void testSetEvaluateFunctionForSet1() {
        String evaluateFunctionForSet1 = "P0*W0 + P1*W1";
        stableMatchingRBOProblem.setEvaluateFunctionForSet1(evaluateFunctionForSet1);

        assertTrue(stableMatchingRBOProblem.f1Status);
        assertEquals(evaluateFunctionForSet1, stableMatchingRBOProblem.evaluateFunctionForSet1);
    }

    @Test
    public void testSetEvaluateFunctionForSet2() {
        String evaluateFunctionForSet2 = "P0*W0 + P1*W1";
        stableMatchingRBOProblem.setEvaluateFunctionForSet2(evaluateFunctionForSet2);

        assertTrue(stableMatchingRBOProblem.f2Status);
        assertEquals(evaluateFunctionForSet2, stableMatchingRBOProblem.evaluateFunctionForSet2);
    }

    @Test
    public void testSetFitnessFunction() {
        String fitnessFunction = "SIGMA{S1}";
        stableMatchingRBOProblem.setFitnessFunction(fitnessFunction);

        assertTrue(stableMatchingRBOProblem.fnfStatus);
        assertEquals(fitnessFunction, stableMatchingRBOProblem.fitnessFunction);
    }

    @Test
    public void testDefaultFitnessEvaluation() {
        double[] satisfactions = {0.8, 0.9, 0.7, 0.6, 0.5};
        double expectedFitness = 0.8 + 0.9 + 0.7 + 0.6 + 0.5;

        double actualFitness = stableMatchingRBOProblem.defaultFitnessEvaluation(satisfactions);

        assertEquals(expectedFitness, actualFitness, 0.001);
    }

    @Test
    public void testWithFitnessFunctionEvaluation() {
        double[] satisfactions = {0.8, 0.9, 0.7, 0.6, 0.5};
        String fitnessFunction = "SIGMA{S1}";
        double expectedFitness = 0.8 + 0.9 + 0.7 + 0.6 + 0.5;

        double actualFitness = stableMatchingRBOProblem.withFitnessFunctionEvaluation(satisfactions, fitnessFunction);

        assertEquals(expectedFitness, actualFitness, 0.001);
    }

    @Test
    public void testIsPreferredOver() {
        int newNode = 2;
        int currentNode = 1;
        int selectorNode = 0;

        boolean isPreferred = stableMatchingRBOProblem.isPreferredOver(newNode, currentNode, selectorNode);

        // Assuming the preference decision is based on the sum of weighted properties
        double newPreferenceValue = 12 * 0.3 + 4 * 0.7;
        double currentPreferenceValue = 8 * 0.4 + 6 * 0.6;
        boolean expectedIsPreferred = newPreferenceValue > currentPreferenceValue;

        assertEquals(expectedIsPreferred, isPreferred);
    }
}

