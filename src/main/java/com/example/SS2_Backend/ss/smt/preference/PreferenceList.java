package com.example.SS2_Backend.ss.smt.preference;

import java.util.Set;

/**
 * xô lít for PreferenceList
 */
public interface PreferenceList {

    /**
     * get the least score node out of (currentNode and newNode)
     *
     * @param newNode     matching node
     * @param currentNode matched node
     * @return index of the least score node
     */
    int getLeastNode(int newNode, Set<Integer> currentNode);

    /**
     * get lower score node out of two.
     *
     * @param newNode n1
     * @param oldNode n2
     * @return index of the lower score node
     */
    int getLeastNode(int newNode, int oldNode);

    /**
     * get size of this preference list
     *
     * @return size
     */
    int size();

    /**
     * index of given position in this preference list
     *
     * @param position position or rank =_))
     * @return individual index
     */
    int getIndexByPosition(int position);

    /**
     * get last rank/position/option in this preference list
     *
     * @return least rank individual index
     */
    int getLastOption();

    double getScoreByIndex(int index);

    boolean isScoreGreater(int index1, int index2);

}
