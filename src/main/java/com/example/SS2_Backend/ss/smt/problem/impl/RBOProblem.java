package com.example.SS2_Backend.ss.smt.problem.impl;

import com.example.SS2_Backend.model.stableMatching.PreferenceList;
import com.example.SS2_Backend.ss.smt.match.Matches;
import com.example.SS2_Backend.ss.smt.preference.impl.NewProvider;
import com.example.SS2_Backend.ss.smt.problem.MatchingProblem;
import org.moeaframework.core.Variable;
import org.moeaframework.core.variable.Permutation;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class RBOProblem extends MatchingProblem {

    public RBOProblem(
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
        com.example.SS2_Backend.model.stableMatching.Matches.Matches matches = new com.example.SS2_Backend.model.stableMatching.Matches.Matches(numberOfIndividuals);
        Set<Integer> MatchedNode = new HashSet<>();
        Permutation castVar = (Permutation) var;
        int[] decodeVar = castVar.toArray();
        Queue<Integer> UnMatchedNode = new LinkedList<>();
        for (int val : decodeVar) {
            UnMatchedNode.add(val);
        }

        while (!UnMatchedNode.isEmpty()) {
            int newNode;
            newNode = UnMatchedNode.poll();

            if (MatchedNode.contains(newNode)) {
                continue;
            }
            PreferenceList nodePreference = super.getPreferenceLists().get(newNode);
//			int padding = getPaddingOf(Node);
            //Loop through LeftNode's preference list to find a Match
            for (int i = 0; i < nodePreference.size(); i++) {
                //Next Match (RightNode) is found on the list
                int preferNode = nodePreference.getIndexByPosition(i);
                if (matches.isAlreadyMatch(preferNode, newNode)) {
                    break;
                }
                //If the RightNode Capacity is not full -> create connection between LeftNode - RightNode
                if (!matches.isFull(preferNode, super.getIndividualCapacities()[preferNode])) {
                    matches.addMatch(preferNode, newNode);
                    matches.addMatch(newNode, preferNode);
                    MatchedNode.add(preferNode);
                    break;
                } else {
                    //If the RightNode's Capacity is Full then Left Node will Compete with Nodes that are inside RightNode
                    //Loser will be the return value
                    //System.out.println(preferNode + " is full! Begin making a Compete game involve: " + Node + " ..." );

                    int Loser = getLeastScoreNode(preferNode,
                            newNode,
                            matches.getIndividualMatches(preferNode));

                    //If RightNode is the LastChoice of Loser -> then
                    // Loser will be terminated and Saved in Matches.LeftOvers Container
                    //System.out.println("Found Loser: " + Loser);
                    if (Loser == newNode) {
                        if (getLastChoiceOf(newNode) == preferNode) {
                            //System.out.println(Node + " has nowhere to go. Go to LeftOvers!");
                            matches.addLeftOver(Loser);
                            break;
                        }
                        //Or else Loser go back to UnMatched Queue & Waiting for it's Matching Procedure
                    } else {
                        matches.disMatch(preferNode, Loser);
                        matches.disMatch(Loser, preferNode);
                        UnMatchedNode.add(Loser);
                        MatchedNode.remove(Loser);
                        //System.out.println(Loser + " lost the game, waiting for another chance.");
                        matches.addMatch(preferNode, newNode);
                        matches.addMatch(newNode, preferNode);
                        MatchedNode.add(newNode);
                        //System.out.println(Node + " is more suitable than " + Loser + " matched with " + preferNode);
                        break;
                    }
                }
            }
        }
        return matches;
    }
}
