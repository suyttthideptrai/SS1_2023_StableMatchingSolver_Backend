package com.example.SS2_Backend.util;


    import com.example.SS2_Backend.model.NormalPlayer;
    import com.example.SS2_Backend.model.Strategy;

    import java.math.BigDecimal;
    import java.math.RoundingMode;
    import java.text.DecimalFormat;
    import java.util.ArrayList;
    import java.util.Arrays;
    import java.util.List;
    import java.util.Objects;

public class StringExpressionEvaluator {


	public enum DefaultFunction {
		SUM,
		AVERAGE,
		MIN,
		MAX,
		PRODUCT,
		MEDIAN,
		RANGE
	}

	static DecimalFormat decimalFormat = new DecimalFormat("#.##############");

	public static BigDecimal evaluatePayoffFunctionWithRelativeToOtherPlayers(Strategy strategy,
								    String payoffFunction,
								    List<NormalPlayer> normalPlayers,
								    List<Integer> chosenStrategyIndices) {

		// this method is for some players who take other players' strategies into account when calculating their payoff
		String expression = payoffFunction;

		if (payoffFunction.isBlank()) {
			// the payoff function is the sum function of all properties by default
			return calculateByDefault(strategy.getProperties(), null);
		} else {

			// if there is no relationship in the payoff function, then just evaluate it normally, no need to replace any P placeholder
			if (!payoffFunction.contains("P")) {
				return evaluatePayoffFunctionNoRelative(strategy, expression);
			}

			// replace the placeholder for THIS current player's strategy with the actual value
			// example: payoffFunction is a string formula, e.g: p1 + p2 / p3 - P2p3 with p1, p2, p3 are the properties 1, 2, 3 of the strategy chosen by this player
			for (int i = 0; i < strategy.getProperties().size(); ++i) {
				double propertyValue = strategy.getProperties().get(i);
				String placeholder = String.format("\\bp%d\\b", i + 1);

				expression = expression.replaceAll(placeholder, formatDouble(propertyValue));
			}

			// replace the placeholder for OTHER players' strategies with the actual values
			// example: payoffFunction is a string formula, e.g: p1 + p2 / p3 - P2p3 with P2p3 is the property p of the strategy chosen by the player 2
			for (int i = 0; i < normalPlayers.size(); i++) {
				// example: P1
				NormalPlayer otherPlayer = normalPlayers.get(i);
				Strategy otherPlayerStrategy = otherPlayer.getStrategyAt(chosenStrategyIndices.get(i));

				for (int j = 0; j < otherPlayerStrategy.getProperties().size(); j++) {
					// example: P1p1
					String placeholder = String.format("\\bP%dp%d\\b", i + 1, j + 1);
					Double propertyValue = otherPlayerStrategy.getProperties().get(j);
					expression = expression.replaceAll(placeholder, formatDouble(propertyValue));
				}
			}

			// evaluate this string expression to get the result
			double val = eval(expression);
			return new BigDecimal(val).setScale(10, RoundingMode.HALF_UP);

		}


	}


	public static BigDecimal evaluatePayoffFunctionNoRelative(Strategy strategy,
						        String payoffFunction) {

		// this method is for some players only take their own strategies into account when calculating their payoff

		String expression = payoffFunction;

		if (payoffFunction.isBlank()) {
			// the payoff function is the sum function of all properties by default
			return calculateByDefault(strategy.getProperties(), null);
		} else {

			if (checkIfIsDefaultFunction(payoffFunction)) {
				return calculateByDefault(strategy.getProperties(), payoffFunction);
			}

			// replace the placeholder for THIS current player's strategy with the actual value
			// example: payoffFunction is a string formula, e.g: p1 + p2 / p3 - P2p3 with p1, p2, p3 are the properties 1, 2, 3 of the strategy chosen by this player
			for (int i = 0; i < strategy.getProperties().size(); ++i) {
				double propertyValue = strategy.getProperties().get(i);

				String placeholder = String.format("\\bp%d\\b", i + 1);

				expression = expression.replaceAll(placeholder, formatDouble(propertyValue));
			}


			// evaluate this string expression to get the result
			double val = eval(expression);
			return new BigDecimal(val).setScale(10, RoundingMode.HALF_UP);

		}

	}

	public static BigDecimal evaluateFitnessValue(double[] payoffs, String fitnessFunction) {
		String expression = fitnessFunction;
		List<Double> payoffList = new ArrayList<>();
		for (double payoff : payoffs) {
			payoffList.add(payoff);
		}

		if (fitnessFunction.isBlank()) {
			// if the fitnessFunction is absent,
			// the fitness value is the average of all payoffs of all chosen strategies by default
			return calculateByDefault(payoffList, null);
		} else {
			// replace placeholders for players' payoffs with the actual values

			if (checkIfIsDefaultFunction(fitnessFunction)) {
				return calculateByDefault(payoffList, fitnessFunction);
			}
			for (int i = 0; i < payoffs.length; i++) {
				double playerPayoff = payoffs[i];

				String placeholder = String.format("\\bu%d\\b", i + 1);
				expression = expression.replaceAll(placeholder, formatDouble(playerPayoff));
			}

			double val = eval(expression);
			return new BigDecimal(val).setScale(10, RoundingMode.HALF_UP);

		}

	}
	public static int AfterTokenLength(String function, int startIndex) {
		int length = 0;
		for (int c = startIndex + 1; c < function.length(); c++) {
			char ch = function.charAt(c);
			if (isNumericValue(ch)) {
				length++;
			} else {
				return length;
			}
		}
		return length;
	}

	public static boolean isNumericValue(char c) {
		return c >= '0' && c <= '9';
	}



	private static String formatDouble(double propertyValue) {
		// if the property value is too small it can be written as for example 1.0E-4, so we need to format it to 0.0001
		return decimalFormat.format(propertyValue);
	}


	private static boolean checkIfIsDefaultFunction(String function) {
		return Arrays.stream(DefaultFunction.values()).anyMatch(f -> f.name().equalsIgnoreCase(function));
	}

	private static double calSum(List<Double> values) {
		return values.stream()
		    .mapToDouble(Double::doubleValue)
		    .sum();
	}

	private static double calProduct(List<Double> values) {
		return values.stream()
		    .reduce(1.0, (a, b) -> a * b);
	}

	private static double calMax(List<Double> values) {
		return values.stream()
		    .mapToDouble(Double::doubleValue)
		    .max().getAsDouble();
	}

	private static double calMin(List<Double> values) {
		return values.stream()
		    .mapToDouble(Double::doubleValue)
		    .min().getAsDouble();
	}

	private static double calAverage(List<Double> values) {
		return values.stream()
		    .mapToDouble(Double::doubleValue)
		    .average().getAsDouble();
	}

	private static double calMedian(List<Double> values) {
		double[] arr = values.stream()
		    .mapToDouble(Double::doubleValue)
		    .sorted()
		    .toArray();
		int n = arr.length;
		if (n % 2 == 0) {
			return (arr[n / 2] + arr[n / 2 - 1]) / 2;
		} else {
			return arr[n / 2];
		}
	}

	private static double calRange(List<Double> values) {
		double[] arr = values.stream()
		    .mapToDouble(Double::doubleValue)
		    .sorted()
		    .toArray();
		return arr[arr.length - 1] - arr[0];
	}


	private static BigDecimal calculateByDefault(List<Double> values, String defaultFunction) {
		DefaultFunction function = DefaultFunction.valueOf(defaultFunction.toUpperCase());
		double val;
		switch (function) {
			case SUM:
				val = calSum(values);
				break;
			case PRODUCT:
				val = calProduct(values);
				break;
			case MAX:
				val = calMax(values);
				break;
			case MIN:
				val = calMin(values);
				break;
			case AVERAGE:
				val = calAverage(values);
				break;
			case MEDIAN:
				val = calMedian(values);
				break;
			case RANGE:
				val = calRange(values);
				break;
			default:
				val = calSum(values);
				break;
		}

		return new BigDecimal(val);

	}


	public static double eval(String strExpression) {
		System.out.println("Evaluating: ");
		System.out.println(strExpression);

		String formattedExpression = strExpression
		    .replaceAll("NaN", "0")// Replace NaN(Not A Number) with 0, so that the expression can be evaluated
		    .replaceAll("\\s+", "")// Removes all NBSP characters from the string (NBSP: matches one or more whitespace characters (including spaces, tabs, and newlines)
		    .replaceAll(",", "."); // Replace , to . (default double decimal separator)


		return new Object() {
			int pos = -1, ch;

			void nextChar() {
				ch = (++pos < formattedExpression.length()) ? formattedExpression.charAt(pos) : -1;
			}

			/*
			void nextChar() {
			    if (++pos < formattedExpression.length()) {
			        ch = formattedExpression.charAt(pos);
			    } else {
			        ch = -1;
			    }
			    }
			 */
			boolean eat(int charToEat) {
				//ignore white space
				while (ch == ' ') nextChar();
				//return true if ăn phải charToEat
				if (ch == charToEat) {
					nextChar();
					return true;
				}
				return false;
			}

			double parse() {
				nextChar();
				double x = parseExpression();

				if (pos < formattedExpression.length()) {
					System.out.println("wrong expression: " + formattedExpression);
					throw new RuntimeException("Unexpected: " + (char) ch);
				}
				return x;
			}

			// Grammar:
			// expression = term | expression `+` term | expression `-` term
			// term = factor | term `*` factor | term `/` factor
			// factor = `+` factor | `-` factor | `(` expression `)` | number
			//        | functionName `(` expression `)` | functionName factor
			//        | factor `^` factor

			double parseExpression() {
				double x = parseTerm();
				for (; ; ) {
					if (eat('+')) x += parseTerm(); // addition
					else if (eat('-')) x -= parseTerm(); // subtraction
					else return x;
				}
			}

			double parseTerm() {
				double x = parseFactor();
				for (; ; ) {
					if (eat('*')) x *= parseFactor(); // multiplication
					else if (eat('/')) x /= parseFactor(); // division
					else return x;
				}
			}

			double getArgForFunction(){
				double a;
				if (eat('(')) {
					if(eat('e') || eat('E')){
						a = Math.E;
					}else {
						a = parseExpression();
					}
					if (!eat(')'))
						throw new RuntimeException("Missing ')' after argument to log");
				}else{
					throw new RuntimeException("Incorrect arguments for log function");
				}
				return a;
			}

			double parseFactor() {
				if (eat('+')) return +parseFactor(); // unary plus
				if (eat('-')) return -parseFactor(); // unary minus

				double x;
				int startPos = this.pos;
				if (eat('(')) { // parentheses
					x = parseExpression();
					if (!eat(')')) {
						System.out.println("Missing ')'");
						System.out.println(formattedExpression);
						throw new RuntimeException("Missing ')'");
					}
				} else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
					while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
					x = Double.parseDouble(formattedExpression.substring(startPos, this.pos));
				} else if (ch >= 'a' && ch <= 'z') { // functions
					while (ch >= 'a' && ch <= 'z') nextChar();
					String func = formattedExpression.substring(startPos, this.pos);
					if(Objects.equals(func, "log")){
						double a = getArgForFunction();
						double b = getArgForFunction();
						return customLog(a, b);
					}
					if (eat('(')) {
						x = parseExpression();
						if (!eat(')'))
							throw new RuntimeException("Missing ')' after argument to " + func);
					} else {
						x = parseFactor();
					}
					switch (func) {
						case "abs":
							x = Math.abs(x);
							break;
						case "sqrt":
							x = Math.sqrt(x);
							break;
						case "sin":
							x = Math.sin(Math.toRadians(x));
							break;
						case "cos":
							x = Math.cos(Math.toRadians(x));
							break;
						case "tan":
							x = Math.tan(Math.toRadians(x));
							break;
						default:
							throw new RuntimeException("Unknown function: " + func);
					}
				} else {
					System.out.println("wrong expression: " + formattedExpression);
					throw new RuntimeException("Unexpected: " + (char) ch);
				}
				if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation
				return x;
			}
		}.parse();
	}
	private static double customLog(double base, double logNumber) {
		return Math.log(logNumber) / Math.log(base);
	}
}




