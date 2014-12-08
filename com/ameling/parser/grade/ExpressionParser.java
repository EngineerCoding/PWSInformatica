package com.ameling.parser.grade;

import com.ameling.parser.Parser;
import com.ameling.parser.SyntaxException;
import com.ameling.parser.Tokenizer;
import com.ameling.parser.grade.util.Fraction;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static com.ameling.parser.Constants.CHAR_PLUS;
import static com.ameling.parser.Constants.FORMAT_EXPECTED_CHAR;

public final class ExpressionParser extends Parser {

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
     * in {@link #getCalculator}
     */
    private Fraction weighting = new Fraction(1, 1);

    /**
     * The variable, such as SE1
     */
    private final String variable;

    /**
     * Sub expressions
     */
    private final ExpressionParser[] subExpressions;

    /**
     * The {@link Calculator} where all this parsing is about
     */
    public final Calculator gradeCalculator;

    /**
     * Creates a new instance of ExpressionParser and parses the expression.<br/>
     * This constructor is short for <pre>new ExpressionParser(new StringReader(expression));</pre>
     *
     * @param expression The string to parse
     */
    public ExpressionParser(final String expression) {
        this(new StringReader(expression));
    }

    /**
     * Creates a new instance of ExpressionParser and parses the expression.<br/>
     * This constructor is short for <pre>new ExpressionParser(new Tokenizer(reader));</pre>
     *
     * @param reader The reader to parse from
     */
    public ExpressionParser(final Reader reader) {
        this(new Tokenizer(reader));
    }

    /**
     * Parses an expression
     *
     * @param tokenizer The tokenizer which is the input of characters
     */
    protected ExpressionParser(final Tokenizer tokenizer) {
        super(tokenizer);

        Double multiplier = parseNumber(false);
        boolean asteriskUsed = tokenizer.isNext(CHAR_MULTIPLY);

        final List<ExpressionParser> expressions = new ArrayList<ExpressionParser>();
        if (tokenizer.isNext(CHAR_BRACKET_OPEN)) {
            do {
                expressions.add(new ExpressionParser(tokenizer));
            } while (tokenizer.isNext(CHAR_PLUS));

            if (!tokenizer.isNext(CHAR_BRACKET_CLOSE))
                throw new SyntaxException(FORMAT_EXPECTED_CHAR, CHAR_BRACKET_CLOSE);

            if (multiplier == null)
                multiplier = parseNumber(false);
        }

        this.subExpressions = (expressions.size() == 0 ? new ExpressionParser[0] : expressions.toArray(new ExpressionParser[expressions.size()]));

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

        gradeCalculator = getCalculator();
    }

    /**
     * Divides all sub-expressions
     *
     * @param n The value to divide with
     */
    private void divide(final int n) {
        if (n != 0) {
            if (subExpressions.length != 0) {
                for (final ExpressionParser expression : subExpressions)
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
                for (final ExpressionParser expression : subExpressions)
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

    /**
     * Creates a {@link Calculator} for this parser object.
     *
     * @return The {@link Calculator} for this parser object
     */
    private Calculator getCalculator() {
        final ExpressionParser[] gradeExpressions = findGradeExpressions(subExpressions);
        final List<Integer> denominators = new ArrayList<Integer>();

        // collect the denominators
        for (final ExpressionParser grade : gradeExpressions) {
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

        return new Calculator(grades);
    }

    /**
     * Finds all the expressions which represent a grade (the variable field is not null)
     *
     * @param subs The list to look through, used for recursion
     * @return An array with expressions which represent a grade
     */
    private static ExpressionParser[] findGradeExpressions(final ExpressionParser[] subs) {
        final List<ExpressionParser> grades = new ArrayList<ExpressionParser>();
        for (final ExpressionParser expression : subs) {
            final int lengthSubExpression = expression.subExpressions.length;

            if (lengthSubExpression == 0) {
                grades.add(expression);
            } else {
                for (final ExpressionParser exp : (lengthSubExpression == 1 ? findGradeExpressions(expression.subExpressions) : expression.subExpressions))
                    grades.add(exp);
            }
        }
        return grades.toArray(new ExpressionParser[grades.size()]);
    }
}
