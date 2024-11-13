package com.example.SS2_Backend.ss.smt.problem;

import com.example.SS2_Backend.ss.smt.evaluator.FitnessEvaluator;
import com.example.SS2_Backend.ss.smt.match.Matches;
import com.example.SS2_Backend.ss.smt.preference.PreferenceList;
import com.example.SS2_Backend.ss.smt.preference.impl.provider.NewProvider;
import com.example.SS2_Backend.util.StringUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.variable.Permutation;

import java.util.List;

import static com.example.SS2_Backend.util.StringExpressionEvaluator.convertToStringWithoutScientificNotation;

/**
 * base class for MatchingProblem
 */
@Slf4j
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class MatchingProblem implements Problem {

    /** problem name */
    final String problemName;

    /** all eval functions */
    final String[] evaluateFunctions;

    /** problem fitness function */
    final String fitnessFunction;

    /** preference list  */
    List<PreferenceList> preferenceLists;

    /** preference list builder */
    final NewProvider preferencesProvider;

    /** requirements of all individuals */
    final String[][] requirements;

    /** weights of all individuals */
    final double[][] weights;

    /** properties of all individuals  */
    final double[][] properties;

    /** capacities of all individuals */
    final int[] capacities;

    /** problem size (number of individuals in matching problem */
    final int problemSize;

    /** number of set in matching problem */
    final int setNum;

    /** fitness evaluator */
    final FitnessEvaluator fitnessEvaluator;


    protected MatchingProblem(String problemName,
                              String[] evaluateFunctions,
                              String fitnessFunction,
                              NewProvider preferencesProvider,
                              String[][] requirements,
                              double[][] weights,
                              double[][] properties,
                              int problemSize,
                              int setNum,
                              int[] capacities,
                              FitnessEvaluator fitnessEvaluator) {

        this.problemName = problemName;
        this.evaluateFunctions = evaluateFunctions;
        this.fitnessFunction = fitnessFunction;
        this.preferencesProvider = preferencesProvider;
        this.requirements = requirements;
        this.weights = weights;
        this.properties = properties;
        this.problemSize = problemSize;
        this.setNum = setNum;
        this.capacities = capacities;
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
        log.info("Evaluating ... "); // Start matching & collect result
        Matches result = stableMatching(solution.getVariable(0));
        double[] Satisfactions = fitnessEvaluator.getAllSatisfactions(result, preferenceLists);
        double fitnessScore;
        if (!this.hasFitnessFunc()) {
            fitnessScore = fitnessEvaluator.defaultFitnessEvaluation(Satisfactions);
        } else {
            String fnf = this.fitnessFunction.trim();
            fitnessScore = fitnessEvaluator.withFitnessFunctionEvaluation(Satisfactions, fnf);
        }
        solution.setAttribute("matches", result);
        solution.setObjective(0, -fitnessScore);
        log.info("Score: {}", convertToStringWithoutScientificNotation(fitnessScore));
    }


    /**
     * check exists evaluation function of a set by set num
     * @param setNum set index
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
     * get problem size (total of individuals)
     * @return size
     */
    protected int getProblemSize() {
        return this.problemSize;
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
        return problemName;
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
