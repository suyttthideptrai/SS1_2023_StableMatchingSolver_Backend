package com.example.SS2_Backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

// TODO Reconstruct the DTO Class based on the new JSON FILE STRUCT (DONE)
public class StableMatchingProblemDTO {
    private String problemName;
    private int numberOfSets;
    private int numberOfIndividuals;
    private String[] allPropertyNames;
    private int[] individualSetIndices;
    private int[] individualCapacities;

    private List<List<String>> individualRequirements;
    private List<List<Double>> individualWeights;
    private List<List<Double>> individualProperties;

    private String[] evaluateFunction;
    private String fitnessFunction;
    private int populationSize;
    private int generation;
    private int maxTime;
    private String algorithm;
    private String distributedCores;


    public static String[][] fromListToStringArray(List<List<String>> list) {
        String[][] array = new String[list.size()][];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i).toArray(new String[0]);
        }
        return array;
    }

    public static double[][] fromListToDoubleArray(List<List<Double>> list) {
        double[][] array = new double[list.size()][];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i).stream().mapToDouble(Double::doubleValue).toArray();
        }
        return array;
    }


    public String toString() {
        return "Matching_Theory_Problem {" + "\n" +
                " ProblemName = " + problemName + "\n" +
                ", NumberOfSets = " + numberOfSets + "\n" +
                ", NumberOfIndividuals = " + numberOfIndividuals + "\n" +
                ", IndividualSetIndices = " + Arrays.toString(individualSetIndices) + "\n" +
                ", IndividualCapacities = " + Arrays.toString(individualCapacities) + "\n" +
                ", AllPropertyName = " + Arrays.toString(allPropertyNames) +
                ", fitnessFunction = '" + fitnessFunction + "\n" +
                ", PopulationSize = " + populationSize + "\n" +
                ", Generation = " +generation + "\n" +
                ", individualRequirements: " + Arrays.deepToString(individualRequirements.toArray()) + "\n" +
                ", individualWeights: " + Arrays.deepToString(individualWeights.toArray()) + "\n" +
                ", individualProperties: " + Arrays.deepToString(individualProperties.toArray()) + "\n" +
                "}";
    }
}
