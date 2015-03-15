/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Wesley "EngineerCoding" Ameling
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.grademanager.app.grade;


import com.grademanager.parser.grade.Grade;

import java.util.ArrayList;
import java.util.List;

/**
 * A wrapper class for {@link Grade} so it can contain sub-expressions which are stored in a {@link CalculatorWrapper}. The converter of this class can be found in {@link
 * com.grademanager.app.grade.GradeConverter}.<br/>
 */
public class GradeWrapper extends Grade {

	/**
	 * The calculator with sub grades
	 */
	public CalculatorWrapper calculator;

	/**
	 * An array of all {@link Grade} objects in the {@link #calculator}
	 */
	private List<Grade> subGrades = new ArrayList<>();

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
	 *
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
		if (calculator != null && calculator.grades.size() > 0) {
			this.calculator = calculator;

			for (final Grade grade : calculator.grades) {
				if (grade instanceof GradeWrapper) {
					for (final Grade subGrade : ((GradeWrapper) grade).getChildren())
						subGrades.add(subGrade);
				} else {
					subGrades.add(grade);
				}
			}
		}
	}

	/**
	 * Checks if this is a parent wrapper
	 *
	 * @param grade The grade to check for
	 * @return A boolean whether it exists or not
	 */
	public boolean hasGrade (final Grade grade) {
		if (calculator != null) {
			for (final Grade _grade : calculator.grades) {
				if (grade instanceof GradeWrapper && ((GradeWrapper) _grade).hasGrade(grade))
					return true;
				else if (_grade.equals(grade))
					return true;
			}
		}
		return false;
	}

	@Override
	public double getValue () {
		if (hasValue())
			return calculator.calculateAverage();
		return 0D;
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

	@Override
	public void reset () {
		calculator = null;
	}

	/**
	 * Retrieves all {@link Grade} objects from the {@link #calculator}
	 *
	 * @return All sub grades
	 */
	public List<Grade> getChildren () {
		return subGrades;
	}

	@Override
	public Grade clone () {
		final GradeWrapper wrapper = new GradeWrapper(this);
		wrapper.setSubGrades(calculator.clone());
		return wrapper;
	}
}
