package com.example.SS2_Backend.dto.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class RequirementSyntaxValidator implements ConstraintValidator<ValidRequirementSyntax, String[][]> {

    private static final Pattern VALID_PATTERN = Pattern.compile("^(\\d+(?:\\.\\d+)?)(?::(\\d+(?:\\.\\d+)?))?(?:\\+\\+|--)?$");

    @Override
    public void initialize(ValidRequirementSyntax annotation) {}

    @Override
    public boolean isValid(String[][] value, ConstraintValidatorContext context) {
        for (String[] row : value)
            for (String requirement : row)
                if (!VALID_PATTERN.matcher(requirement).matches()) return false;
        return true;
    }
}
