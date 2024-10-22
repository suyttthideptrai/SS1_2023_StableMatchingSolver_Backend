package com.example.SS2_Backend.model.stableMatching.stableMatchingExtra;

import com.example.SS2_Backend.model.stableMatching.PreferenceList;
import com.example.SS2_Backend.model.stableMatching.StableMatchingProblem;

import java.util.HashMap;
import java.util.Map;

public class StableMatchingExtra extends StableMatchingProblem {
    private Map<Integer, Boolean> fnStatus = new HashMap<>();
    private Map<Integer, String> evaluateFunctionForSetN = new HashMap<>();
    private PreferencesProviderExtra preferencesProviderExtra;


    @Override
    public void initializeFields() {
        this.preferencesProviderExtra = new PreferencesProviderExtra(individuals);
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
