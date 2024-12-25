package com.example.SS2_Backend.ss.gt.implement;

import com.example.SS2_Backend.ss.gt.Conflict;
import com.example.SS2_Backend.ss.gt.GameTheoryProblem;
import com.example.SS2_Backend.ss.gt.NormalPlayer;
import com.example.SS2_Backend.ss.gt.SpecialPlayer;
import com.example.SS2_Backend.ss.gt.Strategy;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * # This GT problem consider the 1st player of the List<NormalPlayer> is PolyStrategicPlayer (PSP)
 * # PolyStrategicPlayer is a player which properties could be numerically randomized
 * #
 * # Specially designed for SS1 Group 4 - Thursday (F2024)
 * #
 * # @info Might need some work to make it unify, code as requirement is (current).
 */
@Data
@Slf4j
@NoArgsConstructor
public class PolyStrategicPlayerGTProblem implements GameTheoryProblem, Serializable {

  public interface AttributeKeys {
    String PSP_PAYOFF = "PSP_PAYOFF";
    String NORMAL_PAYOFFS = "NORMAL_PAYOFFS";
  }

  private String problemName;
  private final int PSP_IDX = 0;
  private List<NormalPlayer> normalPlayers;
  /**
   * Number of properties for PSP
   * 1: fee
   * 2: score
   */
  private final int PSPPropertyNum = 2;
  /**
   * Conditions for generating property value
   * [pos][0] | Lower bound
   * [pos][1] | Upper bound
   */
  private double[][] PSPPropertyConditions;
  private String fitnessFunction;
  private String defaultPayoffFunction;

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
    log.info("{}:setMaximizing: do nothing.", this.getClass().getSimpleName());

  }

  @Override
  public List<NormalPlayer> getNormalPlayers() {
    return this.normalPlayers;
  }

  @Override
  public boolean getPSOCompatibility() {
    return false;
  }

  @Override
  public void setNormalPlayers(List<NormalPlayer> normalPlayers) {
    this.normalPlayers = normalPlayers;

    // PSP initialize
    NormalPlayer psp = normalPlayers.get(this.PSP_IDX);
    int numberOfProperties = psp.getStrategies().size();
    this.PSPPropertyConditions = new double[numberOfProperties][2];
    for (int i = 0; i < numberOfProperties; i++) {
      Strategy strategy = psp.getStrategies().get(i);
      double lowerBound = strategy.getProperties().get(0);
      double upperBound = strategy.getProperties().get(1);
      this.PSPPropertyConditions[i][0] = lowerBound;
      this.PSPPropertyConditions[i][1] = upperBound;
    }
  }

  @Override
  public String getName() {
    return "chay eMOEA ma` return null thi bi exception";
  }

  @Override
  public int getNumberOfVariables() {
    return this.PSPPropertyNum - 1 + this.normalPlayers.size();
  }

  @Override
  public int getNumberOfObjectives() {
    return 1;
  }

  @Override
  public int getNumberOfConstraints() {
    // Constraint free
    return 0;
  }

  @Override
  public void evaluate(Solution solution) {

    double solutionFee = EncodingUtils.getReal(solution.getVariable(0));
    double solutionScore = EncodingUtils.getReal(solution.getVariable(1));
    log.info("evaluate: fee {}, min_score {}", solutionFee, solutionScore);

    double[] payoffs = new double[this.normalPlayers.size() - 1];

    int[] chosenStrategyIndices = new int[solution.getNumberOfVariables()];
    //Ignore the 1st player as it is PolyPlayer
    for (int i = 1; i < normalPlayers.size(); i++) {
      chosenStrategyIndices[i] = this.getSolutionStrategyIdx(solution, i);
    }

    /*
    {CUSTOM} Calculate payoff for PSP
     */

    double minimumRevenue = this.PSPPropertyConditions[2][0];

    final int chooseToEnrollIndex = 0;
    int enrollPlayerCount = 0;
    double sumScores = 0.0;

    // calculate the payoff foreach Student
    /*
    University Player Fee :  solutionFee
    Student Player Fee    :  playerFinancialCapacity

    Payoff = playerFinancialCapacity - solutionFee
     */
    for (int i = 1; i < normalPlayers.size(); i++) {
      NormalPlayer normalPlayer = normalPlayers.get(i);
      double playerPayOff;
      if (chosenStrategyIndices[i] == chooseToEnrollIndex) {
        enrollPlayerCount++;
        sumScores += normalPlayer.getStrategyAt(0).getProperties().get(1);
        double playerFinancialCapacity = normalPlayer.getStrategyAt(0).getProperties().get(0);
        playerPayOff = playerFinancialCapacity - solutionFee;
      } else {
        playerPayOff = 0.0;
      }
    payoffs[i-1] = playerPayOff;
    }

    // Calculate payoff for University
    /*
    P-Uni = average enroll student score + (actual revenue - minimum revenue)
     */
    double AVERAGE_SCORE = sumScores / enrollPlayerCount;
    double REVENUE_PAYOFF = (solutionFee * enrollPlayerCount) / minimumRevenue;

    double universityPayoff = AVERAGE_SCORE + REVENUE_PAYOFF;

    // Calculate payoff for Student players
    /*
    P-Stu(s) = SUM(student_payoffs)
     */
    double SUM_STUDENT_PAYOFF = Arrays.stream(payoffs).sum();

    BigDecimal fitnessValue = new BigDecimal(universityPayoff + SUM_STUDENT_PAYOFF);

    // Maximize by default
    fitnessValue = fitnessValue.negate();

    solution.setAttribute(AttributeKeys.PSP_PAYOFF, universityPayoff);
    solution.setAttribute(AttributeKeys.NORMAL_PAYOFFS, payoffs);

    solution.setObjective(0, fitnessValue.doubleValue());
  }

  @Override
  public Solution newSolution() {
    int numbeOfNP = this.normalPlayers.size() - 1; // Minus PSP
    Solution solution = new Solution(numbeOfNP + this.PSPPropertyNum, 1, 0);

    // PSP properties optimizing
    for (int i = 0; i < this.PSPPropertyNum; i++) {
      double lowerBound = this.PSPPropertyConditions[i][0];
      double upperBound = this.PSPPropertyConditions[i][1];
      RealVariable variable = new RealVariable(lowerBound, upperBound);
      solution.setVariable(i, variable);
    }

    // Normal player strategy selection optimizing
    int playerCount = 1;
    int totalVariable = numbeOfNP + this.PSPPropertyNum;
    for (int i = this.PSPPropertyNum; i < totalVariable; i++) {
      NormalPlayer player = this.normalPlayers.get(playerCount);
      playerCount ++;
      RealVariable variable = new RealVariable(0, player.getStrategies().size() - 0.01);
      solution.setVariable(i, variable);
    }
    return solution;
  }

  private int getSolutionStrategyIdx(Solution solution, int normalPlayerPosition) {
    int beginIndex = (this.PSPPropertyNum - 1);
    Variable playerVariable = solution.getVariable(beginIndex + normalPlayerPosition);
    return EncodingUtils.getInt(playerVariable);
  }

  @Override
  public void close() {
    log.info("{}:close: do nothing.", this.getClass().getSimpleName());
  }

}


