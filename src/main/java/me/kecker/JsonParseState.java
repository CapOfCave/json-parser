package me.kecker;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class JsonParseState {
    private static final Set<Character> WHITESPACE = Set.of('\u0020', '\n', '\r', '\u0009');
    private static final char QUOTE = '"';
    private static final char CURLY_BRACE_OPEN = '{';
    private static final char CURLY_BRACE_CLOSE = '}';

    private String source;
    private char current;
    private int currentIndex;


    public JsonParseState(String source) {
        this.source = source;
        this.currentIndex = 0;
        this.current = source.charAt(0);
    }

    public char current() {
        return current;
    }

    public void advance() {
        this.currentIndex++;
        this.current = reachedEnd() ? 0 : this.source.charAt(this.currentIndex);

    }

    public boolean reachedEnd() {
        return currentIndex == this.source.length();
    }

    public void whitespace() {
        while (WHITESPACE.contains(this.current) && !this.reachedEnd()) {
            this.advance();
        }
    }

    public boolean bool() {
        StringBuilder wordBuilder = new StringBuilder();
        while (Character.isAlphabetic(current()) && !reachedEnd()) {
            wordBuilder.append(current());
            advance();
        }
        String word = wordBuilder.toString();
        if (word.equals("true")) {
            return true;
        }
        if (word.equals("false")) {
            return false;
        }
        throw new IllegalArgumentException("Input '" + word + "' is not a valid boolean.");
    }

    public String string() {
        assertCharacterAndAdvance(QUOTE);
        StringBuilder wordBuilder = new StringBuilder();
        while (current() != QUOTE && !reachedEnd() ) {
            wordBuilder.append(current());
            advance();
        }
        return wordBuilder.toString();
    }

    public Map<String, Object> object() {
        assertCharacterAndAdvance(CURLY_BRACE_OPEN);
        whitespace();
        assertCharacterAndAdvance(CURLY_BRACE_CLOSE);
        return Collections.emptyMap();
    }

    private void assertCharacterAndAdvance(char expected) {
        assertCharacter(expected);
        advance();
    }

    private void assertCharacter(char expected) {
        if (reachedEnd()) {
            throw new IllegalArgumentException("Unexpected EOI, expected '" + expected + "'.");
        }
        if (current() != expected) {
            throw new IllegalArgumentException("Unexpected character: '" + current() + "', expected '" + expected + "'.");
        }
    }
}
