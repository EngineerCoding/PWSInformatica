package com.ameling.parser.json;

import com.ameling.parser.Constants;
import com.ameling.parser.json.JSON.Type;

import java.io.IOException;
import java.io.Writer;

import static com.ameling.parser.Constants.CHAR_JSON_ARRAY_START;
import static com.ameling.parser.Constants.CHAR_JSON_ARRAY_END;
import static com.ameling.parser.Constants.CHAR_JSON_OBJECT_START;
import static com.ameling.parser.Constants.CHAR_JSON_OBJECT_END;
import static com.ameling.parser.Constants.CHAR_COLON;
import static com.ameling.parser.Constants.CHAR_COMMA;

/**
 * This class is used to write to a {@link java.io.Writer} object.
 *
 * @author Wesley A
 */
public class JSONWriter {

	// All constants used within this class only
	private static final char CHAR_TAB = '\t';
	private static final String STRING_EMPTY = "";
	private static final String STRING_SPACE = " ";
	private static final String STRING_WRITER_NULL = "Writer is null";

	private static final String STRING_lINE_SEPARATOR;

	static {
		STRING_lINE_SEPARATOR = System.getProperty("line.separator");
	}

	/**
	 * {@link java.io.Writer} to be used for this class
	 */
	private final Writer writer;

	/**
	 * Boolean whether to decide if we should add indentation and line-ends
	 */
	private final boolean indent;

	/**
	 * Current indentation level
	 */
	private int tabs = 0;

	/**
	 * Creates a new instance of this class using {@link #JSONWriter(java.io.Writer, boolean)} with argument true
	 *
	 * @param writer - The writer to use in this object
	 */
	public JSONWriter (Writer writer) {
		this(writer, true);
	}

	/**
	 * Creates a new instance of this class
	 *
	 * @param writer - The writer to use in this object
	 * @param indent - True to indent and add line-ends or false to have plain text
	 */
	public JSONWriter (final Writer writer, final boolean indent) {
		this.writer = writer;
		this.indent = indent;
		if (writer == null)
			throw new NullPointerException(STRING_WRITER_NULL);
	}

	/**
	 * Appends the writer with this object
	 *
	 * @param parser - The object to write to the {@link #writer}
	 * @throws java.io.IOException when the writer throws one
	 */
	public synchronized void append (final JSONArray parser) throws IOException {
		writer.write(CHAR_JSON_ARRAY_START);

		tabs += 1;
		int maxIndex = parser.getSize();
		for (int i = 0; i < maxIndex; i++) {
			markLineEnd();
			writeValue(parser.get(i), i != maxIndex - 1);
		}

		tabs -= 1;
		if (maxIndex != 0)
			markLineEnd();
		writer.write(CHAR_JSON_ARRAY_END);
		writer.flush();
	}

	/**
	 * Appends the writer with this object
	 *
	 * @param parser - The object to write to the {@link #writer}
	 * @throws java.io.IOException when the {@link #writer} throws one
	 */
	public synchronized void append (final JSONObject parser) throws IOException {
		writer.write(CHAR_JSON_OBJECT_START);

		tabs += 1;
		String[] keys = parser.getKeys();
		for (int i = 0; i < keys.length; i++) {
			markLineEnd();
			writeString(keys[i]);
			writer.write(CHAR_COLON + (indent ? STRING_SPACE : STRING_EMPTY));
			writeValue(parser.get(keys[i]), i != keys.length - 1);
		}

		tabs -= 1;
		//if(keys.length != 0)
		markLineEnd();
		writer.write(CHAR_JSON_OBJECT_END);
		writer.flush();
	}

	/**
	 * Closes the original writer
	 *
	 * @throws IOException if an error occurs closing this writer
	 */
	public void close () throws IOException {
		writer.close();
	}

	/**
	 * Marks the line end using {@code System.getProperty("line.separator")}and indents afterwards.<br/>
	 * This only happens when {@link #indent} is set to true
	 *
	 * @throws java.io.IOException when the {@link #writer} throws one
	 */
	private void markLineEnd () throws IOException {
		if (indent) {
			writer.write(STRING_lINE_SEPARATOR);
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < tabs; i++)
				sb.append(CHAR_TAB);
			writer.write(sb.toString());
		}
	}

	/**
	 * Writes the value separately
	 *
	 * @param object  - The object to write
	 * @param hasNext - Whether have a ',' after the value
	 * @throws java.io.IOException when the {@link #writer} throws one
	 */
	private void writeValue (final Object object, final boolean hasNext) throws IOException {
		Type type = JSON.getType(object);
		if (type != Type.Null) {
			if (type == Type.String) {
				writeString((String) object);
			} else if (type == Type.JSONArray) {
				append((JSONArray) object);
			} else if (type == Type.JSONObject) {
				append((JSONObject) object);
			} else {
				writer.write(object.toString());
			}

			if (hasNext)
				writer.write(CHAR_COMMA);
		}
	}

	/**
	 * Write the string with the correct quotes
	 *
	 * @param string - The string in question
	 * @throws java.io.IOException when the {@link #writer} throws one
	 */
	private void writeString (final String string) throws IOException {
		if (string != null) {
			writer.write(Constants.CHAR_QUOTE_DOUBLE);
			writer.write(string);
			writer.write(Constants.CHAR_QUOTE_DOUBLE);
		}
	}
}
