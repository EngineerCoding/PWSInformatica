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

package com.grademanager.parser.grade.util;

/**
 * This class represents a mathematical fraction (only with integers)
 *
 * @author Wesley A
 */
public final class Fraction implements Cloneable {

	/**
	 * The numerator of this fraction
	 */
	private int numerator;

	/**
	 * The denominator of this fraction
	 */
	private int denominator;

	public Fraction (final int numerator, final int denominator) {
		if (denominator == 0)
			throw new ArithmeticException("Cannot divide by 0");
		this.numerator = numerator;
		this.denominator = denominator;
		makeSmallest();
	}

	/**
	 * Returns the numerator, this is done through this method because it is not a final variable
	 *
	 * @return The numerator of this fraction
	 */
	public int getNumerator () {
		return numerator;
	}

	/**
	 * Returns the numerator, this is done through this method because it is not a final variable
	 *
	 * @return The denominator of this fraction
	 */
	public int getDenominator () {
		return denominator;
	}

	/**
	 * Multiplies this fraction with given number. Only multiplies with the numerator
	 *
	 * @param n The value to multiply with
	 */
	public void multiply (final int n) {
		numerator *= n;
	}

	/**
	 * Divides this fraction with given number.
	 *
	 * @param n The value to divide with
	 * @throws ArithmeticException when n = 0
	 */
	public void divide (final int n) {
		if (n == 0)
			throw new ArithmeticException("Cannot divide by 0");
		denominator *= n;
	}

	/**
	 * Adds this fraction with the given fraction.
	 *
	 * @param fraction The fraction to add with
	 */
	public void add (Fraction fraction) {
		if (fraction.denominator == denominator) {
			numerator += fraction.numerator;
			makeSmallest();
		} else {
			fraction = fraction.clone();
			final int backup_denominator = denominator;

			numerator *= fraction.denominator;
			denominator *= fraction.denominator;
			fraction.numerator *= backup_denominator;
			fraction.denominator *= backup_denominator;

			add(fraction);
		}
	}

	/**
	 * Makes the smallest fraction possible without having decimal points. For instance, 8/24 can become 1/3
	 */
	public Fraction makeSmallest () {
		if (denominator % numerator == 0 && numerator != 1) {
			denominator /= numerator;
			numerator = 1;
			return this;
		}

		makeSmallest_loop();
		return this;
	}

	/**
	 * The loop which is called recursively. This is only used in {@link #makeSmallest}.<br/>
	 * This loop tries to divide the numerator with the value:<pre>2 <= value <= denominator</pre>
	 */
	private void makeSmallest_loop () {
		for (int i = denominator; i > 1; i--) {
			if (numerator % i == 0 && denominator % i == 0) {
				numerator /= i;
				denominator /= i;
				makeSmallest_loop();
				break;
			}
		}
	}

	@Override
	public boolean equals (final Object other) {
		if (other != null && other instanceof Fraction) {
			final Fraction fraction = (Fraction) other;
			return fraction.numerator == numerator && fraction.denominator == denominator;
		}
		return false;
	}

	@Override
	public Fraction clone () {
		return new Fraction(numerator, denominator);
	}
}
