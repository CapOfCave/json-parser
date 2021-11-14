package me.kecker.jsonparser;

import me.kecker.jsonparser.exceptions.JsonParseException;

import java.util.Collections;
import java.util.Map;

public class JsonParser {

    public static Object parse(String source) throws JsonParseException {
        JsonParseState jsonParseState = new JsonParseState(source);
        return jsonParseState.json();
    }
}
