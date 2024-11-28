package com.example.SS2_Backend.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.List;


public class ContainedFromListValidator implements ConstraintValidator<Validator.ContainedFromList, Object> {

    /** Allow null value for field */
    private boolean allowNull;

    /** Field allowed values */
    private List<Object> values;

    @Override
    public void initialize(Validator.ContainedFromList constraintAnnotation) {
        this.allowNull = constraintAnnotation.allowBlank();
        this.values = new ArrayList<>();
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        return false;
    }

}
