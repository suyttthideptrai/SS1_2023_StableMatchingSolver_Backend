package com.example.SS2_Backend.ss.smt.evaluator.impl;

import com.example.SS2_Backend.ss.smt.evaluator.FitnessEvaluator;
import com.example.SS2_Backend.ss.smt.match.Matches;

public class MTMProblem implements FitnessEvaluator {
    @Override
    public double[] getAllSatisfactions(Matches matches) {
        return new double[0];
    }

    @Override
    public double defaultFitnessEvaluation(double[] satisfactions) {
        return 0;
    }

    @Override
    public double withFitnessFunctionEvaluation(double[] satisfactions, String fnf) {
        return 0;
    }

    @Override
    public boolean fnfStatus() {
        return false;
    }

    @Override
    public String getFitnessFunction() {
        return "";
    }
}
