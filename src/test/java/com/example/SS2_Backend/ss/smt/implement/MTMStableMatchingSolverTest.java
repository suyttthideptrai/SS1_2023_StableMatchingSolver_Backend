package com.example.SS2_Backend.ss.smt.implement;

import com.example.SS2_Backend.dto.request.NewStableMatchingProblemDTO;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

public class MTMStableMatchingSolverTest {
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
    public void testStableMatchingSolver() {
        // Set up the solver
        StableMatchingSolverRBO solver = new StableMatchingSolverRBO(null);
        // Run the solver
        solver.solve(newStableMatchingProblemDTO);
    }
}