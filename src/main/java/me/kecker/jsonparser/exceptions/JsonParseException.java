package me.kecker.jsonparser.exceptions;

public class JsonParseException extends Exception {

    public JsonParseException(String message) {
        super(message);
    }

    public JsonParseException(Throwable e) {
        super(e);
    }
}
