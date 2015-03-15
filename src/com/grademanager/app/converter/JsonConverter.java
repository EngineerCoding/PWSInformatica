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

package com.grademanager.app.converter;

import com.grademanager.parser.json.JSON;

/**
 * This class is the super class of the all JsonConverters
 *
 * @param <A> The object which would get converted
 * @param <B> A type of Json, where the default and most used ones are: {@link com.grademanager.parser.json.JSONObject} and {@link com.grademanager.parser.json.JSONArray}
 */
public interface JsonConverter<A, B extends JSON> {
	/**
	 * Converts the given json type to an actual object (A)
	 *
	 * @param json The object to convert from (B)
	 * @return A corresponding object with the json type
	 */
	public abstract A convert (final B json);

	/**
	 * Converts the given object to a json type (B)
	 *
	 * @param object The object to convert from
	 * @return A corresponding json object/array/custom from the object
	 */
	public abstract B convert (final A object);
}
