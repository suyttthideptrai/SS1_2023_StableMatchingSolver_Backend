package com.example.SS2_Backend.ss.smt.problem;

import com.example.SS2_Backend.ss.smt.match.Matches;
import com.example.SS2_Backend.ss.smt.preference.PreferenceList;
import com.example.SS2_Backend.ss.smt.preference.impl.NewProvider;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.variable.Permutation;

import java.util.List;

import static com.example.SS2_Backend.ss.smt.util.EvaluationUtils.*;
import static com.example.SS2_Backend.util.StringExpressionEvaluator.convertToStringWithoutScientificNotation;

@Slf4j
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class MatchingProblem implements Problem {
    final String problemName;
    final String[] evaluateFunctions;
    final String fitnessFunction;

    boolean f1Status = false; boolean f2Status = false; boolean fnfStatus = false;

    List<PreferenceList> preferenceLists;
    final NewProvider preferencesProvider;

    final String[][] individualRequirements;
    final double[][] individualWeights;
    final double[][] individualProperties;
    final int numberOfIndividuals;
    final int[] individualSetIndices;
    final int[] individualCapacities;

    protected MatchingProblem(String problemName, String[] evaluateFunctions, String fitnessFunction, NewProvider preferencesProvider, boolean f1Status, boolean f2Status, boolean fnfStatus, String[][] individualRequirements, double[][] individualWeights, double[][] individualProperties, int numberOfIndividuals, int[] individualSetIndices, int[] individualCapacities) {
        this.problemName = problemName;
        this.evaluateFunctions = evaluateFunctions;
        this.fitnessFunction = fitnessFunction;
        this.preferencesProvider = preferencesProvider;
        this.f1Status = f1Status;
        this.f2Status = f2Status;
        this.fnfStatus = fnfStatus;
        this.individualRequirements = individualRequirements;
        this.individualWeights = individualWeights;
        this.individualProperties = individualProperties;
        this.numberOfIndividuals = numberOfIndividuals;
        this.individualSetIndices = individualSetIndices;
        this.individualCapacities = individualCapacities;
    }

    @Override
    public String getName() {
        return problemName;
    }

    @Override
    public Solution newSolution() {
        Solution solution = new Solution(1, 1);
        Permutation permutationVar = new Permutation(numberOfIndividuals);
        solution.setVariable(0, permutationVar);
        return solution;
    }

    @Override
    public void evaluate(Solution solution) {
        log.info("Evaluating ... "); // Start matching & collect result
        Matches result = stableMatching(solution.getVariable(0));
        double[] Satisfactions = getAllSatisfactions(result);
        double fitnessScore;
        if (!this.fnfStatus) {
            fitnessScore = defaultFitnessEvaluation(Satisfactions);
        } else {
            String fnf = this.fitnessFunction.trim();
            fitnessScore = withFitnessFunctionEvaluation(Satisfactions, fnf);
        }
        solution.setAttribute("matches", result);
        solution.setObjective(0, -fitnessScore);
        log.info("Score: {}", convertToStringWithoutScientificNotation(fitnessScore));
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

    protected int getNumberOfIndividuals() {
        return numberOfIndividuals;
    }

    // This method will be implemented in child classes
    protected Matches stableMatching(Variable var) {
        return null;
    }

    @Override
    public void close() {}
}
