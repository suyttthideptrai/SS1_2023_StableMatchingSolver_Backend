package com.example.SS2_Backend.ss.smt.implement;

import com.example.SS2_Backend.constants.MatchingConst;
import com.example.SS2_Backend.ss.smt.Matches;
import com.example.SS2_Backend.ss.smt.MatchingData;
import com.example.SS2_Backend.ss.smt.MatchingProblem;
import com.example.SS2_Backend.ss.smt.evaluator.FitnessEvaluator;
import com.example.SS2_Backend.ss.smt.preference.PreferenceList;
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

import java.util.*;

@Slf4j
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class OTMProblem implements MatchingProblem {

    /**
     * problem name
     */
    final String problemName;

    /**
     * problem size (number of individuals in matching problem
     */
    final int problemSize;

    /**
     * number of set in matching problem
     */
    final int setNum;

    /**
     * Matching data
     */
    final MatchingData matchingData;

    /**
     * preference list
     */
    final PreferenceListWrapper preferenceLists;

    /**
     * problem fitness function
     */
    final String fitnessFunction;

    /**
     * fitness evaluator
     */
    final FitnessEvaluator fitnessEvaluator;

    /**
     * will not be used
     */
    final int UNUSED_VAL = MatchingConst.UNUSED_VALUE;

    @Override
    public String getName() {
        return problemName;
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

    public boolean hasFitnessFunc() {
        return StringUtils.isEmptyOrNull(this.fitnessFunction);
    }

    @Override
    public Solution newSolution() {
        Solution solution = new Solution(1, 1);
        Permutation permutationVar = new Permutation(problemSize);
        solution.setVariable(0, permutationVar);
        return solution;
    }

    @Override
    public void close() {

    }

    @Override
    public String getMatchingTypeName() {
        return "One-to-Many Matching Problem";
    }

    @Override
    public MatchingData getMatchingData() {
        return matchingData;
    }

    @Override
    public Matches stableMatching(Variable var) {
        Matches matches = new Matches(problemSize);
        Set<Integer> matchedConsumers = new HashSet<>();
        Permutation castVar = (Permutation) var;
        int[] decodeVar = castVar.toArray();
        Queue<Integer> unmatchedConsumers = new LinkedList<>();

        // Split nodes into consumers and providers based on setNum
        for (int i = 0; i < problemSize; i++) {
            if (decodeVar[i] > setNum) {
                unmatchedConsumers.add(i);
            }
        }

        // Process all unmatched consumers
        while (!unmatchedConsumers.isEmpty()) {
            int consumer = unmatchedConsumers.poll();
            // Skip if already matched
            if (matchedConsumers.contains(consumer)) {
                continue;
            }
            PreferenceList consumerPreference = getPreferenceLists().get(consumer);
            // Try to match with each preferred provider
            for (int i = 0; i < consumerPreference.size(UNUSED_VAL); i++) {
                int provider = consumerPreference.getPositionByRank(UNUSED_VAL, i);

                // Check if provider is at full capacity
                Set<Integer> currentMatches = matches.getSetOf(provider);
                int currentMatchCount = currentMatches.size();
                int providerCapacity = matchingData.getCapacityOf(provider);

                if (currentMatchCount >= providerCapacity) {
                    // Find the least preferred match based on provider's preference
                    Integer leastPreferredMatch = null;
                    double leastPreferredScore = Double.MIN_VALUE;

                    for (Integer currentMatch : currentMatches) {
                        double currentScore = preferenceLists.getPreferenceScore(
                                provider,
                                currentMatch
                        );

                        // Find the least preferred (highest score) match
                        if (currentScore > leastPreferredScore) {
                            leastPreferredScore = currentScore;
                            leastPreferredMatch = currentMatch;
                        }
                    }

                    // Check if new consumer is preferred over least preferred current match
                    if (leastPreferredMatch != null) {
                        double newConsumerScore = preferenceLists.getPreferenceScore(
                                provider,
                                consumer
                        );

                        // If new consumer is more preferred, replace the least preferred match
                        if (newConsumerScore < leastPreferredScore) {
                            matches.removeMatchBi(provider, leastPreferredMatch);
                            matchedConsumers.remove(leastPreferredMatch);
                            unmatchedConsumers.add(leastPreferredMatch);
                        } else {
                            // If not preferred, skip this provider
                            continue;
                        }
                    }
                }

                // If provider has available capacity or we've made space
                matches.addMatchBi(provider, consumer);
                matchedConsumers.add(consumer);
                break;
            }
        }
        return matches;
    }

    @Override
    public double[] getMatchesSatisfactions(Matches matches) {
        return this.preferenceLists.getMatchesSatisfactions(matches, matchingData);
    }
}