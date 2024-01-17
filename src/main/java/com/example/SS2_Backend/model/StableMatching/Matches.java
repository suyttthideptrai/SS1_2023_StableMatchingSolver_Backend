package com.example.SS2_Backend.model.StableMatching;

import lombok.Data;

import java.io.Serializable;
import java.util.*;

/**
 * Data Container for Algorithm Result
 * Matches = {Match1, Match2, Match3, ...}
 *
 *
 */
@Data
public class Matches implements Serializable {
	private static final long serialVersionUID = 1L;
	private final int set1Size;
	private final int size;
	private int[] currentCapacities; //Node spaces: index = Individual index /[i] = Current Individual capacity
	private final boolean[][] matches;
	private Set<Integer> leftOvers = new HashSet<>();

	public Matches(int size, int set1Size) {
		this.size = size;
		this.set1Size = set1Size;
		this.currentCapacities = new int[size];
		this.matches = new boolean[set1Size][size - set1Size];
	}

	public MatchSet getSet(int index) {
		MatchSet matchSet = new MatchSet(index);
		for (int i = 0; i < this.size; i++) {
			if(matches[index][i]){
				matchSet.addMatch(i);
			}
			if(matches[i][index]){
				matchSet.addMatch(i);
			}
		}
		return matchSet;
	}

	public void addLeftOver(int index) {
		leftOvers.add(index);
	}

	public int size() {
		return matches.length;
	}

	public boolean isAlreadyMatch(int Node1, int Node2) {
		return matches[Node1][Node2] && matches[Node2][Node1];
	}

	//
	public boolean isFull(int target, int boundCapacity) {
		int currentCapacity = currentCapacities[target];
		return boundCapacity >= currentCapacity;
	}

	public void addMatch(int target, int prefer) {
		int set0;
		int set1;
		if(target < this.set1Size){
			set0 = target;
			set1 = prefer;
		}else{
			set0 = prefer;
			set1 = target;
		}
		matches[set0][set1] = true;
		this.currentCapacities[target]++;
		this.currentCapacities[prefer]++;
	}
	public void unMatch(int target, int nodeToRemove) {
		int set0;
		int set1;
		if(target < this.set1Size){
			set0 = target;
			set1 = nodeToRemove;
		}else{
			set0 = nodeToRemove;
			set1 = target;
		}
		matches[set0][set1] = false;
		this.currentCapacities[target]--;
		this.currentCapacities[nodeToRemove]--;
	}

	public int[] getIndividualMatches(int target) {
		int cap = this.currentCapacities[target];
		int[] result = new int[cap];
		int idx = 0;
		if(target < set1Size){
			for (int i = 0; i < this.size - set1Size; i++) {
				if(idx == cap) break;
				if(matches[target][i]) {
					result[idx] = set1Size + i;
					idx++;
				}
			}
		}else{
			for (int i = 0; i < set1Size; i++) {
				if(idx == cap) break;
				if(matches[i][target]){
					result[idx] = i;
					idx++;
				}
			}
		}
		return result;
	}

	public String toString() {
//		StringBuilder s = new StringBuilder();
//		s.append("Matches {\n");
//		for (MatchSet match : matches) {
//			s.append("[");
//			s.append(match.toString());
//			s.append("]\n");
//		}
//		s.append("}\n");
//		s.append("LeftOvers {");
//		for (Integer leftOver : leftOvers) {
//			s.append("[");
//			s.append(leftOver.toString());
//			s.append("]");
//		}
//		s.append("\n}");
//		return s.toString();
		System.out.println(Arrays.deepToString(this.matches));
		System.out.println(this.leftOvers);
		return "\n";
	}

	public static void main(String[] args) {
		Matches matches = new Matches(5, 10);

		matches.addMatch(1, 4);
		matches.addMatch(1, 5);
		matches.addMatch(1, 6);

		matches.addMatch(2, 3);
		matches.addMatch(2, 1);
		matches.addMatch(2, 8);

		matches.addMatch(3, 7);
		matches.addMatch(3, 11);


		matches.addLeftOver(12);
		matches.addLeftOver(10);
		matches.addLeftOver(9);

//        matches.disMatch(1,4);
		//matches.remove(2);

		System.out.println(matches.isFull(1, 3));
		System.out.println(matches.isFull(2, 3));
		System.out.println(matches.isFull(3, 3));
		System.out.println(matches);
	}
}
