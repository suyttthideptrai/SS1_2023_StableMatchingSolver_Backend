package com.example.SS2_Backend.ss.smt.problem.impl;

import com.example.SS2_Backend.ss.smt.match.Matches;
import com.example.SS2_Backend.ss.smt.preference.impl.NewProvider;
import com.example.SS2_Backend.ss.smt.problem.MatchingProblem;
import org.moeaframework.core.Variable;

public class OTOProblem extends MatchingProblem {

    public OTOProblem(
            String problemName, String[] evaluateFunctions, String fitnessFunction,
            NewProvider preferencesProvider,
            boolean f1Status, boolean f2Status, boolean fnfStatus,
            String[][] individualRequirements, double[][] individualWeights, double[][] individualProperties,
            int numberOfIndividuals,
            int[] individualSetIndices, int[] individualCapacities) {
        super(problemName, evaluateFunctions, fitnessFunction, preferencesProvider, f1Status, f2Status, fnfStatus, individualRequirements, individualWeights, individualProperties, numberOfIndividuals, individualSetIndices, individualCapacities);
    }

    @Override
    protected Matches stableMatching(Variable var) {
        return null;
    }
}
