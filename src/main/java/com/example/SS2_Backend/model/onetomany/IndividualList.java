package com.example.SS2_Backend.model.onetomany;

import com.example.SS2_Backend.model.onetomany.Requirement.Requirement;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static com.example.SS2_Backend.util.Utils.fillWithChar;
import static com.example.SS2_Backend.util.Utils.formatDouble;

@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class IndividualList {

    final List<Individual> individuals;
    final int totalIndividuals;
    int providerCount;
    final int propertiesPerIndividual;
    final int[] providerCapacities;
    final String[] propertiesNames;

    /**
     * Initializes fields related to the population data.
     * ------------------------------------------
     * This method is executed only after the participants list has been initialized.
     * It sets up various fields such as the total number of participants, provider count,
     * number of attributes, and preference lists based on the participants list.
     * @throws IllegalArgumentException if the number of participants is less than 3, as matching would make no sense.
     */
    public IndividualList(List<Individual> individuals, String[] propertiesNames) {
        this.individuals = individuals;
        this.totalIndividuals = individuals.size();
        if (totalIndividuals < 3) {
            throw new IllegalArgumentException(
                    "Invalid number of participants, number must be greater or equal to 3 (int) as matching makes no sense");
        }
        this.propertiesPerIndividual = individuals.get(0).getProperties().size();
        if (propertiesPerIndividual == 0) {
            throw new IllegalArgumentException(
                    "Invalid number of attributes, number must be greater than 0 (int) as matching makes no sense");
        }

        this.propertiesNames = propertiesNames;
        this.providerCapacities = new int[getProviderCountAndInitCapacities()];
    }

    /**
     * Initializes both the provider count and capacities in a single loop.
     * ------------------------------------------
     * This method counts how many providers belong to set 0, and simultaneously
     * initializes the capacities for those providers.
     */
    private int getProviderCountAndInitCapacities() {
        int count = 0;
        for (int i = 0; i < this.totalIndividuals; i++) {
            if (individuals.get(i).getBelongToSet() == 0) {
                providerCapacities[count] = individuals.get(i).getCapacity();
                count++;
            }
        }
        this.providerCount = count;
        return count;
    }

    public int getRoleOfParticipant(int index) {
        return this.individuals.get(index).getBelongToSet();
    }

    /**
     * Retrieves the capacity of each provider.
     * ---------------------------------
     * This method returns an array containing the capacity of each provider.
     * The capacity of a single provider can be obtained by passing its index (in the participant list) to this array.
     * For example: The capacity of the provider at index 0 can be accessed as providerCapacities[0].
     * @return An array of integers representing the capacities of each provider.
     */
    public int getProviderCapacity(int index) {
        return this.providerCapacities[index];
    }

    public double getAttributeValueOf(int index, int attributeIndex) {
        return individuals.get(index).getProperty(attributeIndex);
    }

    public double getAttributeWeightOf(int participantIndex, int attributeIndex) {
        return individuals.get(participantIndex).getPropertyWeight(attributeIndex);
    }

    public Requirement getRequirementOf(int idx1, int i) {
        return this.individuals.get(idx1).getRequirement(i);
    }

    public void print() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.propertiesPerIndividual; i++) {
            sb.append(String.format("%-16s| ", propertiesNames[i]));
        }
        String attrName = sb.toString();
        sb.delete(0, sb.length());
        //header
        System.out.println("No | Set | Name                | " + attrName);
        int width = this.propertiesPerIndividual * 18 + 32;
        String filledString = fillWithChar('-', width);
        sb.append(filledString).append("\n");
        //content
        for (int i = 0; i < this.totalIndividuals; i++) {
            //name / set
            sb.append(String.format("%-3d| ", i));
            sb.append(String.format("%-4d| ", individuals.get(i).getBelongToSet()));
            sb.append(String.format("%-20s| ", individuals.get(i).getName()));
            // attr value
            StringBuilder ss = new StringBuilder();
            for (int j = 0; j < this.propertiesPerIndividual; j++) {
                ss.append(String.format("%-16s| ", formatDouble(this.getAttributeValueOf(i, j))));
            }
            sb.append(ss).append("\n");
            ss.delete(0, sb.length());
            ss.append(String.format("%33s", "Requirement: | "));
            for (int j = 0; j < this.propertiesPerIndividual; j++) {
                ss.append(String.format("%-16s| ",
                        this.individuals.get(i).getRequirement(j).toString()));
            }
            sb.append(ss).append("\n");
            ss.delete(0, sb.length());
            ss.append(String.format("%33s", "Weight: | "));
            for (int j = 0; j < this.propertiesPerIndividual; j++) {
                ss.append(String.format("%-16s| ", this.getAttributeWeightOf(i, j)));
            }
            sb.append(ss).append("\n");
        }
        sb.append(filledString).append("\n");
        System.out.print(sb);
    }
}