package com.example.SS2_Backend.model.stableMatching;

import java.util.Arrays;
import static com.example.SS2_Backend.util.Utils.isDouble;
import static com.example.SS2_Backend.util.Utils.isInteger;

public class MatchingHelperFunctions {
    public static String[] decodeInputRequirement(String item) {
        item = item.trim();
        String[] result = new String[2];
        int index = findFirstNonNumericIndex(item);
        if (index == -1) {
            if (isInteger(item)) {
                try {
                    int a = Integer.parseInt(item);
                    result[0] = item;
                    if (a >= 0 && a <= 10) {
                        result[1] = null;
                    } else {
                        result[1] = "++";
                    }
                } catch (NumberFormatException e) {
                    System.out.println("error index - 1");
                    result[0] = "-1";
                    result[1] = "++";
                }
            } else if (isDouble(item)) {
                result[0] = "-2";
                result[1] = null;
            } else {
                result[0] = "-3";
                result[1] = null;
            }
        } else {
            if (item.contains(":")) {
                String[] parts = item.split(":");
                result[0] = parts[0].trim();
                result[1] = parts[1].trim();
            } else if (item.contains("++")) {
                String[] parts = item.split("\\+\\+");
                result[0] = parts[0].trim();
                result[1] = "++";
            } else if (item.contains("--")) {
                String[] parts = item.split("--");
                result[0] = parts[0].trim();
                result[1] = "--";
            } else {
                result[0] = "-2";
                result[1] = "++";
            }
        }
        return result;
    }

    public static void main(String[] args) {
        String inputReq = "200.011--";
        String[] requirement = decodeInputRequirement(inputReq);
        System.out.println(Arrays.toString(requirement));
    }

    private static int findFirstNonNumericIndex(String s) {
        s = s.trim();
        int index = 0;
        while (index < s.length() &&
                (Character.isDigit(s.charAt(index)) || s.charAt(index) == '.')) {
            index++;
        }
        if (index < s.length()) {
            return index;
        } else {
            return -1;
        }
    }


}
