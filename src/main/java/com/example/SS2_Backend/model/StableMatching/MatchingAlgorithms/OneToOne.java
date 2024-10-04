package com.example.SS2_Backend.model.StableMatching.MatchingAlgorithms;

import com.example.SS2_Backend.model.StableMatching.*;
import com.example.SS2_Backend.model.StableMatching.Matches.Matches;
import com.example.SS2_Backend.model.StableMatching.Matches.MatchesOTO;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.Permutation;
import java.util.*;

import static com.example.SS2_Backend.util.StringExpressionEvaluator.convertToStringWithoutScientificNotation;

@Slf4j
@Getter@Setter
public class OneToOne implements StableMatching {
    private PreferencesProvider preferencesProvider;
    private IndividualList individuals;
    private static final List<String> VALID_EVALUATE_FUNCTION_KEYWORDS = Arrays.asList("P", "W", "R");
    private String evaluateFunctionForSet1, evaluateFunctionForSet2, fitnessFunction;
    private boolean func1 = false, func2 = false, fitfunc = false;
    private int n, padding;
    private int[][] preferences;
    private String problemName;
    public OneToOne() {
        super();
    }

    public void setPopulation(ArrayList<Individual> individuals, String[] propertiesNames) {
        this.individuals = new IndividualList(individuals, propertiesNames);
        initializeFields();
    }
    private void initializeFields() {
        this.preferencesProvider = new PreferencesProvider(individuals);
        initializePrefProvider();
        n = individuals.getNumberOfIndividual();
        preferences = new int[n][];
        this.padding = individuals.getNumberOfIndividualForSet0();
        for (int i = 0; i < n; i++) {
            PreferenceList individualPref = getPreferenceOfIndividual(i);
            if (i >= padding) {
                preferences[i] = individualPref.getPositions();
            } else {
                preferences[i] = addPadding(individualPref.getPositions(), this.padding);
            }
        }
    }
    static int[] addPadding(int[] array, int padding) {
        for (int i = 0; i < array.length; i++) {
            array[i] += padding;
        }
        return array;
    }
    public PreferenceList getPreferenceOfIndividual(int index) {
        PreferenceList a;
        if (!func1 && !func2) {
            a = preferencesProvider.getPreferenceListByDefault(index);
        } else {
            a = preferencesProvider.getPreferenceListByFunction(index);
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
        Queue<Integer> singleQueue = new LinkedList<>();
        for(int node : order) singleQueue.add(node);
        int[] matches = new int[n];
        for (int i = 0; i < n; i++) matches[i] = -1;
        Set<Integer> matched = new HashSet<>();
        Set<Integer> leftOver = new HashSet<>();
        int loop = 0;
        while(!singleQueue.isEmpty()){
            int a = singleQueue.poll();
            if (matched.contains(a)) continue;
            // Prevent infinite loop
            if (loop > 2 * n) return MatchesOTO.getEmptyObject();
            int[] aPreference = preferences[a];
            int prefLen = aPreference.length;
            for (int i = 0; i < prefLen; i++) {
                int b = aPreference[i];
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
                        matched.add(a);
                        matches[a] = b;
                        matches[b] = a;
                        break;
                    } else if (i == prefLen - 1) {
                        leftOver.add(a);
                    }
                }
            }
            loop++;
        }
        return new MatchesOTO(matches, leftOver);
    }
    public double[] getAllSatisfactions(MatchesOTO matches) {
        if (matches.isEmpty()) return new double[0];
        List<Integer> list = matches.getMatches();
        double[] totalSatisfaction = new double[n];
        for (int a = 0; a < n; a++) {
            int b = list.get(a);
            if (b == -1 ) totalSatisfaction[a] = 0;
            else {
                int rankA = findRank(a, b);
                int rankB = findRank(b, a);
                totalSatisfaction[a] = preferences[b].length - rankA + preferences[a].length - rankB;
            }
        }
        return totalSatisfaction;
    }

    @Override
    public double[] getAllSatisfactions(Matches res) {
        return new double[0];
    }

    public boolean bLikeAMore(int a, int b, int c) {
        for (int individual : preferences[b]) {
            if (individual == a) return true;
            if (individual == c) return false;
        }
        throw new RuntimeException("The input (preference) have problem.");
    }
    public int findRank(int target, int from) {
        for (int i = 0; i < preferences[from].length; i++) {
            if (preferences[from][i] == target) return i;
        }
        throw new RuntimeException("The input (preference) have problem");
    }
    @Override
    public Solution newSolution() {
        Solution solution = new Solution(this.getNumberOfVariables(), this.getNumberOfObjectives());
        solution.setVariable(0, new Permutation(n));
        return solution;
    }

    @Override
    public void close(){}
}