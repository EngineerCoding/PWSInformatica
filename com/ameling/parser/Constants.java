package com.ameling.parser;

/**
 * This class is here for pure efficiency, it holds all String and char constants to optimize the code, especially in parsers and also for exception throwing <br/>
 * This class is only used in the {@link com.ameling.parser.json} package
 * @author Wesley A
 */
public class Constants {
    // Not intended as object
    private Constants() {}

    // All used chars
    public static final char ObjectStartingChar = '{';
    public static final char ObjectEndingChar = '}';
    public static final char ArrayStartingChar = '[';
    public static final char ArrayEndingChar = ']';
    public static final char OpeningBracket = '(';
    public static final char ClosingBracket = ')';

    public static final char valueSeparator = ',';
    public static final char colon = ':';

    // All formats to be used in Exceptions
    public static final String expectedCharFormat = "Expected '%s'";
    public static final String expectedValueFormat = "Key '%s' is not a %s";
    public static final String expectedExistingKeyFormat = "Key '%s' does not exist in %s!";

    public static final String unexpectedCharFormat = "Unexpected char '%s'";

    // All non-format Exceptions
    public static final String valueOrKeyIsNull = "Value or key is null!";

    // All types of values
    public static final String typeLong = "long";
    public static final String typeInt = "int";
    public static final String typeShort = "short";
    public static final String typeByte = "byte";
    public static final String typeDouble = "double";
    public static final String typeFloat = "float";
    public static final String typeBoolean = "boolean";
    public static final String typeString = "string";
    public static final String typeJSONObject = "JSONObject";
    public static final String typeJSONArray = "JSONArray";
    public static final String typeNull = "null";

    // Other Strings
    public static final String sKey = "key";
    public static final String sValue = "value";
}
