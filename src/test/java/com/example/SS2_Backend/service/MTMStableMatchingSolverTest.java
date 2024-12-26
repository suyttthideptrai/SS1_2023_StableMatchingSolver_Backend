package com.example.SS2_Backend.service;

import com.example.SS2_Backend.dto.request.NewStableMatchingProblemDTO;
import com.example.SS2_Backend.util.MatchingProblemType;
import com.example.SS2_Backend.util.SampleDataGenerator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.moeaframework.core.NondominatedPopulation;

import java.util.Objects;
import java.util.Set;

import static org.junit.Assert.assertFalse;

public class MTMStableMatchingSolverTest {
    NewStableMatchingProblemDTO newStableMatchingProblemDTO;
    int numberOfIndividuals1;
    int numberOfIndividuals2;
    int numberOfProperties;
    private Validator validator;

    @BeforeEach
    public void setUp() {
        numberOfIndividuals1 = 20;
        numberOfIndividuals2 = 200;
        SampleDataGenerator sampleData = new SampleDataGenerator(MatchingProblemType.MTM, numberOfIndividuals1, numberOfIndividuals2, numberOfProperties);
        newStableMatchingProblemDTO = sampleData.generateDto();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

    }

    @Test
    public void testEvaluateFunctions() {
        newStableMatchingProblemDTO.setEvaluateFunctions(new String[]{"invalid eval func", "invalid eval func"});
        Set<ConstraintViolation<NewStableMatchingProblemDTO>> violations = validator.validate(newStableMatchingProblemDTO);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testFitnessCalculation() {
        newStableMatchingProblemDTO.setEvaluateFunctions(new String[]{"invalid eval func", "invalid eval func"});
        Set<ConstraintViolation<NewStableMatchingProblemDTO>> violations = validator.validate(newStableMatchingProblemDTO);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testStableMatchingSolver() {
        // Set up the solver
        StableProblemService solver = new StableProblemService(null);
        // Run the solver
        solver.solve(newStableMatchingProblemDTO);
    }
}

// - test fitness calculation assert kqua đúng.