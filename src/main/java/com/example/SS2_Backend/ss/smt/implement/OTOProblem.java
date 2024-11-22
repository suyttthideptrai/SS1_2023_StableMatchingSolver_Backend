package com.example.SS2_Backend.ss.smt.implement;

import com.example.SS2_Backend.ss.smt.Matches;
import com.example.SS2_Backend.ss.smt.MatchingData;
import com.example.SS2_Backend.ss.smt.MatchingProblem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;

//TODO: finish implementing
public class OTOProblem implements MatchingProblem {

    @Override
    public String getName() {
        return "";
    }

    @Override
    public int getNumberOfVariables() {
        return 0;
    }

    @Override
    public int getNumberOfObjectives() {
        return 0;
    }

    @Override
    public int getNumberOfConstraints() {
        return 0;
    }

    @Override
    public void evaluate(Solution solution) {

    }

    @Override
    public Solution newSolution() {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public String getMatchingTypeName() {
        return "";
    }

    @Override
    public MatchingData getMatchingData() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Matches stableMatching(Variable var) {
        return null;
    }

    @Override
    public double[] getMatchesSatisfactions(Matches matches) {
        return new double[0];
    }

}
