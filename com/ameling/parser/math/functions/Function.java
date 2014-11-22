package com.ameling.parser.math.functions;

import com.ameling.parser.SyntaxException;

import java.util.ArrayList;
import java.util.List;

/**
 * This abstract class is the core of a function in a mathematical expression. This Function could link Root}, sin, cos or tan. When there is not
 * a default function included, one can register a custom function with {@link #registerFunction(Function)}.<br/>
 * {@link #getFunction(String)} is used internally but could be used to check if a function does or does not exist.
 */
public abstract class Function {

    // static part
    /**
     * Used internally for {@link com.ameling.parser.math.Operator}
     */
    public static final Function add = new Function(null, 2, 2) {
        @Override
        public double calculate(final double ... args) {
            return args[0] + args[1];
        }
    };

    /**
     * Used internally for {@link com.ameling.parser.math.Operator}
     */
    public static final Function minus = new Function(null, 2, 2) {
        @Override
        public double calculate(final double ... args) {
            return args[0] - args[1];
        }
    };

    /**
     * Used internally for {@link com.ameling.parser.math.Operator}
     */
    public static final Function multiply = new Function(null, 2, 2) {
        @Override
        public double calculate(final double ... args) {
            return args[0] * args[1];
        }
    };

    /**
     * Used internally for {@link com.ameling.parser.math.Operator}
     */
    public static final Function divide = new Function(null, 2, 2) {
        @Override
        public double calculate(final double ... args) {
            if(args[1] == 0)
                throw new SyntaxException("Cannot divide by 0");
            return args[0] / args[1];
        }
    };

    /**
     * Used internally for {@link com.ameling.parser.math.Operator}
     */
    public static final Function power = new Function(null, 2, 2) {
        @Override
        public double calculate(final double ... args) {
            return Math.pow(args[0], args[1]);
        }
    };

    // Register all the default functions
    static {
        functions = new ArrayList<Function>();
        registerFunction(new Root());
    }

    /**
     * List for the functions
     */
    private static final List<Function> functions;

    /**
     * Registers a function so the parser knows of the existence of this function
     * @param function The function to register
     */
    public static void registerFunction(final Function function) {
        if(function != null) {
            final String name = function.getName();
            if(name != null) {
                for(final Function _function : functions) {
                    if(name.equals(_function.getName())) {
                        return;
                    }
                }

                functions.add(function);
            }
        }
    }

    /**
     * Gets a Function by the name of it
     * @param name The name of the function
     * @return The Function of the name or null
     */
    public static Function getFunction(final String name) {
        if(name != null) {
            for (final Function function : functions) {
                if (name.equals(function.getName()))
                    return function;
            }
        }
        return null;
    }

    // normal class

    /**
     * The name of the function if it is set with the constructor
     */
    private final String function;

    /**
     * The minimal amount of arguments
     */
    private final int min;

    /**
     * The maximum amount of arguments
     */
    private final int max;

    /**
     * Calls {@link #Function(String, int, int)} with a minimum and maximum as 1
     * @param function The name of this function
     */
    public Function(final String function) {
        this(function, 1, 1);
    }

    /**
     * Sets the name of the function, the minimal and maximal amount of arguments
     * @param function The name of the function
     * @param min The minimal amount of arguments
     * @param max The maximal amount of arguments
     * @see #function
     * @see #min
     * @see #max
     */
    public Function(final String function, final int min, final int max) {
        this.function = function;
        this.min = min;
        this.max = max;
    }

    /**
     * Calculates the value of the given arguments
     * @param args The length of this array <code>length >= {@link #min} && length <= {@link #max}</code>
     * @return the calculated value
     */
    public abstract double calculate(final double ... args);

    /**
     * Returns the name of the function
     * @return The name of the function
     * @see #function
     */
    public String getName() {
        return function;
    }

    /**
     * Return the minimal amount of arguments
     * @return An integer containing the minimal amount of arguments
     * @see #min
     */
    public int getMin() {
        return min;
    }

    /**
     * Return the maximal amount of arguments
     * @return An integer containing the maximal amount of arguments
     * @see #max
     */
    public int getMax() {
        return max;
    }
}
