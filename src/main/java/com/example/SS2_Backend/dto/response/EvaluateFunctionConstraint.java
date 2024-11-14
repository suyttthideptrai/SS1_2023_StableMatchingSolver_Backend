//package com.example.SS2_Backend.dto.response;
//
//import jakarta.validation.Constraint;
//import jakarta.validation.Payload;
//
//import java.lang.annotation.*;
//
//import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
//import static java.lang.annotation.ElementType.TYPE;
//
//@Documented
//@Constraint(validatedBy = EvaluateFunctionValidator.class)
//@Target({TYPE, ANNOTATION_TYPE})
//@Retention(RetentionPolicy.RUNTIME)
//public @interface EvaluateFunctionConstraint {
//    String message() default "INVALID EVALUATE FUNCTIONS";
//    Class<?>[] groups() default {};
//    Class<? extends Payload>[] payload() default {};
//}