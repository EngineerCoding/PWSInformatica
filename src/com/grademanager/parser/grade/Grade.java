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

/**
 * Holds a grade and the value of it. This is based on weighting, and this is the object the
 * parser will parse into.
 *
 * @author Wesley A
 */
public class Grade implements Cloneable {

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
	 * @param weighting The weighting of this grade
	 */
	public Grade (final String name, final int weighting) {
		this.name = name;
		this.weighting = weighting;
	}

	/**
	 * This value is used {@link Calculator}
	 */
	protected double value;

	/**
	 * A boolean flag whether it is set or not. Used in {@link #reset}, {@link #setValue(double)} and {@link Calculator}
	 */
	protected boolean isSet = false;

	/**
	 * Sets the grade value to this value.
	 *
	 * @param grade The value to set to
	 */
	public void setValue (final double grade) {
		value = grade;
		if (!isSet)
			isSet = true;
	}

	/**
	 * Returns the current grade value
	 *
	 * @return The grade value or 0 when it is not set. To check properly use {@link #hasValue}
	 */
	public double getValue () {
		return value;
	}

	/**
	 * Returns if the {@link #value} field is set through {@link #setValue(double)}
	 *
	 * @return If this object has a grade value
	 */
	public boolean hasValue () {
		return isSet;
	}

	/**
	 * Resets this grade's value (it is not set after calling this)
	 */
	public void reset () {
		isSet = false;
	}

	@Override
	public Grade clone () {
		final Grade grade = new Grade(name, weighting);
		if (hasValue())
			grade.setValue(getValue());
		return grade;
	}
}
