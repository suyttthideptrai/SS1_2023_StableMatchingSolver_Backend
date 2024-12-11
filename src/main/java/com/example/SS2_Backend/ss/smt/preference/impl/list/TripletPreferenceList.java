package com.example.SS2_Backend.ss.smt.preference.impl.list;

import com.example.SS2_Backend.ss.smt.preference.PreferenceList;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
@Slf4j
@Data
@FieldDefaults(level = AccessLevel.PROTECTED)
public class TripletPreferenceList implements PreferenceList {

    // Scores represent preferences or priorities.
    final double[] scores;
    final int[] positions; // IDs of the individuals.
    int current; // Tracks the current index in the list.
    final int padding; // Used for index adjustments.

    public TripletPreferenceList(int size, int padding) {
        scores = new double[size];
        positions = new int[size];
        current = 0;
        this.padding = padding;
    }

    @Override
    public int size(int set) {
        return positions.length;
    }

    @Override
    public int getNumberOfOtherSets() {
        return 0; // Placeholder if needed for 3 sets.
    }

    @Override
    public int getLeastNode(int set, int newNode, Set<Integer> currentNodes) {
        int leastNode = newNode - this.padding;
        for (int currentNode : currentNodes) {
            if (compareScores(leastNode, currentNode - this.padding) > 0) {
                leastNode = currentNode - this.padding;
            }
        }
        return leastNode + this.padding;
    }

    @Override
    public int getLeastNode(int set, int newNode, int oldNode) {
        if (compareScores(newNode - this.padding, oldNode - this.padding) > 0) {
            return oldNode;
        } else {
            return newNode;
        }
    }

    public int[] getPreferenceForSpecificSet(int currentSet, int setNumber, Map<Integer, Integer> setSizes) {
        int startIndex = 0;
        for (int i = 0; i < setNumber; i++) {
            if (setSizes.containsKey(i) && i != currentSet) {
                startIndex += setSizes.get(i);
            }
        }
        int setLength = setSizes.getOrDefault(setNumber, 0);
        int[] result = new int[setLength];
        System.arraycopy(positions, startIndex, result, 0, setLength);
        return result;
    }

    @Override
    public int getPositionByRank(int set, int rank) throws ArrayIndexOutOfBoundsException {
        try {
            return positions[rank] + this.padding;
        } catch (ArrayIndexOutOfBoundsException e) {
            log.error("Position {} not found:", rank, e);
            return -1;
        }
    }

    @Override
    public int getLastOption(int set) {
        return positions[positions.length - 1] + this.padding;
    }

    public void addArray(double[] scoreTMP, int[] positionTMP) {
        for (int i = 0; i < scoreTMP.length; i++) {
            this.scores[current] = scoreTMP[i];
            this.positions[current] = positionTMP[i];
            this.current++;
        }
    }

    @Override
    public boolean isScoreGreater(int set, int node, int nodeToCompare) {
        return compareScores(node - this.padding, nodeToCompare - this.padding) > 0;
    }

    @Override
    public double getScore(int position) {
        try {
            return scores[position - this.padding];
        } catch (ArrayIndexOutOfBoundsException e) {
            log.error("Position {} not found:", position, e);
            return 0;
        }
    }

    /**
     * Combines scores from multiple sets to evaluate preferences.
     * For 2 sets, it defaults to a single score comparison.
     * For 3 sets, it aggregates preferences.
     */
    public double calculateCombinedScore(int nodeA, int nodeB, int nodeC) {
        return scores[nodeA - this.padding] + scores[nodeB - this.padding] + scores[nodeC - this.padding];
    }

    /**
     * Helper method for comparing scores between two nodes.
     */
    private int compareScores(int node1, int node2) {
        return Double.compare(scores[node1], scores[node2]);
    }
}

