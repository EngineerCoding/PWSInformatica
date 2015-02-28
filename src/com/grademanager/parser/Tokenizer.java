package com.grademanager.parser;

import java.io.IOException;
import java.io.Reader;

/**
 * The tokenizer is an essential part of a parser of some type. One can use this to parse any type of text with the use of this tokenizer with
 * given methods, yet for a compiler it isn't going to work properly since you have to take into account for variables and stuff.
 *
 * @author Wesley A
 */
public class Tokenizer {

	/**
	 * Internal Character object, used by {@link #peek} and {@link #pop}
	 */
	private Character readerTracker;

	/**
	 * The Reader to use
	 */
	private final Reader reader;

	/**
	 * The same as {@link #readerTracker}, but works with {@link #injectedCharacters}
	 */
	private Character injectedTracker;

	/**
	 * The readerTracker injected by {@link #inject(String)}
	 */
	private String injectedCharacters;

	/**
	 * Creates a new instance with the reader
	 *
	 * @param reader The reader to use
	 * @throws NullPointerException when the reader is null
	 */
	public Tokenizer (final Reader reader) {
		if (reader == null)
			throw new NullPointerException();

		this.reader = reader;
		// Initialize the reader
		pop();
	}

	/**
	 * This method lets you inject the next string to be read. This works same as the reader which is used, other then this is not a reader.
	 *
	 * @param sequence The string to inject
	 */
	public void inject (final String sequence) {
		if (sequence != null) {
			if (injectedCharacters != null) {
				injectedCharacters += sequence;
			} else {
				injectedTracker = sequence.charAt(0);
				if (sequence.length() > 2)
					injectedCharacters = sequence.substring(1);
			}
		}
	}

	/**
	 * This method looks at the next character, and when it matches the given char it will {@link #pop()} and return true
	 *
	 * @param character - The character to match
	 * @return Whether the current character matches the given character
	 * @see #pop()
	 * @see #peek()
	 * @see #skipBlanks()
	 */
	public boolean isNext (final char character) {
		skipBlanks();
		final Character c = peek();
		if (c != null && c == character) {
			pop();
			return true;
		}
		return false;
	}

	/**
	 * This method looks at the current character without moving position
	 *
	 * @return <ul>
	 * <li>A {@link Character} Object of the current character.</li>
	 * <li>Null when the current position is not in range (by default that is that the end of the String).</li>
	 * </ul>
	 */
	public Character peek () {
		return injectedTracker != null ? injectedTracker : readerTracker;
	}

	/**
	 * This method returns the current character and moves the position to the next character.
	 *
	 * @return <ul>
	 * <li>A {@link Character} Object of the current character.</li>
	 * <li>Null when the current position is not in range.</li>
	 * </ul>
	 */
	public Character pop () {
		final Character backup = (injectedTracker != null ? injectedTracker : readerTracker);
		if (injectedTracker != null) {
			if (injectedCharacters != null && injectedCharacters.length() > 0) {
				injectedTracker = injectedCharacters.charAt(0);
				if (injectedCharacters.length() > 1) {
					injectedCharacters = injectedCharacters.substring(1);
				} else {
					injectedCharacters = null;
				}
			} else {
				injectedTracker = null;
			}
			return backup;
		}

		try {
			final int cValue = reader.read();
			if (cValue == -1) {
				readerTracker = null;
				reader.close(); // Release the reader's resources and close the InputStream
			} else {
				readerTracker = (char) cValue;
			}
		} catch (final IOException e) {
			readerTracker = null;
		}
		return backup;
	}

	/**
	 * Sets the position to the next non-whitespace character.
	 */
	public void skipBlanks () {
		Character character;
		while ((character = peek()) != null && Character.isWhitespace(character))
			pop();
	}
}
