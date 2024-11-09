package com.example.SS2_Backend.model.stableMatching;

import lombok.Getter;

import java.util.*;

/**
 * {rank,  score}, {r, s}, {r, s}, ...
 * access by indices
 */
public class PreferenceList extends HashMap<Integer, Double> {
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
}
