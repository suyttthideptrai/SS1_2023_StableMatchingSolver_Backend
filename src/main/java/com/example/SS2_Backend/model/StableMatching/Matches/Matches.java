package com.example.SS2_Backend.model.StableMatching.Matches;

import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.util.*;

/**
 * Data Container for Algorithm Result
 * Matches = {Match1, Match2, Match3, ...}
 * Match can be an Object of "Pair" or "MatchSet" Class, both Implement "MatchItem" Interface
 */
@Data
public class Matches implements Serializable {
	private static final long serialVersionUID = 1L;
	private final Vector<Set<Integer>> matches;
	private final Set<Integer> leftOvers = new HashSet<>();
	public Matches(int cap) {
		this.matches = new Vector<>(cap);
		for (int i = 0; i < cap; i++) {
			this.matches.add(new HashSet<>());
		}
	}
	public Set<Integer> getSet(int index) {
		return matches.get(index);
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
		matches.get(target).add(prefer);
	}

	public void disMatch(int target, int nodeToRemove) {
		matches.get(target).remove(nodeToRemove);
	}

	public Set<Integer> getIndividualMatches(int target) {
		return matches.get(target);
	}

	public String toString() {
		int i = 0;
		for(Set<Integer> matchSet : this.matches){
			System.out.println("[" + i + "] " + " -> " +  matchSet);
			i++;
		}
		return leftOvers + "\n";
	}

	public static void main(String[] args) {
//		Matches matches = new Matches();
//		matches.add(new MatchSet(1, 3));
//		matches.add(new MatchSet(2, 4));
//		matches.add(new MatchSet(3, 2));
//
//		matches.addMatch(1, 4);
//		matches.addMatch(1, 5);
//		matches.addMatch(1, 6);
//
//		matches.addMatch(2, 3);
//		matches.addMatch(2, 1);
//		matches.addMatch(2, 8);
//
//		matches.addMatch(3, 7);
//		matches.addMatch(3, 11);
//
//
//		matches.addLeftOver(12);
//		matches.addLeftOver(10);
//		matches.addLeftOver(9);
//
////        matches.disMatch(1,4);
//		//matches.remove(2);
//
//		System.out.println(matches.isFull(1));
//		System.out.println(matches.isFull(2));
//		System.out.println(matches.isFull(3));
//		System.out.println(matches);
	}
}
