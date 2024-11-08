package com.example.SS2_Backend.model.stableMatching;

import com.example.SS2_Backend.model.stableMatching.Matches.Matches;
import lombok.Data;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.Permutation;

import java.util.*;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.DoubleStream;

import lombok.extern.slf4j.Slf4j;

import static com.example.SS2_Backend.util.StringExpressionEvaluator.*;

/**
 * <p>
 * Advanced Stable Matching Problem Class (Many to Many)
 * </p>
 * <p>
 * Problem properties:
 * </p>
 * <ul>
 *   <li> Number of Individuals in two sets (n1, n2) could be different follow the under conditions</li>
 *   <li>        n1 > 1
 *           && n2 > 1
 *           && (n1+n2) >= 3
 *   </li>
 *   <li> Every Individual inside the Population has the exactly same number and order of Properties
 *   	<i>For an object that doesn't have or absent a property, that property field should be expressed as null or zero<i/>
 *   </li>
 * </ul>
 * <p>
 *     Core components of this class:
 * </p>
 *  <ul>
 *      <li> Population Data </li>
 *      <li> Preference List of All Match Objects (Nodes) </li>
 *      <li> Stable Matching Algorithm </li>
 *  </ul>
 * <pre>
 *     Wish to test this Class? Run <i>com.example.SS2_Backend.util.SampleDataGenerator</i>
 * </pre>
 **/
@Slf4j
@Data
public class StableMatchingProblem implements Problem {

    private IndividualList individuals;

    private String evaluateFunctionForSet1;

    private String evaluateFunctionForSet2;
    int [][] excludedPairs;
    /**
     * Preference List of each individual/object inside this whole population
     */

    private List<PreferenceList> preferenceLists; // Preference List of each Individual

    private String fitnessFunction; // Evaluate total Score of each Solution set
    private PreferencesProvider preferencesProvider;
    private boolean f1Status = false;
    private boolean f2Status = false;
    private boolean fnfStatus = false;

    private String problemName;

    private static final List<String> VALID_EVALUATE_FUNCTION_KEYWORDS = Arrays.asList("P", "W", "R");

    /**
     * first setter for the class
     * @param individuals array of individual Objects
     */
    public void setPopulation(ArrayList<Individual> individuals, String[] propertiesNames, int[][] excludedPairs) {
        this.individuals = new IndividualList(individuals, propertiesNames);
        this.excludedPairs = excludedPairs;
        initializeFields();
    }

    private void initializeFields() {
        this.preferencesProvider = new PreferencesProvider(individuals);
        initializePrefProvider();
        preferenceLists = getPreferences();
    }

    private void initializePrefProvider() {
        if (this.evaluateFunctionForSet1 != null) {
            this.preferencesProvider.setEvaluateFunctionForSet1(evaluateFunctionForSet1);
        }
        if (this.evaluateFunctionForSet2 != null) {
            this.preferencesProvider.setEvaluateFunctionForSet2(evaluateFunctionForSet2);
        }
    }

    //No Args/Default Constructor
    public StableMatchingProblem() {
    }

    private boolean isValidEvaluateFunction(String function) {
        return StableMatchingProblem.VALID_EVALUATE_FUNCTION_KEYWORDS
                .stream()
                .anyMatch(function::contains);
    }

    /**
     * Sets the evaluation function for Set 1 and checks its validity.
     * ------------------------------------------------------
     * This method sets the evaluation function for Set 1 and updates the status accordingly.
     * If the input function contains "P" or "M", indicating it as a valid function,
     * the status for function 1 is set to true and the input function is assigned.
     * @param evaluateFunctionForSet1 The input function for Set 1 evaluation.
     *                                It will be validated to contain "P" or "M".
     */
    public void setEvaluateFunctionForSet1(String evaluateFunctionForSet1) {
        if (isValidEvaluateFunction(evaluateFunctionForSet1)) {
            this.f1Status = true;
            this.evaluateFunctionForSet1 = evaluateFunctionForSet1;
        }
    }

    /**
     * Vice versa
     * @param evaluateFunctionForSet2 Input function for Set 2 evaluate Set 1
     */
    public void setEvaluateFunctionForSet2(String evaluateFunctionForSet2) {
        if (isValidEvaluateFunction(evaluateFunctionForSet2)) {
            this.f2Status = true;
            this.evaluateFunctionForSet2 = evaluateFunctionForSet2;
        }
    }

    /**
     * Sets the fitness function and checks its validity.
     * -------------------------------------------
     * This method sets the fitness function and updates the status accordingly.
     * If the input function contains "S", indicating it as a valid fitness function,
     * the status for the fitness function is set to true and the input function is assigned.
     * @param fitnessFunction The fitness function to be set.
     *                        It will be validated to contain "S".
     */
    public void setFitnessFunction(String fitnessFunction) {
        if (fitnessFunction.contains("S") || fitnessFunction.contains("SIGMA{") ||
                fitnessFunction.contains("M")) {
            this.fnfStatus = true;
            this.fitnessFunction = fitnessFunction;
        }
    }

    /**
     * MOEA Framework Problem Class implementations
     */

    //Solution Definition
    @Override
    public Solution newSolution() {
        Solution solution = new Solution(1, 1);
        // Randomize the order (from 0 to this.NumberOfIndividual)
        Permutation permutationVar = new Permutation(individuals.getNumberOfIndividual());
        solution.setVariable(0, permutationVar);
        return solution;
    }

    // Evaluate
    public void evaluate(Solution solution) {
        log.info("Evaluating ... "); // Start matching & collect result
        Matches result = StableMatchingExtra(EncodingUtils.getPermutation(solution.getVariable(0)));
        // check excluded pairs here
        // first check the first pair of the 2d array, the first element is the index of the individual, the second element is the index of the individual that the first individual is matched with
        // if result.getSet(firstIndividualIndex).contains(secondIndividualIndex) == true [0,1], result.getSet(0) = [1,2,3] .contains(1) == true, return, stop the evaluation
        // then the pair is excluded from the result
        // the below case work best for 1,1 matching
        if (Objects.nonNull(excludedPairs)) {
            for (int[] excludedPair : excludedPairs) {
                if (result.getSet(excludedPair[0]).contains(excludedPair[1])) {
                    solution.setObjective(0, -Double.MAX_VALUE);
                    return;
                }
            }
        }
        double[] Satisfactions = getAllSatisfactions(result); // Get all satisfactions
        double fitnessScore;
        if (!this.fnfStatus) {
            fitnessScore = defaultFitnessEvaluation(Satisfactions);
        } else {
            String fnf = this.fitnessFunction.trim();
            fitnessScore = withFitnessFunctionEvaluation(Satisfactions, fnf);
        }
        solution.setAttribute("matches", result);
        solution.setObjective(0, -fitnessScore);
        log.info("Score: {}", convertToStringWithoutScientificNotation(fitnessScore));
    }

    @Override
    public String getName() {
        return "Two Sided Stable Matching Problem";
    }

    public int getNumberOfVariables() {
        return 1;
    }

    @Override
    public int getNumberOfObjectives() {
        return 1;
    }

    @Override
    public int getNumberOfConstraints() {
        return 1;
    }

    public void close() {
    }

    /**
     * Extra Methods for  Stable Matching Problem
     */
    public PreferenceList getPreferenceOfIndividual(int index) {
        PreferenceList a;
        if (!f1Status && !f2Status) {
            a = preferencesProvider.getPreferenceListByDefault(index);
        } else {
            a = preferencesProvider.getPreferenceListByFunction(index);
        }
        return a;
    }

    // Add to a complete List
    private List<PreferenceList> getPreferences() {
        List<PreferenceList> fullList = new ArrayList<>();
        for (int i = 0; i < individuals.getNumberOfIndividual(); i++) {
            PreferenceList a = getPreferenceOfIndividual(i);
            fullList.add(a);
        }
        return fullList;
    }


    private Matches StableMatchingExtra(int[] nodes) {
        //Parse Variable
        //System.out.println("parsing");
        Matches matches = new Matches(nodes.length);
        Set<Integer> MatchedNode = new TreeSet<>();
        Queue<Integer> UnMatchedNode = new LinkedList<>( );
        Arrays.stream(nodes)
            .forEach(UnMatchedNode::add);

        while (!UnMatchedNode.isEmpty()) {
            int leftNode = UnMatchedNode.poll();

            if (MatchedNode.contains(leftNode)) {
                continue;
            }

            //Loop through LeftNode's preference list to find a Match
            for (int rightNode : preferenceLists.get(leftNode).keySet()) {
                if (matches.isAlreadyMatch(rightNode, leftNode)) {
                    break;
                }
                //If the RightNode Capacity is not full -> create connection between LeftNode - RightNode
                if (!matches.isFull(rightNode, this.individuals.getCapacityOf(rightNode))) {
                    //AddMatch (Node, NodeToConnect)
                    matches.addMatch(rightNode, leftNode);
                    matches.addMatch(leftNode, rightNode);
                    MatchedNode.add(rightNode);
                    break;
                } else {
                    //If the RightNode's Capacity is Full then Left Node will Compete with Nodes that are inside RightNode
                    //Loser will be the return value

                    int rightNodeLoser = getLoser(leftNode, rightNode, matches);

                    //If RightNode is the LastChoice of Loser -> then
                    // Loser will be terminated and Saved in Matches.LeftOvers Container
                    if (rightNodeLoser == leftNode) {
                        int leftNodeLoser = getLoser(rightNode, leftNode, matches);

                        if ( leftNodeLoser == rightNode) {
                            //System.out.println(Node + " has nowhere to go. Go to LeftOvers!");
                            matches.addLeftOver(rightNodeLoser);
                            break;
                        }
                        //Or else Loser go back to UnMatched Queue & Waiting for it's Matching Procedure
                    } else {
                        matches.disMatch(rightNode, rightNodeLoser);
                        matches.disMatch(rightNodeLoser, rightNode);
                        UnMatchedNode.add(rightNodeLoser);
                        MatchedNode.remove(rightNodeLoser);
                        //System.out.println(Loser + " lost the game, waiting for another chance.");
                        matches.addMatch(rightNode, leftNode);
                        matches.addMatch(leftNode, rightNode);
                        MatchedNode.add(leftNode);
                        //System.out.println(Node + " is more suitable than " + Loser + " matched with " + preferNode);
                        break;
                    }
                }
            }
        }
        return matches;
    }

    /**
     *
     * @param matcher New node that compete with matchee's nodes
     * @param matchee Node that matcher want to match to
     * @param matches Current matching state
     * @return Index of the least preferred node of matchee
     */
    private int getLoser(int matcher, int matchee, Matches matches) {
        Set<Integer> nodes = matches.getIndividualMatches(matchee);
        nodes.add(matcher);
        return preferenceLists
            .get(matchee)
            .getLeastNode(nodes)
            .get();
    }

    private double defaultFitnessEvaluation(double[] Satisfactions) {
        return Arrays.stream(Satisfactions)
            .sum();
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
                        tmpSB.append(
                            convertToStringWithoutScientificNotation(
                                DoubleStream.of(getSatisfactoryOfASetByDefault(satisfactions, set))
                                    .sum()));
                    }
                }
                c += 3;
            } else if (ch == 'M') {
                int ssLength = AfterTokenLength(fitnessFunction, c);
                int positionOfM = Integer.parseInt(fitnessFunction.substring(c + 1,
                        c + 1 + ssLength));
                if (positionOfM < 0 || positionOfM > individuals.getNumberOfIndividual()) {
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

    /**
     * @param satisfactions - Double array contains satisfactions of the whole population sequentially (0, 1, 2, ... , n)
     * @param expression    - Mathematical String that Express how each of the value calculated. Example: S0/2, S1^3
     *                      <i>
     *                      Cases:
     *                                       <ol>
     *                                       	<li>
     *                                          		<i>S1</i>represents satisfactions of set 1 (array)
     *                                       	</li>
     *                                       	<li>
     *                                      		<i>S2</i>represents satisfactions of set 2 (array)
     *                                      	 </li>
     *                                       </ol>
     *                      </i>
     * @return double value - Sum of satisfactions of the whole set sequentially
     */
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
        return DoubleStream.of(streamValue)
            .map(calculator)
            .sum();
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

    public double[] getAllSatisfactions(Matches matches) {
        double[] satisfactions = new double[individuals.getNumberOfIndividual()];
        for (int i = 0; i < satisfactions.length; i++) {
            PreferenceList preferenceList = preferenceLists.get(i);
            Set<Integer> SetMatches = matches.getSet(i);

            satisfactions[i] = SetMatches.stream()
                .mapToDouble(preferenceList::get)
                .sum();
        }
        return satisfactions;
    }

    private double[] getSatisfactoryOfASetByDefault(double[] Satisfactions, int set) {
        int numberOfIndividual = individuals.getNumberOfIndividual();
        int numberOfIndividualOfSet0 = individuals.getNumberOfIndividualForSet0();
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

    public void printIndividuals() {
        this.individuals.print();
    }

    @Override
    public String toString() {
        return "Problem: " + "\n" + "Num of Individuals: " +
                this.individuals.getNumberOfIndividual() + "\nNumber Of Properties: " +
                individuals.getNumberOfProperties() + "\nFitness Function: " + fitnessFunction +
                "\nEvaluate Function For Set 1: " + this.evaluateFunctionForSet1 +
                "\nEvaluate Function For Set 2: " + this.evaluateFunctionForSet2 + "\n" +
                individuals;
    }

    public String getPreferenceListsString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < preferenceLists.size(); i++) {
            sb
                    .append("ID ")
                    .append(i)
                    .append(" : ");
            sb.append(preferenceLists
                    .get(i)
                    .toString());
            sb.append("\n");
        }
        return sb.toString();
    }

}
