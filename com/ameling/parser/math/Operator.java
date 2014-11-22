package com.ameling.parser.math;

import com.ameling.parser.SyntaxException;
import com.ameling.parser.math.functions.Function;
import com.ameling.parser.math.functions.Root;

import java.util.ArrayList;
import java.util.List;

/**
 * This is an enumeration which holds all the operators the parser currently can parse
 */
public enum Operator {
    PLUS('+', 1, Function.add, Function.minus),
    MINUS('-', 1, Function.minus, Function.add),
    MULTIPLY('*', 0, Function.multiply, Function.divide),
    DIVIDE('/', 0, Function.divide, Function.multiply),
    POWER('^', 0, Function.power, Function.getFunction(Root.name));

    /**
     * The symbol which represents the operator
     */
    public final char character;

    /**
     * The priority this operator has. Can only be 0 or 1. <br/>
     * For instance, multiplying and dividing have a higher priority because that is what the rules are. For isntance:<br/>
     * <pre>5 + 2 * 3 = 11</pre>
     * <code>(5 + 2) * 3 = 21</code> -> <code>7 * 3 = 21</code><br/>
     * The order of operators and brackets are really important.
     */
    private final int priority;

    /**
     * The function that this operator represents; for simplicity's sake
     */
    private final Function function;

    /**
     *	A reversed function of the operator
     */
    public final Function reversedFunction;

    private Operator(final char character, final int priority, final Function function, final Function reversedFunction) {
        this.character = character;
        this.priority = priority;
        this.function = function;
        this.reversedFunction = reversedFunction;
    }

    /**
     * Calculates the value with this {@link Operator} object
     * @param number1 The first value
     * @param number2 The second value
     * @return <pre>number1 &lt;operator&gt; number2</pre>
     * @throws SyntaxException When an invalid operation is going on, such as dividing by 0. It is a {@link SyntaxException} because this is only used by this parser
     */
    public double calculate(final double number1, final double number2) throws SyntaxException {
        return function.calculate(new double[] { number1, number2 });
    }

    /**
     * Returns a boolean whether it is high priority or low priority.
     * @return True when it is high priority otherwise false
     */
    public boolean isHighPriority() {
        return priority == 0;
    }

    /**
     * Gets a list of operators with this priority
     * @param priority The priority the operator must be
     * @return a list of {@link Operator}s with the correct priority
     */
    private static Operator[] getPriority(final int priority) {
        final List<Operator> operators = new ArrayList<Operator>();
        for(final Operator operator : Operator.values()) {
            if(operator.priority == priority)
                operators.add(operator);
        }

        return operators.toArray(new Operator[operators.size()]);
    }

    /**
     * @return Gets a list of {@link Operator}s with priority 0
     * @see #getPriority(int)
     */
    public static Operator[] getHighPriority() {
        return getPriority(0);
    }

    /**
     * @return Gets a list of {@link Operator}s with priority 1
     * @see #getPriority(int)
     */
    public static Operator[] getLowPriority() {
        return getPriority(1);
    }

    /**
     * Gets the correct {@link Operator} for the character
     * @param character The character to get the {@link Operator} for
     * @return The {@link Operator} for this character
     * @see #character
     */
    public static Operator getOperator(char character) {
        for (Operator operator : Operator.values()) {
            if (character == operator.character)
                return operator;
        }
        return null;
    }
}
