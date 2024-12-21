package com.example.SS2_Backend.dto.validator;

import com.example.SS2_Backend.constants.MatchingConst;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EvaluateFunctionValidator implements ConstraintValidator<ValidEvaluateFunction, String[]> {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("(P\\d+|W\\d+|R\\d+)");

    @Override
    public boolean isValid(String[] values, ConstraintValidatorContext context) {
        for (String func : values) {
            if (func.equalsIgnoreCase(MatchingConst.DEFAULT_EVALUATE_FUNC)) continue;
            String cleanFunc = func.replaceAll("\\s+", "");
            try {
                Set<String> variables = extractVariables(cleanFunc);
                ExpressionBuilder builder = new ExpressionBuilder(cleanFunc);
                for (String var : variables) builder.variable(var);
                Expression expression = builder.build();
                for (String var : variables) expression.setVariable(var, 1.0);
                expression.evaluate();
            } catch (Exception e) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Invalid evaluate function syntax: '" + func + "'")
                        .addConstraintViolation();
                return false;
            }
        }
        return true;
    }

    private Set<String> extractVariables(String func) {
        Set<String> variables = new HashSet<>();
        Matcher matcher = VARIABLE_PATTERN.matcher(func);
        while (matcher.find()) variables.add(matcher.group(1));
        return variables;
    }
}