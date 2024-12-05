package com.example.SS2_Backend.service;

import org.junit.Test;

public class StableMatchingSolverTest {
    @Test
    public void testStableMatchingSolver() {
        StableMatchingSolverRBO solver = new StableMatchingSolverRBO();
        // Set up the problem
        solver.setNumberOfIndividuals(10);
        solver.setEvaluateFunctionForSet2("P");
        solver.setFitnessFunction("S");
        // Run the solver
        solver.solveStableMatching();
        // No need to assert anything, just make sure it runs without errors
    }

    @Test
    public void testStableMatchingSolverWithCustomFunctions() {
        StableMatchingSolverRBO solver = new StableMatchingSolverRBO();
        // Set up the problem
        solver.setNumberOfIndividuals(10);
        solver.setEvaluateFunctionForSet1("M");
        solver.setEvaluateFunctionForSet2("W");
        solver.setFitnessFunction("SIGMA{S1}");
        // Run the solver
        solver.solveStableMatching();
        // No need to assert anything, just make sure it runs without errors
    }
}