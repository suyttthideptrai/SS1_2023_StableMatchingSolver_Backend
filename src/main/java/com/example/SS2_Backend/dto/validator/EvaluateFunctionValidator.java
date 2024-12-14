package com.example.SS2_Backend.dto.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.Objects;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EvaluateFunctionValidator implements ConstraintValidator<ValidEvaluateFunction, String[]> {

    // Regex to extract different types of variables
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("(P\\d+|W\\d+|M\\d+|S\\(\\d+\\)|SIGMA\\{[^}]+\\})");

    @Override
    public boolean isValid(String[] values, ConstraintValidatorContext context) {
        for (String func : values) {
            // Allow 'default' as a special case
            if (Objects.equals(func, "default")) return true;

            // Remove all whitespaces for easier parsing
            String cleanFunc = func.replaceAll("\\s+", "");

            try {
                // Extract all variables
                Set<String> variables = extractVariables(cleanFunc);

                // Create an expression with placeholders for variables
                ExpressionBuilder builder = new ExpressionBuilder(cleanFunc);

                // Add variables with placeholder values
                for (String var : variables) {
                    builder.variable(var);
                }

                // Try to build and validate the expression
                Expression expression = builder.build();

                // Set dummy values to test evaluation
                for (String var : variables) {
                    expression.setVariable(var, 1.0);
                }

                // Attempt to evaluate
                expression.evaluate();

                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    private Set<String> extractVariables(String func) {
        Set<String> variables = new HashSet<>();
        Matcher matcher = VARIABLE_PATTERN.matcher(func);

        while (matcher.find()) {
            variables.add(matcher.group(1));
        }

        return variables;
    }
}