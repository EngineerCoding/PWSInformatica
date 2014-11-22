package com.ameling.parser.math;

import com.ameling.parser.Tokenizer;

import java.io.Reader;
import java.io.StringReader;

/**
 * This class takes takes a string as mathematical expression. Since this is an embedded version of a math parser, it has specific details for the app.<br/>
 * This parser does not inherit from {@link com.ameling.parser.Parser} because this class does not need anything of the built-in methods. It uses a {@link Tokenizer} in the constructor, and that
 * is the only place it will be used and parsed.<br/>
 * This parser will assume every expression is the evaluation of the school exam grade, or a part of that. This means that the expression will calculate the end average grade, so every grade which is
 * used in the expression has a weight. This weight will be used to calculate a proper average grade with only, let's say two grades out of 4 grades which are necassery for the expression. Every
 * expression which is used to calculate the average grade from Scala Molenwatering does apply to this standard. An example expression is:
 * <pre>(4((SE1 + SE2)/2) + SE3)/5</pre>
 * This means that SE1 and SE2 both have a weight of 2/5 and SE3 has a weight of 1/5 (so it totals in 5/5 again).<br/>
 * With this logic this class parses the expression and tries to grade accordingly.<br/>
 *
 * As probably been noticed, this parser only supports plus, minus, multiply and divide functions, since that is all what is needed to determine a grade
 *
 */
public final class Expression {

    /**
     * This constructor creates a new {@link StringReader} and calls {@link #Expression(Reader)}
     * @param expression The String to create the {@link StringReader} with
     */
    public Expression(final String expression) {
        this(new StringReader(expression));
    }

    /**
     * This constructor creates a new {@link Tokenizer} and calls {@link #Expression(Tokenizer)}
     * @param reader The reader to create the {@link Tokenizer} with
     */
    public Expression(final Reader reader) {
        this(new Tokenizer(reader));
    }

    /**
     * This constructor actually does all the parsing work.
     * @param tokenizer The {@link Tokenizer} which is a tool to read out its {@link Reader}
     */
    public Expression(final Tokenizer tokenizer) {

    }

}
