package com.example.SS2_Backend.ss.smt.evaluator.impl;

import com.example.SS2_Backend.ss.smt.evaluator.FitnessEvaluator;
import com.example.SS2_Backend.ss.smt.match.Matches;
import com.example.SS2_Backend.ss.smt.preference.PreferenceList;
import com.example.SS2_Backend.ss.smt.problem.MatchingProblem;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class DefaultFitnessEvaluator implements FitnessEvaluator {

    MatchingProblem matchingProblem;

    @Override
    public double[] getAllSatisfactions(Matches matches, List<PreferenceList> preferenceLists) {
        double[] satisfactions = new double[matchingProblem.getProblemSize()];
        int numSet0 = matchingProblem.getPreferencesProvider().getNumberOfIndividualForSet0();
        for (int i = 0; i < numSet0; i++) {
            double setScore = 0.0;
            PreferenceList ofInd = preferenceLists.get(i);
            Set<Integer> SetMatches = matches.getSet(i);
            for (int x : SetMatches) {
                setScore += ofInd.getScoreByIndex(x);
            }
            satisfactions[i] = setScore;
        }
        for (int i = numSet0; i < numberOfIndividuals; i++) {
            double setScore = 0.0;
            PreferenceList ofInd = preferenceLists.get(i);
            Set<Integer> SetMatches = matches.getSet(i);
            for (int x : SetMatches) {
                setScore += ofInd.getScoreByIndex(x);
            }
            satisfactions[i] = setScore;
        }
        return satisfactions;
    }

    @Override
    public double defaultFitnessEvaluation(double[] satisfactions) {
        return 0;
    }

    @Override
    public double withFitnessFunctionEvaluation(double[] satisfactions, String fnf) {
        return 0;
    }
}
