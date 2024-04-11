package com.example.SS2_Backend.model.StableMatching;

import lombok.Getter;
import lombok.Setter;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.*;

import static com.example.SS2_Backend.model.StableMatching.Requirement.DefaultPreferenceEvaluate.getPreferenceListByDefaultt;

public class PreferencesProvider {
    @Setter
    private List<Individual> individuals;
    @Getter
    private Expression expressionOfSet1;
    @Getter
    private Expression expressionOfSet2;
    private Map<String, Set<Integer>> variablesOfSet1;
    private Map<String, Set<Integer>> variablesOfSet2;

    public PreferencesProvider() {
    }

    public void setEvaluateFunctionForSet1(String EvaluateFunction1) {
        if(expressionOfSet1 != null) return;
        this.variablesOfSet1 = filterVariable(EvaluateFunction1);
        this.expressionOfSet1 = new ExpressionBuilder(EvaluateFunction1)
                .variables(convertMapToSet(variablesOfSet1)).build();
    }

    public void setEvaluateFunctionForSet2(String EvaluateFunction2) {
        if(expressionOfSet2 != null) return;
        this.variablesOfSet2 = filterVariable(EvaluateFunction2);
        this.expressionOfSet2 = new ExpressionBuilder(EvaluateFunction2)
                .variables(convertMapToSet(variablesOfSet2)).build();
    }

    public Set<String> convertMapToSet(Map<String, Set<Integer>> varMap) {
        Set<String> resultSet = new HashSet<>();
        for (Map.Entry<String, Set<Integer>> entry : varMap.entrySet()) {
            String variable = entry.getKey();
            for (Integer value : entry.getValue()) {
                resultSet.add(variable + value.toString());
            }
        }
        return resultSet;
    }

    public Map<String, Set<Integer>> filterVariable(String evaluateFunction) {
        Map<String, Set<Integer>> variables = new HashMap<>();
        for (int c = 0; c < evaluateFunction.length(); c++) {
            char ch = evaluateFunction.charAt(c);
            switch (ch) {
                case 'p':
                case 'w':
                case 'r':
                    String prefix = String.valueOf(ch);
                    Optional<Integer> nextIdx = getNextIndex(evaluateFunction, c);
                    if(nextIdx.isPresent()){
                        int idx = nextIdx.get().intValue();
                        variables.compute(prefix, (key, value) -> {
                            if (value == null) {
                                Set<Integer> set = new HashSet<>();
                                set.add(idx);
                                return set;
                            } else {
                                value.add(idx);
                                return value;
                            }
                        });
                    }else{
                        throw new IllegalArgumentException("Invalid expression after: " + prefix);
                    }
            }
        }
        return variables;
    }

    public Optional<Integer> getNextIndex(String evaluateFunction, int currentIndex) {
        int nextIndex = currentIndex + 1;
        while (nextIndex < evaluateFunction.length() && Character.isDigit(evaluateFunction.charAt(nextIndex))) {
            nextIndex++;
        }
        if (nextIndex == currentIndex + 1) {
            return Optional.empty();
        }
        String subString = evaluateFunction.substring(currentIndex + 1, nextIndex);
        int idx = Integer.parseInt(subString);
        return Optional.of(idx);
    }

    public Map<String, Double> getVariableValuesForSet1(int indexOfEvaluator, int indexOfBeEvaluate){
        return getVariableValues(this.variablesOfSet1, indexOfEvaluator, indexOfBeEvaluate);
    }

    public Map<String, Double> getVariableValuesForSet2(int indexOfEvaluator, int indexOfBeEvaluate){
        return getVariableValues(this.variablesOfSet2, indexOfEvaluator, indexOfBeEvaluate);
    }

    private Map<String, Double> getVariableValues(Map<String, Set<Integer>> variables, int idx1, int idx2) {
        Map<String, Double> variablesValues = new HashMap<>();
        for (Map.Entry<String, Set<Integer>> entry : variables.entrySet()) {
            String key = entry.getKey();
            Set<Integer> values = entry.getValue();
                switch (key) {
                    case "p":
                        for(Integer value : values) {
                            double val = individuals.get(idx2).getPropertyValue(value-1);
                            variablesValues.put(key + value, val);
                        }
                        break;
                    case "w":
                        for(Integer value : values) {
                            double val = individuals.get(idx1).getPropertyWeight(value-1);
                            variablesValues.put(key + value, val);
                        }
                        break;
                    case "r":
                        for(Integer value : values){
                        double val = individuals.get(idx1).getRequirement(value-1).getValueForFunction();
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

    public PreferenceList getPreferenceListByDefault(int index){
        PreferenceList a;
        a = getPreferenceListByDefaultt(individuals, index);
        a.sort();
        return a;
    }

    public PreferenceList getPreferenceListByFunction(int index) {
        int set = individuals.get(index).getIndividualSet();
        PreferenceList a = new PreferenceList();
        Expression e;
        if(set == 0){
            if(this.expressionOfSet1 == null){
                return this.getPreferenceListByDefault(index);
            }
            e = this.expressionOfSet1;
        }else{
            if(this.expressionOfSet2 == null){
                return this.getPreferenceListByDefault(index);
            }
            e = this.expressionOfSet2;
        }
        int numberOfIndividual = individuals.size();
        for (int i = 0; i < numberOfIndividual; i++) {
            if (individuals.get(i).getIndividualSet() != set) {
                e.setVariables(set == 0 ? this.getVariableValuesForSet1(index, i) : this.getVariableValuesForSet2(index, i));
                double totalScore = e.evaluate();
                a.add(new PreferenceList.IndexValue(i, totalScore));
            }
        }
        a.sort();
        return a;
    }
}
