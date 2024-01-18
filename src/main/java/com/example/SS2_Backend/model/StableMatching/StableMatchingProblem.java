package com.example.SS2_Backend.model.StableMatching;

import lombok.Getter;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.variable.Permutation;

import java.util.*;

import static com.example.SS2_Backend.util.StringExpressionEvaluator.*;
import static com.example.SS2_Backend.util.Utils.fillWithChar;
import static com.example.SS2_Backend.util.Utils.formatDouble;

/**
 * Base case of Stable Matching Problem (One to One) : Number of Individuals in two sets are Equal (n1 = n2)
 * : Every Individual inside the Population have equal number of Properties
 * : Every Individual inside the Population have the same way to evaluate Partner
 * Wish to test this Class? Run "src.main.java.com.example.SS2_Backend.util.SampleDataGenerator.java"
 * <p>
 * Problems viewing code? press: Ctrl + Alt + L (Windows)
 **/

public class StableMatchingProblem implements Problem {

	private ArrayList<Individual> Individuals; // Storing Data of the Whole population
	@Getter
	private int numberOfIndividual;
	@Getter
	private int numberOfIndividualOfSet0;
	@Getter
	private int numberOfProperties;
	@Getter
	private int[] capacities;
	private String[] PropertiesName;
	@Getter
	private String evaluateFunctionForSet1;
	@Getter
	private String evaluateFunctionForSet2;
	@Getter
	private List<PreferenceList> preferenceLists; // Preference List of each Individual
	@Getter
	private String fitnessFunction; // Evaluate total Score of each Solution set
	private boolean f1Status = false;
	private boolean f2Status = false;
	private boolean fnfStatus = false;

	//No Args/Default Constructor
	public StableMatchingProblem() {
	}

	public void setEvaluateFunctionForSet1(String evaluateFunctionForSet1) {
		if (evaluateFunctionForSet1.contains("P") || evaluateFunctionForSet1.contains("M")) {
			this.f1Status = true;
			this.evaluateFunctionForSet1 = evaluateFunctionForSet1;
		}
	}

	public void setEvaluateFunctionForSet2(String evaluateFunctionForSet2) {
		if (evaluateFunctionForSet2.contains("P") || evaluateFunctionForSet2.contains("M")) {
			this.f2Status = true;
			this.evaluateFunctionForSet2 = evaluateFunctionForSet2;
		}
	}

	private int getCapacityOfIndividual(int target) {
		return Individuals.get(target).getCapacity();
	}

	private String getPropertyNameOfIndex(int index) {
		return PropertiesName[index];
	}

	public Double getPropertyValueOf(int index, int jndex) {
		return Individuals.get(index).getPropertyValue(jndex);
	}

	public int getPropertyWeightOf(int index, int jndex) {
		return Individuals.get(index).getPropertyWeight(jndex);
	}

	public void setFitnessFunction(String fitnessFunction) {
		if (fitnessFunction.contains("S")) {
			this.fnfStatus = true;
			this.fitnessFunction = fitnessFunction;
		}
	}

	public void setPopulation(ArrayList<Individual> individuals) {
		this.Individuals = individuals;
		this.numberOfIndividual = Individuals.size();
		this.capacities = this.getCapacities();
		this.numberOfIndividualOfSet0 = getNumberOfSet0();
		this.numberOfProperties = Individuals.get(0).getNumberOfProperties();
		this.preferenceLists = getPreferences();
	}

	private int[] getCapacities(){
		int[] result = new int[this.numberOfIndividual];
		for (int i = 0; i < this.numberOfIndividual; i++) {
			result[i] = this.Individuals.get(i).getCapacity();
		}
		return result;
	}

	public int getNumberOfSet0() {
		int c = 0;
		for (int i = 0; i < this.numberOfIndividual; i++) {
			if (Individuals.get(i).getIndividualSet() == 0) {
				c++;
			} else {
				break;
			}
		}
		return c;
	}

	public void setAllPropertyNames(String[] allPropertyNames) {
		this.PropertiesName = allPropertyNames;
	}

	/**
	 * MOEA Problem Implementations
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
		long startTime = System.currentTimeMillis();
		Matches result = StableMatchingExtra(solution.getVariable(0));
		long endTime = System.currentTimeMillis();
		double runtime = (double) (endTime - startTime);
		System.out.println("End matching ritual: " + runtime + "ms");
		double fitnessScore;

		System.out.println("Start Evaluating ");
		long startTime1 = System.currentTimeMillis();
		if (!this.fnfStatus) {
			fitnessScore = defaultFitnessEvaluation(result);
		} else {
			String fnf = this.fitnessFunction.trim();
			fitnessScore = withFitnessFunctionEvaluation(result, fnf);
		}
		long endTime1 = System.currentTimeMillis();
		double runtime1 = (double) (endTime1 - startTime1);
		System.out.println("End fitness evaluation: " + runtime1 + "ms");
		solution.setAttribute("matches", result);
		solution.setObjective(0, -fitnessScore);

		System.out.println("Score: " + -fitnessScore);
		System.out.println("End of evaluate");
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
	private List<Double> getAllSatisfactoryOfASet(Matches result, int set) {
		List<Double> a = new ArrayList<>();
		int length = result.size();
		for (int i = 0; i < length; i++) {
			int tmpSet = Individuals.get(i).getIndividualSet();
			if (tmpSet == set) {
				double val = getSetSatisfactory(result.getSet(i));
				a.add(val);
			}
		}
		return a;
	}

	/*
	 * Evaluate Methods
	 */
	public PreferenceList getPreferenceOfIndividual(int index) {
		PreferenceList a;
		if (!f1Status && !f2Status) {
			a = getPreferenceListByDefault(Individuals, index);
			return a;
		} else {
			int set = Individuals.get(index).getIndividualSet();
			if (set == 0) {
				if (f1Status) {
					a = getPreferenceListByFunction(Individuals, index, this.evaluateFunctionForSet1.toUpperCase());
				} else {
					a = getPreferenceListByDefault(Individuals, index);
				}
			} else {
				if (f2Status) {
					a = getPreferenceListByFunction(Individuals, index, this.evaluateFunctionForSet2.toUpperCase());
				} else {
					a = getPreferenceListByDefault(Individuals, index);
				}
			}
		}
		// Sort: Individuals with higher score than others sit on the top of the List
		a.sort();
		// return Sorted list
		return a;
	}

	// Add to a complete List
	public List<PreferenceList> getPreferences() {
		List<PreferenceList> fullList = new ArrayList<>();
		for (int i = 0; i < numberOfIndividual; i++) {
			//System.out.println("Adding preference for Individual " + i );
			PreferenceList a = getPreferenceOfIndividual(i);
			//System.out.println(a.toString());
			fullList.add(a);
		}
		return fullList;
	}

	private Matches StableMatchingExtra(Variable var) {
		//Parse Variable
		System.out.println("parsing");
		Matches matches = new Matches(this.numberOfIndividual, this.numberOfIndividualOfSet0);
		Set<Integer> MatchedNode = new HashSet<>();

		Permutation castVar = (Permutation) var;
		int[] decodeVar = castVar.toArray();

		System.out.println(Arrays.toString(decodeVar));

		Queue<Integer> UnMatchedNode = new LinkedList<>();
		for (int val : decodeVar) {
			UnMatchedNode.add(val);
		}
		String s = var.toString();

		String[] decodedSolution = s.split(",");
		System.out.println(Arrays.toString(decodedSolution));
//		for (String token : decodedSolution) {
//			try {
//				// Convert each token to an Integer and add it to the queue
//				int i = Integer.parseInt(token);
//				UnMatchedNode.add(i);
//			} catch (NumberFormatException e) {
//				// Handle invalid tokens (non-integer values)
//				System.err.println("Skipping invalid token: " + token);
//				return null;
//			}
//		}
		while (!UnMatchedNode.isEmpty()) {
			//printPreferenceLists();
			//System.out.println(matches);
			//System.out.println(UnMatchedNode);
			int Node;
			Node = UnMatchedNode.poll();

			if (MatchedNode.contains(Node)) {
				continue;
			}
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
				if (!matches.isFull(preferNode, this.capacities[preferNode])) {
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
						matches.unMatch(preferNode, Loser);
						matches.unMatch(Loser, preferNode);
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

	public int Compete(int SelectorNode, int newNode, int[] occupiedNodes) {
		PreferenceList prefOfSelectorNode = preferenceLists.get(SelectorNode);
		if (Individuals.get(SelectorNode).getCapacity() == 1) {
			int currentNode = occupiedNodes[0];
			if (isPreferredOver(newNode, currentNode, SelectorNode)) {
				return occupiedNodes[0];
			} else {
				return newNode;
			}
		} else {
			return prefOfSelectorNode.getLeastNode(newNode, occupiedNodes);
		}
	}

	private double defaultFitnessEvaluation(Matches matches) {
		double fitnessScore = 0.0;
		for (int i = 0; i < matches.size(); i++) {
			fitnessScore += getSetSatisfactory(matches.getSet(i));
		}
		return fitnessScore;
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
	 * 6. logarithm     : log(expression)(expression) Logarithm calculation requires 2 parameters in two separate braces
	 * 								   Default log calculation could be expressed like this: log(e)(expression)
	 * 								   Make sure expression is not negative or the final outcome might be resulted: NaN / Infinity / - Infinity
	 * 7. square root : sqrt(expression)
	 */
	private double withFitnessFunctionEvaluation(Matches matches, String fitnessFunction) {
		StringBuilder tmpSB = new StringBuilder();
		for (int c = 0; c < fitnessFunction.length(); c++) {
			char ch = fitnessFunction.charAt(c);
			if (ch == 'S') {
				if (Objects.equals(fitnessFunction.substring(c, c + 5), "SIGMA")) {
					if (fitnessFunction.charAt(c + 5) != '{') {
						System.out.println("Missing '{'");
						System.out.println(fitnessFunction);
						throw new RuntimeException("Missing '{' after Sigma function");
					} else {
						int expressionStartIndex = c + 6;
						int expressionLength = getFunctionExpressionLength(fitnessFunction, expressionStartIndex);
						String expression = fitnessFunction.substring(expressionStartIndex, expressionStartIndex + expressionLength);
						double val = this.sigmaCalculate(matches, expression);
						tmpSB.append(val);
						c += expressionLength + 3;
					}
				}
				// Check for F(index) pattern
				if (c + 3 < fitnessFunction.length() && fitnessFunction.charAt(c + 1) == '(' && fitnessFunction.charAt(c + 3) == ')') {
					if (isNumericValue(fitnessFunction.charAt(c + 2))) {
						int set = Character.getNumericValue(fitnessFunction.charAt(c + 2));
						//Calculate SUM
						tmpSB.append(calculateSatisfactoryOfASetByDefault(matches, set));
					}
				}
				c += 3;
			} else if (ch == 'M') {
				int ssLength = AfterTokenLength(fitnessFunction, c);
				int indexOfM = Integer.parseInt(fitnessFunction.substring(c + 1, c + 1 + ssLength));
				double valueOfM = getSetSatisfactory(matches.getSet(indexOfM));
				tmpSB.append(valueOfM);
				c += ssLength;
			} else {
				//No occurrence of W/w/P/w
				tmpSB.append(ch);
			}
		}
		return eval(tmpSB.toString());
	}

	private double sigmaCalculate(Matches matches, String expression) {
		System.out.println("sigma calculating...");
		StringBuilder parseString = new StringBuilder();
		List<Double> streamValue = new ArrayList<>();
		String regex = null;
		int length = expression.length();
		for (int i = 0; i < length; i++) {
			char ch = expression.charAt(i);
			if (ch == 'S') {
				char set = expression.charAt(i + 1);
				if (set == '0') {
					streamValue = this.getAllSatisfactoryOfASet(matches, 0);
					regex = "S0";
					break;
				} else if (set == '1') {
					streamValue = this.getAllSatisfactoryOfASet(matches, 1);
					regex = "S1";
					break;
				}
			}
		}
		int streamLength = streamValue.size();
		for (int i = 0; i < streamLength; i++) {
			double value = streamValue.get(i);
			String updatedExpression = expression.replaceAll(regex, String.valueOf(value));
			if (i == streamLength - 1) {
				parseString.append(updatedExpression);
			} else {
				parseString.append(updatedExpression).append('+');
			}
		}
		return eval(parseString.toString());
	}

	private static int getFunctionExpressionLength(String function, int startIndex) {
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

	private double getSetSatisfactory(MatchSet matchSet) {
		if (matchSet.getIndividualMatches().isEmpty()) {
			return 0.0;
		}
		int a = matchSet.getIndividualIndex();
		int cap = Individuals.get(a).getCapacity();
		PreferenceList ofInd = preferenceLists.get(a);
		if (cap == 1) {
			int IndividualMatch = matchSet.getIndividualMatches().get(0);
			return ofInd.getIndexValueByKey(IndividualMatch).getValue();
		} else {
			double setScore = 0.0;
			List<Integer> list = matchSet.getIndividualMatches();
			for (int x : list) {
				setScore += ofInd.getIndexValueByKey(x).getValue();
			}
			return setScore;
		}
	}

	private double calculateSatisfactoryOfASetByDefault(Matches matches, int set) {
		double totalScore = 0.0;
		if (set == 0) {
			for (int i = 0; i < this.numberOfIndividualOfSet0; i++) {
				int a = matches.getSet(i).getIndividualIndex();
				List<Integer> list = matches.getSet(i).getIndividualMatches();
				PreferenceList ofInd = preferenceLists.get(a);
				double setScore = 0.0;
				for (int x : list) {
					setScore += ofInd.getIndexValueByKey(x).getValue();
				}
				totalScore += setScore;
			}
		} else {
			for (int i = this.numberOfIndividualOfSet0; i < numberOfIndividual; i++) {
				int a = matches.getSet(i).getIndividualIndex();
				PreferenceList ofInd = preferenceLists.get(a);
				int index = matches.getSet(i).getIndividualMatches().get(0);
				totalScore += ofInd.getIndexValueByKey(index).getValue();
			}
		}
		return totalScore;
	}

	public void printIndividuals() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.numberOfProperties; i++) {
			sb.append(String.format("%-16s| ", this.getPropertyNameOfIndex(i)));
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
			sb.append(String.format("%-4d| ", Individuals.get(i).getIndividualSet()));
			sb.append(String.format("%-20s| ", Individuals.get(i).getIndividualName()));
			// prop value
			StringBuilder ss = new StringBuilder();
			for (int j = 0; j < this.numberOfProperties; j++) {
				ss.append(String.format("%-16s| ", formatDouble(this.getPropertyValueOf(i, j))));
			}
			sb.append(ss).append("\n");
			ss.delete(0, sb.length());
			ss.append(String.format("%33s", "Requirement: | "));
			for (int j = 0; j < this.numberOfProperties; j++) {
				ss.append(String.format("%-16s| ", this.Individuals.get(i).getRequirement(j).toString()));
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
		for (Individual individual : Individuals) {
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
