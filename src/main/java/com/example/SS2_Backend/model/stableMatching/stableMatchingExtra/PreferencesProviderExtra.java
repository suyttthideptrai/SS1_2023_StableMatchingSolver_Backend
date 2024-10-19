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

    public PreferencesProviderExtra(IndividualList individuals) {
        super(individuals);
        this.setSizes = new HashMap<>();
        this.expressions = new HashMap<>();
        this.variables = new HashMap<>();

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

    public Map<String, Double> getVariableValuesForSet(int set,int indexOfEvaluator,
                                                       int indexOfBeEvaluated) {
        return getVariableValues(this.variables.get(set), indexOfEvaluator, indexOfBeEvaluated);
    }

    @Override
    public PreferenceList getPreferenceListByFunction(int index) {
        int set = individuals.getSetOf(index);
        PreferenceList a;
        Expression e;
        if (set == 0) {
            a = new PreferenceList(this.setSizes.get(set + 1), this.setSizes.get(set));
            if (this.expressions.get(set) == null) {
                return this.getPreferenceListByDefault(index);
            }
            e = this.expressions.get(set);
            for (int i = this.setSizes.get(set); i < numberOfIndividuals; i++) {
                e.setVariables(this.getVariableValuesForSet(set, index, i));
                double totalScore = e.evaluate();
                a.add(totalScore);
            }
        } else {
            a = new PreferenceList(this.setSizes.get(set), 0);
            if (this.expressions.get(set + 1) == null) {
                return this.getPreferenceListByDefault(index);
            }
            e = this.expressions.get(set + 1);
            for (int i = 0; i < this.setSizes.get(set); i++) {
                e.setVariables(this.getVariableValuesForSet(set + 1, index, i));
                double totalScore = e.evaluate();
                a.add(totalScore);
            }
        }
        a.sort();
        return a;
    }

    @Override
    public PreferenceList getPreferenceListByDefault(int index) {
        int set = individuals.getSetOf(index);
        int numberOfProperties = individuals.getNumberOfProperties();
        PreferenceList a;
        if (set == 0) {
            a = new PreferenceList(this.setSizes.get(set + 1), this.setSizes.get(set));
            for (int i = this.setSizes.get(0); i < numberOfIndividuals; i++) {
                double totalScore = 0;
                for (int j = 0; j < numberOfProperties; j++) {
                    double PropertyValue = individuals.getPropertyValueOf(i, j);
                    Requirement requirement = individuals.getRequirementOf(index, j);
                    double PropertyWeight = individuals.getPropertyWeightOf(index, j);
                    totalScore += getDefaultScaling(requirement, PropertyValue) * PropertyWeight;
                }
                a.add(totalScore);
            }
        } else {
            a = new PreferenceList(this.setSizes.get(set - 1), 0);
            for (int i = 0; i < this.setSizes.get(set - 1); i++) {
                double totalScore = 0;
                for (int j = 0; j < numberOfProperties; j++) {
                    double PropertyValue = individuals.getPropertyValueOf(i, j);
                    Requirement requirement = individuals.getRequirementOf(index, j);
                    double PropertyWeight = individuals.getPropertyWeightOf(index, j);
                    totalScore += getDefaultScaling(requirement, PropertyValue) * PropertyWeight;
                }
                a.add(totalScore);
            }
        }
        a.sort();
        return a;
    }




}
