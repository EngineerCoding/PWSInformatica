package com.ameling.grademanager.converter;

import com.ameling.parser.json.JSON;

/**
 * This class is the super class of the all JsonConverters
 *
 * @param <A> The object which would get converted
 * @param <B> A type of Json, where the default and most used ones are: {@link com.ameling.parser.json.JSONObject} and {@link com.ameling.parser.json.JSONArray}
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
