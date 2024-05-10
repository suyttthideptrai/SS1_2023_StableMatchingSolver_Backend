package com.example.SS2_Backend.model.StableMatching.Requirement;

import lombok.Getter;

import static com.example.SS2_Backend.util.Utils.formatDouble;

@Getter
public class TwoBoundRequirement extends Requirement {
    private final double lowerBound;
    private final double upperBound;
    public TwoBoundRequirement(double lowerBound, double upperBound) {
        super(2);
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    @Override
    public double getValueForFunction() {
        return (lowerBound + upperBound) / 2;
    }

    public String toString(){
        return "[" + formatDouble(lowerBound) + ", " + formatDouble(upperBound) + "]";
    }
}
