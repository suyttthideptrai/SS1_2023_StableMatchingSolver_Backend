package com.example.SS2_Backend.model.stableMatching.stableMatchingExtra;

import com.example.SS2_Backend.model.stableMatching.Individual;
import com.example.SS2_Backend.model.stableMatching.IndividualList;
import com.example.SS2_Backend.model.stableMatching.PreferenceList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StableMatchingExtra extends StableMatchingProblem {
    private Map<Integer, Boolean> fnStatus = new HashMap<>();
    private Map<Integer, String> evaluateFunctionForSetN = new HashMap<>();
    private PreferencesProviderExtra preferencesProviderExtra;
    private int numberOfSets;


    @Override
    public void setPopulation(ArrayList<Individual> individuals, String[] propertiesNames, int numberOfSets) {
        this.individuals = new IndividualListExtra(individuals, propertiesNames, numberOfSets);
        initializeFields();
    }

    @Override
    public void initializeFields() {
        this.preferencesProviderExtra = new PreferencesProviderExtra(individuals, numberOfSets);
        initializePrefProvider();
        preferenceLists = getPreferences();
    }

    @Override
    protected void initializePrefProvider() {
        for (Map.Entry<Integer, String> entry : this.evaluateFunctionForSetN.entrySet()) {
            // Set evaluate function for each set in the PreferencesProvider
            this.preferencesProviderExtra.setEvaluateFunction(entry.getKey(), entry.getValue());
        }
    }

    public StableMatchingExtra() {}

    public void setEvaluateFunctionForSetN(int setN, String evaluateFunction) {
        if (isValidEvaluateFunction(evaluateFunction)) {
            fnStatus.put(setN, true);
            this.evaluateFunctionForSetN.put(setN, evaluateFunction);
        }
    }

    /**
     * Extra Methods for  Stable Matching Problem
     */
    @Override
    public PreferenceList getPreferenceOfIndividual(int index){
        PreferenceList a ;
            boolean allFalse = fnStatus.values().stream().allMatch(status -> !status);

            if(allFalse){
                a = preferencesProviderExtra.getPreferenceListByDefault(index);
            } else {
                a = preferencesProviderExtra.getPreferenceListByFunction(index);
            }
            return a ;
    }

    // Add to a complete List by getPreferences()

}
