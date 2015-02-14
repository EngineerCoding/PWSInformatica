package com.ameling.grademanager.util;

import com.ameling.parser.grade.Calculator;

/**
 * A holder class which gets given at the main activity to expose all those subjects
 */
public class Subject {

	/**
	 * The id of the subject
	 */
	public final String name;

	/**
	 * The parsed formula of the subject
	 */
	public final Calculator calculator;

	/**
	 * An array containing all names of a subject
	 */
	private String[] names = null;

	public Subject (final String name, final Calculator calculator) {
		if (name == null || name.length() == 0 || calculator == null)
			throw new NullPointerException();
		this.name = name;
		this.calculator = calculator;
	}

	/**
	 * Creates a new array with grade names from {@link #calculator} or returns it from {@link #names}
	 *
	 * @return An array containing all grade names
	 */
	public String[] getGradeNames () {
		if (names == null) {
			names = new String[calculator.grades.size()];
			for (int i = 0; i < names.length; i++)
				names[i] = calculator.grades.get(i).name;
		}
		return names;
	}
}
