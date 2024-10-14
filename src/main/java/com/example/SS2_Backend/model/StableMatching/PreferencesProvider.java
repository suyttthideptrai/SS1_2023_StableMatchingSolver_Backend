package com.example.SS2_Backend.model.StableMatching;

import com.example.SS2_Backend.model.StableMatching.Requirement.Requirement;
import lombok.Getter;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.*;

public class PreferencesProvider {

    private final IndividualList individuals;
    private final int numberOfIndividuals;

    private final Map<Integer, Integer> setSizes;

    @Getter
    private final Map<Integer, Expression> expressions;
    private final Map<Integer, Map<String, Set<Integer>>> variables;


    public PreferencesProvider(IndividualList individuals) {
        this.individuals = individuals;
        this.numberOfIndividuals = individuals.getNumberOfIndividual();
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
                case 'P':
                case 'W':
                case 'R':
                    String prefix = String.valueOf(ch);
                    Optional<Integer> nextIdx = getNextIndexToken(evaluateFunction, c);
                    if (nextIdx.isPresent()) {
                        int idx = nextIdx.get();
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
                    } else {
                        throw new IllegalArgumentException("Invalid expression after: " + prefix);
                    }
            }
        }
        return variables;
    }

    public Optional<Integer> getNextIndexToken(String evaluateFunction, int currentIndex) {
        int nextIndex = currentIndex + 1;
        while (nextIndex < evaluateFunction.length() &&
                Character.isDigit(evaluateFunction.charAt(nextIndex))) {
            nextIndex++;
        }
        if (nextIndex == currentIndex + 1) {
            return Optional.empty();
        }
        String subString = evaluateFunction.substring(currentIndex + 1, nextIndex);
        int idx = Integer.parseInt(subString);
        return Optional.of(idx);
    }

    public Map<String, Double> getVariableValuesForSet(int set,int indexOfEvaluator,
                                                        int indexOfBeEvaluated) {
        return getVariableValues(this.variables.get(set), indexOfEvaluator, indexOfBeEvaluated);
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
                        double val = individuals.getPropertyValueOf(idx2, value - 1);
                        variablesValues.put(key + value, val);
                    }
                    break;
                case "W":
                    for (Integer value : values) {
                        double val = individuals.getPropertyWeightOf(idx1, value - 1);
                        variablesValues.put(key + value, val);
                    }
                    break;
                case "R":
                    for (Integer value : values) {
                        double val = individuals
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

    public static double getDefaultScaling(Requirement requirement, double propertyValue) {
        int type = requirement.getType();
        // Case: Scale
        if (type == 0) {
            int targetValue = requirement.getTargetValue();
            if (propertyValue < 0 || propertyValue > 10) {
                return 0.0;
            } else {
                double Distance = Math.abs(propertyValue - targetValue);
                if (Distance > 7) return 0;
                if (Distance > 5) return 1;
                return (10 - Distance) / 10 + 1;
            }
            //Case: 1 Bound
        } else if (type == 1) {
            double Bound = requirement.getBound();
            String expression = requirement.getExpression();
            if (Objects.equals(expression, "++")) {
                if (propertyValue < Bound) {
                    return 0.0;
                } else {
                    if (Bound == 0) return 2.0;
                    double distance = Math.abs(propertyValue - Bound);
                    return (Bound + distance) / Bound;
                }
            } else {
                if (propertyValue > Bound) {
                    return 0.0;
                } else {
                    if (Bound == 0) return 2.0;
                    double distance = Math.abs(propertyValue - Bound);
                    return (Bound + distance) / Bound;
                }
            }
            //Case: 2 Bounds
        } else {
            double lowerBound = requirement.getLowerBound();
            double upperBound = requirement.getUpperBound();
            if (propertyValue < lowerBound || propertyValue > upperBound ||
                    lowerBound == upperBound) {
                return 0.0;
            } else {
                double diff = Math.abs(upperBound - lowerBound) / 2;
                double distance = Math.abs(((lowerBound + upperBound) / 2) - propertyValue);
                return (diff - distance) / diff + 1;
            }
        }
    }

}
