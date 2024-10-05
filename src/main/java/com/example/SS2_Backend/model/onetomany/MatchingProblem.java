package com.example.SS2_Backend.model.onetomany;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.variable.Permutation;

import java.util.*;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import static com.example.SS2_Backend.util.StringExpressionEvaluator.*;

@Slf4j
@Data
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class MatchingProblem implements Problem {

    IndividualList individuals;
    List<PreferenceList> preferenceLists;
    PreferenceProvider preferencesProvider;
    String evaluateFunctionForProviders;
    String evaluateFunctionForConsumers;
    String fitnessFunction;
    boolean providerFunctionStatus = false;
    boolean consumerFunctionStatus = false;
    boolean fitnessFunctionStatus = false;

    public void setPopulation(ArrayList<Individual> individuals, String[] propertiesNames) {
        this.individuals = new IndividualList(individuals, propertiesNames);
        initializeFields();
    }

    private void initializeFields() {
        this.preferencesProvider = new PreferenceProvider(individuals);
        initializePrefProvider();
        preferenceLists = getPreferences();
    }

    private List<PreferenceList> getPreferences() {
        List<PreferenceList> fullList = new ArrayList<>();
        for (int i = 0; i < individuals.getTotalIndividuals(); i++) {
            PreferenceList a = getPreferenceOfIndividual(i);
            fullList.add(a);
        }
        return fullList;
    }

    public PreferenceList getPreferenceOfIndividual(int index) {
        PreferenceList a;
        if (!providerFunctionStatus && !consumerFunctionStatus) {
            a = preferencesProvider.getPreferenceListByDefault(index);
        } else {
            a = preferencesProvider.getPreferenceListByFunction(index);
        }
        return a;
    }

    private void initializePrefProvider() {
        if (this.evaluateFunctionForProviders != null) {
            this.preferencesProvider.setProviderEvaluateFunction(evaluateFunctionForProviders);
        }
        if (this.evaluateFunctionForConsumers != null) {
            this.preferencesProvider.setConsumerEvaluateFunction(evaluateFunctionForConsumers);
        }
    }

    public void setEvaluateFunctionForProviders(String evaluateFunction) {
        if (isValidEvaluateFunction(evaluateFunction)) {
            this.providerFunctionStatus = true;
            this.evaluateFunctionForProviders = evaluateFunction;
        }
    }

    public void setEvaluateFunctionForConsumers(String evaluateFunction) {
        if (isValidEvaluateFunction(evaluateFunction)) {
            this.consumerFunctionStatus = true;
            this.evaluateFunctionForConsumers = evaluateFunction;
        }
    }

    public void setFitnessFunction(String fitnessFunction) {
        if (fitnessFunction.contains("S") || fitnessFunction.contains("SIGMA{") ||
                fitnessFunction.contains("M")) {
            this.fitnessFunctionStatus = true;
            this.fitnessFunction = fitnessFunction;
        }
    }

    private boolean isValidEvaluateFunction(String function) {
        return Stream.of("P", "W", "R").anyMatch(function::contains);
    }

    @Override
    public Solution newSolution() {
        Solution solution = new Solution(1, 1);
        Permutation permutationVar = new Permutation(individuals.getTotalIndividuals());
        solution.setVariable(0, permutationVar);
        return solution;
    }

    @Override
    public void evaluate(Solution solution) {
        log.info("Evaluating...");
        Match result = oneToManyMatching(solution.getVariable(0));
        double[] satisfactions = getAllSatisfactions(result);
        double fitnessScore;
        if (!this.fitnessFunctionStatus) {
            fitnessScore = defaultFitnessEvaluation(satisfactions);
        } else {
            fitnessScore = withFitnessFunctionEvaluation(satisfactions, this.fitnessFunction);
        }
        solution.setAttribute("matches", result);
        solution.setObjective(0, -fitnessScore);
        log.info("Score: {}", fitnessScore);
    }

    private Match oneToManyMatching(Variable var) {
        Match match = new Match(individuals.getTotalIndividuals());
        Set<Integer> matchedConsumers = new HashSet<>();
        Permutation castVar = (Permutation) var;
        int[] decodeVar = castVar.toArray();
        Queue<Integer> unmatchedConsumers = new LinkedList<>();
        for (int val : decodeVar) {
            if (individuals.getRoleOfParticipant(val) == 1) { // Consumer
                unmatchedConsumers.add(val);
            }
        }

        while (!unmatchedConsumers.isEmpty()) {
            int consumer = unmatchedConsumers.poll();
            if (matchedConsumers.contains(consumer)) {
                continue;
            }

            PreferenceList consumerPreference = preferenceLists.get(consumer);
            for (int i = 0; i < consumerPreference.size(); i++) {
                int provider = consumerPreference.getIndexByPosition(i);
                if (!match.isProviderFull(provider, individuals.getProviderCapacity(provider))) {
                    match.addMatch(provider, consumer);
                    matchedConsumers.add(consumer);
                    break;
                } else {
                    int leastPreferredConsumer = getLeastPreferredConsumer(provider, consumer, match.getConsumerMatchesForProvider(provider));
                    if (leastPreferredConsumer != consumer) {
                        match.removeMatch(provider, leastPreferredConsumer);
                        match.addMatch(provider, consumer);
                        unmatchedConsumers.add(leastPreferredConsumer);
                        matchedConsumers.remove(leastPreferredConsumer);
                        matchedConsumers.add(consumer);
                        break;
                    }
                }
            }
            if (!matchedConsumers.contains(consumer)) {
                match.addLeftOverConsumer(consumer);
            }
        }
        return match;
    }

    private int getLeastPreferredConsumer(int provider, int newConsumer, Integer[] currentConsumers) {
        PreferenceList providerPreference = preferenceLists.get(provider);
        return providerPreference.getLeastPreferredIndividual(newConsumer, currentConsumers);
    }

    private double defaultFitnessEvaluation(double[] satisfactions) {
        return Arrays.stream(satisfactions).sum();
    }

    private double withFitnessFunctionEvaluation(double[] satisfactions, String fitnessFunction) {
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
                        int expressionLength = getSigmaFunctionExpressionLength(fitnessFunction,
                                expressionStartIndex);
                        String expression = fitnessFunction.substring(expressionStartIndex,
                                expressionStartIndex + expressionLength);
                        double val = sigmaCalculate(satisfactions, expression);
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
                if (positionOfM < 0 || positionOfM > individuals.getTotalIndividuals()) {
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

    private static int getSigmaFunctionExpressionLength(String function, int startIndex) {
        int num = 0;
        for (int i = startIndex; i < function.charAt(i); i++) {
            char ch = function.charAt(i);
            if (ch == '}') {
                return num;
            } else {
                num++;
            }
        }
        return num;
    }

    private double sigmaCalculate(double[] satisfactions, String expression) {
        System.out.println("sigma calculating...");
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

    private double[] getSatisfactoryOfASetByDefault(double[] Satisfactions, int set) {
        int numberOfIndividual = individuals.getTotalIndividuals();
        int numberOfIndividualOfSet0 = individuals.getTotalIndividuals();
        double[] setSatisfactions;
        if (set == 0) {
            setSatisfactions = new double[numberOfIndividualOfSet0];
            System.arraycopy(Satisfactions, 0, setSatisfactions, 0, numberOfIndividualOfSet0);
        } else {
            setSatisfactions = new double[numberOfIndividual - numberOfIndividualOfSet0];
            if (numberOfIndividual - numberOfIndividualOfSet0 >= 0) {
                int idx = 0;
                for (int i = numberOfIndividualOfSet0; i < numberOfIndividual; i++) {
                    setSatisfactions[idx] = Satisfactions[i];
                    idx++;
                }
            }
        }
        return setSatisfactions;
    }

    private double[] getAllSatisfactions(Match match) {
        double[] satisfactions = new double[individuals.getTotalIndividuals()];

        for (int i = 0; i < individuals.getTotalIndividuals(); i++) {
            double score = 0.0;
            PreferenceList prefList = preferenceLists.get(i);
            if (individuals.isProvider(i)) {
                // The individual is a provider
                Set<Integer> providerMatches = match.getConsumersOfProvider(i);
                for (int consumer : providerMatches)
                    score += prefList.getScoreByIndex(consumer);
            } else {
                Integer matchedProvider = match.getProviderOfConsumer(i);
                if (matchedProvider != null)
                    score = prefList.getScoreByIndex(matchedProvider);
            }
            satisfactions[i] = score;
        }
        return satisfactions;
    }


    @Override
    public String getName() {
        return "One-to-Many Matching Problem";
    }

    @Override
    public int getNumberOfVariables() {
        return 1;
    }

    @Override
    public int getNumberOfObjectives() {
        return 1;
    }

    @Override
    public int getNumberOfConstraints() {
        return 0;
    }

    @Override
    public void close() {
        // No resources to close
    }

}
