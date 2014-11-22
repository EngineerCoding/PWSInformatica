package com.ameling.parser;


/**
 * The abstract class of the parser. This currently is here just for the sake of abstraction
 * @author Wesley A
 */
public abstract class Parser {

    // All constants used by this class, for optimization
    // chars:
    private static final char singleQuote = '\'';
    private static final char doubleQuote = '"';
    private static final char backslash = '\\';
    private static final char lowerE = 'e';
    private static final char upperE = 'E';
    protected static final char dash = '-';
    protected static final char plus = '+';
    private static final char dot = '.';
    private static final char t = 't';
    private static final char f = 'f';

    // exceptions:
    // formats:
    private static final String expectedNumberFormat = "Expected a number, got '%s'";
    private static final String expectedBooleanFormat = "Expected '%s', got %s";
    private static final String booleanParseErrorFormat = "Tried to parse to '%s', got %s";
    protected static final String unexpectedSymbolFormat = "Unexpected symbol '%s'";
    // regular strings
    private static final String unfinishedString = "Unfinished string";
    private static final String multipleDots = "Multiple dots have been found";
    private static final String eAtBeginning = "Cannot have 'e' or 'E' at the beginning of a number";
    // End constants

	/**
	 * The tokenizer that is used for this object
	 */
	protected Tokenizer tokenizer;
	
	/**
	 * Creates a new instance of a parser with the tokenizer in mind
	 * @param tokenizer This should be used in {@link #parseValue()} method and in any other parsing part
	 */
	public Parser(Tokenizer tokenizer) {
		this.tokenizer = tokenizer;
	}
	
	/**
	 * This should call all other value parsers when the state is appropriate. <br/>
	 * Of course you can differ from that, since not all parsers need these value types.<br/>
	 * The default value parsers  are:
	 * <ul>
	 * 	<li>{@link #parseString()}</li>
	 * 	<li>{@link #parseNumber(boolean)}</li>
	 *  <li>{@link #parseBoolean()}</li>
	 * </ul><br/>
	 * By default it already checks for those values, but you obviously can override it if you wish.
	 * 
	 * @return the appropriate object for the next value
	 * @throws SyntaxException when a value parser throws one, it will simply get back to the user
	 */
	protected Object parseValue() throws SyntaxException {
        tokenizer.skipBlanks();

		Character character = tokenizer.peek();
		if(character != null) {
			if(character == singleQuote || character == doubleQuote)
				return parseString();
			else if(Character.isDigit(character) || character == dash || character == plus)
				return parseNumber(true);
			else if(character == t || character == f)
				return parseBoolean();
		}
		return null;
	}
	
	/**
	 * Attempts to parse a string with the {@link #tokenizer}
	 * @return {@link String} when it successfully parsed or null when it failed
	 * @throws SyntaxException when a syntax error occurred
	 */
	protected String parseString() throws SyntaxException {
        tokenizer.skipBlanks();
		Character character = tokenizer.peek();
		
		final boolean singleQuote = character == Parser.singleQuote;
		final boolean doubleQuote = character == Parser.doubleQuote;
		
		if(singleQuote || doubleQuote) {
            tokenizer.pop();

            boolean backslash = false;
            final StringBuilder sb = new StringBuilder();
            while ((singleQuote || doubleQuote) && (character = tokenizer.peek()) != null) {
                if (backslash) {
                    backslash = false;
                    sb.append(tokenizer.pop());
                } else {
                    if (character == (singleQuote ? Parser.singleQuote : Parser.doubleQuote)) {
                        tokenizer.pop();
                        return sb.toString();
                    } else if (character == Parser.backslash) {
                        backslash = true;
                        sb.append(tokenizer.pop());
                    } else {
                        sb.append(tokenizer.pop());
                    }
                }
            }

            if (singleQuote || doubleQuote) {
                throw new SyntaxException(unfinishedString);
            } else {
                throw new SyntaxException(unexpectedSymbolFormat, character);
            }
        }
		return null;
	}
	
	/**
	 * Attempts to parse a number with the {@link #tokenizer}
	 * @param parseE Whether to parse the 'e' part of a number. This is used internally, but can be used externally. <br/>
	 * When it finds 'e' 
	 * @return {@link Number} object when it successfully parsed or null when it failed
	 * @throws SyntaxException when a syntax error occurred
	 */
	protected Number parseNumber(boolean parseE) throws SyntaxException {
        tokenizer.skipBlanks();

        Character character;
		boolean parsedDot = false;
		final StringBuilder sb = new StringBuilder();

		while((character = tokenizer.peek()) != null) {
			if(Character.isDigit(character)) {
				sb.append(tokenizer.pop());
			} else if(character == dash || character == plus) {
				if(sb.length() == 0) {
					sb.append(tokenizer.pop());
				} else {
					throw new SyntaxException(expectedNumberFormat, character);
				}
			} else if(character == dot) {
				if(!parsedDot) {
					parsedDot = true;
					sb.append(tokenizer.pop());
				} else {
					throw new SyntaxException(multipleDots);
				}
			} else if((character == lowerE || character == upperE)) {
				if(parseE && sb.length() != 0) {
                    tokenizer.pop();

					final Number number = parseNumber(false);
					if(number != null)
						return Double.parseDouble(sb.toString()) * Math.pow(10D, number.doubleValue());
				} else if(!parseE) {
					break;
				} else if(sb.length() == 0) {
					throw new SyntaxException(eAtBeginning);
				}
			} else {
                break;
            }
		}

		if(sb.length() != 0)
			return Double.parseDouble(sb.toString());

		return null;
	}
	
	/**
	 * Attempts to parse a boolean with the {@link #tokenizer}
	 * @return {@link Boolean} when it successfully parsed or null when it failed
	 * @throws SyntaxException when a syntax error occurred
	 */
	protected Boolean parseBoolean() throws SyntaxException {
        tokenizer.skipBlanks();

		Character character = tokenizer.peek();
		if(character != null && (character == t || character == f)) {
			final boolean parseTrue = character == t;
			final StringBuilder sb = new StringBuilder();

            for(int i = 0; i < (parseTrue ? 4 : 5) && tokenizer.peek() != null; i++) {
                character = tokenizer.peek();
                if(character != null) {
                    sb.append(character);
                } else {
                    break;
                }
            }
			
			if(sb.length() != (parseTrue ? 4 : 5))
                throw new SyntaxException(expectedBooleanFormat, parseTrue, sb.toString());

			try {
				 return Boolean.parseBoolean(sb.toString());
			} catch(Exception e) {
				throw new SyntaxException(booleanParseErrorFormat, parseTrue, sb.toString());
			}
		}
		
		return null;
	}
}
