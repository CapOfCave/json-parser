package me.kecker.jsonparser;

import me.kecker.jsonparser.exceptions.JsonParseException;

public class JsonParser {

    public static JsonElement parse(String source) throws JsonParseException {
        JsonParseState jsonParseState = new JsonParseState(source);
        return jsonParseState.json();
    }

    private JsonParser() {
        // class should not be instantiated
    }
}
