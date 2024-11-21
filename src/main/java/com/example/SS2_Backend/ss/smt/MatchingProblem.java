package com.example.SS2_Backend.ss.smt;

import com.example.SS2_Backend.constants.MatchingConst;
import com.example.SS2_Backend.ss.smt.evaluator.FitnessEvaluator;
import com.example.SS2_Backend.ss.smt.match.Matches;
import com.example.SS2_Backend.ss.smt.preference.PreferenceListWrapper;
import com.example.SS2_Backend.util.StringUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.variable.Permutation;

/**
 * base class for MatchingProblem
 */
public interface MatchingProblem extends Problem {

    /**
     * Main matching logic for Stable Matching Problem Types
     *
     * @param var Variable
     * @return Matches
     */
    Matches stableMatching(Variable var);

    /**
     * Get Matching type name
     */
    String getMatchingTypeName();

}
