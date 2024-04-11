package com.example.SS2_Backend.model.StableMatching.Matches;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Match Data Structure for Match:One to Many Stable Matching Problem
 * [individual1] => [individual2, individual3, individual4, ...]
 */

@Getter
public class MatchSet {
	private final int IndividualIndex;
	@Getter
	private final int Capacity;
	private final List<Integer> IndividualMatches = new ArrayList<>();

	public MatchSet(int Individual, int Capacity) {
		this.IndividualIndex = Individual;
		this.Capacity = Capacity;
	}

	public void addMatch(int target) {
		if (!IndividualMatches.contains(target)) {
			IndividualMatches.add(target);
		}
	}

	public void unMatch(int target) {
		IndividualMatches.remove((Integer) target);
	}

	public String toString() {
		return "[" + IndividualIndex + "] => " + IndividualMatches;
	}
}
