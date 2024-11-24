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
        Set<Integer> matchedNode = new HashSet<>();
        Permutation castVar = (Permutation) var;
        int[] decodeVar = castVar.toArray();
        Queue<Integer> unmatchedConsumers = new LinkedList<>();
        Queue<Integer> unmatchedProviders = new LinkedList<>();

        // Split nodes into consumers and providers based on setNum
        for (int i = 0; i < problemSize; i++) {
            if (decodeVar[i] < setNum) {
                unmatchedConsumers.add(i);
            } else {
                unmatchedProviders.add(i);
            }
        }

        // Process all unmatched consumers
        while (!unmatchedConsumers.isEmpty()) {
            int consumer = unmatchedConsumers.poll();

            if (matchedNode.contains(consumer)) {
                continue;
            }

            PreferenceList consumerPreference = getPreferenceLists().get(consumer);
            boolean matched = false;

            // Try to match with each preferred provider
            for (int i = 0; i < consumerPreference.size(UNUSED_VAL); i++) {
                int provider = consumerPreference.getPositionByRank(UNUSED_VAL, i);

                // If already matched to this provider, skip
                if (matches.isMatched(provider, consumer)) {
                    break;
                }

                // If provider has available capacity
                if (!matches.isFull(provider, matchingData.getCapacityOf(provider))) {
                    matches.addMatchBi(provider, consumer);
                    matchedNode.add(consumer);
                    matched = true;
                    break;
                } else {
                    // Provider is at capacity - check if current consumer is preferred over existing matches
                    int leastPreferred = preferenceLists.getLeastScoreNode(
                            UNUSED_VAL,
                            provider,
                            consumer,
                            matches.getSetOf(provider),
                            matchingData.getCapacityOf(provider)
                    );

                    if (leastPreferred != consumer) {
                        // Current consumer is preferred over least preferred match
                        matches.removeMatchBi(provider, leastPreferred);
                        unmatchedConsumers.add(leastPreferred);
                        matchedNode.remove(leastPreferred);

                        matches.addMatchBi(provider, consumer);
                        matchedNode.add(consumer);
                        matched = true;
                        break;
                    } else if (preferenceLists.getLastChoiceOf(UNUSED_VAL, consumer) == provider) {
                        // If this was consumer's last choice and they weren't preferred, add to leftovers
                        matches.addLeftOver(consumer);
                        break;
                    }
                }
            }

            if (!matched && !matches.getLeftOvers().contains(consumer)) {
                matches.addLeftOver(consumer);
            }
        }

        // Add any remaining unmatched providers to leftovers
        while (!unmatchedProviders.isEmpty()) {
            int provider = unmatchedProviders.poll();
            if (matches.getSetOf(provider).isEmpty()) {
                matches.addLeftOver(provider);
            }
        }

        return matches;
    }

    @Override
    public double[] getMatchesSatisfactions(Matches matches) {
        return new double[0];
    }
}
