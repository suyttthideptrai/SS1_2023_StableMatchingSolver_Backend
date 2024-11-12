package com.example.SS2_Backend.ss.smt.evaluator;

import com.example.SS2_Backend.ss.smt.match.Matches;

public interface FitnessEvaluator {
    double[] getAllSatisfactions(Matches matches);
    double defaultFitnessEvaluation(double[] satisfactions);
    double withFitnessFunctionEvaluation(double[] satisfactions, String fnf);
    boolean fnfStatus();
    String getFitnessFunction();
}
