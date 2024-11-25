package com.example.SS2_Backend.model.stableMatching.Timestamp;

import com.example.SS2_Backend.model.stableMatching.Requirement.OneBoundRequirement;
import com.example.SS2_Backend.model.stableMatching.Requirement.Requirement;
import com.example.SS2_Backend.model.stableMatching.Requirement.TwoBoundRequirement;
import lombok.Getter;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.SS2_Backend.util.Utils.isDouble;
import static com.example.SS2_Backend.util.Utils.isInteger;

public class PropertyDate {
    private static final boolean INCREMENT = true;
    private static final boolean DECREMENT = false;
    private final double value;
    private final double weight;
    @Getter
    private final Requirement requirement;

    private Timestamp analyzer;


    public PropertyDate(double value, double weight, String[] inputRequirement) {
        this.value = value;
        this.weight = weight;
        this.requirement = setRequirement(inputRequirement);
    }

    public Requirement setRequirement(String[] array) {
        try {
            if (Objects.equals(array[1], "++")) {
                return new OneBoundRequirement(Double.parseDouble(array[0]), INCREMENT);
            } else if (Objects.equals(array[1], "--")) {
                return new OneBoundRequirement(Double.parseDouble(array[0]), DECREMENT);
            } else if (Objects.equals(array[1], null)) {
                if (isInteger(array[0])) {
                    return new ScaleTargetRequirementExtra(Integer.parseInt(array[0]));
                } else if (isDouble(array[0])) {
                    return new OneBoundRequirement(Double.parseDouble(array[0]), INCREMENT);
                } else if(isValidFormat(array[0])) {
                    double set = analyzer.calculateFitness();
                    analyzer.parseInput(array[0]); // 0 1 2 3 4

                    return new ScaleTargetRequirementExtra(set);
                }
                else {
                    return new OneBoundRequirement(0.0, INCREMENT);
                }

            } else {
                double value1 = Double.parseDouble(array[0]);
                double value2 = Double.parseDouble(array[1]);
                return new TwoBoundRequirement(value1, value2);
            }
        } catch (NumberFormatException e) {
            return new OneBoundRequirement(0.0, INCREMENT);
        }
    }

    public static boolean isValidFormat(String input) {
        String regex = "\\[\\d+(, \\d+)*](, \\[\\d+(, \\d+)*])*";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        return matcher.matches();
    }


    @Override
    public String toString() {
        return "Value: " + value + " , Requirement: " + requirement + " , Weight: " + weight;
    }

}