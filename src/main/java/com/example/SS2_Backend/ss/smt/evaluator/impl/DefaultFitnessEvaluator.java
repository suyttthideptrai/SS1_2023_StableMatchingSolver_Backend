package com.example.SS2_Backend.ss.smt.evaluator.impl;

import com.example.SS2_Backend.ss.smt.evaluator.FitnessEvaluator;
import com.example.SS2_Backend.ss.smt.match.Matches;
import com.example.SS2_Backend.ss.smt.preference.PreferenceList;
import com.example.SS2_Backend.ss.smt.problem.MatchingProblem;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.DoubleStream;

import static com.example.SS2_Backend.util.SMTEvaluatorUtils.*;
import static com.example.SS2_Backend.util.StringExpressionEvaluator.*;

@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class DefaultFitnessEvaluator implements FitnessEvaluator {

    MatchingProblem matchingProblem;

    @Override
    public double[] getAllSatisfactions(Matches matches, List<PreferenceList> preferenceLists) {
        double[] satisfactions = new double[matchingProblem.getProblemSize()];
        int numSet0 = matchingProblem.getPreferencesProvider().getNumberOfIndividualForSet0();
        for (int i = 0; i < numSet0; i++) {
            double setScore = 0.0;
            PreferenceList ofInd = preferenceLists.get(i);
            Set<Integer> SetMatches = matches.getSetOf(i);
            for (int x : SetMatches) {
                setScore += ofInd.getScoreByIndex(x);
            }
            satisfactions[i] = setScore;
        }
        for (int i = numSet0; i < matchingProblem.getProblemSize(); i++) {
            double setScore = 0.0;
            PreferenceList ofInd = preferenceLists.get(i);
            Set<Integer> SetMatches = matches.getSetOf(i);
            for (int x : SetMatches) {
                setScore += ofInd.getScoreByIndex(x);
            }
            satisfactions[i] = setScore;
        }
        return satisfactions;
    }

    @Override
    public double defaultFitnessEvaluation(double[] satisfactions) {
        return Arrays.stream(satisfactions).sum();
    }

    /**
     * Fitness Function Grammar:
     * $: i - index of MatchSet in "matches"
     * $: set - value (1 or 2) represent set 1 (0) or set 2 (1)
     * $: S(set) - Sum of all payoff scores of "set" evaluate by opposite set
     * $: M(i) - Value of specific matchSet's satisfaction eg: M1 (satisfactory of Individual no 1, index 0 in "matches")
     * Supported functions:
     * #: SIGMA{S1} calculate sum of all MatchSet of a belonging set eg: SIGMA{S1}
     * Supported mathematical calculations:
     *     Name             :    Usage
     * 1. absolute       : abs(expression)
     * 2. exponent      : (expression)^(expression)
     * 3. sin                 : sin(expression)
     * 4. cos                 : cos(expression)
     * 5. tan                : tan(expression)
     * 6. logarithm     : log(expression)(expression) Logarithm calculation requires 2 parameters in two separate curly braces
     * 							   Default log calculation (with Math.E constant) could be achieved like this: log(e)(expression)
     * 							   Make sure expression is not negative or the final outcome might be
     * 							   resulted in: NaN / Infinity / - Infinity
     * 7. square root : sqrt(expression)
     */
    @Override
    public double withFitnessFunctionEvaluation(double[] satisfactions, String fnf) {
        StringBuilder tmpSB = new StringBuilder();
        String fitnessFunction = matchingProblem.getFitnessFunction();
        int problemSize = matchingProblem.getProblemSize();
        int numSet0 = matchingProblem.getPreferencesProvider().getNumberOfIndividualForSet0();
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
                        int expressionLength = getSigmaFunctionExpressionLength(fitnessFunction,
                                expressionStartIndex);
                        String expression = fitnessFunction.substring(expressionStartIndex,
                                expressionStartIndex + expressionLength);
                        double val = sigmaCalculate(satisfactions, expression, problemSize, numSet0);
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
                                .of(getSatisfactoryOfASetByDefault(satisfactions, set, problemSize, numSet0))
                                .sum()));
                    }
                }
                c += 3;
            } else if (ch == 'M') {
                int ssLength = AfterTokenLength(fitnessFunction, c);
                int positionOfM = Integer.parseInt(fitnessFunction.substring(c + 1,
                        c + 1 + ssLength));
                if (positionOfM < 0 || positionOfM > matchingProblem.getProblemSize()) {
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
}
