package com.example.SS2_Backend.ss.smt.preference;

import com.example.SS2_Backend.model.stableMatching.Requirement.Requirement;
import com.example.SS2_Backend.ss.smt.preference.impl.list.PreferenceListExtra;
import net.objecthunter.exp4j.Expression;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface PreferencesProvider {

    void setEvaluateFunction(int set, String evaluateFunction);

    Set<String> convertMapToSet(Map<String, Set<Integer>> varMap);

    Map<String, Set<Integer>> filterVariable(String evaluateFunction);

    Optional<Integer> getNextIndexToken(String evaluateFunction, int currentIndex);

    Map<String, Double> getVariableValuesForSet(int set, int indexOfEvaluator, int indexOfBeEvaluated);
    Map<String, Double> getVariableValues(Map<String, Set<Integer>> variables,
                                          int idx1,
                                          int idx2);

    PreferenceListExtra getPreferenceListByFunction(int index);

    PreferenceListExtra getPreferenceListByDefault(int index);

    double getDefaultScaling(Requirement requirement, double propertyValue);
    void sortDescendingByScores(double[] tempScores, int[] tempPositions);
    void heapify(double[] tempScores, int[] tempPositions, int heapSize, int rootIndex);

}
