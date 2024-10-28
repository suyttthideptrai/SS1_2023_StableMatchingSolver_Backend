package com.example.SS2_Backend.dto.response;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EvaluateFunctionValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EvaluateFunctionConstraint {
    String message() default "Invalid fields detected";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}