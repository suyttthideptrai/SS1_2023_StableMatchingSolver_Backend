package com.example.SS2_Backend.model.stableMatching.Matches;

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
    public Matches toMatches() {
        Matches obj = new Matches(matches.size());
        for (int match : matches) {
            obj.addMatch(match, matches.get(match));
            obj.addMatch(matches.get(match), match);
        }
        for(int individual: leftOver) obj.addLeftOver(individual);
        return obj;
    }

    public static MatchesOTO getEmptyObject() {
        return new MatchesOTO(new int[0], new HashSet<>());
    }
    public boolean isEmpty() {
        return matches.isEmpty();
    }
}