package com.example.SS2_Backend.util;

import com.example.SS2_Backend.model.StableMatching.Matches.Matches;

import java.util.*;

public class Testing {
	private final int nums;
	private final Matches matches;
	private int[] capacities;
	public Testing(Matches matches, int num, int[] capacities) {
		this.nums = num;
		this.capacities = capacities;
		this.matches = matches;
	}
//	public boolean isValidQuantity(){
//
//	}
	public boolean hasDuplicate(){
		int[] checkArr = capacities;
		int sz = matches.size();
		for (int i = 0; i < sz; i++) {
			Set<Integer> matchSet = matches.getSet(i);
			for (int elm : matchSet) {
				checkArr[elm]--;
				if (checkArr[elm] < 0) return true;
			}
			//System.out.println(Arrays.toString(checkArr));
		}
		Set<Integer> lefts = matches.getLeftOvers();
		for(int elm : lefts){
			checkArr[elm]--;
			if (checkArr[elm] < 0) return true;
			//System.out.println(Arrays.toString(checkArr));
		}
		return false;
	}

	public static void main(String[] args) {
		int[] check = {3,3,3,1,1,1,1,1,1,1,1,1};
		Matches matches = new Matches(12);
		matches.addMatch(0, 4);
		matches.addMatch(0, 5);
		matches.addMatch(0, 6);

		matches.addMatch(1, 3);
		matches.addMatch(1, 10);
		matches.addMatch(1, 8);

		matches.addMatch(2, 7);
		matches.addMatch(2, 11);


		matches.addLeftOver(11);
		matches.addLeftOver(10);
		matches.addLeftOver(9);

	}

}
