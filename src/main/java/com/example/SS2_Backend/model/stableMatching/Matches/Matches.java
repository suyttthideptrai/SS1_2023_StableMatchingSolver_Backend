package com.example.SS2_Backend.model.stableMatching.Matches;

import lombok.Getter;

import java.io.Serializable;
import java.util.*;

/**
 * Data Structure for result of StableMatchingExtra algorithm
 * Matches = {Match1, Match2, Match3, ...}
 * Match can be an Object of "Pair" or "MatchSet" Class, both Implement "MatchItem" Interface
 */
@Getter
public class Matches implements Serializable {
    private final Set<Integer>[] matches;
    private final Set<Integer> leftOvers = new TreeSet<>();

    public Matches(int cap) {
        this.matches = new Set[cap];
        for (int i = 0; i < cap; i++) {
            this.matches[i] = new TreeSet<>();
        }
    }

    public Set<Integer> getSet(int index) {
        return matches[index];
    }

    public void addLeftOver(int index) {
        leftOvers.add(index);
    }

    public int size() {
        return matches.length;
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
        matches[target].add(prefer);
    }

    public void disMatch(int target, int nodeToRemove) {
        matches[target]
                .remove(nodeToRemove);
    }

    public Integer[] getIndividualMatches(int target) {
        return matches[target].toArray(new Integer[0]);
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
