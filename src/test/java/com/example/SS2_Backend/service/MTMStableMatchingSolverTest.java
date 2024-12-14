package com.example.SS2_Backend.service;

import com.example.SS2_Backend.dto.request.NewStableMatchingProblemDTO;
import com.example.SS2_Backend.util.SampleDataGenerator;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

public class MTMStableMatchingSolverTest {
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
        newStableMatchingProblemDTO = sampleData.generate();
    }

    @Test
    public void testStableMatchingSolver() {
        // Set up the solver
        StableProblemService solver = new StableProblemService(null);
        // Run the solver
        solver.solve(newStableMatchingProblemDTO);
    }
}