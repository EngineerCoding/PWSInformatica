package com.ameling.parser.math;

/**
 * This interface will make communicating with variables bearable
 */
public interface IComponent {

    /**
     * Checks if the component has a variable
     * @return Whether the component has a variable
     */
    public boolean hasVariable();

    /**
     * Get the variables for this component
     * @return the variables of the component
     */
    public String[] getVariables();

    /**
     * Sets the variable value
     * @param variable Which variable it is
     * @param value The new value of said variable
     */
    public void setVariable(final String variable, final double value);

    /**
     * Gets the value for this component, when a variable is not set, 0 should be assumed
     * @return the value of this component
     */
    public double value();

}
