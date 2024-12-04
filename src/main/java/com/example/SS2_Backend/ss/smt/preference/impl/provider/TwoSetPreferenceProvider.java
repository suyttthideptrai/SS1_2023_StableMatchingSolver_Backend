package com.example.SS2_Backend.ss.smt.preference.impl.provider;

import com.example.SS2_Backend.ss.smt.preference.PreferenceList;
import com.example.SS2_Backend.ss.smt.MatchingData;
import com.example.SS2_Backend.ss.smt.preference.PreferenceBuilder;
import com.example.SS2_Backend.ss.smt.preference.PreferenceListWrapper;
import com.example.SS2_Backend.ss.smt.preference.impl.list.TwoSetPreferenceList;
import com.example.SS2_Backend.ss.smt.requirement.Requirement;
import com.example.SS2_Backend.util.PreferenceProviderUtils;
import lombok.Data;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Standard implementation of PreferenceBuilder that uses Exp4j lib
 */
@Data
public class TwoSetPreferenceProvider implements PreferenceBuilder {

    private final MatchingData matchingData;
    private final int sizeOf1;
    private final int sizeOf2;
    private Expression expressionOfSet1;
    private Expression expressionOfSet2;
    private Map<String, Set<Integer>> variablesOfSet1;
    private Map<String, Set<Integer>> variablesOfSet2;


    /**
     * initialize Exp4j mathematical Expression & variables for each set
     */
    public TwoSetPreferenceProvider(MatchingData matchingData, String[] evaluationFunctions) {
        this.matchingData = matchingData;
        String evalFunctionForSet1 = evaluationFunctions[0];
        String evalFunctionForSet2 = evaluationFunctions[1];
        this.sizeOf1 = matchingData.getTotalIndividualOfSet(0);
        this.sizeOf2 = matchingData.getSize() - sizeOf1;

        this.variablesOfSet1 = PreferenceProviderUtils.filterVariable(evalFunctionForSet1);
        this.expressionOfSet1 = new ExpressionBuilder(evalFunctionForSet1)
                .variables(PreferenceProviderUtils.convertMapToSet(variablesOfSet1))
                .build();

        if (expressionOfSet2 != null) return;
        this.variablesOfSet2 = PreferenceProviderUtils.filterVariable(evalFunctionForSet2);
        this.expressionOfSet2 = new ExpressionBuilder(evalFunctionForSet2)
                .variables(PreferenceProviderUtils.convertMapToSet(variablesOfSet2))
                .build();

    }


    public Map<String, Double> getVariableValuesForSet1(int indexOfEvaluator,
                                                        int indexOfBeEvaluated) {
        return getVariableValues(this.variablesOfSet1, indexOfEvaluator, indexOfBeEvaluated);
    }

    public Map<String, Double> getVariableValuesForSet2(int indexOfEvaluator,
                                                        int indexOfBeEvaluated) {
        return getVariableValues(this.variablesOfSet2, indexOfEvaluator, indexOfBeEvaluated);
    }

    private Map<String, Double> getVariableValues(Map<String, Set<Integer>> variables,
                                                  int idx1,
                                                  int idx2) {
        Map<String, Double> variablesValues = new HashMap<>();
        for (Map.Entry<String, Set<Integer>> entry : variables.entrySet()) {
            String key = entry.getKey();
            Set<Integer> values = entry.getValue();
            switch (key) {
                case "P":
                    for (Integer value : values) {
                        double val = matchingData.getPropertyValueOf(idx2, value - 1);
                        variablesValues.put(key + value, val);
                    }
                    break;
                case "W":
                    for (Integer value : values) {
                        double val = matchingData.getPropertyWeightOf(idx1, value - 1);
                        variablesValues.put(key + value, val);
                    }
                    break;
                case "R":
                    for (Integer value : values) {
                        double val = matchingData
                                .getRequirementOf(idx1, value - 1)
                                .getValueForFunction();
                        variablesValues.put(key + value, val);
                    }
                    break;
                default:
                    double val = 0d;
                    variablesValues.put(key, val);
            }
        }
        return variablesValues;
    }

    public PreferenceList getPreferenceListByFunction(int index) {
        int set = matchingData.getSetNoOf(index);
        TwoSetPreferenceList a;
        Expression e;
        if (set == 0) {
            a = new TwoSetPreferenceList(this.sizeOf2, this.sizeOf1);
            if (this.expressionOfSet1 == null) {
                return this.getPreferenceListByDefault(index);
            }
            e = this.expressionOfSet1;
            for (int i = this.sizeOf1; i < matchingData.getSize(); i++) {
                e.setVariables(this.getVariableValuesForSet1(index, i));
                double totalScore = e.evaluate();
                a.add(totalScore);
            }
        } else {
            a = new TwoSetPreferenceList(this.sizeOf1, 0);
            if (this.expressionOfSet2 == null) {
                return this.getPreferenceListByDefault(index);
            }
            e = this.expressionOfSet2;
            for (int i = 0; i < sizeOf1; i++) {
                e.setVariables(this.getVariableValuesForSet2(index, i));
                double totalScore = e.evaluate();
                a.add(totalScore);
            }
        }
        a.sort();
        return a;
    }

    public PreferenceList getPreferenceListByDefault(int index) {
        int set = matchingData.getSetNoOf(index);
        int numberOfProperties = matchingData.getPropertyNum();
        TwoSetPreferenceList a;
        if (set == 0) {
            a = new TwoSetPreferenceList(this.sizeOf2, this.sizeOf1);
            for (int i = sizeOf1; i < matchingData.getSize(); i++) {
                double totalScore = 0;
                for (int j = 0; j < numberOfProperties; j++) {
                    double propertyValue = matchingData.getPropertyValueOf(i, j);
                    Requirement requirement = matchingData.getRequirementOf(index, j);
                    double propertyWeight = matchingData.getPropertyWeightOf(index, j);
                    totalScore += requirement.getDefaultScaling(propertyValue) * propertyWeight;
                }
                // Add
                a.add(totalScore);
            }
        } else {
            a = new TwoSetPreferenceList(this.sizeOf1, 0);
            for (int i = 0; i < sizeOf1; i++) {
                double totalScore = 0;
                for (int j = 0; j < numberOfProperties; j++) {
                    double PropertyValue = matchingData.getPropertyValueOf(i, j);
                    Requirement requirement = matchingData.getRequirementOf(index, j);
                    double PropertyWeight = matchingData.getPropertyWeightOf(index, j);
                    totalScore += requirement.getDefaultScaling(PropertyValue) * PropertyWeight;
                }
                // Add
                a.add(totalScore);
            }
        }
        a.sort();
        return a;
    }

    @Override
    public PreferenceListWrapper toListWrapper() {
        List<PreferenceList> lists = new ArrayList<>();
        for (int i = 0; i < matchingData.getSize(); i++) {
            lists.add(this.getPreferenceListByFunction(i));
        }
        return new PreferenceListWrapper(lists);
    }

}
