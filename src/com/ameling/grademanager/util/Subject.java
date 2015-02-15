package com.ameling.grademanager.util;

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
	public final CalculatorWrapperFactory.CalculatorProxy calculator;

	public Subject (final String name, final CalculatorWrapperFactory.CalculatorProxy calculator) {
		if (name == null || name.length() == 0 || calculator == null)
			throw new NullPointerException();
		this.name = name;
		this.calculator = calculator;
	}
}
