package com.ameling.parser.grade;

import com.ameling.parser.Parser;
import com.ameling.parser.SyntaxException;
import com.ameling.parser.Tokenizer;
import com.ameling.parser.grade.util.Fraction;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static com.ameling.parser.Constants.*;

public final class ExpressionParser extends Parser {

    // All constants used within this class only
    private static final char CHAR_BRACKET_CLOSE = ')';
    private static final char CHAR_BRACKET_OPEN = '(';
    private static final char CHAR_MULTIPLY = '*';
    private static final String REGEX_VARIABLE_STARTING = "[a-zA-Z_]";
    private static final String REGEX_VARIABLE_REST = "\\w";

    /**
     * The fraction which goes for a temporary weighting. When all Expression have the same denominator, this can be turned into a {@link com.ameling.parser.grade.Grade} object
     * via {@link #} TODO: Add JD
     */
    private Fraction weighting = new Fraction(1, 1);

    /**
     * The variable, such as SE1
     */
    private final String variable;

    /**
     * Sub expressions
     */
    public final ExpressionParser[] expressions;

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
        }

        this.expressions = (expressions.size() == 0 ? new ExpressionParser[0] : expressions.toArray(new ExpressionParser[expressions.size()]));

        if (expressions.size() == 0) {
            if (asteriskUsed)
                tokenizer.skipBlanks();

            // No brackets, so maybe a variable (eg. SE1
            final StringBuilder builder = new StringBuilder();
            Character character = tokenizer.peek();
            if (character != null && character.toString().matches(REGEX_VARIABLE_STARTING)) {
                builder.append(tokenizer.pop());
                while ((character = tokenizer.peek()) != null && character.toString().matches(REGEX_VARIABLE_REST))
                    builder.append(tokenizer.pop());
            }

            if(builder.length() == 0)
                ; // TODO: throw exception, need variable
            this.variable = builder.toString();
        } else {
            this.variable = null;
            if (multiplier == null)
                multiplier = parseNumber(false);

            if (multiplier != null) // Recheck because it can exist after previous check
                multiply(multiplier.intValue());

            if (tokenizer.isNext(CHAR_SLASH_FORWARD)) {// divide char
                final Double divider = parseNumber(false);
                if (divider != null)
                    divide(divider.intValue());
            }
        }
    }

    /**
     * Divides all sub-expressions
     *
     * @param n The value to divide with
     */
    protected void divide(final int n) {
        if (n != 0) {
            if (expressions.length != 0) {
                for (final ExpressionParser expression : expressions) {
                    System.out.println("Dividing " + expression.variable + " with " + n);
                    expression.weighting.divide(n);
                }
                //countFractions();
            } else {
                weighting.divide(n);
                System.out.println("Dividing this");
            }
        }

        for (final ExpressionParser expression : expressions) {
            System.out.println(expression.toString());
        }
    }

    /**
     * Multiplies all sub-expressions
     *
     * @param n The value to multiply with
     */
    protected void multiply(final int n) {
        if (n != 0) {
            if (expressions.length != 0) {
                for (final ExpressionParser expression : expressions)
                    expression.weighting.multiply(n);
                countFractions();
            } else {
                weighting.multiply(n);
            }
        }
    }

    /**
     * Counts all fractions of sub-expressions if they are present.
     */ //TODO: fix this method!@E!@RE!
    private void countFractions() {
        if (expressions.length != 0) {
            final Fraction start = expressions[0].weighting;
            for (int i = 1; i < expressions.length; i++)
                start.add(expressions[i].weighting);
            weighting = start.makeSmallest();
        }
    }

    @Override
    public String toString() {
        return variable + ":" + weighting.getNumerator() + " " + weighting.getDenominator();
    }

}