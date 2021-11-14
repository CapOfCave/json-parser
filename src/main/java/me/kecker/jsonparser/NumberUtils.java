package me.kecker.jsonparser;

import me.kecker.jsonparser.exceptions.IllegalNumberException;

public class NumberUtils {

    public static int toDecimal(char character) throws IllegalNumberException {
        checkBounds(character, '0', '9');
        return character - '0';
    }

    public static int toOneNine(char character) throws IllegalNumberException {
        checkBounds(character, '1', '9');
        return character - '0';
    }

    private static void checkBounds(char character, int min, int max) throws IllegalNumberException {
        if (character < min || character > max) {
            throw new IllegalNumberException("Character '" + character + "' does not match bounds [" + min + " ; "+ max + "].");
        }
    }

}
