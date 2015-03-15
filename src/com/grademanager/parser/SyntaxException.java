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

package com.grademanager.parser;

/**
 * This is an unchecked exception to tell the developer that a syntax error occurred
 *
 * @author Wesley A
 */
public class SyntaxException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * The message which this exception correspondents
	 */
	private final String message;

	/**
	 * Generate a new instance with the given message
	 *
	 * @param message - The message
	 */
	public SyntaxException (final String message) {
		this.message = message;
	}

	/**
	 * A message which is formatted with {@link String#format(String, Object[])}
	 *
	 * @param format  String format
	 * @param objects The objects matching with the format
	 * @throws NullPointerException     If the format is null
	 * @throws IllegalArgumentException When the format does not match the objects
	 */
	public SyntaxException (final String format, final Object... objects) {
		if (format != null) {
			message = String.format(format, objects);
			return;
		}
		throw new NullPointerException();
	}

	/**
	 * Overrides the expression to have a formatted message with the following constructor: {@link #SyntaxException(String, Object[])}
	 *
	 * @return String message of this exception
	 * @see #SyntaxException(String, Object[])
	 */
	@Override
	public String getMessage () {
		return message;
	}
}
