package com.example.SS2_Backend.ss.smt.evaluator.impl;

import com.example.SS2_Backend.ss.smt.preference.PreferenceList;
import com.example.SS2_Backend.ss.smt.evaluator.FitnessEvaluator;
import com.example.SS2_Backend.ss.smt.match.Matches;

import java.util.List;

public class OTMProblem implements FitnessEvaluator {
    @Override
    public double[] getAllSatisfactions(Matches matches, List<PreferenceList> preferenceLists) {
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
    public String getFitnessFunction() {
        return "";
    }
}
