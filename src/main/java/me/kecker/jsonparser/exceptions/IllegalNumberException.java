package me.kecker.jsonparser.exceptions;

public class IllegalNumberException extends JsonParseException {
    public IllegalNumberException(String message) {
        super(message);
    }

    public IllegalNumberException(Throwable e) {
        super(e);
    }
}
