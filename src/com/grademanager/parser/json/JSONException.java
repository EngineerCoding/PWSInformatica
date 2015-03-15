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

package com.grademanager.parser.json;

import com.grademanager.parser.SyntaxException;

public class JSONException extends SyntaxException {
	private static final long serialVersionUID = 1L;

	/**
	 * Generate a new instance with the given message
	 *
	 * @param message - The message
	 */
	public JSONException (final String message) {
		super(message);
	}

	/**
	 * A message which is formatted with {@link String#format(String, Object[])}
	 *
	 * @param format  String format
	 * @param objects The objects matching with the format
	 * @throws NullPointerException     If the format is null
	 * @throws IllegalArgumentException When the format does not match the objects
	 */
	public JSONException (final String format, final Object... objects) {
		super(format, objects);
	}
}
