package com.example.SS2_Backend.model.stableMatching;

import com.example.SS2_Backend.model.stableMatching.Requirement.Requirement;
import lombok.Getter;

import java.util.*;

import static com.example.SS2_Backend.util.Utils.fillWithChar;
import static com.example.SS2_Backend.util.Utils.formatDouble;

@Getter
public class IndividualList {
    // Change from private to protected
    protected final List<Individual> individuals;
    // Change from private to protected
    protected final int numberOfIndividual;
    // Change from private to protected
    protected int numberOfIndividualForSet0;
    private final int numberOfProperties;
    // Change from private to protected
    protected final int[] capacities;
    private final String[] propertyNames;

    private final Map<Integer, Integer> setCounts;  // Map to store the count of individuals per set
    private int numberOfSets;  // Total number of unique sets

    /**
     * Initializes fields related to the population data.
     * ------------------------------------------
     * This method is executed only after the Individuals list has been initialized.
     * It sets up various fields such as the number of individuals, number of individuals in set 0,
     * number of properties, and preference lists based on the Individuals list.
     * @throws IllegalArgumentException if the number of individuals is less than 3, as matching would make no sense.
     */
    public IndividualList(List<Individual> individuals, String[] propertyNames) {
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
        this.propertyNames = propertyNames;

        this.setCounts = new HashMap<>();

        initialize();
    }

    private void initialize() {
        int count = 0;
        int tmpCapacity;
        for (int i = 0; i < this.numberOfIndividual; i++) {
            tmpCapacity = individuals.get(i).getCapacity();
            this.capacities[i] = tmpCapacity;
            this.capacities[count] = tmpCapacity;
            int individualSet = individuals.get(i).getIndividualSet();
            this.setCounts.put(individualSet, this.setCounts.getOrDefault(individualSet, 0) + 1);
            if (individualSet == 0) {
                count++;
            }
        }
        this.numberOfIndividualForSet0 = count;
        this.numberOfSets = this.setCounts.size();
    }
    //public Individual getIndividual(int index) {
    //    return this.individuals.get(index);
    //}

    public int getSetOf(int index) {
        return this.individuals.get(index).getIndividualSet();
    }

    /**
     * Retrieves the capacity of each node.
     * ---------------------------------
     * This method returns an array containing the capacity of each object.
     * The capacity of a single object can be obtained by passing its index (in the individual list) to this array.
     * For example: The capacity of the person at index 0 can be accessed as capacities[0].
     * @return An array of integers representing the capacities of each object.
     */
    public int getCapacityOf(int index) {
        return this.capacities[index];
    }

    public double getPropertyValueOf(int index, int indexOfProperty) {
        return individuals.get(index).getPropertyValue(indexOfProperty);
    }

    public double getPropertyWeightOf(int indexOfObject, int indexOfProperty) {
        return individuals.get(indexOfObject).getPropertyWeight(indexOfProperty);
    }

    public void print() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.numberOfProperties; i++) {
            sb.append(String.format("%-16s| ", propertyNames[i]));
        }
        String propName = sb.toString();
        sb.delete(0, sb.length());
        //header
        System.out.println("No | Set | Name                | " + propName);
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
        System.out.print(sb);
    }

    public String toString() {
        return "IndividualList{" + "individuals=" + individuals + ", numberOfIndividual=" +
                numberOfIndividual + ", numberOfIndividualForSet0=" + numberOfIndividualForSet0 +
                ", numberOfProperties=" + numberOfProperties +
//                 ", capacities=" + capacities +
                ", propertyNames=" + Arrays.toString(propertyNames) + '}';
    }

    public Requirement getRequirementOf(int idx1, int i) {
        return this.individuals.get(idx1).getRequirement(i);
    }


    public static void main(String[] args) {
        // Tạo danh sách các đối tượng Individual
        List<Individual> individuals = new ArrayList<>();

        // Tạo một vài cá nhân mẫu
        Individual individual1 = new Individual();
        individual1.setIndividualName("John Doe");
        individual1.setIndividualSet(0);
        individual1.setCapacity(5);
        individual1.setProperty(10.5, 2.0, new String[]{"100", "++"});

        Individual individual2 = new Individual();
        individual2.setIndividualName("Jane Smith");
        individual2.setIndividualSet(1);
        individual2.setCapacity(3);
        individual2.setProperty(51.0, 1.5, new String[]{"10", "--"});

        Individual individual3 = new Individual();
        individual3.setIndividualName("Tom Johnson");
        individual3.setIndividualSet(1);
        individual3.setCapacity(2);
        individual3.setProperty(20.0, 1.2, new String[]{"50", "++"});

        // Thêm các cá nhân vào danh sách
        individuals.add(individual1);
        individuals.add(individual2);
        individuals.add(individual3);

        // Tạo một mảng tên thuộc tính (property names)
        String[] propertyNames = {"Property 1", "Property 2"};

        // Khởi tạo đối tượng IndividualList
        IndividualList individualList = new IndividualList(individuals, propertyNames);

        // In thông tin danh sách các cá nhân
        System.out.println("Danh sách các cá nhân và thuộc tính:");
        individualList.print();

        // Kiểm tra một vài phương thức khác
        System.out.println("\nThông tin bổ sung:");
        System.out.println("Set của cá nhân 0: " + individualList.getSetOf(0));
        System.out.println("Capacity của cá nhân 1: " + individualList.getCapacityOf(1));
        System.out.println("Giá trị thuộc tính của cá nhân 1, thuộc tính 1: " +
                individualList.getPropertyValueOf(1, 0));
        System.out.println("Trọng số thuộc tính của cá nhân 2, thuộc tính 1: " +
                individualList.getPropertyWeightOf(2, 0));
        System.out.println("Yêu cầu của cá nhân 0, thuộc tính 1: " +
                individualList.getRequirementOf(0, 0));
    }

}
