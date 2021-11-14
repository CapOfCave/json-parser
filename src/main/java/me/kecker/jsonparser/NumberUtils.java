package me.kecker.jsonparser;

import me.kecker.jsonparser.exceptions.IllegalNumberException;

public class NumberUtils {

    public static int toDecimal(char character) throws IllegalNumberException {
        if (character < '0' || character > '9') {
            throw new IllegalNumberException("Character '" + character + "' is not a decimal digit.");
        }
        return character - '0';
    }

}
