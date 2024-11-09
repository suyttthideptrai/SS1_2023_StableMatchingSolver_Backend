package com.example.SS2_Backend.model.stableMatching;

import com.example.SS2_Backend.model.stableMatching.Matches.MatchesOTO;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.Permutation;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;

import static com.example.SS2_Backend.util.StringExpressionEvaluator.convertToStringWithoutScientificNotation;

/*
* An extended version of StableMatchingProblem model that designed for one-to-one problem (problem where every individual has capacity = 0).
* */
@Slf4j
@Getter@Setter
public class StableMatchingOTOProblem implements Problem {
    private PreferencesProvider preferencesProvider;
    private IndividualList individuals;
    private static final List<String> VALID_EVALUATE_FUNCTION_KEYWORDS = Arrays.asList("P", "W", "R");
    private String evaluateFunctionForSet1, evaluateFunctionForSet2, fitnessFunction;
    private boolean func1 = false, func2 = false, fitfunc = false;
    private int problemSize, padding;
    private List<PreferenceList> preferences;
    private String problemName;
    public StableMatchingOTOProblem() {
        super();
    }

    public void setPopulation(ArrayList<Individual> individuals, String[] propertiesNames) {
        this.individuals = new IndividualList(individuals, propertiesNames);
        initializeFields();
    }
    private void initializeFields() {
        this.preferencesProvider = new PreferencesProvider(individuals);
        initializePrefProvider();
        problemSize = individuals.getNumberOfIndividual();
        this.preferences = new ArrayList<>();
        this.padding = individuals.getNumberOfIndividualForSet0();
        for (int i = 0; i < problemSize; i++) {
            PreferenceList individualPref = getPreferenceOfIndividual(i);
            this.preferences.add(individualPref);
        }
    }

    public PreferenceList getPreferenceOfIndividual(int index) {
        PreferenceList a;
        if (!func1 && !func2) {
            a = this.preferencesProvider.getPreferenceListByDefault(index);
        } else {
            a = this.preferencesProvider.getPreferenceListByFunction(index);
        }
        return a;
    }

    private void initializePrefProvider() {
        if (this.evaluateFunctionForSet1 != null) {
            this.preferencesProvider.setEvaluateFunctionForSet1(evaluateFunctionForSet1);
        }
        if (this.evaluateFunctionForSet2 != null) {
            this.preferencesProvider.setEvaluateFunctionForSet2(evaluateFunctionForSet2);
        }
    }

    public void setEvaluateFunctionForSet1(String func) {
        if(VALID_EVALUATE_FUNCTION_KEYWORDS.stream().anyMatch(func::contains)) {
            this.func1 = true;
            this.evaluateFunctionForSet1 = func;
        }
    }
    public void setEvaluateFunctionForSet2(String func) {
        if(VALID_EVALUATE_FUNCTION_KEYWORDS.stream().anyMatch(func::contains)) {
            this.func2 = true;
            this.evaluateFunctionForSet2 = func;
        }
    }
    public void setFitnessFunction(String func) {
        if (func.contains("S") || func.contains("SIGMA{") ||
                func.contains("M")) {
            this.fitfunc = true;
            this.fitnessFunction = func;
        }
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
    @Override
    public int getNumberOfVariables() {
        return 1;
    }
    @Override
    public int getNumberOfObjectives() {
        return 1;
    }
    @Override
    public int getNumberOfConstraints() {
        return 0;
    }
    @Override
    public void evaluate(Solution solution) {
        log.info("Evaluating ... ");
        int[] order = ((Permutation) solution.getVariable(0)).toArray();
        MatchesOTO matches = StableMatchingAlgorithm(order);
        double[] satisfaction = getAllSatisfactions(matches);
        solution.setAttribute("matches", matches);
        if (satisfaction.length == 0) {
            solution.setObjective(0, 1);
            log.info("Skip infinite loop");
        } else {
            double sum = Arrays.stream(satisfaction).sum();
            solution.setObjective(0, -sum);
            log.info("Score: {}", convertToStringWithoutScientificNotation(sum));
        }
    }
    public MatchesOTO StableMatchingAlgorithm(int[] order) {
        Queue<Integer> unmatched = new ArrayBlockingQueue<>(order.length);
        Arrays.stream(order).forEach(unmatched::add);
        // when init all element are null
        Integer[] matches = new Integer[order.length];
        for (int i = 0; i < problemSize; i++) matches[i] = -1;
        Set<Integer> matched = new TreeSet<>();
        Set<Integer> leftOver = new TreeSet<>();

        while(!unmatched.isEmpty()){
            int leftNode = unmatched.poll();
            if (matched.contains(leftNode)) continue;
            for (int rightNode : preferences.get(leftNode).keySet()) {
                if (rightNode == matches[leftNode]
                    && matches[rightNode] == leftNode)
                    break;
                if (null == matches[rightNode]) {
                    matches[leftNode] = rightNode;
                    matches[rightNode] = leftNode;
                    break;
                } else {
                    int rightMatch = matches[rightNode];
                    if (bLikeAMore(leftNode, rightNode, rightMatch)) {
                        unmatched.add(rightMatch);
                        matched.remove(rightMatch);
                        matches[rightMatch] = -1;
                        matched.add(leftNode);
                        matches[leftNode] = rightNode;
                        matches[rightNode] = leftNode;
                        break;
                    } else {
                        leftOver.add(leftNode);
                    }
                }
            }
        }
        return new MatchesOTO(matches, leftOver);
    }
    public double[] getAllSatisfactions(MatchesOTO matches) {
        Integer[] list = matches.getMatches();
        double[] totalSatisfaction = new double[problemSize];
        for (int a = 0; a < problemSize; a++) {
            int b = list[a];
            if (b == -1) totalSatisfaction[a] = 0;
            else {
                double aSatis = getPreferenceOfIndividual(a).get(b);
                double bSatis = getPreferenceOfIndividual(b).get(a);
                totalSatisfaction[a] = aSatis + bSatis;
            }
        }
        return totalSatisfaction;
    }

    public boolean bLikeAMore(int a, int b, int c) {
        return c == preferences.get(b).getLeastNode(a, c);
    }
    @Override
    public Solution newSolution() {
        Solution solution = new Solution(this.getNumberOfVariables(), this.getNumberOfObjectives());
        solution.setVariable(0, new Permutation(problemSize));
        return solution;
    }

    @Override
    public void close(){}
}