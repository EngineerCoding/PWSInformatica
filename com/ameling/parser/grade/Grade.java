package com.ameling.parser.grade;

/**
 * Holds a grade and the value of it. This is based on weighting, and this is the object the
 * parser will parse into.
 *
 * @author Wesley A
 */
public class Grade {

    /**
     * The name of this grade
     */
    public final String name;

    /**
     * The weighting of this grade
     */
    public final int weighting;

    /**
     * Creates a new grade with the name and weighting
     *
     * @param name      The name of this grade
     * @param weighting The weigthing of this grade
     */
    public Grade(final String name, final int weighting) {
        this.name = name;
        this.weighting = weighting;
    }

    /**
     * This value is used {@link Calculator}
     */
    protected double value;

    /**
     * A boolean flag whether it is set or not. Used in {@link #reset}, {@link #set(double)} and {@link Calculator}
     */
    protected boolean isSet = false;

    /**
     * Sets the grade value to this value.
     *
     * @param grade The value to set to
     */
    public void setGrade(final double grade) {
        value = grade;
        if (!isSet)
            isSet = true;
    }

    /**
     * Resets this grade's value (it is not set after calling this)
     */
    public void reset() {
        isSet = false;
    }
}
