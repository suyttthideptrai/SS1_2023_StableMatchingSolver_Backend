package com.example.SS2_Backend.dto.request;

import com.example.SS2_Backend.model.StableMatching.Individual;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StableMatchingProblemDTO {
    private String problemName;
    private int numberOfSets;
    private int numberOfIndividuals;
    @JsonDeserialize(contentUsing = IndividualDeserializer.class)
    private ArrayList<Individual> Individuals;
    private String[] allPropertyNames;
    private String[] evaluateFunction;
    private String fitnessFunction;
    private int populationSize;
    private int generation;
    private int maxTime;
    private String algorithm;
    private String distributedCores;

    @JsonProperty("Individuals")
    public void setIndividuals(ArrayList<Individual> individuals) {
        this.Individuals = individuals;
    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }

    public Individual getIndividual(int index) {
        return Individuals.get(index);
    }

    public int getNumberOfIndividuals(){
        return Individuals.size();
    }

    public void setGeneration(int generation) {
        this.generation = generation;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public void setDistributedCores(String distributedCores) {
        this.distributedCores = distributedCores;
    }

    public String toString() {
        return "Matching_Theory_Problem {" + "\n" +
                " ProblemName = " + problemName + "\n" +
                ", NumberOfSets = " + numberOfSets + "\n" +
                ", NumberOfIndividuals = " + numberOfIndividuals + "\n" +
                ", Individuals = " + Individuals.toString() + "\n" +
                ", AllPropertyName = " + java.util.Arrays.toString(allPropertyNames) +
                ", fitnessFunction = '" + fitnessFunction + "\n" +
                ", PopulationSize = " + populationSize + "\n" +
                ", Generation = " +generation + "\n" +
                ", MaximumExecutionTime: " + maxTime + "\n" +
                "}";
    }
}
