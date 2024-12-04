package com.example.SS2_Backend.ss.smt.preference;

/**
 * ThreeSetPreferenceList
 * ----------------------
 * Mở rộng từ `PreferenceList` để xử lý bài toán Matching ba tập.
 * ----------------------
 */
public interface ThreeSetPreferenceList extends PreferenceList {

    /**
     * Get all scores for a specific set
     *
     * @param set set no
     * @return array of scores
     */
    double[] getScores(int set);

    /**
     * Add a competitor with a specific score to a specific set
     *
     * @param set   set no
     * @param score score of the competitor
     */
    void add(int set, double score);

    /**
     * Sort all preference lists for each set by descending scores
     */
    void sortAll();
}

