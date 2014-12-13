package com.ameling.parser.json;

import com.ameling.parser.SyntaxException;
import com.ameling.parser.Tokenizer;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import static com.ameling.parser.Constants.*;

/**
 * A JSON object to parse a JSON String. The syntax for a JSON string is as follows:<br/>
 * For information on what JSON is, please visit <a href="http://json.org/">The Official site of JSON</a>
 *
 * @author Wesley A
 */
public class JSONObject extends JSON {

	// All constants used within this class only
	private static final String STRING_KEY = "key";
	private static final String STRING_VALUE = "value";

	/**
	 * A {@link Map} to store keys with a value
	 */
	private final Map<String, Object> storage = new HashMap<String, Object>();

	/**
	 * Creates an empty JSONObject
	 */
	public JSONObject () {
		super(null);
	}

	/**
	 * Creates a new instance of JSONObject and parses it.<br/>
	 * This constructor is short for <code>new JSONObject(new StringReader(jsonobject));</code>
	 *
	 * @param jsonobject The string that reads a JSONObject
	 */
	public JSONObject (final String jsonobject) {
		this(new StringReader(jsonobject));
	}

	/**
	 * Creates a new instance of JSONObject and parses it.<br/>
	 * This constructor is short for <code>new JSONObject(new Tokenizer(String));</code>
	 *
	 * @param reader The reader to use for this object
	 * @throws SyntaxException when a syntax error is detected in this string
	 */
	public JSONObject (final Reader reader) {
		this(new Tokenizer(reader));
	}

	/**
	 * Creates a new instance of JSONObject and uses the tokenizer to parse a JSON object.
	 *
	 * @param tokenizer - The tokenizer which is used to parse
	 * @throws SyntaxException when a syntax error is detected in this tokenizer
	 */
	public JSONObject (final Tokenizer tokenizer) {
		super(tokenizer);

		if (tokenizer.isNext(CHAR_JSON_OBJECT_START)) {
			do {
				parseKeyValue();
			} while (tokenizer.isNext(CHAR_COMMA));

			if (tokenizer.isNext(CHAR_JSON_OBJECT_END))
				return;
			throw new SyntaxException(FORMAT_EXPECTED_CHAR, CHAR_JSON_OBJECT_END);
		}

		throw new SyntaxException(FORMAT_EXPECTED_CHAR, CHAR_JSON_OBJECT_START);
	}

	/**
	 * Parses the next key with a value, automatically adds to the storage{@link #storage}
	 *
	 * @throws SyntaxException when a syntax error has occurred in the JSON string
	 */
	private void parseKeyValue () {
		final String key = parseString();
		if (key != null) {
			if (tokenizer.isNext(CHAR_COLON)) {
				final Object obj = parseValue();
				if (obj != null) {
					storage.size();
					storage.put(key, obj);
					return;
				}
				throw new SyntaxException(FORMAT_EXPECTED_CHAR, STRING_VALUE);
			}
			throw new SyntaxException(FORMAT_EXPECTED_CHAR, CHAR_COLON);
		}
		throw new SyntaxException(FORMAT_EXPECTED_CHAR, STRING_KEY);
	}

	/**
	 * Checks if the key exists in the storage
	 *
	 * @param key The key of the associated value
	 * @return Whether it exists in the storage or not
	 * @throws JSONException when the key is not in the {@link #storage}
	 */
	public boolean has (final String key) {
		if (storage.containsKey(key))
			return true;
		throw new JSONException(FORMAT_EXPECTED_EXISTING_KEY, key, TYPE_JSON_OBJECT);
	}

	/**
	 * Checks if the value of the key is a dummy null object
	 *
	 * @param key The key of the associated value
	 * @return Whether this is a dummy null object (so in the JSON a value is 'null') or not. When it returns false it can mean it is
	 * an actual value or java-null
	 * @throws JSONException when the key is not in the {@link #storage}
	 */
	public boolean isNull (final String key) {
		return JSON.isNullValue(get(key));
	}

	/**
	 * Retrieves the type of the object which is associated with the key
	 *
	 * @param key The key of the associated value
	 * @return A {@link Type} object, defining the object which is associated with the key
	 * @throws JSONException when the key is not in the {@link #storage}
	 */
	public Type getType (final String key) {
		return JSON.getType(get(key));
	}

	/**
	 * Retrieves a value from the key
	 *
	 * @param key String defining a key which is in the {@link #storage}
	 * @return The value associated with the keys
	 * @throws JSONException when the key is not in the {@link #storage}
	 */
	public Object get (final String key) {
		if (has(key))
			return storage.get(key);
		return null;
	}

	/**
	 * Retrieves a value from the key and tries to convert it to a {@link String}
	 *
	 * @param key String defining a key which is in the {@link #storage}
	 * @return The {@link String} associated with the key
	 * @throws JSONException when the key is not in the {@link #storage}
	 */
	public String getString (final String key) {
		if (getType(key) == Type.String)
			return (String) get(key);
		throw new JSONException(FORMAT_EXPECTED_VALUE, key, TYPE_STRING);
	}

	/**
	 * Retrieves a value from the key and tries to convert it to a {@link Number}
	 *
	 * @param key  String defining a key which is in the {@link #storage}
	 * @param type String defining what the parent caller was, as this method is private
	 * @return The {@link Number} associated with the key
	 * @throws JSONException when the key is not in the {@link #storage}
	 */
	private Number getNumber (final String key, final String type) {
		if (getType(key) == Type.Number)
			return (Number) get(key);
		throw new JSONException(FORMAT_EXPECTED_VALUE, key, type);
	}

	/**
	 * Retrieves a value from the key and tries to convert it to a long
	 *
	 * @param key String defining a key which is in the {@link #storage}
	 * @return The long associated with the key
	 * @throws JSONException when the key is not in the {@link #storage}
	 */
	public long getLong (final String key) {
		return getNumber(key, TYPE_LONG).longValue();
	}

	/**
	 * Retrieves a value from the key and tries to convert it to a int
	 *
	 * @param key String defining a key which is in the {@link #storage}
	 * @return The int associated with the key
	 * @throws JSONException when the key is not in the {@link #storage}
	 */
	public int getInt (final String key) {
		return getNumber(key, TYPE_INT).intValue();
	}

	/**
	 * Retrieves a value from the key and tries to convert it to a short
	 *
	 * @param key String defining a key which is in the {@link #storage}
	 * @return The short associated with the key
	 * @throws JSONException when the key is not in the {@link #storage}
	 */
	public short getShort (final String key) {
		return getNumber(key, TYPE_SHORT).shortValue();
	}

	/**
	 * Retrieves a value from the key and tries to convert it to a byte
	 *
	 * @param key String defining a key which is in the {@link #storage}
	 * @return The byte associated with the key
	 * @throws JSONException when the key is not in the {@link #storage}
	 */
	public byte getByte (final String key) {
		return getNumber(key, TYPE_BYTE).byteValue();
	}

	/**
	 * Retrieves a value from the key and tries to convert it to a doubles
	 *
	 * @param key String defining a key which is in the {@link #storage}
	 * @return The double associated with the key
	 * @throws JSONException when the key is not in the {@link #storage}
	 */
	public double getDouble (final String key) {
		return getNumber(key, TYPE_DOUBLE).doubleValue();
	}

	/**
	 * Retrieves a value from the key and tries to convert it to a float
	 *
	 * @param key String defining a key which is in the {@link #storage}
	 * @return The float associated with the key
	 * @throws JSONException when the key is not in the {@link #storage}
	 */
	public float getFloat (final String key) {
		return getNumber(key, TYPE_FLOAT).floatValue();
	}

	/**
	 * Retrieves a value from the key and tries to convert it to a {@link Boolean}
	 *
	 * @param key String defining a key which is in the {@link #storage}
	 * @return The boolean associated with the key
	 * @throws JSONException when the key is not in the {@link #storage}
	 */
	public boolean getBoolean (final String key) {
		if (getType(key) == Type.Boolean)
			return (Boolean) get(key);
		throw new JSONException(FORMAT_EXPECTED_VALUE, TYPE_BOOLEAN);
	}

	/**
	 * Retrieves a value from the key and tries to convert it to a {@link JSONObject}
	 *
	 * @param key String defining a key which is in the {@link #storage}
	 * @return The {@link JSONObject} associated with the key
	 * @throws JSONException when the key is not in the {@link #storage}
	 */
	public JSONObject getJSONObject (final String key) {
		if (getType(key) == Type.JSONObject)
			return (JSONObject) get(key);
		throw new JSONException(FORMAT_EXPECTED_VALUE, TYPE_JSON_OBJECT);
	}

	/**
	 * Retrieves a value from the key and tries to convert it to a {@link JSONArray}
	 *
	 * @param key String defining a key which is in the {@link #storage}
	 * @return The {@link JSONArray} associated with the key
	 * @throws JSONException when the key is not in the {@link #storage}
	 */
	public JSONArray getJSONArray (final String key) {
		if (getType(key) == Type.JSONArray)
			return (JSONArray) get(key);
		throw new JSONException(FORMAT_EXPECTED_VALUE, TYPE_JSON_ARRAY);
	}

	/**
	 * This collects all the key strings available in the {@link #storage}
	 *
	 * @return A String[] containing all key strings
	 */
	public String[] getKeys () {
		String[] names = new String[storage.size()];

		int index = 0;
		for (Map.Entry<String, Object> entry : storage.entrySet())
			names[index++] = entry.getKey();

		return names;
	}

	/**
	 * Sets the given key to the given value
	 *
	 * @param key   The key to set the value to
	 * @param value The value to set the key to
	 * @return Whether it succeed or not to set the value. It will fail when the key or value is null.
	 * @throws JSONException when the key or value is null (use {@link #setNull(String)} to set a value to null}
	 */
	private JSONObject set (final String key, final Object value) {
		if (key != null && value != null) {
			storage.put(key, value);
			return this;
		}
		throw new JSONException(EXCEPTION_VALUE_KEY_NULL);
	}

	/**
	 * Sets the given key to the given {@link String}
	 *
	 * @param key   The key to set the value to
	 * @param value The value to set the key to
	 * @return Whether it succeed or not to set the value. It will fail when the key or value is null.
	 * @throws JSONException when the key or value is null (use {@link #setNull(String)} to set a value to null}
	 */
	public JSONObject set (final String key, final String value) {
		return set(key, (Object) value);
	}

	/**
	 * Sets the given key to the given boolean
	 *
	 * @param key   The key to set the value to
	 * @param value The value to set the key to
	 * @return Whether it succeed or not to set the value. It will fail when the key or value is null.
	 * @throws JSONException when the key or value is null (use {@link #setNull(String)} to set a value to null}
	 */
	public JSONObject set (final String key, final boolean value) {
		return set(key, (Boolean) value);
	}

	/**
	 * Sets the given key to the given {@link JSONObject}
	 *
	 * @param key   The key to set the value to
	 * @param value The value to set the key to
	 * @return Whether it succeed or not to set the value. It will fail when the key or value is null.
	 * @throws JSONException when the key or value is null (use {@link #setNull(String)} to set a value to null}
	 */
	public JSONObject set (final String key, final JSONObject value) {
		return set(key, (Object) value);
	}

	/**
	 * Sets the given key to the given {@link JSONArray}
	 *
	 * @param key   The key to set the value to
	 * @param value The value to set the key to
	 * @return Whether it succeed or not to set the value. It will fail when the key or value is null.
	 * @throws JSONException when the key or value is null (use {@link #setNull(String)} to set a value to null}
	 */
	public JSONObject set (final String key, final JSONArray value) {
		return set(key, (Object) value);
	}

	/**
	 * Sets the given key to the given long
	 *
	 * @param key   The key to set the value to
	 * @param value The value to set the key to
	 * @return Whether it succeed or not to set the value. It will fail when the key or value is null.
	 * @throws JSONException when the key or value is null (use {@link #setNull(String)} to set a value to null}
	 */
	public JSONObject set (final String key, final long value) {
		return set(key, (Long) value);
	}

	/**
	 * Sets the given key to the given int
	 *
	 * @param key   The key to set the value to
	 * @param value The value to set the key to
	 * @return Whether it succeed or not to set the value. It will fail when the key or value is null.
	 * @throws JSONException when the key or value is null (use {@link #setNull(String)} to set a value to null}
	 */
	public JSONObject set (final String key, final int value) {
		return set(key, (Integer) value);
	}

	/**
	 * Sets the given key to the given short
	 *
	 * @param key   The key to set the value to
	 * @param value The value to set the key to
	 * @return Whether it succeed or not to set the value. It will fail when the key or value is null.
	 * @throws JSONException when the key or value is null (use {@link #setNull(String)} to set a value to null}
	 */
	public JSONObject set (final String key, final short value) {
		return set(key, (Short) value);
	}

	/**
	 * Sets the given key to the given byte
	 *
	 * @param key   The key to set the value to
	 * @param value The value to set the key to
	 * @return Whether it succeed or not to set the value. It will fail when the key or value is null.
	 * @throws JSONException when the key or value is null (use {@link #setNull(String)} to set a value to null}
	 */
	public JSONObject set (final String key, final byte value) {
		return set(key, (Byte) value);
	}

	/**
	 * Sets the given key to the given double
	 *
	 * @param key   The key to set the value to
	 * @param value The value to set the key to
	 * @return Whether it succeed or not to set the value. It will fail when the key or value is null.
	 * @throws JSONException when the key or value is null (use {@link #setNull(String)} to set a value to null}
	 */
	public JSONObject set (final String key, final double value) {
		return set(key, (Double) value);
	}

	/**
	 * Sets the given key to the given float
	 *
	 * @param key   The key to set the value to
	 * @param value The value to set the key to
	 * @return Whether it succeed or not to set the value. It will fail when the key or value is null.
	 * @throws JSONException when the key or value is null (use {@link #setNull(String)} to set a value to null}
	 */
	public JSONObject set (final String key, final float value) {
		return set(key, (Float) value);
	}

	/**
	 * Sets the given object to {@link JSON#NULL}
	 *
	 * @param key The key to set {@link JSON#NULL} to
	 * @return Whether it succeed or not. It will fail when the key is null
	 * @throws JSONException when the key
	 */
	public JSONObject setNull (final String key) {
		return set(key, JSON.NULL);
	}

	/**
	 * Deletes the node
	 *
	 * @param key Int defining a key which is in the {@link #storage}
	 * @return The int associated with the key
	 * @throws JSONException when the key is not in the {@link #storage}
	 */
	public void deleteNode (final String key) {
		if (has(key))
			storage.remove(key);
	}
}
