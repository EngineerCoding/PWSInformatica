package com.ameling;

import com.ameling.parser.math.MathExpression;

public class Main {
    // Testing it
    public static void main(String[] args) {
        final MathExpression expression = new MathExpression("(5*175)/3");

        expression.setVariable("ab", 9);
        expression.setVariable("a", 5);
        System.out.println(expression.value());

    }
}
