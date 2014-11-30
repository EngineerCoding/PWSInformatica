package com.ameling.parser.grade.weighting;

public class Grade {

    public final String name;
    public final int weighting;

    public Grade(final String name, final int weighting) {
        this.name = name;
        this.weighting = weighting;
    }

    protected double value;
    protected boolean isSet = false;

    public void setGrade(final double grade) {
        value = grade;
        if(!isSet)
            isSet = true;
    }

    public void reset() {
        isSet = false;
    }
}
