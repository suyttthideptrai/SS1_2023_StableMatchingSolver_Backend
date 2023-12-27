package com.example.SS2_Backend.model.StableMatching;

import com.example.SS2_Backend.model.StableMatching.Requirement.Requirement;
import lombok.Getter;
import lombok.Setter;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.variable.EncodingUtils;

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
	private String[] PropertiesName;
	@Getter
	@Setter
	private String evaluateFunctionForSet1 = "";
	@Getter
	@Setter
	private String evaluateFunctionForSet2 = "";
	@Getter
	private List<PreferenceList> preferenceLists; // Preference List of each Individual
	@Getter
	private String fitnessFunction = ""; // Evaluate total Score of each Solution set
	/**
	 * MOEA Problem Implementations
	 */
	//No Args Constructor & With Args Constructor
	public StableMatchingProblem() {
	}

	//Solution Definition
	@Override
	public Solution newSolution() {
		Solution solution = new Solution(1, 1);
		// Randomize the order (from 0 to this.NumberOfIndividual)
		solution.setVariable(0, EncodingUtils.newPermutation(this.numberOfIndividual));
		return solution;
	}

	// Evaluate
	public void evaluate(Solution solution) {
		Matches result = StableMatchingExtra(solution.getVariable(0));
		double fitnessScore;

		String fnf = this.fitnessFunction.toUpperCase();
		if (!fnf.contains("S") || this.fitnessFunction.isEmpty()) {
			assert result != null;
			fitnessScore = defaultFitnessEvaluation(result);
		}else{
			fitnessScore = withFitnessFunctionEvaluation(result, fnf);
		}
		solution.setAttribute("matches", result);
		solution.setObjective(0, -fitnessScore);
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
	private List<Double> getAllSatisfactoryOfASet(Matches result, int set){
		List<Double> a = new ArrayList<>();
		int length = result.size();
		for(int i = 0; i < length; i++){
			int tmpSet = Individuals.get(result.getSet(i).getIndividualIndex()).getIndividualSet();
			if(tmpSet == set){
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
		// get this Individual set belong to
		int set = Individuals.get(index).getIndividualSet();
		String evaluateFunction;
		if (set == 0) {
			evaluateFunction = this.evaluateFunctionForSet1;
		} else {
			evaluateFunction = this.evaluateFunctionForSet2;
		}
		evaluateFunction = evaluateFunction.toUpperCase();
		if (!evaluateFunction.contains("P")) {
			a = getPreferenceListByDefault(Individuals, index);
		} else {
			a = getPreferenceListByFunction(Individuals, index, evaluateFunction);
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
		Matches matches = new Matches();
		Queue<Integer> UnMatchedNode = new LinkedList<>();
		List<Integer> MatchedNode = new LinkedList<>();

		String s = var.toString();

		String[] decodedSolution = s.split(",");
		for (int i = 0; i < decodedSolution.length; i++) {
			matches.add(new MatchSet(i, getCapacityOfIndividual(i)));
		}
		for (String token : decodedSolution) {
			try {
				// Convert each token to an Integer and add it to the queue
				int i = Integer.parseInt(token);
				UnMatchedNode.add(i);
			} catch (NumberFormatException e) {
				// Handle invalid tokens (non-integer values)
				System.err.println("Skipping invalid token: " + token);
				return null;
			}
		}
		while (!UnMatchedNode.isEmpty()) {
			printPreferenceLists();
			System.out.println(matches);
			System.out.println(UnMatchedNode);
			int Node;

			assert !UnMatchedNode.isEmpty();

			Node = UnMatchedNode.poll();

			if (MatchedNode.contains(Node)) {
				continue;
			}
			System.out.println("working on Node:" + Node);
			//Get pref List of LeftNode
			PreferenceList NodePreference = preferenceLists.get(Node);
			//Loop through LeftNode's preference list to find a Match
			for (int i = 0; i < NodePreference.size(); i++) {
				//Next Match (RightNode) is found on the list
				int preferNode = NodePreference.getByIndex(i).getIndividualIndex();
				System.out.println(Node + " Prefer : " + preferNode);
				if (matches.alreadyMatch(preferNode, Node)) {
					//System.out.println(Node + " is already match with " + preferNode);
					break;
				}
				//If the RightNode Capacity is not full -> create connection between LeftNode - RightNode
				if (!matches.isFull(preferNode)) {
					System.out.println(preferNode + " is not full.");
					//AddMatch (Node, NodeToConnect)
					matches.addMatch(preferNode, Node);
					matches.addMatch(Node, preferNode);
					MatchedNode.add(preferNode);
					break;
				} else {
					//If the RightNode's Capacity is Full then Left Node will Compete with Nodes that are inside RightNode
					//Loser will be the return value
					System.out.println(preferNode + " is full! Begin making a Compete game involve: " + Node + " ..." );
					int Loser = Compete(preferNode, Node, matches.getIndividualMatches(preferNode));
					//If RightNode is the LastChoice of Loser -> then
					// Loser will be terminated and Saved in Matches.LeftOvers Container
					System.out.println("Found Loser: " + Loser);
					if (Loser == Node) {
						if (LastChoice(Node) == preferNode) {
							System.out.println(Node + " has no where to go. Go to LeftOvers!");
							matches.addLeftOver(Loser);
							break;
						}
						//Or else Loser go back to UnMatched Queue & Waiting for it's Matching Procedure
					} else {
						matches.disMatch(preferNode, Loser);
						matches.disMatch(Loser, preferNode);
						UnMatchedNode.add(Loser);
						MatchedNode.remove((Integer) Loser);
						System.out.println(Loser + " lost the game, waiting for another chance.");
						matches.addMatch(preferNode, Node);
						matches.addMatch(Node, preferNode);
						MatchedNode.add(Node);
						System.out.println(Node + " is more suitable than " + Loser + " matched with " + preferNode);
						break;
					}
				}
			}
		}
		return matches;
	}

	private int getCapacityOfIndividual(int target) {
		return Individuals.get(target).getCapacity();
	}

	// Stable Matching Algorithm Component: isPreferredOver
	private boolean isPreferredOver(int male1, int male2, int female) {
		PreferenceList preference = preferenceLists.get(female);
		for (int i = 0; i < preference.size(); i++) {
			if (preference.getByIndex(i).getIndividualIndex() == male1) {
				return true;
			} else if (preference.getByIndex(i).getIndividualIndex() == male2) {
				return false;
			}
		}
		return false;
	}

	// return true if TargetNode is the last choice of Loser
	private int LastChoice(int loser) {
		PreferenceList pref = preferenceLists.get(loser);
		return pref.getByIndex(pref.size() - 1).getIndividualIndex();
	}

	public int Compete(int Judge, int newPlayer, List<Integer> oldPlayers) {
		PreferenceList JudgeScore = preferenceLists.get(Judge);
		if (Individuals.get(Judge).getCapacity() == 1) {
			if (isPreferredOver(newPlayer, oldPlayers.get(0), Judge)) {
				return oldPlayers.get(0);
			} else {
				return newPlayer;
			}
		} else {
			PreferenceList Game = new PreferenceList();
			// The issue lies here - Index out of Bound
			Game.add(JudgeScore.getByKey(newPlayer));
			// The issue lies here - Index out of Bound
			for (Integer oldPlayer : oldPlayers) {
				Game.add(JudgeScore.getByKey(oldPlayer));
			}
			Game.sort();
			// Return Loser
			return Game.getByIndex(Game.size() - 1).getIndividualIndex();
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
	 */
	private double withFitnessFunctionEvaluation(Matches matches, String fitnessFunction) {
		StringBuilder tmpSB = new StringBuilder();
		for (int c = 0; c < fitnessFunction.length(); c++) {
			char ch = fitnessFunction.charAt(c);
			if (ch == 'S') {
				if(Objects.equals(fitnessFunction.substring(c, c+5), "SIGMA")){
					if (fitnessFunction.charAt(c+5) != '{') {
						System.out.println("Missing '{'");
						System.out.println(fitnessFunction);
						throw new RuntimeException("Missing '{' after Sigma function");
					}else{
						int expressionStartIndex = c + 6;
						int expressionLength = getFunctionExpressionLength(fitnessFunction, expressionStartIndex);
						String expression = fitnessFunction.substring(expressionStartIndex, expressionStartIndex+expressionLength);
						double val = this.sigmaCalculate(matches, expression);
						tmpSB.append(val);
						c += expressionLength + 3;
					}
				}
				// Check for F(index) pattern
				if (c+ 3 < fitnessFunction.length() && fitnessFunction.charAt(c+1) ==  '(' && fitnessFunction.charAt(c + 3) == ')') {
					if(isNumericValue(fitnessFunction.charAt(c+2))) {
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

	private double sigmaCalculate(Matches matches, String expression){
		System.out.println("sigma calculating...");
		StringBuilder parseString = new StringBuilder();
		List<Double> streamValue = new ArrayList<>();
		String regex = null;
		int length = expression.length();
		for(int i = 0; i < length; i++){
			char ch = expression.charAt(i);
			if(ch == 'S'){
				char set = expression.charAt(i+1);
				if(set == '0'){
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
		for(int i = 0; i < streamLength; i++){
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

	private double getScale(Requirement requirement, double PropertyValue) {
		int type = requirement.getType();
		// Case: Scale
		if (type == 0) {
			int TargetValue = requirement.getTargetValue();
			if (PropertyValue < 0 || PropertyValue > 0) {
				return 0.0;
			} else {
				if (TargetValue != 0.0) {
					double Distance = Math.abs(PropertyValue - TargetValue);
					return (TargetValue - Distance) / TargetValue + 1;
				} else {
					return 0.0;
				}
			}
			//Case: 1 Bound
		} else if (type == 1) {
			Double Bound = requirement.getBound();
			String expression = requirement.getExpression();
			if (Objects.equals(expression, "++")) {
				if (PropertyValue < Bound) {
					return 0.0;
				} else {
					Double distance = Math.abs(PropertyValue - Bound);
					return (Bound + distance) / Bound;
				}
			} else {
				if (PropertyValue > Bound) {
					return 0.0;
				} else {
					Double distance = Math.abs(PropertyValue - Bound);
					return (Bound + distance) / Bound;
				}
			}
			//Case: 2 Bounds
		} else {
			Double lowerBound = requirement.getLowerBound();
			Double upperBound = requirement.getUpperBound();
			if (PropertyValue < lowerBound || PropertyValue > upperBound) {
				double medium = (lowerBound + upperBound) / 2;
				double distance = Math.abs(PropertyValue - medium);
				return (medium - distance) / medium + 1;
			}
		}
		return 0.0;
	}

	private double getSetSatisfactory(MatchSet matchSet) {
		if(matchSet.getIndividualMatches().isEmpty()){
			return 0.0;
		}
		int a = matchSet.getIndividualIndex();
		int cap = Individuals.get(a).getCapacity();
		PreferenceList ofInd = preferenceLists.get(a);
		if(cap == 1){
			int IndividualMatch = matchSet.getIndividualMatches().get(0);
			return ofInd.getByKey(IndividualMatch).getValue();
		}else {
			double setScore = 0.0;
			List<Integer> list = matchSet.getIndividualMatches();
			for (int x : list) {
				setScore += ofInd.getByKey(x).getValue();
			}
			return setScore;
		}
	}
	private  double calculateSatisfactoryOfASetByDefault(Matches matches, int set){
		double totalScore = 0.0;
		if(set == 0){
			for(int i = 0; i < this.numberOfIndividualOfSet0; i++){
				int a = matches.getSet(i).getIndividualIndex();
				List<Integer> list = matches.getSet(i).getIndividualMatches();
				PreferenceList ofInd = preferenceLists.get(a);
				double setScore = 0.0;
				for (int x : list) {
					setScore += ofInd.getByKey(x).getValue();
				}
				totalScore += setScore;
			}
		}else{
			for(int i = this.numberOfIndividualOfSet0; i < numberOfIndividual; i++){
				int a = matches.getSet(i).getIndividualIndex();
				PreferenceList ofInd = preferenceLists.get(a);
				int index = matches.getSet(i).getIndividualMatches().get(0);
				totalScore += ofInd.getByKey(index).getValue();
			}
		}
		return totalScore;
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

	public void setFitnessFunction(String fitnessFunction) {
		this.fitnessFunction = fitnessFunction;
	}

	public void setPopulation(ArrayList<Individual> individuals) {
		this.Individuals = individuals;
		this.numberOfIndividual = Individuals.size();
		this.numberOfIndividualOfSet0 = getNumberOfSet0();
		this.numberOfProperties = Individuals.get(0).getNumberOfProperties();
		this.preferenceLists = getPreferences();
	}

	public int getNumberOfSet0(){
		int c = 0;
		for(int i = 0; i < this.numberOfIndividual; i++){
			if (Individuals.get(i).getIndividualSet() == 0){
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
}
