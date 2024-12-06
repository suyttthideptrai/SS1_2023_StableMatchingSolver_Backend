package com.example.SS2_Backend.util;

import com.example.SS2_Backend.model.stableMatching.Individual;
import com.example.SS2_Backend.model.stableMatching.Matches.Matches;
import com.example.SS2_Backend.model.stableMatching.StableMatchingProblem;
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

    /**
     * Main method to demonstrate usage.
     */
    public static void main(String[] args) {

        SampleDataGenerator generator = new SampleDataGenerator(20, 2000);
        String[] propNames = {"Prop1", "Prop2", "Prop3", "Prop4"};
        generator.setPropNames(propNames);
        generator.setSet1Cap(1);
        generator.setSet2Cap(100);
        generator.setRandCapSet1(false);
        generator.setRandCapSet2(false);
        generator.setF1("none");
        generator.setF2("none");
        generator.setFnf("none");
        // Generate the StableMatchingProblem instance
        StableMatchingProblem problem = generator.generate();

        String algo = "IBEA";

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
            Testing tester = new Testing(matches,
                    problem.getIndividuals().getNumberOfIndividual(),
                    problem.getIndividuals().getCapacities());
            System.out.println("Solution has duplicate individual? : " + tester.hasDuplicate());
        }
        System.out.println("\nExecution time: " + runtime + " Second(s) with Algorithm: " + algo);

    }
    /**
     * Constructor for configuring generator with required sets.
     *
     * @param numberOfSet1 Number of individuals in set 1
     * @param numberOfSet2 Number of individuals in set 2
     */
    public SampleDataGenerator(int numberOfSet1, int numberOfSet2) {
        this.numberOfSet1 = numberOfSet1;
        this.numberOfSet2 = numberOfSet2;
    }

    /**
     * Generates a StableMatchingProblem instance based on the configured parameters.
     *
     * @return A StableMatchingProblem object
     */
    public StableMatchingProblem generate() {
        // Generate the individual population
        ArrayList<Individual> individuals = generateIndividualsWithCapacity();

//        // Define excluded pairs
//        int[][] excludedPairs = {
//                {0, 3},
//                {1, 2},
//                {0, 2},
//                {1, 3}
//        };
        // Create and configure the StableMatchingProblem instance
        StableMatchingProblem problem = new StableMatchingProblem();
        problem.setEvaluateFunctionForSet1(f1);
        problem.setEvaluateFunctionForSet2(f2);
        problem.setFitnessFunction(fnf);
        problem.setPopulation(individuals, this.propNames, null);
        return problem;
    }

    /**
     * Generates a list of individuals with their respective capacities.
     *
     * @return A list of individuals
     */
    private ArrayList<Individual> generateIndividualsWithCapacity() {
        ArrayList<Individual> individuals = new ArrayList<>();
        individuals.addAll(createIndividuals(numberOfSet1, set1Cap, randCapSet1, 0, propNames)); // Set 1
        individuals.addAll(createIndividuals(numberOfSet2, set2Cap, randCapSet2, 2, propNames)); // Set 2
        return individuals;
    }

    /**
     * Creates a list of individuals for a specific set.
     *
     * @param count      Number of individuals to create
     * @param capacity   Capacity of individuals
     * @param randomize  Whether to randomize capacity
     * @param set        Set number (0 for Set 1, 2 for Set 2)
     * @return A list of individuals
     */
    private List<Individual> createIndividuals(int count, int capacity, boolean randomize, int set, String[] propNames) {
        List<Individual> individuals = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Individual individual = new Individual();
            individual.setIndividualName("Individual " + (i + 1));
            individual.setIndividualSet(set);
            individual.setCapacity(randomize ? RANDOM.nextInt(capacity - 1) + 1 : capacity);
            addPropertiesToIndividual(individual, propNames);
            individuals.add(individual);
        }
        return individuals;
    }

    /**
     * Adds properties to an individual.
     *
     * @param individual The individual to which properties will be added
     */
    private void addPropertiesToIndividual(Individual individual, String[] propNames) {
        int numProps = propNames.length;
        for (int j = 0; j < numProps; j++) {
            // Example property values
            double propertyValue = RANDOM.nextDouble() * (70.0 - 20.0) + 20.0;
            double propertyWeight = 1 + (10 - 1) * RANDOM.nextDouble();
            String[] expression = {"", "--", "++"};
            double propertyBound = RANDOM.nextDouble() * (70.0 - 20.0) + 20.0;
            double propertyBound2 = RANDOM.nextDouble() * (70.0 - 20.0) + 20.0;
            int randomType = RANDOM.nextInt(2) + 1;
            int randomExpression = RANDOM.nextInt(2) + 1;

            // Property requirements
            String[] requirement;
            if (randomType == 1) {
                requirement = new String[]{String.valueOf(propertyBound), expression[randomExpression]};
            } else {
                requirement = new String[]{String.valueOf(propertyBound), String.valueOf(propertyBound2)};
            }
            individual.setProperty(propertyValue, propertyWeight, requirement);
        }
    }
}
