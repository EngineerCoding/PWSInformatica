package com.ameling.parser;

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
    private Character character;

    /**
     * The Reader to use
     */
    private final Reader reader;

    /**
     * Creates a new instance with the reader
     *
     * @param reader The reader to use
     */
    public Tokenizer(final Reader reader) {
        if (reader == null)
            throw new NullPointerException();

        this.reader = reader;
        // Initialize the reader
        pop();
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
    public boolean isNext(char character) {
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
    public Character peek() {
        return character;
    }

    /**
     * This method returns the current character and moves the position to the next character.
     *
     * @return <ul>
     * <li>A {@link Character} Object of the current character.</li>
     * <li>Null when the current position is not in range.</li>
     * </ul>
     */
    public Character pop() {
        final Character backup = character;
        try {
            final int cValue = reader.read();
            if (cValue == -1) {
                character = null;
            } else {
                character = (char) cValue;
            }
        } catch (IOException e) {
            character = null;
        }
        return backup;
    }

    /**
     * Sets the position to the next non-whitespace character.
     */
    public void skipBlanks() {
        Character character;
        while ((character = peek()) != null && Character.isWhitespace(character))
            pop();
    }
}
