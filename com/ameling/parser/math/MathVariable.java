package com.ameling.parser.math;

import com.ameling.parser.Constants;
import com.ameling.parser.Parser;
import com.ameling.parser.SyntaxException;
import com.ameling.parser.Tokenizer;
import com.ameling.parser.math.functions.Function;

import java.util.ArrayList;
import java.util.List;

public final class MathVariable extends Parser implements IComponent {

    /**
     * A holder class so the parent class only has to handle with an {@link IComponent}
     */
    private class Component implements IComponent {
        /**
         * The variable name
         */
        private final String variable;
        /**
         * The array that gets returned in {@link #getVariables()}
         */
        private final String[] variables;
        /**
         * The number the variable will multiply with. Also holds the number when this instance is created with {@link #Component(double)}
         */
        private final Double number;
        /**
         * The variable value, which standards to 1.0 to avoid issues with dividing (yet it can be set to 0.0)
         */
        private double value;

        /**
         * Creates a new component which simulates a IComponent
         * @param variable The variable name
         * @param number The value the variable multiplies with
         * @throws SyntaxException When both variable and number are null, no valid data has been parsed then
         */
        public Component(final String variable, final Double number) throws SyntaxException {
            this.variable = variable;
            this.number = number;

            variables = (variable != null ? new String[] { variable } : new String[0]);

            if(variable == null && number == null)
                throw new SyntaxException(unexpectedSymbolFormat, tokenizer.peek());
        }

        /**
         * Creates a new component which simulates as a {@link IComponent} and only holds a number
         * @param number The number to hold
         */
        public Component(final double number) {
            variable = null;
            variables = new String[0];
            this.number = number;
        }

        @Override
        public boolean hasVariable() {
            return variable != null;
        }

        @Override
        public String[] getVariables() {
            return variables;
        }

        @Override
        public void setVariable(final String variable, final double value) {
            if(hasVariable() && this.variable.equals(variable))
                this.value = value;
        }

        @Override
        public double value() {
            if(hasVariable())
                return (number != null ? value * number : value);
            return number;
        }
    }


    // Constants for this class
    private static final String firstCharMatch = "[a-zA-Z_]";
    private static final String variableMatch = "\\w";
    private static final String emptyString = "";
    // End constants

    /**
     * The component this class is
     */
    private final IComponent component;

    /**
     * Tries to parse either of the following:<br/>
     * <ul>
     * <li>A number</li>
     * <li>A variable</li>
     * <li>A number with a variable (for instance 5z)</li>
     * </ul>
     * @param tokenizer The tokenizer this component will use
     * @throws SyntaxException when nothing is parsed. (it was not there)
     */
    protected MathVariable(final Tokenizer tokenizer) throws SyntaxException {
        super(tokenizer);

        if(tokenizer.isNext(Constants.OpeningBracket)) {
            component = new MathExpression(tokenizer);
            if(!tokenizer.isNext(Constants.ClosingBracket))
                throw new SyntaxException(Constants.expectedCharFormat, Constants.ClosingBracket);
        } else {
            // Parse the number to multiply with
            final Double number = (Double) parseNumber(false);

            // Parse the variable name
            Character character;
            final StringBuilder sb = new StringBuilder();
            while((character = tokenizer.peek()) != null) {
                final String c = character.toString();
                if(sb.length() == 0 ? c.matches(firstCharMatch) : c.matches(variableMatch)) {
                    sb.append(tokenizer.pop());
                } else {
                    break;
                }
            }

            if(tokenizer.isNext(Constants.OpeningBracket) && sb.length() != 0) {
                final Function function = Function.getFunction(sb.toString());
                final List<IComponent> expressions = new ArrayList<IComponent>();
                expressions.add(new MathExpression(tokenizer));

                while(tokenizer.isNext(Constants.valueSeparator))
                    expressions.add(new MathExpression(tokenizer));

                if(!tokenizer.isNext(Constants.ClosingBracket))
                    throw new SyntaxException(Constants.expectedCharFormat, Constants.ClosingBracket);

                component = new MathFunction(function, expressions.toArray(new IComponent[expressions.size()]));
                return;
            }

            final String variable = sb.toString();
            component = new Component(emptyString.equals(variable) ? null : variable, number);
        }
    }

    /**
     * Constructor which is just a placeholder for an ordinary number
     * @param value The value it represents in the {@link #value()} method
     */
    public MathVariable(double value) {
        super(null);
        component = new Component(value);
    }

    @Override
    public boolean hasVariable() {
        return component.hasVariable();
    }

    @Override
    public String[] getVariables() {
        return component.getVariables();
    }

    @Override
    public void setVariable(final String variable, final double value) {
        component.setVariable(variable, value);
    }

    @Override
    public double value() {
        return component.value();
    }
}
