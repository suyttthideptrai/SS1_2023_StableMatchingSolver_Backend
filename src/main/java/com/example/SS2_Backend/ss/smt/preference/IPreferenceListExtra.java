package com.example.SS2_Backend.ss.smt.preference;


import java.util.Map;

public interface IPreferenceListExtra {

    int size();

    int getIndexByPosition(int position) throws ArrayIndexOutOfBoundsException;

    void add(double score, int position);

    void addArray(double[] scoreTMP, int[] positionTMP);

    boolean isScoreGreater(int node, int nodeToCompare);

    int getLeastNode(int newNode, Integer[] currentNodes);

    void sort();

    void sortPreferences();

    double getScoreByIndex(int x);

    int getIndividualByRank(int rank) throws ArrayIndexOutOfBoundsException;

    int[] getPreferenceForSpecificSet(int currentSet, int setNumber, Map<Integer, Integer> setSizes);

    String toString();
}

