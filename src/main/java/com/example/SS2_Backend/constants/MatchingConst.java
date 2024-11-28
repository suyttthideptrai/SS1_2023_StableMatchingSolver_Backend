package com.example.SS2_Backend.constants;

public class MatchingConst {
    public static final String MATCHES_KEY = "matches";
    public static final int UNUSED_VALUE = 0;
    public static final String[] RUN_ALGORITHMS = {"NSGAII", "NSGAIII", "eMOEA", "PESA2", "VEGA"};
    public static final String[] INSIGHT_ALGORITHMS = {"NSGAII", "NSGAIII", "eMOEA", "PESA2", "VEGA"};
    /**
     * Requirement types
     */
    public interface ReqTypes {
        int SCALE_TARGET = 0;
        int ONE_BOUND = 1;
        int TWO_BOUND = 2;
        int TIME_SLOT = 3;
    }
}
