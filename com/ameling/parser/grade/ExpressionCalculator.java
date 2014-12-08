package com.ameling.parser.grade;

import com.ameling.parser.Parser;
import com.ameling.parser.SyntaxException;
import com.ameling.parser.Tokenizer;
import com.ameling.parser.grade.util.Fraction;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.ameling.parser.Constants.CHAR_PLUS;
import static com.ameling.parser.Constants.FORMAT_EXPECTED_CHAR;

/**
 * A {@link Calculator} object which is first parses an expression. All formulas of the PTA of Scala Molenwatering will work with this,
 * which is the primary goal.
 *
 * @author Wesley A
 */
public class ExpressionCalculator extends Calculator {

    /**
     * This class actually is the parsing part of {@link ExpressionCalculator}.
     *
     * @author Wesley A
     * @see ExpressionCalculator
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
         * The fraction which goes for a temporary weighting. When all Expression have the same denominator, this is turned into a {@link Grade} object
         */
        private Fraction weighting = new Fraction(1, 1);

        /**
         * The variable, such as SE1
         */
        private final String variable;

        /**
         * Sub expressions
         */
        private final Expression[] subExpressions;

        /**
         * Pares an expression with a {@link Tokenizer}
         * @param tokenizer The tokenizer which letters an expressio
         */
        private Expression(final Tokenizer tokenizer) {
            super(tokenizer);
            Double multiplier = parseNumber(false);
            boolean asteriskUsed = tokenizer.isNext(CHAR_MULTIPLY);

            final List<Expression> expressions = new ArrayList<Expression>();
            if (tokenizer.isNext(CHAR_BRACKET_OPEN)) {
                do {
                    expressions.add(new Expression(tokenizer));
                } while (tokenizer.isNext(CHAR_PLUS));

                if (!tokenizer.isNext(CHAR_BRACKET_CLOSE))
                    throw new SyntaxException(FORMAT_EXPECTED_CHAR, CHAR_BRACKET_CLOSE);

                if (multiplier == null)
                    multiplier = parseNumber(false);
            }

            this.subExpressions = (expressions.size() == 0 ? new Expression[0] : expressions.toArray(new Expression[expressions.size()]));

            if (expressions.size() == 0) {
                if (asteriskUsed)
                    tokenizer.skipBlanks();

                // No brackets, so maybe a variable (eg. SE1)
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

            if (multiplier != null)
                multiply(multiplier.intValue());

            if (tokenizer.isNext(CHAR_SLASH_FORWARD)) {// divide char
                final Double divider = parseNumber(false);
                if (divider != null)
                    divide(divider.intValue());
            }
        }

        /**
         * Divides all sub-expressions
         *
         * @param n The value to divide with
         */
        private void divide(final int n) {
            if (n != 0) {
                if (subExpressions.length != 0) {
                    for (final Expression expression : subExpressions)
                        expression.divide(n);
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
        private void multiply(final int n) {
            if (n != 0) {
                if (subExpressions.length != 0) {
                    for (final Expression expression : subExpressions)
                        expression.multiply(n);
                    countFractions();
                } else {
                    weighting.multiply(n);
                }
            }
        }

        /**
         * Counts all fractions of sub-expressions if they are present.
         */
        private void countFractions() {
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
    public ExpressionCalculator(final String expression) {
        this(new StringReader(expression));
    }

    /**
     * Creates a new instance of ExpressionCalculator and parses the expression.<br/>
     * This constructor is short for <pre>new ExpressionCalculator(new Tokenizer(reader));</pre>
     *
     * @param reader The reader to parse from
     */
    public ExpressionCalculator(final Reader reader) {
        this(new Tokenizer(reader));
    }

    /**
     * Parses an expression, and only calls {@link #getGrades(Tokenizer)} which does the parsing. It is some sort of wrapper.
     *
     * @param tokenizer The tokenizer which is the input of characters
     */
    protected ExpressionCalculator(final Tokenizer tokenizer) {
        super(getGrades(tokenizer));
    }

    /**
     * Collects the grades of a given expression
     * @param tokenizer The {@link Tokenizer} which letters an expression
     * @return The grades associated with the expression
     */
    private static Grade[] getGrades(final Tokenizer tokenizer) {
        final Expression[] gradeExpressions = findGradeExpressions(new Expression(tokenizer).subExpressions);
        final List<Integer> denominators = new ArrayList<Integer>();

        // collect the denominators
        for (final Expression grade : gradeExpressions) {
            if (!denominators.contains(grade.weighting.getDenominator()))
                denominators.add(grade.weighting.getDenominator());
        }

        final Grade[] grades = new Grade[gradeExpressions.length];

        for (int i = 0; i < gradeExpressions.length; i++) {
            final int denominator_backup = gradeExpressions[i].weighting.getDenominator();
            for (final Integer denominator : denominators) {
                if (denominator != denominator_backup) {
                    gradeExpressions[i].weighting.multiply(denominator);
                    gradeExpressions[i].weighting.divide(denominator);
                }
            }
            grades[i] = new Grade(gradeExpressions[i].variable, gradeExpressions[i].weighting.getNumerator());
        }
        return grades;
    }

    /**
     * Finds all the expressions which represent a grade (the variable field is not null)
     *
     * @param subs The list to look through, used for recursion
     * @return An array with expressions which represent a grade
     */
    private static Expression[] findGradeExpressions(final Expression[] subs) {
        final List<Expression> grades = new ArrayList<Expression>();
        for (final Expression expression : subs) {
            final int lengthSubExpression = expression.subExpressions.length;

            if (lengthSubExpression == 0) {
                grades.add(expression);
            } else {
                grades.addAll(Arrays.asList((lengthSubExpression == 1 ? findGradeExpressions(expression.subExpressions) : expression.subExpressions)));
            }
        }
        return grades.toArray(new Expression[grades.size()]);
    }

}
