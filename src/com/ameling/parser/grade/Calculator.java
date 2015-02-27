package com.ameling.parser.grade;

import com.ameling.parser.SyntaxException;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * This is the core of the app which calculates the grades. The weighting of all the grades is
 * used to calculate the average. This all is based on {@link com.ameling.parser.grade.Grade} objects.
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
	 * Calculates the given grade when the average is given. This method will find the {@link com.ameling.parser.grade.Grade}
	 * object for you and calls {@link #calculateGrade(com.ameling.parser.grade.Grade, double)}
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
	 * @param grade   The grade to calculate the value of
	 * @param average The average to achieve
	 * @return the grade's value
	 * @throws SyntaxException when grade is null
	 * @see com.ameling.parser.grade.Grade#setValue(double)
	 * @see com.ameling.parser.grade.Grade#reset()
	 */
	public double calculateGrade (final Grade grade, double average) {
		if (grade != null) {
			// Firstly we want to collect all grades which have a value set, along with their weighting in the average grade
			final List<Grade> setGrades = new ArrayList<>();
			int totalWeighting = grade.weighting;

			// Loop through all grades, add the grade when it is set to the list of setGrades and add the total weighting
			for (final Grade _grade : grades) {
				if (_grade.isSet) {
					setGrades.add(_grade);
					totalWeighting += _grade.weighting;
				}
			}

			// multiply the average we want, with the total weighting of set numbers
			average *= totalWeighting;
			for (final Grade _grade : setGrades) {
				// Now we subtract the total value with the total of a set grade (which is its value multiplied with its weighting)
				average -= (_grade.weighting * _grade.value);
			}
			// By dividing the leaving amount by its weighting, we get the value of the grade which it should be to achieve the average
			return average / grade.weighting;
		}

		throw new SyntaxException(FORMAT_INVALID_GRADE, STRING_UNKNOWN);
	}

	/**
	 * Gets the {@link com.ameling.parser.grade.Grade} object in {@link #grades} by id
	 *
	 * @param name The id of the grade
	 * @return The {@link com.ameling.parser.grade.Grade} object corresponding with the id
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
	 * @return The average of all set {@link com.ameling.parser.grade.Grade} objects
	 * @see com.ameling.parser.grade.Grade#isSet
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
