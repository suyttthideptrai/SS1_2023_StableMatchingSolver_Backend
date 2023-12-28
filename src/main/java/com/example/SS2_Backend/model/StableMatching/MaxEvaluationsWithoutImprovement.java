package com.example.SS2_Backend.model.StableMatching;

import org.moeaframework.core.Algorithm;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.TerminationCondition;

public class MaxEvaluationsWithoutImprovement implements TerminationCondition {
	private final int maxEvaluationsWithoutImprovement;
	private int evaluationsWithoutImprovement;
	private double lastFitness = 0;
	public MaxEvaluationsWithoutImprovement(int maxEvaluationsWithoutImprovement) {
		super();
		this.maxEvaluationsWithoutImprovement = maxEvaluationsWithoutImprovement;
		this.evaluationsWithoutImprovement = 0;
	}

	@Override
	public void initialize(Algorithm algorithm) {

	}

	@Override
	public boolean shouldTerminate(Algorithm algorithm) {
		NondominatedPopulation result = algorithm.getResult();
		boolean isBetter = true;
		for (Solution solution : result) {
			double fitnessScore = -solution.getObjective(0);
			if (fitnessScore < this.lastFitness) {
				isBetter = false;
			}else{
				this.lastFitness = fitnessScore;
			}
		}
		if (isBetter) {
			evaluationsWithoutImprovement = 0;
		} else {
			evaluationsWithoutImprovement++;
		}
		return evaluationsWithoutImprovement >= maxEvaluationsWithoutImprovement;
	}
}
