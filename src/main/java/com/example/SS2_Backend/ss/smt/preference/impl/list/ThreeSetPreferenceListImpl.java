package com.example.SS2_Backend.ss.smt.preference.impl.list;

import com.example.SS2_Backend.ss.smt.preference.ThreeSetPreferenceList;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * Implementation for Three Set Preference List
 */
@Getter
@Slf4j
public class ThreeSetPreferenceListImpl implements ThreeSetPreferenceList {

    private final Map<Integer, List<Double>> scoresBySet; // Map set no to its scores
    private final Map<Integer, List<Integer>> positionsBySet; // Map set no to positions
    private final int padding;

    public ThreeSetPreferenceListImpl(int setCount, int initialCapacity, int padding) {
        scoresBySet = new HashMap<>();
        positionsBySet = new HashMap<>();
        this.padding = padding;

        // Initialize maps for each set
        for (int i = 1; i <= setCount; i++) {
            scoresBySet.put(i, new ArrayList<>(initialCapacity));
            positionsBySet.put(i, new ArrayList<>(initialCapacity));
        }
    }

    @Override
    public int size(int set) {
        return scoresBySet.getOrDefault(set, Collections.emptyList()).size();
    }

    @Override
    public int getNumberOfOtherSets() {
        return scoresBySet.size() - 1;
    }

    @Override
    public double[] getScores(int set) {
        return scoresBySet.getOrDefault(set, Collections.emptyList())
                .stream()
                .mapToDouble(Double::doubleValue)
                .toArray();
    }

    @Override
    public void add(int set, double score) {
        scoresBySet.get(set).add(score);
        positionsBySet.get(set).add(scoresBySet.get(set).size() - 1 + padding);
    }

    @Override
    public int getLeastNode(int set, int newNode, Set<Integer> currentNodes) {
        int leastNode = newNode - padding;
        for (int currentNode : currentNodes) {
            if (getScore(currentNode) < getScore(leastNode + padding)) {
                leastNode = currentNode - padding;
            }
        }
        return leastNode + padding;
    }

    @Override
    public int getLeastNode(int set, int newNode, int oldNode) {
        return getScore(newNode) < getScore(oldNode) ? newNode : oldNode;
    }

    @Override
    public boolean isScoreGreater(int set, int proposeNode, int preferNodeCurrentNode) {
        return getScore(proposeNode) > getScore(preferNodeCurrentNode);
    }

    @Override
    public int getPositionByRank(int set, int rank) {
        try {
            return positionsBySet.get(set).get(rank);
        } catch (IndexOutOfBoundsException e) {
            log.error("Rank {} not found in set {}:", rank, set, e);
            return -1;
        }
    }

    @Override
    public int getLastOption(int set) {
        int size = size(set);
        return getPositionByRank(set, size - 1);
    }

    @Override
    public double getScore(int position) {
        for (Map.Entry<Integer, List<Double>> entry : scoresBySet.entrySet()) {
            int set = entry.getKey();
            List<Integer> positions = positionsBySet.get(set);
            if (positions.contains(position)) {
                return entry.getValue().get(position - padding);
            }
        }
        log.error("Position {} not found in any set", position);
        return 0.0;
    }

    @Override
    public void sortAll() {
        for (Map.Entry<Integer, List<Double>> entry : scoresBySet.entrySet()) {
            int set = entry.getKey();
            sortDescendingByScores(set);
        }
    }

    private void sortDescendingByScores(int set) {
        List<Double> scores = scoresBySet.get(set);
        List<Integer> positions = positionsBySet.get(set);

        // Sort scores and positions together by scores in descending order
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < scores.size(); i++) {
            indices.add(i);
        }

        indices.sort((i1, i2) -> Double.compare(scores.get(i2), scores.get(i1)));

        List<Double> sortedScores = new ArrayList<>();
        List<Integer> sortedPositions = new ArrayList<>();
        for (int index : indices) {
            sortedScores.add(scores.get(index));
            sortedPositions.add(positions.get(index));
        }

        scoresBySet.put(set, sortedScores);
        positionsBySet.put(set, sortedPositions);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Integer, List<Double>> entry : scoresBySet.entrySet()) {
            int set = entry.getKey();
            builder.append("Set ").append(set).append(": ");
            builder.append(entry.getValue()).append("\n");
        }
        return builder.toString();
    }
}