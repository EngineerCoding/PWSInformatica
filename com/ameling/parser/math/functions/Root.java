package com.ameling.parser.math.functions;

/**
 * One can calculate the root of a number<br/>
 * With one argument it calculates the square root of a number, with two  arguments it calculates the root with the second number
 */
public class Root extends Function {

    /**
     * The name of the function
     */
    public static final String name = "root";

    protected Root() {
        super(name, 1, 2);
    }

    @Override
    public double calculate(final double ... args) {
        if(args.length == 1)
            return calculate(new double[] {args[0], 2});
        return Math.pow(args[0], 1 / args[1]);
    }

}
