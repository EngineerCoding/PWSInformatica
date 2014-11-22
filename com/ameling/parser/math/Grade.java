package com.ameling.parser.math;

/**
 * This class holds a single grade, with a weight. This weight is used to determine a proper grade-average and leads to a smoother algorithm.
 */
public final class Grade {

    /**
     * The name of this grade (in mathematical terms: a variable)
     */
    protected final String name;

    /**
     * The weight of this grade
     */
    protected final double weight;

    /**
     * This is the number which multiplies with this variable, is one when none is given.
     */
    protected final double multiplier;


    protected Grade(final String name, final double weight, final double multiplier) {
        this.name = name;
        this.weight = weight;
        this.multiplier = multiplier;
    }

}
