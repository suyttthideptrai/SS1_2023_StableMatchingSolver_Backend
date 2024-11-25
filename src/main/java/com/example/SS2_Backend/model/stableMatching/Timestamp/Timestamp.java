package com.example.SS2_Backend.model.stableMatching.Timestamp;

import java.util.*;

public class Timestamp {
    // Store the main time ranges and their corresponding free slots
    private final Map<int[], List<Integer>> timeRanges;

    public Timestamp() {
        this.timeRanges = new HashMap<>();
    }

    /**
     * Parse the input string to store the main time ranges and their corresponding free slots.
     *
     * @param input Input string in the format "[1, 10], [1, 3, 4, 5, 6, 7, 9, 10]"
     */
    public void parseInput(String input) {
        // Step 1: Remove unnecessary characters and split the string
        String cleanedInput = input.replaceAll("\\s+", "");
        String[] parts = cleanedInput.split("\\],\\[");

        // Step 2: Process pairs [range], [freeSlots]
        for (int i = 0; i < parts.length; i += 2) {
            String rangePart = parts[i].replace("[", "").replace("]", "");
            String freePart = parts[i + 1].replace("[", "").replace("]", "");

            // Convert strings to integer arrays
            int[] range = Arrays.stream(rangePart.split(","))
                    .mapToInt(Integer::parseInt)
                    .toArray();
            List<Integer> freeSlots = Arrays.stream(freePart.split(","))
                    .map(Integer::parseInt)
                    .toList();

            // Add to the map
            timeRanges.put(range, freeSlots);
        }
    }

    /**
     * Calculate the fitness function: the ratio of free time slots to total time slots.
     *
     * @return Fitness function value (between 0.0 and 1.0)
     */
    public double calculateFitness() {
        double totalSlots = 0;
        double freeSlotsCount = 0;

        for (Map.Entry<int[], List<Integer>> entry : timeRanges.entrySet()) {
            int[] range = entry.getKey();
            List<Integer> freeSlots = entry.getValue();

            // Calculate the total slots in the range
            int rangeSize = range[1] - range[0] + 1;
            totalSlots += rangeSize;

            // Count the free slots
            freeSlotsCount += freeSlots.size();
        }

        // Return the ratio of free slots
        return totalSlots > 0 ? freeSlotsCount / totalSlots : 0.0;
    }

    /**
     * Compare a new range with existing ranges to check for overlaps.
     *
     * @param newRange The range to compare against existing ranges.
     * @return True if there is an overlap, false otherwise.
     */
    public boolean compareRanges(int[] newRange) {
        for (int[] range : timeRanges.keySet()) {
            if (overlaps(range, newRange)) {
                return true; // Overlap found
            }
        }
        return false; // No overlaps
    }

    // Helper method to check if two ranges overlap
    private boolean overlaps(int[] range1, int[] range2) {
        return range1[0] <= range2[1] && range2[0] <= range1[1];
    }
}