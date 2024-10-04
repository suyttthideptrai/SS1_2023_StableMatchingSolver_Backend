package com.example.SS2_Backend.model.StableMatching.Matches;

import lombok.Data;
import java.io.Serializable;
import java.util.*;

@Data
public class MatchesOTO implements Serializable {
    private static final long serialVersionUID = 2L;
    private final List<Integer> matches;
    private final Set<Integer> leftOver;

    public MatchesOTO(int[] matches, Set<Integer> leftOver) {
        this.matches = new ArrayList<>();
        for (int match : matches) this.matches.add(match);
        this.leftOver = leftOver;
    }
    public static MatchesOTO getEmptyObject() {
        return new MatchesOTO(new int[0], new HashSet<>());
    }
    public boolean isEmpty() {
        return matches.isEmpty();
    }
}