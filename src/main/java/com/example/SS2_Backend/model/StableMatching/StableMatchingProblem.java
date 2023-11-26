package com.example.SS2_Backend.model.StableMatching;

import java.util.*;

import com.example.SS2_Backend.model.StableMatching.Requirement.Requirement;
import lombok.Getter;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.variable.EncodingUtils;

import static com.example.SS2_Backend.util.Utils.formatDouble;

/**
 *Base case of Stable Matching Problem (One to One) : Number of Individuals in two sets are Equal (n1 = n2)
 *                                                  : Every Individual inside the Population have equal number of Properties                                                 : Every Individual inside the Population have the same way to evaluate Partner
 *Wish to test this Class? Run "src.main.java.com.example.SS2_Backend.util.SampleDataGenerator.java"
 **/

public class StableMatchingProblem implements Problem {
    private ArrayList<Individual> Individuals; // Storing Data of the Whole population
    @Getter
    private int numberOfSets;
    @Getter
    private int numberOfIndividual;
    @Getter
    private int numberOfProperties;
    private String[] PropertiesName;
    private List<PreferenceList> preferenceLists; // Preference List of each Individual
    @Getter
    private String fitnessFunction; // Evaluate total Score of each Solution set

    //Constructor

    public StableMatchingProblem(){

    }
    public StableMatchingProblem(ArrayList<Individual> Individuals, String[] PropertiesName,  String fitnessFunction) {
        this.Individuals = Individuals;
        this.numberOfIndividual = Individuals.size();
        this.numberOfProperties = Individuals.get(0).getNumberOfProperties();
        this.PropertiesName = PropertiesName;
        //this.compositeWeightFunction = compositeWeightFunction;
        this.fitnessFunction = fitnessFunction;
        this.preferenceLists = getPreferences(); // Construct Preference List based on the given above Individuals data
    }

    //MOEA Solution Definition
    @Override
    public Solution newSolution() {
        Solution solution = new Solution(1, 1);
        // Randomize the order (from 0 to this.NumberOfIndividual)
        solution.setVariable(0, EncodingUtils.newPermutation(this.numberOfIndividual));
        return solution;
    }

    // Need to edit to evaluate each Individual based on the Composite Weight function (Expression Evaluator Utility) provided for each set (more complex problem)
    public PreferenceList getPreferenceOfIndividual(int index) {
        PreferenceList a = new PreferenceList();
        // get this Individual set belong to
        int set = Individuals.get(index).getIndividualSet();
        // Calc totalScore of others for this Individual
        for (int i = 0; i < numberOfIndividual; i++) {
            if(Individuals.get(i).getIndividualSet() != set){
                double totalScore = 0;
                for (int j = 0; j < numberOfProperties; j++) {
                    double Score = 0.0;
                    Double PropertyValue = Individuals.get(i).getPropertyValue(j);
                    Requirement requirement = Individuals.get(index).getRequirement(j);
                    int PropertyWeight = Individuals.get(index).getPropertyWeight(j);
                    // Case: Scale
                    if (requirement.getType() == 0){
                      int TargetValue = requirement.getTargetValue();
                      if(PropertyValue < 0 || PropertyValue > 0){
                          Score += 0;
                      }else{
                          if(TargetValue != 0.0){
                              double Distance = Math.abs(PropertyValue-TargetValue);
                              double Scale = (TargetValue-Distance)/TargetValue + 1;
                              Score += Scale;
                          }else{
                              Score += 0;
                          }
                      }
                        //Case: 1 Bound
                    } else if (requirement.getType() == 1){
                        Double Bound = requirement.getBound();
                        String expression = requirement.getExpression();
                        if(Objects.equals(expression, "++")){
                            if(PropertyValue < Bound){
                                Score += 0.0;
                            }else{
                                Double distance = Math.abs(PropertyValue - Bound);
                                double Scale = (Bound + distance)/Bound;
                                Score = Scale * PropertyWeight;
                            }
                        }else{
                            if(PropertyValue > Bound){
                                Score += 0.0;
                            }else{
                                Double distance = Math.abs(PropertyValue - Bound);
                                double Scale = (Bound + distance)/Bound;
                                Score = Scale * PropertyWeight;
                            }
                        }
                    //Case: 2 Bounds
                    }else{
                        Double lowerBound = requirement.getLowerBound();
                        Double upperBound = requirement.getUpperBound();
                        if(PropertyValue < lowerBound || PropertyValue > upperBound){
                            Double medium = (lowerBound + upperBound)/2;
                            Double distance = Math.abs(PropertyValue - medium);
                            double Scale = (medium-distance)/medium + 1;
                            Score = Scale * PropertyWeight;
                        }
                    }
                    totalScore += Score;
                }
                // Add
                a.add(new PreferenceList.IndexValue(i, totalScore));
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
    // Gale Shapley: Stable Matching Algorithm
    public Matches stableMatching(Variable var) {
        Matches matches = new Matches();
        Queue<Integer> unmatchedMales = new LinkedList<>();
        LinkedList<Integer> engagedFemale = new LinkedList<>();

        String s = var.toString();

        String[] decodedSolution = s.split(",");
        for (String token : decodedSolution) {
            try {
                // Convert each token to an Integer and add it to the queue
                int i = Integer.parseInt(token);
                if (Individuals.get(i).getIndividualSet() == 1) {
                    unmatchedMales.add(i);
                }
            } catch (NumberFormatException e) {
                // Handle invalid tokens (non-integer values)
                System.err.println("Skipping invalid token: " + token);
                return null;
            }
            //System.out.println("Solution: " + java.util.Arrays.toString(decodedSolution));
        }

        while (!unmatchedMales.isEmpty()) {
            int male = unmatchedMales.poll();
            //System.out.println("working on Individual:" + male);
            PreferenceList preferenceList = preferenceLists.get(male);
            //System.out.print("Hmm ... He prefers Individual ");
            for (int i = 0; i < preferenceList.size(); i++) {
                int female = preferenceList.getByIndex(i).getIndividualIndex();
                //System.out.println(female);
                if (!engagedFemale.contains(female)) {
                    engagedFemale.add(female);
                    matches.add(new Pair(male, female));
                    //System.out.println(male + female + " is now together");
                    break;
                } else {
                    int currentMale = Integer.parseInt(matches.findCompany(female));
                    //System.out.println("Oh no, she is with " + currentMale + " let see if she prefers " + male + " than " + currentMale );
                    if (isPreferredOver(male, currentMale, female)) {
                        matches.disMatch(currentMale);
                        unmatchedMales.add(currentMale);
                        matches.add(new Pair(male, female));
                        //System.out.println("Hell yeah! " + female + " ditch the guy " + currentMale + " to be with " + male + "!");
                        break;
                    }
                    else {
                        if(preferenceList.getByIndex(preferenceList.size()-1).getIndividualIndex() == female){
                            matches.addLeftOver(male);
                        }
                        //System.out.println(male + " lost the game, back to the hood...");
                    }
                }
            }
        }
        for(int i = 0; i < Individuals.size(); i++){
            if(Individuals.get(i).getIndividualSet() == 0){
                if(!engagedFemale.contains(i)){
                    matches.addLeftOver(i);
                }
            }
        }
        //System.out.println("Matching Complete!!");
        return matches;
    }

//    private Matches StableMatchingExtra(Variable var){
//        Matches matches = new Matches();
//        Queue<Integer> unMatchedLeftSideNode = new LinkedList<>();
//        //List<Integer> matchedRightSideNode = new LinkedList<>();
//
//        String s = var.toString();
//
//        String[] decodedSolution = s.split(",");
//        for (String token : decodedSolution) {
//            try {
//                // Convert each token to an Integer and add it to the queue
//                int i = Integer.parseInt(token);
//                matches.add(new MatchSet(i, getCapacityOfIndividual(i)));
//                if (Individuals.get(i).getIndividualSet() == 1) {
//                    unMatchedLeftSideNode.add(i);
//                }
//            } catch (NumberFormatException e) {
//                // Handle invalid tokens (non-integer values)
//                System.err.println("Skipping invalid token: " + token);
//                return null;
//            }
//        }
//
//        while(!unMatchedLeftSideNode.isEmpty()){
//            System.out.println(matches);
//            int leftNode = unMatchedLeftSideNode.poll();
//            System.out.println("working on Node:" + leftNode);
//            //Get pref List of LeftNode
//            PreferenceList NodePreference = preferenceLists.get(leftNode);
//           //Loop through LeftNode's preference list to find a Match
//            for (int i = 0; i < NodePreference.size(); i++){
//                //Next Match (RightNode) is found on the list
//                int rightNode = NodePreference.getByIndex(i).getIndividualIndex();
//                System.out.println(leftNode + " Prefer : " + rightNode);
//                //If the RightNode Capacity is not full -> create connection between LeftNode - RightNode
//                if(!matches.isFull(rightNode)) {
//                    System.out.println(rightNode + " is not full.");
//                    //AddMatch (Node, NodeToConnect)
//                    matches.addMatch(rightNode, leftNode);
//                    matchedRightSideNode.add(rightNode);
//                    break;
//                }else{
//                    //If the RightNode's Capacity is Full then Left Node will Compete with Nodes that are inside RightNode
//                    //Loser will be the return value
//                    System.out.println(rightNode + " is full! Begin making a Compete game involve: " + leftNode + " ..." );
//                    int Loser = Compete(rightNode, leftNode, matches.getIndividualMatches(rightNode));
//                    //If RightNode is the LastChoice of Loser -> then
//                    // Loser will be terminated and Saved in Matches.LeftOvers Container
//                    System.out.println("Found Loser: " + Loser);
//                    if(LastChoice(Loser) == rightNode){
//                        System.out.println(Loser + " has no where to go. Go to LeftOvers!");
//                        matches.disMatch(rightNode, Loser);
//                        matches.addMatch(rightNode, leftNode);
//                        matches.addLeftOver(Loser);
//                        break;
//                    //Or else Loser go back to UnMatched Queue & Waiting for it's Matching Procedure
//                    }else{
//                        System.out.println(Loser + " lost the game, waiting for the second chance.");
//                        unMatchedLeftSideNode.add(Loser);
//                        break;
//                    }
//                }
//            }
//        }
//        return matches;
//    }

    private int getCapacityOfIndividual(int target){
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
    private int LastChoice(int loser){
        PreferenceList pref = preferenceLists.get(loser);
        return pref.getByIndex(pref.size()-1).getIndividualIndex();
    }
    public int Compete(int Judge, int newPlayer, List<Integer> oldPlayers){
        PreferenceList JudgeScore = preferenceLists.get(Judge);
        PreferenceList Game = new PreferenceList();
        // The issue lies here - Index out of Bound
        Game.add(JudgeScore.getByKey(newPlayer));
        // The issue lies here - Index out of Bound
        for(int i = 0; i < oldPlayers.size(); i++){
            Game.add(JudgeScore.getByKey(oldPlayers.get(i)));
        }
        Game.sort();
        // Return Loser
        return Game.getByIndex(Game.size()-1).getIndividualIndex();
    }

    // Calculate each pair Satisfactory of the result produced By Stable Matching Algorithm
    private double calculatePairSatisfactory(MatchItem pair) {
        int a = pair.getIndividual1Index();
        int b = pair.getIndividual2Index();
        PreferenceList ofA = preferenceLists.get(a);
        PreferenceList ofB = preferenceLists.get(b);
        double aScore = 0.0;
        double bScore = 0.0;
        for (int i = 0; i < ofA.size(); i++) {
            if(ofA.getByIndex(i).getIndividualIndex()==b){
                aScore += ofA.getByIndex(i).getValue();
            }
        }
        for (int i = 0; i < ofB.size(); i++) {
            if(ofB.getByIndex(i).getIndividualIndex()==a){
                bScore += ofA.getByIndex(i).getValue();
            }
        }
        return aScore + bScore;
    }

    // Evaluate
    public void evaluate(Solution solution) {
        Matches result = stableMatching(solution.getVariable(0));
        //System.out.println(solution.getVariable(1).toString());
        double fitnessScore = 0;
        if (result != null) {
            for (int i = 0; i < result.size(); i++) {
                fitnessScore += calculatePairSatisfactory(result.getPair(i));
            }
        }
        solution.setAttribute("matches", result);
        solution.setObjective(0, -fitnessScore);

    }
    @Override
    public String getName() {
        return "Two Sided Stable Matching Problem";
    }

    public boolean isPreferenceEmpty(){
        return preferenceLists.isEmpty();
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

    public List<PreferenceList> getPreferenceLists(){
        return this.preferenceLists;
    }

    private String getPropertyNameOfIndex(int index){
        return PropertiesName[index];
    }

    public Double getPropertyValueOf(int index, int jndex){
        return Individuals.get(index).getPropertyValue(jndex);
    }

    public int getPropertyWeightOf(int index, int jndex){
        return Individuals.get(index).getPropertyWeight(jndex);
    }
    private static String fillWithChar(char character, int width) {
        String format = "%" + width + "s";
        return String.format(format, "").replace(' ', character);
    }

    public void printIndividuals(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i< this.numberOfProperties; i++){
                sb.append(String.format("%-16s| ", this.getPropertyNameOfIndex(i)));
        }
        String propName = sb.toString();
        sb.delete(0, sb.length());
        //header
        System.out.println("No | Set | Name                | " + propName );
        int width = this.numberOfProperties * 18 + 32;
        String filledString = fillWithChar('-', width);
        sb.append(filledString).append("\n");
        //content
        for (int i = 0; i<this.numberOfIndividual; i++){
            //name / set
            sb.append(String.format("%-3d| ", i));
            sb.append(String.format("%-4d| ", Individuals.get(i).getIndividualSet()));
            sb.append(String.format("%-20s| ", Individuals.get(i).getIndividualName()));
            // prop value
            StringBuilder ss = new StringBuilder();
            for (int j = 0; j< this.numberOfProperties; j++){
                ss.append(String.format("%-16s| ", formatDouble(this.getPropertyValueOf(i,j))));
            }
            sb.append(ss).append("\n");
            ss.delete(0, sb.length());
            ss.append(String.format("%33s", "Requirement: | "));
            for (int j = 0; j< this.numberOfProperties; j++){
                ss.append(String.format("%-16s| ", this.Individuals.get(i).getRequirement(j).toString()));
            }
            sb.append(ss).append("\n");
            ss.delete(0, sb.length());
            ss.append(String.format("%33s", "Weight: | "));
            for (int j = 0; j< this.numberOfProperties; j++){
                ss.append(String.format("%-16s| ", this.getPropertyWeightOf(i,j)));
            }
            sb.append(ss).append("\n");
        }
        sb.append(filledString).append("\n");
        System.out.print(sb);
    }


    public void close() {
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < Individuals.size(); i++){
            sb.append(Individuals.get(i).toString()).append("\n");
        }
        return numberOfProperties + "\n" + fitnessFunction + "\n" + sb;
    }

    public String printPreferenceLists() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < preferenceLists.size(); i ++){
            sb.append("Individual " + i + " : ");
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
        this.numberOfProperties = Individuals.get(0).getNumberOfProperties();
        this.preferenceLists = getPreferences();
    }

    public void setAllPropertyNames(String[] allPropertyNames) {
        this.PropertiesName = allPropertyNames;
    }
}
