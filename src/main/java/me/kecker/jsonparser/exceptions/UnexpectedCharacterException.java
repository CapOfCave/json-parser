package me.kecker.jsonparser.exceptions;

public class UnexpectedCharacterException extends JsonParseException {

    public UnexpectedCharacterException(char expected) {
        super("Unexpected EOI, expected '" + expected + "'.");
    }

    public UnexpectedCharacterException(char expected, char actual) {
        super("Unexpected character: '" + actual + "', expected '" + expected + "'.");
    }
}
