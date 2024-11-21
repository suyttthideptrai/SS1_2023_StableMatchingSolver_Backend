package com.example.SS2_Backend.constants;

public class MatchingConst {
    public static final String MatchesKey = "matches";
    public static final int UNUSED_VALUE = 0;

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
