package me.kecker;

import java.util.Set;

public class JsonParseState {
    private static final Set<Character> WHITESPACE = Set.of('\u0020', '\n', '\r', '\u0009');
    private static final char QUOTE = '"';

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
        if (current() != QUOTE) {
            throw new IllegalArgumentException("String must start with quotes, but the current character is '" + current() + "'.");
        }
        advance();
        StringBuilder wordBuilder = new StringBuilder();
        while (current() != QUOTE && !reachedEnd() ) {
            wordBuilder.append(current());
            advance();
        }
        return wordBuilder.toString();
    }
}
