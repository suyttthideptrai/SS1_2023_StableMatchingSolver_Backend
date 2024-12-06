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
import org.moeaframework.core.variable.EncodingUtils;
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
                if (result.isMatched(excludedPair[0], excludedPair[1])) {
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
        int[] decodeVar = EncodingUtils.getPermutation(var);
        Queue<Integer> unMatchedNode = new LinkedList<>();

        for (int val : decodeVar) {
            unMatchedNode.add(val);
        }

        while (!unMatchedNode.isEmpty()) {
            int leftNode;
            leftNode = unMatchedNode.poll();

            //Get preference list of proposing node
            PreferenceList nodePreference = preferenceLists.get(leftNode);

            //Loop through LeftNode's preference list to find a Match
            for (int i = 0; i < nodePreference.size(UNUSED_VAL); i++) {
                //Next Match (RightNode) is found on the list
                int rightNode = nodePreference.getPositionByRank(UNUSED_VAL, i);

                if (matches.isMatched(rightNode, leftNode)) {
                    continue;
                }

                boolean rightIsFull = matches.isFull(rightNode, this.matchingData.getCapacityOf(rightNode));
                boolean leftIsFull = matches.isFull(leftNode, this.matchingData.getCapacityOf(leftNode));

                // Both nodes are not full
                if (!rightIsFull && !leftIsFull) {
                    matches.addMatchBi(leftNode, rightNode);
                    continue;
                }

                int rightLoser =  preferenceLists.getLeastScoreNode(
                    UNUSED_VAL,
                    rightNode,
                    leftNode,
                    matches.getSetOf(rightNode),
                    matchingData.getCapacityOf(rightNode));

                int leftLoser = preferenceLists.getLeastScoreNode(
                    UNUSED_VAL,
                    leftNode,
                    rightNode,
                    matches.getSetOf(leftNode),
                    matchingData.getCapacityOf(leftNode));

                // If neither leftNode and rightNode like the other
                if ((rightLoser == leftNode) || (leftLoser == rightNode)) {
                    continue;
                }

                // Unmatch right from loser if full
                if (rightIsFull) {
                    matches.removeMatchBi(rightNode, rightLoser);
                }

                // Unmatch leftNode from loser if full
                if (leftIsFull) {
                    matches.removeMatchBi(leftNode, leftLoser);
                }

                matches.addMatchBi(leftNode, rightNode);
            }
        }
        return matches;
    }

    @Override
    public String getMatchingTypeName() {
        return "Many to Many";
    }

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
