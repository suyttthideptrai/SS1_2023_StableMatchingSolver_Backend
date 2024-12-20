package com.example.SS2_Backend.ss.smt.implement;

import com.example.SS2_Backend.constants.MatchingConst;
import com.example.SS2_Backend.dto.mapper.StableMatchingProblemMapper;
import com.example.SS2_Backend.dto.request.NewStableMatchingProblemDTO;
import com.example.SS2_Backend.ss.smt.Matches;
import com.example.SS2_Backend.util.SampleDataGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.moeaframework.core.Solution;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MTMNodeCapacityTest {
    NewStableMatchingProblemDTO newStableMatchingProblemDTO;
    int numberOfIndividuals1;
    int numberOfIndividuals2;
    String[] propNames;

    @BeforeEach
    public void setUp() {
        numberOfIndividuals1 = 20;
        numberOfIndividuals2 = 200;
        propNames = new String[]{"Properties 1", "Properties 2", "Properties 3", "Properties 4", "Properties 5"};

        SampleDataGenerator sampleData = new SampleDataGenerator(numberOfIndividuals1, numberOfIndividuals2, propNames);
        newStableMatchingProblemDTO = sampleData.generateDto();
    }

    @Test
    public void testNodeCapacity() {
        MTMProblem problem = StableMatchingProblemMapper.toMTM(newStableMatchingProblemDTO);
        // Tạo một Solution để kiểm tra và lấy các Matches được trả về từ Solution
        Solution solution = problem.newSolution();
        problem.evaluate(solution);
        Matches matches = (Matches) solution.getAttribute(MatchingConst.MATCHES_KEY);
        // Check if each node's capacity is respected
        // size here means the node's number?
        for (int i = 0; i < newStableMatchingProblemDTO.getNumberOfIndividuals(); i++) {
            // Getting individual
            int capacity = problem.getMatchingData().getCapacityOf(i);
            int matchedCount = matches.getSetOf(i).size();
            assertTrue(matchedCount <= capacity, "Node " + i + " has exceeded its capacity");
        }
    }
}