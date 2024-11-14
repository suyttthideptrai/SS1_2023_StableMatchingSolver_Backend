package com.example.SS2_Backend.constants;

public class MessageConst {
    public interface MatchingValidate {
        /** validate number of set */
        String MES_001 = "Number of set must be greater or equal to 2";
        /** validate number of individualNumber */
        String MES_002 = "The number of individuals (or corresponding elements that related to the number of individuals) should be at least 3";
        String MES_003 = "There should be at least one property";
        String MES_GREATER_THAN_ZERO = "It should be greater than 0";
    }
}
