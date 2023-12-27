package com.example.SS2_Backend.util;


    import com.example.SS2_Backend.model.NormalPlayer;
    import com.example.SS2_Backend.model.StableMatching.Individual;
    import com.example.SS2_Backend.model.StableMatching.PreferenceList;
    import com.example.SS2_Backend.model.StableMatching.Requirement.Requirement;
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
			// the payoff function is the the sum function of all properties by default
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

	/*
	 * Stable Matching Calculation:
	 */

	public static PreferenceList getPreferenceListByFunction(List<Individual> Individuals, int index, String function) {
		PreferenceList a = new PreferenceList();
		int set = Individuals.get(index).getIndividualSet();
		int numberOfIndividual = Individuals.size();
		for (int i = 0; i < numberOfIndividual; i++) {
			if (Individuals.get(i).getIndividualSet() != set) {
				StringBuilder tmpSB = new StringBuilder();
				for (int c = 0; c < function.length(); c++) {
					char ch = function.charAt(c);
					if (ch == 'P' || ch == 'p') {
						// read next char then parse to int (index)
						int ssLength = AfterTokenLength(function, c);
						int indexOfP = Integer.parseInt(function.substring(c + 1, c + 1 + ssLength)) - 1;
						double PropertyValue = Individuals.get(i).getPropertyValue(indexOfP);
						Requirement requirement = Individuals.get(index).getRequirement(indexOfP);
						double Scale = getScale(requirement, PropertyValue);
						tmpSB.append(formatDouble(Scale));
						c += ssLength;
					} else if (ch == 'W' || ch == 'w') {
						//read next char then parse to int (index)
						int ssLength = AfterTokenLength(function, c);
						int indexOfW = Integer.parseInt(function.substring(c + 1, c + 1 + ssLength)) - 1;
						int weight = Individuals.get(index).getPropertyWeight(indexOfW);
						tmpSB.append(weight);
						c += ssLength;
					} else {
						//No occurrence of W/w/P/w
						tmpSB.append(ch);
					}
				}
				double totalScore = eval(tmpSB.toString());
				// Add
				a.add(new PreferenceList.IndexValue(i, totalScore));
			}
		}
		return a;
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


	public static PreferenceList getPreferenceListByDefault(List<Individual> Individuals, int index) {
		PreferenceList a = new PreferenceList();
		int set = Individuals.get(index).getIndividualSet();
		int numberOfIndividuals = Individuals.size();
		int numberOfProperties = Individuals.get(0).getNumberOfProperties();
		for (int i = 0; i < numberOfIndividuals; i++) {
			if (Individuals.get(i).getIndividualSet() != set) {
				double totalScore = 0;
				for (int j = 0; j < numberOfProperties; j++) {
					Double PropertyValue = Individuals.get(i).getPropertyValue(j);
					Requirement requirement = Individuals.get(index).getRequirement(j);
					int PropertyWeight = Individuals.get(index).getPropertyWeight(j);
					totalScore += getScale(requirement, PropertyValue) * PropertyWeight;
				}
				// Add
				a.add(new PreferenceList.IndexValue(i, totalScore));
			}
		}
		return a;
	}

	private static double getScale(Requirement requirement, double PropertyValue) {
		int type = requirement.getType();
		// Case: Scale
		if (type == 0) {
			int TargetValue = requirement.getTargetValue();
			if (PropertyValue < 0 || PropertyValue > 0) {
				return 0.0;
			} else {
				if (TargetValue != 0.0) {
					double Distance = Math.abs(PropertyValue - TargetValue);
					return (TargetValue - Distance) / TargetValue + 1;
				} else {
					return 0.0;
				}
			}
			//Case: 1 Bound
		} else if (type == 1) {
			Double Bound = requirement.getBound();
			String expression = requirement.getExpression();
			if (Objects.equals(expression, "++")) {
				if (PropertyValue < Bound) {
					return 0.0;
				} else {
					Double distance = Math.abs(PropertyValue - Bound);
					return (Bound + distance) / Bound;
				}
			} else {
				if (PropertyValue > Bound) {
					return 0.0;
				} else {
					Double distance = Math.abs(PropertyValue - Bound);
					return (Bound + distance) / Bound;
				}
			}
			//Case: 2 Bounds
		} else {
			Double lowerBound = requirement.getLowerBound();
			Double upperBound = requirement.getUpperBound();
			if (PropertyValue < lowerBound || PropertyValue > upperBound) {
				double medium = (lowerBound + upperBound) / 2;
				double distance = Math.abs(PropertyValue - medium);
				return (medium - distance) / medium + 1;
			}
		}
		return 0.0;
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

		String formattedExpression = strExpression.replaceAll("NaN", "0")// replace NaN with 0, so that the expression can be evaluated
		    .replaceAll("\\s+", "").replaceAll(",", "."); // Removes all NBSP characters from the string (NBSP: matches one or more whitespace characters (including spaces, tabs, and newlines)

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

	public static void main(String[] args) {
		System.out.println(eval("3*(2+5)+|-6|"));
	}

}




