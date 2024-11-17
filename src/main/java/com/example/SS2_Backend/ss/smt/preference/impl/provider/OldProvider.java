package com.example.SS2_Backend.ss.smt.preference.impl.provider;

import com.example.SS2_Backend.model.stableMatching.IndividualList;
import com.example.SS2_Backend.model.stableMatching.PreferenceList;
import com.example.SS2_Backend.model.stableMatching.Requirement.Requirement;
import com.example.SS2_Backend.ss.smt.preference.PreferenceProvider;
import com.example.SS2_Backend.util.PreferenceProviderUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import net.objecthunter.exp4j.Expression;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Data
public class OldProvider extends PreferenceProvider {
    IndividualList individuals;
    PreferenceProviderUtils preferenceProviderUtils;

    public OldProvider(IndividualList individuals, PreferenceProviderUtils preferenceProviderUtils) {
        this.individuals = individuals;
        this.preferenceProviderUtils = preferenceProviderUtils; // likely to be circular dependency, need to workaround to define the way to inject this dependency
        setNumberOfIndividuals(individuals.getNumberOfIndividual());
        setSizeOf1(individuals.getNumberOfIndividualForSet0());
        setSizeOf2(getNumberOfIndividuals() - getSizeOf1());
    }

    @Override
    protected Map<String, Double> getVariableValues(Map<String, Set<Integer>> variables, int idx1, int idx2) {
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

    @Override
    protected PreferenceList getPreferenceListByDefault(int index) {
        int set = individuals.getSetOf(index);
        PreferenceList a;
        Expression e;
        if (set == 0) {
            a = new PreferenceList(getSizeOf2(), getSizeOf1());
            if (getExpressionOfSet1() == null) {
                return getPreferenceListByDefault(index);
            }
            e = getExpressionOfSet1();
            for (int i = getSizeOf1(); i < getNumberOfIndividuals(); i++) {
                e.setVariables(getVariableValuesForSet1(index, i));
                double totalScore = e.evaluate();
                a.add(totalScore);
            }
        } else {
            a = new PreferenceList(getSizeOf1(), 0);
            if (getExpressionOfSet2() == null) {
                return getPreferenceListByDefault(index);
            }
            e = getExpressionOfSet2();
            for (int i = 0; i < getSizeOf1(); i++) {
                e.setVariables(getVariableValuesForSet2(index, i));
                double totalScore = e.evaluate();
                a.add(totalScore);
            }
        }
        a.sort();
        return a;
    }

    @Override
    protected PreferenceList getPreferenceListByFunction(int index) {
        int set = individuals.getSetOf(index);
        int numberOfProperties = individuals.getNumberOfProperties();
        PreferenceList a;
        if (set == 0) {
            a = new PreferenceList(getSizeOf2(), getSizeOf1());
            for (int i = getSizeOf1(); i < getNumberOfIndividuals(); i++) {
                double totalScore = 0;
                for (int j = 0; j < numberOfProperties; j++) {
                    double PropertyValue = individuals.getPropertyValueOf(i, j);
                    Requirement requirement = individuals.getRequirementOf(index, j);
                    double PropertyWeight = individuals.getPropertyWeightOf(index, j);
                    totalScore += preferenceProviderUtils.getDefaultScaling(requirement, PropertyValue) * PropertyWeight;
                }
                // Add
                a.add(totalScore);
            }
        } else {
            a = new PreferenceList(getSizeOf1(), 0);
            for (int i = 0; i < getSizeOf1(); i++) {
                double totalScore = 0;
                for (int j = 0; j < numberOfProperties; j++) {
                    double PropertyValue = individuals.getPropertyValueOf(i, j);
                    Requirement requirement = individuals.getRequirementOf(index, j);
                    double PropertyWeight = individuals.getPropertyWeightOf(index, j);
                    totalScore += preferenceProviderUtils.getDefaultScaling(requirement, PropertyValue) * PropertyWeight;
                }
                // Add
                a.add(totalScore);
            }
        }
        a.sort();
        return a;
    }
}
