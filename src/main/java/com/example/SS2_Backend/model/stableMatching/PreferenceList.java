package com.example.SS2_Backend.model.stableMatching;

import lombok.Getter;

import java.util.*;

/**
 * {rank,  score}, {r, s}, {r, s}, ...
 * access by indices
 */
@Getter
public class PreferenceList extends HashMap<Integer, Double> {

    public int getLeastNode(Integer[] nodes) {
        return Arrays.stream(nodes)
            .reduce(nodes[0], (x, y) -> (get(x) > get(y)) ? y : x);
    }
}
