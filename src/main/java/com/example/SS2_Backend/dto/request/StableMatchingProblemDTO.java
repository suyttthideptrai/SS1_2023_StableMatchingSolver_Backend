package com.example.SS2_Backend.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;

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

    @JsonDeserialize(using = Custom2DStringArrayDeserializer.class)
    private String[][] individualRequirements;
    @JsonDeserialize(using = CustomDouble2DArrayDeserializer.class)
    private double[][] individualWeights;
    @JsonDeserialize(using = CustomDouble2DArrayDeserializer.class)
    private double[][] individualProperties;

    private String[] evaluateFunction;
    private String fitnessFunction;
    private int populationSize;
    private int generation;
    private int maxTime;
    private String algorithm;
    private String distributedCores;


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
                ", MaximumExecutionTime: " + maxTime + "\n" +
                "}";
    }
}
