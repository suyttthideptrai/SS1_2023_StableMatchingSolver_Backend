package com.example.SS2_Backend.util;

public class EvaluationUtils {

    /**
     * get length of ??CONTENT?? inside SIGMA{??CONTENT??}
     *
     * @param function function String
     * @param startIndex "{" start bracket index
     * @return length
     */
    public static int getSigmaFunctionExpressionLength(String function, int startIndex) {
        int num = 0;
        for (int i = startIndex; i < function.charAt(i); i++) {
            char ch = function.charAt(i);
            if (ch == '}') {
                return num;
            } else {
                num++;
            }
        }
        return num;
    }
}
