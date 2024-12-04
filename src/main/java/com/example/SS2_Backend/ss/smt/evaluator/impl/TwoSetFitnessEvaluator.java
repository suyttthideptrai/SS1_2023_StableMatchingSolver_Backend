package com.example.SS2_Backend.ss.smt.evaluator.impl;

import com.example.SS2_Backend.ss.smt.MatchingData;
import com.example.SS2_Backend.ss.smt.evaluator.FitnessEvaluator;
import com.example.SS2_Backend.util.EvaluatorUtils;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.Objects;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.DoubleStream;

import static com.example.SS2_Backend.util.StringExpressionEvaluator.*;

/**
 * Compatible with Two Set Matching Problems only
 */
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class TwoSetFitnessEvaluator implements FitnessEvaluator {

    private final MatchingData matchingData;

    @Override
    public double defaultFitnessEvaluation(double[] satisfactions) {
        return 0;
    }

    @Override
    public double withFitnessFunctionEvaluation(double[] satisfactions, String fitnessFunction) {

        StringBuilder tmpSB = new StringBuilder();
        for (int c = 0; c < fitnessFunction.length(); c++) {
            char ch = fitnessFunction.charAt(c);
            if (ch == 'S') {
                if (Objects.equals(fitnessFunction.substring(c, c + 5), "SIGMA")) {
                    if (fitnessFunction.charAt(c + 5) != '{') {
                        System.err.println("Missing '{'");
                        System.err.println(fitnessFunction);
                        throw new RuntimeException("Missing '{' after Sigma function");
                    } else {
                        int expressionStartIndex = c + 6;
                        int expressionLength = EvaluatorUtils
                                .getSigmaFunctionExpressionLength(fitnessFunction, expressionStartIndex);
                        String expression = fitnessFunction.substring(expressionStartIndex,
                                expressionStartIndex + expressionLength);
                        double val = this.sigmaCalculate(satisfactions, expression);
                        tmpSB.append(convertToStringWithoutScientificNotation(val));
                        c += expressionLength + 3;
                    }
                }
                // Check for F(index) pattern
                if (c + 3 < fitnessFunction.length() && fitnessFunction.charAt(c + 1) == '(' &&
                        fitnessFunction.charAt(c + 3) == ')') {
                    if (isNumericValue(fitnessFunction.charAt(c + 2))) {
                        int set = Character.getNumericValue(fitnessFunction.charAt(c + 2));
                        //Calculate SUM
                        tmpSB.append(convertToStringWithoutScientificNotation(DoubleStream
                                .of(getSatisfactoryOfASetByDefault(satisfactions, set))
                                .sum()));
                    }
                }
                c += 3;
            } else if (ch == 'M') {
                int ssLength = AfterTokenLength(fitnessFunction, c);
                int positionOfM = Integer.parseInt(fitnessFunction.substring(c + 1,
                        c + 1 + ssLength));
                if (positionOfM < 0 || positionOfM > matchingData.getSize()) {
                    throw new IllegalArgumentException(
                            "invalid position after variable M: " + positionOfM);
                }
                double valueOfM = satisfactions[positionOfM - 1];
                tmpSB.append(valueOfM);
                c += ssLength;
            } else {
                //No occurrence of W/w/P/w
                tmpSB.append(ch);
            }
        }
        System.out.println(tmpSB);
        return new ExpressionBuilder(tmpSB.toString())
                .build()
                .evaluate();
    }

    private double sigmaCalculate(double[] satisfactions, String expression) {
        double[] streamValue = null;
        String regex = null;
        for (int i = 0; i < expression.length() - 1; i++) {
            char ch = expression.charAt(i);
            if (ch == 'S') {
                char set = expression.charAt(i + 1);
                switch (set) {
                    case '1':
                        streamValue = getSatisfactoryOfASetByDefault(satisfactions, 0);
                        regex = "S1";
                        break;
                    case '2':
                        streamValue = getSatisfactoryOfASetByDefault(satisfactions, 1);
                        regex = "S2";
                        break;
                    default:
                        throw new IllegalArgumentException(
                                "Illegal value after S regex in sigma calculation: " + expression);
                }
            }
        }
        if (regex == null) {
            return 0;
        }
        Expression exp = new ExpressionBuilder(expression)
                .variables(regex)
                .build();
        String finalRegex = regex;
        DoubleUnaryOperator calculator = x -> {
            exp.setVariable(finalRegex, x);
            return exp.evaluate();
        };
        return DoubleStream
                .of(streamValue)
                .map(calculator)
                .sum();
    }

    private double[] getSatisfactoryOfASetByDefault(double[] satisfactions, int set) {
        int setTotal = this.matchingData.getTotalIndividualOfSet(set);
        double[] result = new double[setTotal];
        for (int i = 0; i < matchingData.getSize(); i++) {
            if (Objects.equals(matchingData.getSetNoOf(i), set)) {
                setTotal --;
                result[i] = satisfactions[i];
            }
            if (Objects.equals(setTotal, 0)) {
                break;
            }
        }
        return result;
    }
}
