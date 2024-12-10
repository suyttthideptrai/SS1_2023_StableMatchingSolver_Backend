package com.example.SS2_Backend.ss.smt.implement;

import com.example.SS2_Backend.constants.MatchingConst;
import com.example.SS2_Backend.dto.mapper.StableMatchingProblemMapper;
import com.example.SS2_Backend.dto.request.NewStableMatchingProblemDTO;
import com.example.SS2_Backend.ss.smt.Matches;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.moeaframework.core.Solution;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MTMNodeCapacityTest {
    NewStableMatchingProblemDTO newStableMatchingProblemDTO = new NewStableMatchingProblemDTO();

    @BeforeEach
    public void setUp() {
        String[][] individualRequirements = {
                {"0.15", "0.15"},
                {"0.15", "0.15"},
                {"0.15", "0.15"},
                {"0.15", "0.15"},
                {"0.15", "0.15"}
        };
        double[][] individualWeights = {
                {0.5, 0.5},
                {0.4, 0.6},
                {0.3, 0.7},
                {0.6, 0.4},
                {0.7, 0.3}
        };
        double[][] individualProperties = {
                {10, 5},
                {8, 6},
                {12, 4},
                {9, 7},
                {11, 3}
        };
        int[] individualSetIndices = {0, 1, 1, 1, 1};
        newStableMatchingProblemDTO.setNumberOfIndividuals(5);
        newStableMatchingProblemDTO.setIndividualProperties(individualProperties);
        newStableMatchingProblemDTO.setIndividualWeights(individualWeights);
        newStableMatchingProblemDTO.setIndividualRequirements(individualRequirements);
        newStableMatchingProblemDTO.setIndividualSetIndices(individualSetIndices);
    }

    @Test
    public void testNodeCapacity() {
        // Số lượng Node
        int size = 5;
        MTMProblem problem = StableMatchingProblemMapper.toMTM(newStableMatchingProblemDTO);
        // Tạo một Solution để kiểm tra và lấy các Matches được trả về từ Solution
        Solution solution = problem.newSolution();
        problem.evaluate(solution);
        Matches matches = (Matches) solution.getAttribute(MatchingConst.MATCHES_KEY);
        // Check if each node's capacity is respected
        for (int i = 0; i < size; i++) {
            int capacity = problem.getMatchingData().getCapacityOf(i);
            int matchedCount = matches.getSetOf(i).size();
            assertTrue(matchedCount <= capacity, "Node " + i + " has exceeded its capacity");
        }
    }
}