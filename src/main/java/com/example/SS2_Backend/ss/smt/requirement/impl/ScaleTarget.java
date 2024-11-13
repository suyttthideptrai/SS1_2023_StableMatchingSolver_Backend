package com.example.SS2_Backend.ss.smt.requirement.impl;

import com.example.SS2_Backend.ss.smt.requirement.Requirement;
import lombok.Getter;

@Getter
public class ScaleTarget extends Requirement {

    private final int targetValue;

    public ScaleTarget(int targetValue) {
        super(0);
        this.targetValue = targetValue;
    }

    @Override
    public double getValueForFunction() {
        return targetValue;
    }

    @Override
    public String toString() {
        return "[" + this.targetValue + "]";
    }

}
