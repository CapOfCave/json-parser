package me.kecker.jsonparser;

import me.kecker.jsonparser.exceptions.IllegalNumberException;
import me.kecker.jsonparser.exceptions.IllegalTokenException;
import me.kecker.jsonparser.exceptions.JsonParseException;
import me.kecker.jsonparser.exceptions.UnexpectedCharacterException;

import javax.lang.model.type.NullType;
import java.math.BigDecimal;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
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
    private static final char BACKSLASH = '\\';
    private static final char MINUS = '-';
    public static final char ZERO = '0';
    public static final char POINT = '.';

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

    public BigDecimal number() throws IllegalNumberException {
        StringBuilder numberStringBuilder = new StringBuilder();
        while (!reachedEnd() && (Character.isDigit(current()) || current() == '-' || current() == '.')) {
            numberStringBuilder.append(current());
            advance();
        }

        String stringValue = numberStringBuilder.toString();
        String absoluteStringValue = stringValue.startsWith("-") ? stringValue.substring(1) : stringValue;
        if ((absoluteStringValue.length() > 1 && absoluteStringValue.startsWith("0"))) {
            throw new IllegalNumberException("Number must not start with a leading zero, but was \"" + stringValue + "\"");
        }
        if (absoluteStringValue.startsWith(".")) {
            throw new IllegalNumberException("Number must not start with a leading point, but was \"" + stringValue + "\"");
        }
        if (stringValue.endsWith(".")) {
            throw new IllegalNumberException("Number must not end with a trailing point, but was \"" + stringValue + "\"");
        }

        try {
            return new BigDecimal(stringValue);
        } catch (NumberFormatException e) {
            throw new IllegalNumberException(e);
        }
    }

    public String string() throws UnexpectedCharacterException {
        assertCharacterAndAdvance(QUOTE);
        StringBuilder wordBuilder = new StringBuilder();
        while (!reachedEnd() && current() != QUOTE) {
            if (current() == BACKSLASH) {
                advance();
                wordBuilder.append(escape());
                continue;
            }
            wordBuilder.append(current());
            advance();
        }
        String word = wordBuilder.toString();
        assertCharacterAndAdvance(QUOTE);
        return word;

    }

    private String escape() {
        if (current() == 'u') {
            advance(); // skip the 'u'
            int total = 0;
            for (int i = 0; i < 4; i++) {
                total *= 16;
                total += hex();
            }
            return new String(Character.toChars(total));
        }

        String escape = switch (current()) {
            case '"' -> "\"";
            case '\\' -> "\\";
            case '/' -> "/";
            case 'b' -> "\b";
            case 'f' -> "\f";
            case 'n' -> "\n";
            case 'r' -> "\r";
            case 't' -> "\t";
            default -> throw new IllegalStateException("Unexpected value: " + current());
        };
        advance();
        return escape;
    }

    private int hex() {
        int i = Character.getNumericValue(current());
        if (i > 16) {
            throw new IllegalArgumentException("'" + current() + "' is not a hex character.");
        }
        advance();
        return i;
    }

    public Map<String, Object> object() throws JsonParseException {
        assertCharacterAndAdvance(CURLY_BRACE_OPEN);
        whitespace();
        Map<String, Object> members = new HashMap<>();
        if (!reachedEnd() && current() != CURLY_BRACE_CLOSE) {
            Map.Entry<String, Object> firstMember = member();
            members.put(firstMember.getKey(), firstMember.getValue());
            while (!reachedEnd() && current() == COMMA) {
                assertCharacterAndAdvance(COMMA);
                Map.Entry<String, Object> additionalMember = member();
                members.put(additionalMember.getKey(), additionalMember.getValue());
            }
        }
        assertCharacterAndAdvance(CURLY_BRACE_CLOSE);
        return members;
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
                if (Character.isDigit(current()) || current() == '-') {
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

    public Object json() throws JsonParseException {
        return element();
    }
}
