package com.example.SS2_Backend.dto.validator;

import com.example.SS2_Backend.dto.request.NewStableMatchingProblemDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IndividualArraysSizeValidator implements ConstraintValidator<ValidIndividualArraysSize, NewStableMatchingProblemDTO> {

    @Override
    public void initialize(ValidIndividualArraysSize annotation) {}

    @Override
    public boolean isValid(NewStableMatchingProblemDTO dto, ConstraintValidatorContext context) {
        int expectedCount = dto.getNumberOfIndividuals();
        return
                dto.getIndividualSetIndices().length == expectedCount &&
                dto.getIndividualCapacities().length == expectedCount &&
                dto.getIndividualRequirements().length == expectedCount &&
                dto.getIndividualWeights().length == expectedCount &&
                dto.getIndividualProperties().length == expectedCount;
    }
}
