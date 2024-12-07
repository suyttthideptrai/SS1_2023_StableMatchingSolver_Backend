package com.example.SS2_Backend.constants;

import java.util.List;

public class MatchingConst {
    public static final String MATCHES_KEY = "matches";
    public static final int UNUSED_VALUE = 0;
    public static final String[] ALLOWED_INSIGHT_ALGORITHMS = {"NSGAII", "NSGAIII", "eMOEA", "PESA2", "VEGA"};
    public static final String DEFAULT_EVALUATE_FUNC = "default";
    public static final String EVALUATE_FUNC_REGEX = "[PWR0-9]*";

    /**
     * Requirement types
     */
    public interface ReqTypes {
        int SCALE_TARGET = 0;
        int ONE_BOUND = 1;
        int TWO_BOUND = 2;
        int TIME_SLOT = 3;
    }

    public interface InsightConfig {
        int POPULATION_SIZE = 50;
        int GENERATIONS = 100;
    }
}
