package com.example.SS2_Backend.util;

import com.example.SS2_Backend.model.StableMatching.Individual;
import com.example.SS2_Backend.model.StableMatching.Matches;
import com.example.SS2_Backend.model.StableMatching.Requirement.OneBoundRequirement;
import com.example.SS2_Backend.model.StableMatching.Requirement.Requirement;
import com.example.SS2_Backend.model.StableMatching.Requirement.TwoBoundRequirement;
import com.example.SS2_Backend.model.StableMatching.StableMatchingProblem;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Stable Matching Problem Testing Space:
 */

public class SampleDataGenerator {

    public static void main(String[] args) {
        // Generate Individuals data Randomly
        ArrayList<Individual> individuals = generateSampleIndividualsWithCapacity(40, 1, false, 10, 4, false, 4);

        String[] propNames = {"Prop1", "Prop2", "Prop3", "Prop4"};

        // Create an Instance of StableMatchingProblem class with randomly generated data
        StableMatchingProblem problem = new StableMatchingProblem(individuals, propNames,
                "Default");

        // Print the whole Population
        System.out.println(
                "\n[ Randomly Generated Population ]\n"
        );
        problem.printIndividuals();

        // Number of Individuals inside this problem
        System.out.println("Number Of Individual: " + problem.getNumberOfIndividual());

        // Preference List Produced by Algorithm
        System.out.println(
                "\n[ Preference List Produced By the Program ]\n"
        );
        System.out.println(problem.printPreferenceLists());

        System.out.println(
                "\n[ Algorithm Output Solution ]\n"
        );
        // Run algorithm:
        long startTime = System.currentTimeMillis();

        NondominatedPopulation result = new Executor()
            .withProblem(problem)
            .withAlgorithm("NSGAII")
            .withMaxEvaluations(1000)
            .withProperty("populationSize", 20)
            .distributeOnAllCores()
            .run();
        long endTime = System.currentTimeMillis();
        double runtime = ((double) (endTime - startTime) / 1000);
        runtime = Math.round(runtime * 100.0) / 100.0;
        for (Solution solution : result) {
            System.out.println("Randomized Individuals Input Order (by MOEA): " + solution.getVariable(0).toString());
            // Turn Solution:Attribute(Serializable Object) to Matches:"matches"(Instance of Matches Class)
            Matches matches = (Matches) solution.getAttribute("matches");
            // Prints matches
            System.out.println("Output Matches (by Gale Shapley):\n" + matches.toString());
            // Prints fitness score of this Solution
            System.out.println("Fitness Score: " + -solution.getObjective(0));
        }
        System.out.println("\nExecution time: " + runtime + " Second(s) with Algorithm: " + "NSGAII");



    }

    public static ArrayList<Individual> generateSampleIndividuals(int numIndividuals, int numProps) {
        ArrayList<Individual> individuals = new ArrayList<>();

        for (int i = 1; i <= numIndividuals; i++) {
            String individualName = "Individual Name" + i;
            int individualSet;
            if (i <= numIndividuals / 2) {
                individualSet = 0;
            } else {
                individualSet = 1;
            }
            Individual individual = new Individual();
            individual.setIndividualName(individualName);
            individual.setIndividualSet(individualSet);

            // Add some sample properties (you can customize this part)
            for (int j = 0; j < numProps; j++) {
                double propertyValue = new Random().nextDouble() * (70.0 - 20.0) + 20.0;
                // Random property Value (20 -> 50)
                int propertyWeight = new Random().nextInt(10) + 1; // Random property Weight (1 -> 10)
                String[] expression = {"", "--", "++"};
                double propertyBound = new Random().nextDouble() * (70.0 - 20.0) + 20.0;
                double propertyBound2 = new Random().nextDouble() * (70.0 - 20.0) + 20.0;
                int randomType = new Random().nextInt(2) + 1;
                int randomExpression = new Random().nextInt(2) + 1;

                if (randomType == 1) {
                    String[] requirement = {String.valueOf(propertyBound), expression[randomExpression]};
                    individual.setProperty(propertyValue, propertyWeight, requirement);
                } else {
                    String[] requirement = {String.valueOf(propertyBound), String.valueOf(propertyBound2)};
                    individual.setProperty(propertyValue, propertyWeight, requirement);
                }
            }
            individuals.add(individual);

        }
        return individuals;
    }
    public static ArrayList<Individual> generateSampleIndividualsWithCapacity(int numSet1, int set1PeakCap, boolean cap1Randomize, int numSet2, int set2PeakCap, boolean cap2Randomize, int numProps) {
        ArrayList<Individual> individuals = new ArrayList<>();

        for (int i = 1; i <= numSet1; i++) {
            String individualName = "Individual Name" + i;
            int individualSet = 0;
            int individualCapacity;
            if(cap1Randomize) {
                individualCapacity = new Random().nextInt(set1PeakCap-1) + 1;
            }else {
                individualCapacity = set1PeakCap;
            }
            Individual individual = new Individual();
            individual.setIndividualName(individualName);
            individual.setIndividualSet(individualSet);
            individual.setCapacity(individualCapacity);

            // Add some sample properties (you can customize this part)
            for (int j = 0; j < numProps; j++) {
                // Random property Value (20 -> 50)
                double propertyValue = new Random().nextDouble() * (70.0 - 20.0) + 20.0;
                // Random property Weight (1 -> 10)
                int propertyWeight = new Random().nextInt(10) + 1; // Random property Weight (1 -> 10)
                // Random property Requirement with types
                String[] expression = {"", "--", "++"};
                double propertyBound = new Random().nextDouble() * (70.0 - 20.0) + 20.0;
                double propertyBound2 = new Random().nextDouble() * (70.0 - 20.0) + 20.0;
                int randomType = new Random().nextInt(2) + 1;
                int randomExpression = new Random().nextInt(2) + 1;

                if (randomType == 1) {
                    String[] requirement = {String.valueOf(propertyBound), expression[randomExpression]};
                    individual.setProperty(propertyValue, propertyWeight, requirement);
                } else {
                    String[] requirement = {String.valueOf(propertyBound), String.valueOf(propertyBound2)};
                    individual.setProperty(propertyValue, propertyWeight, requirement);
                }
            }
            individuals.add(individual);
        }
        for (int i = 1; i <= numSet2; i++) {
            String individualName = "Individual Name" + i;
            int individualSet;
            individualSet = 1;
            int individualCapacity;
            if(cap2Randomize) {
                individualCapacity = new Random().nextInt(set2PeakCap-1) + 1;
            }else {
                individualCapacity = set2PeakCap;
            }
            Individual individual = new Individual();
            individual.setIndividualName(individualName);
            individual.setIndividualSet(individualSet);
            individual.setCapacity(individualCapacity);

            // Add some sample properties (you can customize this part)
            for (int j = 0; j < numProps; j++) {
                // Random property Value (20 -> 50)
                double propertyValue = new Random().nextDouble() * (70.0 - 20.0) + 20.0;
                // Random property Weight (1 -> 10)
                int propertyWeight = new Random().nextInt(10) + 1; // Random property Weight (1 -> 10)
                // Random property Requirement with types
                String[] expression = {"", "--", "++"};
                double propertyBound = new Random().nextDouble() * (70.0 - 20.0) + 20.0;
                double propertyBound2 = new Random().nextDouble() * (70.0 - 20.0) + 20.0;
                int randomType = new Random().nextInt(2) + 1;
                int randomExpression = new Random().nextInt(2) + 1;

                if (randomType == 1) {
                    String[] requirement = {String.valueOf(propertyBound), expression[randomExpression]};
                    individual.setProperty(propertyValue, propertyWeight, requirement);
                } else {
                    String[] requirement = {String.valueOf(propertyBound), String.valueOf(propertyBound2)};
                    individual.setProperty(propertyValue, propertyWeight, requirement);
                }
            }
            individuals.add(individual);

        }
        return individuals;
    }
}

