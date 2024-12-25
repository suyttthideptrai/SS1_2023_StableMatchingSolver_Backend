package com.example.SS2_Backend.ss.gt;

import org.moeaframework.core.Problem;

import java.util.List;

/**
 * Base class for Game theory problem
 */
public interface GameTheoryProblem extends Problem {

    void setDefaultPayoffFunction(String payoffFunction);

    void setFitnessFunction(String fitnessFunction);

    void setSpecialPlayer(SpecialPlayer specialPlayer);

    void setNormalPlayers(List<NormalPlayer> normalPlayers);

    void setConflictSet(List<Conflict> conflictSet);

    void setMaximizing(boolean isMaximizing);

    List<NormalPlayer> getNormalPlayers();

}
