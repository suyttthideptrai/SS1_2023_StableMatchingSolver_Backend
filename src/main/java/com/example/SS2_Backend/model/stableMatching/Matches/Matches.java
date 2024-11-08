package com.example.SS2_Backend.model.stableMatching.Matches;

import lombok.Data;

import java.io.Serializable;
import java.util.*;

/**
 * Data Structure for result of StableMatchingExtra algorithm
 * Matches = {Match1, Match2, Match3, ...}
 * Match can be an Object of "Pair" or "MatchSet" Class, both Implement "MatchItem" Interface
 */
@Data
public class Matches implements Serializable {

    private static final long serialVersionUID = 1L;
    private final List<TreeSet<Integer>> matches;
    private final Set<Integer> leftOvers = new TreeSet<>();

    public Matches(int individualCount) {
        matches = new ArrayList<>(individualCount);
        for (int i = 0; i < individualCount; i++) {
            matches.add(new TreeSet<>());
        }
    }

    public Set<Integer> getSet(int index) {
        return matches.get(index );
    }

    public void addLeftOver(int index) {
        leftOvers.add(index);
    }

    public int size() {
        return matches.size();
    }

    public boolean isAlreadyMatch(int Node1, int Node2) {
        Set<Integer> ofNode1 = getSet(Node1);
        return ofNode1.contains(Node2);
    }

    public boolean isFull(int target, int boundCapacity) {
        int currentSize = getSet(target).size();
        return currentSize >= boundCapacity;
    }

    public void addMatch(int target, int prefer) {
        matches.get(target)
                .add(prefer);
    }

    public void disMatch(int target, int nodeToRemove) {
        matches.get(target)
                .remove(nodeToRemove);
    }


    /**
     *
     * @param target
     * @return The copy of current matches
     */
    public Set<Integer> getIndividualMatches(int target) {
        return new TreeSet<>(matches.get(target));
    }

    public String toString() {
        int i = 0;
        for (Set<Integer> matchSet : this.matches) {
            System.out.println("[" + i + " -> " + matchSet + "] ");
            i++;
        }
        return "Left Overs: " + leftOvers + "\n";
    }

}
