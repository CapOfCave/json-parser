package me.kecker;

public class JsonParseState {
    private char current;

    public JsonParseState(String abcd) {
        this.current = abcd.charAt(0);
    }

    public char current() {
        return current;
    }
}
