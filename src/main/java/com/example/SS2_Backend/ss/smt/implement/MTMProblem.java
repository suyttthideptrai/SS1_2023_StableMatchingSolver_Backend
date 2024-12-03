package com.example.SS2_Backend.ss.smt.implement;

import com.example.SS2_Backend.constants.MatchingConst;
import com.example.SS2_Backend.ss.smt.preference.PreferenceList;
import com.example.SS2_Backend.ss.smt.MatchingData;
import com.example.SS2_Backend.ss.smt.MatchingProblem;
import com.example.SS2_Backend.ss.smt.evaluator.FitnessEvaluator;
import com.example.SS2_Backend.ss.smt.Matches;
import com.example.SS2_Backend.ss.smt.preference.PreferenceListWrapper;
import com.example.SS2_Backend.util.StringUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.variable.Permutation;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

@Slf4j
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class MTMProblem implements MatchingProblem {

    /** problem name */
    final String problemName;

    /** problem size (number of individuals in matching problem */
    final int problemSize;

    /** number of set in matching problem */
    final int setNum;

    /** Matching data */
    final MatchingData matchingData;

    /** preference list  */
    final PreferenceListWrapper preferenceLists;

    /** problem fitness function */
    final String fitnessFunction;

    /** fitness evaluator */
    final FitnessEvaluator fitnessEvaluator;

    /** will not be used */
    final int UNUSED_VAL = MatchingConst.UNUSED_VALUE;



    /**
     * generate new solution
     * @return Solution contains Variable(s)
     */
    @Override
    public Solution newSolution() {
        Solution solution = new Solution(1, 1);
        Permutation permutationVar = new Permutation(problemSize);
        solution.setVariable(0, permutationVar);
        return solution;
    }

    /**
     * evaluate function for matching problem
     * @param solution Solution contains Variable(s)
     */
    @Override
    public void evaluate(Solution solution) {
        Matches result = this.stableMatching(solution.getVariable(0));
        // Check Exclude Pairs
        int[][] excludedPairs = this.matchingData.getExcludedPairs();
        if (Objects.nonNull(excludedPairs)) {
            for (int[] excludedPair : excludedPairs) {
                if (result.getSetOf(excludedPair[0]).contains(excludedPair[1])) {
                    solution.setObjective(0, Double.MAX_VALUE);
                    return;
                }
            }
        }
        double[] satisfactions = this.preferenceLists.getMatchesSatisfactions(result, matchingData);
        double fitnessScore;
        if (this.hasFitnessFunc()) {
            fitnessScore = fitnessEvaluator
                    .withFitnessFunctionEvaluation(satisfactions, this.fitnessFunction);
        } else {
            fitnessScore = fitnessEvaluator.defaultFitnessEvaluation(satisfactions);
        }
        solution.setAttribute(MatchingConst.MATCHES_KEY, result);
        solution.setObjective(0, -fitnessScore);
    }


    /**
     * check exists fitness function
     * @return true if exists
     */
    public boolean hasFitnessFunc() {
        return StringUtils.isEmptyOrNull(this.fitnessFunction);
    }


    public double[] getMatchesSatisfactions(Matches matches) {
        return this.preferenceLists.getMatchesSatisfactions(matches, matchingData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Matches stableMatching(Variable var) {

        Matches matches = new Matches(matchingData.getSize());
        Set<Integer> matchedNode = new HashSet<>();
        Permutation castVar = (Permutation) var;
        int[] decodeVar = castVar.toArray();
        Queue<Integer> unMatchedNode = new LinkedList<>();

        for (int val : decodeVar) {
            unMatchedNode.add(val);
        }

        while (!unMatchedNode.isEmpty()) {
            int newNode;
            newNode = unMatchedNode.poll();

            if (matchedNode.contains(newNode)) {
                continue;
            }

            //Get preference list of proposing node
            PreferenceList nodePreference = preferenceLists.get(newNode);

            //Loop through LeftNode's preference list to find a Match
            for (int i = 0; i < nodePreference.size(UNUSED_VAL); i++) {
                //Next Match (RightNode) is found on the list
                int preferNode = nodePreference.getPositionByRank(UNUSED_VAL, i);
                if (matches.isMatched(preferNode, newNode)) {
                    break;
                }

                //If the RightNode Capacity is not full -> create connection between LeftNode - RightNode
                if (!matches.isFull(preferNode, this.matchingData.getCapacityOf(preferNode))) {
                    matches.addMatch(preferNode, newNode);
                    matches.addMatch(newNode, preferNode);
                    matchedNode.add(preferNode);
                    break;
                } else {
                    //If the RightNode's Capacity is Full then Left Node will Compete with Nodes that are inside RightNode
                    //Loser will be the return value

                    int loser = preferenceLists.getLeastScoreNode(
                            UNUSED_VAL,
                            preferNode,
                            newNode,
                            matches.getSetOf(preferNode),
                            matchingData.getCapacityOf(preferNode));

                    if (loser == newNode) {
                        if (preferenceLists.getLastChoiceOf(UNUSED_VAL, newNode) == preferNode) {
                            break;
                        }
                        //Or else Loser go back to UnMatched Queue & Waiting for it's Matching Procedure
                    } else {
                        matches.removeMatchBi(preferNode, loser);
                        unMatchedNode.add(loser);
                        matchedNode.remove(loser);
                        matches.addMatchBi(preferNode, newNode);
                        matchedNode.add(newNode);
                        break;
                    }
                }
            }
        }
        return matches;
    }

    @Override
    public String getMatchingTypeName() {
        return "Many to Many";
    }

    /**
     * MOEA Framework Problem implements
     */

    @Override
    public String getName() {
        return this.problemName;
    }

    @Override
    public int getNumberOfConstraints() {
        return 0;
    }

    @Override
    public int getNumberOfObjectives() {
        return 1;
    }

    @Override
    public int getNumberOfVariables() {
        return 1;
    }

    @Override
    public void close() {
    }
}
