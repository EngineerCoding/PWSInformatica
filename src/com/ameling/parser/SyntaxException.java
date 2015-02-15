package com.ameling.parser;

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
