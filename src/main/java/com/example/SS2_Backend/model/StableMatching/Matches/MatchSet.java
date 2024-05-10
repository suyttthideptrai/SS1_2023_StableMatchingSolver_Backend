package com.example.SS2_Backend.model.StableMatching.Matches;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

/**
 * Match Data Structure for Match:One to Many Stable Matching Problem
 * [individual1] => [individual2, individual3, individual4, ...]
 */

public class MatchSet {
	private final int IndividualIndex;
	private final Set<Integer> IndividualMatches = new HashSet<>();

	public MatchSet(int Individual) {
		this.IndividualIndex = Individual;
	}

	public void addMatch(int target) {
		IndividualMatches.add(target);
	}

	public void unMatch(int target) {
		IndividualMatches.remove(target);
	}

	public String toString() {
		return "[" + IndividualIndex + "] => " + IndividualMatches;
	}
}
