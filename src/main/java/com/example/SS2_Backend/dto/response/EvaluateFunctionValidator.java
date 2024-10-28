package com.example.SS2_Backend.dto.response;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.ValidationResult;

@Slf4j
public class EvaluateFunctionValidator implements
        ConstraintValidator<EvaluateFunctionConstraint, String[]> {

    @Override
    public void initialize(EvaluateFunctionConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String[] evaluateFunctions, ConstraintValidatorContext constraintValidatorContext) {
        for (String evaluateFunction: evaluateFunctions) {
            Expression e = new ExpressionBuilder(evaluateFunction)
                    .variable("P1").variable("P2").variable("P3")
                    .variable("P4").variable("P5").variable("P6")
                    .variable("P7").variable("P8").variable("P9")
                    .variable("P10")
                    .variable("W1").variable("W2").variable("W3")
                    .variable("W4").variable("W5").variable("W6")
                    .variable("W7").variable("W8").variable("W9")
                    .variable("W10")
                    .build();
            ValidationResult res = e.validate();
            if (res.isValid()) {
                // TODO
                log.info("{} is VALID, proceeding..", evaluateFunction);
            } else {
                return false;
            }
        }
        return true;
    }
}