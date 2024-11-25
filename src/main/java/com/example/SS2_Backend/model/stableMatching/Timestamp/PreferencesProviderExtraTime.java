package com.example.SS2_Backend.model.stableMatching.Timestamp;

import com.example.SS2_Backend.model.stableMatching.Extra.IndividualListExtra;
import com.example.SS2_Backend.model.stableMatching.Extra.PreferenceListExtra;
import com.example.SS2_Backend.model.stableMatching.Requirement.Requirement;
import lombok.Getter;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.*;


public class PreferencesProviderExtraTime {

    private IndividualListExtra individuals;
    private int numberOfIndividuals;
    private PreferenceListExtra preferenceList;
    private final Map<Integer, Integer> setSizes;

    @Getter
    private final Map<Integer, Expression> expressions;
    private final Map<Integer, Map<String, Set<Integer>>> variables;
    private int numberOfSets ;      // new

    public PreferencesProviderExtraTime(IndividualListExtra individuals, int numberOfSets) {
        this.individuals = individuals;
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

    public Map<String, Double> getVariableValuesForSet(int set, int indexOfEvaluator,
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


    public PreferenceListExtra getPreferenceListByFunction(int index) {
        int set = individuals.getSetOf(index);
        PreferenceListExtra a = new PreferenceListExtra(0, 0);
        Expression e;
        int size = 0;
        if (setSizes.containsKey(set)) {          // 1 2 3 4 5   6 7 8 9 10
            for (int setNumber : setSizes.keySet()) {
                if (setNumber != set) {
                    size += setSizes.get(set);
                }
            }
            if(set == 1) {
                a = new PreferenceListExtra(size, setSizes.get(1));    // khởi tạo preferlist với size = 2 set còn lại + vào
            } else {
                a = new PreferenceListExtra(size, 0);
            }
            if (this.expressions.get(set) == null) {
                return this.getPreferenceListByDefault(index);
            }
            e = this.expressions.get(set);


            int currentPosition = 0;

            // Xử lý từng set riêng biệt
            int tempIndex = 0;

            for (int otherSet : setSizes.keySet()) {
                if (otherSet != set) {
                    int setSize = setSizes.get(otherSet);

                    double[] tempScores = new double[setSize];
                    int[] tempPositions = new int[setSize];

                    for (int i = 0; i < numberOfIndividuals; i++) {
                        if (individuals.getSetOf(i) == otherSet) {
                            e.setVariables(this.getVariableValuesForSet(set, index, i));
                            tempScores[i] = e.evaluate();
                            tempPositions[i] = tempIndex;
                            tempIndex++;
                        }
                    }
                    // Sort mảng tạm (sử dụng hàm sort bên ngoài)
                    sortDescendingByScores(tempScores, tempPositions);
                    // Add vào PreferenceList chính
                    a.addArray(tempScores, tempPositions);


                    currentPosition += setSize; // padding
                }
            }

        }
        return a;
    }


    public PreferenceListExtra getPreferenceListByDefault(int index) {
        int set = individuals.getSetOf(index);
        int numberOfProperties = individuals.getNumberOfProperties();
        PreferenceListExtra a = new PreferenceListExtra(0, 0);
        int size = 0;

        if (setSizes.containsKey(set)) {
            for (int setNumber : setSizes.keySet()) {
                if (setNumber != set) {
                    size += setSizes.get(set);
                }
            }
            if(set == 1) {
                a = new PreferenceListExtra(size, setSizes.get(1));    // khởi tạo preferlist với size = 2 set còn lại + vào
            } else {
                a = new PreferenceListExtra(size, 0);
            }

            int tempIndex = 0;
            for (int otherSet : setSizes.keySet()) {
                if (otherSet != set) {
                    int setSize = setSizes.get(otherSet);
                    double[] tempScores = new double[setSize];
                    int[] tempPositions = new int[setSize];

                    for (int i = 0; i < numberOfIndividuals; i++) {
                        if (individuals.getSetOf(i) == otherSet) {
                            double totalScore = 0;
                            for (int j = 0; j < numberOfProperties; j++) {
                                double propertyValue = individuals.getPropertyValueOf(i, j);
                                Requirement requirement = individuals.getRequirementOf(index, j);
                                double propertyWeight = individuals.getPropertyWeightOf(index, j);
                                totalScore += getDefaultScaling(requirement, propertyValue) * propertyWeight;
                            }
                            tempScores[i] = totalScore;
                            tempPositions[i] = tempIndex;
                        }
                    }

                    sortDescendingByScores(tempScores, tempPositions);

                    a.addArray(tempScores, tempPositions);
                }
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


    private void sortDescendingByScores(double[] tempScores, int[] tempPositions) {
        int size = tempScores.length;

        // Build min heap
        for (int i = size / 2 - 1; i >= 0; i--) {
            heapify(tempScores, tempPositions, size, i);
        }

        // Extract elements from heap one by one
        for (int i = size - 1; i > 0; i--) {
            // Move current root to end
            double tempScore = tempScores[0];
            int tempPos = tempPositions[0];

            tempScores[0] = tempScores[i];
            tempPositions[0] = tempPositions[i];

            tempScores[i] = tempScore;
            tempPositions[i] = tempPos;

            // Call min heapify on the reduced heap
            heapify(tempScores, tempPositions, i, 0);
        }
    }

    private void heapify(double[] tempScores, int[] tempPositions, int heapSize, int rootIndex) {
        int smallestIndex = rootIndex;
        int leftChildIndex = 2 * rootIndex + 1;
        int rightChildIndex = 2 * rootIndex + 2;

        // If left child is smaller than root
        if (leftChildIndex < heapSize && tempScores[leftChildIndex] < tempScores[smallestIndex]) {
            smallestIndex = leftChildIndex;
        }

        // If right child is smaller than smallest so far
        if (rightChildIndex < heapSize && tempScores[rightChildIndex] < tempScores[smallestIndex]) {
            smallestIndex = rightChildIndex;
        }

        // If smallest is not root
        if (smallestIndex != rootIndex) {
            // Swap scores
            double swapScore = tempScores[rootIndex];
            tempScores[rootIndex] = tempScores[smallestIndex];
            tempScores[smallestIndex] = swapScore;

            // Swap positions
            int swapPos = tempPositions[rootIndex];
            tempPositions[rootIndex] = tempPositions[smallestIndex];
            tempPositions[smallestIndex] = swapPos;

            // Recursively heapify the affected sub-tree
            heapify(tempScores, tempPositions, heapSize, smallestIndex);
        }
    }


}
