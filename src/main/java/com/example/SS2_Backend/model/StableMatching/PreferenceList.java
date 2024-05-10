package com.example.SS2_Backend.model.StableMatching;

import lombok.Getter;

import java.util.*;

import static com.example.SS2_Backend.util.Utils.formatDouble;

/**
 * {rank,  score}, {r, s}, {r, s}, ...
 * access by indices
 */
@Getter
public class PreferenceList {
    private final OrderedMap preferenceList = new OrderedMap();
    private double[] scores;

    public PreferenceList() {
    }

    public void transfer(int numberOfIndividual) {
        if (this.preferenceList.isEmpty()) return;
        this.scores = new double[numberOfIndividual];
        for (int i = 0; i < numberOfIndividual; i++) {
                Double score = this.preferenceList.get(i);
                if (score != null) {
                    this.scores[i] = this.preferenceList.get(i);
                }else{
                    this.scores[i] = 0;
                }
        }
    }


    public int size() {
        return this.preferenceList.size();
    }

    public IndexScore getByPosition(int indexOnPreferenceList) {
        double i = this.preferenceList.getPositionScore(indexOnPreferenceList);
        return new IndexScore(indexOnPreferenceList, i);
    }

    //public boolean isEmpty() {return this.preferenceList.isEmpty();}

    public Double getScoreByIndex(int index) {
        return this.preferenceList.get(index);
    }

    public IndexScore getIndexValueByKey(int indexOnIndividualList) {
            return new IndexScore(indexOnIndividualList, this.preferenceList.get(indexOnIndividualList));
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

    public void add(IndexScore indexScore) {
        this.preferenceList.put(indexScore.getIndividualIndex(), indexScore.getScore());
    }

    public void sort() {
        this.preferenceList.sortByValueDescending();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" [");
        int individual;
        for (int i = 0; i < preferenceList.size(); i++) {
            individual = preferenceList.getPositionHolder(i);
            sb.append("Rank ").append(i + 1).append(": ");
            sb.append(individual).append("\t");
            sb.append("Score: ");
            sb.append(formatDouble(preferenceList.get(individual))).append(" |");
        }
        sb.append("]\n");
        return sb.toString();
    }

    @Getter
    public static class IndexScore {
        private final int individualIndex;
        private final double score;

        public IndexScore(int IndividualIndex, double score) {
            this.individualIndex = IndividualIndex;
            this.score = score;
        }

        public String toString() {
            return "Index: " + individualIndex + " Score: " + score;
        }
    }

//    public static class MergeSortPair {
//        public static void mergeSort(List<IndexScore> list) {
//            if (list == null || list.size() <= 1) {
//                return; // Nothing to sort
//            }
//
//            int middle = list.size() / 2;
//            List<IndexScore> left = new ArrayList<>(list.subList(0, middle));
//            List<IndexScore> right = new ArrayList<>(list.subList(middle, list.size()));
//
//            mergeSort(left);
//            mergeSort(right);
//
//            merge(list, left, right);
//        }
//
//        private static void merge(List<IndexScore> list, List<IndexScore> left, List<IndexScore> right) {
//            int leftIndex = 0;
//            int rightIndex = 0;
//            int listIndex = 0;
//
//            while (leftIndex < left.size() && rightIndex < right.size()) {
//                if (left.get(leftIndex).getScore() >= right.get(rightIndex).getScore()) {
//                    list.set(listIndex, left.get(leftIndex));
//                    leftIndex++;
//                } else {
//                    list.set(listIndex, right.get(rightIndex));
//                    rightIndex++;
//                }
//                listIndex++;
//            }
//
//            while (leftIndex < left.size()) {
//                list.set(listIndex, left.get(leftIndex));
//                leftIndex++;
//                listIndex++;
//            }
//
//            while (rightIndex < right.size()) {
//                list.set(listIndex, right.get(rightIndex));
//                rightIndex++;
//                listIndex++;
//            }
//        }
//    }

    public static void main(String[] args) {
        PreferenceList pref = new PreferenceList();
        pref.add(new IndexScore(1, 12.4));
        pref.add(new IndexScore(2, 62.4));
        pref.add(new IndexScore(3, 45.9));
        System.out.println(pref);
        pref.sort();
        pref.transfer(100);
        System.out.println(pref);
        //get leastNode
        IndexScore newNode = new IndexScore(6, 12.4);
        pref.add(newNode);
        System.out.println(pref);
        System.out.println(pref.getLeastNode(2, Set.of(1,2,3)));
        System.out.println(pref.getScoreByIndex(1));
    }
}
