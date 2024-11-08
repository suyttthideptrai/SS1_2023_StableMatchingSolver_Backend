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
     * @param nodes list of nodes in this preference list
     * @return null if nodes is empty or nodes given are not in the preference
     * list, else Index of node with the smallest score
     */
    public Optional<Integer> getLeastNode(Set<Integer> nodes) {
        return nodes.stream()
            .min((x, y) -> (get(x) > get(y)) ? y : x) ;
    }
}
