package com.example.SS2_Backend.model.stableMatching;

import java.util.HashMap;
import java.util.Set;

/**
 * An implementation of Preference list for an individual, should provide
 * individual's capacity as initial size to save memory
 */
public class PreferenceMap extends HashMap<Integer, Double> {
    public PreferenceMap(int initialCapacity) {
        /**
         * loadFactor is used to decide when the Hash table is resized, with
         * loadFactor=1 means only resize when the number of keys is greater
         * than initialCapacity {@link HashMap}
         */
        super(initialCapacity, 2);
    }

    /**
     *
     * @param currentMatches list of nodes in this preference list that are matched
     * @param newMatch node in this preference list competing with currentMatches
     * @return return the node with worst score, default to newMatch
     */
    public Integer getLeastNode(int newMatch, Set<Integer> currentMatches) {
        return currentMatches.stream()
            .reduce(newMatch, (x, y) -> (get(x) > get(y)) ? y : x) ;
    }

    public Integer getLeastNode(int newMatch, int currentMatch) {
        return (get(newMatch) > get(currentMatch))
            ? currentMatch
            : newMatch;
    }
}
