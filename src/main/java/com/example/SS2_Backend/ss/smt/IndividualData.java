package com.example.SS2_Backend.ss.smt;

import com.example.SS2_Backend.model.stableMatching.Individual;
import com.example.SS2_Backend.model.stableMatching.Requirement.Requirement;
import lombok.Getter;

import java.util.List;

import static com.example.SS2_Backend.util.NumberUtils.formatDouble;
import static com.example.SS2_Backend.util.StringUtils.fillWithChar;

@Getter
public class IndividualData {

    private final List<Individual> individuals;
    private final int numberOfIndividual;
    private final int numberOfProperties;
    //TODO: continue after refactor PWR
    private final int[] sets;
    private final int[] capacities;
    private int numberOfIndividualForSet0;

    public IndividualData(List<Individual> individuals) {
        this.individuals = individuals;
        this.numberOfIndividual = individuals.size();
        if (numberOfIndividual < 3) {
            throw new IllegalArgumentException(
                    "Invalid number of individuals, number must be greater or equal to 3 (int) as matching makes no sense");
        }
        this.numberOfProperties = individuals.get(0).getProperties().size();
        if (numberOfProperties == 0) {
            throw new IllegalArgumentException(
                    "Invalid number of properties, number must be greater than 0 (int) as matching makes no sense");
        }
        this.capacities = new int[individuals.size()];
        initialize();
    }

    private void initialize() {
        int count = 0;
        int tmpCapacity;
        for (int i = 0; i < this.numberOfIndividual; i++) {
            tmpCapacity = individuals.get(i).getCapacity();
            this.capacities[i] = tmpCapacity;
            this.capacities[count] = tmpCapacity;
            if (individuals.get(i).getIndividualSet() == 0) {
                count++;
            }
        }
        this.numberOfIndividualForSet0 = count;
    }

    public int getSetOf(int index) {
        return this.individuals.get(index).getIndividualSet();
    }

    public int getCapacityOf(int index) {
        return this.capacities[index];
    }

    public double getPropertyValueOf(int index, int indexOfProperty) {
        return individuals.get(index).getPropertyValue(indexOfProperty);
    }

    public double getPropertyWeightOf(int indexOfObject, int indexOfProperty) {
        return individuals.get(indexOfObject).getPropertyWeight(indexOfProperty);
    }

    public StringBuilder toStringBuilder() {
        StringBuilder sb = new StringBuilder();
        String propName;
        for (int i = 1; i <= this.numberOfProperties; i++) {
            propName = "Prop " + i;
            sb.append(String.format("%-16s| ", propName));
        }
        String propNames = sb.toString();
        sb.delete(0, sb.length());
        //header
        System.out.println("No | Set | Name                | " + propNames);
        int width = this.numberOfProperties * 18 + 32;
        String filledString = fillWithChar('-', width);
        sb.append(filledString).append("\n");
        //content
        for (int i = 0; i < this.numberOfIndividual; i++) {
            //name / set
            sb.append(String.format("%-3d| ", i));
            sb.append(String.format("%-4d| ", individuals.get(i).getIndividualSet()));
            sb.append(String.format("%-20s| ", individuals.get(i).getIndividualName()));
            // prop value
            StringBuilder ss = new StringBuilder();
            for (int j = 0; j < this.numberOfProperties; j++) {
                ss.append(String.format("%-16s| ", formatDouble(this.getPropertyValueOf(i, j))));
            }
            sb.append(ss).append("\n");
            ss.delete(0, sb.length());
            ss.append(String.format("%33s", "Requirement: | "));
            for (int j = 0; j < this.numberOfProperties; j++) {
                ss.append(String.format("%-16s| ",
                        this.individuals.get(i).getRequirement(j).toString()));
            }
            sb.append(ss).append("\n");
            ss.delete(0, sb.length());
            ss.append(String.format("%33s", "Weight: | "));
            for (int j = 0; j < this.numberOfProperties; j++) {
                ss.append(String.format("%-16s| ", this.getPropertyWeightOf(i, j)));
            }
            sb.append(ss).append("\n");
        }
        sb.append(filledString).append("\n");
        return sb;
    }

    public String toString() {
        return this.toStringBuilder().toString();
    }

    public Requirement getRequirementOf(int idx1, int i) {
        return this.individuals.get(idx1).getRequirement(i);
    }

}

