package com.example.SS2_Backend.ss.smt.preference;

import com.example.SS2_Backend.model.stableMatching.PreferenceList;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import net.objecthunter.exp4j.Expression;
import org.springframework.stereotype.Component;

import java.util.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public abstract class PreferenceProvider {
    int numberOfIndividuals;
    int numberOfIndividualForSet0;
    int sizeOf1;
    int sizeOf2;
    Expression expressionOfSet1;
    Expression expressionOfSet2;
    Map<String, Set<Integer>> variablesOfSet1;
    Map<String, Set<Integer>> variablesOfSet2;

    public Map<String, Double> getVariableValuesForSet1(int indexOfEvaluator,
                                                        int indexOfBeEvaluated) {
        return getVariableValues(this.variablesOfSet1, indexOfEvaluator, indexOfBeEvaluated);
    }

    public Map<String, Double> getVariableValuesForSet2(int indexOfEvaluator,
                                                        int indexOfBeEvaluated) {
        return getVariableValues(this.variablesOfSet2, indexOfEvaluator, indexOfBeEvaluated);
    }

    protected Map<String, Double> getVariableValues(Map<String, Set<Integer>> variables,
                                                  int idx1,
                                                  int idx2) {
        return null;
    }

    protected PreferenceList getPreferenceListByFunction(int index) {
        return null;
    }

    protected PreferenceList getPreferenceListByDefault(int index) {
        return null;
    }
}
