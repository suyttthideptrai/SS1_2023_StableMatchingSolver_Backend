package com.example.SS2_Backend.ss.smt.preference.impl.list;

import com.example.SS2_Backend.ss.smt.preference.IPreferenceListExtra;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Data
@FieldDefaults(level = AccessLevel.PROTECTED)
public class PreferenceListExtra implements IPreferenceListExtra {

    final double[] scores;
    final int[] positions;
    int current;
    final int padding;

    public PreferenceListExtra(int size, int padding) {
        scores = new double[size];
        positions = new int[size];
        current = 0;
        this.padding = padding;
    }

    @Override
    public int size() {
        return positions.length;
    }

    @Override
    public int getIndexByPosition(int position) throws ArrayIndexOutOfBoundsException {
        try {
            return positions[position] + this.padding;
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Position " + position + " not found: " + e.getMessage());
            return -1;
        }
    }

    @Override
    public void add(double score, int position) {
        this.scores[current] = score;
        this.positions[current] = position;
        this.current++;
    }

    @Override
    public void addArray(double[] scoreTMP, int[] positionTMP) {
        for (int i = 0; i < scoreTMP.length; i++) {
            this.scores[current] = scoreTMP[i];
            this.positions[current] = positionTMP[i];
            this.current++;
        }
    }

    @Override
    public boolean isScoreGreater(int node, int nodeToCompare) {
        return this.scores[node - this.padding] > this.scores[nodeToCompare - this.padding];
    }

    @Override
    public int getLeastNode(int newNode, Integer[] currentNodes) {
        int leastNode = newNode - this.padding;
        for (int currentNode : currentNodes) {
            if (this.scores[leastNode] > this.scores[currentNode - this.padding]) {
                leastNode = currentNode - this.padding;
            }
        }
        return leastNode + this.padding;
    }

    @Override
    public void sort() {
        sortDescendingByScores();
    }

    @Override
    public void sortPreferences() {
        sortDescendingByScores();
    }

    @Override
    public double getScoreByIndex(int x) {
        return scores[x - this.padding];
    }

    @Override
    public int getIndividualByRank(int rank) throws ArrayIndexOutOfBoundsException {
        try {
            return positions[rank] + this.padding;
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Rank " + rank + " not found: " + e.getMessage());
            return -1;
        }
    }

    @Override
    public int[] getPreferenceForSpecificSet(int currentSet, int setNumber, Map<Integer, Integer> setSizes) {
        int startIndex = 0;
        for (int i = 1; i < setNumber; i++) {
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
    public String toString() {
        StringBuilder result = new StringBuilder("{");
        for (int i = 0; i < scores.length; i++) {
            int pos = positions[i];
            result
                    .append("[")
                    .append(pos)
                    .append(" -> ")
                    .append(String.format("%.2f", scores[pos]))
                    .append("]");
            if (i < scores.length - 1) result.append(", ");
        }
        result.append("}");
        return result.toString();
    }

    private void sortDescendingByScores() {
        double[] cloneScores = scores.clone();
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

    private void heapify(double[] array, int heapSize, int rootIndex) {
        int largestIndex = rootIndex;
        int leftChildIndex = 2 * rootIndex + 1;
        int rightChildIndex = 2 * rootIndex + 2;

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

