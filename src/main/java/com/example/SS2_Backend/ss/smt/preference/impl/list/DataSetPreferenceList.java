package com.example.SS2_Backend.ss.smt.preference.impl.list;

import com.example.SS2_Backend.ss.smt.preference.PreferenceList;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
@Slf4j
@Data
@FieldDefaults(level = AccessLevel.PROTECTED)
public class DataSetPreferenceList implements PreferenceList {

    // Scores are the preferences or priorities for the matching (either from provider or consumer perspective).
    final double[] scores;
    // The positions correspond to the IDs of the individuals (either providers or consumers).
    final int[] positions;
    int current; // Tracks the current index in the list.
    final int padding; // Used for index adjustments.
    int[] ranges; // For timestamp


    public DataSetPreferenceList(int size, int padding) {
        scores = new double[size];
        positions = new int[size];
        current = 0;
        ranges = new int[2];
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
        for (int i = 0; i < setNumber; i++) {
            if (setSizes.containsKey(i) && i !=currentSet) {
                startIndex += setSizes.get(i);
            }
        }
        int setLength = setSizes.getOrDefault(setNumber, 0);
        int[] result = new int[setLength];
        System.arraycopy(positions, startIndex, result, 0, setLength);
        return result;
    }


    // Compare Time
    public int[] getRanges() {
        return ranges;
    }

    /**
     * @param score score of the respective competitor
     *
     * this method registers new competitor instance to the preference list
     */
    public void add(double score, int start, int end) {
        this.scores[current] = score;
        this.positions[current] = current;
        this.ranges[0] = start;
        this.ranges[1] = end;
        this.current++;
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

    public void sort() {
        sortDescendingByScores();
    }

    public void sortDescendingByScores() {
        double[] cloneScores = scores.clone(); //copy to new array
        int size = cloneScores.length;

        // Build min heap
        for (int i = size / 2 - 1; i >= 0; i--) {
            heapify(cloneScores, size, i);
        }

        // Extract elements from heap one by one
        for (int i = size - 1; i > 0; i--) {
            // Move current root to end
            double temp = cloneScores[0];
            int tempPos = positions[0];

            cloneScores[0] = cloneScores[i];
            positions[0] = positions[i];

            cloneScores[i] = temp;
            positions[i] = tempPos;

            // Call min heapify on the reduced heap
            heapify(cloneScores, i, 0);
        }
    }

    void heapify(double[] array, int heapSize, int rootIndex) {
        int smallestIndex = rootIndex; // Initialize smallest as root
        int leftChildIndex = 2 * rootIndex + 1; // left = 2*rootIndex + 1
        int rightChildIndex = 2 * rootIndex + 2; // right = 2*rootIndex + 2

        // If left child is smaller than root
        if (leftChildIndex < heapSize && array[leftChildIndex] < array[smallestIndex]) {
            smallestIndex = leftChildIndex;
        }

        // If right child is smaller than smallest so far
        if (rightChildIndex < heapSize && array[rightChildIndex] < array[smallestIndex]) {
            smallestIndex = rightChildIndex;
        }

        // If smallest is not root
        if (smallestIndex != rootIndex) {
            double swap = array[rootIndex];
            int posSwap = positions[rootIndex];

            array[rootIndex] = array[smallestIndex];
            positions[rootIndex] = positions[smallestIndex];

            array[smallestIndex] = swap;
            positions[smallestIndex] = posSwap;

            // Recursively heapify the affected sub-tree
            heapify(array, heapSize, smallestIndex);
        }
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

}
