package me.kecker;

import java.util.Set;

public class JsonParseState {
    private static final Set<Character> WHITESPACE = Set.of('\u0020', '\n', '\r', '\u0009');

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
        this.current = this.source.charAt(this.currentIndex);
    }

    public boolean reachedEnd() {
        return currentIndex == this.source.length() - 1;
    }

    public void whitespace() {
        while (WHITESPACE.contains(this.current)) {
            this.advance();
        }
    }
}
