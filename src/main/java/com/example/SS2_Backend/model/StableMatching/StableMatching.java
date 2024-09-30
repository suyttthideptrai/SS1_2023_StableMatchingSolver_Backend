package com.example.SS2_Backend.model.StableMatching;

import com.example.SS2_Backend.model.StableMatching.Matches.Matches;
import com.example.SS2_Backend.model.StableMatching.Matches.MatchesOTO;
import org.moeaframework.core.Problem;

import java.util.ArrayList;

public interface StableMatching extends Problem {
    String getProblemName();
    void setProblemName(String name);
    void setEvaluateFunctionForSet1(String func);
    void setEvaluateFunctionForSet2(String func);
    void setFitnessFunction(String func);
    void setPopulation(ArrayList<Individual> individuals, String[] propertiesNames);
    double[] getAllSatisfactions(MatchesOTO res);
    double[] getAllSatisfactions(Matches res);
}
