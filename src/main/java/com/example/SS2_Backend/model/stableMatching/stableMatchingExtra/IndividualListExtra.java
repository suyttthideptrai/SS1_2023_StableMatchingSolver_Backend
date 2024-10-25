package com.example.SS2_Backend.model.stableMatching.stableMatchingExtra;

import com.example.SS2_Backend.model.stableMatching.Individual;
import com.example.SS2_Backend.model.stableMatching.IndividualList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndividualListExtra extends IndividualList {

    private final Map<Integer, Integer> setCounts;  // Map to store the count of individuals per set
    private int numberOfSets;  // Total number of unique sets

    public IndividualListExtra(List<Individual> individuals, String[] propertyNames, int numberOfSets) {
        super(individuals,propertyNames);
        this.setCounts = new HashMap<>();
        this.numberOfSets = numberOfSets;     // new
        initializeExtra();
    }

    private void initializeExtra() {
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


}
