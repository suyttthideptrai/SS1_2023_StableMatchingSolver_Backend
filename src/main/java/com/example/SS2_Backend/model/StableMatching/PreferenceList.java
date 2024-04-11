package com.example.SS2_Backend.model.StableMatching;

import lombok.Getter;

import java.util.*;

import static com.example.SS2_Backend.model.StableMatching.PreferenceList.MergeSortPair.mergeSort;
import static com.example.SS2_Backend.util.Utils.formatDouble;

/**
 * {rank,  score}, {r, s}, {r, s}, ...
 * access by indices
 */
@Getter
public class PreferenceList {
    private final List<IndexValue> preferenceList = new ArrayList<>();
    private double[] scores;

    public PreferenceList() {
    }

    public void transfer(int numberOfIndividual) {
        if (this.preferenceList.isEmpty()) return;
        this.scores = new double[numberOfIndividual];
        for (IndexValue value : this.preferenceList) {
            int index = value.getIndividualIndex();
            this.scores[index] = value.getValue();
        }
    }


    public int size() {
        return this.preferenceList.size();
    }

    public IndexValue getByIndex(int indexOnPreferenceList) {
        return this.preferenceList.get(indexOnPreferenceList);
    }

    //public boolean isEmpty() {return this.preferenceList.isEmpty();}

    public IndexValue getIndexValueByKey(int indexOnIndividualList) {
        for (IndexValue indexValue : this.preferenceList) {
            if (indexValue.getIndividualIndex() == indexOnIndividualList) {
                return indexValue;
            }
        }
        return null;
    }

    public int getLeastNode(int newNode, Set<Integer> currentNodes) {
        int leastNode = newNode;
        for (int currentNode : currentNodes) {
            if (this.scores[leastNode] > this.scores[currentNode]) {
                leastNode = currentNode;
            }
        }
        return leastNode;
    }

    public void add(IndexValue indexValue) {
        this.preferenceList.add(indexValue);
    }

    public void sort() {
        mergeSort(this.preferenceList);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" [");
        for (int i = 0; i < preferenceList.size(); i++) {
            sb.append("Rank ").append(i + 1).append(": ");
            sb.append(preferenceList.get(i).getIndividualIndex()).append("\t");
            sb.append("Score: ");
            sb.append(formatDouble(preferenceList.get(i).getValue())).append(" |");
        }
        sb.append("]\n");
        return sb.toString();
    }

    @Getter
    public static class IndexValue {
        private final int IndividualIndex;
        private final double Value;

        public IndexValue(int IndividualIndex, double Value) {
            this.IndividualIndex = IndividualIndex;
            this.Value = Value;
        }

        public String toString() {
            return "Index: " + IndividualIndex + " Score: " + Value;
        }
    }

    public static class MergeSortPair {
        public static void mergeSort(List<IndexValue> list) {
            if (list == null || list.size() <= 1) {
                return; // Nothing to sort
            }

            int middle = list.size() / 2;
            List<IndexValue> left = new ArrayList<>(list.subList(0, middle));
            List<IndexValue> right = new ArrayList<>(list.subList(middle, list.size()));

            mergeSort(left);
            mergeSort(right);

            merge(list, left, right);
        }

        private static void merge(List<IndexValue> list, List<IndexValue> left, List<IndexValue> right) {
            int leftIndex = 0;
            int rightIndex = 0;
            int listIndex = 0;

            while (leftIndex < left.size() && rightIndex < right.size()) {
                if (left.get(leftIndex).getValue() >= right.get(rightIndex).getValue()) {
                    list.set(listIndex, left.get(leftIndex));
                    leftIndex++;
                } else {
                    list.set(listIndex, right.get(rightIndex));
                    rightIndex++;
                }
                listIndex++;
            }

            while (leftIndex < left.size()) {
                list.set(listIndex, left.get(leftIndex));
                leftIndex++;
                listIndex++;
            }

            while (rightIndex < right.size()) {
                list.set(listIndex, right.get(rightIndex));
                rightIndex++;
                listIndex++;
            }
        }
    }

    public static void main(String[] args) {
        PreferenceList pref = new PreferenceList();
        pref.add(new IndexValue(1, 12.4));
        pref.add(new IndexValue(2, 62.4));
        pref.add(new IndexValue(3, 45.9));
        System.out.println(pref);
        pref.sort();
        pref.transfer(100);
        System.out.println(pref);
        //get leastNode
        IndexValue newNode = new IndexValue(6, 12.4);
        pref.add(newNode);
        System.out.println(pref);
        System.out.println(pref.getLeastNode(2, Set.of(1,2,3)));
    }
}
