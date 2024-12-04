package com.example.SS2_Backend.ss.smt;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

/**
 * Data Structure for result of StableMatchingExtra algorithm
 * Matches = {Match1, Match2, Match3, ...}
 * Match can be an Object of "Pair" or "MatchSet" Class, both Implement "MatchItem" Interface
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Matches implements Serializable {

    /** size of Matches */
    final int size;

    /** matches data */
    final TreeSet<Integer>[] matches;

    /** leftover individuals go here =)) */
    final TreeSet<Integer> leftOvers = (new TreeSet<>());

    @SuppressWarnings("unchecked")
    public Matches(int size) {
        this.size = size;
        this.matches = new TreeSet[size];
        for (int i = 0; i < size; i++) {
            this.matches[i] = new TreeSet<>();
        }
    }

    /**
     * get matched individual(s) of targetIndividual
     *
     * @param targetIndividual int
     * @return matched individual(s)
     */
    public Set<Integer> getSetOf(int targetIndividual) {
        return matches[targetIndividual];
    }

    /**
     * add node to leftOvers :((
     *
     * @param leftOverNode int
     */
    public void addLeftOver(int leftOverNode) {
        leftOvers.add(leftOverNode);
    }

    /**
     * as name
     *
     * @return int
     */
    public int size() {
        return matches.length;
    }

    /**
     * check if node is matched with other node(s)
     *
     * @param node to check
     * @return as title true
     */
    public boolean isMatched(int node) {
        return !matches[node].isEmpty();
    }

    /**
     * check if two nodes is matched
     *
     * @param node1 node1
     * @param node2 node2
     * @return as title true
     */
    public boolean isMatched(int node1, int node2) {
        return matches[node1].contains(node2) || matches[node2].contains(node1);
    }

    /**
     * check if node number of connections reach its capacity
     *
     * @param targetNode node to check
     * @param targetNodeCapacity node cap
     * @return as title true
     */
    public boolean isFull(int targetNode, int targetNodeCapacity) {
        int currentSize = this.getSetOf(targetNode).size();
        return currentSize >= targetNodeCapacity;
    }

    /**
     * add match
     *
     * @param node as name
     * @param nodeToAdd as name
     */
    public void addMatch(int node, int nodeToAdd) {
        matches[node].add(nodeToAdd);
    }

    /**
     * remove match bidirectionally
     *
     * @param node as name
     * @param nodeToRemove as name
     */
    public void removeMatch(int node, int nodeToRemove) {
        matches[node].remove(nodeToRemove);
    }

    /**
     * add match bidirectionally
     *
     * @param node1 index of node1
     * @param node2 index of node2
     */
    public void addMatchBi(int node1, int node2) {
        matches[node1].add(node2);
        matches[node2].add(node1);
    }

    /**
     * remove match bidirectionally
     *
     * @param node1 index of node1
     * @param node2 index of node2
     */
    public void removeMatchBi(int node1, int node2) {
        matches[node1].remove(node2);
        matches[node2].remove(node1);
    }

    /**
     * as name
     *
     * @return StringBuilder
     */
    public StringBuilder toStringBuilder() {
        StringBuilder sb = new StringBuilder();
        int i = 0;

        for (Set<Integer> matchSet : this.matches) {
            sb.append("[").append(i).append(" -> ").append(matchSet).append("] ").append("\n");
            i++;
        }

        sb.append("Left Overs: ").append(leftOvers).append("\n");
        return sb;
    }

    /**
     * as name
     *
     * @return String
     */
    @Override
    public String toString() {
       return this.toStringBuilder().toString();
    }


}
