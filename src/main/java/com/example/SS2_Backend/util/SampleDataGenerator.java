package com.example.SS2_Backend.util;

import com.example.SS2_Backend.dto.mapper.StableMatchingProblemMapper;
import com.example.SS2_Backend.dto.request.NewStableMatchingProblemDTO;
import com.example.SS2_Backend.model.stableMatching.Individual;
import com.example.SS2_Backend.model.stableMatching.Matches.Matches;
import com.example.SS2_Backend.model.stableMatching.StableMatchingProblem;
import com.example.SS2_Backend.ss.smt.MatchingProblem;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Stable Matching Problem Testing Space.
 */
@Data
@Slf4j
public class SampleDataGenerator {
    // Configuration parameters
    private String[] propNames;
    private int numberOfSet1;
    private int numberOfSet2;
    private int set1Cap = 10; // Default capacity for set 1
    private int set2Cap = 10; // Default capacity for set 2
    private boolean randCapSet1 = false; // Randomize capacity for set 1
    private boolean randCapSet2 = false; // Randomize capacity for set 2
    private String f1 = "none";  // Evaluation function for set 1
    private String f2 = "none";  // Evaluation function for set 2
    private String fnf = "none"; // Fitness function
    private static final Random RANDOM = new Random(); // Random generator


    // Individuals' properties
    private int numberOfIndividuals;
    private double[][] individualProperties;
    private double[][] individualWeights;
    private String[][] individualRequirements;
    private int[] individualSetIndices;
    private int[] individualCapacity;

    /**
     * Main method to demonstrate usage.
     */
    public static void main(String[] args) {
        String[] propNames = {"Prop1", "Prop2", "Prop3", "Prop4"};
        SampleDataGenerator generator = new SampleDataGenerator(20, 2000, propNames);
        generator.setPropNames(propNames);
        generator.setSet1Cap(1);
        generator.setSet2Cap(100);
        generator.setRandCapSet1(false);
        generator.setRandCapSet2(false);
        generator.setF1("none");
        generator.setF2("none");
        generator.setFnf("none");
        // Generate the NewStableMatchingProblemDTO instance
        NewStableMatchingProblemDTO request = generator.generate();
        String algo = "IBEA";
        // Mapping DTO to MatchingProblem
        MatchingProblem problem = StableMatchingProblemMapper.toMTM(request);
        // Run the algorithm
        long startTime = System.currentTimeMillis();
        NondominatedPopulation result = new Executor()
                .withProblem(problem)
                .withAlgorithm(algo)
                .withMaxEvaluations(100)
                .withProperty("populationSize", 1000)
                .distributeOnAllCores()
                .run();
        long endTime = System.currentTimeMillis();
        double runtime = ((double) (endTime - startTime) / 1000);
        runtime = Math.round(runtime * 100.0) / 100.0;

        // Process and print the results
        for (Solution solution : result) {
            Matches matches = (Matches) solution.getAttribute("matches");
//            System.out.println("Output Matches (by Gale Shapley):\n" + matches.toString());
//            System.out.println("Randomized Individuals Input Order (by MOEA): " + solution.getVariable(0).toString());
            System.out.println("Fitness Score: " + -solution.getObjective(0));
//            Testing tester = new Testing(matches,
//                    problem.getIndividuals().getNumberOfIndividual(),
//                    problem.getIndividuals().getCapacities());
//            System.out.println("Solution has duplicate individual? : " + tester.hasDuplicate());
        }
        System.out.println("\nExecution time: " + runtime + " Second(s) with Algorithm: " + algo);

    }
    /**
     * Constructor for configuring generator with required sets.
     *
     * @param numberOfSet1 Number of individuals in set 1
     * @param numberOfSet2 Number of individuals in set 2
     */
    public SampleDataGenerator(int numberOfSet1, int numberOfSet2, String[] propNames) {
        this.numberOfSet1 = numberOfSet1;
        this.numberOfSet2 = numberOfSet2;
        this.propNames = propNames;
        numberOfIndividuals = numberOfSet1 + numberOfSet2;
        individualProperties = new double[numberOfIndividuals][propNames.length];
        individualWeights = new double[numberOfIndividuals][propNames.length];
        individualRequirements = new String[numberOfIndividuals][propNames.length];
        individualCapacity = new int[numberOfIndividuals];
        individualSetIndices = new int[numberOfIndividuals];
    }

    /**
     * Generates a NewStableMatchingProblemDTO instance based on the configured parameters.
     *
     * @return A NewStableMatchingProblemDTO object
     */
    public NewStableMatchingProblemDTO generate() {
        generateIndividualsWithCapacity();
        NewStableMatchingProblemDTO problemDTO = new NewStableMatchingProblemDTO();
        problemDTO.setIndividualProperties(individualProperties);
        problemDTO.setIndividualWeights(individualWeights);
        problemDTO.setIndividualRequirements(individualRequirements);
        problemDTO.setIndividualSetIndices(individualSetIndices);
        return problemDTO;
    }

    /**
     * Generates a list of individuals with their respective capacities.
     *
     * @return A list of individuals
     */
    private void generateIndividualsWithCapacity() {
        createIndividuals(numberOfSet1, set1Cap, randCapSet1, 0, propNames); // Set 1
        createIndividuals(numberOfSet2, set2Cap, randCapSet2, 2, propNames); // Set 2
    }

    /**
     * Creates a list of individuals for a specific set.
     *
     * @param count      Number of individuals to create
     * @param capacity   Capacity of individuals
     * @param randomize  Whether to randomize capacity
     * @param set        Set number (0 for Set 1, 2 for Set 2)
     */
    private void createIndividuals(int count, int capacity, boolean randomize, int set, String[] propNames) {
        for (int i = 0; i < count; i++) {
            individualSetIndices[i] = set;
            individualCapacity[i] = (randomize ? RANDOM.nextInt(capacity - 1) + 1 : capacity);
            addPropertiesToIndividual(i, propNames);
        }
    }

    /**
     * Adds properties to an individual.
     *
     * @param currentIndividual The individual to which properties will be added
     */
    private void addPropertiesToIndividual(int currentIndividual, String[] propNames) {
        int numProps = propNames.length;

        for (int j = 0; j < numProps; j++) {
            // Example property values
            double propertyValue = RANDOM.nextDouble() * (70.0 - 20.0) + 20.0;
            double propertyWeight = 1 + (10 - 1) * RANDOM.nextDouble();
//            String[] expression = {"", "--", "++"};
            double propertyBound = RANDOM.nextDouble() * (70.0 - 20.0) + 20.0;
            double propertyBound2 = RANDOM.nextDouble() * (70.0 - 20.0) + 20.0;
//            int randomType = RANDOM.nextInt(2) + 1;
//            int randomExpression = RANDOM.nextInt(2) + 1;
            String requirement = propertyBound + ":" + propertyBound2;

//            // Property requirements
//            String[] requirement;
//            if (randomType == 1) {
//                requirement = new String[]{String.valueOf(propertyBound), expression[randomExpression]};
//            } else {
//                requirement = new String[]{String.valueOf(propertyBound), String.valueOf(propertyBound2)};
//            }
            individualProperties[currentIndividual][j] = propertyValue;
            individualWeights[currentIndividual][j] = propertyWeight;
            individualRequirements[currentIndividual][j] = requirement;
        }
    }
}
