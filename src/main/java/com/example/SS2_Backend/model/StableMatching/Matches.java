package com.example.SS2_Backend.model.StableMatching;

import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


@Data
public class Matches implements Serializable {
	/**
	 * Data Container for Algorithm Result
	 * Matches = {Match1, Match2, Match3, ...}
	 * Base Data Structure: Adjacency Matrix boolean[size1][size1]
	 *
	 * @global matches
	 * #if there exist a match of 2 Nodes: x and y where x and y is indices of the two inside total node population
	 * then => matches[x][y-size1] is equivalent to TRUE (size1 is the number of Nodes in the first set)
	 *
	 * @global currentCapacities
	 * this array records changes of nodes capacities. If method addMatch(int Node, int preferNode) is executed
	 * capacities of Node & preferNode both increment by one. Vice versa for unMatch(int Node, int nodeToRemove) (decrement)
	 *
	 * @global leftOvers
	 * this Set holds Nodes that not contain any connect to other nodes (not having edge(s))
	 */
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
	public boolean checkSetOne(int index){
		return index<set1Size;
	}
	public Set<Integer> getSet(int index) {
		Set<Integer> matchedSet = new HashSet<>();
		if(checkSetOne(index)){
			for (int i = 0; i < size - set1Size; i++) {
				if(matches[index][i]){
					matchedSet.add(i+set1Size);
				}
			}
		}else{
			for (int i = 0; i < set1Size; i++) {
				if(matches[index][i]){
					matchedSet.add(i);
				}
			}
		}
		return matchedSet;
	}

	public void addLeftOver(int index) {
		leftOvers.add(index);
	}

	public int size() {
		return this.size;
	}

	public boolean isAlreadyMatch(int Node1, int Node2) {
		if(checkSetOne(Node1)){
			return matches[Node1][Node2-set1Size];
		}else{
			return matches[Node2][Node1-set1Size];
		}
	}

	//
	public boolean isFull(int target, int boundCapacity) {
		int currentCapacity = currentCapacities[target];
		return boundCapacity >= currentCapacity;
	}

	public void addMatch(int index, int prefer) {
		int set0;
		int set1;
		if(checkSetOne(index)){
			set0 = index;
			set1 = prefer-set1Size;
		}else{
			set0 = prefer;
			set1 = index-set1Size;
		}
		matches[set0][set1] = true;
		this.currentCapacities[index]++;
		this.currentCapacities[prefer]++;
	}
	public void unMatch(int index, int nodeToRemove) {
		int set0;
		int set1;
		if(checkSetOne(index)){
			set0 = index;
			set1 = nodeToRemove-this.set1Size;
		}else{
			set0 = nodeToRemove;
			set1 = index-this.set1Size;
		}
		matches[set0][set1] = false;
		this.currentCapacities[index]--;
		this.currentCapacities[nodeToRemove]--;
	}

	public int[] getIndividualMatches(int target) {
		int cap = this.currentCapacities[target];
		int[] result = new int[cap];
		int idx = 0;
		if(checkSetOne(target)){
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
				if(matches[i][target-set1Size]){
					result[idx] = i;
					idx++;
				}
			}
		}
		return result;
	}

	public String toString() {
		System.out.println(Arrays.deepToString(this.matches));
		System.out.println(this.leftOvers);
		return "\n";
	}

	public static void main(String[] args) {
		Matches matches = new Matches(12, 3);

		matches.addMatch(0, 4);
		matches.addMatch(0, 5);
		matches.addMatch(0, 6);

		matches.addMatch(1, 3);
		matches.addMatch(1, 9);
		matches.addMatch(1, 7);

		matches.addMatch(2, 8);
		matches.addMatch(2, 10);
		matches.addMatch(2, 11);


//		matches.addLeftOver(12);
//		matches.addLeftOver(13);
//		matches.addLeftOver(11);

//        matches.disMatch(1,4);
		//matches.remove(2);

		System.out.println(matches.isFull(1, 3));
		System.out.println(matches.isFull(2, 3));
		System.out.println(matches.isFull(3, 3));
		System.out.println(matches);
	}
}
