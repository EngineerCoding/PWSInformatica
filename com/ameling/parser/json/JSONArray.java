package com.ameling.parser.json;

import com.ameling.parser.SyntaxException;
import com.ameling.parser.Tokenizer;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static com.ameling.parser.Constants.*;

/**
 * A JSON array to parse a JSON String. The syntax for a JSON string is as follows:<br/>
 * For information on what JSON is, please visit <a href="http://json.org/">The Official site of JSON</a>
 *
 * @author Wesley A
 */
public class JSONArray extends JSON {

	/**
	 * A {@link List} to store keys with a value
	 */
	private final List<Object> storage = new ArrayList<Object>();

	/**
	 * Creates an empty JSONArray
	 */
	public JSONArray () {
		super(null);
	}

	/**
	 * Creates a new instance of JSONArray and parses it.<br/>
	 * This constructor is short for <code>new JSONArray(new StringReader(jsonarray));</code>
	 *
	 * @param jsonarray The string that reads a JSONArray
	 */
	public JSONArray (final String jsonarray) {
		this(new StringReader(jsonarray));
	}

	/**
	 * Creates a new instance of JSONArray and parses it.<br/>
	 * This constructor is short for <code>new JSONArray(new Tokenizer(Reader));</code>
	 *
	 * @param reader The reader to use for this object
	 * @throws SyntaxException when a syntax error is detected in this string
	 */
	public JSONArray (final Reader reader) {
		this(new Tokenizer(reader));
	}

	/**
	 * Creates a new instance of JSONArray and uses the tokenizer to parse a JSON array.
	 *
	 * @param tokenizer - The tokenizer which is used to parse
	 * @throws SyntaxException when a syntax error is detected in this tokenizer
	 */
	public JSONArray (final Tokenizer tokenizer) {
		super(tokenizer);

		if (tokenizer.isNext(CHAR_JSON_ARRAY_START)) {
			do {
				final Object object = parseValue();
				if (object != null) {
					storage.add(object);
				} else {
					break;
				}
			} while (tokenizer.isNext(CHAR_COMMA));

			if (tokenizer.isNext(CHAR_JSON_ARRAY_END))
				return;

			throw new SyntaxException(FORMAT_EXPECTED_CHAR, CHAR_JSON_ARRAY_END);
		}
		throw new SyntaxException(FORMAT_EXPECTED_CHAR, CHAR_JSON_ARRAY_START);
	}

	/**
	 * Checks if the key exists in the storage
	 *
	 * @param key The key of the associated value
	 * @return Whether it exists in the storage or not
	 * @throws JSONException when the key is not in the {@link #storage}
	 */
	public boolean has (final int key) {
		if (key >= 0 && key < storage.size())
			return true;
		throw new JSONException(FORMAT_EXPECTED_EXISTING_KEY, key, TYPE_JSON_ARRAY);
	}

	/**
	 * Checks if the value of the key is a dummy null object
	 *
	 * @param key The key of the associated value
	 * @return Whether this is a dummy null object (so in the JSON a value is 'null') or not. When it returns false it can mean it is
	 * an actual value or java-null
	 * @throws JSONException when the key is not in the {@link #storage}
	 */
	public boolean isNull (final int key) {
		return JSON.isNullValue(get(key));
	}

	/**
	 * Retrieves the type of the object which is associated with the key
	 *
	 * @param key The key of the associated value
	 * @return A {@link Type} object, defining the object which is associated with the key
	 * @throws JSONException when the key is not in the {@link #storage}
	 */
	public Type getType (final int key) {
		return JSON.getType(get(key));
	}

	/**
	 * Retrieves a value from the key
	 *
	 * @param key Int defining a key which is in the {@link #storage}
	 * @return The value associated with the keys
	 * @throws JSONException when the key is not in the {@link #storage}
	 */
	public Object get (final int key) {
		if (has(key))
			return storage.get(key);
		return null;
	}

	/**
	 * Retrieves a value from the key and tries to convert it to a {@link String}
	 *
	 * @param key Int defining a key which is in the {@link #storage}
	 * @return The {@link String} associated with the key
	 * @throws JSONException when the key is not in the {@link #storage}
	 */
	public String getString (final int key) {
		if (getType(key) == Type.String)
			return (String) get(key);
		throw new JSONException(FORMAT_EXPECTED_VALUE, key, TYPE_STRING);
	}

	/**
	 * Retrieves a value from the key and tries to convert it to a {@link Number}
	 *
	 * @param key  Int defining a key which is in the {@link #storage}
	 * @param type String defining what the parent caller was, as this method is private
	 * @return The {@link Number} associated with the key
	 * @throws JSONException when the key is not in the {@link #storage}
	 */
	private Number getNumber (final int key, final String type) {
		if (getType(key) == Type.Number)
			return (Number) get(key);
		throw new JSONException(FORMAT_EXPECTED_VALUE, key, type);
	}

	/**
	 * Retrieves a value from the key and tries to convert it to a long
	 *
	 * @param key Int defining a key which is in the {@link #storage}
	 * @return The long associated with the key
	 * @throws JSONException when the key is not in the {@link #storage}
	 */
	public long getLong (final int key) {
		return getNumber(key, TYPE_LONG).longValue();
	}

	/**
	 * Retrieves a value from the key and tries to convert it to a int
	 *
	 * @param key Int defining a key which is in the {@link #storage}
	 * @return The int associated with the key
	 * @throws JSONException when the key is not in the {@link #storage}
	 */
	public int getInt (final int key) {
		return getNumber(key, TYPE_INT).intValue();
	}

	/**
	 * Retrieves a value from the key and tries to convert it to a short
	 *
	 * @param key Int defining a key which is in the {@link #storage}
	 * @return The short associated with the key
	 * @throws JSONException when the key is not in the {@link #storage}
	 */
	public short getShort (final int key) {
		return getNumber(key, TYPE_SHORT).shortValue();
	}

	/**
	 * Retrieves a value from the key and tries to convert it to a byte
	 *
	 * @param key Int defining a key which is in the {@link #storage}
	 * @return The byte associated with the key
	 * @throws JSONException when the key is not in the {@link #storage}
	 */
	public byte getByte (final int key) {
		return getNumber(key, TYPE_BYTE).byteValue();
	}

	/**
	 * Retrieves a value from the key and tries to convert it to a doubles
	 *
	 * @param key Int defining a key which is in the {@link #storage}
	 * @return The double associated with the key
	 * @throws JSONException when the key is not in the {@link #storage}
	 */
	public double getDouble (final int key) {
		return getNumber(key, TYPE_DOUBLE).doubleValue();
	}

	/**
	 * Retrieves a value from the key and tries to convert it to a float
	 *
	 * @param key Int defining a key which is in the {@link #storage}
	 * @return The float associated with the key
	 * @throws JSONException when the key is not in the {@link #storage}
	 */
	public float getFloat (final int key) {
		return getNumber(key, TYPE_FLOAT).floatValue();
	}

	/**
	 * Retrieves a value from the key and tries to convert it to a {@link Boolean}
	 *
	 * @param key Int defining a key which is in the {@link #storage}
	 * @return The boolean associated with the key
	 * @throws JSONException when the key is not in the {@link #storage}
	 */
	public boolean getBoolean (final int key) {
		if (getType(key) == Type.Boolean)
			return (Boolean) get(key);
		throw new JSONException(FORMAT_EXPECTED_VALUE, TYPE_BOOLEAN);
	}

	/**
	 * Retrieves a value from the key and tries to convert it to a {@link JSONObject}
	 *
	 * @param key Int defining a key which is in the {@link #storage}
	 * @return The {@link JSONObject} associated with the key
	 * @throws JSONException when the key is not in the {@link #storage}
	 */
	public JSONObject getJSONObject (final int key) {
		if (getType(key) == Type.JSONObject)
			return (JSONObject) get(key);
		throw new JSONException(FORMAT_EXPECTED_VALUE, TYPE_JSON_OBJECT);
	}

	/**
	 * Retrieves a value from the key and tries to convert it to a {@link JSONArray}
	 *
	 * @param key Int defining a key which is in the {@link #storage}
	 * @return The {@link JSONArray} associated with the key
	 * @throws JSONException when the key is not in the {@link #storage}
	 */
	public JSONArray getJSONArray (final int key) {
		if (getType(key) == Type.JSONArray)
			return (JSONArray) get(key);
		throw new JSONException(FORMAT_EXPECTED_VALUE, TYPE_JSON_ARRAY);
	}

	/**
	 * Returns the size of the {@link #storage}
	 *
	 * @return Int max size
	 */
	public int getSize () {
		return storage.size();
	}

	/**
	 * Sets the given key to the given value, when the key is bigger then the {@link #storage} size it will append at the next index
	 *
	 * @param key   The key to set the value to
	 * @param value The value to set the key to
	 * @throws IndexOutOfBoundsException when the index <code>{@link #storage}.size() < 0 || index >= {@link #storage}.size()</code>
	 * @throws JSONException             when the value is null
	 */
	private JSONArray set (final int key, final Object value) {
		if (value != null) {
			if (key > 0 && key < storage.size()) {
				storage.set(key, value);
				return this;
			} else {
				throw new IndexOutOfBoundsException();
			}
		}
		throw new JSONException(EXCEPTION_VALUE_KEY_NULL);
	}

	/**
	 * Sets the given key to the given value
	 *
	 * @param key   The key to set the value to
	 * @param value The value to set the key to
	 * @return this
	 * @throws IndexOutOfBoundsException when the index <code>{@link #storage}.size() < 0 || index >= {@link #storage}.size()</code>
	 * @throws JSONException             when the value is null
	 */
	public JSONArray set (final int key, final String value) {
		return set(key, (Object) value);
	}

	/**
	 * Sets the given key to the given value
	 *
	 * @param key   The key to set the value to
	 * @param value The value to set the key to
	 * @return this
	 * @throws IndexOutOfBoundsException when the index <code>{@link #storage}.size() < 0 || index >= {@link #storage}.size()</code>
	 */
	public JSONArray set (final int key, final boolean value) {
		return set(key, (Boolean) value);
	}

	/**
	 * Sets the given key to the given value
	 *
	 * @param key   The key to set the value to
	 * @param value The value to set the key to
	 * @return this
	 * @throws IndexOutOfBoundsException when the index <code>{@link #storage}.size() < 0 || index >= {@link #storage}.size()</code>
	 * @throws JSONException             when the value is null
	 */
	public JSONArray set (final int key, final JSONObject value) {
		return set(key, (Object) value);
	}

	/**
	 * Sets the given key to the given value
	 *
	 * @param key   The key to set the value to
	 * @param value The value to set the key to
	 * @return this
	 * @throws IndexOutOfBoundsException when the index <code>{@link #storage}.size() < 0 || index >= {@link #storage}.size()</code>
	 * @throws JSONException             when the value is null
	 */
	public JSONArray set (final int key, final JSONArray value) {
		return set(key, (Object) value);
	}

	/**
	 * Sets the given key to the given value
	 *
	 * @param key   The key to set the value to
	 * @param value The value to set the key to
	 * @return this
	 * @throws IndexOutOfBoundsException when the index <code>{@link #storage}.size() < 0 || index >= {@link #storage}.size()</code>
	 */
	public JSONArray set (final int key, final long value) {
		return set(key, (Long) value);
	}

	/**
	 * Sets the given key to the given value
	 *
	 * @param key   The key to set the value to
	 * @param value The value to set the key to
	 * @return this
	 * @throws IndexOutOfBoundsException when the index <code>{@link #storage}.size() < 0 || index >= {@link #storage}.size()</code>
	 */
	public JSONArray set (final int key, final int value) {
		return set(key, (Integer) value);
	}

	/**
	 * Sets the given key to the given value
	 *
	 * @param key   The key to set the value to
	 * @param value The value to set the key to
	 * @return this
	 * @throws IndexOutOfBoundsException when the index <code>{@link #storage}.size() < 0 || index >= {@link #storage}.size()</code>
	 */
	public JSONArray set (final int key, final short value) {
		return set(key, (Short) value);
	}

	/**
	 * Sets the given key to the given value
	 *
	 * @param key   The key to set the value to
	 * @param value The value to set the key to
	 * @return this
	 * @throws IndexOutOfBoundsException when the index <code>{@link #storage}.size() < 0 || index >= {@link #storage}.size()</code>
	 */
	public JSONArray set (final int key, final byte value) {
		return set(key, (Byte) value);
	}

	/**
	 * Sets the given key to the given value
	 *
	 * @param key   The key to set the value to
	 * @param value The value to set the key to
	 * @return this
	 * @throws IndexOutOfBoundsException when the index <code>{@link #storage}.size() < 0 || index >= {@link #storage}.size()</code>
	 */
	public JSONArray set (final int key, final double value) {
		return set(key, (Double) value);
	}

	/**
	 * Sets the given key to the given value
	 *
	 * @param key   The key to set the value to
	 * @param value The value to set the key to
	 * @return this
	 * @throws IndexOutOfBoundsException when the index <code>{@link #storage}.size() < 0 || index >= {@link #storage}.size()</code>
	 */
	public JSONArray set (final int key, final float value) {
		return set(key, (Float) value);
	}

	/**
	 * Sets the given key to the given value
	 *
	 * @param key The key to set the value to
	 * @return this
	 * @throws IndexOutOfBoundsException when the index <code>{@link #storage}.size() < 0 || index >= {@link #storage}.size()</code>
	 */
	public JSONArray setNull (final int key) {
		return set(key, JSON.NULL);
	}

	/**
	 * Adds the value to {@link #storage}
	 *
	 * @param value The value to add
	 * @return this
	 * @throws JSONException when the value is null
	 */
	private JSONArray add (final Object value) {
		if (value != null) {
			storage.add(value);
			return this;
		}
		throw new JSONException(EXCEPTION_VALUE_KEY_NULL);
	}

	/**
	 * Adds the value to {@link #storage}
	 *
	 * @return this
	 * @throws JSONException when the value is null
	 */
	public JSONArray add (final String string) {
		return add((Object) string);
	}

	/**
	 * Adds the value to {@link #storage}
	 *
	 * @param value The value to add
	 * @return this
	 */
	public JSONArray add (final boolean value) {
		return add((Boolean) value);
	}

	/**
	 * Adds the value to {@link #storage}
	 *
	 * @param value The value to add
	 * @return this
	 * @throws JSONException when the value is null
	 */
	public JSONArray add (final JSONObject value) {
		return add((Object) value);
	}

	/**
	 * Adds the value to {@link #storage}
	 *
	 * @param value The value to add
	 * @return this
	 * @throws JSONException when the value is null
	 */
	public JSONArray add (final JSONArray value) {
		return add((Object) value);
	}

	/**
	 * Adds the value to {@link #storage}
	 *
	 * @param value The value to add
	 * @return this
	 */
	public JSONArray add (final long value) {
		return add((Long) value);
	}

	/**
	 * Adds the value to {@link #storage}
	 *
	 * @param value The value to add
	 * @return this
	 */
	public JSONArray add (final int value) {
		return add((Integer) value);
	}

	/**
	 * Adds the value to {@link #storage}
	 *
	 * @param value The value to add
	 * @return this
	 */
	public JSONArray add (final short value) {
		return add((Short) value);
	}

	/**
	 * Adds the value to {@link #storage}
	 *
	 * @param value The value to add
	 * @return this
	 */
	public JSONArray add (final byte value) {
		return add((Byte) value);
	}

	/**
	 * Adds the value to {@link #storage}
	 *
	 * @param value The value to add
	 * @return this
	 */
	public JSONArray add (final double value) {
		return add((Double) value);
	}

	/**
	 * Adds the value to {@link #storage}
	 *
	 * @param value The value to add
	 * @return this
	 */
	public JSONArray add (final float value) {
		return add((Float) value);
	}

	/**
	 * Adds {@link JSON#NULL} to {@link #storage}
	 *
	 * @return this
	 */
	public JSONArray addNull () {
		return add(JSON.NULL);
	}

	/**
	 * Deletes the node and shifts all upper entries
	 *
	 * @param key Int defining a key which is in the {@link #storage}
	 * @return The int associated with the key
	 * @throws JSONException when the key is not in the {@link #storage}
	 */
	public void deleteNode (final int key) {
		if (has(key))
			storage.remove(key);
	}
}
