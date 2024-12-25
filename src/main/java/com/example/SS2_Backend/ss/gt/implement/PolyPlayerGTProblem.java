package com.example.SS2_Backend.ss.gt.implement;

import com.example.SS2_Backend.ss.gt.Conflict;
import com.example.SS2_Backend.ss.gt.GameTheoryProblem;
import com.example.SS2_Backend.ss.gt.NormalPlayer;
import com.example.SS2_Backend.ss.gt.SpecialPlayer;
import com.example.SS2_Backend.ss.gt.Strategy;
import com.example.SS2_Backend.util.StringExpressionEvaluator;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.variable.BinaryIntegerVariable;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import static com.example.SS2_Backend.util.StringExpressionEvaluator.*;

/**
 * # This GT problem consider the 1st player of the list is PolyPlayer
 * # PolyPlayer is a player which properties could be numerically randomized
 * #
 * # Specially designed for SS1 Group 4 - Thursday (F2024)
 * # @warning NON PSO COMPATIBLE
 */
@Data
@Slf4j
public class PolyPlayerGTProblem implements GameTheoryProblem, Serializable {

  private final String problemName;
  private final int PP_IDX = 0;
  private final List<NormalPlayer> normalPlayers;
  /**
   * Number of properties for pPlayer
   */
  private final int polyPlayerPropertyNum;
  /**
   * Conditions for generating property value
   * [pos][0] | Lower bound
   * [pos][1] | Upper bound
   */
  private final double[][] pPlayerPropertyConditions;
  private String fitnessFunction;
  private String defaultPayoffFunction;
  private boolean isMaximizing;

  @Override
  public void setDefaultPayoffFunction(String payoffFunction) {
    this.defaultPayoffFunction = payoffFunction;
  }

  @Override
  public void setFitnessFunction(String fitnessFunction) {
    this.fitnessFunction = fitnessFunction;
  }

  @Override
  public void setSpecialPlayer(SpecialPlayer specialPlayer) {
    log.info("{}:setSpecialPlayer: do nothing.", this.getClass().getSimpleName());
  }

  @Override
  public void setConflictSet(List<Conflict> conflictSet) {
    log.info("{}:setConflictSet: do nothing.", this.getClass().getSimpleName());
  }

  @Override
  public void setMaximizing(boolean isMaximizing) {
    this.isMaximizing = isMaximizing;
  }

  @Override
  public List<NormalPlayer> getNormalPlayers() {
    return this.normalPlayers;
  }

  @Override
  public void setNormalPlayers(List<NormalPlayer> normalPlayers) {

  }

  @Override
  public String getName() {
    return this.problemName;
  }

  @Override
  public int getNumberOfVariables() {
    return this.polyPlayerPropertyNum + this.normalPlayers.size();
  }

  @Override
  public int getNumberOfObjectives() {
    return 1;
  }

  @Override
  public int getNumberOfConstraints() {
    return 0;
  }

  @Override
  public void evaluate(Solution solution) {
    double[] payoffs = new double[this.normalPlayers.size()];

    int[] chosenStrategyIndices = new int[solution.getNumberOfVariables()];
    //Ignore the 1st player as it is PolyPlayer
    for (int i = 1; i < normalPlayers.size(); i++) {
      chosenStrategyIndices[i] = this.getSolutionStrategyIdx(solution, i);
    }

    // calculate the payoff of the strategy each player has chosen
    for (int i = 0; i < normalPlayers.size(); i++) {
      NormalPlayer normalPlayer = normalPlayers.get(i);
      Strategy chosenStrategy = normalPlayer.getStrategyAt(chosenStrategyIndices[i]);

      String payoffFunction = normalPlayer.getPayoffFunction();
      // if the player does not have his own payoff function, use the default one
      if (payoffFunction == null) {
        payoffFunction = defaultPayoffFunction;
      }

      BigDecimal chosenStrategyPayoff = new BigDecimal(0);
      if (payoffFunction.contains("P")) {
        chosenStrategyPayoff = StringExpressionEvaluator.evalPayoffWithPolyPlayer(
                chosenStrategy,
                payoffFunction,
                normalPlayers,
                chosenStrategyIndices,
                solution
        );
      } else {
        // if the payoff function is relative to the player itself, then it can be calculated in the initialization
        chosenStrategyPayoff = normalPlayer.getPayoffValues().get(chosenStrategyIndices[i]);
      }

      chosenStrategy.setPayoff(chosenStrategyPayoff.doubleValue());
      payoffs[i] = chosenStrategyPayoff.doubleValue();
    }

    BigDecimal fitnessValue
            = evaluateFitnessValue(
            payoffs,
            fitnessFunction
    );

    if (isMaximizing) {
      //Don't ask me, ask MOEA framework documents
      fitnessValue = fitnessValue.negate();
    }

    solution.setObjective(0, fitnessValue.doubleValue());
  }

  @Override
  public Solution newSolution() {
    int numbeOfNP = this.normalPlayers.size();
    Solution solution = new Solution(numbeOfNP, 1);

    for (int i = 0; i < this.polyPlayerPropertyNum; i++) {
      double lowerBound = this.pPlayerPropertyConditions[i][0];
      double upperBound = this.pPlayerPropertyConditions[i][1];
      RealVariable variable = new RealVariable(lowerBound, upperBound);
      solution.setVariable(i, variable);
    }
    int solutionOffSet = this.PP_IDX + this.polyPlayerPropertyNum;
    for (int i = solutionOffSet; i < numbeOfNP + solutionOffSet; i++) {
      NormalPlayer player = this.normalPlayers.get(i);
      /*
        Use this instead for PSO compatibility
        //RealVariable variable = new RealVariable(0, player.getStrategies().size() - 0.01);
       */
      BinaryIntegerVariable variable = new BinaryIntegerVariable(0, player.getStrategies().size() - 1);
      solution.setVariable(i, variable);
    }
    return solution;
  }

  private int getSolutionStrategyIdx(Solution solution, int normalPlayerPosition) {
    Variable playerVariable = solution.getVariable(
            this.polyPlayerPropertyNum + normalPlayerPosition);
    return EncodingUtils.getInt(playerVariable);
  }

  @Override
  public void close() {

  }

}
