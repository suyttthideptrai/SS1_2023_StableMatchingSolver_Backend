//package com.example.SS2_Backend.model.stableMatching;
//
//import com.example.SS2_Backend.dto.request.NewStableMatchingProblemDTO;
//import com.example.SS2_Backend.util.SampleDataGenerator;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//public class StableMatchingRBOTest {
//    NewStableMatchingProblemDTO newStableMatchingProblemDTO;
//    private final StableMatchingRBOProblem newStableMatchingRBOProblem = new StableMatchingRBOProblem();
//    int numberOfIndividuals1;
//    int numberOfIndividuals2;
//
//    @BeforeEach
//    public void setUp() {
//        numberOfIndividuals1 = 5;
//        numberOfIndividuals2 = 5;
//        SampleDataGenerator sampleData = new SampleDataGenerator(numberOfIndividuals1, numberOfIndividuals2);
//        newStableMatchingProblemDTO = sampleData.generate();
//
//        newStableMatchingRBOProblem.setIndividualProperties(newStableMatchingProblemDTO.getIndividualProperties());
//        newStableMatchingRBOProblem.setIndividualWeights(newStableMatchingProblemDTO.getIndividualWeights());
//        newStableMatchingRBOProblem.setIndividualRequirements(newStableMatchingProblemDTO.getIndividualRequirements());
//        newStableMatchingRBOProblem.setIndividualSetIndices(newStableMatchingProblemDTO.getIndividualSetIndices());
//    }
//
//    @Test
//    public void testDefaultFitnessEvaluation() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        double[] satisfactions = {0.8, 0.9, 0.7, 0.6, 0.5};
//        double expectedFitness = 0.8 + 0.9 + 0.7 + 0.6 + 0.5;
//
//        Method method = StableMatchingRBOProblem.class.getDeclaredMethod("defaultFitnessEvaluation", double.class);
//        method.setAccessible(true);
//        double actualFitness = (double) method.invoke(newStableMatchingRBOProblem, satisfactions);
//
//        assertEquals(expectedFitness, actualFitness, 0.001);
//    }
//
//    @Test
//    public void testSigmaCalculate() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        double[] satisfactions = {0.8, 0.9, 0.7, 0.6, 0.5};
//        String expression = "P0*W0 + P1*W1";
//        newStableMatchingRBOProblem.setEvaluateFunctionForSet1(expression);
//        double expectedSigma = 0.8 * 0.5 + 0.9 * 0.6 + 0.7 * 0.7 + 0.6 * 0.4 + 0.5 * 0.3;
//
//        Method method = StableMatchingRBOProblem.class.getDeclaredMethod("sigmaCalculate", double.class);
//        method.setAccessible(true);
//        double actualSigma = (double) method.invoke(newStableMatchingRBOProblem, new Object[]{satisfactions, expression});
//
//        assertEquals(expectedSigma, actualSigma, 0.001);
//    }
//
//    @Test
//    public void testWithFitnessFunctionEvaluation() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        double[] satisfactions = {0.8, 0.9, 0.7, 0.6, 0.5};
//        String fitnessFunction = "P0*W0 + P1*W1";
//        double expectedFitness = 0.8 * 0.5 + 0.9 * 0.6 + 0.7 * 0.7 + 0.6 * 0.4 + 0.5 * 0.3;
//
//        Method method = StableMatchingRBOProblem.class.getDeclaredMethod("withFitnessFunctionEvaluation", double.class);
//        method.setAccessible(true);
//        double actualFitness = (double) method.invoke(newStableMatchingRBOProblem, new Object[]{satisfactions, fitnessFunction});
//
//
//        assertEquals(expectedFitness, actualFitness, 0.001);
//    }
//
//    @Test
//    public void testIsPreferredOver() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        int newNode = 2;
//        int currentNode = 1;
//        int selectorNode = 0;
//
//        Method method = StableMatchingRBOProblem.class.getDeclaredMethod("isPreferredOver", boolean.class);
//        method.setAccessible(true);
//        boolean isPreferred = (boolean) method.invoke(newStableMatchingRBOProblem, new Object[]{newNode, currentNode, selectorNode});
//
//        // Assuming the preference decision is based on the sum of weighted properties
//        double newPreferenceValue = 12 * 0.3 + 4 * 0.7;
//        double currentPreferenceValue = 8 * 0.4 + 6 * 0.6;
//        boolean expectedIsPreferred = newPreferenceValue > currentPreferenceValue;
//
//
//        assertEquals(expectedIsPreferred, isPreferred);
//    }
//
//    @Test
//    public void testFilterVariable() {
//        String evaluateFunction = "P1*W2 + P3*R4";
//        Map<String, Set<Integer>> expectedVariables = new HashMap<>();
//        expectedVariables.put("P", new HashSet<>(Set.of(1, 3)));
//        expectedVariables.put("W", new HashSet<>(Set.of(2)));
//        expectedVariables.put("R", new HashSet<>(Set.of(4)));
//
////        Method method = StableMatchingRBOProblem.class.getDeclaredMethod("filterVariable", double.class);
////        method.setAccessible(true);
////        double actualFitness = (double) method.invoke(newStableMatchingRBOProblem, new Object[]{satisfactions, fitnessFunction});
////
////        Map<String, Set<Integer>> actualVariables = newStableMatchingRBOProblem.filterVariable(evaluateFunction);
//
////        assertEquals(expectedVariables, actualVariables);
//    }
//}
