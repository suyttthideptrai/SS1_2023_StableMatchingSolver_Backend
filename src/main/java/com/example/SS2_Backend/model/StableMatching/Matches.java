package com.example.SS2_Backend.model.StableMatching;

import lombok.Data;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Data Container for Algorithm Result
 * Matches = {Match1, Match2, Match3, ...}
 * Match can be an Object of "Pair" or "MatchSet" Class, both Implement "MatchItem" Interface
 */
@Data
public class Matches implements Serializable {
	private static final long serialVersionUID = 1L;
	private final List<MatchSet> matches = new LinkedList<>();
	private final List<Integer> leftOvers = new LinkedList<>();
	private List<Double> coupleFitness = new LinkedList<>();

	public Matches() {
	}

	public void add(MatchSet match) {
		matches.add(match);
	}

	public void remove(int index) {
		matches.remove(index);
	}

	public MatchSet getSet(int index) {
		return matches.get(index);
	}

	public int findCompany(int target) {
		for (MatchSet match : matches) {
			if (match.getIndividualMatches().contains(target)) {
				return match.getIndividualIndex();
			}
		}
		return 0;
	}

	public void addLeftOver(int index) {
		leftOvers.add(index);
	}

	public int size() {
		return matches.size();
	}

	public boolean alreadyMatch(int Node1, int Node2) {
		for (MatchSet match : matches) {
			if (match.getIndividualIndex() == Node1) {
				if (match.getIndividualMatches().contains(Node2)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isFull(int target) {
		for (MatchSet match : matches) {
			// hmm
			if (match.getIndividualIndex() == target) {
				int cap = match.getCapacity();
				return match.getIndividualMatches().size() >= cap;
			}
		}
		return false;
	}

	public void addMatch(int target, int prefer) {
		for (MatchSet matchSet : matches) {
			if (matchSet.getIndividualIndex() == target) {
				matchSet.addMatch(prefer);
			}
		}
	}

	public void disMatch(int target, int nodeToRemove) {
		for (MatchSet matchSet : matches) {
			if (matchSet.getIndividualIndex() == target) {
				matchSet.unMatch(nodeToRemove);
			}
		}
	}

	public List<Integer> getIndividualMatches(int target) {
		return matches.get(target).getIndividualMatches();
	}

	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("Matches {\n");
		for (MatchSet match : matches) {
			s.append("[");
			s.append(match.toString());
			s.append("]\n");
		}
		s.append("}\n");
		s.append("LeftOvers {");
		for (Integer leftOver : leftOvers) {
			s.append("[");
			s.append(leftOver.toString());
			s.append("]");
		}
		s.append("\n}");
		return s.toString();
	}

	public static void main(String[] args) {
		Matches matches = new Matches();
		matches.add(new MatchSet(1, 3));
		matches.add(new MatchSet(2, 4));
		matches.add(new MatchSet(3, 2));

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

		System.out.println(matches.isFull(1));
		System.out.println(matches.isFull(2));
		System.out.println(matches.isFull(3));
		System.out.println(matches);
	}
}
