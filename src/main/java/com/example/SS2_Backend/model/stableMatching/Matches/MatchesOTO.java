package com.example.SS2_Backend.model.stableMatching.Matches;

import lombok.Data;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Data Structure for result of StableMatchingOTOProblem algorithm only.
 * This minified unnecessary attribute, but need to be converted to Matches as result.
 */
@Data
public class MatchesOTO implements Serializable {
    private static final long serialVersionUID = 2L;
    private final Integer[] matches;
    private final Set<Integer> leftOver;

    public MatchesOTO(Integer[] matches, Set<Integer> leftOver) {
        this.matches = matches;
        this.leftOver = leftOver;
    }
    public Matches toMatches() {
        Matches obj = new Matches(matches.length);
        for (int match : matches) {
            if (match == -1) continue;
            obj.addMatch(match, matches[match]);
            obj.addMatch(matches[match], match);
        }
        for(int individual: leftOver) obj.addLeftOver(individual);
        return obj;
    }

    public boolean isEmpty() {
        return matches.length == 0;
    }
}