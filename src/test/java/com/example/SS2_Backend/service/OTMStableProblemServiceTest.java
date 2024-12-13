//package com.example.SS2_Backend.service;
//
//import com.example.SS2_Backend.dto.request.NewStableMatchingProblemDTO;
//import com.example.SS2_Backend.dto.response.Response;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//
//import java.util.Arrays;
//import java.util.Random;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@ExtendWith(MockitoExtension.class)
//class OTMStableProblemServiceTest {
//
//    @Mock
//    private SimpMessagingTemplate mockSimpMessagingTemplate;
//
//    private OTMStableMatchingSolver otmStableMatchingSolverUnderTest;
//
//    @BeforeEach
//    void setUp() {
//        otmStableMatchingSolverUnderTest = new OTMStableMatchingSolver(mockSimpMessagingTemplate);
//    }
//
//    @Test
//    void testSolve() {
//        // Setup a two-set stable matching problem
//        final NewStableMatchingProblemDTO request = new NewStableMatchingProblemDTO();
//        request.setProblemName("Two-Set Stable Matching Optimization");
//
//        // Two sets (e.g., Students and Universities)
//        request.setNumberOfSets(2);
//
//        // Total number of individuals across both sets
//        request.setNumberOfIndividuals(15);
//
//        // Assume 5 universities (set 0) and 10 students (set 1)
//        int[] setIndices = new int[15];
//        Arrays.fill(setIndices, 0, 5, 0);  // First 5 are universities
//        Arrays.fill(setIndices, 5, 15, 1); // Next 10 are students
//        request.setIndividualSetIndices(setIndices);
//
//        // Capacities for universities (how many students they can accept)
//        int[] capacities = new int[15];
//        capacities[0] = 3;  // First university can take 3 students
//        capacities[1] = 2;  // Second university can take 2 students
//        capacities[2] = 4;  // Third university can take 4 students
//        capacities[3] = 3;  // Fourth university can take 3 students
//        capacities[4] = 2;  // Fifth university can take 2 students
//        Arrays.fill(capacities, 5, 15, 1);  // Students have individual capacity of 1
//        request.setIndividualCapacities(capacities);
//
//        // Properties could represent things like academic performance, research interests, etc.
//        request.setNumberOfProperty(2);
//
//        // Student requirements for universities
//        String[][] requirements = new String[15][2];
//        for (int i = 0; i < 5; i++) {
//            requirements[i][0] = "ResearchCapacity";
//            requirements[i][1] = "FundingLevel";
//        }
//        for (int i = 5; i < 15; i++) {
//            requirements[i][0] = "AcademicScore";
//            requirements[i][1] = "ResearchInterest";
//        }
//        request.setIndividualRequirements(requirements);
//
//        // Weights for evaluating match quality
//        double[][] weights = new double[15][2];
//        for (int i = 0; i < 5; i++) {
//            weights[i][0] = 0.7;  // Universities care more about research
//            weights[i][1] = 0.3;  // Less about funding
//        }
//        for (int i = 5; i < 15; i++) {
//            weights[i][0] = 0.6;  // Students care about academic performance
//            weights[i][1] = 0.4;  // And research interests
//        }
//        request.setIndividualWeights(weights);
//
//        // Actual property values
//        double[][] properties = new double[15][2];
//        // Universities' properties
//        properties[0] = new double[]{8.5, 9.2};   // Top research university
//        properties[1] = new double[]{7.8, 8.5};   // Strong research focus
//        properties[2] = new double[]{9.0, 7.5};   // High research capacity
//        properties[3] = new double[]{8.2, 8.8};   // Balanced
//        properties[4] = new double[]{7.5, 9.0};   // High funding
//
//        // Students' properties (random but realistic academic scores and research interests)
//        Random rand = new Random();
//        for (int i = 5; i < 15; i++) {
//            properties[i][0] = 5.0 + rand.nextDouble() * 4.0;  // Academic score 5-9
//            properties[i][1] = rand.nextDouble();  // Research interest 0-1
//        }
//        request.setIndividualProperties(properties);
//
//        // Two simple evaluation functions
//        request.setEvaluateFunctions(new String[]{
//                "P1 * W1",  // First property weighted importance
//                "P2 * W2"   // Second property weighted importance
//        });
//
//        // Fitness function that combines both evaluation functions
//        request.setFitnessFunction("(P1 * W1) + (P2 * W2)");
//
//        // Some excluded pairs (incompatible matches)
//        request.setExcludedPairs(new int[][]{
//                {0, 5},  // University 0 doesn't want student 5
//                {1, 7},  // University 1 doesn't want student 7
//                {2, 9}   // University 2 doesn't want student 9
//        });
//
//        // Genetic algorithm parameters
//        request.setPopulationSize(200);
//        request.setGeneration(50);
//        request.setMaxTime(3600);  // 1 hour
//
//        request.setAlgorithm("NSGAII");
//        request.setDistributedCores("4");
//
//        final ResponseEntity<Response> expectedResult = new ResponseEntity<>(Response.builder()
//                .status(0)
//                .message("Stable Matching Solution Found")
//                .data("Optimized University-Student Matching")
//                .build(), HttpStatus.OK);
//
//        // Run the test
//        final ResponseEntity<Response> result = otmStableMatchingSolverUnderTest.solve(request);
//
//        // Verify the results
//        assertThat(result).isEqualTo(expectedResult);
//    }
//
//}
