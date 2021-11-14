package me.kecker;

import java.util.Collections;
import java.util.Map;

public class JsonParser {

    public static Object parse(String source) {
        if (source.startsWith("\"")) {
            return parseString(source);
        }
        return parseBoolean(source);
    }

    @Deprecated
    public static boolean parseBoolean(String source) {
        if (source.equals("true")) {
            return true;
        }
        if (source.equals("false")) {
            return false;
        }
        throw new IllegalArgumentException("Input '" + source + "' is not a valid boolean.");
    }

    @Deprecated
    public static String parseString(String source) {
        return source.substring(1, source.length() - 1);
    }

    public static Map<String, Object> parseObject(String source) {
        return Collections.emptyMap();
    }
}
