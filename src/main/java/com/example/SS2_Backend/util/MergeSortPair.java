package com.example.SS2_Backend.util;

import java.util.List;
import java.util.ArrayList;
import com.example.SS2_Backend.model.StableMatching.PreferenceList.IndexValue;

public class MergeSortPair {
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

    public static void main(String[] args) {
        // Sample usage
        List<IndexValue> pairList = new ArrayList<>();
        pairList.add(new IndexValue(1, 5.5));
        pairList.add(new IndexValue(2, 6.4));
        pairList.add(new IndexValue(3, 1.22));
        pairList.add(new IndexValue(4, 7.98));

        mergeSort(pairList);

        for (IndexValue pair : pairList) {
            System.out.println(pair);
        }
    }
}
