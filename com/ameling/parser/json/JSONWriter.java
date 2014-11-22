package com.ameling.parser.json;

import com.ameling.parser.Constants;
import com.ameling.parser.json.JSONParser.Type;

import java.io.IOException;
import java.io.Writer;

/**
 * This class is used to write to a {@link Writer} object. Then you can call 
 * @author Wesley A
 */
public class JSONWriter {
	
	/**
	 * {@link Writer} to be used for this class
	 */
	private final Writer writer;
	
	/**
	 * Boolean whether to decide if we should add indentation and line-ends 
	 */
	private final boolean bIndent;
	
	/**
	 * Current indentation level
	 */
	private int tabs = 0;
	
	/**
	 * Creates a new instance of this class using {@link #JSONWriter(Writer, boolean)} with argument true 
	 * @param writer - The writer to use in this object
	 */
	public JSONWriter(Writer writer) {
		this(writer, true);
	}
	
	/**
	 * Creates a new instance of this class
	 * @param writer - The writer to use in this object
	 * @param bIndent - True to indent and add line-ends or false to have plain text
	 */
	public JSONWriter(final Writer writer, final boolean bIndent) {
		this.writer = writer;
		this.bIndent = bIndent;
		if(writer == null)
			throw new NullPointerException("Writer is null");
	}
	
	/**
	 * Appends the writer with this object
	 * @param parser - The object to write to the {@link #writer}
	 * @throws IOException when the writer throws one
	 */
	public synchronized void append(final JSONArray parser) throws IOException {
   		writer.write('[');

		tabs += 1;
		int maxIndex = parser.getSize();
		for(int i = 0; i < maxIndex; i++) {
			markLineEnd();
			writeValue(parser.get(i), i != maxIndex - 1);
		}
			
		tabs -= 1;
		if(maxIndex != 0)
			markLineEnd();
		writer.write(']');
		writer.flush();
	}
	
	/**
	 * Appends the writer with this object
	 * @param parser - The object to write to the {@link #writer}
	 * @throws IOException when the {@link #writer} throws one
	 */
	public synchronized void append(final JSONObject parser) throws IOException {
		writer.write(Constants.ObjectStartingChar);
			
		tabs += 1;
		String[] keys = parser.getKeys();
		for(int i = 0; i < keys.length; i++) {
			markLineEnd();
			writeString(keys[i]);
			writer.write(Constants.colon + (bIndent ? " " : ""));
			writeValue(parser.get(keys[i]), i != keys.length - 1);
		}
			
		tabs -= 1;
		//if(keys.length != 0)
			markLineEnd();
		writer.write(Constants.ObjectEndingChar);
		writer.flush();
	}
	
	/**
	 * Marks the line end using {@link System#lineSeparator()} and indents afterwards.<br/>
	 * This only happens when {@link #bIndent} is set to true
	 * @throws IOException when the {@link #writer} throws one
	 */
	private void markLineEnd() throws IOException {
		if(bIndent) {
			writer.write(System.lineSeparator());
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < tabs; i ++)
				sb.append('\t');
			writer.write(sb.toString());
		}
	}
	
	/**
	 * Writes the value separately
	 * @param object - The object to write
	 * @param hasNext - Whether have a ',' after the value
	 * @throws IOException when the {@link #writer} throws one
	 */
	private void writeValue(final Object object, final boolean hasNext) throws IOException {
		Type type = JSONParser.getType(object);
		if(type != Type.Null) {
			if(type == Type.String) {
				writeString((String) object);
			} else if(type == Type.JSONArray) {
				append((JSONArray) object);
			} else if(type == Type.JSONObject) {
				append((JSONObject) object);
			} else {
				writer.write(object.toString());
			}
			
			if(hasNext)
				writer.write(',');
		}
	}
	
	/**
	 * Write the string with the correct quotes
	 * @param string - The string in question
	 * @throws IOException when the {@link #writer} throws one
	 */
	private void writeString(final String string) throws IOException {
		if(string != null) {
			writer.write('"');
			writer.write(string);
			writer.write('"');
		}
	}
}
