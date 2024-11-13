package com.example.SS2_Backend.util;

import java.util.Objects;

/**
 * String related util functions
 */
public class StringUtils {

    /**
     * check if str is empty or null
     *
     * @param str String
     * @return true if empty or null
     */
    public static boolean isEmptyOrNull(String str) {
        return Objects.isNull(str) || Objects.equals(str, "");
    }

    /**
     * as name
     *
     * @param character char to fill
     * @param length fill length
     * @return filled string
     */
    public static String fillWithChar(char character, int length) {
        String format = "%" + length + "s";
        return String.format(format, "").replace(' ', character);
    }

}
