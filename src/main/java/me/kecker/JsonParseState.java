package me.kecker;

public class JsonParseState {
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
}
