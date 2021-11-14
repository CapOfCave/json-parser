package me.kecker.jsonparser;

import me.kecker.jsonparser.exceptions.IllegalNumberException;

public class NumberUtils {

    public static int toDecimal(char character) throws IllegalNumberException {
        if (!isDigit(character)) {
            throw new IllegalNumberException("Character '" + character + "' is not a valid decimal digit.");
        }
        return character - '0';
    }

    public static boolean isDigit(char character) {
        return character >= '0' && character <= '9';
    }

}
