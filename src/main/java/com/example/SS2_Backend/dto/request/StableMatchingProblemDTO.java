package com.example.SS2_Backend.dto.request;

import com.example.SS2_Backend.constants.MessageConst.ErrMessage;
import com.example.SS2_Backend.model.stableMatching.Individual;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StableMatchingProblemDTO {

    private String problemName;
    @Min(value = 2, message = ErrMessage.MES_001) private int numberOfSets;
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
    private int[][] excludedPairs;

    @JsonProperty("Individuals")
    public void setIndividuals(ArrayList<Individual> individuals) {
        this.Individuals = individuals;
    }

    public Individual getIndividual(int index) {
        return Individuals.get(index);
    }

    public int getNumberOfIndividuals() {
        return Individuals.size();
    }

    @Override
    public String toString() {
        return "StableMatchingProblemDTO{" + "problemName='" + problemName + '\'' +
                ", numberOfSets=" + numberOfSets + ", numberOfIndividuals=" + numberOfIndividuals +
                ", Individuals=" + Individuals + ", allPropertyNames=" +
                Arrays.toString(allPropertyNames) + ", evaluateFunction=" +
                Arrays.toString(evaluateFunction) + ", fitnessFunction='" + fitnessFunction + '\'' +
                ", populationSize=" + populationSize + ", generation=" + generation + ", maxTime=" +
                maxTime + ", algorithm='" + algorithm + '\'' + ", distributedCores='" +
                distributedCores + '\'' + ", excludedPairs=" + Arrays.toString(excludedPairs) + '}';
    }

}
