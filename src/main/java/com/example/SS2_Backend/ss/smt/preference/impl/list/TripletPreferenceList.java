package com.example.SS2_Backend.ss.smt.preference.impl.list;

import com.example.SS2_Backend.ss.smt.preference.PreferenceList;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PROTECTED)
public class TripletPreferenceList implements PreferenceList {

    // Scores are the preferences or priorities for the matching (either from provider or consumer perspective).
    final double[] scores;
    // The positions correspond to the IDs of the individuals (either providers or consumers).
    final int[] positions;
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
        return 0;
    }

    @Override
    public int getLeastNode(int set, int newNode, Set<Integer> currentNodes) {
        int leastNode = newNode - this.padding;
        for (int currentNode : currentNodes) {
            if (this.scores[leastNode] > this.scores[currentNode - this.padding]) {
                leastNode = currentNode - this.padding;
            }
        }
        return leastNode + this.padding;
    }

    @Override
    public int getLeastNode(int set, int newNode, int oldNode) {
        if (isScoreGreater(set, newNode, oldNode)) {
            return oldNode;
        } else {
            return newNode;
        }
    }

    public int[] getPreferenceForSpecificSet(int currentSet,int setNumber, Map<Integer, Integer> setSizes) {
        int startIndex = 0;   // 1 2 3
        for (int i = 1; i < setNumber; i++) {
            if (setSizes.containsKey(i) && i !=currentSet) {
                startIndex += setSizes.get(i);
            }
        }
        int setLength = setSizes.getOrDefault(setNumber, 0);
        int[] result = new int[setLength];
        System.arraycopy(positions, startIndex, result, 0, setLength);
        return result;
    }
    @Override
    public int getPositionByRank(int set, int rank) {
        return 0;
    }

    @Override
    public int getLastOption(int set) {
        return 0;
    }
    public void addArray (double[] scoreTMP, int[] positionTMP) {
        for (int i = 0; i < scoreTMP.length; i++) {
            this.scores[current] = scoreTMP[i];
            this.positions[current] = positionTMP[i];
            this.current++;
        }
    }

    @Override
    public boolean isScoreGreater(int set, int node, int nodeToCompare) {
        return this.scores[node - this.padding] > this.scores[nodeToCompare - this.padding];
    }

    @Override
    public double getScore(int position) {
        return 0;
    }

    public void sortDescendingByScores() {
        double[] cloneScores = scores.clone(); // Copy to a new array
        int size = cloneScores.length;

        for (int i = size / 2 - 1; i >= 0; i--) {
            heapify(cloneScores, size, i);
        }

        for (int i = size - 1; i > 0; i--) {
            double temp = cloneScores[0];
            int tempPos = positions[0];

            cloneScores[0] = cloneScores[i];
            positions[0] = positions[i];

            cloneScores[i] = temp;
            positions[i] = tempPos;

            heapify(cloneScores, i, 0);
        }
    }

    // Heapify the array to maintain the sorting order
    private void heapify(double[] array, int heapSize, int rootIndex) {
        int largestIndex = rootIndex; // Initialize largest as root
        int leftChildIndex = 2 * rootIndex + 1; // left = 2*rootIndex + 1
        int rightChildIndex = 2 * rootIndex + 2; // right = 2*rootIndex + 2

        if (leftChildIndex < heapSize && array[leftChildIndex] > array[largestIndex]) {
            largestIndex = leftChildIndex;
        }

        if (rightChildIndex < heapSize && array[rightChildIndex] > array[largestIndex]) {
            largestIndex = rightChildIndex;
        }

        if (largestIndex != rootIndex) {
            double swap = array[rootIndex];
            int posSwap = positions[rootIndex];

            array[rootIndex] = array[largestIndex];
            positions[rootIndex] = positions[largestIndex];

            array[largestIndex] = swap;
            positions[largestIndex] = posSwap;

            heapify(array, heapSize, largestIndex);
        }
    }
}
