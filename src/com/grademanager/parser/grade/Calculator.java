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

package com.grademanager.parser.grade;

import com.grademanager.parser.SyntaxException;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * This is the core of the app which calculates the grades. The weighting of all the grades is
 * used to calculate the average. This all is based on {@link com.grademanager.parser.grade.Grade} objects.
 *
 * @author Wesley A
 */
public class Calculator implements Cloneable {

	// Constants which are used within this class only
	private static final String FORMAT_INVALID_GRADE = "Invalid grade id '%s'";

	private static final String STRING_UNKNOWN = "unknown";

	/**
	 * The grades this object uses and knows. This is an immutable list
	 */
	public final List<Grade> grades;

	/**
	 * Creates a new object using the given grades
	 *
	 * @param grades The grades to use
	 */
	public Calculator (final Grade[] grades) {
		this.grades = asList(grades);
	}

	/**
	 * Calculates the given grade when the average is given. This method will find the {@link com.grademanager.parser.grade.Grade}
	 * object for you and calls {@link #calculateGrade(com.grademanager.parser.grade.Grade, double)}
	 *
	 * @param name    The id of the grade to calculate the value of
	 * @param average What the average should be
	 * @return the given grade's value to achieve the average
	 */
	public double calculateGrade (final String name, final double average) {
		final Grade grade = getGrade(name);
		if (grade != null)
			return calculateGrade(grade, average);

		throw new SyntaxException(FORMAT_INVALID_GRADE, name);
	}

	/**
	 * Calculates the grade object's value to get the given average. Takes into account for other
	 * set grades.
	 *
	 * @param gradeToCalculate The grade to calculate the value of
	 * @param average          The average to achieve
	 * @return the grade's value
	 * @throws SyntaxException when grade is null
	 * @see com.grademanager.parser.grade.Grade#setValue(double)
	 * @see com.grademanager.parser.grade.Grade#reset()
	 */
	public double calculateGrade (final Grade gradeToCalculate, double average) {
		if (gradeToCalculate != null) {
			// Firstly we want to collect all grades which have a value set, along with their weighting in the average grade
			final List<Grade> collectedGrades = new ArrayList<>();

			// Loop through all grades, add the grade when it is set to the list of setGrades and add the total weighting
			int totalWeighting = gradeToCalculate.weighting;
			for (final Grade grade : grades) {
				if (grade.hasValue() && grade != gradeToCalculate) {
					totalWeighting += grade.weighting;
					collectedGrades.add(grade);
				}
			}

			// multiply the average we want, with the total weighting of set numbers
			average *= totalWeighting;
			for (final Grade grade : collectedGrades)
				average -= (grade.getValue() * grade.weighting); // Now we subtract the total value with the total of a set grade (which is its value multiplied with its weighting)
			return average / gradeToCalculate.weighting;
		}
		// By dividing the leaving amount by its weighting, we get the value of the grade which it should be to achieve the average
		throw new SyntaxException(FORMAT_INVALID_GRADE, STRING_UNKNOWN);
	}

	/**
	 * Gets the {@link com.grademanager.parser.grade.Grade} object in {@link #grades} by id
	 *
	 * @param name The id of the grade
	 * @return The {@link com.grademanager.parser.grade.Grade} object corresponding with the id
	 */
	public Grade getGrade (final String name) {
		if (name != null) {
			for (final Grade grade : grades)
				if (name.equals(grade.name))
					return grade;
		}
		return null;
	}

	/**
	 * Calculates the average of all set grades
	 *
	 * @return The average of all set {@link com.grademanager.parser.grade.Grade} objects
	 * @see com.grademanager.parser.grade.Grade#isSet
	 */
	public double calculateAverage () {
		double total = 0.0D; // The total of all grades
		int totalWeighting = 0; // Total weighting

		for (final Grade grade : grades) {
			if (grade.hasValue()) { // when the grade is set, it is valid
				totalWeighting += grade.weighting; // add to the weighting
				total += grade.getValue() * grade.weighting; // add the grade times the weighting (otherwise you get odd values)
			}
		}

		if (totalWeighting != 0) // If the totalWeighting is 0, that means that there are no value found
			return total / totalWeighting;
		return 0.0D;
	}

	@Override
	public Calculator clone () {
		final Grade[] grades = new Grade[this.grades.size()];
		for (int i = 0; i < grades.length; i++)
			grades[i] = this.grades.get(i).clone();
		return new Calculator(grades);
	}

}
