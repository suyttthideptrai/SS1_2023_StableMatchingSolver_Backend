package com.example.SS2_Backend.ss.smt.util;

import com.example.SS2_Backend.model.stableMatching.PreferenceList;
import com.example.SS2_Backend.ss.smt.match.Matches;

public class EvaluationUtils {
//    PreferenceList getPreferenceOfIndividual(int index); // the logic of this logic will not be changed, once defined, it will be used in all child classes
//    boolean isPreferredOver(int newNode, int currentNode, int SelectorNode); // the logic of this logic will not be changed, once defined, it will be used in all child classes
//    int getLastChoiceOf(int target); // the logic of this logic will not be changed, once defined, it will be used in all child classes
//    int getLeastScoreNode(int selectorNode, int newNode, Integer[] occupiedNodes); // the logic of this logic will not be changed, once defined, it will be used in all child classes
//    double defaultFitnessEvaluation(double[] Satisfactions); // the logic of this logic will not be changed, once defined, it will be used in all child classes
//    double withFitnessFunctionEvaluation(double[] satisfactions, String fitnessFunction); // the logic of this logic will not be changed, once defined, it will be used in all child classes
//    double sigmaCalculate(double[] satisfactions, String expression); // the logic of this logic will not be changed, once defined, it will be used in all child classes
//    int getSigmaFunctionExpressionLength(String function, int startIndex); // the logic of this logic will not be changed, once defined, it will be used in all child classes
//    double[] getAllSatisfactions(Matches matches); // the logic of this logic will not be changed, once defined, it will be used in all child classes
//    double[] getSatisfactoryOfASetByDefault(double[] Satisfactions, int set); // the logic of this logic will not be changed, once defined, it will be used in all child classes
//    String toString(); // the logic of this logic will not be changed, once defined, it will be used in all child classes

    public static double[] getAllSatisfactions(Matches matches){
        return new double[0];
    }

    public static double defaultFitnessEvaluation(double[] satisfactions){
        return 0;
    }

    public static double withFitnessFunctionEvaluation(double[] satisfactions, String fitnessFunction){
        return 0;
    }

    public static int getLeastScoreNode(int selectorNode, int newNode, int[] occupiedNodes, int[] individualCapacities, PreferenceList preferenceLists){
        PreferenceList prefOfSelectorNode = preferenceLists.get(selectorNode);
        if (individualCapacities[selectorNode] == 1) {
            int currentNode = occupiedNodes[0];
            if (isPreferredOver(newNode, currentNode, selectorNode)) {
                return currentNode;
            } else {
                return newNode;
            }
        } else {
            return prefOfSelectorNode.getLeastNode(newNode, occupiedNodes);
        }
    }

}
