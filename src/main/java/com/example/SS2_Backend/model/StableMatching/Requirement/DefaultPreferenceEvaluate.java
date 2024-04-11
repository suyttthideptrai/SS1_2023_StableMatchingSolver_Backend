package com.example.SS2_Backend.model.StableMatching.Requirement;

import com.example.SS2_Backend.model.StableMatching.Individual;
import com.example.SS2_Backend.model.StableMatching.PreferenceList;

import java.util.List;
import java.util.Objects;

public class DefaultPreferenceEvaluate {
    public static PreferenceList getPreferenceListByDefaultt(List<Individual> Individuals, int index) {
        PreferenceList a = new PreferenceList();
        int set = Individuals.get(index).getIndividualSet();
        int numberOfIndividuals = Individuals.size();
        int numberOfProperties = Individuals.get(0).getNumberOfProperties();
        for (int i = 0; i < numberOfIndividuals; i++) {
            if (Individuals.get(i).getIndividualSet() != set) {
                double totalScore = 0;
                for (int j = 0; j < numberOfProperties; j++) {
                    Double PropertyValue = Individuals.get(i).getPropertyValue(j);
                    Requirement requirement = Individuals.get(index).getRequirement(j);
                    int PropertyWeight = Individuals.get(index).getPropertyWeight(j);
                    totalScore += getScale(requirement, PropertyValue) * PropertyWeight;
                }
                // Add
                a.add(new PreferenceList.IndexValue(i, totalScore));
            }
        }
        return a;
    }

    public static double getScale(Requirement requirement, double PropertyValue) {
        int type = requirement.getType();
        // Case: Scale
        if (type == 0) {
            int TargetValue = requirement.getTargetValue();
            if (PropertyValue < 0 || PropertyValue > 10) {
                return 0.0;
            } else {
                double Distance = Math.abs(PropertyValue - TargetValue);
                if(Distance > 7) return 0;
                if(Distance > 5) return 1;
                return (10 - Distance) / 10 + 1;
            }
            //Case: 1 Bound
        } else if (type == 1) {
            double Bound = requirement.getBound();
            String expression = requirement.getExpression();
            if (Objects.equals(expression, "++")) {
                if (PropertyValue < Bound) {
                    return 0.0;
                } else {
                    if(Bound == 0) return 2.0;
                    double distance = Math.abs(PropertyValue - Bound);
                    return (Bound + distance) / Bound;
                }
            } else {
                if (PropertyValue > Bound) {
                    return 0.0;
                } else {
                    if(Bound == 0) return  2.0;
                    double distance = Math.abs(PropertyValue - Bound);
                    return (Bound + distance) / Bound;
                }
            }
            //Case: 2 Bounds
        } else {
            double lowerBound = requirement.getLowerBound();
            double upperBound = requirement.getUpperBound();
            if (PropertyValue < lowerBound || PropertyValue > upperBound || lowerBound == upperBound) {
                return 0.0;
            }else{
                double diff = Math.abs(upperBound - lowerBound)/2;
                double distance = Math.abs(((lowerBound+upperBound)/2) -PropertyValue);
                return (diff-distance)/diff + 1;
            }
        }
    }
}
