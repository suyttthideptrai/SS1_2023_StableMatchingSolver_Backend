package com.example.SS2_Backend.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Utils {
	public static boolean isInteger(String str) {
		try {
			// Attempt to parse the String as an integer
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			// The String is not a valid integer
			return false;
		}
	}

	public static boolean isDouble(String str) {
		if (!str.contains(",") || !str.contains(".")) {
			return false;
		}
		try {
			// Attempt to parse the String as an integer
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException e) {
			// The String is not a valid integer
			return false;
		}
	}

	public static Double formatDouble(double val) {
		DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
		DecimalFormat df = new DecimalFormat("#.##", symbols);
		String formattedValue = df.format(val);
		return Double.parseDouble(formattedValue);
	}
	public static String fillWithChar(char character, int width) {
		String format = "%" + width + "s";
		return String.format(format, "").replace(' ', character);
	}
}
