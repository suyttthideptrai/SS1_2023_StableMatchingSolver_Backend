//package com.example.SS2_Backend.dto.response;
//
//
//import com.example.SS2_Backend.dto.request.NewStableMatchingProblemDTO;
//import jakarta.validation.ConstraintValidator;
//import jakarta.validation.ConstraintValidatorContext;
//import lombok.extern.slf4j.Slf4j;
//import net.objecthunter.exp4j.Expression;
//import net.objecthunter.exp4j.ExpressionBuilder;
//import net.objecthunter.exp4j.ValidationResult;
//
//@Slf4j
//public class EvaluateFunctionValidator implements
//        ConstraintValidator<EvaluateFunctionConstraint, NewStableMatchingProblemDTO> {
//
//    @Override
//    public void initialize(EvaluateFunctionConstraint constraintAnnotation) {
//        ConstraintValidator.super.initialize(constraintAnnotation);
//    }
//
//    @Override
//    public boolean isValid(NewStableMatchingProblemDTO response, ConstraintValidatorContext constraintValidatorContext) {
//        for (String evaluateFunction: response.getEvaluateFunction()) {
//            ExpressionBuilder e = new ExpressionBuilder(evaluateFunction);
//            for (int i = 1; i <= response.getNumberOfProperty(); i++) {
//                e.variable(String.format("P%d", i)).variable(String.format("W%d", i));
//            }
//
//            Expression expressionValidator = e.build();
//            ValidationResult res = expressionValidator.validate();
//
//            if (res.isValid()) {
//                log.info("{} is VALID, proceeding..", evaluateFunction);
//            } else {
//                return false;
//            }
//        }
//        return true;
//    }
//}