package com.example.SS2_Backend.util;

import com.example.SS2_Backend.dto.mapper.StableMatchingProblemMapper;
import com.example.SS2_Backend.dto.request.NewStableMatchingProblemDTO;
import com.example.SS2_Backend.model.stableMatching.Matches.Matches;
import com.example.SS2_Backend.model.stableMatching.StableMatchingRBOProblem;
import com.example.SS2_Backend.ss.smt.MatchingProblem;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;

import java.util.Map;
import java.util.Random;

enum MATCHING_PROBLEM_TYPE {
    MTM,
    OTM,
    OTO
}
/**
 * Stable Matching Problem Testing Space.
 */
@Data
@Slf4j
public class SampleDataGenerator {
    // Configuration parameters
    private MATCHING_PROBLEM_TYPE matchingProblemType = MATCHING_PROBLEM_TYPE.MTM;
    // problemSize
    private int individualNum;
    private int numberOfProperties;
    // max capacity tiêu chuẩn cho mỗi set dạng map<int, int>, vd: với MTM: {0: 2, 1: 10}, 3Set: {0: 1, 1: 10, 2: 12}
     private int numberOfSet1;
     private int numberOfSet2;
    // private int set1Cap = 10; // Default capacity for set 1
    // private int set2Cap = 10; // Default capacity for set 2
    Map<Integer, Integer> setCapacities;
    // capRandomize: Căn cứ với set Capacities để generate capacity cho matching data
    boolean[] capRandomize = {false, false};
    private String[] evaluateFunctions = {"none", "none"};
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
        int numberOfProperties = 5;
        SampleDataGenerator generator = new SampleDataGenerator(20, 2000, numberOfProperties);
        generator.setCapacities.put(20, 1);
        generator.setCapacities.put(2000, 100);
        generator.setCapRandomize(new boolean[]{false, false});
        generator.setEvaluateFunctions(new String[]{"none", "none"});

        generator.setFnf("none");
        // Generate the NewStableMatchingProblemDTO instance
        NewStableMatchingProblemDTO request = generator.generateDto();
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
    public SampleDataGenerator(int numberOfSet1, int numberOfSet2, int numberOfProperties) {
        this.numberOfSet1 = numberOfSet1;
        this.numberOfSet2 = numberOfSet2;
        this.setCapacities.put(numberOfSet1, 10);
        this.setCapacities.put(numberOfSet2, 10);
        this.numberOfProperties = numberOfProperties;
        numberOfIndividuals = numberOfSet1 + numberOfSet2;
        individualProperties = new double[numberOfIndividuals][numberOfProperties];
        individualWeights = new double[numberOfIndividuals][numberOfProperties];
        individualRequirements = new String[numberOfIndividuals][numberOfProperties];
        individualCapacity = new int[numberOfIndividuals];
        individualSetIndices = new int[numberOfIndividuals];
    }


    /**
     * Generates a NewStableMatchingProblemDTO instance based on the configured parameters.
     *
     * @return A NewStableMatchingProblemDTO object
     */
    public NewStableMatchingProblemDTO generateDto() {
        NewStableMatchingProblemDTO problemDTO = new NewStableMatchingProblemDTO();
        problemDTO.setIndividualProperties(individualProperties);
        problemDTO.setIndividualWeights(individualWeights);
        problemDTO.setIndividualRequirements(individualRequirements);
        problemDTO.setIndividualSetIndices(individualSetIndices);
        return problemDTO;
    }

    /**
     * Generates a StableMatchingRBOProblem instance based on the configured parameters.
     *
     * @return StableMatchingRBOProblem
     */
    public StableMatchingRBOProblem generateProblem() {
        NewStableMatchingProblemDTO problemDTO = new NewStableMatchingProblemDTO();
        problemDTO.setIndividualProperties(individualProperties);
        problemDTO.setIndividualWeights(individualWeights);
        problemDTO.setIndividualRequirements(individualRequirements);
        problemDTO.setIndividualSetIndices(individualSetIndices);

        StableMatchingRBOProblem stableMatchingRBOProblem = new StableMatchingRBOProblem();
        stableMatchingRBOProblem.setPopulation(problemDTO);
        return stableMatchingRBOProblem;
    }

    /**
     * Generates a list of individuals with their respective capacities.
     */
    private void generateIndividualsWithCapacity() {
        createIndividuals(numberOfSet1, setCapacities.get(numberOfSet1), capRandomize[0], 0, numberOfProperties); // Set 1
        // Set Number - Set Capacity - Rand cap
        createIndividuals(numberOfSet2, setCapacities.get(numberOfSet2), capRandomize[1], 2, numberOfProperties);
    }

    /**
     * Creates a list of individuals for a specific set.
     *
     * @param count      Number of individuals to create
     * @param capacity   Capacity of individuals
     * @param randomize  Whether to randomize capacity
     * @param set        Set number (0 for Set 1, 2 for Set 2)
     */
    private void createIndividuals(int count, int capacity, boolean randomize, int set, int numberOfProperties) {
        for (int i = 0; i < count; i++) {
            individualSetIndices[i] = set;
            individualCapacity[i] = (randomize ? RANDOM.nextInt(capacity - 1) + 1 : capacity);
            addPropertiesToIndividual(i, numberOfProperties);
        }
    }

    /**
     * Adds properties to an individual.
     *
     * @param currentIndividual The individual to which properties will be added
     */
    private void addPropertiesToIndividual(int currentIndividual, int numberOfProperties) {
        for (int j = 0; j < numberOfProperties; j++) {
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
