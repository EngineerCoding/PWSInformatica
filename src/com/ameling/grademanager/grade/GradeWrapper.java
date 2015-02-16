package com.ameling.grademanager.grade;


import com.ameling.parser.grade.Grade;

/**
 * A wrapper class for {@link Grade} so it can contain sub-expressions which are stored in a {@link CalculatorWrapper}. The converter of this class can be found in {@link
 * com.ameling.grademanager.grade.GradeConverter}.
 */
public class GradeWrapper extends Grade {

	/**
	 * The calculator with sub grades
	 */
	public CalculatorWrapper calculator;

	/**
	 * Setup super values
	 *
	 * @param name      The grade's id
	 * @param weighting The grade's weighting
	 */
	public GradeWrapper (final String name, final int weighting) {
		super(name, weighting);
	}

	/**
	 * Creates from the given grade a wrapper
	 * @param grade The grade with the weighting and name
	 */
	public GradeWrapper (final Grade grade) {
		this(grade.name, grade.weighting);
	}

	/**
	 * Sets the calculator as the calculator to use on how to calculate this grade. When the calculator
	 * has no sub-grades, the calculator is discarded.
	 *
	 * @param calculator The calculator to set
	 */
	public void setSubGrades (final CalculatorWrapper calculator) {
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
