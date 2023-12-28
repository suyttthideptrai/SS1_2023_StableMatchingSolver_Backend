package com.example.SS2_Backend.model.StableMatching.Requirement;

import lombok.Getter;

import static com.example.SS2_Backend.util.Utils.formatDouble;

@Getter
public class OneBoundRequirement implements Requirement {
	private final Double bound;
	private final String expression;

	public OneBoundRequirement(Double bound, String expression) {
		this.bound = bound;
		this.expression = expression;
	}

	public int getType() {
		return 1;
	}

	@Override
	public Double getLowerBound() {
		return null;
	}

	@Override
	public Double getUpperBound() {
		return null;
	}

	public int getTargetValue() {
		return 0;
	}

	public String toString() {
		return "[" + formatDouble(bound) + ", " + expression + "]";
	}
}
