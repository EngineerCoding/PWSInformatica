package com.ameling.parser.json;

/**
 * This factory is intended to be used by users. In the future it can be expected that this interface actually has a function in this package, but for now it is just a nice
 * place to create JSON objects and your wished objects. It is recommended to make an implementation of this in a singleton class.
 *
 * @author Wesley A
 */
public interface IJSONFactory<T> {

	/**
	 * Create a JSON object/array from the given class
	 *
	 * @param object The object to make a JSON object/array from
	 * @return A JSON object/array which can be turned into the original object
	 */
	public JSON createJSON (final T object);

	/**
	 * Creates an instance from the JSON object/array returned by {@link #createJSON(T)}
	 *
	 * @param json The JSON to use
	 * @return A object which is similar to input object of {@link #createJSON(T)}
	 * @throws JSONException when the JSON object/array is invalid
	 */
	public T createInstance (final JSON json) throws JSONException;

}
