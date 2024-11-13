package com.example.SS2_Backend.ss.smt.requirement.impl;

import com.example.SS2_Backend.ss.smt.requirement.Requirement;
import lombok.Getter;

import static com.example.SS2_Backend.util.NumberUtils.formatDouble;

@Getter
public class TwoBound extends Requirement {

    private final double lowerBound;
    private final double upperBound;

    public TwoBound(double lowerBound, double upperBound) {
        super(2);
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    @Override
    public double getValueForFunction() {
        return (lowerBound + upperBound) / 2;
    }

    public String toString() {
        return "[" + formatDouble(lowerBound) + ", " + formatDouble(upperBound) + "]";
    }

}
