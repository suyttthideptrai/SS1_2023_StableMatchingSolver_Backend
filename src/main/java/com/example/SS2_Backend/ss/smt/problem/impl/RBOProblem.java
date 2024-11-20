package com.example.SS2_Backend.ss.smt.problem.impl;

import com.example.SS2_Backend.ss.smt.evaluator.FitnessEvaluator;
import com.example.SS2_Backend.ss.smt.preference.PreferenceList;
import com.example.SS2_Backend.ss.smt.match.Matches;
import com.example.SS2_Backend.ss.smt.preference.impl.provider.NewProvider;
import com.example.SS2_Backend.ss.smt.problem.MatchingProblem;
import org.moeaframework.core.Variable;
import org.moeaframework.core.variable.Permutation;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class RBOProblem extends MatchingProblem {

    public RBOProblem(String problemName,
                      String[] evaluateFunctions,
                      String fitnessFunction,
                      NewProvider preferencesProvider,
                      String[][] individualRequirements,
                      double[][] individualWeights,
                      double[][] individualProperties,
                      int problemSize,
                      int setNum,
                      int[] individualCapacities,
                      String evaluateFunctionForSet,
                      String evaluateFunctionForSet2,
                      FitnessEvaluator fitnessEvaluator) {
        super(problemName,
                evaluateFunctions,
                fitnessFunction,
                preferencesProvider,
                individualRequirements,
                individualWeights,
                individualProperties,
                problemSize,
                setNum,
                individualCapacities,
                evaluateFunctionForSet,
                evaluateFunctionForSet2,
                fitnessEvaluator
                );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Matches stableMatching(Variable var) {

        Matches matches = new Matches(this.getProblemSize());
        Permutation castVar = (Permutation) var;
        int[] decodeVar = castVar.toArray();
        Queue<Integer> unMatchedNode = new LinkedList<>();
        for (int val : decodeVar) {
            unMatchedNode.add(val);
        }
        Set<Integer> matchedNode = new HashSet<>();

        while (!unMatchedNode.isEmpty()) {
            int newNode;
            newNode = unMatchedNode.poll();

            if (matchedNode.contains(newNode)) {
                continue;
            }

            PreferenceList newNodePreference = super.getPreferenceLists().get(newNode);
            for (int i = 0; i < newNodePreference.size(); i++) {

                int preferNode = newNodePreference.getIndexByPosition(i);

                if (matches.isMatched(preferNode, newNode)) {
                    break;
                }

                if (!matches.isFull(preferNode, super.getCapacities()[preferNode])) {
                    matches.addMatchBi(preferNode, newNode);
                    matchedNode.add(preferNode);
                    break;

                } else {

                    int loser = this.getPreferenceLists()
                            .get(preferNode)
                            .getLeastNode(newNode, matches.getSetOf(preferNode));

                    if (loser == newNode) {

                        if (newNodePreference.getLastOption() == preferNode) {
                            matches.addLeftOver(loser);
                            break;
                        }

                    } else {

                        matches.removeMatchBi(preferNode, loser);
                        matches.addMatchBi(preferNode, newNode);
                        matchedNode.remove(loser);
                        unMatchedNode.add(loser);
                        matchedNode.add(newNode);
                        break;

                    }

                }
            }
        }
        return matches;
    }
}
