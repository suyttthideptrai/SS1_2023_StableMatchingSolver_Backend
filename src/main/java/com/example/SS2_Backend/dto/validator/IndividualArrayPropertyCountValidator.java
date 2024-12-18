package com.example.SS2_Backend.dto.validator;

import com.example.SS2_Backend.dto.request.NewStableMatchingProblemDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class IndividualArrayPropertyCountValidator implements ConstraintValidator<ValidIndividualArrayPropertyCount, NewStableMatchingProblemDTO> {

    @Override
    public void initialize(ValidIndividualArrayPropertyCount annotation) {}

    @Override
    public boolean isValid(NewStableMatchingProblemDTO dto, ConstraintValidatorContext context) {
        int expectedPropertyCount = dto.getNumberOfProperty();

        return Arrays.stream(dto.getIndividualRequirements()).allMatch(row -> row.length == expectedPropertyCount) &&
                Arrays.stream(dto.getIndividualWeights()).allMatch(row -> row.length == expectedPropertyCount) &&
                Arrays.stream(dto.getIndividualProperties()).allMatch(row -> row.length == expectedPropertyCount);
    }
}
