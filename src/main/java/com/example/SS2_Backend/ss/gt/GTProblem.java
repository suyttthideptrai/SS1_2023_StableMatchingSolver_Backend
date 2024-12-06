package com.example.SS2_Backend.ss.gt;

import com.example.SS2_Backend.model.gameTheory.Conflict;
import com.example.SS2_Backend.model.gameTheory.NormalPlayer;
import com.example.SS2_Backend.model.gameTheory.SpecialPlayer;
import org.moeaframework.core.Problem;

import java.util.List;

public interface GTProblem extends Problem {

    void setDefaultPayoffFunction(String payoffFunction);

    void setFitnessFunction(String fitnessFunction);

    void setSpecialPlayer(SpecialPlayer specialPlayer);

    void setNormalPlayers(List<NormalPlayer> normalPlayers);

    void setConflictSet(List<Conflict> conflictSet);

    void setMaximizing(boolean isMaximizing);

    List<NormalPlayer> getNormalPlayers();

}
