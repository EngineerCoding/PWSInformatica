package com.grademanager.parser.grade;

import com.grademanager.parser.Parser;
import com.grademanager.parser.SyntaxException;
import com.grademanager.parser.Tokenizer;
import com.grademanager.parser.grade.util.Fraction;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static com.grademanager.parser.Constants.CHAR_PLUS;
import static com.grademanager.parser.Constants.FORMAT_EXPECTED_CHAR;

/**
 * A {@link com.grademanager.parser.grade.Calculator} object which is first parses an expression. All formulas of the PTA of Scala Molenwatering will work with this,
 * which is the primary goal.<br/>
 * When an expression is given, it always looks like this:
 * <pre>(SE1 + SE2)/2</pre>
 * The names do not have to be the same, nor the denominator. For each variable (i.e. SE1) a {@link com.grademanager.parser.grade.util.Fraction} is calculated. When of all the Fractions of these variables are added
 * all up together, and it is not a total of 1 (the static field {@link #FRACTION_1} it will throw a {@link SyntaxException}
 *
 * @author Wesley A
 */
public class ExpressionCalculator extends Calculator {

	private static final Fraction FRACTION_1 = new Fraction(1, 1);

	private static final String EXCEPTION_INVALID_EXPRESSION = "This is an invalid average expression!";

	/**
	 * This class actually is the parsing part of {@link com.grademanager.parser.grade.ExpressionCalculator}. How this is done, please refer to {@link com.grademanager.parser.grade.ExpressionCalculator}
	 *
	 * @author Wesley A
	 * @see com.grademanager.parser.grade.ExpressionCalculator
	 */
	private static class Expression extends Parser {

		// All constants used within this class only
		private static final char CHAR_BRACKET_CLOSE = ')';

		private static final char CHAR_BRACKET_OPEN = '(';

		private static final char CHAR_MULTIPLY = '*';

		private static final char CHAR_SLASH_FORWARD = '/';

		private static final String EXCEPTION_NEED_VARIABLE = "A variable is needed here!";

		private static final String REGEX_VARIABLE_STARTING = "[a-zA-Z_]";

		private static final String REGEX_VARIABLE_REST = "\\w";

		/**
		 * The fraction which goes for a temporary weighting. When all Expression's have the same denominator, this is turned into a {@link com.grademanager.parser.grade.Grade} object<br/>
		 * This starts as the value of {@link #FRACTION_1}
		 */
		private Fraction weighting = FRACTION_1.clone();

		/**
		 * The variable, such as SE1
		 */
		private final String variable;

		/**
		 * Sub expressions
		 */
		private final Expression[] subExpressions;

		/**
		 * Pares an expression with a {@link Tokenizer}. An Expression looks like the following:
		 * <pre>[NUMBER] Expression </pre>
		 *
		 * @param tokenizer The tokenizer which letters an expression
		 */
		private Expression (final Tokenizer tokenizer) {
			super(tokenizer); // required for the parent class, Parser

			// First try to parse a number
			Double multiplier = parseNumber(false);
			boolean usedAsterisk = false;
			if (multiplier != null) {
				final Object[] multiplyObject = parseMultipliers();
				if ((Boolean) multiplyObject[0])
					multiplier *= (Double) multiplyObject[1];
				usedAsterisk = (Boolean) multiplyObject[2];
			}

			final List<Expression> expressions = new ArrayList<>();
			if (tokenizer.isNext(CHAR_BRACKET_OPEN)) {
				do {
					expressions.add(new Expression(tokenizer));
				} while (tokenizer.isNext(CHAR_PLUS));
				if (!tokenizer.isNext(CHAR_BRACKET_CLOSE))
					throw new SyntaxException(FORMAT_EXPECTED_CHAR, CHAR_BRACKET_CLOSE);
			}

			if (expressions.size() == 0) {
				if (usedAsterisk)
					tokenizer.skipBlanks();

				final StringBuilder builder = new StringBuilder();
				Character character = tokenizer.peek();
				if (character != null && character.toString().matches(REGEX_VARIABLE_STARTING)) {
					builder.append(tokenizer.pop());
					while ((character = tokenizer.peek()) != null && character.toString().matches(REGEX_VARIABLE_REST))
						builder.append(tokenizer.pop());
				}

				if (builder.length() == 0)
					throw new SyntaxException(EXCEPTION_NEED_VARIABLE);
				this.variable = builder.toString();
			} else {
				this.variable = null;
			}

			// Try to parse multipliers again
			final Object[] multiplyObject = parseMultipliers();
			if ((Boolean) multiplyObject[0]) {
				if (multiplier != null) {
					multiplier *= (Double) multiplyObject[1];
				} else {
					multiplier = (Double) multiplyObject[1];
				}
			}

			if ((Boolean) multiplyObject[2])
				throw new SyntaxException("Expected number!");

			// Set the found expressions in a proper immutable list (arrays are mutable)
			this.subExpressions = (expressions.size() == 0 ? new Expression[0] : expressions.toArray(new Expression[expressions.size()]));

			// multiply all sub expressions with the multiplier, if available
			if (multiplier != null)
				multiply(multiplier.intValue());

			// Check for a division, also divide all the sub expressions with that number
			if (tokenizer.isNext(CHAR_SLASH_FORWARD)) {// divide char
				final Double divider = parseNumber(false);
				if (divider != null)
					divide(divider.intValue());
			}
		}

		/**
		 * Parses a number which to multiply with
		 *
		 * @return An array containing the following information (index-1):
		 * <ol>
		 * <li>Boolean: whether to use the next index</li>
		 * <li>Double: the parsed number, defaults to 1D</li>
		 * <li>Boolean: whether a number couldn't be parsed</li>
		 * </ol>
		 */
		private Object[] parseMultipliers () {
			Double number = 1D;
			boolean changed = false;
			while (tokenizer.isNext(CHAR_MULTIPLY)) {
				final Double multiplyNumber = parseNumber(false);
				if (multiplyNumber != null) {
					number *= multiplyNumber;
				} else {
					return new Object[]{ changed, number, true };
				}

				if (!changed)
					changed = true;
			}
			return new Object[]{ changed, number, false };
		}

		/**
		 * Divides all sub-expressions
		 *
		 * @param n The value to divide with
		 */
		private void divide (final int n) {
			if (n != 0) {
				if (subExpressions.length != 0) {
					for (final Expression expression : subExpressions)
						expression.divide(n); // The sub expression can contain sub expressions too, so call the method instead of direct division like in the else clause
					countFractions();
				} else {
					weighting.divide(n);
				}
			}
		}

		/**
		 * Multiplies all sub-expressions
		 *
		 * @param n The value to multiply with
		 */
		private void multiply (final int n) {
			if (n != 0) {
				if (subExpressions.length != 0) {
					for (final Expression expression : subExpressions)
						expression.multiply(n); // The sub expression can contain sub expressions too, so call the method instead of direct multiplication like in the else clause
					countFractions();
				} else {
					weighting.multiply(n);
				}
			}
		}

		/**
		 * Counts all fractions of sub-expressions if they are present.
		 */
		private void countFractions () {
			if (subExpressions.length != 0) {
				final Fraction start = subExpressions[0].weighting.clone();
				for (int i = 1; i < subExpressions.length; i++)
					start.add(subExpressions[i].weighting);
				weighting = start.makeSmallest();
			}
		}
	}

	/**
	 * Creates a new instance of ExpressionCalculator and parses the expression.<br/>
	 * This constructor is short for <pre>new ExpressionCalculator(new StringReader(expression));</pre>
	 *
	 * @param expression The string to parse
	 */
	public ExpressionCalculator (final String expression) {
		this(new StringReader(expression));
	}

	/**
	 * Creates a new instance of ExpressionCalculator and parses the expression.<br/>
	 * This constructor is short for <pre>new ExpressionCalculator(new Tokenizer(reader));</pre>
	 *
	 * @param reader The reader to parse from
	 */
	public ExpressionCalculator (final Reader reader) {
		this(new Tokenizer(reader));
	}

	/**
	 * Parses an expression, and only calls {@link #getGrades(Tokenizer)} which does the parsing. It is some sort of wrapper.
	 *
	 * @param tokenizer The tokenizer which is the input of characters
	 */
	public ExpressionCalculator (final Tokenizer tokenizer) {
		super(getGrades(tokenizer));
	}

	/**
	 * Collects the grades of a given expression
	 *
	 * @param tokenizer The {@link Tokenizer} which letters an expression
	 * @return The grades associated with the expression
	 * @throws SyntaxException When the total weighting is not 1
	 */
	private static Grade[] getGrades (final Tokenizer tokenizer) {
		final Expression parentExpression = new Expression(tokenizer);
		parentExpression.countFractions();

		// If the parentExpression is not the Fraction 1/1, then the expression is not valid for an average
		if (parentExpression.weighting.equals(FRACTION_1)) {
			final List<Expression> gradeExpressions = findGradeExpressions(new Expression[]{ parentExpression }); // find all expressions which have a variable
			final List<Integer> denominators = new ArrayList<>();

			// collect the denominators of Expression.weighting
			for (final Expression grade : gradeExpressions) {
				if (!denominators.contains(grade.weighting.getDenominator()))
					denominators.add(grade.weighting.getDenominator());
			}

			// Create an empty array of Grade objects
			final Grade[] grades = new Grade[gradeExpressions.size()];

			// Fill the grades array
			for (final Expression gradeExpression : gradeExpressions) {
				// Multiply the denominator of this Grade with all other denominators except it's own (all grade objects must have the same denominator)
				final int denominator_backup = gradeExpression.weighting.getDenominator();
				for (final Integer denominator : denominators) {
					if (denominator != denominator_backup) {
						gradeExpression.weighting.multiply(denominator);
						gradeExpression.weighting.divide(denominator);
					}
				}
				// Create the Grade object for the given expression
				grades[gradeExpressions.indexOf(gradeExpression)] = new Grade(gradeExpression.variable, gradeExpression.weighting.getNumerator());
			}
			return grades;
		}

		throw new SyntaxException(EXCEPTION_INVALID_EXPRESSION);
	}

	/**
	 * Finds all the expressions which represent a grade ({@link com.grademanager.parser.grade.ExpressionCalculator.Expression#variable} is not null)
	 *
	 * @param subs The list to look through, used for recursion
	 * @return An array with expressions which represent a grade
	 */
	private static List<Expression> findGradeExpressions (final Expression[] subs) {
		final List<Expression> gradeExpressions = new ArrayList<>();
		for (final Expression expression : subs) {
			if (expression.subExpressions.length == 0) {
				gradeExpressions.add(expression);
			} else {
				gradeExpressions.addAll(findGradeExpressions(expression.subExpressions));
			}
		}
		return gradeExpressions;
	}

}
