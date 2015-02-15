package com.ameling.grademanager.util;


import com.ameling.parser.grade.Grade;

/**
 * A wrapper class for the Grade Object. This is so because we want to have child expressions and the default parser Grade Object is not capable
 * of doing that.
 */
public class GradeWrapper extends Grade {

	/**
	 * The calculator with sub grades
	 */
	public CalculatorWrapperFactory.CalculatorProxy calculator;

	/**
	 * Setup super values
	 *
	 * @param name      The grade's id
	 * @param weighting The grade's weighting
	 */
	public GradeWrapper (final String name, final int weighting) {
		super(name, weighting);
	}

	public GradeWrapper (final Grade grade) {
		this(grade.name, grade.weighting);
	}

	/**
	 * Sets the calculator as the calculator to use on how to calculate this grade. When the calculator
	 * has no sub-grades, the calculator is discarded.
	 *
	 * @param calculator The calculator to set
	 */
	public void setSubGrades (final CalculatorWrapperFactory.CalculatorProxy calculator) {
		if (calculator != null && calculator.grades.size() > 0)
			this.calculator = calculator;
	}

	@Override
	public double getValue () {
		return calculator.calculateAverage();
	}

	@Override
	public void setValue (final double value) {
		// This method is not available since this is a wrapper
		throw new IllegalAccessError();
	}

	@Override
	public boolean hasValue () {
		if (calculator != null)
			for (final Grade grade : calculator.grades)
				if (grade.hasValue())
					return true;
		return false;
	}

}
