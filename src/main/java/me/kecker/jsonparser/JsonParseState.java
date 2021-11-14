package me.kecker.jsonparser;

import me.kecker.jsonparser.exceptions.IllegalTokenException;
import me.kecker.jsonparser.exceptions.JsonParseException;
import me.kecker.jsonparser.exceptions.UnexpectedCharacterException;

import javax.lang.model.type.NullType;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JsonParseState {
    private static final Set<Character> WHITESPACE = Set.of('\u0020', '\n', '\r', '\u0009');
    private static final char QUOTE = '"';
    private static final char CURLY_BRACE_OPEN = '{';
    private static final char CURLY_BRACE_CLOSE = '}';
    private static final char BRACKETS_OPEN = '[';
    private static final char BRACKETS_CLOSE = ']';
    private static final char COMMA = ',';
    private static final char COLON = ':';

    private final String source;
    private char current;
    private int currentIndex;


    public JsonParseState(String source) {
        this.source = source;
        this.currentIndex = 0;
        this.current = source.charAt(0);
    }

    public char current() {
        assert !reachedEnd();
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

    public NullType nullType() throws JsonParseException {
        StringBuilder wordBuilder = new StringBuilder();
        while (!reachedEnd() && Character.isAlphabetic(current())) {
            wordBuilder.append(current());
            advance();
        }
        String word = wordBuilder.toString();
        if (!word.equals("null")) {
            throw new IllegalTokenException("Input '" + word + "' is not a valid nullType.");
        }
        return null;
    }

    public boolean bool() throws IllegalTokenException {
        StringBuilder wordBuilder = new StringBuilder();
        while (!reachedEnd() && Character.isAlphabetic(current())) {
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
        throw new IllegalTokenException("Input '" + word + "' is not a valid boolean.");
    }

    public Number number() {
        StringBuilder wordBuilder = new StringBuilder();
        while (!reachedEnd() && Character.isDigit(current())) {
            wordBuilder.append(current());
            advance();
        }
        String number = wordBuilder.toString();
        return Integer.parseInt(number);
    }

    public String string() throws UnexpectedCharacterException {
        assertCharacterAndAdvance(QUOTE);
        StringBuilder wordBuilder = new StringBuilder();
        while (!reachedEnd() && current() != QUOTE) {
            wordBuilder.append(current());
            advance();
        }
        String word = wordBuilder.toString();
        assertCharacterAndAdvance(QUOTE);
        return word;

    }

    public Map<String, Object> object() throws UnexpectedCharacterException {
        assertCharacterAndAdvance(CURLY_BRACE_OPEN);
        whitespace();
        assertCharacterAndAdvance(CURLY_BRACE_CLOSE);
        return Collections.emptyMap();
    }

    public Map.Entry<String, Object> member() throws JsonParseException {
        whitespace();
        String key = string();
        whitespace();
        assertCharacterAndAdvance(COLON);
        Object value = element();
        return new AbstractMap.SimpleImmutableEntry<>(key, value);
    }

    public Object[] array() throws JsonParseException {
        assertCharacterAndAdvance(BRACKETS_OPEN);
        whitespace();
        List<Object> elements = new ArrayList<>();
        if (!reachedEnd() && current() != BRACKETS_CLOSE) {
            elements.add(element());
            while (!reachedEnd() && current() == COMMA) {
                assertCharacterAndAdvance(COMMA);
                elements.add(element());
            }
        }
        assertCharacterAndAdvance(BRACKETS_CLOSE);
        return elements.toArray(Object[]::new);
    }

    private void assertCharacterAndAdvance(char expected) throws UnexpectedCharacterException {
        assertCharacter(expected);
        advance();
    }

    private void assertCharacter(char expected) throws UnexpectedCharacterException {
        if (reachedEnd()) {
            throw new UnexpectedCharacterException(expected);
        }
        if (current() != expected) {
            throw new UnexpectedCharacterException(expected, current());
        }
    }

    public Object value() throws JsonParseException {
        return switch (current()) {
            case 't', 'f' -> bool();
            case 'n' -> nullType();
            case QUOTE -> string();
            case CURLY_BRACE_OPEN -> object();
            case BRACKETS_OPEN -> array();
            default -> {
                if (Character.isDigit(current())) {
                    yield number();
                }
                throw new IllegalStateException("Unexpected value: " + current());
            }
        };
    }

    public Object element() throws JsonParseException {
        whitespace();
        Object value = value();
        whitespace();
        return value;
    }
}
