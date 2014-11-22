package com.ameling.parser.math;

import com.ameling.parser.Constants;
import com.ameling.parser.SyntaxException;
import com.ameling.parser.Tokenizer;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the class that can parse a full valid mathematical expression. When variables are found in this expression and they are not set, those
 * variables will have a standard value of 0. This can have negative impact on the result of the expression, so keep it in mind.
 */
public final class MathExpression implements IComponent {

    /**
     * The components that are used in {@link #value}
     */
    private final IComponent[] components;
    /**
     * The {@link Operator}s that are used in {@link #value}
     */
    private final Operator[] operators;
    /**
     * The variables
     */
    private final String[] variables;

    /**
     * With this expression a new {@link StringReader} gets created and calls {@link #MathExpression(Reader)}
     * @param expression The string that is a valid expression
     */
    public MathExpression(final String expression) {
        this(new StringReader(expression));
    }

    /**
     * With this reader a new {@link Tokenizer} gets created and calls {@link #MathExpression(Tokenizer)}
     * @param reader The reader that reads a valid expression
     */
    public MathExpression(final Reader reader) {
        this(new Tokenizer(reader));
    }

    /**
     * Parses the expression with a {@link Tokenizer}
     * @param tokenizer The tokenizer that reads a valid expression
     */
    public MathExpression(final Tokenizer tokenizer) {
        this(tokenizer, Operator.getLowPriority());
    }

    /**
     * Constructor for internal use only. It will parse the tokenizer with given {@link Tokenizer}<br/>
     * It is always called for the first time with {@link Operator#getLowPriority}
     * @param tokenizer The tokenizer which reads a valid expression
     * @param handlingOperators The operators this will handle
     */
    private MathExpression(final Tokenizer tokenizer, final Operator[] handlingOperators) {
        tokenizer.skipBlanks();
        boolean variableFound = false;
        IComponent variable = (handlingOperators[0].isHighPriority() ? new MathVariable(tokenizer) : new MathExpression(tokenizer, Operator.getHighPriority()));

        tokenizer.skipBlanks();
        Character character = tokenizer.peek();
        if(character != null) { // When the character is null, then there is no operator present
            final List<IComponent> components = new ArrayList<IComponent>();
            final List<Operator> operators = new ArrayList<Operator>();

            while (character != null) {
                // Check if the next operator is an operator in the Operator array (handlingOperators)
                boolean validOperator = false;
                final Operator operator = Operator.getOperator(character);
                if(operator != null) {
                    for(final Operator op : handlingOperators) {
                        if(op == operator) {
                            validOperator = true;
                            break;
                        }
                    }
                }

                if(validOperator) {
                    // It is a valid operator so we can continue our parsing.
                    tokenizer.pop();
                    final IComponent next = (operator.isHighPriority() ? new MathVariable(tokenizer) : new MathExpression(tokenizer, Operator.getHighPriority()));
                    if (!variable.hasVariable() && !next.hasVariable() && !variableFound) {
                        // The variables do not have any variables, so create a new object on the first variable, since the outcome is just a number
                        variable = new MathVariable(operator.calculate(variable.value(), next.value()));
                    } else {
                        if (!variableFound) {
                            // Variable is found, so add the variable to list, to avoid issues
                            components.add(variable);
                            variableFound = true;
                        }

                        components.add(next);
                        operators.add(Operator.getOperator(character));
                    }

                    tokenizer.skipBlanks();
                    character = tokenizer.peek();
                } else {
                    // if the operator is an invalid operator and the character is not a bracket, then error
                    if(operator == null && !(character == Constants.OpeningBracket || character == Constants.ClosingBracket))
                        throw new SyntaxException(Constants.unexpectedCharFormat, character);
                    break;
                }
            }

            this.components = (components.size() == 0 ? new IComponent[] {variable} : components.toArray(new IComponent[components.size()]));
            this.operators = operators.toArray(new Operator[operators.size()]);

            final List<String> variables = new ArrayList<String>();
            for(final IComponent component : this.components) {
                final String[] vars = component.getVariables();
                for(final String var : vars) {
                    if(!variables.contains(var))
                        variables.add(var);
                }
            }
            this.variables = variables.toArray(new String[variables.size()]);
            return;
        }

        this.components = new IComponent[] {variable};
        this.operators = new Operator[0];
        this.variables = variable.getVariables();
    }

    @Override
    public boolean hasVariable() {
        return variables.length > 0;
    }

    @Override
    public String[] getVariables() {
        return variables;
    }

    @Override
    public void setVariable(final String variable, final double value) {
        if(variable != null) {
            for (final IComponent component : components) {
                if (component.hasVariable()) {
                    // Simply set the variable, if the component contains the variable, it will set that variable.
                    // When not available, it should do nothing
                    component.setVariable(variable, value);
                    // Don't break because multiple components can contain the same variable
                }
            }
        }
    }

    @Override
    public double value() {
        // The value of the first component (always present, otherwise a SyntaxException is throw in the constructor
        double result = components[0].value();
        // When multiple components are present, calculate them with the Operator
        // The operator is the index one less than the next component
        for(int i = 1; i < components.length; i++)
            result = operators[i - 1].calculate(result, components[i].value());
        return result;
    }
}
