package com.example.SS2_Backend.model.StableMatching.Matches;

import lombok.Getter;

import java.io.Serializable;
import java.util.*;

public class MatchesOTO implements Serializable {
    private static final long serialVersionUID = 2L;
    private final List<Integer> matches;
    private final Set<Integer> matched;
    @Getter
    private final Set<Integer> leftOver;

    public MatchesOTO(int n) {
        matches = new ArrayList<>();
        while (matches.size() < n) matches.add(-1);
        matched = new HashSet<>();
        leftOver = new HashSet<>();
    }
    public Matches toMatches() {
        Matches res = new Matches(matched.size() + leftOver.size());
        for (int i = 0; i < matches.size(); i++) {
            if (matches.get(i) == -1) continue;
            res.addMatch(i, matches.get(i));
        }
        for (int e : leftOver) res.addLeftOver(e);
        return res;
    }

    public void addLeftover(int target) {
        leftOver.add(target);
    }
    public List<Integer> getList() {
        return matches;
    }
    public boolean isEmpty() {return matches.isEmpty();}

    public void link(int a, int b) {
        matched.add(a);
        matched.add(b);
        matches.set(a, b);
        matches.set(b, a);
    }
    public void relinkWith(int a, int b, int interrupter) {
        matches.set(a, interrupter);
        matches.set(b, -1);
        matches.set(interrupter, a);
        matched.remove(b);
        matched.add(interrupter);
    }
    public boolean isLinkedWith(int a, int b) {
        if (!matched.contains(a) || !matched.contains(b)) return false;
        return matches.get(a) == b && matches.get(b) == a;
    }
    public boolean isLinked(int target) {
        return matched.contains(target);
    }
    public int getPartner(int target) {
        return matches.get(target);
    }

}