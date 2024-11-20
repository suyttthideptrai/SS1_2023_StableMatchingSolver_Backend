package com.example.SS2_Backend.ss.smt.preference;

import com.example.SS2_Backend.ss.smt.match.Matches;

import java.util.List;

/**
 * Wrapper class to interact with big list of preference list
 */
public class PreferenceListWrapper {

    /** preference lists */
    List<PreferenceList> lists;

    public PreferenceListWrapper(List<PreferenceList> lists) {
        this.lists = lists;
    }

    /**
     * get preference list
     *
     * @param idx position of individual
     * @return Preference list
     */
    public PreferenceList get(int idx) {
        return lists.get(idx);
    }

    /**
     * get all satisfaction based on matches
     *
     * @param matches matching result
     * @return satisfactions
     */
    public double[] getAllSatisfactions(Matches matches) {
        double[] satisfactions = new double[lists.size()];
        //TODO: implement
        return satisfactions;
    }

}
