package com.example.SS2_Backend.ss.smt.problem.impl;

import com.example.SS2_Backend.model.stableMatching.Matches.MatchesOTO;
import com.example.SS2_Backend.ss.smt.IndividualList;
import com.example.SS2_Backend.ss.smt.evaluator.FitnessEvaluator;
import com.example.SS2_Backend.ss.smt.match.Matches;
import com.example.SS2_Backend.ss.smt.preference.PreferenceList;
import com.example.SS2_Backend.ss.smt.preference.impl.provider.NewProvider;
import com.example.SS2_Backend.ss.smt.problem.MatchingProblem;
import org.moeaframework.core.Variable;
import org.moeaframework.core.variable.Permutation;

import java.util.*;
import java.util.stream.Collectors;

public class OTOProblem extends MatchingProblem {

    IndividualList individuals;

    public OTOProblem(String problemName,
                      IndividualList individuals,
                      String[] evaluateFunctions,
                      String fitnessFunction,
                      NewProvider preferencesProvider,
                      List<PreferenceList> preferenceLists,
                      String[][] individualRequirements,
                      double[][] individualWeights,
                      double[][] individualProperties,
                      int numberOfIndividuals,
                      int setNum,
                      int[] individualCapacities,
                      FitnessEvaluator fitnessEvaluator) {
        super(
                problemName,
                evaluateFunctions,
                fitnessFunction,
                preferencesProvider,
                preferenceLists,
                individualRequirements,
                individualWeights,
                individualProperties,
                numberOfIndividuals,
                setNum,
                individualCapacities,
                fitnessEvaluator);
        this.individuals = individuals;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Matches stableMatching(Variable var) {
        int[] order = ((Permutation) var).toArray();
        Queue<Integer> singleQueue = Arrays.stream(order).boxed().collect(Collectors.toCollection(LinkedList::new));
        int[] matches = new int[getProblemSize()];
        for (int i = 0; i < getProblemSize(); i++) matches[i] = -1;
        Set<Integer> matched = new HashSet<>();
        Set<Integer> leftOver = new HashSet<>();
        while(!singleQueue.isEmpty()){
            int a = singleQueue.poll();
            if (matched.contains(a)) continue;
            PreferenceList aPreference = getPreferenceLists().get(a);
            int prefLen = aPreference.size();
            for (int i = 0; i < prefLen; i++) {
                int b = aPreference.getIndexByPosition(i);
                if (matches[a] == b && matches[b] == a) break;
                if (!matched.contains(b)) {
                    matched.add(a);
                    matched.add(b);
                    matches[a] = b;
                    matches[b] = a;
                    break;
                } else {
                    int bPartner = matches[b];
                    if (bLikeAMore(a, b, bPartner)) {
                        singleQueue.add(bPartner);
                        matched.remove(bPartner);
                        matches[bPartner] = -1;
                        matched.add(a);
                        matches[a] = b;
                        matches[b] = a;
                        break;
                    } else if (i == prefLen - 1) {
                        leftOver.add(a);
                    }
                }
            }
        }
        return new MatchesOTO(matches, leftOver);
    }

    private boolean bLikeAMore(int a, int b, int c) {
        return getPreferenceLists().get(b).isScoreGreater(a, c);
    }
}
