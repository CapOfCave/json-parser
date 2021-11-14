package me.kecker;

public class JsonParser {
    public static boolean parseBoolean(String source) {
        if (source.equals("true")) {
            return true;
        }
        if (source.equals("false")) {
            return false;
        }
        throw new IllegalArgumentException("Input '" + source + "' is not a valid boolean.");
    }
}
