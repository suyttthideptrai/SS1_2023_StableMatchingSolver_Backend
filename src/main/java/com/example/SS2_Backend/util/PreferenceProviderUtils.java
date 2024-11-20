package com.example.SS2_Backend.util;

import com.example.SS2_Backend.model.stableMatching.Requirement.Requirement;
import com.example.SS2_Backend.ss.smt.preference.PreferenceProvider;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.*;

@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class PreferenceProviderUtils {

    PreferenceProvider preferenceProvider;

    public void setEvaluateFunctionForSet1(String EvaluateFunction1) {
        if (preferenceProvider.getExpressionOfSet1() != null) return;
        preferenceProvider.setVariablesOfSet1(filterVariable(EvaluateFunction1));
        preferenceProvider.setExpressionOfSet1(new ExpressionBuilder(EvaluateFunction1)
                .variables(convertMapToSet(preferenceProvider.getVariablesOfSet1()))
                .build());
    }

    public void setEvaluateFunctionForSet2(String EvaluateFunction2) {
        if (preferenceProvider.getExpressionOfSet2() != null) return;
        preferenceProvider.setVariablesOfSet2(filterVariable(EvaluateFunction2));
        preferenceProvider.setExpressionOfSet2(new ExpressionBuilder(EvaluateFunction2)
                .variables(convertMapToSet(preferenceProvider.getVariablesOfSet2()))
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

    public double getDefaultScaling(Requirement requirement, double propertyValue) {
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
