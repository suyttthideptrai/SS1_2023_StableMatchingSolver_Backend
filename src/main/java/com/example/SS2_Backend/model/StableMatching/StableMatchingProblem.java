package com.example.SS2_Backend.model.StableMatching;

import com.example.SS2_Backend.model.StableMatching.Matches.Matches;
import lombok.Getter;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.variable.Permutation;

import java.util.*;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.DoubleStream;

import static com.example.SS2_Backend.util.StringExpressionEvaluator.*;
import static com.example.SS2_Backend.util.Utils.fillWithChar;
import static com.example.SS2_Backend.util.Utils.formatDouble;

/**
 * <p>
 *     Advanced Stable Matching Problem (Many to Many)
 * </p>
 * <p>
 *     Problem properties:
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

public class StableMatchingProblem implements Problem {

	/*
	Storing Data of the Whole population as an array[object1, object2, ...]
	 */
	private List<Individual> individuals;
	@Getter
	/*
	Number of objects that must be matched
	 */
	private int numberOfIndividual;
	/**
	Number of objects in the first set
	We accumulate number of the other set by
	@var numberOfIndividual -
	 @var numberOfIndividualOfSet0
	 */
	@Getter
	private int numberOfIndividualOfSet0;
	/**
	 Properties name eg: person[age, salary, height, ...]
	 @var NumberOfProperties is the size of
	 @var PropertiesName array
	 */
	@Getter
	private int numberOfProperties;
	private String[] PropertiesName;
	@Getter
	private String evaluateFunctionForSet1;
	@Getter
	private String evaluateFunctionForSet2;
	/**
	 * Preference List of each individual/object inside this whole population
	 */
	@Getter
	private List<PreferenceList> preferenceLists; // Preference List of each Individual
	@Getter
	private String fitnessFunction; // Evaluate total Score of each Solution set
	private final PreferencesProvider preferencesProvider = new PreferencesProvider();
	private boolean f1Status = false;
	private boolean f2Status = false;
	private boolean fnfStatus = false;

	/**
	 * first setter for the class
	 * @param individuals array of individual Objects
	 */
	public void setPopulation(ArrayList<Individual> individuals) {
		this.individuals = individuals;
		this.preferencesProvider.setIndividuals(individuals);
		initializeFields();
	}

	/**
	 * Initializes fields related to the population data.
	 *------------------------------------------
	 * This method is executed only after the Individuals list has been initialized.
	 * It sets up various fields such as the number of individuals, number of individuals in set 0,
	 * number of properties, and preference lists based on the Individuals list.
	 *
	 * @throws IllegalArgumentException if the number of individuals is less than 3, as matching would make no sense.
	 */
	private void initializeFields() {
		numberOfIndividual = individuals.size();
		if (numberOfIndividual < 3) {
			throw new IllegalArgumentException("Invalid number of individuals, number must be greater or equal to 3 (int) as matching makes no sense");
		}
		initializePrefProvider();
		numberOfIndividualOfSet0 = getNumberOfSet0();
		numberOfProperties = individuals.isEmpty() ? 0 : individuals.get(0).getNumberOfProperties();
		preferenceLists = getPreferences();
	}

	private void initializePrefProvider() {
		if(this.evaluateFunctionForSet1 != null) {
			this.preferencesProvider.setEvaluateFunctionForSet1(evaluateFunctionForSet1);
		}
		if(this.evaluateFunctionForSet2 != null) {
			this.preferencesProvider.setEvaluateFunctionForSet2(evaluateFunctionForSet2);
		}
	}

	//No Args/Default Constructor
	public StableMatchingProblem() {
	}
	//Args constructor
//	public StableMatchingProblem(ArrayList<Individual> individuals){
//		setPopulation(individuals);
//	}

	/**
	 * Sets the evaluation function for Set 1 and checks its validity.
	 *------------------------------------------------------
	 * This method sets the evaluation function for Set 1 and updates the status accordingly.
	 * If the input function contains "P" or "M", indicating it as a valid function,
	 * the status for function 1 is set to true and the input function is assigned.
	 *
	 * @param evaluateFunctionForSet1 The input function for Set 1 evaluation.
	 *                                It will be validated to contain "P" or "M".
	 */
	public void setEvaluateFunctionForSet1(String evaluateFunctionForSet1) {
		if(evaluateFunctionForSet1.contains("p") || evaluateFunctionForSet1.contains("w") || evaluateFunctionForSet1.contains("r")) {
			this.f1Status = true;
			this.evaluateFunctionForSet1 = evaluateFunctionForSet1;
		}
	}
	/**
	 * Vice versa
	 * @param evaluateFunctionForSet2 Input function for Set 2 evaluate Set 1
	 */
	public void setEvaluateFunctionForSet2(String evaluateFunctionForSet2) {
		if(evaluateFunctionForSet2.contains("p") || evaluateFunctionForSet2.contains("w") || evaluateFunctionForSet2.contains("r")) {
			this.f2Status = true;
			this.evaluateFunctionForSet2 = evaluateFunctionForSet2;
		}
	}

	/**
	 * Sets the fitness function and checks its validity.
	 *-------------------------------------------
	 * This method sets the fitness function and updates the status accordingly.
	 * If the input function contains "S", indicating it as a valid fitness function,
	 * the status for the fitness function is set to true and the input function is assigned.
	 *
	 * @param fitnessFunction The fitness function to be set.
	 *                        It will be validated to contain "S".
	 */
	public void setFitnessFunction(String fitnessFunction) {
		if(fitnessFunction.contains("S") || fitnessFunction.contains("SIGMA{") || fitnessFunction.contains("M")) {
			this.fnfStatus = true;
			this.fitnessFunction = fitnessFunction;
		}
	}

	/**
	 * Retrieves the capacity of each object.
	 * ---------------------------------
	 * This method returns an array containing the capacity of each object.
	 * The capacity of a single object can be obtained by passing its index (in the individual list) to this array.
	 * For example: The capacity of the person at index 0 can be accessed as capacities[0].
	 *
	 * @return An array of integers representing the capacities of each object.
	 */
	public int[] getCapacities(){
		int[] capacities = new int[this.numberOfIndividual];
		for (int i = 0; i < this.numberOfIndividual; i++) {
			capacities[i] = individuals.get(i).getCapacity();
		}
		return capacities;
	}
	private String getPropertyNameByIndex(int index) {
		return PropertiesName[index];
	}

	public Double getPropertyValueOf(int indexOfObject, int indexOfProperty) {
		return individuals.get(indexOfObject).getPropertyValue(indexOfProperty);
	}
	public int getPropertyWeightOf(int indexOfObject, int indexOfProperty) {
		return individuals.get(indexOfObject).getPropertyWeight(indexOfProperty);
	}

	public int getNumberOfSet0(){
		int c = 0;
		for(int i = 0; i < this.numberOfIndividual; i++){
			if (individuals.get(i).getIndividualSet() == 0){
				c++;
			}else{
				break;
			}
		}
		return c;
	}

	public void setAllPropertyNames(String[] allPropertyNames) {
		this.PropertiesName = allPropertyNames;
	}

	/**
	 * MOEA Framework Problem Implementations
	 */

	//Solution Definition
	@Override
	public Solution newSolution() {
		Solution solution = new Solution(1, 1);
		// Randomize the order (from 0 to this.NumberOfIndividual)
		Permutation permutationVar = new Permutation(this.numberOfIndividual);
		solution.setVariable(0, permutationVar);
		return solution;
	}

	// Evaluate
	public void evaluate(Solution solution) {
		System.out.println("[Service] Evaluating ... ");
		Matches result = StableMatchingExtra(solution.getVariable(0));
		double[] Satisfactions = getAllSatisfactions(result);

		double fitnessScore;
		if (!this.fnfStatus) {
			fitnessScore = defaultFitnessEvaluation(Satisfactions);
		}else{
			String fnf = this.fitnessFunction.trim();
			fitnessScore = withFitnessFunctionEvaluation(Satisfactions, fnf);
		}

		solution.setAttribute("matches", result);
		solution.setObjective(0, -fitnessScore);

		System.out.println("Score: " + -fitnessScore);
		System.out.println("[Service] End of evaluate");
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

	/*
	 * After Matching Gets
	 */
	/*
	 * Evaluate Methods
	 */
	public PreferenceList getPreferenceOfIndividual(int index) {
		PreferenceList a;
		if(!f1Status && !f2Status){
			a = preferencesProvider.getPreferenceListByDefault(index);
		}else{
			a = preferencesProvider.getPreferenceListByFunction(index);
		}
		// Sort: Individuals with higher score than others sit on the top of the List
		a.sort();
		a.transfer(this.numberOfIndividual);
		// return Sorted list
		return a;
	}
	// Add to a complete List
	private List<PreferenceList> getPreferences() {
		List<PreferenceList> fullList = new ArrayList<>();
		for (int i = 0; i < numberOfIndividual; i++) {
			PreferenceList a = getPreferenceOfIndividual(i);
			fullList.add(a);
		}
		return fullList;
	}

	private Matches StableMatchingExtra(Variable var) {
		//Parse Variable
		//System.out.println("parsing");
		Matches matches = new Matches(this.numberOfIndividual);
		Set<Integer> MatchedNode = new HashSet<>();

		Permutation castVar = (Permutation) var;
		int[] decodeVar = castVar.toArray();

		Queue<Integer> UnMatchedNode = new LinkedList<>();
		for (int val : decodeVar){
			UnMatchedNode.add(val);
		}

		while (!UnMatchedNode.isEmpty()) {
			//printPreferenceLists();
			//System.out.println(matches);
			//System.out.println(UnMatchedNode);
			int Node;
			Node = UnMatchedNode.poll();

			if (MatchedNode.contains(Node)) {
				continue;
			}
			//System.out.println("working on Node:" + Node);
			//Get pref List of LeftNode
			PreferenceList NodePreference = preferenceLists.get(Node);
			//Loop through LeftNode's preference list to find a Match
			for (int i = 0; i < NodePreference.size(); i++) {
				//Next Match (RightNode) is found on the list
				int preferNode = NodePreference.getByIndex(i).getIndividualIndex();
				//System.out.println(Node + " Prefer : " + preferNode);
				if (matches.isAlreadyMatch(preferNode, Node)) {
					//System.out.println(Node + " is already match with " + preferNode);
					break;
				}
				//If the RightNode Capacity is not full -> create connection between LeftNode - RightNode
				if (!matches.isFull(preferNode, this.individuals.get(preferNode).getCapacity())) {
					//System.out.println(preferNode + " is not full.");
					//AddMatch (Node, NodeToConnect)
					matches.addMatch(preferNode, Node);
					matches.addMatch(Node, preferNode);
					MatchedNode.add(preferNode);
					break;
				} else {
					//If the RightNode's Capacity is Full then Left Node will Compete with Nodes that are inside RightNode
					//Loser will be the return value
					//System.out.println(preferNode + " is full! Begin making a Compete game involve: " + Node + " ..." );
					int Loser = Compete(preferNode, Node, matches.getIndividualMatches(preferNode));
					//If RightNode is the LastChoice of Loser -> then
					// Loser will be terminated and Saved in Matches.LeftOvers Container
					//System.out.println("Found Loser: " + Loser);
					if (Loser == Node) {
						if (LastChoice(Node) == preferNode) {
							//System.out.println(Node + " has nowhere to go. Go to LeftOvers!");
							matches.addLeftOver(Loser);
							break;
						}
						//Or else Loser go back to UnMatched Queue & Waiting for it's Matching Procedure
					} else {
						matches.disMatch(preferNode, Loser);
						matches.disMatch(Loser, preferNode);
						UnMatchedNode.add(Loser);
						MatchedNode.remove(Loser);
						//System.out.println(Loser + " lost the game, waiting for another chance.");
						matches.addMatch(preferNode, Node);
						matches.addMatch(Node, preferNode);
						MatchedNode.add(Node);
						//System.out.println(Node + " is more suitable than " + Loser + " matched with " + preferNode);
						break;
					}
				}
			}
		}
		return matches;
	}

	// Stable Matching Algorithm Component: isPreferredOver
	private boolean isPreferredOver(int newNode, int currentNode, int SelectorNode) {
		PreferenceList preferenceOfSelectorNode = preferenceLists.get(SelectorNode);
		double ofNewNode = preferenceOfSelectorNode.getIndexValueByKey(newNode).getValue();
		double ofCurrentNode = preferenceOfSelectorNode.getIndexValueByKey(currentNode).getValue();
		return ofNewNode > ofCurrentNode;
	}

	// return true if TargetNode is the last choice of Loser
	private int LastChoice(int loser) {
		PreferenceList pref = preferenceLists.get(loser);
		return pref.getByIndex(pref.size() - 1).getIndividualIndex();
	}

	private int Compete(int SelectorNode, int newNode, Set<Integer> occupiedNodes) {
		PreferenceList prefOfSelectorNode = preferenceLists.get(SelectorNode);
		if (individuals.get(SelectorNode).getCapacity() == 1) {
			Iterator<Integer> iterator = occupiedNodes.iterator();
			int currentNode = iterator.next();
			if (isPreferredOver(newNode, currentNode, SelectorNode)) {
				return currentNode;
			} else {
				return newNode;
			}
		} else {
			return prefOfSelectorNode.getLeastNode(newNode, occupiedNodes);
		}
	}
	private double defaultFitnessEvaluation(double[] Satisfactions) {
		return Arrays.stream(Satisfactions).sum();
	}
	/*
	 * Fitness Function Grammar:
	 * $: i - index of MatchSet in "matches"
	 * $: set - value (1 or 2) represent set 1 (0) or set 2 (1)
	 * $: S(set) - Sum of all payoff scores of "set" evaluate by opposite set
	 * $: M(i) - Value of specific matchSet's satisfaction eg: M0 (satisfactory of Individual no 0)
	 *
	 * Supported functions:
	 * #: SIGMA{S1} calculate sum of all MatchSet of a belonging set eg: SIGMA{S1}
	 *
	 * Supported mathematical calculations:
	 *     Name             :    Usage
	 * 1. absolute       : abs(expression)
	 * 2. exponent      : (expression)^(expression)
	 * 3. sin                 : sin(expression)
	 * 4. cos                 : cos(expression)
	 * 5. tan                : tan(expression)
	 * 6. logarithm     : log(expression)(expression) Logarithm calculation requires 2 parameters in two separate curly braces
	 * 								   Default log calculation (with Math.E constant) can be achieved like this: log(e)(expression)
	 * 								   Make sure expression is not negative or the final outcome might be
	 * 								   resulted in: NaN / Infinity / - Infinity
	 * 7. square root : sqrt(expression)
	 */
	private double withFitnessFunctionEvaluation(double[] Satisfactions, String fitnessFunction) {
		StringBuilder tmpSB = new StringBuilder();
		for (int c = 0; c < fitnessFunction.length(); c++) {
			char ch = fitnessFunction.charAt(c);
			if (ch == 'S') {
				if(Objects.equals(fitnessFunction.substring(c, c+5), "SIGMA")){
					if (fitnessFunction.charAt(c+5) != '{') {
						System.err.println("Missing '{'");
						System.err.println(fitnessFunction);
						throw new RuntimeException("Missing '{' after Sigma function");
					}else{
						int expressionStartIndex = c + 6;
						int expressionLength = getFunctionExpressionLength(fitnessFunction, expressionStartIndex);
						String expression = fitnessFunction.substring(expressionStartIndex, expressionStartIndex+expressionLength);
						double val = sigmaCalculate(Satisfactions, expression);
						tmpSB.append(val);
						c += expressionLength + 3;
					}
				}
				// Check for F(index) pattern
				if (c+ 3 < fitnessFunction.length() && fitnessFunction.charAt(c+1) ==  '(' && fitnessFunction.charAt(c + 3) == ')') {
					if(isNumericValue(fitnessFunction.charAt(c+2))) {
						int set = Character.getNumericValue(fitnessFunction.charAt(c + 2));
						//Calculate SUM
						tmpSB.append(DoubleStream.of(getSatisfactoryOfASetByDefault(Satisfactions, set)).sum());
					}
				}
				c += 3;
			} else if (ch == 'M') {
				int ssLength = AfterTokenLength(fitnessFunction, c);
				int positionOfM = Integer.parseInt(fitnessFunction.substring(c + 1, c + 1 + ssLength));
				if(positionOfM < 0 || positionOfM > this.numberOfIndividual - 1){
					c+= ssLength;
					continue;
				}
				double valueOfM = Satisfactions[positionOfM];
				tmpSB.append(valueOfM);
				c += ssLength;
			} else {
					//No occurrence of W/w/P/w
					tmpSB.append(ch);
			}
		}
		return eval(tmpSB.toString());
	}

	/**
	 * @param Satisfactions - Double array contains satisfactions of the whole population sequentially (0, 1, 2, ... , n)
	 * @param expression - Mathematical String that Express how each of the value calculated. Example: S0/2, S1^3
	 *  <i>
	 *        Cases:
	 *                   <ol>
	 *                   	<li>
	 *                      		<i>S1</i>represents satisfactions of set 1 (array)
	 *                   	</li>
	 *                   	<li>
	 * 	                		<i>S2</i>represents satisfactions of set 2 (array)
	 *                  	 </li>
	 *                   </ol>
	 *  </i>
	 * @return double value - Sum of satisfactions of the whole set sequentially
	 */
	private double sigmaCalculate(double[] Satisfactions, String expression){
		System.out.println("sigma calculating...");
		double[] streamValue = null;
		String regex = null;
		for (int i = 0; i < expression.length() - 1; i++) {
			char ch = expression.charAt(i);
			if (ch == 'S') {
				char set = expression.charAt(i + 1);
				switch (set) {
					case '1':
						streamValue = getSatisfactoryOfASetByDefault(Satisfactions, 0);
						regex = "S1";
						break;
					case '2':
						streamValue = getSatisfactoryOfASetByDefault(Satisfactions, 1);
						regex = "S2";
						break;
					default: throw new IllegalArgumentException("Illegal value after S regex in sigma calculation: " + expression);
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
	private static int getFunctionExpressionLength(String function, int startIndex){
		int num = 0;
		for(int i = startIndex; i < function.charAt(i); i++){
			char ch = function.charAt(i);
			if(ch == '}'){
				return num;
			}else{
				num++;
			}
		}
		return num;
	}
	public double[] getAllSatisfactions(Matches matches){
		double[] satisfactions = new double[this.numberOfIndividual];
		for (int i = 0; i < this.numberOfIndividual; i++) {
			double setScore = 0.0;
			PreferenceList ofInd = preferenceLists.get(i);
			Set<Integer> SetMatches = matches.getSet(i);
			for (int x : SetMatches) {
				setScore += ofInd.getIndexValueByKey(x).getValue();
			}
			satisfactions[i] = setScore;
		}
		return satisfactions;
	}
	private double[] getSatisfactoryOfASetByDefault(double[] Satisfactions, int set){
		double[] setSatisfactions;
		if(set == 0){
			setSatisfactions = new double[this.numberOfIndividualOfSet0];
			System.arraycopy(Satisfactions, 0, setSatisfactions, 0, this.numberOfIndividualOfSet0);
		}else{
			setSatisfactions = new double[this.numberOfIndividual-this.numberOfIndividualOfSet0];
			if (numberOfIndividual - this.numberOfIndividualOfSet0 >= 0) {
				int idx = 0;
				for (int i = this.numberOfIndividualOfSet0; i < this.numberOfIndividual; i++) {
					setSatisfactions[idx] = Satisfactions[i];
					idx++;
				}
			}
		}
		return setSatisfactions;
	}

	public void printIndividuals() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.numberOfProperties; i++) {
			sb.append(String.format("%-16s| ", this.getPropertyNameByIndex(i)));
		}
		String propName = sb.toString();
		sb.delete(0, sb.length());
		//header
		System.out.println("No | Set | Name                | " + propName);
		int width = this.numberOfProperties * 18 + 32;
		String filledString = fillWithChar('-', width);
		sb.append(filledString).append("\n");
		//content
		for (int i = 0; i < this.numberOfIndividual; i++) {
			//name / set
			sb.append(String.format("%-3d| ", i));
			sb.append(String.format("%-4d| ", individuals.get(i).getIndividualSet()));
			sb.append(String.format("%-20s| ", individuals.get(i).getIndividualName()));
			// prop value
			StringBuilder ss = new StringBuilder();
			for (int j = 0; j < this.numberOfProperties; j++) {
				ss.append(String.format("%-16s| ", formatDouble(this.getPropertyValueOf(i, j))));
			}
			sb.append(ss).append("\n");
			ss.delete(0, sb.length());
			ss.append(String.format("%33s", "Requirement: | "));
			for (int j = 0; j < this.numberOfProperties; j++) {
				ss.append(String.format("%-16s| ", this.individuals.get(i).getRequirement(j).toString()));
			}
			sb.append(ss).append("\n");
			ss.delete(0, sb.length());
			ss.append(String.format("%33s", "Weight: | "));
			for (int j = 0; j < this.numberOfProperties; j++) {
				ss.append(String.format("%-16s| ", this.getPropertyWeightOf(i, j)));
			}
			sb.append(ss).append("\n");
		}
		sb.append(filledString).append("\n");
		System.out.print(sb);
	}

	public String toString() {
		System.out.println("Problem: " + "\n");
		System.out.println("Num of Individuals: " + this.numberOfIndividual);
		StringBuilder sb = new StringBuilder();
		for (Individual individual : individuals) {
			sb.append(individual.toString()).append("\n");
		}
		return "\nNumber Of Properties: " + numberOfProperties +
		    "\nFitness Function: " + fitnessFunction +
		    "\nEvaluate Function For Set 1: " + this.evaluateFunctionForSet1 +
		    "\nEvaluate Function For Set 2: " + this.evaluateFunctionForSet2 +
		    "\n" + sb;
	}

	public String printPreferenceLists() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < preferenceLists.size(); i++) {
			sb.append("Individual ").append(i).append(" : ");
			sb.append(preferenceLists.get(i).toString());
			sb.append("\n");
		}
		return sb.toString();
	}
}
