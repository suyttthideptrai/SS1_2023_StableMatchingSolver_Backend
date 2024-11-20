package com.example.SS2_Backend.ss.smt.problem;

import com.example.SS2_Backend.constants.MatchingConst;
import com.example.SS2_Backend.ss.smt.MatchingData;
import com.example.SS2_Backend.ss.smt.evaluator.FitnessEvaluator;
import com.example.SS2_Backend.ss.smt.match.Matches;
import com.example.SS2_Backend.ss.smt.preference.PreferenceListWrapper;
import com.example.SS2_Backend.util.StringUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.variable.Permutation;

/**
 * base class for MatchingProblem
 */
@Slf4j
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class MatchingProblem implements Problem {

    /** problem name */
    final String problemName;

    /** Matching data */
    final MatchingData matchingData;

    /** all eval functions */
    final String[] evaluateFunctions;

    /** problem fitness function */
    final String fitnessFunction;

    /** preference list  */
    PreferenceListWrapper preferenceLists;


    /** problem size (number of individuals in matching problem */
    @Getter
    final int problemSize;

    /** number of set in matching problem */
    final int setNum;

    /** fitness evaluator */
    final FitnessEvaluator fitnessEvaluator;


    protected MatchingProblem(String problemName,
                              String[] evaluateFunctions,
                              String fitnessFunction,
                              MatchingData matchingData,
                              int problemSize,
                              int setNum,
                              FitnessEvaluator fitnessEvaluator) {

        this.problemName = problemName;
        this.evaluateFunctions = evaluateFunctions;
        this.fitnessFunction = fitnessFunction;
        this.matchingData = matchingData;
        this.problemSize = problemSize;
        this.setNum = setNum;
        this.fitnessEvaluator = fitnessEvaluator;
    }

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
        Matches result = stableMatching(solution.getVariable(0));
        double[] satisfactions = this.preferenceLists.getAllSatisfactions(result);
        double fitnessScore;
        if (this.hasFitnessFunc()) {
            fitnessScore = fitnessEvaluator
                    .withFitnessFunctionEvaluation(satisfactions, this.fitnessFunction);
        } else {
            fitnessScore = fitnessEvaluator.defaultFitnessEvaluation(satisfactions);
        }
        solution.setAttribute(MatchingConst.MatchesKey, result);
        solution.setObjective(0, -fitnessScore);
    }


    /**
     * check exists evaluation function of a set by set num
     * @return true if exists
     */
    protected boolean hasEvaluationFunc(int setNum) {
        return StringUtils.isEmptyOrNull(this.evaluateFunctions[setNum]);
    }

    /**
     * check exists fitness function
     * @return true if exists
     */
    protected boolean hasFitnessFunc() {
        return StringUtils.isEmptyOrNull(this.fitnessFunction);
    }

    /**
     * Main matching logic for Stable Matching Problem Types
     *
     * @param var Variable
     * @return Matches
     */
    protected Matches stableMatching(Variable var) {
        return null;
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
