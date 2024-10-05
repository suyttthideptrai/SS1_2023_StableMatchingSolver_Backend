package com.example.SS2_Backend.model.onetomany;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.variable.Permutation;

import java.util.*;
import java.util.stream.Stream;

@Slf4j
@Data
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class MatchingProblem implements Problem {

    IndividualList individuals;
    List<PreferenceList> preferenceLists;
    PreferenceProvider preferencesProvider;
    String evaluateFunctionForProviders;
    String evaluateFunctionForConsumers;
    String fitnessFunction;
    boolean providerFunctionStatus = false;
    boolean consumerFunctionStatus = false;
    boolean fitnessFunctionStatus = false;

    public void setPopulation(ArrayList<Individual> individuals, String[] propertiesNames) {
        this.individuals = new IndividualList(individuals, propertiesNames);
        initializeFields();
    }

    private void initializeFields() {
        this.preferencesProvider = new PreferenceProvider(individuals);
        initializePrefProvider();
        preferenceLists = getPreferences();
    }

    private List<PreferenceList> getPreferences() {
        List<PreferenceList> fullList = new ArrayList<>();
        for (int i = 0; i < individuals.getTotalIndividuals(); i++) {
            PreferenceList a = getPreferenceOfIndividual(i);
            fullList.add(a);
        }
        return fullList;
    }

    public PreferenceList getPreferenceOfIndividual(int index) {
        PreferenceList a;
        if (!providerFunctionStatus && !consumerFunctionStatus) {
            a = preferencesProvider.getPreferenceListByDefault(index);
        } else {
            a = preferencesProvider.getPreferenceListByFunction(index);
        }
        return a;
    }

    private void initializePrefProvider() {
        if (this.evaluateFunctionForProviders != null) {
            this.preferencesProvider.setProviderEvaluateFunction(evaluateFunctionForProviders);
        }
        if (this.evaluateFunctionForConsumers != null) {
            this.preferencesProvider.setConsumerEvaluateFunction(evaluateFunctionForConsumers);
        }
    }

    public void setEvaluateFunctionForProviders(String evaluateFunction) {
        if (isValidEvaluateFunction(evaluateFunction)) {
            this.providerFunctionStatus = true;
            this.evaluateFunctionForProviders = evaluateFunction;
        }
    }

    public void setEvaluateFunctionForConsumers(String evaluateFunction) {
        if (isValidEvaluateFunction(evaluateFunction)) {
            this.consumerFunctionStatus = true;
            this.evaluateFunctionForConsumers = evaluateFunction;
        }
    }

    public void setFitnessFunction(String fitnessFunction) {
        if (fitnessFunction.contains("S") || fitnessFunction.contains("SIGMA{") ||
                fitnessFunction.contains("M")) {
            this.fitnessFunctionStatus = true;
            this.fitnessFunction = fitnessFunction;
        }
    }

    private boolean isValidEvaluateFunction(String function) {
        return Stream.of("P", "W", "R").anyMatch(function::contains);
    }

    @Override
    public Solution newSolution() {
        Solution solution = new Solution(1, 1);
        Permutation permutationVar = new Permutation(individuals.getTotalIndividuals());
        solution.setVariable(0, permutationVar);
        return solution;
    }

    @Override
    public void evaluate(Solution solution) {
        log.info("Evaluating...");
        Match result = oneToManyMatching(solution.getVariable(0));
        double[] satisfactions = getAllSatisfactions(result);
        double fitnessScore;
        if (!this.fitnessFunctionStatus) {
            fitnessScore = defaultFitnessEvaluation(satisfactions);
        } else {
            fitnessScore = withFitnessFunctionEvaluation(satisfactions, this.fitnessFunction);
        }
        solution.setAttribute("matches", result);
        solution.setObjective(0, -fitnessScore);
        log.info("Score: {}", fitnessScore);
    }

    private Match oneToManyMatching(Variable var) {
        Match match = new Match(individuals.getTotalIndividuals());
        Set<Integer> matchedConsumers = new HashSet<>();
        Permutation castVar = (Permutation) var;
        int[] decodeVar = castVar.toArray();
        Queue<Integer> unmatchedConsumers = new LinkedList<>();
        for (int val : decodeVar) {
            if (individuals.getRoleOfParticipant(val) == 1) { // Consumer
                unmatchedConsumers.add(val);
            }
        }

        while (!unmatchedConsumers.isEmpty()) {
            int consumer = unmatchedConsumers.poll();
            if (matchedConsumers.contains(consumer)) {
                continue;
            }

            PreferenceList consumerPreference = preferenceLists.get(consumer);
            for (int i = 0; i < consumerPreference.size(); i++) {
                int provider = consumerPreference.getIndexByPosition(i);
                if (!match.isProviderFull(provider, individuals.getProviderCapacity(provider))) {
                    match.addMatch(provider, consumer);
                    matchedConsumers.add(consumer);
                    break;
                } else {
                    int leastPreferredConsumer = getLeastPreferredConsumer(provider, consumer, match.getConsumerMatchesForProvider(provider));
                    if (leastPreferredConsumer != consumer) {
                        match.removeMatch(provider, leastPreferredConsumer);
                        match.addMatch(provider, consumer);
                        unmatchedConsumers.add(leastPreferredConsumer);
                        matchedConsumers.remove(leastPreferredConsumer);
                        matchedConsumers.add(consumer);
                        break;
                    }
                }
            }
            if (!matchedConsumers.contains(consumer)) {
                match.addLeftOverConsumer(consumer);
            }
        }
        return match;
    }

    private int getLeastPreferredConsumer(int provider, int newConsumer, Integer[] currentConsumers) {
        PreferenceList providerPreference = preferenceLists.get(provider);
        return providerPreference.getLeastPreferredIndividual(newConsumer, currentConsumers);
    }

    private double defaultFitnessEvaluation(double[] satisfactions) {
        return Arrays.stream(satisfactions).sum();
    }

    private double withFitnessFunctionEvaluation(double[] satisfactions, String fitnessFunction) {
        // Implementation similar to StableMatchingProblem
        // You may need to adapt this method based on your specific requirements
        return 0.0; // Placeholder
    }

    private double[] getAllSatisfactions(Match match) {
        double[] satisfactions = new double[individuals.getTotalIndividuals()];

        for (int i = 0; i < individuals.getTotalIndividuals(); i++) {
            double score = 0.0;
            PreferenceList prefList = preferenceLists.get(i);
            if (individuals.isProvider(i)) {
                // The individual is a provider
                Set<Integer> providerMatches = match.getConsumersOfProvider(i);
                for (int consumer : providerMatches)
                    score += prefList.getScoreByIndex(consumer);
            } else {
                Integer matchedProvider = match.getProviderOfConsumer(i);
                if (matchedProvider != null)
                    score = prefList.getScoreByIndex(matchedProvider);
            }
            satisfactions[i] = score;
        }
        return satisfactions;
    }


    @Override
    public String getName() {
        return "One-to-Many Matching Problem";
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
    public void close() {
        // No resources to close
    }

    // Additional helper methods and overrides as needed
}
