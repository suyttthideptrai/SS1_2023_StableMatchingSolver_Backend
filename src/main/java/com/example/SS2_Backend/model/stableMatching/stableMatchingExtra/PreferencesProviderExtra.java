package com.example.SS2_Backend.model.stableMatching.stableMatchingExtra;

import com.example.SS2_Backend.model.stableMatching.IndividualList;
import com.example.SS2_Backend.model.stableMatching.PreferenceList;
import com.example.SS2_Backend.model.stableMatching.PreferencesProvider;
import com.example.SS2_Backend.model.stableMatching.Requirement.Requirement;
import lombok.Getter;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.*;


public class PreferencesProviderExtra extends PreferencesProvider {

    private final Map<Integer, Integer> setSizes;

    @Getter
    private final Map<Integer, Expression> expressions;
    private final Map<Integer, Map<String, Set<Integer>>> variables;
    private int numberOfSets ;      // new

    public PreferencesProviderExtra(IndividualList individuals, int numberOfSets) {
        super(individuals);
        this.setSizes = new HashMap<>();
        this.expressions = new HashMap<>();
        this.variables = new HashMap<>();
        this.numberOfSets = numberOfSets;  // new

        // Initialize set sizes
        for (int i = 0; i < numberOfIndividuals; i++) {
            int set = individuals.getSetOf(i);
            setSizes.put(set, setSizes.getOrDefault(set, 0) + 1);
        }
    }


    public void setEvaluateFunction(int set, String evaluateFunction) {
        if (expressions.containsKey(set)) return;
        Map<String, Set<Integer>> vars = filterVariable(evaluateFunction);
        variables.put(set, vars);
        expressions.put(set, new ExpressionBuilder(evaluateFunction)
                .variables(convertMapToSet(vars))
                .build());
    }

    public Map<String, Double> getVariableValuesForSet(int set, int indexOfEvaluator,
                                                       int indexOfBeEvaluated) {
        return getVariableValues(this.variables.get(set), indexOfEvaluator, indexOfBeEvaluated);
    }


    @Override
    public PreferenceList getPreferenceListByFunction(int index) {
        int set = individuals.getSetOf(index);
        PreferenceList a = new PreferenceList(0, 0);
        Expression e;
        int size = 0;
        if (setSizes.containsKey(set)) {          // 1 2 3 4 5   6 7 8 9 10
            for (int setNumber : setSizes.keySet()) {
                if (setNumber != set) {
                    size += setSizes.get(set);
                }
            }
            if(set == 1) {
                a = new PreferenceList(size, setSizes.get(1));    // khởi tạo preferlist với size = 2 set còn lại + vào
            } else {
                a = new PreferenceList(size, 0);
            }
            if (this.expressions.get(set) == null) {
                return this.getPreferenceListByDefault(index);
            }
            e = this.expressions.get(set);

            for (int i = 0; i < numberOfIndividuals; i++) {
                if (individuals.getSetOf(i) != set) {
                    e.setVariables(this.getVariableValuesForSet(set, index, i));
                    double totalScore = e.evaluate();
                    a.add(totalScore);       // chưa hoàn thiện vì sort sẽ chạy qua tất cả các set chứa trong ds của set đang xét
                }
            }
        }
        a.sort();



        return a;
    }


    @Override
    public PreferenceList getPreferenceListByDefault(int index) {
        int set = individuals.getSetOf(index);
        int numberOfProperties = individuals.getNumberOfProperties();
        PreferenceList a = new PreferenceList(0, 0);
        int size = 0;

        if (setSizes.containsKey(set)) {
            for (int setNumber : setSizes.keySet()) {
                if (setNumber != set) {
                    size += setSizes.get(set);
                }
            }
            if(set == 1) {
                a = new PreferenceList(size, setSizes.get(1));    // khởi tạo preferlist với size = 2 set còn lại + vào
            } else {
                a = new PreferenceList(size, 0);
            }
            for (int i = 0; i < numberOfIndividuals; i++) {
                double totalScore = 0;
                if (individuals.getSetOf(i) != set) {
                    for (int j = 0; j < numberOfProperties; j++) {
                        double PropertyValue = individuals.getPropertyValueOf(i, j);
                        Requirement requirement = individuals.getRequirementOf(index, j);
                        double PropertyWeight = individuals.getPropertyWeightOf(index, j);
                        totalScore += getDefaultScaling(requirement, PropertyValue) * PropertyWeight;
                    }
                    a.add(totalScore);
                }
            }
        }
        a.sort();
        return a;
    }




}
