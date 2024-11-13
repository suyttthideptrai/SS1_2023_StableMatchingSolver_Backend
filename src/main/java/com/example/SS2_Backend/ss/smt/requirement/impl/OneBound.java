package com.example.SS2_Backend.ss.smt.requirement.impl;

import com.example.SS2_Backend.ss.smt.requirement.Requirement;
import lombok.Getter;

import static com.example.SS2_Backend.util.NumberUtils.formatDouble;

@Getter
public class OneBound extends Requirement {

    @Getter
    private final double bound;
    private final boolean expression;

    public OneBound(double bound, boolean expression) {
        super(1);
        this.bound = bound;
        this.expression = expression;
    }

    private String expressionToString(boolean expression) {
        return expression ? "++" : "--";
    }

    @Override
    public double getValueForFunction() {
        return bound;
    }

    public String toString() {
        return "[" + formatDouble(bound) + ", " + expressionToString(expression) + "]";
    }

}
