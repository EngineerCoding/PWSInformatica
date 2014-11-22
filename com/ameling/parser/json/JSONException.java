package com.ameling.parser.json;

import com.ameling.parser.SyntaxException;

public class JSONException extends SyntaxException {
	private static final long serialVersionUID = 1L;

    /**
     * Generate a new instance with the given message
     * @param message - The message
     */
	public JSONException(final String message) {
		super(message);
	}

    /**
     * A message which is formatted with {@link String#format(String, Object[])}
     * @param format String format
     * @param objects The objects matching with the format
     * @throws NullPointerException If the format is null
     * @throws IllegalArgumentException When the format does not match the objects
     */
    public JSONException(final String format, final Object ... objects) {
        super(format, objects);
    }
}
